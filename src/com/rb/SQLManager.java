/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rb;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.dbcp2.BasicDataSource;

/**
 *
 * @author Ballestax
 */
public class SQLManager {

    private static Aplication app;
    private BasicDataSource bds;

    private SQLManager() {
    }

    public static SQLManager getInstance(Aplication app) {
        SQLManager.app = app;
        return SQLManagerHolder.INSTANCE;
    }

    private static class SQLManagerHolder {

        private static final SQLManager INSTANCE = new SQLManager();
    }

    public ResultSet consultarTabla(String sql) throws Exception {
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        try {
//            Class.forName("org.sqlite.JDBC");
//            String urlDEF = app.getDirTrabajo() + File.separator + Aplication.DATABASE;
//            String url = app.getConfiguration().getProperty(Configuration.DATABASE_URL, urlDEF);
//            conn = DriverManager.getConnection("jdbc:sqlite:" + url);
            conn = bds.getConnection();
            st = conn.createStatement();
            rs = st.executeQuery(sql);
            return rs;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            app.getGuiManager().showError(e.getMessage());
            return null;
        }
    }

    public ResultSet consultarTabla(String sql, String url) throws Exception {
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            Class.forName("org.sqlite.JDBC");
            String urlDB = url;
            conn = DriverManager.getConnection("jdbc:sqlite:" + urlDB);
//            conn = bds.getConnection();
            st = conn.createStatement();
            rs = st.executeQuery(sql);
            return rs;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            app.getGuiManager().showError(e.getMessage());
            return null;
        }
    }

    public void actualizarTabla(String sql, String link) throws Exception {

        Connection conn = null;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + link);
//            conn = bds.getConnection();
            Statement statement = conn.createStatement();
            statement.executeUpdate(sql);
            statement.close();
        } catch (SQLException e) {
            app.getGuiManager().showError("Error al intentar guardar en la base de datos.\n" + e.getMessage());
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e);
            }
        }
    }

    public void actualizarTabla(String sql) throws Exception {

        Connection conn = null;
        try {
            Class.forName("org.sqlite.JDBC");
            String urlDEF = app.getDirTrabajo() + File.separator + Aplication.DATABASE;
            String url = app.getConfiguration().getProperty(Configuration.DATABASE_URL, urlDEF);
            conn = DriverManager.getConnection("jdbc:sqlite:" + url);
//            conn = bds.getConnection();
            Statement statement = conn.createStatement();
            statement.executeUpdate(sql);
            statement.close();
        } catch (SQLException e) {
            app.getGuiManager().showError("Error al intentar guardar en la base de datos.\n" + e.getMessage());
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e);
            }
        }
    }

    public void actualizarTablaBatch(ArrayList<String> batchSql) throws Exception {

        Connection conn = null;
        try {
            Class.forName("org.sqlite.JDBC");
            String urlDEF = app.getDirTrabajo() + File.separator + Aplication.DATABASE;
            String url = app.getConfiguration().getProperty(Configuration.DATABASE_URL, urlDEF);
            conn = DriverManager.getConnection("jdbc:sqlite:" + url);
//            conn = bds.getConnection();
            Statement statement = conn.createStatement();
            for (int i = 0; i < batchSql.size(); i++) {
                String string = batchSql.get(i);
                statement.addBatch(string);
            }
            statement.executeBatch();
            statement.clearBatch();
            statement.close();
        } catch (SQLException e) {
            app.getGuiManager().showError("Error al intentar guardar en la base de datos.\n" + e.getMessage());
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e);
            }
        }
    }

    public void setDatasource(String driverName, String prefijo, String url, String user, String pass) {

        bds = new BasicDataSource();
        bds.setDriverClassName(driverName);
        bds.setUsername(user);
        bds.setPassword(pass);
        bds.setUrl(prefijo + ":" + url);

    }

    public boolean testDataSourceSQLite(String url) {
        if (!Files.exists(Paths.get(url, ""), LinkOption.NOFOLLOW_LINKS)) {
            try {
                Class.forName("org.sqlite.JDBC");
                String prefijo = "jdbc:sqlite";
                Connection con = DriverManager.getConnection(prefijo + ":" + url);
                con.prepareStatement("select 1").executeQuery();
            } catch (SQLException ex) {
                Logger.getLogger(SQLManager.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("ex = " + ex);
                return false;
            } catch (ClassNotFoundException ex) {
                System.out.println("ex = " + ex);
                Logger.getLogger(SQLManager.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean testDataSourceMySql(String url, String user, char[] password) {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            String prefijo = "jdbc:mysql:";
            Connection con = DriverManager.getConnection(prefijo + url, user, new String(password));
            con.prepareStatement("select 1").executeQuery();
        } catch (SQLException ex) {
            Logger.getLogger(SQLManager.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("ex = " + ex);
            return false;
        } catch (ClassNotFoundException ex) {
            System.out.println("ex = " + ex);
            Logger.getLogger(SQLManager.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    public BasicDataSource getDataSource() {
        return bds;
    }

    public String getQueryCrearTabla() {
        StringBuffer s;

        s = new StringBuffer("CREATE TABLE proyectos(");
        s.append("id INTEGER  NOT NULL  PRIMARY KEY,");
        s.append("nombre TEXT NOT NULL,");
        s.append("departamento TEXT NOT NULL,");
        s.append("municipio TEXT NOT NULL); ");

        return s.toString();
    }
}
