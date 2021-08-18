package com.duncpro.jroute.router;

import com.duncpro.jroute.route.Route;
import net.jcip.annotations.Immutable;

@Immutable
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

    /**
     * Returns the route matching the path. The returned {@link Route} object can be used to extract path arguments
     * for route parameters. See {@link Route#extractVariables(String)}.
     */
    public Route getRoute() {
        return route;
    }
}
