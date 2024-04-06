/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rb.persistence.JDBC;

import com.rb.DBManager;
import com.rb.domain.Additional;
import com.rb.persistence.SQLExtractor;
import com.rb.persistence.SQLLoader;
import com.rb.persistence.dao.DAOException;
import com.rb.persistence.dao.AdditionalDAO;
import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 *
 * @author LuisR
 */
public class JDBCAdditionalDAO implements AdditionalDAO {

    public static final String TABLE_NAME = "additionals";
    public static final String NAMED_PARAM_WHERE = "{where}";
    public static final String NAMED_PARAM_ORDER_BY = "{orderby}";
    private static final Logger logger = Logger.getLogger(JDBCAdditionalDAO.class.getCanonicalName());
    private final DataSource dataSource;
    private final SQLLoader sqlStatements;
    protected static final String CREATE_ADDITIONALS_TABLE_KEY = "CREATE_ADDITIONALS_TABLE";
    protected static final String ADD_ADDITIONAL_KEY = "ADD_ADDITIONAL";
    protected static final String UPDATE_ADDITIONAL_KEY = "UPDATE_ADDITIONAL";
    protected static final String GET_ADDITIONAL_KEY = "GET_ADDITIONAL";
    protected static final String DELETE_ADDITIONAL_KEY = "DELETE_ADDITIONAL";

    public JDBCAdditionalDAO(DataSource dataSource, SQLLoader sqlStatements) throws DAOException {
        this.dataSource = dataSource;
        this.sqlStatements = sqlStatements;
    }

