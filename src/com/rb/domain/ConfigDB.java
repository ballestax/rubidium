/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rb.domain;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 *
 * @author ballestax
 */
public class ConfigDB {

    private int id;
    private String clave;
    private String tipo;
    private String valor;
    private String user;
    private String device;
    public static final String INTEGER = "INTEGER";
    public static final String STRING = "STRING";
    public static final String DOUBLE = "DOUBLE";
    public static final String BOOLEAN = "BOOLEAN";
    public static final String DATE = "DATE";
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public ConfigDB() {
        this("", "", "");
    }

    public ConfigDB(String clave, String tipo, String valor) {
        this.clave = clave;
        this.tipo = tipo;
        this.valor = valor;
    }

    public ConfigDB(String clave, String tipo, String valor, String user, String device) {
        this.clave = clave;
        this.tipo = tipo;
        this.valor = valor;
        this.user = user;
        this.device = device;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getDevice() {
        return device;
    }

    public java.util.Date castValorToDate() throws Exception {
        if (!DATE.equals(tipo)) {
            throw new Exception("Tipo no valido");
        }
        try {
            return DATE_FORMAT.parse(valor);
        } catch (ParseException ex) {
            return null;
        }
    }

    public final Object castValor() {
        switch (tipo) {
            case INTEGER:
                try {
                return Integer.parseInt(valor);
            } catch (Exception e) {
                return null;
            }
            case DATE:
                try {
                return DATE_FORMAT.parse(valor);
            } catch (Exception e) {
                System.err.println("CastValor:" + e.getMessage());
                return null;
            }
            case BOOLEAN:
                try {
                return Boolean.valueOf(valor);
            } catch (Exception e) {
                return null;
            }
            case DOUBLE:
                try {
                return Double.valueOf(valor);
            } catch (Exception e) {
                return null;
            }
            default:
                throw new AssertionError();
        }
    }

    @Override
    public String toString() {
        return clave + "(" + tipo + ")->" + valor + "[" + user + "/" + device + "]";
    }

}
