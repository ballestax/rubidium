/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bacon;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.prefs.Preferences;
import org.apache.log4j.Logger;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.properties.EncryptableProperties;

/**
 *
 * @author ballestax
 */
public class Configuration {

    private Preferences horPrefs;
    private EncryptableProperties configuration;
    public static final String ARCHIVO_RECIENTE = "arc_reciente";
    private String path;
    private String NAME = "configuracion.ini";
    public static final String DATABASE_DRIVER = "db.driver";
    public static final String DATABASE_PREFIJO = "db.prefijo";
    public static final String DATABASE_URL = "db.url";
    public static final String DATABASE_USER = "db.user";
    public static final String DATABASE_PASSWORD = "db.pass";

    public static final String DINST = "cf.dinst";
    public static final String CUS = "cf.cus";
    public static final String BACKUP_LAST_DIR = "bck.lastdir";
    public static final String BACKUP_LIST = "bck.list";
    public static final String DATABASE_STATION = "db.station";
    public static final String PRINTER_SELECTED = "cf.printer";
    public static final String DELIVERY_VALUE = "cf.delivery";
    public static final String PREFIX_INVOICES = "cf.prefix";
    public static final String ZEROS_INVOICES = "cf.zeros";
    public static final String MAX_SERVICE_PORC = "cf.maxservice";
    public static final String DEF_SERVICE_PORC = "cf.defservice";
    public static final String IS_SERVICE_DEF = "cf.isservice";
    public static final String PATH_IMG = "cf.pathimg";
    public static final String PRINT_PREV_DELIVERY = "cf.printprevius";
    public static final String SHOW_EXCLUSIONS = "cf.showexclusions";
    public static final String MAX_CATEGORIES_LIST = "cf.maxcategorieslist";
    public static final String SHOW_TOOLBAR = "cf.showtoolbar";
    public static final String SHOW_MENUBAR = "cf.showmenubar";
    public static final String PN_ENTRADA_PERIODO = "cf.listperiodo";
    public static final String INVOICE_OUT_STOCK = "cf.invoiceoutstock";
    public static final String DOCUMENT_NAME = "cf.documentname";
    public static final String CLIENT_NAME = "cf.clientname";
    public static final String PROD_ORDER = "cf.order";
    public static final String NUM_COLUMNS_VIEW1 = "cf.numcolumnsv1";
    public static final String NUM_COLUMNS_VIEW2 = "cf.numcolumnsv2";
    

    public static final String BS_ID = "cf.tck.id";
    public static final String BS_NAME = "cf.tck.name";
    public static final String BS_ADDRESS = "cf.tck.address";
    public static final String BS_PHONE = "cf.tck.phone";
    public static final String BS_CUSTOM_TOP = "cf.tck.custom1";
    public static final String BS_CUSTOM_BOTTON = "cf.tck.custom2";
    public static final String BS_CUSTOM_QUALITY_MSG = "cf.tck.quality_msg";
    public static final String BS_CUSTOM_QUALITY_SCALE = "cf.tck.quality_scale";
    public static final String BS_CUSTOM_QUALITY_ENABLED = "cf.tck.quality_enabled";

    private StandardPBEStringEncryptor encryptor;

    private static final Logger logger = Logger.getLogger(Configuration.class.getCanonicalName());

    private Configuration() {
        try {
            horPrefs = Preferences.userRoot().node(Aplication.PREFERENCES);
            encryptor = new StandardPBEStringEncryptor();
            encryptor.setPassword(String.valueOf(new TimeWaste().cast()));
            configuration = new EncryptableProperties(encryptor);
        } catch (Exception e) {
            // thrown when running unsigned JAR
            horPrefs = null;
        }
    }

    public static Configuration getInstancia() {
        return Configuration.ConfigurationInstance.INSTANCIA;
    }

    private void loadDefault() {
        logger.debug("Loading default configuration..");

        save();
    }

    private static class ConfigurationInstance {

        private static final Configuration INSTANCIA = new Configuration();
    }

    public String loadPreferences(String llave, String valorDefecto) {
        return horPrefs.get(llave, valorDefecto);
    }

    public void savePreferences(String llave, String valor) {
        if (llave != null && valor != null) {
            horPrefs.put(llave, valor);
        }
    }

    public void cleanPreferences() {
        try {
            logger.debug("Cleaning preferences..");
            horPrefs.clear();
            horPrefs.flush();
        } catch (Exception e) {
            logger.debug("Error cleaning preferences..", e);
        }
    }

    public void savePreferences() {
        try {
            logger.debug("Save preferences..");
            horPrefs.flush();
        } catch (Exception e) {
            logger.debug("Error saving preferences..", e);
        }
    }

    public void load() {
        try {
            File arc = new File(path + File.separator + NAME);
            FileInputStream fis = new FileInputStream(arc);
            configuration.load(fis);
            logger.debug("Loading configuration from: " + arc.getAbsolutePath());
        } catch (IOException e) {
            logger.debug("Configuration file is not found", e);
            loadDefault();
        }
    }

    public synchronized void save() {
        logger.debug("Saving configuration file");
        try {
            File arc = new File(path + File.separator + NAME);
            FileOutputStream fos = new FileOutputStream(arc);
            configuration.store(fos, "Configuration " + Aplication.TITLE);
            logger.debug("Configuration file saved: " + path);
        } catch (IOException e) {
            logger.debug("Error saving configuration.", e);
        }
    }

    public String getProperty(String property) {
        return configuration.getProperty(property);
    }

    public String getProperty(String property, String propDefault) {
        return configuration.getProperty(property, propDefault);
    }

    public int getProperty(String property, int propDefault) {        
        int valor = propDefault;
        try {
            valor = Integer.parseInt(configuration.getProperty(property, "" + propDefault));
        } catch (Exception e) {
        }        
        return valor;
    }

    public void setProperty(String property, String valor, boolean save) {
        String val = encryptor.encrypt(valor);
        configuration.setProperty(property, "ENC(" + val + ")");
        if (save) {
            save();
        }
    }

    public void setProperty(String property, String valor) {
        setProperty(property, valor, false);
    }

    public Set<Map.Entry<Object, Object>> getConfiguration() {
        return configuration.entrySet();
    }

    public void setPath(String path) {
        this.path = path;
    }
}
