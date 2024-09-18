package com.emazon.microservicio_carrito.domain.api.usecase;

import com.emazon.microservicio_carrito.domain.api.ICartProductServicePort;
import com.emazon.microservicio_carrito.domain.api.ICartServicePort;
import com.emazon.microservicio_carrito.domain.exception.InvalidProductException;
import com.emazon.microservicio_carrito.domain.exception.RemoteServiceException;
import com.emazon.microservicio_carrito.domain.model.*;
import com.emazon.microservicio_carrito.domain.spi.*;
import com.emazon.microservicio_carrito.domain.util.DomainConstants;
import com.emazon.microservicio_carrito.domain.validation.CartProductValidation;
import feign.FeignException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class CartProductUseCase implements ICartProductServicePort {
    private final ICartProductPersistencePort cartProductPersistencePort;
    private final IStockPersistencePort stockPersistencePort;
    private final ITransactionPersistencePort transactionPersistencePort;
    private final ICartServicePort cartServicePort;
    private final IAuthPersistencePort authPersistencePort;
    private final CartProductValidation cartProductValidation;

    public CartProductUseCase(ICartProductPersistencePort cartProductPersistencePort, IStockPersistencePort stockPersistencePort, ITransactionPersistencePort transactionPersistencePort, ICartServicePort cartServicePort, IAuthPersistencePort authPersistencePort, CartProductValidation cartProductValidation) {
        this.cartProductPersistencePort = cartProductPersistencePort;
        this.stockPersistencePort = stockPersistencePort;
        this.transactionPersistencePort = transactionPersistencePort;
        this.cartServicePort = cartServicePort;
        this.authPersistencePort = authPersistencePort;
        this.cartProductValidation = cartProductValidation;
    }

    @Override
    public CartOrSupplyDate saveCartProduct(CartProduct cartProduct) {
        try {
            Long clientId = authPersistencePort.getAuthenticatedUserId();
            Cart clientCart = cartServicePort.getCartByClientId(clientId);
            cartProductValidation.validateCartProduct(cartProduct);

            Product productResponse = stockPersistencePort.verifyProduct(cartProduct.getProductId());

            if (cartProduct.getQuantity() <= productResponse.getQuantity()) {
                if (clientCart == null) {
                    clientCart = createCart(clientId);

                    CartProduct cartProductSaved = saveCartProductInBD(clientCart, cartProduct, productResponse);
                    List<CartProduct> cartProducts = clientCart.getProducts();
                    cartProducts.add(cartProductSaved);

                    clientCart = saveCartInBD(clientCart, cartProduct, productResponse, cartProducts);
                } else {
                    List<Long> cartProductCategoryIds = productResponse.getCategories();
                    List<CartProduct> cartProducts = clientCart.getProducts();
                    AtomicBoolean isSameProduct = new AtomicBoolean(false);

                    clientCart.setTotalQuantity(clientCart.getTotalQuantity() + cartProduct.getQuantity());
                    clientCart.setTotalPrice(clientCart.getTotalPrice().add(productResponse.getPrice().multiply(BigDecimal.valueOf(cartProduct.getQuantity()))));

                    long countProductsInSameCategories = getCountProductsInSameCategories(cartProduct, productResponse, cartProducts, isSameProduct, cartProductCategoryIds);


                    if (countProductsInSameCategories < DomainConstants.MAXIMUM_QUANTITY_OF_PRODUCTS_OF_THE_SAME_CATEGORY) {
                        CartProduct cartProductSaved = saveExistCartProductInBD(clientCart, cartProduct, isSameProduct, productResponse);
                        cartProducts.add(cartProductSaved);

                        clientCart.setProducts(cartProducts);
                        clientCart = cartServicePort.updateCart(clientCart);
                    } else {
                        throw new InvalidProductException(DomainConstants.INVALID_CATEGORY_PRODUCT_EXCEPTION_MESSAGE);
                    }
                }
            } else {
                SupplyDate supplyDate = getNextSupplyDate(cartProduct);
                return new CartOrSupplyDate(supplyDate);
            }

            return new CartOrSupplyDate(clientCart);
        } catch (FeignException fe) {
            String errorMessage = extractMessageFromError(fe.getMessage());
            throw new RemoteServiceException(errorMessage);
        } catch (Exception e) {
            throw e;
        }
    }

    public Cart createCart(Long clientId) {
        return new Cart(
                1L,
                clientId,
                0L,
                new BigDecimal(0),
                LocalDateTime.now(),
                LocalDateTime.now(),
                new ArrayList<>()
        );
    }

    public CartProduct saveCartProductInBD(Cart clientCart, CartProduct cartProduct, Product productResponse) {
        cartProduct.setCartId(clientCart.getCartId());
        cartProduct.setUnitPrice(productResponse.getPrice());
        cartProduct.setTotalPrice(
                productResponse.getPrice().multiply(BigDecimal.valueOf(cartProduct.getQuantity()))
        );

        return cartProductPersistencePort.saveCartProduct(cartProduct);
    }

    public CartProduct saveExistCartProductInBD(Cart clientCart, CartProduct cartProduct, AtomicBoolean isSameProduct, Product productResponse) {
        cartProduct.setCartId(clientCart.getCartId());
        cartProduct.setUnitPrice(productResponse.getPrice());

        if (!isSameProduct.get()) {
            cartProduct.setTotalPrice(
                    productResponse.getPrice().multiply(BigDecimal.valueOf(cartProduct.getQuantity()))
            );
        }
        return cartProductPersistencePort.saveCartProduct(cartProduct);
    }

    public Cart saveCartInBD(Cart clientCart, CartProduct cartProduct, Product productResponse, List<CartProduct> cartProducts) {
        clientCart.setTotalQuantity(cartProduct.getQuantity());
        clientCart.setTotalPrice(productResponse.getPrice());
        clientCart.setProducts(cartProducts);

        return cartServicePort.saveCart(clientCart);
    }

    public long getCountProductsInSameCategories(CartProduct cartProduct, Product productResponse, List<CartProduct> cartProducts, AtomicBoolean isSameProduct, List<Long> cartProductCategoryIds) {
        return cartProducts.stream()
                .filter(p -> {
                    if (p.getProductId().equals(cartProduct.getProductId())) {
                        BigDecimal totalPriceProduct = productResponse.getPrice().multiply(BigDecimal.valueOf(cartProduct.getQuantity()));
                        cartProduct.setCartProductId(p.getCartProductId());
                        cartProduct.setQuantity(cartProduct.getQuantity() + p.getQuantity());
                        cartProduct.setTotalPrice(totalPriceProduct.add(p.getTotalPrice()));
                        isSameProduct.set(true);
                        return false;
                    }
                    List<Long> productCategories = stockPersistencePort.verifyProduct(p.getProductId()).getCategories();
                    return productCategories.stream().anyMatch(cartProductCategoryIds::contains);
                })
                .count();
    }

    public SupplyDate getNextSupplyDate(CartProduct cartProduct) {
        Supply supplyResponse = transactionPersistencePort.verifySupply(cartProduct.getProductId());
        LocalDate nextSupplyDate = LocalDate.now();

        if (supplyResponse == null) {
            nextSupplyDate = nextSupplyDate.plusDays(DomainConstants.DAYS_FOR_SUPPLY);
        } else {
            nextSupplyDate = supplyResponse.getDate().plusDays(DomainConstants.DAYS_FOR_SUPPLY);
        }

        return new SupplyDate(DomainConstants.SUPPLY_DATE_MESSAGE, nextSupplyDate);
    }

    private String extractMessageFromError(String errorResponse) {
        String key = "\"message\":\"";
        int startIndex = errorResponse.indexOf(key);

        if (startIndex == -1) {
            return DomainConstants.UNKNOWN_ERROR_OCCURRED_MESSAGE;
        }

        startIndex += key.length();
        int endIndex = errorResponse.indexOf("\"", startIndex);

        if (endIndex == -1) {
            return DomainConstants.UNKNOWN_ERROR_OCCURRED_MESSAGE;
        }

        return errorResponse.substring(startIndex, endIndex);
    }
}
