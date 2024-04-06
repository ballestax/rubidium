/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rb.domain;

import java.util.Date;

/**
 *
 * @author LuisR
 */
public class Conciliacion {

    private int id;
    private String codigo;
    private Date fecha;
    private long idItem;
    private double existencias;
    private double conciliacion;
    private int locacion;
    private String nota;
    private Date updateTime;
    private String user;

    public Conciliacion() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public long getIdItem() {
        return idItem;
    }

    public void setIdItem(long idItem) {
        this.idItem = idItem;
    }

    public double getExistencias() {
        return existencias;
    }

    public void setExistencias(double existencias) {
        this.existencias = existencias;
    }

    public double getConciliacion() {
        return conciliacion;
    }

    public void setConciliacion(double conciliacion) {
        this.conciliacion = conciliacion;
    }

    public int getLocacion() {
        return locacion;
    }

    public void setLocacion(int locacion) {
        this.locacion = locacion;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

}
