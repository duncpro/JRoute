package com.duncpro.jroute.router;

import com.duncpro.jroute.HttpMethod;
import com.duncpro.jroute.Path;
import com.duncpro.jroute.RouteConflictException;
import com.duncpro.jroute.route.Route;
import net.jcip.annotations.NotThreadSafe;

import java.util.Optional;

@NotThreadSafe
public class TreeRouter<E> implements Router<E> {
    private final RouteTreeNode<E> rootRoute = RouteTreeNode.newTree();

    @Override
    public Optional<RouterResult<E>> route(HttpMethod method, String path) {
        return findNode(rootRoute, new Path(path))
                .flatMap(node -> node.getEndpointAsRouterResult(method));
    }

    @Override
    public void addRoute(HttpMethod method, String routeString, E endpoint) throws RouteConflictException {
        final var node = findOrCreateNode(rootRoute, new Route(routeString));
        node.addEndpoint(method, endpoint);
    }

    /**
     * Returns the {@link RouteTreeNode} which is responsible for handling requests to the given {@link Path}.
     * If no {@link RouteTreeNode} matches the given path then an empty optional is returned instead.
     */
    protected static <E> Optional<RouteTreeNode<E>> findNode(RouteTreeNode<E> routeTree, Path path) {
        if (path.isRoot()) return Optional.of(routeTree);

        return routeTree.matchChildRoute(path.getElements().get(0))
                .flatMap(child -> findNode(child, path.withoutFirstElement()));
    }

    protected static <E> RouteTreeNode<E> findOrCreateNode(RouteTreeNode<E> routeTree, Route route) {
        if (route.isRoot()) return routeTree;

        final var child = routeTree.getOrCreateChildRoute(route.getElements().get(0));
        return findOrCreateNode(child, route.withoutFirstElement());
    }
}
