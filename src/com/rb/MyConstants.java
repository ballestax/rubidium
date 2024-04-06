/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rb;

import com.rb.domain.Permission;
import java.text.SimpleDateFormat;

/**
 *
 * @author hp
 */
public class MyConstants {

    public static final String FIELD_ID = "id";

    public static final String[] MONTHS = {"ENERO", "FEBRERO", "MARZO", "ABRIL", "MAYO", "JUNIO",
        "JULIO", "AGOSTO", "SEPTIEMBRE", "OCTUBRE", "NOVIEMBRE", "DICIEMBRE"};

    public static final int FILTER_TEXT_INT_EQUALS = 1;
    public static final int FILTER_TEXT_INT_START = 2;
    public static final int FILTER_TEXT_INT_CONTAINS = 3;
    public static final int FILTER_NUM_INT_EQUALS = 4;
    public static final int FILTER_NUM_INT_GREATER = 5;
    public static final int FILTER_NUM_INT_GREATER_EQUAL = 6;
    public static final int FILTER_NUM_INT_LESS = 7;
    public static final int FILTER_NUM_INT_LESS_EQUAL = 8;
    public static final int FILTER_NUM_INT_DIFFERENT = 9;

    public static final String FILTER_TEXT_EQUALS = "LIKE '<>'";
    public static final String FILTER_TEXT_START = "LIKE '<>%'";
    public static final String FILTER_TEXT_CONTAINS = "LIKE '%<>%'";
    public static final String FILTER_NUM_EQUALS = "=";
    public static final String FILTER_NUM_GREATER = ">";
    public static final String FILTER_NUM_GREATER_EQUAL = ">=";
    public static final String FILTER_NUM_LESS = "<";
    public static final String FILTER_NUM_LESS_EQUAL = "<=";
    public static final String FILTER_NUM_DIFFERENT = "<>";
    public static final String FILTER_BOOL_EQUAL = "=";
    public static final String FILTER_BOOL_DIFFERENT = "!=";

    public static final String[] FILTROS_TEXTO = {"IGUAL A", "EMPIEZA POR", "CONTIENE"};
    public static final String[] FILTROS_NUMERO = {"=", ">", ">=", "<", "<=", "<>"};
    public static final String[] FILTROS_BOOLEANOS = {"=", "<>"};

    public static final Permission[] PERMISSIONS = {
        new Permission("show-admin-module", "Ver el modulo administrador"),
        new Permission("show-orders-module", "Ver el modulo pedidos"),
        new Permission("show-cash-module", "Ver el modulo caja"),
        new Permission("show-reports-module", "Ver el modulo reportes"),
        new Permission("show-orderlist-module", "Ver el modulo lista de pedidos"),
        new Permission("show-tab-backup", "Ver pestaña copia de seguridad"),        
        new Permission("show-tab-config", "Ver pestaña configuracion"),
        new Permission("show-tab-users", "Ver el modulo administrador"),
        new Permission("show-inventory-module", "Ver el modulo inventario"),
        new Permission("show-products-module", "Ver el modulo productos"),
        new Permission("add-items-inventary", "Agregar items al inventario"),
        new Permission("load-items-inventary", "Cargar items al inventario"),
        new Permission("download-items-inventary", "Descargar items al inventario"),
        new Permission("conciliate-items-inventary", "Coniciliar items al inventario"),
        new Permission("print-items-inventary", "Inprimir items del inventario"),
        new Permission("export-items-inventary", "Exportar items del inventario"),
        
        new Permission("show-pos-module", "Ver el modulo POS"),
        new Permission("allow-cancel-product-order", "Cancelar producto en una orden"),
        new Permission("allow-modify-product-order", "Modificar producto en una orden"),
        
    };

    public static final SimpleDateFormat FORMAT_DATE = new SimpleDateFormat("dd-MM-yyyy");

    public static final String PEDIDO_LOCAL = "LOCAL";
    public static final String PEDIDO_DOMICILIO = "DOMICILIO";
    public static final String PEDIDO_PARA_LLEVAR = "PARA LLEVAR";

    public static final String[] TIPO_PEDIDO = {PEDIDO_LOCAL, PEDIDO_DOMICILIO, PEDIDO_PARA_LLEVAR};
    
    public static final String[] PERIODOS = {"DIA", "SEMANA", "MES", "AÑO", "RANGO"};
    public static final String[] TIPO_REPORTE = {"PRODUCTOS VENDIDOS", "VENTAS"};
    
    public static final String CF_FACTURA_FINAL = "factura_final";
    public static final String CF_FACTURA_INICIAL = "factura_inicial";
    public static final String CF_FACTURA_ACTUAL = "factura_actual";

}
