package com.emazon.microservicio_carrito.domain.exception;

public class NegativeNotAllowedException extends RuntimeException {
    public NegativeNotAllowedException(String message) {
        super(message);
    }
}
