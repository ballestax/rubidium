/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rb.persistence.dao;

/**
 *
 * @author ballestax
 */
public class DAOException extends Exception {

    public DAOException(String msg, Exception e) {
        super(msg, e);
    }

    public DAOException(String msg) {
        super(msg);
    }

    @Override
    public String getMessage() {
        if (getCause() != null) {
            return super.getMessage() + ": " + getCause().getMessage();
        }
        return super.getMessage();
    }

    @Override
    public void printStackTrace() {
        if (getCause() != null) {
            getCause().printStackTrace();
        }
        super.printStackTrace();
    }
}
