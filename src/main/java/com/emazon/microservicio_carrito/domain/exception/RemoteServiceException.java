package com.emazon.microservicio_carrito.domain.exception;

public class RemoteServiceException extends RuntimeException {
    public RemoteServiceException(String message) {
        super(message);
    }
}
