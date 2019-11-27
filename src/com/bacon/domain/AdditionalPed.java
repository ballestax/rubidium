package com.bacon.domain;

import java.util.Objects;

/**
 *
 * @author lrod
 */
public class AdditionalPed {

    private Additional additional;
    private int cantidad;

    public AdditionalPed(Additional add, int cantidad) {
        this.additional = add;
        this.cantidad = cantidad;
    }

    public Additional getAdditional() {
        return additional;
    }

    public void setAdditional(Additional additional) {
        this.additional = additional;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AdditionalPed)) {
            return false;
        }
        AdditionalPed addP = (AdditionalPed) obj;
        if (!additional.equals(addP.additional)) {
            return false;
        }
        if (cantidad != (addP.cantidad)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + Objects.hashCode(this.additional);
        hash = 71 * hash + this.cantidad;
        return hash;
    }

}
