/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rb.persistence.dao;


import com.rb.domain.Product;
import java.util.ArrayList;

/**
 *
 * @author LuisR
 */
public interface ProductDAO {

    public Product getProduct(int id) throws com.rb.persistence.dao.DAOException;

    public ArrayList<Product> getProductList() throws com.rb.persistence.dao.DAOException;

    public void addProduct(Product product) throws com.rb.persistence.dao.DAOException;

    public void deleteProduct(int id) throws com.rb.persistence.dao.DAOException;

    public void updateProduct(Product product) throws com.rb.persistence.dao.DAOException;

}
