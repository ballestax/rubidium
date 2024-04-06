/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rb.persistence.dao;


import com.rb.domain.Invoice;
import java.util.ArrayList;

/**
 *
 * @author LuisR
 */
public interface InvoiceDAO {

    public Invoice getInvoice(int id) throws com.rb.persistence.dao.DAOException;

    public ArrayList<Invoice> getInvoiceList() throws com.rb.persistence.dao.DAOException;

    public void addInvoice(Invoice invoice) throws com.rb.persistence.dao.DAOException;

    public void deleteInvoice(int id) throws com.rb.persistence.dao.DAOException;

    public void updateInvoice(Invoice invoice) throws com.rb.persistence.dao.DAOException;

}
