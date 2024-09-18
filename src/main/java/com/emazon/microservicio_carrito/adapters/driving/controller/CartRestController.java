package com.emazon.microservicio_carrito.adapters.driving.controller;

import com.emazon.microservicio_carrito.adapters.driving.dto.request.AddProductToCart;
import com.emazon.microservicio_carrito.adapters.driving.dto.response.CartOrSupplyResponse;
import com.emazon.microservicio_carrito.adapters.driving.dto.response.CartResponse;
import com.emazon.microservicio_carrito.adapters.driving.dto.response.SupplyMessageResponse;
import com.emazon.microservicio_carrito.adapters.driving.mapper.ICartProductRequestMapper;
import com.emazon.microservicio_carrito.adapters.driving.mapper.ICartResponseMapper;
import com.emazon.microservicio_carrito.adapters.driving.mapper.ISupplyMessageResponseMapper;
import com.emazon.microservicio_carrito.adapters.driving.util.DrivingConstants;
import com.emazon.microservicio_carrito.domain.api.ICartProductServicePort;
import com.emazon.microservicio_carrito.domain.model.CartOrSupplyDate;
import com.emazon.microservicio_carrito.domain.model.CartProduct;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
@Tag(name = DrivingConstants.TAG_CART_NAME, description = DrivingConstants.TAG_CART_DESCRIPTION)
public class CartRestController {
    private final ICartProductServicePort cartProductServicePort;
    private final ICartProductRequestMapper cartProductRequestMapper;
    private final ICartResponseMapper cartResponseMapper;
    private final ISupplyMessageResponseMapper supplyMessageResponseMapper;

    @Operation(summary = DrivingConstants.SAVE_CART_PRODUCT_SUMMARY, description = DrivingConstants.SAVE_CART_PRODUCT_DESCRIPTION)
    @ApiResponses(value = {
            @ApiResponse(responseCode = DrivingConstants.RESPONSE_CODE_201, description = DrivingConstants.SAVE_CART_PRODUCT_RESPONSE_201_DESCRIPTION),
            @ApiResponse(responseCode = DrivingConstants.RESPONSE_CODE_400, description = DrivingConstants.SAVE_CART_PRODUCT_RESPONSE_400_DESCRIPTION, content = @Content),
            @ApiResponse(responseCode = DrivingConstants.RESPONSE_CODE_503, description = DrivingConstants.SAVE_CART_PRODUCT_RESPONSE_503_DESCRIPTION, content = @Content)
    })
    @PreAuthorize(DrivingConstants.HAS_ROLE_CLIENT)
    @PostMapping
    public ResponseEntity<CartOrSupplyResponse> addProductToCart(@Valid @RequestBody AddProductToCart request) {
        CartProduct product = cartProductRequestMapper.addRequestToCartProduct(request);
        CartOrSupplyDate cartUpdated = cartProductServicePort.saveCartProduct(product);

        if (cartUpdated.getCart() != null) {
            CartResponse response = cartResponseMapper.toCartResponse(cartUpdated.getCart());

            return new ResponseEntity<>(new CartOrSupplyResponse(response), HttpStatus.CREATED);
        } else if (cartUpdated.getSupplyDate() != null) {
            SupplyMessageResponse supplyMessageResponse = supplyMessageResponseMapper.toSupplyMessageResponse(cartUpdated.getSupplyDate());

            return new ResponseEntity<>(new CartOrSupplyResponse(supplyMessageResponse), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
