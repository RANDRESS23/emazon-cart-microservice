package com.emazon.microservicio_carrito.adapters.driven.jpa.mysql.adapter;

import com.emazon.microservicio_carrito.adapters.driving.mapper.IReportRequestMapper;
import com.emazon.microservicio_carrito.configuration.feign.IReportFeignClient;
import com.emazon.microservicio_carrito.domain.model.Sale;
import com.emazon.microservicio_carrito.domain.spi.IReportPersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class ReportAdapter implements IReportPersistencePort {
    private final IReportFeignClient reportFeignClient;
    private final IReportRequestMapper reportRequestMapper;

    @Transactional
    @Override
    public void saveReport(Sale sale) {
        reportFeignClient.addReport(reportRequestMapper.reportToAddReportRequest(sale));
    }
}
