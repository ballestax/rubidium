package com.bacon.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import org.apache.commons.collections.CollectionUtils;

/**
 *
 * @author lrod
 */
public class ProductoPed {

    protected Product producto;
    protected ArrayList<AdditionalPed> adicionales;
    protected ArrayList<Ingredient> exclusiones;
    protected Presentation presentation;
    protected String especificaciones;
    protected int cantidad;
    protected double precio;
    protected HashMap<Integer, HashMap> data;

    public ProductoPed() {
        this(new Product());

    }

    public ProductoPed(Product producto) {
        this.producto = producto;
        adicionales = new ArrayList<>();
        exclusiones = new ArrayList<>();
    }

    public Product getProduct() {
        return producto;
    }

    public void setProduct(Product product) {
        this.producto = product;
    }

    public ArrayList<AdditionalPed> getAdicionales() {
        return adicionales;
    }

    public String getStAdicionales() {
        StringBuilder stb = new StringBuilder();
        for (int i = 0; i < adicionales.size(); i++) {
            Additional adic = adicionales.get(i).getAdditional();
            int cant = adicionales.get(i).getCantidad();
            stb.append("+").append(adic.getName()).append("<font color=blue>(x").append(cant).append(")</font>,");
        }
        if (!stb.toString().isEmpty()) {
            stb.delete(stb.length() - 1, stb.length());
        }
        return stb.toString();
    }

    public String getStAdicionales2() {
        StringBuilder stb = new StringBuilder();
        for (int i = 0; i < adicionales.size(); i++) {
            Additional adic = adicionales.get(i).getAdditional();
            int cant = adicionales.get(i).getCantidad();
            stb.append("+").append(adic.getName()).append("(x").append(cant).append(")\n");
        }
        if (!stb.toString().isEmpty()) {
            stb.delete(stb.length() - 1, stb.length());
        }
        return stb.toString();
    }

    public String[] getStAdicionales3() {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < adicionales.size(); i++) {
            Additional adic = adicionales.get(i).getAdditional();
            int cant = adicionales.get(i).getCantidad();
            StringBuilder stb = new StringBuilder();
            stb.append("+").append(adic.getName()).append("<font color=blue>(x").append(cant).append(")</font>");
            list.add(stb.toString());
        }
        return list.toArray(new String[0]);
    }

    public double getValueAdicionales() {
        double value = 0;
        for (int i = 0; i < adicionales.size(); i++) {
            Additional adic = adicionales.get(i).getAdditional();
            int cant = adicionales.get(i).getCantidad();
            value += adic.getPrecio() * cant;
        }
        return value;
    }

    public void setAdicionales(ArrayList<AdditionalPed> adicionales) {
        this.adicionales = adicionales;
    }

    public void addAdicional(Additional adicion, int cant) {
        this.adicionales.add(new AdditionalPed(adicion, cant));
    }

    public String getEspecificaciones() {
        return especificaciones;
    }

    public void setEspecificaciones(String especificaciones) {
        this.especificaciones = especificaciones;
    }

    public ArrayList<Ingredient> getExclusiones() {
        return exclusiones;
    }

    public boolean hasAdditionals() {
        return !adicionales.isEmpty();
    }

    public boolean hasExcluisones() {
        return !exclusiones.isEmpty();
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public Presentation getPresentation() {
        return presentation;
    }

    public void setPresentation(Presentation presentation) {
        this.presentation = presentation;
    }

    public boolean hasPresentation() {
        return presentation != null;
    }

    public String getStExclusiones() {
        StringBuilder stb = new StringBuilder();
        for (int i = 0; i < exclusiones.size(); i++) {
            Ingredient ing = exclusiones.get(i);
            stb.append(ing.getName()).append(",");
        }
        if (!stb.toString().isEmpty()) {
            stb.delete(stb.length() - 1, stb.length());
        }
        return stb.toString();
    }

    public void setExclusiones(ArrayList<Ingredient> exclusiones) {
        this.exclusiones = exclusiones;
    }

    public void addExclusion(Ingredient excluision) {
        this.exclusiones.add(excluision);
    }

    public HashMap<Integer, HashMap> getData() {
        return data;
    }

    public void setData(HashMap<Integer, HashMap> data) {
        this.data = data;
    }

    public void putData(Integer key, HashMap data) {
        if (this.data == null) {
            this.data = new HashMap<>();
        }
        this.data.put(key, data);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        ProductoPed prod = (ProductoPed) obj;
        if (!producto.equals(prod.getProduct())) {
            return false;
        }

        if (presentation != null && !presentation.equals(prod.getPresentation())) {
            return false;
        }

        if (precio != prod.getPrecio()) {
            return false;
        }

        if (!CollectionUtils.isEqualCollection(adicionales, prod.getAdicionales())) {
            return false;
        }

        if (!CollectionUtils.isEqualCollection(exclusiones, prod.getExclusiones())) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.producto);
        hash = 97 * hash + Objects.hashCode(this.adicionales);
        hash = 97 * hash + Objects.hashCode(this.exclusiones);
        hash = 97 * hash + Objects.hashCode(this.presentation);
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.precio) ^ (Double.doubleToLongBits(this.precio) >>> 32));
        return hash;
    }


}
