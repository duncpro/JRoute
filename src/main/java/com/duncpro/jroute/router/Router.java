package com.duncpro.jroute.router;

import com.duncpro.jroute.HttpMethod;
import com.duncpro.jroute.RouteConflictException;
import com.duncpro.jroute.route.Route;

import java.util.Optional;

public interface Router<E> {
    /**
     * Finds the {@link Route} and endpoint ({@link E}) which has been assigned responsibility for requests made to the
     * given path using the given {@link HttpMethod}. Routes must be registered with the router in advance using
     * {@link #addRoute(HttpMethod, String, Object)}. If no route exists for the given method and path then an empty
     * optional is returned instead.
     */
    Optional<RouterResult<E>> route(HttpMethod method, String path);

    /**
     * Assigns the given endpoint responsibility for requests made with the given {@link HttpMethod}
     * to paths matching the given {@code routeString}.
     *
     * @param method the http method to bind to this handler . It's perfectly valid to call this function multiple
     *               times with the same route but different {@link HttpMethod}s. This is useful for handling multiple
     *               request types with a single endpoint.
     * @param routeString a path-like string describing which paths this handler is responsible for. Examples include:
     *                    "/books/moby-dick", "/users/*\/pets/*", etc. The star (*) symbol is a wildcard which
     *                    matches any and all path elements for that position.
     * @param endpoint the endpoint which is responsible for handling requests made to paths following the form of this
     *                 route. This endpoint will be returned by {@link #route(HttpMethod, String)}.
     * @throws RouteConflictException if the given {@code routeString} overlaps another pre-existing route. The route
     *  will not be overwritten.
     */
    void addRoute(HttpMethod method, String routeString, E endpoint) throws RouteConflictException;
}
