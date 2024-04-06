/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rb.domain;

import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author ballestax
 */
public class Backup {

    private String name;
    private Date creationDate;
    private String path;
    private long size;
    private ArrayList<String> querys;

    public Backup() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public ArrayList<String> getQuerys() {
        return querys;
    }

    public void setQuerys(ArrayList<String> querys) {
        this.querys = querys;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

}
