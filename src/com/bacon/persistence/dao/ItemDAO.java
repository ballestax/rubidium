/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bacon.persistence.dao;


import com.bacon.domain.Item;
import java.util.ArrayList;

/**
 *
 * @author LuisR
 */
public interface ItemDAO {

    public Item getItem(long id) throws com.bacon.persistence.dao.DAOException;

    public ArrayList<Item> getItemList() throws com.bacon.persistence.dao.DAOException;

    public void addItem(Item item) throws com.bacon.persistence.dao.DAOException;

    public void deleteItem(long id) throws com.bacon.persistence.dao.DAOException;

    public void updateItem(Item item) throws com.bacon.persistence.dao.DAOException;

}
