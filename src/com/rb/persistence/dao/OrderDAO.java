    /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rb.persistence.dao;


import com.rb.domain.Order;
import java.util.ArrayList;

/**
 *
 * @author LuisR
 */
public interface OrderDAO {

    public Order getOrder(int id) throws com.rb.persistence.dao.DAOException;

    public ArrayList<Order> getOrderList() throws com.rb.persistence.dao.DAOException;

    public long addOrder(Order order) throws com.rb.persistence.dao.DAOException;

    public void deleteOrder(int id) throws com.rb.persistence.dao.DAOException;

    public void updateOrder(Order order) throws com.rb.persistence.dao.DAOException;

}
