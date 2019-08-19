/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bacon.persistence.dao;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author ballestax
 */
public abstract class DAOFactory {

    private static final String PROPERTY_FACTORY_CLASS_FILE = "dao.factory.class";
    private static final String CONFIG_FILE = "config/dao.properties";
    private static DAOFactory INSTANCE;

    protected DAOFactory() {

    }

    public static DAOFactory getInstance() throws DAOException {
        init();
        return INSTANCE;
    }

    private static final void init() throws DAOException {
        if (INSTANCE != null) {
            return;
        }
        try {
            Properties properties = new Properties();
//            properties.load(new FileInputStream(Aplication.getDirTrabajo() + File.separator + CONFIG_FILE));
            properties.load(new FileInputStream(CONFIG_FILE));
            String className = properties.getProperty(PROPERTY_FACTORY_CLASS_FILE);
            if (className == null) {
                throw new IOException("Invalid " + PROPERTY_FACTORY_CLASS_FILE + " property");
            }
            INSTANCE = (DAOFactory) Class.forName(className).newInstance();
        } catch (IllegalAccessException ex) {
            throw new DAOException("Cannot construct factory class.", ex);
        } catch (ClassNotFoundException ex) {
            throw new DAOException("Cannot construct factory class.", ex);
        } catch (IOException ex) {
            throw new DAOException("Unable to parse configuration file", ex);
        } catch (InstantiationException ex) {
            throw new DAOException("Cannot construct factory class.", ex);
        }

    }

    public abstract void clean() throws DAOException;

    public abstract void close() throws DAOException;

    public abstract UserDAO getUserDAO() throws DAOException;

    public abstract UtilDAO getUtilDAO() throws DAOException;

    public abstract ConfigDAO getConfigDAO() throws DAOException;

}
