package com.duncpro.jroute.router;

import com.duncpro.jroute.route.Route;

public class RouterResult<E> {
    private final E endpoint;
    private final Route route;

    public RouterResult(E endpoint, Route route) {
        this.endpoint = endpoint;
        this.route = route;
    }

    public E getEndpoint() {
        return endpoint;
    }

    public Route getRoute() {
        return route;
    }
}
