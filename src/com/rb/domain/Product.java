/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rb.domain;

import java.util.List;
import java.util.Objects;

/**
 *
 * @author lrod
 */
public class Product implements Comparable<Product>{

    private long id;

    private String code;
    private String name;
    private double price;
    private String image;
    private String description;
    private String category;
    private boolean variablePrice;
    private List<Ingredient> ingredients;
    private boolean _enabled;

    public Product(long id) {
        this.id = id;
    }

    public Product(long id, String name, double price, String image) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.image = image;
    }

    public Product() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name.trim();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isVariablePrice() {
        return variablePrice;
    }

    public void setVariablePrice(boolean variablePrice) {
        this.variablePrice = variablePrice;
    }

    public List getIngredients() {
        return ingredients;
    }

    public void addIngredients(Ingredient ingredient) {
        ingredients.add(ingredient);
    }

    public boolean isEnabled() {
        return _enabled;
    }

    public void setEnabled(boolean _enabled) {
        this._enabled = _enabled;
    }
    
    

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        Product prod = (Product) obj;
        if (!name.equalsIgnoreCase(prod.getName())) {
            return false;
        }

        return price == prod.getPrice();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.name);
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.price) ^ (Double.doubleToLongBits(this.price) >>> 32));
        return hash;
    }

    @Override
    public String toString() {
        return name.toUpperCase();
    }

    public String toStringFull() {
        return "Product ["
                + "\nid:" + id
                + "\nname:" + name
                + "\ncode:" + code
                + "\nprice:" + price
                + "\ncategory:" + category
                + "\n]";
    }

    @Override
    public int compareTo(Product o) {
        return Long.compare(id, o.id);
    }

}
