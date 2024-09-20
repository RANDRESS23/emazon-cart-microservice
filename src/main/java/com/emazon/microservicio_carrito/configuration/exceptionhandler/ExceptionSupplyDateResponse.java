package com.emazon.microservicio_carrito.configuration.exceptionhandler;

import java.time.LocalDate;

public class ExceptionSupplyDateResponse {
    private final String message;
    private final LocalDate nextSupplyDate;

    public ExceptionSupplyDateResponse(String message, LocalDate nextSupplyDate) {
        this.message = message;
        this.nextSupplyDate = nextSupplyDate;
    }

    public String getMessage() {
        return message;
    }

    public LocalDate getNextSupplyDate() {
        return nextSupplyDate;
    }
}
