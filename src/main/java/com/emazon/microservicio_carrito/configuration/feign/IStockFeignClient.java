package com.emazon.microservicio_carrito.configuration.feign;

import com.emazon.microservicio_carrito.adapters.driving.dto.request.UpdateProductQuantityRequest;
import com.emazon.microservicio_carrito.adapters.driving.dto.response.ProductResponse;
import com.emazon.microservicio_carrito.configuration.Constants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = Constants.STOCK_SERVICE_NAME, url = Constants.STOCK_SERVICE_URL, configuration = FeignClientInterceptor.class)
public interface IStockFeignClient {
    @GetMapping("/id/{productId}")
    ProductResponse getProductById(@PathVariable Long productId);

    @PatchMapping("/update-quantity")
    ResponseEntity<Void> updateProductQuantity(@RequestBody UpdateProductQuantityRequest request);
}
