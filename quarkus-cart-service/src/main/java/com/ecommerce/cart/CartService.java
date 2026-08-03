package com.ecommerce.cart;

import com.ecommerce.cart.client.ProductServiceClient;
import com.ecommerce.cart.client.ProductView;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.Optional;

@ApplicationScoped
public class CartService {
    @Inject
    @RestClient
    ProductServiceClient productServiceClient;

    public Cart getOrCreateCart(String customerId) {
        Optional<Cart> existing = Cart.<Cart>find("customerId", customerId).firstResultOptional();
        if (existing.isPresent()) {
            return existing.get();
        }
        Cart cart = new Cart(customerId);
        cart.persist();
        return cart;
    }

    public Cart addItem(String customerId, AddItemRequest request, String authorizationHeader) {
        ProductView product = fetchProductWithCircuitBreaker(request.productId, authorizationHeader);
        if ("UNAVAILABLE".equals(product.name)) {
            throw new WebApplicationException("Product service is currently unavailable. Please try again shortly.", Response.Status.SERVICE_UNAVAILABLE);
        }
        if (product.stockQuantity < request.quantity) {
            throw new WebApplicationException("Insufficient stock for " + product.name, Response.Status.CONFLICT);
        }

        Cart cart = getOrCreateCart(customerId);
        Optional<Cart.CartItem> existingItem = cart.items.stream().filter(item -> item.productId.equals(request.productId)).findFirst();
        if (existingItem.isPresent()) {
            existingItem.get().quantity += request.quantity;
        } else {
            cart.items.add(new Cart.CartItem(product.id, product.name, product.price, request.quantity));
        }
        cart.update();
        return cart;
    }

    @Retry(maxRetries = 2, delay = 200)
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.5, delay = 10000, successThreshold = 2)
    @Fallback(fallbackMethod = "productFallback")
    public ProductView fetchProductWithCircuitBreaker(String productId, String authorizationHeader) {
        try {
            return productServiceClient.getProduct(productId, authorizationHeader);
        } catch (WebApplicationException e) {
            if (e.getResponse().getStatus() == 404) {
                throw new WebApplicationException("Product not found: " + productId, Response.Status.NOT_FOUND);
            }
            throw new WebApplicationException("Product service error", Response.Status.BAD_GATEWAY);
        }
    }

    public ProductView productFallback(String productId, String authorizationHeader) {
        ProductView fallback = new ProductView();
        fallback.id = productId;
        fallback.name = "UNAVAILABLE";
        fallback.price = 0.0;
        fallback.stockQuantity = 0;
        return fallback;
    }

    public Cart removeItem(String customerId, String productId) {
        Cart cart = getOrCreateCart(customerId);
        cart.items.removeIf(item -> item.productId.equals(productId));
        cart.update();
        return cart;
    }

    public void clearCart(String customerId) {
        Cart cart = getOrCreateCart(customerId);
        cart.items.clear();
        cart.update();
    }
}
