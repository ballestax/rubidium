package com.rb.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import org.apache.commons.collections4.CollectionUtils;

/**
 *
 * @author lrod
 */
public class ProductoPed {

    protected Product producto;
    protected String termino;
    protected String corte;
    protected ArrayList<AdditionalPed> adicionales;
    protected ArrayList<Ingredient> exclusiones;
    protected Presentation presentation;
    protected String especificaciones;
    protected int cantidad;
    protected double precio;
    protected boolean entry;
    protected boolean delivery;
    protected HashMap<Integer, HashMap> data;
    protected int status;
    protected String stations;

    public static final int ST_NORMAL = 0;
    public static final int ST_SENDED = 1;
    public static final int ST_SENDED_MOD = 2;
    public static final int ST_MOD_ADD_CANT = 3;
    public static final int ST_MOD_MIN_CANT = 4;
    public static final int ST_NEW_ADD = 5;
    public static final int ST_AVOIDED = 6;

    public ProductoPed() {
        this(new Product());
        this.precio = 0;
    }

    public ProductoPed(Product producto) {
        this.producto = producto;
        termino = null;
        corte = null;
        entry = false;
        adicionales = new ArrayList<>();
        exclusiones = new ArrayList<>();
        this.precio = producto.getPrice();
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
        if (presentation != null) {
            this.precio = presentation.getPrice();
        }
    }

    public boolean hasPresentation() {
        return presentation != null;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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

    public void setTermino(String termino) {
        this.termino = termino;
    }

    public String getTermino() {
        return termino;
    }

    public boolean hasTermino() {
        return termino != null && termino.trim().isEmpty();
    }

    public String getCorte() {
        return corte;
    }

    public void setCorte(String corte) {
        this.corte = corte;
    }

    public boolean hasCorte() {
        return corte != null && corte.trim().isEmpty();
    }

    public boolean isEntry() {
        return entry;
    }

    public void setEntry(boolean entry) {
        this.entry = entry;
    }

    public boolean isDelivery() {
        return delivery;
    }

    public void setDelivery(boolean delivery) {
        this.delivery = delivery;
    }

    public void setStations(String stations) {
        this.stations = stations;
    }

    public String getStations() {
        return stations;
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

        if (termino != null && !termino.equals(prod.getTermino())) {
            System.out.println(":" + termino);
            System.out.println("::" + prod.getTermino());
            System.out.println("distinc termino");
            return false;
        }
        if (corte != null && !corte.equals(prod.getCorte())) {
            System.out.println("distinc corte");
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + Objects.hashCode(this.producto);
        hash = 11 * hash + Objects.hashCode(this.termino);
        hash = 11 * hash + Objects.hashCode(this.corte);
        hash = 11 * hash + Objects.hashCode(this.adicionales);
        hash = 11 * hash + Objects.hashCode(this.exclusiones);
        hash = 11 * hash + Objects.hashCode(this.presentation);
        hash = 11 * hash + Objects.hashCode(this.especificaciones);
        hash = 11 * hash + (int) (Double.doubleToLongBits(this.precio) ^ (Double.doubleToLongBits(this.precio) >>> 32));
        return hash;
    }

}
