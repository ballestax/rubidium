/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rb.domain;

/**
 *
 * @author lrod
 */
public class Additional extends Ingredient {

    private double precio;

    public Additional() {
    }

    public Additional(String codigo, String nombre, double precio) {
        super(codigo, nombre);
        this.precio = precio;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    @Override
    public String toString() {
        return "[" + code + "] " + name + "(" + precio + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Additional)) {
            return false;
        }
        Additional add = (Additional) obj;
        return name.equalsIgnoreCase(add.getName());
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + (int) (Double.doubleToLongBits(this.precio) ^ (Double.doubleToLongBits(this.precio) >>> 32));
        return hash;
    }

}

