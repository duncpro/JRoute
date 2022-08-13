package com.duncpro.jroute.rest;

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
}