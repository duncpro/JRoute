package com.duncpro.jroute.router;

import com.duncpro.jroute.HttpMethod;
import com.duncpro.jroute.route.Route;
import net.jcip.annotations.Immutable;

import java.util.Optional;

/**
 * There are three district types of {@link RouterResult}.
 * 1. {@link Matched}, representing a successful match of a request to an endpoint.
 * 2. {@link ResourceNotFound}, representing a failure to match the path to a resource.
 * 3. {@link MethodNotAllowed}, representing a successful match of a request to a resource but a failure
 *  to match and the request method (http verb) to an endpoint on the resource.
 */
@SuppressWarnings("unused") // The type parameter is used! Quit complaining IntelliJ.
public class RouterResult<E> {
    /**
     * {@link RouterResult} which indicates a failure to match the given path with any registered route.
     * This class represents an HTTP 404 error. See also {@link MethodNotAllowed}, which represents the case
     * where the route exists but the method does not.
     */
    public static final class ResourceNotFound<E> extends RouterResult<E> {
        ResourceNotFound() {}
    }

    /**
     * {@link RouterResult} representing the case where the given path matched a resource within the
     * {@link Router}, but the given {@link HttpMethod} is not defined for the resource.
     * This result is associated with HTTP Status Code 405.
     */
    public static final class MethodNotAllowed<E> extends RouterResult<E> {
        MethodNotAllowed() {}
    }

    /**
     * {@link RouterResult} representing the case where the given patch matched a resource within the
     * {@link Router}, and the given {@link HttpMethod} is defined on that resource.
     */
    @Immutable
    public static final class Matched<E> extends RouterResult<E> {
        private final E endpoint;
        private final Route route;

        Matched(E endpoint, Route route) {
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

    private RouterResult() {}

    /**
     * If this {@link RouterResult} is a successful matched (i.e. instanceof {@link Matched}),
     * then this method returns an optional containing this object but cast to {@link Matched}.
     * If this {@link RouterResult} is an error type, such as {@link ResourceNotFound} or {@link MethodNotAllowed},
     * then this method returns an empty optional.
     */
    public Optional<Matched<E>> asOptional() {
        if (this instanceof Matched) return Optional.of((Matched<E>) this);
        return Optional.empty();
    }
}
