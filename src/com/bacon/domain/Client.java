package com.bacon.domain;

import java.util.List;

/**
 *
 * @author lrod
 */
public class Client {

    private int id;
    private String cellphone;
    private String names;
    private String lastName;
    private List addresses;

    public Client() {
    }

    public Client(int id) {
        this.id = id;
    }

    public Client(String cellphone) {
        this.cellphone = cellphone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    public String getNames() {
        return names;
    }

    public void setNames(String names) {
        this.names = names;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List getAddresses() {
        return addresses;
    }

    public void setAddresses(List addresses) {
        this.addresses = addresses;
    }

}
