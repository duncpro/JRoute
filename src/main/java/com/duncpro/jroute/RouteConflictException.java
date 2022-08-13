package com.duncpro.jroute;

public class RouteConflictException extends IllegalStateException {
    public RouteConflictException(String message) {
        super(message);
    }

    public RouteConflictException(String message, Exception cause) {
        super(message, cause);
    }
}
