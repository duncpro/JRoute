package com.duncpro.jroute.router;

import com.duncpro.jroute.HttpMethod;
import com.duncpro.jroute.JRouteInternalUtilities;
import com.duncpro.jroute.RouteConflictException;
import com.duncpro.jroute.route.RouteElement;
import com.duncpro.jroute.route.WildcardRouteElement;
import net.jcip.annotations.NotThreadSafe;

import java.util.*;
import java.util.stream.Stream;

@NotThreadSafe
class RouteTreeNode<E> {
    protected final RouteTreeNodePosition<E> position;
    private final List<RouteTreeNode<E>> children = new ArrayList<>();
    private final Map<HttpMethod, E> endpoints = new HashMap<>();

    protected RouteTreeNode(RouteTreeNodePosition<E> position) {
        this.position = position;
    }

    /**
     * Returns a {@link Stream} containing all endpoints which are defined at this {@link RouteTreeNode}
     * as well as all {@link RouteTreeNode}s descending from the given {@link RouteTreeNode}s.
     */
    static <E> Set<PositionedEndpoint<E>> getAllEndpoints(RouteTreeNode<E> root) {
        final var endpoints = new HashSet<PositionedEndpoint<E>>();

        root.endpoints.entrySet().stream()
                .map(e -> new PositionedEndpoint<E>(root.position.getRoute(), e.getKey(), e.getValue()))
                .forEach(endpoints::add);

        root.children.stream()
                .flatMap(child -> RouteTreeNode.getAllEndpoints(child).stream())
                .forEach(endpoints::add);

        return endpoints;
    }

    private boolean hasGreedyChild() {
        return children.size() == 1
                && children.get(0).position.getRouteElement() instanceof WildcardRouteElement;
    }

    Optional<RouteTreeNode<E>> matchChildRoute(String pathElement) {
        if (hasGreedyChild()) {
            return Optional.of(children.get(0));
        } else {
            return children.stream()
                    .filter(child -> JRouteInternalUtilities.accepts(pathElement, child.position.getRouteElement()))
                    .reduce(($, $$) -> { throw new AssertionError(); });
        }
    }

    private void addChildRoute(RouteTreeNode<E> childNode) throws RouteConflictException {
        if (hasGreedyChild()) throw new RouteConflictException("This RouteTreeNode is associated with a route element" +
                " which is directly preceding a wild card route element. A wild card child accepts all path elements " +
                " and can therefore never have siblings.");

        if (childNode.position.getRouteElement() instanceof WildcardRouteElement && !children.isEmpty()) {
            throw new RouteConflictException("This RouteTreeNode already has at least one child and can therefore" +
                    " not accept a wildcard child because it would introduce a routing conflict.");
        }

        children.add(childNode);
    }

    Optional<RouteTreeNode<E>> getChildRoute(RouteElement trailingRouteElement) {
        return children.stream()
                .filter(child -> child.position.getRouteElement().equals(trailingRouteElement))
                .findFirst();
    }

    RouteTreeNode<E> getOrCreateChildRoute(RouteElement trailingRouteElement) {
        return children.stream()
                .filter(child -> Objects.equals(child.position.getRouteElement(), trailingRouteElement))
                .reduce(($, $$) -> { throw new AssertionError(); })
                .orElseGet(() -> {
                    final var newChild = new RouteTreeNode<E>(new RouteTreeNodePosition<>(this, trailingRouteElement));
                    addChildRoute(newChild);
                    return newChild;
                });
    }

    void addEndpoint(HttpMethod method, E endpoint) {
        if (endpoint == null) throw new IllegalArgumentException();
        final var prev = endpoints.putIfAbsent(method, endpoint);
        if (prev != null) throw new IllegalStateException("PositionedEndpoint already bound to method: " + method.name());
    }

    Optional<E> getEndpoint(HttpMethod method) {
        return Optional.ofNullable(endpoints.get(method));
    }
}

