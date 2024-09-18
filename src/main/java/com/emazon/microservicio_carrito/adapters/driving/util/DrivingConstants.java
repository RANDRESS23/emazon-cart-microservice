package com.emazon.microservicio_carrito.adapters.driving.util;

public class DrivingConstants {
    private DrivingConstants() {
        throw new IllegalStateException("Utility class");
    }

    public static final String HAS_ROLE_CLIENT = "hasRole('CLIENTE')";

    public static final String CART_ID = "cartId";
    public static final String CART_PRODUCT_ID = "cartProductId";

    public static final String RESPONSE_CODE_201="201";
    public static final String RESPONSE_CODE_400="400";
    public static final String RESPONSE_CODE_503="503";

    public static final String TAG_CART_NAME = "Cart";
    public static final String TAG_CART_DESCRIPTION = "Cart API";
    public static final String SAVE_CART_PRODUCT_SUMMARY = "Save cart product";
    public static final String SAVE_CART_PRODUCT_DESCRIPTION = "Add a new product to the cart";
    public static final String SAVE_CART_PRODUCT_RESPONSE_201_DESCRIPTION = "Cart product saved successfully";
    public static final String SAVE_CART_PRODUCT_RESPONSE_400_DESCRIPTION = "Invalid field";
    public static final String SAVE_CART_PRODUCT_RESPONSE_503_DESCRIPTION = "Service unavailable";
}
