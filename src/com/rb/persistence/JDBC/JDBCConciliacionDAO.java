
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rb.persistence.JDBC;

import com.rb.DBManager;
import com.rb.domain.Conciliacion;
import com.rb.persistence.SQLExtractor;
import com.rb.persistence.SQLLoader;
import com.rb.persistence.dao.ConciliacionDAO;
import com.rb.persistence.dao.DAOException;
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
import org.apache.log4j.Logger;

/**
 *
 * @author LuisR
 */
public class JDBCConciliacionDAO implements ConciliacionDAO {

    public static final String TABLE_NAME = "conciliaciones";
    public static final String NAMED_PARAM_WHERE = "{where}";
    public static final String NAMED_PARAM_ORDER_BY = "{orderby}";
    private static final Logger logger = Logger.getLogger(JDBCConciliacionDAO.class.getCanonicalName());
    private final DataSource dataSource;
    private final SQLLoader sqlStatements;
    protected static final String CREATE_CONCILIACIONES_TABLE_KEY = "CREATE_CONCILIACIONES_TABLE";
    protected static final String INSERT_CONCILIACION_KEY = "ADD_CONCILIACION";
    protected static final String UPDATE_CONCILIACION_KEY = "UPDATE_CONCILIACION";
    protected static final String GET_CONCILIACION_KEY = "GET_CONCILIACION";
    protected static final String DELETE_CONCILIACION_KEY = "DELETE_CONCILIACION";

    public JDBCConciliacionDAO(BasicDataSource dataSource, SQLLoader sqlStatements) throws DAOException {
        this.dataSource = dataSource;
        this.sqlStatements = sqlStatements;
    }

