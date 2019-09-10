package com.bacon.domain;

import java.util.ArrayList;
import java.util.Arrays;
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
    protected String especificaciones;

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
            Additional adic = adicionales.get(i).additional;
            int cant = adicionales.get(i).cantidad;
            stb.append("+").append(adic.getName()).append("<font color=blue>(x").append(cant).append(")</font>,");
        }
        if (!stb.toString().isEmpty()) {
            stb.delete(stb.length() - 1, stb.length());
        }
        return stb.toString();

    }

    public double getValueAdicionales() {
        double value = 0;
        for (int i = 0; i < adicionales.size(); i++) {
            Additional adic = adicionales.get(i).additional;
            int cant = adicionales.get(i).cantidad;
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

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            System.out.println("obj diff");
            return true;
        }
        if (!(obj instanceof ProductoPed)) {
            System.out.println("instance diff");
            return false;
        }
        ProductoPed prod = (ProductoPed) obj;
        if (!producto.equals(prod.getProduct())) {
            System.out.println("prod diff");
            return false;
        }

        System.out.println(Arrays.toString(adicionales.toArray()));
        System.out.println(Arrays.toString(prod.getAdicionales().toArray()));

        if (!CollectionUtils.isEqualCollection(adicionales, prod.getAdicionales())) {
            System.out.println("add diff");
            return false;
        }

        
        System.out.println(Arrays.toString(exclusiones.toArray()));
        System.out.println(Arrays.toString(prod.getExclusiones().toArray()));
        if (!CollectionUtils.isEqualCollection(exclusiones, prod.getExclusiones())) {
            System.out.println("exc diff");
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + Objects.hashCode(this.producto);
        hash = 53 * hash + Objects.hashCode(this.adicionales);
        hash = 53 * hash + Objects.hashCode(this.exclusiones);
        return hash;
    }

}
