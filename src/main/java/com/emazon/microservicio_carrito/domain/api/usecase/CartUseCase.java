package com.emazon.microservicio_carrito.domain.api.usecase;

import com.emazon.microservicio_carrito.domain.api.ICartServicePort;
import com.emazon.microservicio_carrito.domain.exception.*;
import com.emazon.microservicio_carrito.domain.model.*;
import com.emazon.microservicio_carrito.domain.spi.*;
import com.emazon.microservicio_carrito.domain.util.DomainConstants;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartUseCase implements ICartServicePort {
    private final ICartPersistencePort cartPersistencePort;
    private final ICartProductPersistencePort cartProductPersistencePort;
    private final IAuthPersistencePort authPersistencePort;
    private final IStockPersistencePort stockPersistencePort;
    private final ITransactionPersistencePort transactionPersistencePort;
    private final IReportPersistencePort reportPersistencePort;

    public CartUseCase(ICartPersistencePort cartPersistencePort, ICartProductPersistencePort cartProductPersistencePort, IAuthPersistencePort authPersistencePort, IStockPersistencePort stockPersistencePort, ITransactionPersistencePort transactionPersistencePort, IReportPersistencePort reportPersistencePort) {
        this.cartPersistencePort = cartPersistencePort;
        this.cartProductPersistencePort = cartProductPersistencePort;
        this.authPersistencePort = authPersistencePort;
        this.stockPersistencePort = stockPersistencePort;
        this.transactionPersistencePort = transactionPersistencePort;
        this.reportPersistencePort = reportPersistencePort;
    }

    @Override
    public Cart saveCart(Cart cart) {
        if (cartPersistencePort.getCartByClientId(cart.getClientId()).isPresent()) {
            throw new AlreadyExistsFieldException(DomainConstants.CART_ALREADY_EXISTS_MESSAGE);
        }

        Long clientId = authPersistencePort.getAuthenticatedUserId();

        cart.setClientId(clientId);
        cart.setCreatedAt(LocalDateTime.now());
        cart.setUpdatedAt(LocalDateTime.now());

        Cart cartSaved = cartPersistencePort.saveCart(cart);
        List<CartProduct> products = cartProductPersistencePort.getAllProducts(cartSaved.getCartId());
        cartSaved.setProducts(products);

        return cartSaved;
    }

    @Override
    public Cart updateCart(Cart cart) {
        cart.setUpdatedAt(LocalDateTime.now());

        Cart cartUpdated = cartPersistencePort.saveCart(cart);
        List<CartProduct> products = cartProductPersistencePort.getAllProducts(cartUpdated.getCartId());
        cartUpdated.setProducts(products);

        return cartUpdated;
    }

    @Override
    public Cart getCartByClientId() {
        Long clientId = authPersistencePort.getAuthenticatedUserId();

        Cart cart = cartPersistencePort.getCartByClientId(clientId)
                .orElse(null);

        if (cart != null) {
            List<CartProduct> products = cartProductPersistencePort.getAllProducts(cart.getCartId());
            cart.setProducts(products);
        }

        return cart;
    }

    @Override
    public void buyCartProducts() {
        Map<Long, Long> originalQuantities = new HashMap<>();
        Sale saleSaved = null;
        Long saleId = null;

        try {
            Cart clientCart = getCartByClientId();

            if (clientCart == null) throw new NotFoundException(DomainConstants.CART_NOT_FOUND);

            List<ProductSold> productsSold = getProductsSold(clientCart, originalQuantities);

            cartProductPersistencePort.removeAllCartProducts(clientCart.getCartId());
            saleSaved = buildSale(clientCart, productsSold);

            saleId = transactionPersistencePort.saveSale(saleSaved);
            reportPersistencePort.saveReport(saleSaved);
            setCartQuantityAndPriceToZero(clientCart);
        } catch (Exception exception) {
            originalQuantities.forEach((productId, originalQuantity) -> stockPersistencePort.updateProductQuantity(productId, originalQuantity, true));

            if (saleSaved != null) {
                transactionPersistencePort.deleteSale(saleId);
            }

            String errorMessage = extractMessageFromError(exception.getMessage());
            throw new RemoteServiceException(errorMessage);
        }
    }

    private List<ProductSold> getProductsSold(Cart clientCart, Map<Long, Long> originalQuantities) {
        List<CartProduct> cartProducts = clientCart.getProducts();
        List<ProductSold> productsSold = new ArrayList<>();

        if (cartProducts.size() == DomainConstants.ZERO_CONSTANT) {
            throw new EmptyCartException(DomainConstants.EMPTY_CART_MESSAGE);
        }

        cartProducts.forEach(cartProduct -> {
            Product product = stockPersistencePort.verifyProduct(cartProduct.getProductId());

            if (cartProduct.getQuantity() > product.getQuantity()) {
                throw new InvalidProductQuantityException(DomainConstants.INVALID_BUY_PRODUCT_QUANTITY_MESSAGE);
            }

            originalQuantities.put(cartProduct.getProductId(), cartProduct.getQuantity());

            insertProductSoldIntoList(productsSold, cartProduct);
            stockPersistencePort.updateProductQuantity(product.getProductId(), cartProduct.getQuantity(), false);
        });

        return productsSold;
    }

    private void setCartQuantityAndPriceToZero(Cart clientCart) {
        clientCart.setTotalQuantity(DomainConstants.QUANTITY_ZERO);
        clientCart.setTotalPrice(DomainConstants.TOTAL_PRICE_ZERO);
        clientCart.setUpdatedAt(LocalDateTime.now());

        cartPersistencePort.saveCart(clientCart);
    }

    private Sale buildSale(Cart clientCart, List<ProductSold> productsSold) {
        Sale sale = new Sale();
        String clientEmail = authPersistencePort.getAuthenticatedUserEmail();

        sale.setClientId(clientCart.getClientId());
        sale.setEmail(clientEmail);
        sale.setTotalQuantity(clientCart.getTotalQuantity());
        sale.setTotalPrice(clientCart.getTotalPrice());
        sale.setDate(LocalDateTime.now());
        sale.setProducts(productsSold);

        return sale;
    }

    private void insertProductSoldIntoList(List<ProductSold> productsSold, CartProduct cartProduct) {
        ProductSold productSold = new ProductSold();
        productSold.setProductId(cartProduct.getProductId());
        productSold.setName(cartProduct.getName());
        productSold.setQuantity(cartProduct.getQuantity());
        productSold.setUnitPrice(cartProduct.getUnitPrice());
        productSold.setTotalPrice(cartProduct.getTotalPrice());

        productsSold.add(productSold);
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
}
