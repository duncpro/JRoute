# JRoute
Extremely barebones URL router for Java.

[![Build Status](https://www.travis-ci.com/duncpro/JRoute.svg?branch=master)](https://www.travis-ci.com/duncpro/JRoute)
[![codecov](https://codecov.io/gh/duncpro/JRoute/branch/master/graph/badge.svg?token=01IKEI8IW6)](https://codecov.io/gh/duncpro/JRoute)

## FAQ
- The included implementation of `Router`, `TreeRouter`, is not thread-safe for mutations.
It can however be safely used by multiple threads simultaneously if the threads are only
  routing requests via `TreeRouter.route`.

## Usage
```java
class Example {
    public void example() {
        // Instantiate a new router, passing in the type of the request handler.
        // Our request handler will accept a Request object and a list of path arguments.
        // Our request handler will return an integer response.
        final Router<BiFunction<Request, List<String>, Integer>> router = new TreeRouter<>();

        // On application startup, register the request handlers with the router.
        router.addRoute(HttpMethod.GET, "/calculator/add/*/*", (request, pathArgs) -> {
            final var x = Integer.parseInt(pathArgs.get(0));
            final var y = Integer.parseInt(pathArgs.get(1));
            return x + y;
        });

        // Simulate an incoming request
        final var fakeIncomingRequest = new Request();
        fakeIncomingRequest.path = "/calculator/add/6/4";
        fakeIncomingRequest.method = "GET";

        // Resolve the request handler based on the path.
        final Optional<RouterResult> routerResult = router.route(
                HttpMethod.valueOf(fakeIncomingRequest.method),
                fakeIncomingRequest.path
        );

        // Extract the variable path elements so they may be used by the request handler.
        final var pathArgs = routerResult.orElseThrow()
                .getRoute().extractVariables(fakeIncomingRequest.path);

        // Invoke the request handler and pass in the variables.
        final var response = routerResult.orElseThrow().getEndpoint()
                .apply(fakeIncomingRequest, pathArgs);

        // Make sure our calculator endpoint is working properly
        assertEquals(10, response);
    }
}
```


## Terminology
### Path
A subsection of a URL. Namely, the portion following the domain-name and preceding the query parameters.
Example: `/users/1234/favorite-foods`.

See `com.duncpro.jroute.Path`.
### Route
A path-like string which describes a subset of paths.
Example: `/users/*/favorite-foods`.

This route can match an infinite number of paths.

See `com.duncpro.jroute.route.Route`.
### Endpoint
Every route has a single *Endpoint* object associated with it. This object is of
arbitrary type.

In JRoute endpoints are represented by the type parameter `E`.
### Router
Specialized object which is responsible for mapping *Paths* to *Routes*.

To remove the need for a second mapping mechanism, *Router* also stores associations
between *Routes* and *Endpoints*.

See `com.duncpro.jroute.router.Router`.

### Path Element
The smallest unit of a path. Path units are separated by slashes.
Paths take the form of `path element/path element/` etc.

In JRoute these are represented by `String`.

### Route Element
The smallest unit of a route. Like Path elements, Route elements are separated by slashes.
The differentiating factor between Route elements and Path elements is that `*` has a special
meaning as a Route element. It is a wildcard and matches any *path element*.
