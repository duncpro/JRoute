package com.duncpro.jroute.router;

import com.duncpro.jroute.Path;
import com.duncpro.jroute.RouteConflictException;
import com.duncpro.jroute.route.Route;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Router<E> {
    default Optional<RouteMatch<E>> route(String pathString) {
        return route(new Path(pathString));
    }

    Optional<RouteMatch<E>> route(Path path);

    default void add(String routeString, E endpoint) throws RouteConflictException {
        add(new Route(routeString), endpoint);
    }

    void add(Route route, E endpoint) throws RouteConflictException;

    E getOrAdd(Route route, Supplier<E> endpointFactory);

    /**
     * Returns a set of {@link PositionedEndpoint}s which are accessible via the given {@link Route}.
     * The returned collection includes the endpoints defined on the given {@link Route} as well as all endpoints
     * which exists on routes that descend from the given {@link Route}.
     */
    Set<PositionedEndpoint<E>> getAllEndpoints(Route prefix);

    /**
     * @throws RouteConflictException if the given endpoint conflicts with a pre-existing endpoint within the router.
     */
    default void add(PositionedEndpoint<E> positionedEndpoint) {
        add(positionedEndpoint.route, positionedEndpoint.endpoint);
    }
}
