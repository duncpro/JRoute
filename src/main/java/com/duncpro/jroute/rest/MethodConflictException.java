package com.duncpro.jroute.rest;

import static java.util.Objects.requireNonNull;

public class MethodConflictException extends RuntimeException {
    MethodConflictException(String message) {
        super(message);
    }
}
