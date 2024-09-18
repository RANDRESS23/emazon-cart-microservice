package com.emazon.microservicio_carrito.domain.model;

import java.time.LocalDate;

public class SupplyDate {
    private String message;
    private LocalDate nextSupplyDate;

    public SupplyDate(String message, LocalDate nextSupplyDate) {
        this.message = message;
        this.nextSupplyDate = nextSupplyDate;
    }

    public String getMessage() {
        return message;
    }

    public LocalDate getNextSupplyDate() {
        return nextSupplyDate;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setNextSupplyDate(LocalDate nextSupplyDate) {
        this.nextSupplyDate = nextSupplyDate;
    }
}
