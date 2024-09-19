package com.emazon.microservicio_carrito.domain.exception;

import java.time.LocalDate;

public class SupplyDateException extends RuntimeException {
    private final LocalDate nextSupplyDate;

    public SupplyDateException(String message, LocalDate nextSupplyDate) {
        super(message);
        this.nextSupplyDate = nextSupplyDate;
    }

    public LocalDate getNextSupplyDate() {
        return nextSupplyDate;
    }
}
