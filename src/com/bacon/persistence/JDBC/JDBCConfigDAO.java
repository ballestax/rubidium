/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bacon.persistence.JDBC;


import com.bacon.DBManager;
import com.bacon.domain.ConfigDB;
import com.bacon.persistenc.SQLExtractor;
import com.bacon.persistenc.SQLLoader;
import com.bacon.persistence.dao.ConfigDAO;
import com.bacon.persistence.dao.DAOException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 *
 * @author ballestax
 */
public class JDBCConfigDAO implements ConfigDAO {

    public static final String TABLE_NAME = "config";
    public static final String NAMED_PARAM_WHERE = "{where}";
    public static final String NAMED_PARAM_ORDER_BY = "{orderby}";
    private static final Logger logger = Logger.getLogger(JDBCConfigDAO.class.getCanonicalName());
    private DataSource dataSource;
    private SQLLoader sqlStatements;

    public static final String CREATE_CONFIG_TABLE_KEY = "CREATE_CONFIG_TABLE";
    public static final String GET_CONFIG_KEY = "GET_CONFIG";
    public static final String DELETE_CONFIG_KEY = "DELETE_CONFIG";
    public static final String UPDATE_CONFIG_KEY = "UPDATE_CONFIG";
    public static final String EXIST_CONFIG_KEY = "EXIST_CONFIG";
    public static final String ADD_CONFIG_KEY = "ADD_CONFIG";

    public JDBCConfigDAO(DataSource dataSource, SQLLoader sqlStatements) {
        this.dataSource = dataSource;
        this.sqlStatements = sqlStatements;
    }

    public void init() throws DAOException {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            if (DBManager.tableExists(TABLE_NAME, conn)) {
                return;
            }
            ps = sqlStatements.buildSQLStatement(conn, CREATE_CONFIG_TABLE_KEY);
            ps.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot create config table", e);
        } catch (IOException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot create config table", e);
        } finally {
            DBManager.closeStatement(ps);
            DBManager.closeConnection(conn);
        }
    }

    @Override
    public void addConfigDB(ConfigDB config) throws DAOException {
        if (config == null) {
            throw new IllegalArgumentException("Null config");
        }
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);            
            Object[] parameters = {
                config.getClave(),
                config.getValor(),
                config.getTipo()
            };
            ps = sqlStatements.buildSQLStatement(conn, ADD_CONFIG_KEY, parameters);
            ps.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot add config", e);
        } catch (IOException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot add config", e);
        } finally {
            DBManager.closeStatement(ps);
            DBManager.closeConnection(conn);
        }
    }

    @Override
    public ConfigDB getConfigDB(String clave) throws DAOException {
        String retrieveImporter;
        if (clave == null) {
            throw new IllegalArgumentException("Null clave");
        }
        try {
            SQLExtractor sqlExtractorWhere = new SQLExtractor("code='" + clave + "'", SQLExtractor.Type.WHERE);;
            Map<String, String> namedParams = new HashMap<String, String>();
            namedParams.put(NAMED_PARAM_WHERE, sqlExtractorWhere.extractWhere());
            retrieveImporter = sqlStatements.getSQLString(GET_CONFIG_KEY, namedParams);

        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the ConfigDB", e);
        } catch (IOException e) {
            throw new DAOException("Could not properly retrieve the ConfigDB", e);
        }
        Connection conn = null;
        PreparedStatement retrieve = null;
        ResultSet rs = null;
        ConfigDB configDB = null;
        try {
            conn = dataSource.getConnection();
            retrieve = conn.prepareStatement(retrieveImporter);
            rs = retrieve.executeQuery();
            while (rs.next()) {
                configDB = new ConfigDB();
                configDB.setId(rs.getInt(1));
                configDB.setClave(rs.getString(2));
                configDB.setValor(rs.getString(3));
                configDB.setTipo(rs.getString(4));

            }
        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the ConfigDB: " + e);
        } finally {
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(retrieve);
            DBManager.closeConnection(conn);
        }
        return configDB;
    }

    @Override
    public void deleteConfigDB(String clave) throws DAOException {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            Object[] parameters = {clave};
            ps = sqlStatements.buildSQLStatement(conn, DELETE_CONFIG_KEY, parameters);
            ps.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot delete the config", e);
        } catch (IOException e) {
            throw new DAOException("Cannot delete the config", e);
        } finally {
            DBManager.closeStatement(ps);

        }
    }

    @Override
    public void updateConfigDB(ConfigDB configDB) throws DAOException {
        Connection conn = null;
        PreparedStatement update = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            Object[] parameters = {
                configDB.getValor(),
                configDB.getTipo(),                
                configDB.getClave()
            };
            update = sqlStatements.buildSQLStatement(conn, UPDATE_CONFIG_KEY, parameters);
            update.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Could not properly update the configDB", e);
        } catch (IOException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Could not properly update the configDB", e);
        } finally {
            DBManager.closeStatement(update);
            DBManager.closeConnection(conn);
        }
    }

    public int existConfig(String code) throws DAOException {
        String retrieve;
        try {
            retrieve = sqlStatements.getSQLString(EXIST_CONFIG_KEY);
        } catch (IOException e) {
            throw new DAOException("Could not properly retrieve the outdated count", e);
        }
        Connection conn = null;
        PreparedStatement pSt = null;
        ResultSet rs = null;
        int count = 0;
        try {
            conn = dataSource.getConnection();
            Object[] parameters = {code};
//            pSt = conn.prepareStatement(retrieve);
            pSt = sqlStatements.buildSQLStatement(conn, EXIST_CONFIG_KEY, parameters);
            rs = pSt.executeQuery();
            while (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the outdated count: " + e);
        } catch (IOException ex) {
            throw new DAOException("Could not properly retrieve the outdated count: " + ex);
        } finally {
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(pSt);
            DBManager.closeConnection(conn);
        }
        return count;
    }

}
