package com.duncpro.jroute;

import com.duncpro.jroute.route.Route;
import com.duncpro.jroute.route.RouteElement;
import com.duncpro.jroute.route.StaticRouteElement;
import com.duncpro.jroute.route.WildcardRouteElement;

public class JRouteUtilities {
    public static boolean accepts(String pathElement, RouteElement routeElement) {
        if (routeElement instanceof WildcardRouteElement) {
            return true;
        } else if (routeElement instanceof StaticRouteElement) {
            return ((StaticRouteElement) routeElement).getMatch().equals(pathElement);
        } else {
            throw new AssertionError();
        }
    }

    public static boolean accepts(Path path, Route route) {
        if (path.getElements().size() != route.getElements().size()) {
            return false;
        }

        for (int i = 0; i < path.getElements().size(); i++) {
            final var pathElement = path.getElements().get(i);
            final var routeElement = route.getElements().get(i);

            if (!accepts(pathElement, routeElement)) {
                return false;
            }
        }

        return true;
    }
}
