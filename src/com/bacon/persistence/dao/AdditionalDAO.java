/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bacon.persistence.dao;


import com.bacon.domain.Additional;
import java.util.ArrayList;

/**
 *
 * @author LuisR
 */
public interface AdditionalDAO {

    public Additional getAdditional(int id) throws com.bacon.persistence.dao.DAOException;

    public ArrayList<Additional> getAdditionalList() throws com.bacon.persistence.dao.DAOException;

    public void addAdditional(Additional additional) throws com.bacon.persistence.dao.DAOException;

    public void deleteAdditional(int id) throws com.bacon.persistence.dao.DAOException;

    public void updateAdditional(Additional additional) throws com.bacon.persistence.dao.DAOException;

}
