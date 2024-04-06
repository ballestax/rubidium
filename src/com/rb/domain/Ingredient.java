package com.rb.domain;

import java.util.Objects;

/**
 *
 * @author lrod
 */
public class Ingredient {

    protected long id;

    protected String code;
    protected String name;
    protected String measure;
    protected int quantity;
    protected boolean opcional;

    public Ingredient() {
    }

    public Ingredient(long id) {
        this.id = id;
    }

    public Ingredient(String code, String nombre) {
        this.code = code;
        this.name = nombre;
    }

    public Ingredient(long id, String code, String name, String medida) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.measure = medida;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String medida) {
        this.measure = medida;
    }

    public void setQuantity(int cantidad) {
        this.quantity = cantidad;
    }

    public int getQuantity() {
        return quantity;
    }

    public boolean isOpcional() {
        return opcional;
    }

    public void setOpcional(boolean opcional) {
        this.opcional = opcional;
    }

    @Override
    public String toString() {
        return "[" + code + "] " + name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Ingredient)) {
            return false;
        }
        Ingredient ing = (Ingredient) obj;
        return name.equalsIgnoreCase(ing.getName());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.name);
        return hash;
    }
    
    

}
