/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bacon.domain;

import java.util.List;
import java.util.Objects;

/**
 *
 * @author lrod
 */
public class Product {

    private String name;
    private double price;
    private String image;
    private String description;
    private List<Ingredient> ingredients;

    public Product(String name, double price, String image) {
        this.name = name;
        this.price = price;
        this.image = image;
    }

    public Product() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public List getIngredients() {
        return ingredients;
    }

    public void addIngredients(Ingredient ingredient) {
        ingredients.add(ingredient);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Product)) {
            return false;
        }
        Product prod = (Product) obj;
        if (!name.equals(prod.getName())) {
            return false;
        }   
//        if (!nombre.equals(prod.getNombre())) {
//            return false;
//        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + Objects.hashCode(this.name);
        return hash;
    }

}
