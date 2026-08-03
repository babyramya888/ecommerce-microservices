package com.ecommerce.order;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;

import java.time.Instant;
import java.util.List;

@MongoEntity(collection = "orders")
public class Order extends PanacheMongoEntity {
    public String customerId;
    public String customerEmail;
    public String customerName;
    public String customerPhone;
    public List<OrderItem> items;
    public double totalAmount;
    public String status;
    public Instant createdAt;

    public Order() {}

    public Order(String customerId, String customerEmail, String customerName, String customerPhone, List<OrderItem> items, double totalAmount) {
        this.customerId = customerId;
        this.customerEmail = customerEmail;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.items = items;
        this.totalAmount = totalAmount;
        this.status = "PLACED";
        this.createdAt = Instant.now();
    }

    public static class OrderItem {
        public String productId;
        public String productName;
        public int quantity;
        public double price;

        public OrderItem() {}

        public OrderItem(String productId, String productName, int quantity, double price) {
            this.productId = productId;
            this.productName = productName;
            this.quantity = quantity;
            this.price = price;
        }
    }
}
