/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bacon.persistence.dao;

import com.bacon.domain.Client;
import java.util.ArrayList;

/**
 *
 * @author LuisR
 */
public interface ClientDAO {

    public Client getClient(long id) throws com.bacon.persistence.dao.DAOException;

    public ArrayList<Client> getClientList() throws com.bacon.persistence.dao.DAOException;

    public void addClient(Client cliente) throws com.bacon.persistence.dao.DAOException;

    public void deleteClient(long id) throws com.bacon.persistence.dao.DAOException;

    public void updateClient(Client cliente) throws com.bacon.persistence.dao.DAOException;

}
