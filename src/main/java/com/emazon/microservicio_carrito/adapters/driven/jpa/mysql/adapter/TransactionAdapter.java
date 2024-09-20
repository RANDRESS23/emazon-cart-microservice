package com.emazon.microservicio_carrito.adapters.driven.jpa.mysql.adapter;

import com.emazon.microservicio_carrito.adapters.driving.dto.response.SupplyResponse;
import com.emazon.microservicio_carrito.adapters.driving.mapper.ISupplyResponseMapper;
import com.emazon.microservicio_carrito.configuration.feign.ITransactionFeignClient;
import com.emazon.microservicio_carrito.domain.model.Supply;
import com.emazon.microservicio_carrito.domain.spi.ITransactionPersistencePort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TransactionAdapter implements ITransactionPersistencePort {
    private final ITransactionFeignClient transactionFeignClient;
    private final ISupplyResponseMapper supplyResponseMapper;

    @Override
    public Supply verifySupply(Long productId) {
        SupplyResponse supplyResponse = transactionFeignClient.getSupplyByProductId(productId);
        return supplyResponseMapper.toDomainModel(supplyResponse);
    }
}
