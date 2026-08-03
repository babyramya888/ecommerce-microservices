package com.ecommerce.order;

import com.ecommerce.order.client.CartServiceClient;
import com.ecommerce.order.client.CartView;
import com.ecommerce.order.client.NotificationEventRequest;
import com.ecommerce.order.client.NotificationServiceClient;
import com.ecommerce.order.client.ProductServiceClient;
import com.ecommerce.order.event.OrderPlacedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@ApplicationScoped
public class OrderService {
    @Inject @RestClient CartServiceClient cartServiceClient;
    @Inject @RestClient ProductServiceClient productServiceClient;
    @Inject @RestClient NotificationServiceClient notificationServiceClient;
    @Inject ObjectMapper objectMapper;
    @Inject @Channel("order-placed") Emitter<OrderPlacedEvent> orderPlacedEventEmitter;

    public Order placeOrder(String customerId, String authorizationHeader, JsonWebToken jwt) {
        CartView cart = fetchCart(authorizationHeader);
        if (cart.items.isEmpty()) {
            throw new WebApplicationException("Cannot place an order with an empty cart", Response.Status.BAD_REQUEST);
        }

        List<Order.OrderItem> orderItems = new ArrayList<>();
        double total = 0;
        List<ReducedItem> reducedItems = new ArrayList<>();

        for (CartView.CartItemView cartItem : cart.items) {
            try {
                productServiceClient.reduceStock(cartItem.productId, cartItem.quantity, authorizationHeader);
                reducedItems.add(new ReducedItem(cartItem.productId, cartItem.quantity));
            } catch (WebApplicationException e) {
                compensateStockReductions(reducedItems, authorizationHeader);
                int status = e.getResponse().getStatus();
                if (status == 409) {
                    throw new WebApplicationException("Insufficient stock for " + cartItem.productName, Response.Status.CONFLICT);
                }
                throw new WebApplicationException("Could not reserve stock for " + cartItem.productName, Response.Status.BAD_GATEWAY);
            }

            orderItems.add(new Order.OrderItem(cartItem.productId, cartItem.productName, cartItem.quantity, cartItem.price));
            total += cartItem.price * cartItem.quantity;
        }

        String customerEmail = jwt.getClaim("email");
        String customerName = jwt.getClaim("name");
        String customerPhone = jwt.getClaim("phone") != null ? jwt.getClaim("phone") : "";

        Order order;
        try {
            order = new Order(customerId, customerEmail, customerName, customerPhone, orderItems, total);
            order.persist();
        } catch (Exception e) {
            compensateStockReductions(reducedItems, authorizationHeader);
            throw new WebApplicationException("Failed to save order. Stock has been restored.", Response.Status.INTERNAL_SERVER_ERROR);
        }

        try {
            cartServiceClient.clearCart(authorizationHeader);
        } catch (WebApplicationException ignored) {}

        saveToOutbox(order);
        triggerNotificationAsync(order);
        return order;
    }

    private void triggerNotificationAsync(Order order) {
        CompletableFuture.runAsync(() -> {
            try {
                OrderPlacedEvent event = OrderPlacedEvent.from(order, order.customerEmail, order.customerName, order.customerPhone);
                NotificationEventRequest request = NotificationEventRequest.fromOrderEvent(event);
                notificationServiceClient.sendOrderEvent(request);
                System.out.println("[OrderService][ASYNC] Notification dispatched for order " + order.id);
            } catch (Exception e) {
                System.err.println("[OrderService][ASYNC] FAILED to dispatch notification for order " + order.id + ": " + e.getMessage());
            }
        });
    }

    private void saveToOutbox(Order order) {
        try {
            OrderPlacedEvent event = OrderPlacedEvent.from(order, order.customerEmail, order.customerName, order.customerPhone);
            String payload = objectMapper.writeValueAsString(event);
            OutboxEvent outboxEvent = new OutboxEvent(order.id.toString(), "Order", "OrderPlaced", payload);
            outboxEvent.persist();
        } catch (Exception e) {
            System.err.println("[OrderService][OUTBOX] FAILED to save OutboxEvent: " + e.getMessage());
        }
    }

    private void compensateStockReductions(List<ReducedItem> reducedItems, String authorizationHeader) {
        for (ReducedItem item : reducedItems) {
            try {
                productServiceClient.restoreStock(item.productId, item.quantity, authorizationHeader);
            } catch (Exception e) {
                System.err.println("[OrderService][SAGA-COMPENSATE] FAILED to restore stock for " + item.productId + ": " + e.getMessage());
            }
        }
    }

    public Order findOrThrow(String id) {
        if (!ObjectId.isValid(id)) {
            throw new WebApplicationException("Invalid order id", Response.Status.BAD_REQUEST);
        }
        Order order = Order.findById(new ObjectId(id));
        if (order == null) {
            throw new WebApplicationException("Order not found", Response.Status.NOT_FOUND);
        }
        return order;
    }

    public List<Order> findByCustomer(String customerId) {
        return Order.list("customerId", customerId);
    }

    private CartView fetchCart(String authorizationHeader) {
        try {
            return cartServiceClient.getMyCart(authorizationHeader);
        } catch (WebApplicationException e) {
            throw new WebApplicationException("Could not retrieve cart from Cart Service", Response.Status.BAD_GATEWAY);
        }
    }

    private static class ReducedItem {
        String productId;
        int quantity;
        ReducedItem(String productId, int quantity) { this.productId = productId; this.quantity = quantity; }
    }
}
