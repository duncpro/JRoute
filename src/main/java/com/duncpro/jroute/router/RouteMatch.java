package com.duncpro.jroute.router;

import com.duncpro.jroute.route.Route;

@SuppressWarnings("unused") // The type parameter is used! Quit complaining IntelliJ.
public class RouteMatch<E> {
    private final E endpoint;
    private final Route route;

    RouteMatch(E endpoint, Route route) {
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