    public final void init() throws DAOException, RemoteException {
        // Create the table if it does not already exist
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            if (DBManager.tableExists(TABLE_NAME, conn)) {
                return;
            }
            ps = sqlStatements.buildSQLStatement(conn, CREATE_ADDITIONALS_TABLE_KEY);
            ps.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot create Additional table", e);
        } catch (IOException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot create Additional table", e);
        } finally {
            DBManager.closeStatement(ps);
            DBManager.closeConnection(conn);
        }
    }

    public Additional getAdditionalBy(String query) throws DAOException {
        String retrieveImporter;
        try {
            SQLExtractor sqlExtractorWhere = new SQLExtractor(query, SQLExtractor.Type.WHERE);
            Map<String, String> namedParams = new HashMap<String, String>();
            namedParams.put(NAMED_PARAM_WHERE, sqlExtractorWhere.extractWhere());
            retrieveImporter = sqlStatements.getSQLString(GET_ADDITIONAL_KEY, namedParams);

        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the additional", e);
        } catch (IOException e) {
            throw new DAOException("Could not properly retrieve the additional", e);
        }
        Connection conn = null;
        PreparedStatement retrieve = null;
        ResultSet rs = null;
        Additional additional = null;
        try {
            conn = dataSource.getConnection();
            retrieve = conn.prepareStatement(retrieveImporter);
            rs = retrieve.executeQuery();
            while (rs.next()) {
                additional = new Additional();
                additional.setId(rs.getInt(1));
                additional.setName(rs.getString(2));
                additional.setCode(rs.getString(3));
                additional.setMeasure(rs.getString(4));
                additional.setPrecio(rs.getDouble(5));
            }
        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the Additional: " + e);
        } finally {
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(retrieve);
            DBManager.closeConnection(conn);
        }
        return additional;
    }

    @Override
    public Additional getAdditional(int id) throws DAOException {
        return getAdditionalBy("id=" + id);
    }

    @Override
    public ArrayList<Additional> getAdditionalList() throws DAOException {
        return getAdditionalList("", "");
    }

    public ArrayList<Additional> getAdditionalList(String where, String orderBy) throws DAOException {
        String retrieveAdditionals;
        ArrayList<Additional> additionals = new ArrayList<>();
        try {
            SQLExtractor sqlExtractorWhere = new SQLExtractor(where, SQLExtractor.Type.WHERE);
            SQLExtractor sqlExtractorOrderBy = new SQLExtractor(orderBy, SQLExtractor.Type.ORDER_BY);
            Map<String, String> namedParams = new HashMap<>();
            namedParams.put(NAMED_PARAM_WHERE, sqlExtractorWhere.extractWhere());
            namedParams.put(NAMED_PARAM_ORDER_BY, sqlExtractorOrderBy.extractOrderBy());
            retrieveAdditionals = sqlStatements.getSQLString(GET_ADDITIONAL_KEY, namedParams);

        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the Additional List", e);
        } catch (IOException e) {
            throw new DAOException("Could not properly retrieve the Additional List", e);
        }

        Connection conn = null;
        PreparedStatement retrieve = null;
        ResultSet rs = null;
        Additional additional = null;
        try {
            conn = dataSource.getConnection();
            retrieve = conn.prepareStatement(retrieveAdditionals);
            rs = retrieve.executeQuery();            

            while (rs.next()) {
                additional = new Additional();
                additional.setId(rs.getInt(1));
                additional.setName(rs.getString(2));
                additional.setCode(rs.getString(3));
                additional.setMeasure(rs.getString(4));
                additional.setPrecio(rs.getDouble(5));
                additionals.add(additional);
            }
        } catch (SQLException e) {
            throw new DAOException("Could not proper retrieve the Additional: " + e);
        } finally {
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(retrieve);
            DBManager.closeConnection(conn);
        }
        return additionals;
    }

    @Override
    public void addAdditional(Additional additional) throws DAOException {
        if (additional == null) {
            throw new IllegalArgumentException("Null additional");
        }
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            Object[] parameters = {
                additional.getName(),
                additional.getCode(),
                additional.getMeasure(),
                additional.getPrecio()
            };
            ps = sqlStatements.buildSQLStatement(conn, ADD_ADDITIONAL_KEY, parameters);

            ps.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot add Additional", e);
        } catch (IOException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot add Additional", e);
        } finally {
            DBManager.closeStatement(ps);
            DBManager.closeConnection(conn);
        }
    }

    @Override
    public void deleteAdditional(int id) throws DAOException {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            Object[] parameters = {id};
            ps = sqlStatements.buildSQLStatement(conn, DELETE_ADDITIONAL_KEY, parameters);
            ps.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot delete the additional", e);
        } catch (IOException e) {
            throw new DAOException("Cannot delete the additional", e);
        } finally {
            DBManager.closeStatement(ps);
            DBManager.closeConnection(conn);
        }
    }

    @Override
    public void updateAdditional(Additional additional) throws DAOException {
        Connection conn = null;
        PreparedStatement update = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            Object[] parameters = {
                additional.getName(),
                additional.getCode(),
                additional.getMeasure(),
                additional.getPrecio()
            };
            update = sqlStatements.buildSQLStatement(conn, UPDATE_ADDITIONAL_KEY, parameters);
            update.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Could not properly update the additional", e);
        } catch (IOException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Could not properly update the additional", e);
        } finally {
            DBManager.closeStatement(update);
            DBManager.closeConnection(conn);
        }
    }

    public ArrayList<Additional> getAdditionalByQuery(String query) throws DAOException {
        String retrieve = query;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Additional additional = null;
        ArrayList<Additional> additionals = new ArrayList<>();
        try {
            conn = dataSource.getConnection();
            ps = conn.prepareStatement(retrieve);
            rs = ps.executeQuery();
            while (rs.next()) {
                additional = new Additional();
                additional.setId(rs.getInt(1));
                additional.setName(rs.getString(2));
                additional.setCode(rs.getString(3));
                additional.setMeasure(rs.getString(4));
                additional.setPrecio(rs.getDouble(5));
                additionals.add(additional);
            }
        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the Additional: " + e);
        } finally {
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(ps);
            DBManager.closeConnection(conn);
        }
        return additionals;
    }

}
