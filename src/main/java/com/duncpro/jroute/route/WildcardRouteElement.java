package com.duncpro.jroute.route;

import net.jcip.annotations.Immutable;

@Immutable
public final class WildcardRouteElement extends RouteElement {
    @Override
    public boolean equals(Object o) {
        return o instanceof WildcardRouteElement;
    }

    @Override
    public String toString() {
        return "*";
    }
}
