package com.duncpro.jroute;

import com.duncpro.jroute.router.Router;
import com.duncpro.jroute.router.TreeRouter;
import com.duncpro.jroute.router.RouterResult;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SmokeTest {
    private static class Request {
        String path;
    }

    @Test
    public void example() {
        final Router<BiFunction<Request, List<String>, Integer>> router = new TreeRouter<>();

        // On application startup, register HTTP request handlers with the router.
        router.add(HttpMethod.GET, "/calculator/add/*/*", (request, pathArgs) -> {
            final var x = Integer.parseInt(pathArgs.get(0));
            final var y = Integer.parseInt(pathArgs.get(1));
            return x + y;
        });

        // Simulate an incoming HTTP request
        final var fakeIncomingRequest = new Request();
        fakeIncomingRequest.path = "/calculator/add/6/4";

        // Resolve the HTTP request handler based on the path in the URL.
        final RouterResult<BiFunction<Request, List<String>, Integer>> routerResult = router
                .route(HttpMethod.GET, fakeIncomingRequest.path);

        if (routerResult instanceof RouterResult.MethodNotAllowed) throw new AssertionError();
        if (routerResult instanceof RouterResult.ResourceNotFound) throw new AssertionError();
        if (!(routerResult instanceof RouterResult.Matched)) throw new AssertionError();

        final var match = (RouterResult.Matched<BiFunction<Request, List<String>, Integer>>) routerResult;

        // Extract the variable path elements, so they may be used by the request handler.
        final var pathArgs = match.getRoute().extractVariables(fakeIncomingRequest.path);

        // Invoke the request handler and pass in the variables.
        final var response = match.getEndpoint().apply(fakeIncomingRequest, pathArgs);

        // Make sure our calculator endpoint is working properly
        assertEquals(10, response);
    }
}
