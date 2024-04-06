/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rb.domain;

/**
 *
 * @author LuisR
 */
public class Location {

    public int id;
    public String name;
    public String address;
    public boolean salePoint;

    public Location() {
    }

    public Location(int id, String nombre, String direccion) {
        this.id = id;
        this.name = nombre;
        this.address = direccion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isSalePoint() {
        return salePoint;
    }

    public void setSalePoint(boolean salePoint) {
        this.salePoint = salePoint;
    }

    @Override
    public String toString() {
        return name;
    }

}
