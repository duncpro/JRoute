package com.duncpro.jroute.router;

import com.duncpro.jroute.HttpMethod;
import com.duncpro.jroute.Path;
import com.duncpro.jroute.RouteConflictException;
import com.duncpro.jroute.route.Route;
import com.duncpro.jroute.route.RouteElement;
import net.jcip.annotations.NotThreadSafe;

import java.util.Optional;

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
}
