package com.emazon.microservicio_carrito.adapters.driving.mapper;

import com.emazon.microservicio_carrito.adapters.driving.dto.response.*;
import com.emazon.microservicio_carrito.domain.model.*;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ICartResponseMapper {
    CartResponse toCartResponse(Cart cart);
    CartDto toCartDto(Cart cart);
    CategoryDto toCategoryDto(Category category);
    BrandDto toBrandDto(Brand brand);
    ProductDto toProductDto(CartProductPage cartProductPage);

    default CustomPage<ProductDto> toPageProductDto(CustomPage<CartProductPage> cartProductPage) {
        List<ProductDto> dtoList = cartProductPage.getContent().stream()
            .map(this::toProductDto)
            .toList();

        CustomPage<ProductDto> productDtoPage = new CustomPage<>();
        productDtoPage.setPageNumber(cartProductPage.getPageNumber());
        productDtoPage.setPageSize(cartProductPage.getPageSize());
        productDtoPage.setTotalElements(cartProductPage.getTotalElements());
        productDtoPage.setTotalPages(cartProductPage.getTotalPages());
        productDtoPage.setContent(dtoList);

        return productDtoPage;
    }
}
