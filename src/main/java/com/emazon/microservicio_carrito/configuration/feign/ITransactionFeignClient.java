package com.emazon.microservicio_carrito.configuration.feign;

import com.emazon.microservicio_carrito.adapters.driving.dto.request.AddSaleRequest;
import com.emazon.microservicio_carrito.adapters.driving.dto.response.SupplyResponse;
import com.emazon.microservicio_carrito.configuration.Constants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = Constants.TRANSACTION_SERVICE_NAME, url = Constants.TRANSACTION_SERVICE_URL, configuration = FeignClientInterceptor.class)
public interface ITransactionFeignClient {
    @GetMapping("/supply/product/{productId}")
    SupplyResponse getSupplyByProductId(@PathVariable Long productId);

    @PostMapping("/sale")
    ResponseEntity<Long> addSale(@RequestBody AddSaleRequest request);

    @DeleteMapping("/sale")
    ResponseEntity<HttpStatus> deleteSale(@RequestBody Long saleId);
}
