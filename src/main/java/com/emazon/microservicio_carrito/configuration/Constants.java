package com.emazon.microservicio_carrito.configuration;

public class Constants {
    private Constants(){
        throw new IllegalStateException("utility class");
    }

    public static final String USER_ID_FIELD  = "userId";
    public static final String ROLE_FIELD  = "role";

    public static final String STOCK_SERVICE_NAME = "microservicio-stock";
    public static final String STOCK_SERVICE_URL = "http://localhost:8080/api/v1/product";

    public static final String TRANSACTION_SERVICE_NAME = "microservicio-transaccion";
    public static final String TRANSACTION_SERVICE_URL = "http://localhost:8082/api/v1/supply";

    public static final String AUTHORIZATION_HEADER  = "Authorization";
    public static final String BEARER_HEADER  = "Bearer ";
}
