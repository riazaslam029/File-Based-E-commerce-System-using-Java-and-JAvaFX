package com.example.javafx.model;

import javafx.scene.image.Image;
import java.io.File;
import java.util.Objects;

/**
 * This class represents a Product in the shopping system.
 * It includes details like name, price, image path, and quantity.
 */
public class Product {
    private String name;
    private double price;
    private String imagePath;
    private int quantity;

    // Constructor with quantity
    public Product(String name, double price, String imagePath, int quantity) {
        this.name = name;
        this.price = price;
        this.imagePath = imagePath;
        this.quantity = quantity;
    }

    // Constructor with default quantity = 1
    public Product(String name, double price, String imagePath) {
        this(name, price, imagePath, 1);
    }

    // Getter and setter methods for name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter and setter methods for price
    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    // Getter and setter methods for image path
    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    /**
     * Returns a JavaFX Image object using the product's image path.
     * Returns null if the image file doesn't exist.
     */

    // Getter and setter for quantity
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }


    /**
     * Two products are considered equal if their names match (case-insensitive).
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        Product product = (Product) o;
        return name.equalsIgnoreCase(product.name);
    }


    // Returns a string representation of the product
    @Override
    public String toString() {
        return name + " ($" + price + ")";
    }
}
