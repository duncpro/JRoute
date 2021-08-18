package com.duncpro.jroute.router;

import com.duncpro.jroute.HttpMethod;
import com.duncpro.jroute.RouteConflictException;

import java.util.Optional;

public interface Router<E> {
    Optional<RouterResult<E>> route(HttpMethod method, String path);

    void addRoute(HttpMethod method, String routeString, E endpoint) throws RouteConflictException;
}
