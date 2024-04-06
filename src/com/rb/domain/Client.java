package com.rb.domain;

import java.util.ArrayList;
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
        addresses = new ArrayList();
    }

    public Client(int id) {
        addresses = new ArrayList();
        this.id = id;
    }

    public Client(String cellphone) {
        addresses = new ArrayList();
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

    public void addAddress(String address) {
        this.addresses.add(address);
    }

    @Override
    public String toString() {
        return cellphone;
    }

    public String toStringFull() {
        return "Client:+"
                + "\nid:" + id
                + "\ncellphone:" + cellphone
                + "\nname:" + names
                + "\nlastname:" + lastName
                + "\naddress:" + (!addresses.isEmpty() ? addresses.get(0) : "---");
    }

}
