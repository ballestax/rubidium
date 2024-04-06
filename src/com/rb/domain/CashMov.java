package com.rb.domain;

import java.util.Date;

/**
 *
 * @author lrod
 */
public class CashMov {

    private long id;
    private int type;
    private Date date;
    private long idCycle;
    private String description;
    private long idCategory;

    public static final int INCOME = 1;
    public static final int EXPENSE = 2;

    public CashMov() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getIdCycle() {
        return idCycle;
    }

    public void setIdCycle(long idCycle) {
        this.idCycle = idCycle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getIdCategory() {
        return idCategory;
    }

    public void setIdCategory(long idCategory) {
        this.idCategory = idCategory;
    }

    public static class Category {

        private long id;
        private String name;

        public Category(String name) {
            this.name = name;
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

        @Override
        public String toString() {
            return name.toUpperCase();
        }

    }

}
