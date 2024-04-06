/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rb.persistence.dao;

import com.rb.domain.Conciliacion;
import java.util.ArrayList;

/**
 *
 * @author LuisR
 */
public interface ConciliacionDAO {

    public Conciliacion getConciliacion(int id) throws DAOException;

    public ArrayList<Conciliacion> getConciliacionList() throws DAOException;

    public void addConciliacion(Conciliacion conciliacion) throws DAOException;

    public void deleteConciliacion(int id) throws DAOException;

    public void updateConciliacion(Conciliacion conciliacion) throws DAOException;

}
