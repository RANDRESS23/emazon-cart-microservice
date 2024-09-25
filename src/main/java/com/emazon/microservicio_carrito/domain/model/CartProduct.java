package com.emazon.microservicio_carrito.domain.model;

import com.emazon.microservicio_carrito.domain.util.DomainConstants;

import java.math.BigDecimal;

import static java.util.Objects.requireNonNull;

public class CartProduct {
    private Long cartProductId;
    private Long cartId;
    private final Long productId;
    private String name;
    private Long quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;

    public CartProduct(Long cartProductId, Long cartId, Long productId, String name, Long quantity, BigDecimal unitPrice, BigDecimal totalPrice) {
        this.cartProductId = cartProductId;
        this.cartId = cartId;
        this.productId = requireNonNull(productId, DomainConstants.FIELD_PRODUCT_ID_NULL_MESSAGE);
        this.name = name;
        this.quantity = requireNonNull(quantity, DomainConstants.FIELD_QUANTITY_NULL_MESSAGE);
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
    }

    public Long getCartProductId() {
        return cartProductId;
    }

    public Long getCartId() {
        return cartId;
    }

    public Long getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public Long getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setCartProductId(Long cartProductId) {
        this.cartProductId = cartProductId;
    }

    public void setCartId(Long cartId) {
        this.cartId = cartId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}
