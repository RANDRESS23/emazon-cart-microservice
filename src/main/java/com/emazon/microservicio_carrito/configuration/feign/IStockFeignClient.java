package com.emazon.microservicio_carrito.configuration.feign;

import com.emazon.microservicio_carrito.adapters.driving.dto.response.ProductResponse;
import com.emazon.microservicio_carrito.configuration.Constants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = Constants.STOCK_SERVICE_NAME, url = Constants.STOCK_SERVICE_URL, configuration = FeignClientInterceptor.class)
public interface IStockFeignClient {
    @GetMapping("/id/{productId}")
    ProductResponse getProductById(@PathVariable Long productId);
}
