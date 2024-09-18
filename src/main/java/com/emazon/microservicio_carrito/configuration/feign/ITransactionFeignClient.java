package com.emazon.microservicio_carrito.configuration.feign;

import com.emazon.microservicio_carrito.adapters.driving.dto.response.SupplyResponse;
import com.emazon.microservicio_carrito.configuration.Constants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = Constants.TRANSACTION_SERVICE_NAME, url = Constants.TRANSACTION_SERVICE_URL, configuration = FeignClientInterceptor.class)
public interface ITransactionFeignClient {
    @GetMapping("/product/{productId}")
    SupplyResponse getSupplyByProductId(@PathVariable Long productId);
}
