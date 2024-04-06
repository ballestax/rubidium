/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rb;

/**
 *
 * @author ballestax
 */
public class Field {

    public static final int T_NUMERIC = 1;
    public static final int T_TEXT = 2;
    public static final int T_DATE = 3;
    public static final int T_BOOLEAN = 4;

    private int type;
    private String nameField;
    private String nameInDB;
    

    public Field() {

    }

    public Field(int type, String nameField, String nameInDB) {
        this.type = type;
        this.nameField = nameField;
        this.nameInDB = nameInDB;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getNameField() {
        return nameField;
    }

    public void setNameField(String nameField) {
        this.nameField = nameField;
    }

    public String getNameInDB() {
        return nameInDB;
    }

    public void setNameInDB(String nameInDB) {
        this.nameInDB = nameInDB;
    }

    @Override
    public String toString() {
        return nameField;
    }
    
    

}
