/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rb.persistence.dao;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

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
            INSTANCE = (DAOFactory) Class.forName(className).getDeclaredConstructor().newInstance();
        } catch (IllegalAccessException | ClassNotFoundException | InstantiationException ex) {
            throw new DAOException("Cannot construct factory class.", ex);
        } catch (IOException ex) {
            throw new DAOException("Unable to parse configuration file", ex);
        } catch (NoSuchMethodException ex) {
            throw new DAOException("No such method construct factory class.", ex);
        } catch (SecurityException ex) {
            throw new DAOException("Security construct factory class.", ex);
        } catch (IllegalArgumentException ex) {
            throw new DAOException("Illegal arguments.", ex);
        } catch (InvocationTargetException ex) {
            throw new DAOException("Invocation target.", ex);
        }

    }

    public abstract void clean() throws DAOException;

    public abstract void close() throws DAOException;

    public abstract UserDAO getUserDAO() throws DAOException;

    public abstract UtilDAO getUtilDAO() throws DAOException;

    public abstract ConfigDAO getConfigDAO() throws DAOException;

    public abstract ProductDAO getProductDAO() throws DAOException;

    public abstract IngredientDAO getIngredientDAO() throws DAOException;

    public abstract AdditionalDAO getAdditionalDAO() throws DAOException;

    public abstract InvoiceDAO getInvoiceDAO() throws DAOException;

    public abstract OrderDAO getOrderDAO() throws DAOException;

    public abstract ClientDAO getClientDAO() throws DAOException;

    public abstract ItemDAO getItemDAO() throws DAOException;

    public abstract ConciliacionDAO getConciliacionDAO() throws DAOException;

    public abstract LocationDAO getLocationDAO() throws DAOException;

}
