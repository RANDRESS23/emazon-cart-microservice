package com.emazon.microservicio_carrito.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class CartProductPage {
    private Long cartProductId;
    private Long productId;
    private String name;
    private Long stockQuantity;
    private LocalDate nextSupplyDate;
    private Long totalQuantityInCart;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private List<Category> categories;
    private Brand brand;

    public CartProductPage(CartProductPageBuilder builder) {
        this.cartProductId = builder.cartProductId;
        this.productId = builder.productId;
        this.name = builder.name;
        this.stockQuantity = builder.stockQuantity;
        this.nextSupplyDate = builder.nextSupplyDate;
        this.totalQuantityInCart = builder.totalQuantityInCart;
        this.unitPrice = builder.unitPrice;
        this.totalPrice = builder.totalPrice;
        this.categories = builder.categories;
        this.brand = builder.brand;
    }

    public static class CartProductPageBuilder {
        private Long cartProductId;
        private Long productId;
        private String name;
        private Long stockQuantity;
        private LocalDate nextSupplyDate;
        private Long totalQuantityInCart;
        private BigDecimal unitPrice;
        private BigDecimal totalPrice;
        private List<Category> categories;
        private Brand brand;

        public CartProductPageBuilder cartProductId(Long cartProductId) {
            this.cartProductId = cartProductId;
            return this;
        }

        public CartProductPageBuilder productId(Long productId) {
            this.productId = productId;
            return this;
        }

        public CartProductPageBuilder name(String name) {
            this.name = name;
            return this;
        }

        public CartProductPageBuilder stockQuantity(Long stockQuantity) {
            this.stockQuantity = stockQuantity;
            return this;
        }

        public CartProductPageBuilder nextSupplyDate(LocalDate nextSupplyDate) {
            this.nextSupplyDate = nextSupplyDate;
            return this;
        }

        public CartProductPageBuilder totalQuantityInCart(Long totalQuantityInCart) {
            this.totalQuantityInCart = totalQuantityInCart;
            return this;
        }

        public CartProductPageBuilder unitPrice(BigDecimal unitPrice) {
            this.unitPrice = unitPrice;
            return this;
        }

        public CartProductPageBuilder totalPrice(BigDecimal totalPrice) {
            this.totalPrice = totalPrice;
            return this;
        }

        public CartProductPageBuilder categories(List<Category> categories) {
            this.categories = categories;
            return this;
        }

        public CartProductPageBuilder brand(Brand brand) {
            this.brand = brand;
            return this;
        }

        public CartProductPage build() {
            return new CartProductPage(this);
        }
    }

    public Long getCartProductId() {
        return cartProductId;
    }

    public void setCartProductId(Long cartProductId) {
        this.cartProductId = cartProductId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getStockQuantity() {
        return stockQuantity;
    }

    public LocalDate getNextSupplyDate() {
        return nextSupplyDate;
    }

    public void setStockQuantity(Long stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public void setNextSupplyDate(LocalDate nextSupplyDate) {
        this.nextSupplyDate = nextSupplyDate;
    }

    public Long getTotalQuantityInCart() {
        return totalQuantityInCart;
    }

    public void setTotalQuantityInCart(Long totalQuantityInCart) {
        this.totalQuantityInCart = totalQuantityInCart;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }
}
