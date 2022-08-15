package com.duncpro.jroute.rest;

import com.duncpro.jroute.Path;
import com.duncpro.jroute.RouteConflictException;
import com.duncpro.jroute.route.Route;
import com.duncpro.jroute.router.Router;
import com.duncpro.jroute.router.TreeRouter;
import com.duncpro.jroute.util.DelegatingRouter;

public class RestRouter<E> extends DelegatingRouter<RestResource<E>> {
    public RestRouter() {
        super(new TreeRouter<>());
    }

    public RestRouter(Router<RestResource<E>> underlyingRouter) {
        super(underlyingRouter);
    }

    public void add(HttpMethod method, Route route, E endpoint) throws RouteConflictException {
        RestResource<E> resource = getOrAdd(route, RestResource::new);

        try {
            resource.addMethodEndpoint(method, endpoint);
        } catch (MethodConflictException e) {
            throw new RouteConflictException("Can not add endpoint for method " + method + " at" +
                    " route " + route + " because there is already an endpoint associated with that" +
                    " position.", e);
        }
    }

    public void add(HttpMethod method, String route, E endpoint) throws RouteConflictException {
        add(method, new Route(route), endpoint);
    }

    public RestRouteResult<E> route(HttpMethod method, Path path) {
        return route(path)
                .map(result -> result.getEndpoint().getMethodEndpoint(method)
                        .<RestRouteResult<E>>map(endpoint -> new RestRouteResult.RestRouteMatch<>(endpoint, result.getRoute()))
                        .orElse(new RestRouteResult.UnsupportedMethod<>()))
                .orElse(new RestRouteResult.ResourceNotFound<>());
    }

    public RestRouteResult<E> route(HttpMethod method, String path) {
        return route(method, new Path(path));
    }
}
