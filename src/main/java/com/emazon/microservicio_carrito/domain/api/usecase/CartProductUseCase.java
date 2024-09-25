package com.emazon.microservicio_carrito.domain.api.usecase;

import com.emazon.microservicio_carrito.domain.api.ICartProductServicePort;
import com.emazon.microservicio_carrito.domain.api.ICartServicePort;
import com.emazon.microservicio_carrito.domain.exception.*;
import com.emazon.microservicio_carrito.domain.model.*;
import com.emazon.microservicio_carrito.domain.spi.*;
import com.emazon.microservicio_carrito.domain.util.DomainConstants;
import com.emazon.microservicio_carrito.domain.validation.CartProductValidation;

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
    public Cart saveCartProduct(CartProduct cartProduct) {
        try {
            Long clientId = authPersistencePort.getAuthenticatedUserId();
            Cart clientCart = cartServicePort.getCartByClientId();
            cartProductValidation.validateCartProduct(cartProduct);

            Product productResponse = stockPersistencePort.verifyProduct(cartProduct.getProductId());

            if (cartProduct.getQuantity() <= productResponse.getQuantity()) {
                clientCart = createOrUpdateCart(clientCart, clientId, cartProduct, productResponse);
            } else {
                getNextSupplyDate(cartProduct);
            }

            return clientCart;
        } catch (Exception exception) {
            String errorMessage = extractMessageFromError(exception.getMessage());
            throw new RemoteServiceException(errorMessage);
        }
    }

    @Override
    public Cart removeCartProduct(CartProduct cartProduct) {
        try {
            Cart clientCart = cartServicePort.getCartByClientId();

            if (clientCart == null) {
                throw new NotFoundException(DomainConstants.CART_NOT_FOUND);
            }

            cartProductValidation.validateCartProduct(cartProduct);
            Product productResponse = stockPersistencePort.verifyProduct(cartProduct.getProductId());
            AtomicBoolean isTotalProductQuantity = new AtomicBoolean(false);
            List<CartProduct> newCartProducts = new ArrayList<>();
            List<CartProduct> cartProducts = getCartProducts(cartProduct, clientCart, productResponse, isTotalProductQuantity, newCartProducts);

            if (isTotalProductQuantity.get()) {
                cartProductPersistencePort.removeCartProduct(cartProduct);
                setClientCartValues(clientCart, cartProduct, productResponse, newCartProducts);

                return cartServicePort.updateCart(clientCart);
            }

            cartProductPersistencePort.saveCartProduct(cartProduct);
            setClientCartValues(clientCart, cartProduct, productResponse, cartProducts);

            return cartServicePort.updateCart(clientCart);
        } catch (Exception exception) {
            String errorMessage = extractMessageFromError(exception.getMessage());
            throw new RemoteServiceException(errorMessage);
        }
    }

    @Override
    public CustomPage<CartProductPage> getAllCartProducts(Integer page, Integer size, Boolean ascending, String category, String brand) {
        try {
            Cart clientCart = cartServicePort.getCartByClientId();

            if (clientCart == null) throw new NotFoundException(DomainConstants.CART_NOT_FOUND);

            CustomPage<CartProduct> cartProducts = cartProductPersistencePort.getAllCartProducts(page, size, ascending, clientCart.getCartId());
            List<CartProductPage> listOfProductPage = cartProducts.getContent().stream().map(cartProduct -> {
                Product product = stockPersistencePort.verifyProduct(cartProduct.getProductId());
                return createCartProductPage(cartProduct, product);
            }).toList();

            CustomPage<CartProductPage> customCartProductPage = new CustomPage<>();
            setCustomPageValues(customCartProductPage, cartProducts, listOfProductPage);

            if (!category.equals(DomainConstants.DEFAULT_CATEGORY_PARAM)) {
                filterProductsByCategory(customCartProductPage, category);
            }

            if (!brand.equals(DomainConstants.DEFAULT_BRAND_PARAM)) {
                filterProductsByBrand(customCartProductPage, brand);
            }

            return customCartProductPage;
        } catch (Exception exception) {
            String errorMessage = extractMessageFromError(exception.getMessage());
            throw new RemoteServiceException(errorMessage);
        }
    }

    public void filterProductsByCategory(CustomPage<CartProductPage> customCartProductPage, String category) {
        List<CartProductPage> productsFilteredByCategory = customCartProductPage
            .getContent()
            .stream()
            .filter(cartProductPageItem ->
                cartProductPageItem.getCategories()
                    .stream()
                    .anyMatch(item -> category.equalsIgnoreCase(item.getName()))
            )
            .toList();

        customCartProductPage.setContent(productsFilteredByCategory);
        customCartProductPage.setTotalElements(productsFilteredByCategory.size());
    }

    public void filterProductsByBrand(CustomPage<CartProductPage> customCartProductPage, String brand) {
        List<CartProductPage> productsFilteredByBrand = customCartProductPage
            .getContent()
            .stream()
            .filter(cartProductPageItem -> brand.equalsIgnoreCase(cartProductPageItem.getBrand().getName()))
            .toList();

        customCartProductPage.setContent(productsFilteredByBrand);
        customCartProductPage.setTotalElements(productsFilteredByBrand.size());
    }

    public CartProductPage createCartProductPage(CartProduct cartProduct, Product product) {
        LocalDate nextSupplyDate = null;

        if (product.getQuantity() < cartProduct.getQuantity()) {
            Supply supplyResponse = transactionPersistencePort.verifySupply(cartProduct.getProductId());
            nextSupplyDate = supplyResponse.getDate().plusDays(DomainConstants.DAYS_FOR_SUPPLY);
        }

        return new CartProductPage.CartProductPageBuilder()
            .cartProductId(cartProduct.getCartProductId())
            .productId(product.getProductId())
            .name(product.getName())
            .stockQuantity(product.getQuantity())
            .nextSupplyDate(nextSupplyDate)
            .totalQuantityInCart(cartProduct.getQuantity())
            .unitPrice(cartProduct.getUnitPrice())
            .totalPrice(cartProduct.getTotalPrice())
            .categories(product.getCategories())
            .brand(product.getBrand())
            .build();
    }

    public void setCustomPageValues(CustomPage<CartProductPage> customPage, CustomPage<CartProduct> cartProducts, List<CartProductPage> listOfProductPage) {
        customPage.setPageNumber(cartProducts.getPageNumber());
        customPage.setPageSize(cartProducts.getPageSize());
        customPage.setTotalElements(cartProducts.getTotalElements());
        customPage.setTotalPages(cartProducts.getTotalPages());
        customPage.setContent(listOfProductPage);
    }

    public static List<CartProduct> getCartProducts(CartProduct cartProduct, Cart clientCart, Product productResponse, AtomicBoolean isTotalProductQuantity, List<CartProduct> newCartProducts) {
        List<CartProduct> cartProducts = clientCart.getProducts();
        AtomicBoolean isExistsCartProductInTheCart = new AtomicBoolean(false);

        cartProducts.forEach(focusedCartProduct -> {
            if (focusedCartProduct.getProductId().equals(cartProduct.getProductId())) {
                validateEqualsProduct(cartProduct, focusedCartProduct, isExistsCartProductInTheCart, isTotalProductQuantity, productResponse);
            } else newCartProducts.add(focusedCartProduct);
        });

        if (!isExistsCartProductInTheCart.get()) {
            throw new NotFoundException(DomainConstants.CART_PRODUCT_NOT_FOUND);
        }

        return cartProducts;
    }

    public static void setCartProductValues(CartProduct cartProduct, CartProduct focusedCartProduct) {
        cartProduct.setCartProductId(focusedCartProduct.getCartProductId());
        cartProduct.setCartId(focusedCartProduct.getCartId());
        cartProduct.setName(focusedCartProduct.getName());
        cartProduct.setUnitPrice(focusedCartProduct.getUnitPrice());
        cartProduct.setTotalPrice(focusedCartProduct.getTotalPrice());
        cartProduct.setQuantity(focusedCartProduct.getQuantity());
    }

    public void setClientCartValues(Cart clientCart, CartProduct cartProduct, Product productResponse, List<CartProduct> newCartProducts) {
        clientCart.setTotalQuantity(clientCart.getTotalQuantity() - cartProduct.getQuantity());
        clientCart.setTotalPrice(clientCart.getTotalPrice().subtract(productResponse.getPrice().multiply(BigDecimal.valueOf(cartProduct.getQuantity()))));
        clientCart.setProducts(newCartProducts);
    }

    public static void validateEqualsProduct(CartProduct cartProduct, CartProduct focusedCartProduct, AtomicBoolean isExistsCartProductInTheCart, AtomicBoolean isTotalProductQuantity, Product productResponse) {
        if (cartProduct.getQuantity() > focusedCartProduct.getQuantity()) {
            throw new InvalidProductQuantityException(DomainConstants.INVALID_REMOVE_PRODUCT_QUANTITY_FROM_CART_MESSAGE);
        }

        isExistsCartProductInTheCart.set(true);

        if (cartProduct.getQuantity().equals(focusedCartProduct.getQuantity())) {
            setCartProductValues(cartProduct, focusedCartProduct);

            isTotalProductQuantity.set(true);
        } else {
            focusedCartProduct.setQuantity(focusedCartProduct.getQuantity() - cartProduct.getQuantity());
            focusedCartProduct.setTotalPrice(focusedCartProduct.getTotalPrice().subtract(productResponse.getPrice().multiply(BigDecimal.valueOf(cartProduct.getQuantity()))));

            setCartProductValues(cartProduct, focusedCartProduct);
        }
    }

    public Cart createOrUpdateCart(Cart clientCart, Long clientId, CartProduct cartProduct, Product productResponse) {
        if (clientCart == null) {
            clientCart = createCart(clientId);

            CartProduct cartProductSaved = saveCartProductInBD(clientCart, cartProduct, productResponse);
            List<CartProduct> cartProducts = clientCart.getProducts();
            cartProducts.add(cartProductSaved);

            return saveCartInBD(clientCart, cartProduct, productResponse, cartProducts);
        } else {
            clientCart.setTotalQuantity(clientCart.getTotalQuantity() + cartProduct.getQuantity());
            clientCart.setTotalPrice(clientCart.getTotalPrice().add(productResponse.getPrice().multiply(BigDecimal.valueOf(cartProduct.getQuantity()))));

            return verifyCategoriesQuantity(clientCart, cartProduct, productResponse);
        }
    }

    public CartProduct saveCartProductInBD(Cart clientCart, CartProduct cartProduct, Product productResponse) {
        cartProduct.setCartId(clientCart.getCartId());
        cartProduct.setName(productResponse.getName());
        cartProduct.setUnitPrice(productResponse.getPrice());
        cartProduct.setTotalPrice(
                productResponse.getPrice().multiply(BigDecimal.valueOf(cartProduct.getQuantity()))
        );

        return cartProductPersistencePort.saveCartProduct(cartProduct);
    }

    public CartProduct saveExistCartProductInBD(Cart clientCart, CartProduct cartProduct, AtomicBoolean isSameProduct, Product productResponse) {
        cartProduct.setCartId(clientCart.getCartId());
        cartProduct.setName(productResponse.getName());
        cartProduct.setUnitPrice(productResponse.getPrice());

        if (!isSameProduct.get()) {
            cartProduct.setTotalPrice(
                    productResponse.getPrice().multiply(BigDecimal.valueOf(cartProduct.getQuantity()))
            );
        }

        return cartProductPersistencePort.saveCartProduct(cartProduct);
    }

    public Cart verifyCategoriesQuantity(Cart clientCart, CartProduct cartProduct, Product productResponse) {
        AtomicBoolean isSameProduct = new AtomicBoolean(false);
        List<CartProduct> cartProducts = clientCart.getProducts();
        List<Long> cartProductCategoryIds = productResponse.getCategories().stream().map(Category::getCategoryId).toList();
        long countProductsInSameCategories = getCountProductsInSameCategories(cartProduct, productResponse, cartProducts, isSameProduct, cartProductCategoryIds);

        if (countProductsInSameCategories < DomainConstants.MAXIMUM_QUANTITY_OF_PRODUCTS_OF_THE_SAME_CATEGORY) {
            CartProduct cartProductSaved = saveExistCartProductInBD(clientCart, cartProduct, isSameProduct, productResponse);
            cartProducts.add(cartProductSaved);

            clientCart.setProducts(cartProducts);
            return cartServicePort.updateCart(clientCart);
        } else {
            throw new InvalidProductException(DomainConstants.INVALID_CATEGORY_PRODUCT_EXCEPTION_MESSAGE);
        }
    }

    public Cart saveCartInBD(Cart clientCart, CartProduct cartProduct, Product productResponse, List<CartProduct> cartProducts) {
        clientCart.setTotalQuantity(cartProduct.getQuantity());
        clientCart.setTotalPrice(productResponse.getPrice().multiply(BigDecimal.valueOf(cartProduct.getQuantity())));
        clientCart.setProducts(cartProducts);

        return cartServicePort.saveCart(clientCart);
    }

    public long getCountProductsInSameCategories(CartProduct cartProduct, Product productResponse, List<CartProduct> cartProducts, AtomicBoolean isSameProduct, List<Long> cartProductCategoryIds) {
        return cartProducts.stream()
            .filter(focusedCartProduct -> {
                if (focusedCartProduct.getProductId().equals(cartProduct.getProductId())) {
                    return updateCartProductQuantityAndPrice(focusedCartProduct, cartProduct, productResponse, isSameProduct);
                }

                List<Long> productCategories = stockPersistencePort.verifyProduct(focusedCartProduct.getProductId()).getCategories()
                        .stream().map(Category::getCategoryId)
                        .toList();
                return productCategories.stream().anyMatch(cartProductCategoryIds::contains);
            })
            .count();
    }

    public boolean updateCartProductQuantityAndPrice(CartProduct focusedCartProduct, CartProduct cartProduct, Product productResponse, AtomicBoolean isSameProduct) {
        if ((focusedCartProduct.getQuantity() + cartProduct.getQuantity()) > productResponse.getQuantity()) {
            throw new InvalidProductQuantityException(DomainConstants.INVALID_ADD_PRODUCT_QUANTITY_MESSAGE);
        }

        BigDecimal totalPriceProduct = productResponse.getPrice().multiply(BigDecimal.valueOf(cartProduct.getQuantity()));
        cartProduct.setCartProductId(focusedCartProduct.getCartProductId());
        cartProduct.setQuantity(cartProduct.getQuantity() + focusedCartProduct.getQuantity());
        cartProduct.setTotalPrice(totalPriceProduct.add(focusedCartProduct.getTotalPrice()));
        isSameProduct.set(true);

        return false;
    }

    public void getNextSupplyDate(CartProduct cartProduct) {
        Supply supplyResponse = transactionPersistencePort.verifySupply(cartProduct.getProductId());
        LocalDate nextSupplyDate = LocalDate.now();

        if (supplyResponse == null) {
            nextSupplyDate = nextSupplyDate.plusDays(DomainConstants.DAYS_FOR_SUPPLY);
        } else {
            nextSupplyDate = supplyResponse.getDate().plusDays(DomainConstants.DAYS_FOR_SUPPLY);
        }

        throw new SupplyDateException(DomainConstants.SUPPLY_DATE_MESSAGE, nextSupplyDate);
    }

    private String extractMessageFromError(String errorResponse) {
        String key = DomainConstants.MESSAGE_PREFIX;
        int startIndex = errorResponse.indexOf(key);

        if (startIndex == DomainConstants.MINUS_ONE) {
            return DomainConstants.UNKNOWN_ERROR_OCCURRED_MESSAGE;
        }

        startIndex += key.length();
        int endIndex = errorResponse.indexOf(DomainConstants.QUOTE, startIndex);

        if (endIndex == DomainConstants.MINUS_ONE) {
            return DomainConstants.UNKNOWN_ERROR_OCCURRED_MESSAGE;
        }

        return errorResponse.substring(startIndex, endIndex);
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
}