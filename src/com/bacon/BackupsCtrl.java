/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bacon;


import com.bacon.persistence.JDBC.JDBCDAOFactory;
import static com.bacon.persistence.JDBC.JDBCDAOFactory.NAMED_PARAM_TABLE;
import static com.bacon.persistence.JDBC.JDBCDAOFactory.TRUNCATE_TABLE_KEY;
import com.bacon.persistenc.SQLLoader;
import com.bacon.persistence.dao.DAOException;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.SwingWorker;
import org.apache.log4j.Logger;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

/**
 *
 * @author ballestax
 */
public class BackupsCtrl {

    private final Aplication app;
    private static final Logger logger = Logger.getLogger(BackupsCtrl.class.getCanonicalName());

    public BackupsCtrl(Aplication app) {
        this.app = app;
    }

    public void getListadoBackups() {

    }

    public void doBackup(String fileName) {
        logger.debug("Starting backup");
        SQLDump inst = SQLDump.getInstance(app);
        String[] tablas = {
            "table",
        };

        StandardPBEStringEncryptor sse = new StandardPBEStringEncryptor();
        sse.setPasswordCharArray(new TimeWaste().cast());
        ArrayList<String> backup = new ArrayList<>();
        for (String tabla : tablas) {
            try {
                logger.debug("Doing backup: "+tabla);
                ArrayList<String> backupTable = inst.getBackupTable(tabla);
                for (int i = 0; i < backupTable.size(); i++) {
                    String get = backupTable.get(i);
                    String senc = sse.encrypt(get);
                    backup.add(senc);
                }
            } catch (DAOException | SQLException e) {
                System.err.println(e.getMessage());
                String msg = "No se pudo completar la copia de seguridad\n";
                logger.debug(msg, e);
                GUIManager.showErrorMessage(null, msg + e.getMessage(), "Error");
            }
        }

        try {
//            String fileName = "bck_" + app.getFormatoFecha().format(new Date());
            Path get = Paths.get(fileName);
            org.balx.Utiles.escribirTexto(get.toString(), backup);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            logger.debug(e.getMessage(), e);
        }
    }

    public void deleteBackup() {

    }

    public void restoreBackup(final ArrayList<String> backup, PropertyChangeListener pcl) {
        final String[] tablas = {
            "table"
        };

        final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
        pcs.addPropertyChangeListener(pcl);
        SwingWorker sw = new SwingWorker<Object, Object>() {

            @Override
            protected Object doInBackground() throws Exception {
                Connection connection = null;
                PreparedStatement st = null;
                PreparedStatement truncate = null;
                try {
                    pcs.firePropertyChange("RESTORE_INIT", -1, 0);
                    connection = ((JDBCDAOFactory) JDBCDAOFactory.getInstance()).getDataSource().getConnection();
                    connection.setAutoCommit(false);
                    SQLLoader sqlStatements = ((JDBCDAOFactory) JDBCDAOFactory.getInstance()).getSqlStatements();
                    int i = 0;
                    for (i = 0; i < tablas.length; i++) {
                        String tabla = tablas[i];
                        Map<String, String> namedParams = new HashMap<>();
                        namedParams.put(NAMED_PARAM_TABLE, tabla);
                        truncate = sqlStatements.buildSQLStatement(connection, TRUNCATE_TABLE_KEY, namedParams);
                        truncate.executeUpdate();
                        pcs.firePropertyChange("RESTORE_TRUNCATE", -1, i);
                    }
                    StandardPBEStringEncryptor sse = new StandardPBEStringEncryptor();
                    sse.setPasswordCharArray(new TimeWaste().cast());
                    for (int j = 0; j < backup.size(); j++) {
                        String sql = sse.decrypt(backup.get(j));
                        sql = sql.replace("'null'", "NULL");
                        sql = sql.replace("''", "'");
                        sql = sql.replace(",')", ",'')");
                        while (sql.contains(",',")) {
                            sql = sql.replace(",',", ",'',");
                        }
                        st = connection.prepareStatement(sql);
                        pcs.firePropertyChange("RESTORE_INSERT", -1, i + j);
                        st.execute();
                    }
                    connection.commit();
                } catch (SQLException | DAOException | IOException e) {
                    DBManager.rollbackConn(connection);
                    System.err.println(e.getMessage());
                    logger.debug(e.getMessage(), e);
                    pcs.firePropertyChange("RESTORE_ERROR", 0, -99);
                    return false;
                } finally {
                    DBManager.closeStatement(truncate);
                    DBManager.closeStatement(st);
                    DBManager.closeConnection(connection);
                    System.err.println("Restoring finalizado");
                    logger.debug("Restoring finalizado");
                    pcs.firePropertyChange("RESTORE_FINALLY", 0, -100);
                    return true;
                }
            }

        };
        sw.execute();

    }

}