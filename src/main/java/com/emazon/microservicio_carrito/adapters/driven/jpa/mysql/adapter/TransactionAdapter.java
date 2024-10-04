package com.emazon.microservicio_carrito.adapters.driven.jpa.mysql.adapter;

import com.emazon.microservicio_carrito.adapters.driving.dto.response.SupplyResponse;
import com.emazon.microservicio_carrito.adapters.driving.mapper.ISaleRequestMapper;
import com.emazon.microservicio_carrito.adapters.driving.mapper.ISupplyResponseMapper;
import com.emazon.microservicio_carrito.configuration.feign.ITransactionFeignClient;
import com.emazon.microservicio_carrito.domain.model.Sale;
import com.emazon.microservicio_carrito.domain.model.Supply;
import com.emazon.microservicio_carrito.domain.spi.ITransactionPersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class TransactionAdapter implements ITransactionPersistencePort {
    private final ITransactionFeignClient transactionFeignClient;
    private final ISupplyResponseMapper supplyResponseMapper;
    private final ISaleRequestMapper saleRequestMapper;

    @Override
    public Supply verifySupply(Long productId) {
        SupplyResponse supplyResponse = transactionFeignClient.getSupplyByProductId(productId);
        return supplyResponseMapper.toDomainModel(supplyResponse);
    }

    @Transactional
    @Override
    public Long saveSale(Sale sale) {
        ResponseEntity<Long> response = transactionFeignClient.addSale(saleRequestMapper.saleToAddSaleRequest(sale));
        return response.getBody();
    }

    @Override
    public void deleteSale(Long saleId) {
        transactionFeignClient.deleteSale(saleId);
    }
}
