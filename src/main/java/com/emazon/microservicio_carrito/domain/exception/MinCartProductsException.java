package com.emazon.microservicio_carrito.domain.exception;

public class MinCartProductsException extends RuntimeException {
    public MinCartProductsException(String message) {
        super(message);
    }
}
