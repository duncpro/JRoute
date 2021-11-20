package com.duncpro.jroute.router;

import com.duncpro.jroute.HttpMethod;
import com.duncpro.jroute.RouteConflictException;
import com.duncpro.jroute.route.Route;
import com.duncpro.jroute.route.StaticRouteElement;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TreeRouterTest {
    @Test
    void createAndLookupRoute() {
        final var tree = new RouteTreeNode<>(RouteTreeNodePosition.root());

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

    @Test
    void getAllEndpoints() {
        final TreeRouter<Integer> router = new TreeRouter<>();
        assertEquals(0, router.getAllEndpoints(Route.ROOT).size());

        final var expectedEndpoint = new PositionedEndpoint<>(new Route("/hello"), HttpMethod.GET, 1);
        final var expectedNestedEndpoint = new PositionedEndpoint<>(new Route("/hello/world"), HttpMethod.GET, 2);
        router.addRoute(expectedEndpoint);
        router.addRoute(expectedNestedEndpoint);

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

    @Test
    void sum() {
        final Router<Integer> router1 = new TreeRouter<>();
        final var endpoint1 = new PositionedEndpoint<>(new Route("/hello/world"), HttpMethod.GET, 1);
        router1.addRoute(endpoint1);

        final Router<Integer> router2 = new TreeRouter<>();
        final var endpoint2 = new PositionedEndpoint<>(new Route("/hello"), HttpMethod.GET, 1);
        router2.addRoute(endpoint2);

        final var composedRouter = TreeRouter.sum(Set.of(router1, router2));

        {
            final var resolvedEndpoints = composedRouter.getAllEndpoints(new Route("/hello"));
            assertEquals(2, resolvedEndpoints.size());
            assertTrue(resolvedEndpoints.stream()
                    .anyMatch(endpoint -> endpoint.equals(endpoint1)));
            assertTrue(resolvedEndpoints.stream()
                    .anyMatch(endpoint -> endpoint.equals(endpoint2)));
        }
    }

    @Test
    void prefix() {
        final Router<Object> originalRouter = new TreeRouter<>();
        final var endpoint = new Object();

        originalRouter.addRoute(HttpMethod.GET, "world", endpoint);

        final var prefixedRouter = TreeRouter.prefix(originalRouter, new Route("hello"));
        final var resolvedEndpoints = prefixedRouter.getAllEndpoints(Route.ROOT);

        assertEquals(1, resolvedEndpoints.size());
        final var loneRoute = resolvedEndpoints.stream().findFirst().orElseThrow();
        assertEquals(new PositionedEndpoint<>(new Route("hello/world"), HttpMethod.GET, endpoint), loneRoute);
    }
}
