package com.duncpro.jroute.router;

import com.duncpro.jroute.rest.HttpMethod;
import com.duncpro.jroute.RouteConflictException;
import com.duncpro.jroute.route.Route;
import com.duncpro.jroute.route.StaticRouteElement;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TreeRouterTest {
    @Test
    void createAndLookupRoute() {
        final var tree = new RouteTreeNode<>(RouteTreeNodePosition.root());

        final var expectedEndpoint = 1;

        TreeRouter.findOrCreateNode(tree, new Route("/hello/world"))
                .setEndpoint(expectedEndpoint);

        final var actualEndpoint = tree
                .getOrCreateChildRoute(new StaticRouteElement("hello"))
                .getOrCreateChildRoute(new StaticRouteElement("world"))
                .getEndpoint()
                .orElseThrow();

        assertEquals(expectedEndpoint, actualEndpoint);
    }

    @Test
    void resolveNormalPath() {
        final var router = new TreeRouter<>();

        final var expectedEndpoint = 1;

        router.add( "/hello/world", expectedEndpoint);

        final var actualEndpoint = router.route("/hello/world")
                .orElseThrow()
                .getEndpoint();

        assertEquals(expectedEndpoint, actualEndpoint);
    }

    @Test
    void resolveWildcardPath() {
        final var router = new TreeRouter<>();

        final var expectedEndpoint = 10;

        router.add("/users/*/pets/*/age", expectedEndpoint);

        final var actualEndpoint = router.route("/users/duncan/pets/cocoa/age")
                .orElseThrow()
                .getEndpoint();

        assertEquals(expectedEndpoint, actualEndpoint);
    }

    @Test
    void unresolvablePath() {
        final var router = new TreeRouter<>();

        router.add( "/users", 0);

        final var result = router.route("/settings");

        assertTrue(result.isEmpty());
    }

    @Test
    void conflictWildcardAfterStaticRoute() {
        final var router = new TreeRouter<>();
        router.add("/users/duncan", new Object());

        assertThrows(RouteConflictException.class, () -> {
            router.add("/users/*", new Object());
        });
    }

    @Test
    void conflictStaticRouteAfterWildcard() {
        final var router = new TreeRouter<>();
        router.add("/users/*", new Object());

        assertThrows(RouteConflictException.class, () -> {
            router.add("/users/duncan", new Object());
        });
    }

    @Test
    void routeResultHasCorrectRoute() {
        final var router = new TreeRouter<>();

        final var expected = new Route("/users/*/devices");
        router.add(expected.toString(), new Object());

        final var actual = router.route("/users/duncan/devices")
                .orElseThrow()
                .getRoute();

        assertEquals(expected, actual);
    }

    @Test
    void getAllEndpoints() {
        final TreeRouter<Integer> router = new TreeRouter<>();
        assertEquals(0, router.getAllEndpoints(Route.ROOT).size());

        final var expectedEndpoint = new PositionedEndpoint<>(new Route("/hello"), 1);
        final var expectedNestedEndpoint = new PositionedEndpoint<>(new Route("/hello/world"), 2);
        router.add(expectedEndpoint);
        router.add(expectedNestedEndpoint);

        {
            final var resolvedEndpoints = router.getAllEndpoints(new Route("/hello/world"));
            final var resolvedEndpoint = resolvedEndpoints.stream().findFirst().orElseThrow();
            assertEquals(1, resolvedEndpoints.size());
            assertEquals(resolvedEndpoint, expectedNestedEndpoint);
        }

        {
            final var resolvedEndpoints = router.getAllEndpoints(new Route("/hello"));
            assertEquals(2, resolvedEndpoints.size());
            assertTrue(resolvedEndpoints.stream()
                .anyMatch(endpoint -> endpoint.equals(expectedEndpoint)));
            assertTrue(resolvedEndpoints.stream()
                    .anyMatch(endpoint -> endpoint.equals(expectedNestedEndpoint)));
        }

        {
            final var resolvedEndpoints = router.getAllEndpoints(Route.ROOT);
            assertEquals(2, resolvedEndpoints.size());
        }
    }
}
