package com.duncpro.jroute.router;

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
    private E endpoint;

    protected RouteTreeNode(RouteTreeNodePosition<E> position) {
        this.position = position;
    }

    /**
     * Returns a {@link Stream} containing the endpoint which is defined at this {@link RouteTreeNode}
     * as well as all {@link RouteTreeNode}s descending from the given {@link RouteTreeNode}s.
     */
    static <E> Set<PositionedEndpoint<E>> getAllEndpoints(RouteTreeNode<E> root) {
        final var endpoints = new HashSet<PositionedEndpoint<E>>();

        if (root.endpoint != null) {
            endpoints.add(new PositionedEndpoint<>(root.position.getRoute(), root.endpoint));
        }

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

    void setEndpoint(E endpoint) {
        if (endpoint == null) throw new IllegalArgumentException();
        if (this.endpoint != null) throw new RouteConflictException("An endpoint is already bound to the RouteTreeNode" +
                " at position: " + this.position.getRoute());
        this.endpoint = endpoint;
    }

    Optional<E> getEndpoint() {
        return Optional.ofNullable(this.endpoint);
    }
}

