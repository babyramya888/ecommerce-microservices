package com.ecommerce.order.client;

import java.util.ArrayList;
import java.util.List;

public class CartView {
    public String id;
    public String customerId;
    public List<CartItemView> items = new ArrayList<>();

    public CartView() {}

    public static class CartItemView {
        public String productId;
        public String productName;
        public double price;
        public int quantity;

        public CartItemView() {}
    }
}
