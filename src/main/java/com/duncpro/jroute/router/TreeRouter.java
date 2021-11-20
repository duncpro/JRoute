package com.duncpro.jroute.router;

import com.duncpro.jroute.HttpMethod;
import com.duncpro.jroute.Path;
import com.duncpro.jroute.RouteConflictException;
import com.duncpro.jroute.route.Route;
import com.duncpro.jroute.route.RouteElement;
import net.jcip.annotations.NotThreadSafe;

import java.util.*;

@NotThreadSafe
public class TreeRouter<E> implements Router<E> {
    private final RouteTreeNode<E> rootRoute = new RouteTreeNode<>(RouteTreeNodePosition.root());

    @Override
    public Optional<RouterResult<E>> route(HttpMethod method, Path path) {
        return findNode(rootRoute, path)
                .flatMap(node -> node.getEndpointAsRouterResult(method));
    }

    @Override
    public void addRoute(HttpMethod method, Route route, E endpoint) throws RouteConflictException {
        final var node = findOrCreateNode(rootRoute, route);
        node.addEndpoint(method, endpoint);
    }

    @Override
    public Set<PositionedEndpoint<E>> getAllEndpoints(Route prefix) {
        return findNode(prefix)
                .map(RouteTreeNode::getAllEndpoints)
                .orElse(Collections.emptySet());
    }

    private Optional<RouteTreeNode<E>> findNode(Route route) {
        RouteTreeNode<E> prefixNode = rootRoute;

        for (final var element : route.getElements()) {
            final var childNode = prefixNode.getChildRoute(element);
            if (childNode.isEmpty()) return Optional.empty();
            prefixNode = childNode.get();
        }

        return Optional.of(prefixNode);
    }

    /**
     * Finds the {@link RouteTreeNode} which is responsible for handling requests made on the given {@link Path}.
     * If no {@link RouteTreeNode} has been delegated for this path, any empty optional is returned instead.
     */
    protected static <E> Optional<RouteTreeNode<E>> findNode(RouteTreeNode<E> routeTree, Path path) {
        if (path.isRoot()) return Optional.of(routeTree);

        return routeTree.matchChildRoute(path.getElements().get(0))
                .flatMap(child -> findNode(child, path.withoutLeadingElement()));
    }

    /**
     * A variant of {@link TreeRouter#findNode(RouteTreeNode, Path)} which will add new branches to the
     * route tree if necessary so that the {@link Route} may be resolved to a {@link RouteTreeNode}. If no nodes
     * need to be added then this function is analogous to the aforementioned one. Finally this function returns
     * the {@link RouteTreeNode} corresponding to the last {@link RouteElement} in the {@link Route}.
     */
    protected static <E> RouteTreeNode<E> findOrCreateNode(RouteTreeNode<E> routeTree, Route route) {
        if (route.isRoot()) return routeTree;

        final var child = routeTree.getOrCreateChildRoute(route.getElements().get(0));
        return findOrCreateNode(child, route.withoutLeadingElement());
    }

    /**
     * Creates a new {@link TreeRouter} containing the sum of all routes in the given sub routers
     * at the time of invocation.
     * @throws RouteConflictException if the given routers cannot be summed because they
     * contain routes which conflict with one another.
     */
    public static <E> TreeRouter<E> sum(Collection<Router<E>> subRouters) {
        final var masterRouter = new TreeRouter<E>();

        subRouters.stream()
                .flatMap(subRouter -> subRouter.getAllEndpoints(Route.ROOT).stream())
                .forEach(positionedEndpoint -> masterRouter.addRoute(positionedEndpoint.method,
                        positionedEndpoint.route, positionedEndpoint.endpoint));

        return masterRouter;
    }

    /**
     * Returns a new {@link TreeRouter} which is identical to the given {@link Router} except
     * all routes have been prefixed with the given {@link Route}. The returned router will not
     * reflect any changes made to the original router.
     */
    public static <E> TreeRouter<E> prefix(Router<E> router, Route prefix) {
        final var prefixedRouter = new TreeRouter<E>();
        for (final var positionedEndpoint : router.getAllEndpoints(Route.ROOT)) {
            final var prefixedRoute = Route.concat(prefix, positionedEndpoint.route);
            prefixedRouter.addRoute(positionedEndpoint.method, prefixedRoute, positionedEndpoint.endpoint);
        }
        return prefixedRouter;
    }
}
