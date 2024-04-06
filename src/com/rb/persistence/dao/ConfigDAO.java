/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rb.persistence.dao;

import com.rb.domain.ConfigDB;

/**
 *
 * @author ballestax
 */
public interface ConfigDAO {

    public void addConfigDB(ConfigDB config) throws DAOException;

    public ConfigDB getConfigDB(String clave) throws DAOException;

    public void deleteConfigDB(String clave) throws DAOException;

    public void updateConfigDB(ConfigDB configDB) throws DAOException;

}
