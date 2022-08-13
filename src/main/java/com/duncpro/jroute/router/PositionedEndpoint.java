package com.duncpro.jroute.router;

import com.duncpro.jroute.route.Route;

import java.util.Objects;

public class PositionedEndpoint<E> {
    public final Route route;
    public final E endpoint;

    public PositionedEndpoint(Route route, E endpoint) {
        this.route = Objects.requireNonNull(route);
        this.endpoint = Objects.requireNonNull(endpoint);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PositionedEndpoint<?> that = (PositionedEndpoint<?>) o;
        return route.equals(that.route) && endpoint.equals(that.endpoint);
    }

    @Override
    public int hashCode() {
        return Objects.hash(route, endpoint);
    }
}
