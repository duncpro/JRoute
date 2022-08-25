package com.duncpro.jroute.rest;

import com.duncpro.jroute.util.ParameterizedRoute;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RestRouterTest {
    @Test
    public void testMethodMatchFailure() {
        RestRouter<Integer> router = new RestRouter<>();
        router.add(HttpMethod.GET, "/hello", 1);
        final var result = router.route(HttpMethod.POST, "/hello");
        assertTrue(result instanceof RestRouteResult.UnsupportedMethod);
    }

    @Test
    public void testRouteMatchFailure() {
        RestRouter<Integer> router = new RestRouter<>();
        router.add(HttpMethod.GET, "/hello", 1);
        final var result = router.route(HttpMethod.GET, "/hello1");
        assertTrue(result instanceof RestRouteResult.ResourceNotFound);
    }

    @Test
    public void testRouteMatchSuccess() {
        RestRouter<Integer> router = new RestRouter<>();
        router.add(HttpMethod.GET, "/hello", 1);
        final var result = router.route(HttpMethod.GET, "/hello");
        assertTrue(result instanceof RestRouteResult.RestRouteMatch);
    }

    @Test
    public void testMultipleWildcardsSmoke() {
        RestRouter<Integer> router = new RestRouter<>();
        router.add(HttpMethod.GET, ParameterizedRoute.parse("/users/{userId}/docs/{docId}"), 1);
        router.add(HttpMethod.OPTIONS,  ParameterizedRoute.parse("/users/{userId}/docs/{docId}"), 1);
        router.add(HttpMethod.GET, ParameterizedRoute.parse("/festivals/{festivalId}/something"), 1);
        router.add(HttpMethod.OPTIONS, ParameterizedRoute.parse("/festivals/{festivalId}/something"), 1);
    }
}