    public final void init() throws DAOException, RemoteException {
        // Create the conciliacions table if it does not already exist
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            if (DBManager.tableExists(TABLE_NAME, conn)) {
                return;
            }
            ps = sqlStatements.buildSQLStatement(conn, CREATE_CONCILIACIONES_TABLE_KEY);
            ps.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot create Conciliaciones table", e);
        } catch (IOException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot create Conciliaciones table", e);
        } finally {
            DBManager.closeStatement(ps);
            DBManager.closeConnection(conn);
        }
    }

    public Conciliacion getConciliacionBy(String query) throws DAOException {
        String retrieveImporter;
        try {
            SQLExtractor sqlExtractorWhere = new SQLExtractor(query, SQLExtractor.Type.WHERE);;
            Map<String, String> namedParams = new HashMap<String, String>();
            namedParams.put(NAMED_PARAM_WHERE, sqlExtractorWhere.extractWhere());
            retrieveImporter = sqlStatements.getSQLString(GET_CONCILIACION_KEY, namedParams);

        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the Conciliacion", e);
        } catch (IOException e) {
            throw new DAOException("Could not properly retrieve the Conciliacion", e);
        }
        Connection conn = null;
        PreparedStatement retrieve = null;
        ResultSet rs = null;
        Conciliacion conciliacion = null;
        try {
            conn = dataSource.getConnection();
            retrieve = conn.prepareStatement(retrieveImporter);
            rs = retrieve.executeQuery();
            while (rs.next()) {
                conciliacion = new Conciliacion();
                conciliacion.setId(rs.getInt(1));
                conciliacion.setCodigo(rs.getString(2));
                conciliacion.setFecha(rs.getDate(3));
                conciliacion.setIdItem(rs.getInt(4));
                conciliacion.setExistencias(rs.getDouble(5));
                conciliacion.setConciliacion(rs.getDouble(6));
                conciliacion.setLocacion(rs.getInt(7));
                conciliacion.setNota(rs.getString(8));
                conciliacion.setUpdateTime(rs.getDate(9));
                conciliacion.setUser(rs.getString(10));
            }
        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the conciliacion: " + e);
        } finally {
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(retrieve);
            DBManager.closeConnection(conn);
        }
        return conciliacion;
    }

    @Override
    public Conciliacion getConciliacion(int id) throws DAOException {
        return getConciliacionBy("id=" + id);
    }

    @Override
    public ArrayList<Conciliacion> getConciliacionList() throws DAOException {
        return getConciliacionList("", "");
    }

    public ArrayList<Conciliacion> getConciliacionList(String where, String orderBy) throws DAOException {
        String retrieveCategory;
        ArrayList<Conciliacion> conciliaciones = new ArrayList<>();
        try {
            SQLExtractor sqlExtractorWhere = new SQLExtractor(where, SQLExtractor.Type.WHERE);;
            SQLExtractor sqlExtractorOrderBy = new SQLExtractor(orderBy, SQLExtractor.Type.ORDER_BY);;
            Map<String, String> namedParams = new HashMap<>();
            namedParams.put(NAMED_PARAM_WHERE, sqlExtractorWhere.extractWhere());
            namedParams.put(NAMED_PARAM_ORDER_BY, sqlExtractorOrderBy.extractOrderBy());
            retrieveCategory = sqlStatements.getSQLString(GET_CONCILIACION_KEY, namedParams);

        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the Conciliacion List", e);
        } catch (IOException e) {
            throw new DAOException("Could not properly retrieve the Conciliacion List", e);
        }

        Connection conn = null;
        PreparedStatement retrieve = null;
        ResultSet rs = null;
        Conciliacion conciliacion = null;
        try {
            conn = dataSource.getConnection();
            retrieve = conn.prepareStatement(retrieveCategory);
            rs = retrieve.executeQuery();

            while (rs.next()) {
                conciliacion = new Conciliacion();
                conciliacion.setId(rs.getInt(1));
                conciliacion.setCodigo(rs.getString(2));
                conciliacion.setFecha(rs.getTimestamp(3));
                conciliacion.setIdItem(rs.getInt(4));
                conciliacion.setExistencias(rs.getDouble(5));
                conciliacion.setConciliacion(rs.getDouble(6));
                conciliacion.setLocacion(rs.getInt(7));
                conciliacion.setNota(rs.getString(8));
                conciliacion.setUpdateTime(rs.getTimestamp(9));
                conciliacion.setUser(rs.getString(10));
                conciliaciones.add(conciliacion);
            }
        } catch (SQLException e) {
            throw new DAOException("Could not proper retrieve the Conciliacion: " + e);
        } finally {
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(retrieve);
            DBManager.closeConnection(conn);
        }
        return conciliaciones;
    }

    @Override
    public void addConciliacion(Conciliacion conciliacion) throws DAOException {
        if (conciliacion == null) {
            throw new IllegalArgumentException("Null conciliacion");
        }
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            Object[] parameters = {
                conciliacion.getCodigo(),
                conciliacion.getFecha(),
                conciliacion.getIdItem(),
                conciliacion.getExistencias(),
                conciliacion.getConciliacion(),
                conciliacion.getLocacion(),
                conciliacion.getNota(),
                conciliacion.getUser()
            };
            ps = sqlStatements.buildSQLStatement(conn, INSERT_CONCILIACION_KEY, parameters);
            ps.executeUpdate();

            double res = conciliacion.getConciliacion() - conciliacion.getExistencias();
            Object[] parameterx = {
                res,
                conciliacion.getIdItem()
            };
            ps = sqlStatements.buildSQLStatement(conn, JDBCUtilDAO.ADD_INVENTORY_QUANTITY_KEY, parameterx);
            ps.executeUpdate();

            conn.commit();
        } catch (SQLException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot add conciliacion", e);
        } catch (IOException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot add conciliacion", e);
        } finally {
            DBManager.closeStatement(ps);
            DBManager.closeConnection(conn);
        }
    }

    @Override
    public void deleteConciliacion(int id) throws DAOException {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            Object[] parameters = {id};
            ps = sqlStatements.buildSQLStatement(conn, DELETE_CONCILIACION_KEY, parameters);
            ps.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot delete the conciliacion", e);
        } catch (IOException e) {
            throw new DAOException("Cannot delete the conciliacion", e);
        } finally {
            DBManager.closeStatement(ps);
            DBManager.closeConnection(conn);
        }
    }

    @Override
    public void updateConciliacion(Conciliacion conciliacion) throws DAOException {
        Connection conn = null;
        PreparedStatement update = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            Object[] parameters = {
                conciliacion.getCodigo(),
                conciliacion.getFecha(),
                conciliacion.getIdItem(),
                conciliacion.getExistencias(),
                conciliacion.getConciliacion(),
                conciliacion.getLocacion(),
                conciliacion.getNota(),
                conciliacion.getUser(),
                conciliacion.getId()
            };
            update = sqlStatements.buildSQLStatement(conn, UPDATE_CONCILIACION_KEY, parameters);
            update.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Could not properly update the conciliacion", e);
        } catch (IOException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Could not properly update the conciliacion", e);
        } finally {
            DBManager.closeStatement(update);
            DBManager.closeConnection(conn);
        }
    }

}
