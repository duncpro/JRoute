package com.duncpro.jroute.router;

import com.duncpro.jroute.HttpMethod;
import com.duncpro.jroute.route.Route;

import java.util.Objects;

public class PositionedEndpoint<E> {
    public final Route route;
    public final HttpMethod method;
    public final E endpoint;

    public PositionedEndpoint(Route route, HttpMethod method, E endpoint) {
        this.route = Objects.requireNonNull(route);
        this.method = Objects.requireNonNull(method);
        this.endpoint = Objects.requireNonNull(endpoint);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PositionedEndpoint<?> that = (PositionedEndpoint<?>) o;
        return route.equals(that.route) && method == that.method && endpoint.equals(that.endpoint);
    }

    @Override
    public int hashCode() {
        return Objects.hash(route, method, endpoint);
    }
}
