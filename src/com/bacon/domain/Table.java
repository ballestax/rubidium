package com.bacon.domain;

import java.util.Objects;

/**
 *
 * @author lrod
 */
public class Table {

    private int id;
    private String name;
    private int status;
    private int idWaiter;
    private long idOrder;

    public static final int TABLE_ST_LIMPIA = 1;
    public static final int TABLE_ST_OCUPADA = 2;
    public static final int TABLE_ST_RESERVADA = 3;
    public static final int TABLE_ST_PEDIDO_TOMADO = 4;
    public static final int TABLE_ST_PEDIDO_EN_COCINA = 5;
    public static final int TABLE_ST_PEDIDO_SERVIDO = 6;
    public static final int TABLE_ST_PEDIDO_PENDIENTE = 7;

    public Table() {
    }

    public Table(String name, int status) {
        this.name = name;
        this.status = status;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getIdWaiter() {
        return idWaiter;
    }

    public void setIdWaiter(int idWaiter) {
        this.idWaiter = idWaiter;
    }

    public long getIdOrder() {
        return idOrder;
    }

    public void setIdOrder(long idOrder) {
        this.idOrder = idOrder;
    }

    @Override
    public String toString() {
        return name.toUpperCase();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Table)) {
            return false;
        }
        Table table = (Table) obj;
        return name.equalsIgnoreCase(table.getName());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + this.id;
        hash = 97 * hash + Objects.hashCode(this.name);
        return hash;
    }

}
