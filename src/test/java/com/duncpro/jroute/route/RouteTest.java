package com.duncpro.jroute.route;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RouteTest {
    @Test
    public void createRouteWithLeadingSlash() {
        String routeString = "/a/b/c";
        final var actualRoute = new Route(routeString);

        final var expectedRoute = new Route(List.of(
                new StaticRouteElement("a"),
                new StaticRouteElement("b"),
                new StaticRouteElement("c")
        ));
        assertEquals(expectedRoute, actualRoute);
    }

    @Test
    public void createRoute() {
        String routeString = "a/b/c";
        final var actualRoute = new Route(routeString);

        final var expectedRoute = new Route(List.of(
                new StaticRouteElement("a"),
                new StaticRouteElement("b"),
                new StaticRouteElement("c")
        ));
        assertEquals(expectedRoute, actualRoute);
    }

    @Test
    public void createRouteWithTrailingSlash() {
        String routeString = "a/b/c/";
        final var actualRoute = new Route(routeString);

        final var expectedRoute = new Route(List.of(
                new StaticRouteElement("a"),
                new StaticRouteElement("b"),
                new StaticRouteElement("c")
        ));
        assertEquals(expectedRoute, actualRoute);
    }

    @Test
    public void extractPathVariables() {
        final var route = new Route("/users/*/pets/*/age");
        final var path = "/users/duncan/pets/cocoa/age";

        assertEquals(
                List.of("duncan", "cocoa"),
                route.extractVariables(path)
        );
    }

    @Test
    void resolve() {
        final var initial = new Route("/users/*");
        final var suffix = new Route("/pets/*");
        final var expected = new Route("/users/*/pets/*");

        final var actual = initial.resolve(suffix);
        assertEquals(expected, actual);
    }
}
