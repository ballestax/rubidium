/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bacon.persistence.dao;


import com.bacon.domain.Invoice;
import java.util.ArrayList;

/**
 *
 * @author LuisR
 */
public interface InvoiceDAO {

    public Invoice getInvoice(int id) throws com.bacon.persistence.dao.DAOException;

    public ArrayList<Invoice> getInvoiceList() throws com.bacon.persistence.dao.DAOException;

    public void addInvoice(Invoice invoice) throws com.bacon.persistence.dao.DAOException;

    public void deleteInvoice(int id) throws com.bacon.persistence.dao.DAOException;

    public void updateInvoice(Invoice invoice) throws com.bacon.persistence.dao.DAOException;

}
