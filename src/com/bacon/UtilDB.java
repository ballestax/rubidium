/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bacon;

import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author ballestax
 */
public class UtilDB {

    private Aplication app;
    private String URL = "/home/ballestax/Desktop/divipol.db";

    public UtilDB(Aplication app) {
        this.app = app;
        SQLManager sql = SQLManager.getInstance(app);
        sql.testDataSourceSQLite(URL);

        crearTabla();

        leerArchivo("/home/ballestax/Desktop/DIVIPOL_DEFINITIVA_AUTORIDADES_LOCALES_OCTUBRE_2015.csv");
    }

    private void leerArchivo(String archivo) {
        try {
            File file = new File(archivo);
            ArrayList<String> splitArchivoEnLineas = org.bx.Utiles.splitArchivoEnLineas(file, true);
            for (int i = 0; i < splitArchivoEnLineas.size(); i++) {
                String string = splitArchivoEnLineas.get(i);
                System.out.println(string);
                parsearLineas(string);
            }
        } catch (Exception e) {
        }

    }

    private void crearTabla() {
        SQLManager sql = SQLManager.getInstance(app);

        sql.setDatasource("org.sqlite.JDBC", "jdbc:sqlite:/home/ballestax/Desktop/divipol", "divipol", "", "");

        StringBuffer s = new StringBuffer("CREATE TABLE divipol(");
        s.append("id INTEGER  NOT NULL PRIMARY KEY,");
        s.append("dep INTEGER NOT NULL,");
        s.append("mun INTEGER NOT NULL,");
        s.append("zon INTEGER NOT NULL,");
        s.append("pto INTEGER NOT NULL,");
        s.append("departamento TEXT NOT NULL,");
        s.append("municipio TEXT NOT NULL,");
        s.append("nom_puesto TEXT NOT NULL,");
        s.append("direccion TEXT NOT NULL,");
        s.append("potencial INTEGER NOT NULL,");
        s.append("mesas INTEGER NOT NULL,");
        s.append("jal INTEGER NOT NULL,");
        s.append("nom_jal TEXT NOT NULL); ");
        try {
            sql.actualizarTabla(s.toString(), URL);
        } catch (Exception e) {
        }

    }

    private void parsearLineas(String linea) {
        String[] split = linea.split(";");
        SQLManager sql = SQLManager.getInstance(app);
        sql.setDatasource("org.sqlite.JDBC", "jdbc:sqlite", "divipol", "", "");
        StringBuilder str = new StringBuilder();
        str.append("insert into divipol (id,dep,mun,zon,pto,departamento,municipio,nom_puesto,direccion,potencial,mesas,jal,nom_jal) values(");
        str.append("NULL,");
        for (int i = 0; i < 12; i++) {
            if (i == 4 || i == 5 || i == 6 || i == 7 || i == 11) {
                str.append("\'" + split[i] + "\'").append(i != 11 ? "," : "");
            } else {
                str.append(split[i] + ",");
            }
        }        
        str.append(");");
        System.out.println(str.toString());
        try {
            sql.actualizarTabla(str.toString(), URL);
        } catch (Exception e) {
        }
    }

}
