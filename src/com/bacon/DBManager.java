/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bacon;

import com.bacon.persistence.JDBC.JDBCDAOFactory;
import com.bacon.persistence.dao.DAOException;
import com.bacon.persistenc.SQLLoader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.dbcp.BasicDataSource;

/**
 *
 * @author hp
 */
public class DBManager {

    private static String NAMED_PARAM_TABLE = "{table}";
    private static String DROP_TABLE_KEY = "DROP_TABLE";
    private static String TRUNCATE_TABLE_KEY = "TRUNCATE_TABLE";
    private BasicDataSource bds;
    private static final Logger logger = Logger.getLogger(DBManager.class.getCanonicalName());

    private DBManager() {
    }

    public static DBManager getInstance() {
        //DBManager.app = app;
        return DBManagerHolder.INSTANCE;
    }

    private static class DBManagerHolder {

        private static final DBManager INSTANCE = new DBManager();
    }

    public Connection getConnection(String user, String pass) throws SQLException {
        return bds.getConnection(user, pass);
    }

    public Connection getConnection() throws SQLException {
        return bds.getConnection();
    }

    public void setupDatabase(String driverName, String prefijo, String url, String user, String pass) {
        bds = new BasicDataSource();
        bds.setDriverClassName(driverName);
        bds.setUsername(user);
        bds.setPassword(pass);
        bds.setUrl(prefijo + ":" + url);        
    }

    public BasicDataSource getDatasource() {
        return bds;
    }

    public boolean testDataSource(String url) {
        try {
            Connection con = getDatasource().getConnection();
            con.prepareStatement("select 1").executeQuery();
        } catch (SQLException ex) {
            Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("ex = " + ex);
            return false;
        }
        return true;
    }

    public static void rollbackConn(Connection conn) {
        try {
            if (conn != null) {
                conn.rollback();
            }
        } catch (SQLException e) {
            logger.severe("Could not rollback connection: " + e.getMessage());
        }
    }

    public static void closeConnection(Connection conn) {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            logger.severe("Could not close connection: " + e.getMessage());
        }
    }

    public static void closeStatement(Statement st) {
        try {
            if (st != null) {
                st.close();
            }
        } catch (SQLException e) {
            logger.severe("Could not close statement: " + e.getMessage());
        }
    }

    public static void closeResultSet(ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            logger.severe("Could not close result set: " + e.getMessage());
        }
    }

    public static boolean tableExists(String table, Connection conn) throws DAOException {
        if (table == null) {
            throw new IllegalArgumentException("Null table name");
        }
        ResultSet rs = null;
        try {
            rs = conn.getMetaData().getTables(null, null, table.trim().toLowerCase(), new String[]{"TABLE"});
            boolean res = rs.next();
            return res;
        } catch (SQLException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Could not check the table for existence.", e);
        } finally {
            DBManager.closeResultSet(rs);
        }
    }

    public static boolean checkDBExists(String dbName, Connection conn) throws DAOException {
        if (dbName == null) {
            throw new IllegalArgumentException("Null database name");
        }
        ResultSet rs = null;
        try {
            rs = conn.getMetaData().getCatalogs();
            while (rs.next()) {
                String databaseName = rs.getString(1);
                if (databaseName.equals(dbName)) {
                    return true;
                }
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void deleteTable(String tableName, Connection conn, SQLLoader sqlStatements) throws DAOException {
        PreparedStatement delete = null;
        try {
            delete = deleteTableStatement(tableName, conn, sqlStatements);
            delete.executeUpdate();
        } catch (SQLException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Unable to delete table", e);
        }
    }

    public static void truncateTable(String tableName, Connection conn, SQLLoader sqlStatements) throws DAOException {
        PreparedStatement truncate = null;
        try {
            truncate = truncateTableStatement(tableName, conn, sqlStatements);
            truncate.executeUpdate();
        } catch (SQLException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Unable to truncate table", e);
        }
    }

    private static PreparedStatement deleteTableStatement(String tableName, Connection conn, SQLLoader sqlStatements) throws DAOException {
        PreparedStatement delete = null;
        try {
            Map<String, String> namedParams = new HashMap<>();
            namedParams.put(NAMED_PARAM_TABLE, tableName);

            delete = sqlStatements.buildSQLStatement(conn, DROP_TABLE_KEY, namedParams);
            return delete;
        } catch (SQLException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Unable to delete table", e);
        } catch (IOException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Unable to delete table", e);
        }
    }

    private static PreparedStatement truncateTableStatement(String tableName, Connection conn, SQLLoader sqlStatements) throws DAOException {
        PreparedStatement truncate = null;
        try {
            Map<String, String> namedParams = new HashMap<>();
            namedParams.put(NAMED_PARAM_TABLE, tableName);
            truncate = sqlStatements.buildSQLStatement(conn, TRUNCATE_TABLE_KEY, namedParams);
            return truncate;
        } catch (SQLException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Unable to delete table", e);
        } catch (IOException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Unable to delete table", e);
        }
    }

    private static PreparedStatement countTableStatement(String sql, Connection conn, SQLLoader sqlStatements) throws DAOException {
        PreparedStatement count = null;
        try {
            Map<String, String> namedParamsCount = new HashMap<String, String>();
            namedParamsCount.put(JDBCDAOFactory.NAMED_PARAM_QUERY, sql);
            count = sqlStatements.buildSQLStatement(conn, JDBCDAOFactory.COUNT_TABLE_KEY, namedParamsCount);
            return count;
        } catch (SQLException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Could not count the table.", e);
        } catch (IOException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Could not count the table.", e);
        }
    }

    /**
     * Return the number of rows that a query will return
     *
     * @param sql The SQL query in question
     * @param conn A connection to a datasource
     * @param sqlStatements SQLLoader containing sql queries for the database
     * @return The number of rows the sql query will return
     * @throws DAOException If there is a problem connecting to the datasource
     */
    public static int countTable(String sql, Connection conn, SQLLoader sqlStatements) throws DAOException {
        PreparedStatement count = null;
        ResultSet rs = null;
        try {
            count = countTableStatement(sql, conn, sqlStatements);

            rs = count.executeQuery();
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Could not count the table.", e);
        } finally {
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(count);
        }
    }
}
