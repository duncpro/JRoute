package com.duncpro.jroute.router;

import com.duncpro.jroute.HttpMethod;
import com.duncpro.jroute.JRouteUtilities;
import com.duncpro.jroute.RouteConflictException;
import com.duncpro.jroute.route.RouteElement;
import com.duncpro.jroute.route.WildcardRouteElement;
import net.jcip.annotations.NotThreadSafe;

import java.util.*;

@NotThreadSafe
class RouteTreeNode<E> {
    protected final RouteTreeNodePosition<E> position;
    private final List<RouteTreeNode<E>> children = new ArrayList<>();
    private final Map<HttpMethod, E> endpoints = new HashMap<>();

    protected RouteTreeNode(RouteTreeNodePosition<E> position) {
        this.position = position;
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
                    .filter(child -> JRouteUtilities.accepts(pathElement, child.position.getRouteElement()))
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
        final var prev = endpoints.putIfAbsent(method, endpoint);
        if (prev != null) throw new IllegalStateException("Endpoint already bound to method: " + method.name());
    }

    Optional<RouterResult<E>> getEndpointAsRouterResult(HttpMethod method) {
        final var endpoint = endpoints.get(method);

        if (endpoint == null) return Optional.empty();

        final var result = new RouterResult<>(
                endpoint,
                position.getRoute()
        );

        return Optional.of(result);
    }
}

