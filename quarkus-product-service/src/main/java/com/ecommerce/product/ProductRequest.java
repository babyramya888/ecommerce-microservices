package com.ecommerce.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public class ProductRequest {
    @NotBlank(message = "Name is required")
    public String name;

    public String description;

    @Positive(message = "Price must be greater than 0")
    public double price;

    @PositiveOrZero(message = "Stock quantity cannot be negative")
    public int stockQuantity;

    public String category;

    public ProductRequest() {}
}
