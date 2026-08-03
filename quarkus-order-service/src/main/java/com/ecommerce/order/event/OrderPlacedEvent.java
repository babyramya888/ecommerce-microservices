package com.ecommerce.order.event;

import com.ecommerce.order.Order;

import java.time.Instant;

public class OrderPlacedEvent {
    public String orderId;
    public String customerEmail;
    public String customerName;
    public String customerPhone;
    public double totalAmount;
    public String status;
    public Instant createdAt;

    public OrderPlacedEvent() {}

    public static OrderPlacedEvent from(Order order, String customerEmail, String customerName, String customerPhone) {
        OrderPlacedEvent event = new OrderPlacedEvent();
        event.orderId = order.id != null ? order.id.toString() : "";
        event.customerEmail = customerEmail;
        event.customerName = customerName;
        event.customerPhone = customerPhone;
        event.totalAmount = order.totalAmount;
        event.status = order.status;
        event.createdAt = order.createdAt;
        return event;
    }
}
