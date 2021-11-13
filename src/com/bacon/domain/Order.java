package com.bacon.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 *
 * @author lrod
 */
public class Order {

    private Long id;
    private Long ciclo;
    private Date fecha;
    private BigDecimal valor;
    private String nota;
    private int idWaitress;
    private int table;
    private List<ProductoPed> products;

    public Order() {
    }

    public Order(Long ciclo, int idWaitress, int table) {
        this.ciclo = ciclo;
        this.idWaitress = idWaitress;
        this.table = table;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCiclo() {
        return ciclo;
    }

    public void setCiclo(Long ciclo) {
        this.ciclo = ciclo;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public int getIdWaitress() {
        return idWaitress;
    }

    public void setIdWaitress(int idWaitress) {
        this.idWaitress = idWaitress;
    }

    public int getTable() {
        return table;
    }

    public void setTable(int table) {
        this.table = table;
    }

    public List<ProductoPed> getProducts() {
        return products;
    }

    public void setProducts(List<ProductoPed> products) {
        this.products = products;
    }

}
