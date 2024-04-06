/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rb.persistence.dao;


import com.rb.persistence.dao.DAOException;
import com.rb.domain.User;
import com.rb.domain.User.AccessLevel;

/**
 *
 * @author ballestax
 */
public interface UserDAO {

    static final String DEFAULT_ADMIN = "admin";
    static final String DEFAULT_ADMIN_PASSWORD = "password";

    public boolean userExists(String username) throws DAOException;

//    public RemoteUserResultsInterface retrieveUsers() throws DAOException, RemoteException;
//
//    public RemoteUserResultsInterface retrieveUsers(String whereClause, String orderByClause) throws DAOException, RemoteException;
    public void addUser(User user) throws DAOException;

    public void addUser(String username, String passwordHash) throws DAOException;

    public void addUser(String username, String passwordHash, AccessLevel accessLevel) throws DAOException;

//    public void updateUser(String username, String passwordHash, AccessLevel accessLevel) throws DAOException;

    public void updateUser(int userId, String username, String passwordHash, AccessLevel accessLevel) throws DAOException;

    public void deleteUser(String username) throws DAOException;

    public User checkPassword(String username, String passwordHash) throws DAOException;
}
