package com.duncpro.jroute.router;

import com.duncpro.jroute.route.Route;
import net.jcip.annotations.Immutable;

@Immutable
public class NodeSearchResult<E> {
    private final Route route;
    private final RouteTreeNode<E> node;

    public NodeSearchResult(Route route, RouteTreeNode<E> node) {
        this.route = route;
        this.node = node;
    }

    public Route getRoute() {
        return route;
    }

    public RouteTreeNode<E> getNode() {
        return node;
    }
}
