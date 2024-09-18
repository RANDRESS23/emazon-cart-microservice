package com.emazon.microservicio_carrito.domain.spi;

import com.emazon.microservicio_carrito.domain.model.Supply;

public interface ITransactionPersistencePort {
    Supply verifySupply(Long productId);
}