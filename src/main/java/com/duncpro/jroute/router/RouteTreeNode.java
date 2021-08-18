package com.duncpro.jroute.router;

import com.duncpro.jroute.HttpMethod;
import com.duncpro.jroute.JRouteUtilities;
import com.duncpro.jroute.RouteConflictException;
import com.duncpro.jroute.route.Route;
import com.duncpro.jroute.route.RouteElement;
import com.duncpro.jroute.route.StaticRouteElement;
import com.duncpro.jroute.route.WildcardRouteElement;
import net.jcip.annotations.NotThreadSafe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@NotThreadSafe
class RouteTreeNode<E> {
    private final RouteTreeNodePosition<E> position;
    private final List<RouteTreeNode<E>> children = new ArrayList<>();
    private final Map<HttpMethod, E> endpoints = new HashMap<>();

    /**
     * Passing null for {@code associatedRouteElement} indicates that this node is the root of the route tree
     * and therefore has no siblings.
     */
    protected RouteTreeNode(RouteTreeNodePosition<E> position) {
        this.position = position;
    }

    /**
     * Returns true if this RouteTreeNode is associated with a {@link RouteElement} that is directly preceding a
     * {@link WildcardRouteElement}. In other words, if this is node is a parent to a wildcard node.
     */
    private boolean hasGreedyChild() {
        return children.size() == 1
                && children.get(0).position.getAssociatedRouteElement() instanceof WildcardRouteElement;
    }

    /**
     * Returns the {@link RouteTreeNode} which is responsible for requests made to the given
     * {@code pathElement}. If no route matches the path then an empty optional is returned instead.
     *
     * This function accepts a path element, not a path. In other words, it can only match individual direct
     * descendent routes.
     */
    Optional<RouteTreeNode<E>> matchChildRoute(String pathElement) {
        if (hasGreedyChild()) {
            return Optional.of(children.get(0));
        } else {
            return children.stream()
                    .filter(child -> JRouteUtilities.accepts(pathElement, child.position.getAssociatedRouteElement()))
                    .reduce(($, $$) -> { throw new AssertionError(); });
        }
    }

    private void addChildRoute(RouteTreeNode<E> childNode) throws RouteConflictException {
        if (hasGreedyChild()) throw new RouteConflictException("This RouteTreeNode is associated with a route element" +
                " which is directly preceding a wild card route element. A wild card child accepts all path elements " +
                " and can therefore never have siblings.");

        if (childNode.position.getAssociatedRouteElement() instanceof WildcardRouteElement && !children.isEmpty()) {
            throw new RouteConflictException("This RouteTreeNode already has at least one child and can therefore" +
                    " not accept a wildcard child because it would introduce a routing conflict.");
        }

        children.add(childNode);
    }

    RouteTreeNode<E> getOrCreateChildRoute(RouteElement trailingRouteElement) {
        return children.stream()
                .filter(child -> Objects.equals(child.position.getAssociatedRouteElement(), trailingRouteElement))
                .reduce(($, $$) -> { throw new AssertionError(); })
                .orElseGet(() -> {
                    final var newChild = new RouteTreeNode<E>(new RouteTreeNodePosition<>(this, trailingRouteElement));
                    addChildRoute(newChild);
                    return newChild;
                });
    }

    void addEndpoint(HttpMethod method, E endpoint) {
        final var prev = endpoints.putIfAbsent(method, endpoint);
        if (prev != null) throw new IllegalStateException("Endpoint already bound to method: " + method.name());
    }

    private boolean isRoot() { return Objects.equals(this.position, RouteTreeNodePosition.root()); }

    Optional<RouterResult<E>> getEndpointAsRouterResult(HttpMethod method) {
        final var endpoint = endpoints.get(method);

        if (endpoint == null) return Optional.empty();

        final var result = new RouterResult<>(
                endpoint,
                traverseToRoot(this)
        );

        return Optional.of(result);
    }

    private static Route traverseToRoot(RouteTreeNode<?> node) {
        var route = Route.ROOT;

        while (!node.isRoot()) {
            route = route.withLeadingElement(node.position.getAssociatedRouteElement());
            node = node.position.getParent();
        }

        return route;
    }
}

