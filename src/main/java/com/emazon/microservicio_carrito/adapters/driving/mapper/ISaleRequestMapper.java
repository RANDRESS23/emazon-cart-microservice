package com.emazon.microservicio_carrito.adapters.driving.mapper;

import com.emazon.microservicio_carrito.adapters.driving.dto.request.AddSaleRequest;
import com.emazon.microservicio_carrito.adapters.driving.dto.request.ProductSoldDto;
import com.emazon.microservicio_carrito.domain.model.ProductSold;
import com.emazon.microservicio_carrito.domain.model.Sale;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ISaleRequestMapper {
    ProductSoldDto toProductSoldDto(ProductSold productSold);

    default AddSaleRequest saleToAddSaleRequest(Sale sale) {
        List<ProductSoldDto> listOfProductsSoldDto = sale.getProducts().stream()
                .map(this::toProductSoldDto)
                .toList();

        return new AddSaleRequest(
                sale.getClientId(),
                sale.getEmail(),
                sale.getTotalQuantity(),
                sale.getTotalPrice(),
                sale.getDate(),
                listOfProductsSoldDto
        );
    }
}
