package com.ecommerce.cart;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;

import java.util.ArrayList;
import java.util.List;

@MongoEntity(collection = "carts")
public class Cart extends PanacheMongoEntity {
    public String customerId;
    public List<CartItem> items = new ArrayList<>();

    public Cart() {}

    public Cart(String customerId) {
        this.customerId = customerId;
    }

    public static class CartItem {
        public String productId;
        public String productName;
        public double price;
        public int quantity;

        public CartItem() {}

        public CartItem(String productId, String productName, double price, int quantity) {
            this.productId = productId;
            this.productName = productName;
            this.price = price;
            this.quantity = quantity;
        }
    }
}
