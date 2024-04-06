/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rb.persistence.dao;

import com.rb.domain.Client;
import java.util.ArrayList;

/**
 *
 * @author LuisR
 */
public interface ClientDAO {

    public Client getClient(long id) throws com.rb.persistence.dao.DAOException;

    public ArrayList<Client> getClientList() throws com.rb.persistence.dao.DAOException;

    public void addClient(Client cliente) throws com.rb.persistence.dao.DAOException;

    public void deleteClient(long id) throws com.rb.persistence.dao.DAOException;

    public void updateClient(Client cliente) throws com.rb.persistence.dao.DAOException;

}
