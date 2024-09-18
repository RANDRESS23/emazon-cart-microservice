package com.emazon.microservicio_carrito.adapters.driving.mapper;

import com.emazon.microservicio_carrito.adapters.driving.dto.response.SupplyMessageResponse;
import com.emazon.microservicio_carrito.domain.model.SupplyDate;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ISupplyMessageResponseMapper {
    SupplyMessageResponse toSupplyMessageResponse(SupplyDate supplyDate);
}
