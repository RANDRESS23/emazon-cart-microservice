package com.emazon.microservicio_carrito.domain.exception;

public class AlreadyExistsFieldException extends RuntimeException {
    public AlreadyExistsFieldException(String message) {
        super(message);
    }
}
