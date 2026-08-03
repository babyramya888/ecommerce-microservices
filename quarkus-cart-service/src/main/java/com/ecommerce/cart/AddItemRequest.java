package com.ecommerce.cart;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class AddItemRequest {
    @NotBlank(message = "productId is required")
    public String productId;

    @Positive(message = "Quantity must be at least 1")
    public int quantity;

    public AddItemRequest() {}
}
