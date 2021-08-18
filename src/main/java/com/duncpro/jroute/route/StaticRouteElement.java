package com.duncpro.jroute.route;

import net.jcip.annotations.Immutable;

import java.util.Objects;

@Immutable
public final class StaticRouteElement extends RouteElement {
    private final String match;

    public StaticRouteElement(String match) {
        this.match = match;
    }

    public String getMatch() {
        return match;
    }

    @Override
    public String toString() {
        return match;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StaticRouteElement that = (StaticRouteElement) o;
        return match.equals(that.match);
    }

    @Override
    public int hashCode() {
        return Objects.hash(match);
    }
}
