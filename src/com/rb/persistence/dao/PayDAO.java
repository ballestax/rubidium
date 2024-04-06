/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rb.persistence.dao;

import com.rb.domain.Pay;
import java.util.ArrayList;

/**
 *
 * @author LuisR
 */
public interface PayDAO {

    public Pay getPay(int id) throws DAOException;

    public ArrayList<Pay> getPayList() throws DAOException;

    public void addPay(Pay pay) throws DAOException;

    public void deletePay(int id) throws DAOException;

    public void updatePay(Pay pay) throws DAOException;

}
