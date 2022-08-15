package com.duncpro.jroute.rest;

import com.duncpro.jroute.route.Route;

import java.util.Optional;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

public class RestRouteResult<E> {
    public static class RestRouteMatch<E> extends RestRouteResult<E> {
        private final E endpoint;
        private final Route route;

        RestRouteMatch(E endpoint, Route route) {
            this.endpoint = endpoint;
            this.route = route;
        }

        public Route getRoute() {
            return this.route;
        }

        public E getMethodEndpoint() {
            return this.endpoint;
        }
    }

    public static class ResourceNotFound<E> extends RestRouteResult<E> {
        ResourceNotFound() {}
    }

    public static class UnsupportedMethod<E> extends RestRouteResult<E> {
        UnsupportedMethod() {}
    }

    RestRouteResult() {}

    public <R> R map(Function<RestRouteMatch<E>, R> success, Function<RestRouteFailureType, R> failure) {
        if (this instanceof ResourceNotFound) return failure.apply(RestRouteFailureType.RESOURCE_NOT_FOUND);
        if (this instanceof UnsupportedMethod) return failure.apply(RestRouteFailureType.UNSUPPORTED_METHOD);
        if (this instanceof RestRouteResult.RestRouteMatch) return success.apply((RestRouteMatch<E>) this);
        throw new AssertionError();
    }

    public Optional<RestRouteResult.RestRouteMatch<E>> asOptional() {
        if (this instanceof RestRouteResult.RestRouteMatch) return Optional.of((RestRouteResult.RestRouteMatch<E>) this);
        return Optional.empty();
    }
}
