package com.emazon.microservicio_carrito.adapters.driving.mapper;

import com.emazon.microservicio_carrito.adapters.driving.dto.request.AddReportRequest;
import com.emazon.microservicio_carrito.adapters.driving.dto.request.ProductSoldDto;
import com.emazon.microservicio_carrito.domain.model.ProductSold;
import com.emazon.microservicio_carrito.domain.model.Sale;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface IReportRequestMapper {
    ProductSoldDto toProductSoldDto(ProductSold productSold);

    default AddReportRequest reportToAddReportRequest(Sale sale) {
        List<ProductSoldDto> listOfProductsSoldDto = sale.getProducts().stream()
                .map(this::toProductSoldDto)
                .toList();

        return new AddReportRequest(
                sale.getClientId(),
                sale.getEmail(),
                sale.getTotalQuantity(),
                sale.getTotalPrice(),
                sale.getDate(),
                listOfProductsSoldDto
        );
    }
}
