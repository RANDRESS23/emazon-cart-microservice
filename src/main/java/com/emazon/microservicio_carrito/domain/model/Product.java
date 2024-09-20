package com.emazon.microservicio_carrito.domain.model;

import java.math.BigDecimal;
import java.util.List;

public class Product {
    private final Long productId;
    private final Long quantity;
    private final BigDecimal price;
    private List<Long> categories;

    public Product(Long productId, Long quantity, BigDecimal price, List<Long> categories) {
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
        this.categories = categories;
    }

    public Long getProductId() {
        return productId;
    }

    public Long getQuantity() {
        return quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public List<Long> getCategories() {
        return categories;
    }

    public void setCategories(List<Long> categories) {
        this.categories = categories;
    }
}
