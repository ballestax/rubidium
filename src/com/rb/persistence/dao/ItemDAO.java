/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rb.persistence.dao;


import com.rb.domain.Item;
import java.util.ArrayList;

/**
 *
 * @author LuisR
 */
public interface ItemDAO {

    public Item getItem(long id) throws com.rb.persistence.dao.DAOException;

    public ArrayList<Item> getItemList() throws com.rb.persistence.dao.DAOException;

    public void addItem(Item item) throws com.rb.persistence.dao.DAOException;

    public void deleteItem(long id) throws com.rb.persistence.dao.DAOException;

    public void updateItem(Item item) throws com.rb.persistence.dao.DAOException;

}
