package com.rb.domain;

public class Station {

    private int id;
    private String name;
    private String color;
    private String printer;
    private int status;

    

    public Station() {
    }


    public Station(final int id, final String name, final String color, final String printer, final int status) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.printer = printer;
        this.status = status;
    }


    public int getId() {
        return id;
    }


    public String getName() {
        return name;
    }


    public String getColor() {
        return color;
    }


    public String getPrinter() {
        return printer;
    }


    public int getStatus() {
        return status;
    }


    public void setId(final int id) {
        this.id = id;
    }


    public void setName(final String name) {
        this.name = name;
    }


    public void setColor(final String color) {
        this.color = color;
    }


    public void setPrinter(final String printer) {
        this.printer = printer;
    }


    public void setStatus(final int status) {
        this.status = status;
    }


    @Override
    public String toString() {
        return "Station [id=" + id + ", name=" + name + ", color=" + color + ", printer=" + printer + ", status="
                + status + "]";
    }
    
    
}
