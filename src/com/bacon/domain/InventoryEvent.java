package com.bacon.domain;

import java.util.Date;

/**
 *
 * @author lrod
 */
public class InventoryEvent {

    public static final int EVENT_IN = 1;
    public static final int EVENT_OUT = 2;

    protected long id;
    protected long idItem;
    protected long idUser;
    protected int event;
    protected double quantity;
    protected Date lastUpdate;

    public InventoryEvent() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getIdItem() {
        return idItem;
    }

    public void setIdUser(long idUser) {
        this.idUser= idUser;
    }

    public long getIdUser() {
        return idUser;
    }

    public void setIdItem(long idItem) {
        this.idItem = idItem;
    }
    
    public int getEvent() {
        return event;
    }

    public void setEvent(int event) {
        this.event = event;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

}
