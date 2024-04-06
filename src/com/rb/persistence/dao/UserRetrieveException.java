package com.rb.persistence.dao;

/**
 * Thrown if there is a problem processing a User
 *
 * @author Raymes Khoury
 *
 */
@SuppressWarnings("serial")
public class UserRetrieveException extends RetrieveRemoteException {

    public UserRetrieveException(String message) {
        super(message);
    }
}
