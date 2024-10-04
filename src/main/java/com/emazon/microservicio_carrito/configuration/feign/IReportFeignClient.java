package com.emazon.microservicio_carrito.configuration.feign;

import com.emazon.microservicio_carrito.adapters.driving.dto.request.AddReportRequest;
import com.emazon.microservicio_carrito.configuration.Constants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = Constants.REPORT_SERVICE_NAME, url = Constants.REPORT_SERVICE_URL, configuration = FeignClientInterceptor.class)
public interface IReportFeignClient {
    @PostMapping
    ResponseEntity<HttpStatus> addReport(@RequestBody AddReportRequest request);
}
