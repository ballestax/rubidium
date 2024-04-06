/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rb.persistence.dao;

import com.rb.domain.Location;
import java.util.ArrayList;

/**
 *
 * @author LuisR
 */
public interface LocationDAO {

    public Location getLocation(int id) throws DAOException;

    public ArrayList<Location> getLocationList() throws DAOException;

    public void addLocation(Location location) throws DAOException;

    public void deleteLocation(int id) throws DAOException;

    public void updateLocation(Location location) throws DAOException;

}
