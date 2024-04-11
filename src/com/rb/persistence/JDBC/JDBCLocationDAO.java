/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rb.persistence.JDBC;

import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.rb.DBManager;
import com.rb.domain.Location;
import com.rb.persistence.SQLExtractor;
import com.rb.persistence.SQLLoader;
import com.rb.persistence.dao.DAOException;
import com.rb.persistence.dao.LocationDAO;

/**
 *
 * @author LuisR
 */
public class JDBCLocationDAO implements LocationDAO {

    public static final String TABLE_NAME = "locations";
    public static final String NAMED_PARAM_WHERE = "{where}";
    public static final String NAMED_PARAM_ORDER_BY = "{orderby}";
    private static final Logger logger = LogManager.getLogger(JDBCLocationDAO.class.getCanonicalName());
    private final DataSource dataSource;
    private final SQLLoader sqlStatements;
    protected static final String CREATE_LOCATIONS_TABLE_KEY = "CREATE_LOCATIONS_TABLE";
    protected static final String ADD_LOCATION_KEY = "ADD_LOCATION";
    protected static final String UPDATE_LOCATION_KEY = "UPDATE_LOCATION";
    protected static final String GET_LOCATION_KEY = "GET_LOCATION";
    protected static final String DELETE_LOCATION_KEY = "DELETE_LOCATION";

    public JDBCLocationDAO(BasicDataSource dataSource, SQLLoader sqlStatements) throws DAOException {
        this.dataSource = dataSource;
        this.sqlStatements = sqlStatements;
    }

    public final void init() throws DAOException, RemoteException {
        // Create the artciculos table if it does not already exist
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            if (DBManager.tableExists(TABLE_NAME, conn)) {
                return;
            }
            ps = sqlStatements.buildSQLStatement(conn, CREATE_LOCATIONS_TABLE_KEY);
            ps.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot create Location table", e);
        } catch (IOException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot create Location table", e);
        } finally {
            DBManager.closeStatement(ps);
            DBManager.closeConnection(conn);
        }
    }

    public Location getLocationBy(String query) throws DAOException {
        String retrieveLocation;
        try {
            SQLExtractor sqlExtractorWhere = new SQLExtractor(query, SQLExtractor.Type.WHERE);;
            Map<String, String> namedParams = new HashMap<String, String>();
            namedParams.put(NAMED_PARAM_WHERE, sqlExtractorWhere.extractWhere());
            retrieveLocation = sqlStatements.getSQLString(GET_LOCATION_KEY, namedParams);

        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the Location", e);
        } catch (IOException e) {
            throw new DAOException("Could not properly retrieve the Location", e);
        }
        Connection conn = null;
        PreparedStatement retrieve = null;
        ResultSet rs = null;
        Location location = null;
        try {
            conn = dataSource.getConnection();
            retrieve = conn.prepareStatement(retrieveLocation);
            rs = retrieve.executeQuery();
            while (rs.next()) {
                location = new Location();
                location.setId(rs.getInt(1));
                location.setName(rs.getString(2));
                location.setAddress(rs.getString(3));
                location.setSalePoint(rs.getBoolean(4));
            }
        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the Location: " + e);
        } finally {
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(retrieve);
            DBManager.closeConnection(conn);
        }
        return location;
    }

    @Override
    public Location getLocation(int id) throws DAOException {
        return getLocationBy("id=" + id);
    }

    @Override
    public ArrayList<Location> getLocationList() throws DAOException {
        return getLocationList("", "");
    }

    public ArrayList<Location> getLocationList(String where, String orderBy) throws DAOException {
        String retrieveLocation;
        ArrayList<Location> locations = new ArrayList<>();
        try {
            SQLExtractor sqlExtractorWhere = new SQLExtractor(where, SQLExtractor.Type.WHERE);;
            SQLExtractor sqlExtractorOrderBy = new SQLExtractor(orderBy, SQLExtractor.Type.ORDER_BY);;
            Map<String, String> namedParams = new HashMap<>();
            namedParams.put(NAMED_PARAM_WHERE, sqlExtractorWhere.extractWhere());
            namedParams.put(NAMED_PARAM_ORDER_BY, sqlExtractorOrderBy.extractOrderBy());
            retrieveLocation = sqlStatements.getSQLString(GET_LOCATION_KEY, namedParams);

        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the Location List", e);
        } catch (IOException e) {
            throw new DAOException("Could not properly retrieve the Location List", e);
        }

        Connection conn = null;
        PreparedStatement retrieve = null;
        ResultSet rs = null;
        Location location = null;
        try {
            conn = dataSource.getConnection();
            retrieve = conn.prepareStatement(retrieveLocation);
            rs = retrieve.executeQuery();

            while (rs.next()) {
                location = new Location();
                location.setId(rs.getInt(1));
                location.setName(rs.getString(2));
                location.setAddress(rs.getString(3));
                location.setSalePoint(rs.getBoolean(4));
                locations.add(location);
            }
        } catch (SQLException e) {
            throw new DAOException("Could not proper retrieve the Location: " + e);
        } finally {
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(retrieve);
            DBManager.closeConnection(conn);
        }
        return locations;
    }

    @Override
    public void addLocation(Location location) throws DAOException {
        if (location == null) {
            throw new IllegalArgumentException("Null location");
        }
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            Object[] parameters = {
                location.getName(),
                location.getAddress(),
                location.isSalePoint()
            };
            ps = sqlStatements.buildSQLStatement(conn, ADD_LOCATION_KEY, parameters);
            ps.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot add Location", e);
        } catch (IOException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot add Location", e);
        } finally {
            DBManager.closeStatement(ps);
            DBManager.closeConnection(conn);
        }
    }

    @Override
    public void deleteLocation(int id) throws DAOException {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            Object[] parameters = {id};
            ps = sqlStatements.buildSQLStatement(conn, DELETE_LOCATION_KEY, parameters);
            ps.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot delete the location", e);
        } catch (IOException e) {
            throw new DAOException("Cannot delete the location", e);
        } finally {
            DBManager.closeStatement(ps);
            DBManager.closeConnection(conn);
        }
    }

    @Override
    public void updateLocation(Location location) throws DAOException {
        Connection conn = null;
        PreparedStatement update = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            Object[] parameters = {
                location.getName(),
                location.getAddress(),
                location.isSalePoint(),
                location.getId()
            };
            update = sqlStatements.buildSQLStatement(conn, UPDATE_LOCATION_KEY, parameters);
            update.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Could not properly update the location", e);
        } catch (IOException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Could not properly update the location", e);
        } finally {
            DBManager.closeStatement(update);
            DBManager.closeConnection(conn);
        }
    }

}
