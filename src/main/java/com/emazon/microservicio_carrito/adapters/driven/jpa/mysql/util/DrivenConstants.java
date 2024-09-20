package com.emazon.microservicio_carrito.adapters.driven.jpa.mysql.util;

public class DrivenConstants {
    private DrivenConstants() {
        throw new IllegalStateException("Utility class");
    }

    public static final String CART_TABLE_NAME = "cart";
    public static final String COLUMN_CART_ID = "cart_id";
    public static final String COLUMN_CART_CLIENT_ID = "client_id";
    public static final String COLUMN_CART_TOTAL_QUANTITY = "total_quantity";
    public static final String COLUMN_CART_UNIT_PRICE = "unit_price";
    public static final String COLUMN_CART_TOTAL_PRICE = "total_price";
    public static final String COLUMN_CART_CREATED_AT = "created_at";
    public static final String COLUMN_CART_UPDATED_AT = "updated_at";

    public static final String CART_PRODUCT_TABLE_NAME = "cart_products";
    public static final String COLUMN_CART_PRODUCT_ID = "cart_product_id";
    public static final String COLUMN_CART_PRODUCT_PRODUCT_ID = "product_id";
    public static final String COLUMN_CART_PRODUCT_QUANTITY = "quantity";

    public static final String AUTHORIZATION_HEADER  = "Authorization";
}
