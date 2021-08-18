package com.duncpro.jroute;

import com.duncpro.jroute.router.Router;
import com.duncpro.jroute.router.TreeRouter;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExampleUsage {
    private static class Request {
        String path;
    }

    @Test
    public void example() {
        final Router<BiFunction<Request, List<String>, Integer>> router = new TreeRouter<>();

        // On application startup, register HTTP request handlers with the router.
        router.addRoute(HttpMethod.GET, "/calculator/add/*/*", (request, pathArgs) -> {
            final var x = Integer.parseInt(pathArgs.get(0));
            final var y = Integer.parseInt(pathArgs.get(1));
            // Lookup the age of the pet in the database
            // We'll fake it for now
            return x + y;
        });

        // Simulate an incoming HTTP request
        final var fakeIncomingRequest = new Request();
        fakeIncomingRequest.path = "/calculator/add/6/4";

        // Resolve the HTTP request handler based on the path in the URL.
        final var routerResult = router.route(HttpMethod.GET, fakeIncomingRequest.path)
                .orElseThrow();

        // Extract the variable path elements so they may be used by the request handler.
        final var pathArgs = routerResult.getRoute().extractVariables(fakeIncomingRequest.path);

        // Invoke the request handler and pass in the variables.
        final var response = routerResult.getEndpoint().apply(fakeIncomingRequest, pathArgs);

        // Make sure our calculator endpoint is working properly
        assertEquals(10, response);
    }
}
