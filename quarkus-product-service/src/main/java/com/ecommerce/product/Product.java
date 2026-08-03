package com.ecommerce.product;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;

@MongoEntity(collection = "products")
public class Product extends PanacheMongoEntity {
    public String name;
    public String description;
    public double price;
    public int stockQuantity;
    public String category;
    public String createdBy;

    public Product() {}

    public Product(String name, String description, double price, int stockQuantity, String category) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.category = category;
    }
}
