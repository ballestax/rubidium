/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bacon;

import com.bacon.domain.Permission;
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
        new Permission("show-pedidos-module", "Ver el modulo pedidos"),
        new Permission("show-orderlist-module", "Ver el modulo lista de pedidos"),
        new Permission("show-tab-backup", "Ver pestaña copia de seguridad"),        
        new Permission("show-tab-config", "Ver pestaña configuracion"),
        new Permission("show-tab-users", "Ver el modulo administrador"),};

    public static final SimpleDateFormat FORMAT_DATE = new SimpleDateFormat("dd-MM-yyyy");

    public static final String PEDIDO_LOCAL = "LOCAL";
    public static final String PEDIDO_DOMICILIO = "DOMICILIO";
    public static final String PEDIDO_PARA_LLEVAR = "PARA LLEVAR";

    public static final String[] TIPO_PEDIDO = {PEDIDO_LOCAL, PEDIDO_DOMICILIO, PEDIDO_PARA_LLEVAR};

}
