/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rb;

import static com.rb.MyConstants.FILTER_BOOL_DIFFERENT;
import static com.rb.MyConstants.FILTER_BOOL_EQUAL;
import static com.rb.MyConstants.FILTER_NUM_DIFFERENT;
import static com.rb.MyConstants.FILTER_NUM_EQUALS;
import static com.rb.MyConstants.FILTER_NUM_GREATER;
import static com.rb.MyConstants.FILTER_NUM_GREATER_EQUAL;
import static com.rb.MyConstants.FILTER_NUM_LESS;
import static com.rb.MyConstants.FILTER_NUM_LESS_EQUAL;
import static com.rb.MyConstants.FILTER_TEXT_CONTAINS;
import static com.rb.MyConstants.FILTER_TEXT_EQUALS;
import static com.rb.MyConstants.FILTER_TEXT_START;
import static com.rb.MyConstants.FILTROS_BOOLEANOS;
import static com.rb.MyConstants.FILTROS_NUMERO;
import static com.rb.MyConstants.FILTROS_TEXTO;

import java.util.HashMap;

/**
 *
 * @author ballestax
 */
public class ControlFilters {

    private String[] tables;
    private HashMap<String, Field[]> M_FIELDS;
    private HashMap<String, String> FILTERS;
    private Field[] FIELDS_DF_PERSONS;
    private Field[] FIELDS_DF_VEHICLES;
    private Field[] FIELDS_DF_COMMUNE;

    public ControlFilters() {
        init();
    }

