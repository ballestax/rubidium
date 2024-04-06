/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rb.persistence.JDBC;


import com.rb.DBManager;
import com.rb.domain.Product;
import com.rb.persistence.SQLExtractor;
import com.rb.persistence.SQLLoader;
import com.rb.persistence.dao.DAOException;
import com.rb.persistence.dao.ProductDAO;
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
public class JDBCProductDAO implements ProductDAO {
    
    public static final String TABLE_NAME = "products";
    public static final String NAMED_PARAM_WHERE = "{where}";
    public static final String NAMED_PARAM_ORDER_BY = "{orderby}";
    private static final Logger logger = Logger.getLogger(JDBCProductDAO.class.getCanonicalName());
    private final DataSource dataSource;
    private final SQLLoader sqlStatements;
    protected static final String CREATE_PRODUCTS_TABLE_KEY = "CREATE_PRODUCTS_TABLE";
    protected static final String ADD_PRODUCT_KEY = "ADD_PRODUCT";
    protected static final String UPDATE_PRODUCT_KEY = "UPDATE_PRODUCT";
    protected static final String GET_PRODUCT_KEY = "GET_PRODUCT";
    protected static final String DELETE_PRODUCT_KEY = "DELETE_PRODUCT";
    protected static final String GET_PRODUCT_BY_PRESS_ID_KEY = "GET_PRODUCT_BY_PRESS_ID";
    
