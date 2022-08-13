package com.duncpro.jroute.rest;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

public class RestResource<E> {
    private final Map<HttpMethod, E> methodMap = new HashMap<>();

    void addEndpoint(HttpMethod method, E endpoint) throws MethodConflictException {
        final E prev = methodMap.putIfAbsent(method, endpoint);
        if (prev != null) throw new MethodConflictException("There is already an endpoint associated" +
                " with the method " + method + " at this endpoint");
    }

    public Optional<E> getMethodEndpoint(HttpMethod method) {
        return Optional.ofNullable(methodMap.get(method));
    }
}
