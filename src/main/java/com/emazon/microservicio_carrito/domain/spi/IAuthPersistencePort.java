package com.emazon.microservicio_carrito.domain.spi;

public interface IAuthPersistencePort {
    Long getAuthenticatedUserId();
    String getAuthenticatedUserEmail();
}
