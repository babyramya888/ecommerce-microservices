package com.ecommerce.order.client;

public class NotificationEventRequest {
    public String orderId;
    public String customerEmail;
    public String customerName;
    public double totalAmount;

    public NotificationEventRequest() {}

    public static NotificationEventRequest fromOrderEvent(com.ecommerce.order.event.OrderPlacedEvent event) {
        NotificationEventRequest request = new NotificationEventRequest();
        request.orderId = event.orderId;
        request.customerEmail = event.customerEmail;
        request.customerName = event.customerName;
        request.totalAmount = event.totalAmount;
        return request;
    }
}
