package com.emazon.microservicio_carrito.domain.spi;

import com.emazon.microservicio_carrito.domain.model.Sale;

public interface IReportPersistencePort {
    void saveReport(Sale sale);
}
