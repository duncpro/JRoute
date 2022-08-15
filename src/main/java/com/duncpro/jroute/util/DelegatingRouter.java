package com.duncpro.jroute.util;

import com.duncpro.jroute.Path;
import com.duncpro.jroute.RouteConflictException;
import com.duncpro.jroute.route.Route;
import com.duncpro.jroute.router.PositionedEndpoint;
import com.duncpro.jroute.router.RouteMatch;
import com.duncpro.jroute.router.Router;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

public abstract class DelegatingRouter<E> implements Router<E> {
    private final Router<E> underlyingRouter;

    protected DelegatingRouter(Router<E> underlyingRouter) {
        this.underlyingRouter = Objects.requireNonNull(underlyingRouter);
    }

    @Override
    public Optional<RouteMatch<E>> route(Path path) {
        return underlyingRouter.route(path);
    }

    @Override
    public void add(Route route, E endpoint) throws RouteConflictException {
        underlyingRouter.add(route, endpoint);
    }

    @Override
    public E getOrAdd(Route route, Supplier<E> endpointFactory) {
        return underlyingRouter.getOrAdd(route, endpointFactory);
    }

    @Override
    public Set<PositionedEndpoint<E>> getAllEndpoints(Route prefix) {
        return underlyingRouter.getAllEndpoints(prefix);
    }
}
