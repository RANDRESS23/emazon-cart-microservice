package com.emazon.microservicio_carrito.adapters.driven.jpa.mysql.adapter;

import com.emazon.microservicio_carrito.adapters.driving.dto.response.ProductResponse;
import com.emazon.microservicio_carrito.adapters.driving.mapper.IProductResponseMapper;
import com.emazon.microservicio_carrito.configuration.feign.IStockFeignClient;
import com.emazon.microservicio_carrito.domain.model.Product;
import com.emazon.microservicio_carrito.domain.spi.IStockPersistencePort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StockAdapter implements IStockPersistencePort {
    private final IStockFeignClient stockFeignClient;
    private final IProductResponseMapper productResponseMapper;

    @Override
    public Product verifyProduct(Long productId) {
        ProductResponse productResponse = stockFeignClient.getProductById(productId);
        return productResponseMapper.toDomainModel(productResponse);
    }
}
