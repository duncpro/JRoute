package com.duncpro.jroute.router;

import com.duncpro.jroute.Path;
import com.duncpro.jroute.RouteConflictException;
import com.duncpro.jroute.route.Route;
import com.duncpro.jroute.route.RouteElement;
import net.jcip.annotations.NotThreadSafe;

import java.util.*;
import java.util.function.Supplier;

@NotThreadSafe
public class TreeRouter<E> implements Router<E> {
    private final RouteTreeNode<E> rootRoute = new RouteTreeNode<>(RouteTreeNodePosition.root());

    @Override
    public Optional<RouteMatch<E>> route(Path path) {
        return findNode(rootRoute, path)
                .flatMap(node -> node.getEndpoint()
                        .map(endpoint -> new RouteMatch<>(endpoint, node.position.getRoute())));
    }

    @Override
    public void add(Route route, E endpoint) throws RouteConflictException {
        final var node = findOrCreateNode(rootRoute, route);
        node.setEndpoint(endpoint);
    }

    @Override
    public E getOrAdd(Route route, Supplier<E> endpointFactory) {
        final var node = findOrCreateNode(rootRoute, route);
        final var endpoint = endpointFactory.get();
        node.setEndpoint(endpoint);
        return endpoint;
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
}
