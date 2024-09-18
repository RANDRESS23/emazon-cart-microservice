package com.emazon.microservicio_carrito.domain.model;

public class CartOrSupplyDate {
    private Cart cart;
    private SupplyDate supplyDate;

    public CartOrSupplyDate(Cart cart) {
        this.cart = cart;
    }

    public CartOrSupplyDate(SupplyDate supplyDate) {
        this.supplyDate = supplyDate;
    }

    public Cart getCart() {
        return cart;
    }

    public SupplyDate getSupplyDate() {
        return supplyDate;
    }
}
