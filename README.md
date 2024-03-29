# JRoute
General purpose router for Java.
Supports basic path/route matching, as well as REST matching with HTTP verbs.

[![Build Status](https://www.travis-ci.com/duncpro/JRoute.svg?branch=master)](https://www.travis-ci.com/duncpro/JRoute)
[![codecov](https://codecov.io/gh/duncpro/JRoute/branch/master/graph/badge.svg?token=01IKEI8IW6)](https://codecov.io/gh/duncpro/JRoute)
[![](https://jitpack.io/v/com.duncpro/jroute.svg)](https://jitpack.io/#com.duncpro/jroute)

## Getting Started with REST Routing
### Create a Router
```java
final RestRouter<Supplier<Integer>> router = new RestRouter<>();
```
The type parameter describes the type of the endpoint class.
In practice, this will be some interface which is capable of processing
HTTP requests and producing HTTP responses. For the sake of brevity we will be using integer suppliers as our endpoint type.
All endpoints inside a router must conform to the same type. So using something like `Supplier<Integer>` is
less than ideal.
### Add a Route
```java
router.add(HttpMethod.GET, "/users/*/age", () -> new Random().nextInt(100));
```
The wildcard route element `*` indicates this endpoint accepts a single path parameter.
In this case the parameter represents the user's id. 

The third parameter of `add` is the endpoint. For the sake of brevity we will generate a random
number for each request instead of looking up the user's actual age in a database.
### Process Inbound Requests
```java
final Optional<RestRouteMatch<Supplier<Integer>>> result = router.route(HttpMethod.GET, "/users/duncpro/age").asOptional();
```
An empty optional is returned if no route matches the given path. In this case there is obviously a match since
we just registered a route for this path above. In practice you should present a 404 page if a path
cannot be routed, but for the sake of brevity we will skip that step.
```java
final Supplier<Integer> endpoint = result.orElseThrow(AssertionError::new).getEndpoint();
```
In addition to the endpoint, `RouterResult` also contains the `Route` object which matched
the given path. This can be used to extract the values passed in for variable route elements.
```java
final List<String> pathArguments = result.orElseThrow(AssertionError::new).getRoute()
        .extractVariables("/users/duncpro/age");

assertEquals(pathArguments, List.of("duncpro"));
```
### Another Example
For a more in-depth example see the `src/test` directory.
## Thread Safety
All classes are marked with JCIP annotations describing the level of thread-safety which they support.
Notably `TreeRouter` is not thread safe for mutations. Consider wrapping it in a `ReadWriteLock` if you wish
to add routes concurrently. In practice route registration typically only happens at startup and the process is quick 
enough that multi-threading is unnecessary.

## Router Inspection
In some cases, like when generating API documentation, an application might need to
inspect the state of a router at runtime. This can be accomplished using the `Router.getAllEndpoints()` method.

The following example generates a set of all the endpoints which exist on, and descend from,
the root route. In other words, all endpoints registered with the router.
```java
final Router<T> routers = /* ... */;
final Set<PositionedEndpoint<T>> positionedEndpoints = router.getAllEndpoints(Route.ROOT);
```

## Parameterized Routes
Accessing the value of a wildcard route element by its numerical index can be brittle
and unergonomic. To remedy this problem, the utility class `ParameterizedRoute` is provided.
This utility class is a wrapper around `Route` which associates a text label with
every wildcard in a route. 
```java
final ParameterizedRoute route = ParamterizedRoute.parse("/customers/{customerId}/orders/{orderId}");
final Path path = Path("/customers/duncan/orders/abc123");
final Map<String, String> pathArguments = route.extractVariablesMap(path);
assertEquals(Map.of("customerId", "duncan", "orderId", "abc123"), pathArguments);
assertEquals(Route("/customers/*/orders/*"), route);
```

## More Docs
There is a Javadoc for this library [here](https://duncpro.github.io/JRoute).
