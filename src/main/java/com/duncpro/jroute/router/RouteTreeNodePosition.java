package com.duncpro.jroute.router;

import com.duncpro.jroute.route.RouteElement;

import java.util.Objects;

public class RouteTreeNodePosition<E> {
    private final RouteTreeNode<E> parent;
    private final RouteElement associatedRouteElement;

    public RouteTreeNodePosition(RouteTreeNode<E> parent, RouteElement associatedRouteElement) {
        this.parent = parent;
        this.associatedRouteElement = associatedRouteElement;
    }

    public RouteTreeNode<E> getParent() {
        return parent;
    }

    public RouteElement getAssociatedRouteElement() {
        return associatedRouteElement;
    }

    public static <E> RouteTreeNodePosition<E> root() { return new RouteTreeNodePosition<>(null, null); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RouteTreeNodePosition<?> that = (RouteTreeNodePosition<?>) o;
        return Objects.equals(parent, that.parent) && Objects.equals(associatedRouteElement, that.associatedRouteElement);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parent, associatedRouteElement);
    }
}
