package com.emazon.microservicio_carrito.adapters.driving.util;

public class DrivingConstants {
    private DrivingConstants() {
        throw new IllegalStateException("Utility class");
    }

    public static final String HAS_ROLE_CLIENT = "hasRole('CLIENTE')";

    public static final String CART_ID = "cartId";
    public static final String CART_PRODUCT_ID = "cartProductId";

    public static final String DEFAULT_PAGE_PARAM = "0";
    public static final String DEFAULT_SIZE_PARAM = "10";
    public static final String DEFAULT_SORT_PARAM = "asc";
    public static final String DEFAULT_CATEGORY_PARAM = "all";
    public static final String DEFAULT_BRAND_PARAM = "all";

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

    public static final String REMOVE_CART_PRODUCT_SUMMARY = "Remove cart product";
    public static final String REMOVE_CART_PRODUCT_DESCRIPTION = "Remove a product from the cart";
    public static final String REMOVE_CART_PRODUCT_RESPONSE_201_DESCRIPTION = "Cart product removed successfully";
    public static final String REMOVE_CART_PRODUCT_RESPONSE_400_DESCRIPTION = "Invalid field";
    public static final String REMOVE_CART_PRODUCT_RESPONSE_503_DESCRIPTION = "Service unavailable";

    public static final String GET_CART_PRODUCTS_SUMMARY = "Get all cart products";
    public static final String GET_CART_PRODUCTS_DESCRIPTION = "Get all the products from the cart";
    public static final String GET_CART_PRODUCTS_RESPONSE_200_DESCRIPTION = "Cart products successfully obtained";
    public static final String GET_CART_PRODUCTS_RESPONSE_400_DESCRIPTION = "Bad request";
    public static final String GET_CART_PRODUCTS_RESPONSE_503_DESCRIPTION = "Service unavailable";

    public static final String CART_PRODUCTS_BOUGHT_MESSAGE = "Products bought successfully!";
}