    private void init() {
        Field cedula = new Field(Field.T_NUMERIC, "Cedula", "persons.identification");
        Field nombre1 = new Field(Field.T_TEXT, "Nombre1", "persons.firstName1");
        Field nombre2 = new Field(Field.T_TEXT, "Nombre2", "persons.firstName2");
        Field nombres = new Field(Field.T_TEXT, "Nombres", "CONCAT (firstName1,' ',firstName2)");
        Field apellido1 = new Field(Field.T_TEXT, "Apellido1", "persons.lastName1");
        Field apellido2 = new Field(Field.T_TEXT, "Apellido2", "persons.lastName2");
        Field lider = new Field(Field.T_TEXT, "Lider", "persons.lider");
        Field telefono = new Field(Field.T_TEXT, "Telefono", "persons.cellphone");
        Field profesion = new Field(Field.T_TEXT, "Profesion", "persons.profession");
        Field direccion = new Field(Field.T_TEXT, "Direccion", "persons.address");
        Field responsable = new Field(Field.T_TEXT, "Responsable", "liders.responsable");
        Field barrio = new Field(Field.T_TEXT, "Barrio", "persons.neighborhood");
        Field comuna = new Field(Field.T_TEXT, "Comuna", "neighborhoods.commune");
        Field corregimiento = new Field(Field.T_TEXT, "Corregimiento", "persons.place1");
        Field rancheria = new Field(Field.T_TEXT, "Comunidad", "persons.place1");
        Field fnac = new Field(Field.T_DATE, "F. Nacimiento", "persons.birthday");
        Field email = new Field(Field.T_TEXT, "Email", "persons.email");
        Field puestoVot = new Field(Field.T_TEXT, "Puesto", "voting_places.place");
        Field mesaVot = new Field(Field.T_TEXT, "Mesa", "votingTable");
        Field zonVot = new Field(Field.T_TEXT, "Zona", "voting_places.zone");
        Field dep = new Field(Field.T_TEXT, "Departamento", "voting_places.department");
        Field mun = new Field(Field.T_TEXT, "Municipio", "voting_places.municipality");
        Field testigo = new Field(Field.T_BOOLEAN, "Testigo", "persons.witness");
        Field jurado = new Field(Field.T_BOOLEAN, "Jurado", "persons.jury");
        Field votoduro = new Field(Field.T_BOOLEAN, "Voto Duro", "persons.hardvote");
//        Field dirigente = new Field(Field.T_BOOLEAN, "Dirigente", "persons.witness");
//        Field dirigenteCamp = new Field(Field.T_BOOLEAN, "Dir. Campaña", "persons.jury");
        Field voluntario = new Field(Field.T_BOOLEAN, "Voluntario", "persons.volunteer");
//        Field compromiso = new Field(Field.T_TEXT, "Compromiso", "persons.compromise");
        Field vehiculo = new Field(Field.T_TEXT, "Vehiculo", "vehicles.type");
        Field placa = new Field(Field.T_TEXT, "Placa", "vehicles.placa");
//        Field gobernacion = new Field(Field.T_TEXT, "Gobernacion", "liders.gobernacion");
//        Field asamblea = new Field(Field.T_TEXT, "Asamblea", "liders.asamblea");
//        Field alcaldia = new Field(Field.T_TEXT, "Alcaldia", "liders.alcaldia");
//        Field consejo = new Field(Field.T_TEXT, "Consejo", "liders.consejo");
        Field MOD = new Field(Field.T_TEXT, "MODIFICADO", "lastUpdatedTime");
//        Field = new Field(Field.T_TEXT, "", "");

        tables = new String[]{"POTENCIAL", "LIDERES", "TESTIGOS", "JURADOS", "PUBLICIDAD", "TRANSPORTE"};
//        tables = new String[]{"POTENCIAL", "LIDERES", "TESTIGOS", "JURADOS", "PUBLICIDAD", "TRANSPORTE"};
        M_FIELDS = new HashMap<>();
//        M_FIELDS.put(tables[0], new Field[]{cedula, nombre1, nombre2, apellido1, apellido2,
//            lider, telefono, profesion, direccion, barrio, comuna, corregimiento, rancheria, fnac, email, puestoVot, mesaVot,
//            zonVot, dep, mun, testigo, jurado, voluntario, compromiso});
        M_FIELDS.put(tables[0], new Field[]{cedula, nombre1, nombre2, apellido1, apellido2,
            lider, telefono, direccion, barrio, comuna, corregimiento, rancheria, puestoVot, mesaVot,
            zonVot, dep, mun, testigo, jurado, fnac, vehiculo, placa, votoduro});

//        M_FIELDS.put(tables[1], new Field[]{cedula, nombre1, nombre2, apellido1, apellido2,
//            telefono, profesion, direccion, barrio, comuna, corregimiento, rancheria, fnac, email, puestoVot, mesaVot,
//            zonVot, dep, mun, dirigente, dirigenteCamp, compromiso,
//            gobernacion, asamblea, alcaldia, consejo
//        });
        
        M_FIELDS.put(tables[1], new Field[]{cedula, nombre1, nombre2, apellido1, apellido2,
            telefono, direccion, barrio, email, comuna, corregimiento, rancheria, puestoVot, mesaVot,
            zonVot, dep, mun,fnac, vehiculo, placa,votoduro, responsable
        });

//        M_FIELDS.put(tables[2], new Field[]{cedula, nombre1, nombre2, apellido1, apellido2,
//            lider, telefono, profesion, direccion, barrio, comuna, corregimiento, rancheria, fnac, email, puestoVot, mesaVot,
//            zonVot, dep, mun, testigo, jurado, voluntario, compromiso});
//        
        M_FIELDS.put(tables[2], new Field[]{cedula, nombre1, nombre2, apellido1, apellido2,
            lider, telefono, direccion, barrio, email, comuna, corregimiento, rancheria, puestoVot, mesaVot,
            zonVot, dep, mun, testigo, jurado,fnac, vehiculo, placa, votoduro});

//        M_FIELDS.put(tables[3], new Field[]{cedula, nombre1, nombre2, apellido1, apellido2,
//            lider, telefono, profesion, direccion, barrio, comuna, corregimiento, rancheria, fnac, email, puestoVot, mesaVot,
//            zonVot, dep, mun, testigo, jurado, voluntario, compromiso});
        
        M_FIELDS.put(tables[3], new Field[]{cedula, nombre1, nombre2, apellido1, apellido2,
            lider, telefono, direccion, barrio, email, comuna, corregimiento, rancheria, puestoVot, mesaVot,
            zonVot, dep, mun, testigo, jurado,fnac, vehiculo, placa, votoduro});

//        M_FIELDS.put(tables[5], new Field[]{cedula, nombre1, nombre2, apellido1, apellido2,
//            lider, telefono, profesion, direccion, barrio, comuna, corregimiento, rancheria, fnac, email, puestoVot, mesaVot,
//            zonVot, dep, mun, testigo, jurado, voluntario, compromiso, vehiculo, placa});
        
        M_FIELDS.put(tables[5], new Field[]{cedula, nombre1, nombre2, apellido1, apellido2,
            lider, telefono, direccion, barrio, email, comuna, corregimiento, rancheria, puestoVot, mesaVot,
            zonVot, dep, mun, testigo, jurado,fnac, vehiculo, placa, votoduro});

        FIELDS_DF_PERSONS = new Field[]{cedula, nombre1, nombre2, apellido1, apellido2, dep, mun, puestoVot, mesaVot, votoduro};
//        FIELDS_DF_VEHICLES = new Field[]{cedula, nombre1, nombre2, apellido1, apellido2, dep, mun, puestoVot, barrio, vehiculo, placa};

        FIELDS_DF_COMMUNE = new Field[]{cedula, apellido1, apellido2, nombres, telefono,
            direccion, barrio, comuna};

        FILTERS = new HashMap<>();
        FILTERS.put(FILTROS_TEXTO[0], FILTER_TEXT_EQUALS);//1
        FILTERS.put(FILTROS_TEXTO[1], FILTER_TEXT_START);//2
        FILTERS.put(FILTROS_TEXTO[2], FILTER_TEXT_CONTAINS);//3
        FILTERS.put(FILTROS_NUMERO[0], FILTER_NUM_EQUALS);//4
        FILTERS.put(FILTROS_NUMERO[1], FILTER_NUM_GREATER);//5
        FILTERS.put(FILTROS_NUMERO[2], FILTER_NUM_GREATER_EQUAL);//6
        FILTERS.put(FILTROS_NUMERO[3], FILTER_NUM_LESS);//7
        FILTERS.put(FILTROS_NUMERO[4], FILTER_NUM_LESS_EQUAL);//8
        FILTERS.put(FILTROS_NUMERO[5], FILTER_NUM_DIFFERENT);//9
        FILTERS.put(FILTROS_BOOLEANOS[0], FILTER_BOOL_EQUAL);//10
        FILTERS.put(FILTROS_BOOLEANOS[1], FILTER_BOOL_DIFFERENT);//11

    }

    public Field[] getFields(String table) {
        return M_FIELDS.get(table);
    }

    public Field[] getFieldsDefault(String table) {
        if (tables[5].equals(table)) {
            return FIELDS_DF_VEHICLES;
        } else if ("COMUNAS".equals(table)) {
            return FIELDS_DF_COMMUNE;
        } else {
            return FIELDS_DF_PERSONS;
        }
    }

    public String[] getTables() {
        return tables;
    }

    public String getIndexFilter(String key) {
        return FILTERS.get(key);
    }

}
