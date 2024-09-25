package com.emazon.microservicio_carrito.domain.model;

import java.math.BigDecimal;
import java.util.List;

public class Product {
    private final Long productId;
    private final String name;
    private final Long quantity;
    private final BigDecimal price;
    private List<Category> categories;
    private Brand brand;

    public Product(Long productId, String name, Long quantity, BigDecimal price, List<Category> categories, Brand brand) {
        this.productId = productId;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.categories = categories;
        this.brand = brand;
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

    public BigDecimal getPrice() {
        return price;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }
}