    public JDBCProductDAO(DataSource dataSource, SQLLoader sqlStatements) throws DAOException {
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
            ps = sqlStatements.buildSQLStatement(conn, CREATE_PRODUCTS_TABLE_KEY);
            ps.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot create Product table", e);
        } catch (IOException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot create Product table", e);
        } finally {
            DBManager.closeStatement(ps);
            DBManager.closeConnection(conn);
        }
    }
    
    public Product getProductBy(String query) throws DAOException {
        String retrieveImporter;
        try {
            SQLExtractor sqlExtractorWhere = new SQLExtractor(query, SQLExtractor.Type.WHERE);;
            Map<String, String> namedParams = new HashMap<String, String>();
            namedParams.put(NAMED_PARAM_WHERE, sqlExtractorWhere.extractWhere());
            retrieveImporter = sqlStatements.getSQLString(GET_PRODUCT_KEY, namedParams);
            
        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the producto", e);
        } catch (IOException e) {
            throw new DAOException("Could not properly retrieve the producto", e);
        }
        Connection conn = null;
        PreparedStatement retrieve = null;
        ResultSet rs = null;
        Product producto = null;
        try {
            conn = dataSource.getConnection();
            retrieve = conn.prepareStatement(retrieveImporter);
            rs = retrieve.executeQuery();
            while (rs.next()) {
                producto = new Product();
                producto.setId(rs.getInt("id"));
                producto.setName(rs.getString("name"));
                producto.setCode(rs.getString("code"));                
                producto.setDescription(rs.getString("description"));
                producto.setPrice(rs.getBigDecimal("price").doubleValue());
                producto.setImage(rs.getString("image"));
                producto.setCategory(rs.getString("category"));
                producto.setVariablePrice(rs.getBoolean("variable"));
                producto.setEnabled(rs.getBoolean("enabled"));
            }
        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the Product: " + e);
        } finally {
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(retrieve);
            DBManager.closeConnection(conn);
        }
        return producto;
    }
    
    @Override
    public Product getProduct(int id) throws DAOException {
        return getProductBy("id=" + id);
    }
    
    @Override
    public ArrayList<Product> getProductList() throws DAOException {
        return getProductList("", "");
    }
    
    public ArrayList<Product> getProductList(String where, String orderBy) throws DAOException {
        String retrieveProducts;
        ArrayList<Product> productos = new ArrayList<>();
        try {
            SQLExtractor sqlExtractorWhere = new SQLExtractor(where, SQLExtractor.Type.WHERE);
            SQLExtractor sqlExtractorOrderBy = new SQLExtractor(orderBy, SQLExtractor.Type.ORDER_BY);
            Map<String, String> namedParams = new HashMap<>();
            namedParams.put(NAMED_PARAM_WHERE, sqlExtractorWhere.extractWhere());
            namedParams.put(NAMED_PARAM_ORDER_BY, sqlExtractorOrderBy.extractOrderBy());
            retrieveProducts = sqlStatements.getSQLString(GET_PRODUCT_KEY, namedParams);
            
        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the Product List", e);
        } catch (IOException e) {
            throw new DAOException("Could not properly retrieve the Product List", e);
        }
        
        Connection conn = null;
        PreparedStatement retrieve = null;
        ResultSet rs = null;
        Product producto = null;
        try {
            conn = dataSource.getConnection();
            retrieve = conn.prepareStatement(retrieveProducts);
            rs = retrieve.executeQuery();
            
            while (rs.next()) {
                producto = new Product();
                producto.setId(rs.getInt("id"));
                producto.setName(rs.getString("name"));
                producto.setCode(rs.getString("code"));                
                producto.setDescription(rs.getString("description"));
                producto.setPrice(rs.getBigDecimal("price").doubleValue());
                producto.setImage(rs.getString("image"));
                producto.setCategory(rs.getString("category"));
                producto.setVariablePrice(rs.getBoolean("variable"));
                producto.setEnabled(rs.getBoolean("enabled"));             
                productos.add(producto);
            }
        } catch (SQLException e) {
            throw new DAOException("Could not proper retrieve the Product: " + e);
        } finally {
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(retrieve);
            DBManager.closeConnection(conn);
        }
        return productos;
    }
    
    @Override
    public void addProduct(Product producto) throws DAOException {
        if (producto == null) {
            throw new IllegalArgumentException("Null producto");
        }
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            Object[] parameters = {
                producto.getName(),
                producto.getCode(),
                producto.getDescription(),
                producto.getPrice(),
                producto.getImage(),
                producto.getCategory(),
                producto.isVariablePrice(),
                producto.isEnabled()
            };
            ps = sqlStatements.buildSQLStatement(conn, ADD_PRODUCT_KEY, parameters);
            
            ps.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot add Product", e);
        } catch (IOException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot add Product", e);
        } finally {
            DBManager.closeStatement(ps);
            DBManager.closeConnection(conn);
        }
    }
    
    @Override
    public void deleteProduct(int id) throws DAOException {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            Object[] parameters = {id};
            ps = sqlStatements.buildSQLStatement(conn, DELETE_PRODUCT_KEY, parameters);
            ps.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot delete the producto", e);
        } catch (IOException e) {
            throw new DAOException("Cannot delete the producto", e);
        } finally {
            DBManager.closeStatement(ps);
            DBManager.closeConnection(conn);
        }
    }
    
    @Override
    public void updateProduct(Product producto) throws DAOException {
        Connection conn = null;
        PreparedStatement update = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            Object[] parameters = {
                producto.getName(),
                producto.getCode(),
                producto.getDescription(),
                producto.getPrice(),
                producto.getImage(),
                producto.getCategory(),
                producto.isVariablePrice(),
                producto.isEnabled(),
                producto.getId()
            };
            update = sqlStatements.buildSQLStatement(conn, UPDATE_PRODUCT_KEY, parameters);
            update.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Could not properly update the producto", e);
        } catch (IOException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Could not properly update the producto", e);
        } finally {
            DBManager.closeStatement(update);
            DBManager.closeConnection(conn);
        }
    }
    
    public ArrayList<Product> getProductByQuery(String query) throws DAOException {
        String retrieve = query;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Product producto = null;
        ArrayList<Product> productos = new ArrayList<>();
        try {
            conn = dataSource.getConnection();
            ps = conn.prepareStatement(retrieve);
            rs = ps.executeQuery();
            while (rs.next()) {
                producto = new Product();
                producto.setId(rs.getInt("id"));
                producto.setName(rs.getString("name"));
                producto.setCode(rs.getString("code"));                
                producto.setDescription(rs.getString("description"));
                producto.setPrice(rs.getBigDecimal("price").doubleValue());
                producto.setImage(rs.getString("image"));
                producto.setCategory(rs.getString("category"));
                producto.setVariablePrice(rs.getBoolean("variable"));
                producto.setEnabled(rs.getBoolean("enabled"));
                productos.add(producto);
            }
        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the Product: " + e);
        } finally {
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(ps);
            DBManager.closeConnection(conn);
        }
        return productos;
    }
    
    public Product getProductByPressID(String where) throws DAOException {
        String stRetrieve;
        try {
            SQLExtractor sqlExtractorWhere = new SQLExtractor(where, SQLExtractor.Type.WHERE);;
            Map<String, String> namedParams = new HashMap<String, String>();
            namedParams.put(NAMED_PARAM_WHERE, sqlExtractorWhere.extractWhere());
            stRetrieve = sqlStatements.getSQLString(GET_PRODUCT_BY_PRESS_ID_KEY, namedParams);            
        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the producto", e);
        } catch (IOException e) {
            throw new DAOException("Could not properly retrieve the producto", e);
        }
        Connection conn = null;
        PreparedStatement retrieve = null;
        ResultSet rs = null;
        Product producto = null;
        try {
            conn = dataSource.getConnection();
            retrieve = conn.prepareStatement(stRetrieve);
            rs = retrieve.executeQuery();
            while (rs.next()) {
                producto = new Product();
                producto.setId(rs.getInt("id"));
                producto.setName(rs.getString("name"));
                producto.setCode(rs.getString("code"));                
                producto.setDescription(rs.getString("description"));
                producto.setPrice(rs.getBigDecimal("price").doubleValue());
                producto.setImage(rs.getString("image"));
                producto.setCategory(rs.getString("category"));
                producto.setVariablePrice(rs.getBoolean("variable"));
                producto.setEnabled(rs.getBoolean("enabled"));
            }
        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the Product: " + e);
        } finally {
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(retrieve);
            DBManager.closeConnection(conn);
        }
        return producto;
    }
    
}

