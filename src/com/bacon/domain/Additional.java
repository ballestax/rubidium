/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bacon.domain;

import java.util.Objects;

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

class AdditionalPed {

    Additional additional;
    int cantidad;

    public AdditionalPed(Additional add, int cantidad) {
        this.additional = add;
        this.cantidad = cantidad;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AdditionalPed)) {
            return false;
        }
        AdditionalPed addP = (AdditionalPed) obj;
        if (!additional.equals(addP.additional)) {
            return false;
        }
        if (cantidad != (addP.cantidad)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + Objects.hashCode(this.additional);
        hash = 71 * hash + this.cantidad;
        return hash;
    }

}
