package com.emazon.microservicio_carrito.adapters.driving.controller;

import com.emazon.microservicio_carrito.adapters.driving.dto.request.AddProductToCart;
import com.emazon.microservicio_carrito.adapters.driving.dto.response.CartDto;
import com.emazon.microservicio_carrito.adapters.driving.dto.response.CartResponse;
import com.emazon.microservicio_carrito.adapters.driving.dto.response.ListCartProducts;
import com.emazon.microservicio_carrito.adapters.driving.mapper.ICartProductRequestMapper;
import com.emazon.microservicio_carrito.adapters.driving.mapper.ICartResponseMapper;
import com.emazon.microservicio_carrito.adapters.driving.util.DrivingConstants;
import com.emazon.microservicio_carrito.domain.api.ICartProductServicePort;
import com.emazon.microservicio_carrito.domain.api.ICartServicePort;
import com.emazon.microservicio_carrito.domain.model.Cart;
import com.emazon.microservicio_carrito.domain.model.CartProduct;
import com.emazon.microservicio_carrito.domain.model.CartProductPage;
import com.emazon.microservicio_carrito.domain.model.CustomPage;
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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
@Tag(name = DrivingConstants.TAG_CART_NAME, description = DrivingConstants.TAG_CART_DESCRIPTION)
public class CartRestController {
    private final ICartProductServicePort cartProductServicePort;
    private final ICartServicePort cartServicePort;
    private final ICartProductRequestMapper cartProductRequestMapper;
    private final ICartResponseMapper cartResponseMapper;

    @Operation(summary = DrivingConstants.SAVE_CART_PRODUCT_SUMMARY, description = DrivingConstants.SAVE_CART_PRODUCT_DESCRIPTION)
    @ApiResponses(value = {
            @ApiResponse(responseCode = DrivingConstants.RESPONSE_CODE_201, description = DrivingConstants.SAVE_CART_PRODUCT_RESPONSE_201_DESCRIPTION),
            @ApiResponse(responseCode = DrivingConstants.RESPONSE_CODE_400, description = DrivingConstants.SAVE_CART_PRODUCT_RESPONSE_400_DESCRIPTION, content = @Content),
            @ApiResponse(responseCode = DrivingConstants.RESPONSE_CODE_503, description = DrivingConstants.SAVE_CART_PRODUCT_RESPONSE_503_DESCRIPTION, content = @Content)
    })
    @PreAuthorize(DrivingConstants.HAS_ROLE_CLIENT)
    @PostMapping
    public ResponseEntity<CartResponse> addProductToCart(@Valid @RequestBody AddProductToCart request) {
        CartProduct product = cartProductRequestMapper.addRequestToCartProduct(request);
        Cart cartUpdated = cartProductServicePort.saveCartProduct(product);
        CartResponse response = cartResponseMapper.toCartResponse(cartUpdated);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = DrivingConstants.REMOVE_CART_PRODUCT_SUMMARY, description = DrivingConstants.REMOVE_CART_PRODUCT_DESCRIPTION)
    @ApiResponses(value = {
            @ApiResponse(responseCode = DrivingConstants.RESPONSE_CODE_201, description = DrivingConstants.REMOVE_CART_PRODUCT_RESPONSE_201_DESCRIPTION),
            @ApiResponse(responseCode = DrivingConstants.RESPONSE_CODE_400, description = DrivingConstants.REMOVE_CART_PRODUCT_RESPONSE_400_DESCRIPTION, content = @Content),
            @ApiResponse(responseCode = DrivingConstants.RESPONSE_CODE_503, description = DrivingConstants.REMOVE_CART_PRODUCT_RESPONSE_503_DESCRIPTION, content = @Content)
    })
    @PreAuthorize(DrivingConstants.HAS_ROLE_CLIENT)
    @DeleteMapping
    public ResponseEntity<CartResponse> removeProductToCart(@Valid @RequestBody AddProductToCart request) {
        CartProduct product = cartProductRequestMapper.addRequestToCartProduct(request);
        Cart cartUpdated = cartProductServicePort.removeCartProduct(product);
        CartResponse response = cartResponseMapper.toCartResponse(cartUpdated);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = DrivingConstants.GET_CART_PRODUCTS_SUMMARY, description = DrivingConstants.GET_CART_PRODUCTS_DESCRIPTION)
    @ApiResponses(value = {
            @ApiResponse(responseCode = DrivingConstants.RESPONSE_CODE_201, description = DrivingConstants.GET_CART_PRODUCTS_RESPONSE_200_DESCRIPTION),
            @ApiResponse(responseCode = DrivingConstants.RESPONSE_CODE_400, description = DrivingConstants.GET_CART_PRODUCTS_RESPONSE_400_DESCRIPTION, content = @Content),
            @ApiResponse(responseCode = DrivingConstants.RESPONSE_CODE_503, description = DrivingConstants.GET_CART_PRODUCTS_RESPONSE_503_DESCRIPTION, content = @Content)
    })
    @PreAuthorize(DrivingConstants.HAS_ROLE_CLIENT)
    @GetMapping
    public ResponseEntity<ListCartProducts> getAllProducts(
            @RequestParam(defaultValue = DrivingConstants.DEFAULT_PAGE_PARAM) int page,
            @RequestParam(defaultValue = DrivingConstants.DEFAULT_SIZE_PARAM) int size,
            @RequestParam(defaultValue = DrivingConstants.DEFAULT_SORT_PARAM) String sortOrder,
            @RequestParam(defaultValue = DrivingConstants.DEFAULT_CATEGORY_PARAM) String category,
            @RequestParam(defaultValue = DrivingConstants.DEFAULT_BRAND_PARAM) String brand) {
        boolean ascending = DrivingConstants.DEFAULT_SORT_PARAM.equalsIgnoreCase(sortOrder);
        CustomPage<CartProductPage> cartProductPage = cartProductServicePort.getAllCartProducts(page, size, ascending, category, brand);
        CartDto cart = cartResponseMapper.toCartDto(cartServicePort.getCartByClientId());
        ListCartProducts responsePage = new ListCartProducts(cart, cartResponseMapper.toPageProductDto(cartProductPage));

        return new ResponseEntity<>(responsePage, HttpStatus.OK);
    }
}
