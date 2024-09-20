package com.emazon.microservicio_carrito.domain.model;

import com.emazon.microservicio_carrito.domain.util.DomainConstants;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class Cart {
    private final Long cartId;
    private Long clientId;
    private Long totalQuantity;
    private BigDecimal totalPrice;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<CartProduct> products;

    public Cart(Long cartId, Long clientId, Long totalQuantity, BigDecimal totalPrice, LocalDateTime createdAt, LocalDateTime updatedAt, List<CartProduct> products) {
        this.cartId =  cartId;
        this.clientId = requireNonNull(clientId, DomainConstants.FIELD_CLIENT_ID_NULL_MESSAGE);
        this.totalQuantity = requireNonNull(totalQuantity, DomainConstants.FIELD_TOTAL_QUANTITY_NULL_MESSAGE);
        this.totalPrice = requireNonNull(totalPrice, DomainConstants.FIELD_TOTAL_PRICE_NULL_MESSAGE);
        this.createdAt = requireNonNull(createdAt, DomainConstants.FIELD_CREATED_AT_NULL_MESSAGE);
        this.updatedAt = requireNonNull(updatedAt, DomainConstants.FIELD_UPDATED_AT_NULL_MESSAGE);
        this.products = products;
    }

    public Long getCartId() {
        return cartId;
    }

    public Long getClientId() {
        return clientId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public List<CartProduct> getProducts() {
        return products;
    }

    public Long getTotalQuantity() {
        return totalQuantity;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public void setTotalQuantity(Long totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setProducts(List<CartProduct> products) {
        this.products = products;
    }
}
