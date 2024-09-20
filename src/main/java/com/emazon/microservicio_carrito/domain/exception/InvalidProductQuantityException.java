package com.emazon.microservicio_carrito.domain.exception;

public class InvalidProductQuantityException extends RuntimeException {
    public InvalidProductQuantityException(String message) {
        super(message);
    }
}
