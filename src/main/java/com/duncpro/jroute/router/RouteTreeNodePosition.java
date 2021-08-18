package com.duncpro.jroute.router;

import com.duncpro.jroute.route.Route;
import com.duncpro.jroute.route.RouteElement;
import net.jcip.annotations.Immutable;

import java.util.Objects;

/**
 * This class represents a {@link RouteTreeNode}'s position in a {@link Route}.
 */
@Immutable
public class RouteTreeNodePosition<E> {
    private final RouteTreeNode<E> parent;
    private final RouteElement routeElement;

    RouteTreeNodePosition(RouteTreeNode<E> parent, RouteElement routeElement) {
        this.parent = parent;
        this.routeElement = routeElement;
    }

    RouteElement getRouteElement() {
        return routeElement;
    }

    /**
     * Calculates the {@link Route} leading up to and including the {@link RouteTreeNode} which is represented by this
     * {@link RouteTreeNodePosition}.
     */
    Route getRoute() {
        var fullRoute = Route.ROOT;

        RouteTreeNodePosition<?> currentPosition = this;
        while (!Objects.equals(currentPosition, root())) {
            fullRoute = fullRoute.withLeadingElement(currentPosition.routeElement);
            currentPosition = currentPosition.parent.position;
        }

        return fullRoute;
    }

    public static <E> RouteTreeNodePosition<E> root() { return new RouteTreeNodePosition<>(null, null); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RouteTreeNodePosition<?> that = (RouteTreeNodePosition<?>) o;
        return Objects.equals(parent, that.parent) && Objects.equals(routeElement, that.routeElement);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parent, routeElement);
    }
}
