package com.emazon.microservicio_carrito.domain.spi;

import com.emazon.microservicio_carrito.domain.model.Product;

public interface IStockPersistencePort {
    Product verifyProduct(Long productId);
}