package com.emazon.microservicio_carrito.domain.model;

import java.time.LocalDate;

public class Supply {
    private final Long supplyId;
    private final Long productId;
    private final LocalDate date;

    public Supply(Long supplyId, Long productId, LocalDate date) {
        this.supplyId = supplyId;
        this.productId = productId;
        this.date = date;
    }

    public Long getSupplyId() {
        return supplyId;
    }

    public Long getProductId() {
        return productId;
    }

    public LocalDate getDate() {
        return date;
    }
}
