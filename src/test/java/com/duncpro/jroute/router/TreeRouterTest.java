package com.duncpro.jroute.router;

import com.duncpro.jroute.HttpMethod;
import com.duncpro.jroute.RouteConflictException;
import com.duncpro.jroute.route.Route;
import com.duncpro.jroute.route.RouteElement;
import com.duncpro.jroute.route.StaticRouteElement;
import com.duncpro.jroute.route.WildcardRouteElement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TreeRouterTest {
    @Test
    void createAndLookupRoute() {
        final var tree = RouteTreeNode.newTree();

        final var expectedEndpoint = 1;

        TreeRouter.findOrCreateNode(tree, new Route("/hello/world"))
                .addEndpoint(HttpMethod.GET, expectedEndpoint);

        final var actualEndpoint = tree
                .getOrCreateChildRoute(new StaticRouteElement("hello"))
                .getOrCreateChildRoute(new StaticRouteElement("world"))
                .getEndpointAsRouterResult(HttpMethod.GET)
                .orElseThrow()
                .getEndpoint();

        assertEquals(expectedEndpoint, actualEndpoint);
    }

    @Test
    void resolveNormalPath() {
        final var router = new TreeRouter<>();

        final var expectedEndpoint = 1;

        router.addRoute(HttpMethod.GET, "/hello/world", expectedEndpoint);

        final var actualEndpoint = router.route(HttpMethod.GET, "/hello/world")
                .orElseThrow()
                .getEndpoint();

        assertEquals(expectedEndpoint, actualEndpoint);
    }

    @Test
    void resolveWildcardPath() {
        final var router = new TreeRouter<>();

        final var expectedEndpoint = 10;

        router.addRoute(HttpMethod.GET, "/users/*/pets/*/age", expectedEndpoint);

        final var actualEndpoint = router.route(HttpMethod.GET, "/users/duncan/pets/cocoa/age")
                .orElseThrow()
                .getEndpoint();

        assertEquals(expectedEndpoint, actualEndpoint);
    }

    @Test
    void unresolvablePath() {
        final var router = new TreeRouter<>();

        router.addRoute(HttpMethod.GET, "/users", 0);

        final var endpoint = router.route(HttpMethod.POST, "/settings");

        assertTrue(endpoint.isEmpty());
    }

    @Test
    void conflictWildcardAfterStaticRoute() {
        final var router = new TreeRouter<>();
        router.addRoute(HttpMethod.GET, "/users/duncan", new Object());

        assertThrows(RouteConflictException.class, () -> {
            router.addRoute(HttpMethod.GET, "/users/*", new Object());
        });
    }

    @Test
    void conflictStaticRouteAfterWildcard() {
        final var router = new TreeRouter<>();
        router.addRoute(HttpMethod.GET, "/users/*", new Object());

        assertThrows(RouteConflictException.class, () -> {
            router.addRoute(HttpMethod.GET, "/users/duncan", new Object());
        });
    }

    @Test
    void routeResultHasCorrectRoute() {
        final var router = new TreeRouter<>();

        final var expected = new Route("/users/*/devices");
        router.addRoute(HttpMethod.GET, expected.toString(), new Object());

        final var actual = router.route(HttpMethod.GET, "/users/duncan/devices")
                .orElseThrow()
                .getRoute();

        assertEquals(expected, actual);
    }
}
