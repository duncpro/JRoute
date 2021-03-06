package com.duncpro.jroute.router;

import com.duncpro.jroute.HttpMethod;
import com.duncpro.jroute.Path;
import com.duncpro.jroute.RouteConflictException;
import com.duncpro.jroute.route.Route;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface Router<E> {
    default Optional<RouterResult<E>> route(HttpMethod method, String pathString) {
        return route(method, new Path(pathString));
    }

    /**
     * Finds the {@link Route} and endpoint ({@link E}) which has been assigned responsibility for requests made to the
     * given {@link Path} using the given {@link HttpMethod}. Routes must be registered with the router in advance using
     * {@link #addRoute(HttpMethod, String, Object)} or similar. If no route exists for the given method and path then
     * an empty optional is returned instead.
     */
    Optional<RouterResult<E>> route(HttpMethod method, Path path);

    /**
     * Assigns the given endpoint responsibility for requests made with the given {@link HttpMethod}
     * to paths matching the given {@code routeString}.
     *
     * @param method the http method to bind to this endpoint . It's perfectly valid to call this function multiple
     *               times with the same route but different {@link HttpMethod}s. This is useful for handling multiple
     *               request types with a single endpoint.
     * @param routeString a path-like string describing which paths this handler is responsible for. Examples include:
     *                    "/books/moby-dick", "/users/*\/pets/*", etc. The star (*) symbol is a wildcard which
     *                    matches any and all path elements for that position.
     * @param endpoint the endpoint which is responsible for handling requests made to paths following the form of this
     *                 route. This endpoint will be returned by {@link #route(HttpMethod, String)}.
     * @throws RouteConflictException if the given {@code routeString} overlaps another pre-existing route with the same
     *      {@link HttpMethod}. The route will not be overwritten.
     */
    default void addRoute(HttpMethod method, String routeString, E endpoint) throws RouteConflictException {
        addRoute(method, new Route(routeString), endpoint);
    }

    void addRoute(HttpMethod method, Route route, E endpoint) throws RouteConflictException;

    /**
     * Returns a set of {@link PositionedEndpoint}s which are accessible via the given {@link Route}.
     * The returned collection includes the endpoints defined on the given {@link Route} as well as all endpoints
     * which exists on routes that descend from the given {@link Route}.
     */
    Set<PositionedEndpoint<E>> getAllEndpoints(Route prefix);

    /**
     * @throws RouteConflictException if the given endpoint conflicts with a pre-existing endpoint within the router.
     */
    default void addRoute(PositionedEndpoint<E> positionedEndpoint) {
        addRoute(positionedEndpoint.method, positionedEndpoint.route, positionedEndpoint.endpoint);
    }
}
