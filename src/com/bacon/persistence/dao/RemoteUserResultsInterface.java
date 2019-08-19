package com.bacon.persistence.dao;

import com.bacon.domain.User;




/**
 * This is an interface for a proxy results object which is used by a Client to
 * access User objects on the Desktop.
 * 
 * @author Raymes Khoury
 * 
 */
public interface RemoteUserResultsInterface extends RemoteResultsInterface<User, UserRetrieveException> {
	
}
