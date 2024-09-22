package com.emazon.microservicio_carrito.domain.util;

public class DomainConstants {
    private DomainConstants() {
        throw new IllegalStateException("Utility class");
    }

    public static final Integer ZERO_CONSTANT = 0;
    public static final Integer DAYS_FOR_SUPPLY = 30;
    public static final Integer MAXIMUM_QUANTITY_OF_PRODUCTS_OF_THE_SAME_CATEGORY = 3;
    public static final Integer MINUS_ONE = -1;

    public static final String FIELD_CLIENT_ID_NULL_MESSAGE = "Field 'clientId' cannot be null";
    public static final String FIELD_TOTAL_QUANTITY_NULL_MESSAGE = "Field 'total quantity' cannot be null";
    public static final String FIELD_TOTAL_PRICE_NULL_MESSAGE = "Field 'total price' cannot be null";
    public static final String FIELD_PRODUCT_ID_NULL_MESSAGE = "Field 'productId' cannot be null";
    public static final String FIELD_QUANTITY_NULL_MESSAGE = "Field 'quantity' cannot be null";
    public static final String FIELD_CREATED_AT_NULL_MESSAGE = "Field 'createdAt' cannot be null";
    public static final String FIELD_UPDATED_AT_NULL_MESSAGE = "Field 'updatedAt' cannot be null";

    public static final String CART_ALREADY_EXISTS_MESSAGE = "Cart already exists.";
    public static final String CART_NOT_FOUND = "Cart not found.";

    public static final String CART_PRODUCT_NOT_FOUND = "The product was not found in the cart.";

    public static final String SUPPLY_DATE_MESSAGE = "There are no products in stock, please wait for the supply date.";

    public static final String NEGATIVE_NOT_ALLOWED_EXCEPTION_MESSAGE = "Negative exception not allowed.";

    public static final String INVALID_CATEGORY_PRODUCT_EXCEPTION_MESSAGE = "There are already 3 products in the same category.";

    public static final String UNKNOWN_ERROR_OCCURRED_MESSAGE = "Unknown error occurred.";

    public static final String INVALID_ADD_PRODUCT_QUANTITY_MESSAGE = "The quantity of the product you want to add to the cart exceeds the amount of stock available for the product.";
    public static final String INVALID_REMOVE_PRODUCT_QUANTITY_FROM_CART_MESSAGE = "The quantity of the product you want to remove from the cart exceeds the quantity of the product that is in the cart.";
}
