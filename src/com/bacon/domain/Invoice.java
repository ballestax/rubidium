package com.bacon.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 *
 * @author lrod
 */
public class Invoice {

    private Long id;
    private Long ciclo;
    private String factura;
    private Date fecha;
    private Long idCliente;
    private BigDecimal valor;
    private Double descuento;
    private BigDecimal valorDelivery;
    private int tipoEntrega;
    private String nota;
    private int idWaitress;
    private int table;
    private List<ProductoPed> products;

    public Invoice() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFactura() {
        return factura;
    }

    public void setFactura(String factura) {
        this.factura = factura;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Long getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
    }

    public Double getDescuento() {
        return descuento;
    }

    public void setDescuento(Double descuento) {
        this.descuento = descuento;
    }

    public int getTipoEntrega() {
        return tipoEntrega;
    }

    public void setTipoEntrega(int tipoEntrega) {
        this.tipoEntrega = tipoEntrega;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public BigDecimal getValorDelivery() {
        return valorDelivery;
    }

    public void setValorDelivery(BigDecimal valorDelivery) {
        this.valorDelivery = valorDelivery;
    }

    public List<ProductoPed> getProducts() {
        return products;
    }

    public void setProducts(List<ProductoPed> products) {
        this.products = products;
    }

    public Long getCiclo() {
        return ciclo;
    }

    public void setCiclo(Long ciclo) {
        this.ciclo = ciclo;
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

}
