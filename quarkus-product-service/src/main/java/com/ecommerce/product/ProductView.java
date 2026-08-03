package com.ecommerce.product;

public class ProductView {
    public String id;
    public String name;
    public double price;
    public int stockQuantity;

    public ProductView() {}

    public ProductView(Product product) {
        this.id = product.id.toString();
        this.name = product.name;
        this.price = product.price;
        this.stockQuantity = product.stockQuantity;
    }
}
