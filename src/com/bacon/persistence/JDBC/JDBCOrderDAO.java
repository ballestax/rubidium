/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bacon.persistence.JDBC;

import com.bacon.DBManager;
import com.bacon.domain.Additional;
import com.bacon.domain.AdditionalPed;
import com.bacon.domain.Ingredient;
import com.bacon.domain.Order;
import com.bacon.domain.Presentation;
import com.bacon.domain.Product;
import com.bacon.domain.ProductoPed;
import com.bacon.gui.PanelPedido;
import static com.bacon.persistence.JDBC.JDBCUtilDAO.GET_MAX_ID_KEY;
import com.bacon.persistence.SQLExtractor;
import com.bacon.persistence.SQLLoader;
import com.bacon.persistence.dao.DAOException;
import com.bacon.persistence.dao.OrderDAO;
import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 *
 * @author LuisR
 */
public class JDBCOrderDAO implements OrderDAO {

    public static final String TABLE_NAME = "orders";
    public static final String NAMED_PARAM_WHERE = "{where}";
    public static final String NAMED_PARAM_ORDER_BY = "{orderby}";
    public static final String NAMED_PARAM_START = "{start}";
    public static final String NAMED_PARAM_END = "{end}";
    public static final String NAMED_PARAM_NUM = "{num}";
    private static final Logger logger = Logger.getLogger(JDBCOrderDAO.class.getCanonicalName());
    private final DataSource dataSource;
    private final SQLLoader sqlStatements;
    protected static final String CREATE_ORDERS_TABLE_KEY = "CREATE_ORDERS_TABLE";
    protected static final String ADD_ORDER_KEY = "ADD_ORDER";
    protected static final String UPDATE_ORDER_KEY = "UPDATE_ORDER";
    protected static final String GET_ORDER_KEY = "GET_ORDER";
    protected static final String DELETE_ORDER_KEY = "DELETE_ORDER";
    protected static final String ADD_ORDER_PRODUCT_KEY = "ADD_ORDER_PRODUCT";
    protected static final String GET_ORDER_PRODUCT_KEY = "GET_ORDER_PRODUCT";
    protected static final String GET_PRESENTATION_KEY = "GET_PRESENTATION";
    protected static final String GET_ADDITIONAL_PRODUCT_KEY = "GET_ADDITIONAL_PRODUCT";
    protected static final String GET_EXCLUSION_PRODUCT_KEY = "GET_EXCLUSION_PRODUCT";
    protected static final String ADD_ORDER_OTHER_PRODUCT_KEY = "ADD_ORDER_OTHER_PRODUCT";
    protected static final String GET_ORDER_BY_PRODUCT_KEY = "GET_ORDER_BY_PRODUCT";

    public JDBCOrderDAO(DataSource dataSource, SQLLoader sqlStatements) throws DAOException {
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
            ps = sqlStatements.buildSQLStatement(conn, CREATE_ORDERS_TABLE_KEY);
            ps.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot create Order table", e);
        } catch (IOException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot create Order table", e);
        } finally {
            DBManager.closeStatement(ps);
            DBManager.closeConnection(conn);
        }
    }

    public Order getOrderBy(String query) throws DAOException {
        String retrieveProd;
        try {
            SQLExtractor sqlExtractorWhere = new SQLExtractor(query, SQLExtractor.Type.WHERE);
            Map<String, String> namedParams = new HashMap<String, String>();
            namedParams.put(NAMED_PARAM_WHERE, sqlExtractorWhere.extractWhere());
            retrieveProd = sqlStatements.getSQLString(GET_ORDER_KEY, namedParams);

        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the order", e);
        } catch (IOException e) {
            throw new DAOException("Could not properly retrieve the order", e);
        }
        Connection conn = null;
        PreparedStatement retrieve = null;
        ResultSet rs = null, rs1 = null, rs2 = null, rs3 = null, rsx = null;
        Order order = null;
        try {
            conn = dataSource.getConnection();
            retrieve = conn.prepareStatement(retrieveProd);
            rs = retrieve.executeQuery();
            while (rs.next()) {
                order = new Order();
                order.setId(rs.getLong("id"));
                order.setFecha(rs.getTimestamp("take_date"));
                order.setValor(rs.getBigDecimal("value"));
                order.setIdClient(rs.getLong("idClient"));
                order.setIdWaitress(rs.getInt("idWaiter"));
                order.setTable(rs.getInt("idTable"));
                order.setCiclo(rs.getLong("idCycle"));
                order.setNota(rs.getString("notes"));
                order.setDeliveryType(rs.getInt("deliveryType"));
                order.setConsecutive(rs.getString("consecutive"));
                order.setStatus(rs.getInt("status"));

                Object[] parameters = {order.getId()};

                try {
                    retrieve = sqlStatements.buildSQLStatement(conn, GET_ORDER_PRODUCT_KEY, parameters);
                    rs1 = retrieve.executeQuery();
                    Product product;
                    while (rs1.next()) {
                        product = new Product();
                        product.setId(rs1.getInt(1));
                        product.setName(rs1.getString(2));
                        product.setCode(rs1.getString(3));
                        product.setDescription(rs1.getString(4));
                        product.setPrice(rs1.getBigDecimal(5).doubleValue());
                        product.setImage(rs1.getString(6));
                        product.setCategory(rs1.getString(7));
                        product.setVariablePrice(rs1.getBoolean(8));

                        ProductoPed productoPed = new ProductoPed(product);
                        productoPed.setPrecio(rs1.getBigDecimal(9).doubleValue());
                        productoPed.setCantidad(rs1.getInt(10));
                        productoPed.setDelivery(rs1.getBoolean(13));
                        productoPed.setTermino(rs1.getString(14));
                        productoPed.setEntry(rs1.getBoolean(15));
                        productoPed.setEspecificaciones(rs1.getString(16));
                        productoPed.setStatus(rs1.getInt(17));
                        productoPed.setStations(rs1.getString(18));

                        int idProdPed = rs1.getInt(11);
                        int idPresentation = rs1.getInt(12);

                        Object[] parameters1 = {idPresentation};
                        try {
                            retrieve = sqlStatements.buildSQLStatement(conn, GET_PRESENTATION_KEY, parameters1);
                            rsx = retrieve.executeQuery();
                            Presentation pres;
                            while (rsx.next()) {
                                pres = new Presentation();
                                pres.setId(rsx.getInt(1));
                                pres.setIDProd(rsx.getInt(2));
                                pres.setSerie(rsx.getInt(3));
                                pres.setName(rsx.getString(4));
                                pres.setPrice(rsx.getDouble(5));
                                pres.setDefault(rsx.getBoolean(6));
                                pres.setEnabled(rs.getBoolean(7));
                                productoPed.setPresentation(pres);
                            }
                        } catch (SQLException e) {
                            System.out.println(e.getMessage());
                            throw new DAOException("Could not properly retrieve the presentation " + e);
                        } catch (IOException e) {
                            System.out.println(e.getMessage());
                            throw new DAOException("Could not properly retrieve the presentation: " + e);
                        }

                        Object[] parameters2 = {idProdPed};

                        try {
                            retrieve = sqlStatements.buildSQLStatement(conn, GET_ADDITIONAL_PRODUCT_KEY, parameters2);
                            rs2 = retrieve.executeQuery();
                            Additional addition;
                            while (rs2.next()) {

                                addition = new Additional();
                                addition.setId(rs2.getInt(1));
                                addition.setName(rs2.getString(2));
                                addition.setMeasure(rs2.getString(4));
                                addition.setPrecio(rs2.getBigDecimal(5).doubleValue());

                                int cant = rs2.getInt(6);

                                productoPed.addAdicional(addition, cant);
                            }
                        } catch (SQLException e) {
                            throw new DAOException("Could not properly retrieve the additional " + e);
                        } catch (IOException e) {
                            throw new DAOException("Could not properly retrieve the additional: " + e);

                        }

                        try {
                            retrieve = sqlStatements.buildSQLStatement(conn, GET_EXCLUSION_PRODUCT_KEY, parameters2);
                            rs3 = retrieve.executeQuery();
                            Ingredient ingredient;
                            while (rs3.next()) {

                                ingredient = new Ingredient();
                                ingredient.setId(rs3.getInt(1));
                                ingredient.setName(rs3.getString(2));
                                ingredient.setCode(rs3.getString(3));
                                ingredient.setMeasure(rs3.getString(4));

                                productoPed.addExclusion(ingredient);
                            }
                        } catch (SQLException e) {
                            throw new DAOException("Could not properly retrieve the additional " + e);
                        } catch (IOException e) {
                            throw new DAOException("Could not properly retrieve the additional: " + e);

                        }

                        order.addProduct(productoPed);
                    }
                } catch (SQLException e) {
                    throw new DAOException("Could not properly retrieve the product " + e);
                } catch (IOException e) {
                    throw new DAOException("Could not properly retrieve the product: " + e);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the Order: " + e);
        } finally {
            DBManager.closeResultSet(rs3);
            DBManager.closeResultSet(rs2);
            DBManager.closeResultSet(rsx);
            DBManager.closeResultSet(rs1);
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(retrieve);
            DBManager.closeConnection(conn);
        }
        return order;
    }

    @Override
    public Order getOrder(int id) throws DAOException {
        return getOrderBy("id=" + id);
    }

    @Override
    public ArrayList<Order> getOrderList() throws DAOException {
        return getOrderList("", "");
    }

    public ArrayList<Order> getOrderLiteList(String where, String orderBy, int startEntry, int endEntry) throws DAOException {

        if (startEntry < 0) {
            startEntry = 0;
        }

        if (endEntry < 0) {
            endEntry = 0;
        }

        if (endEntry <= startEntry) {
            return new ArrayList<Order>();
        }

        String retrieveProd;
        ArrayList<Order> orders = new ArrayList<>();
        try {
            SQLExtractor sqlExtractorWhere = new SQLExtractor(where, SQLExtractor.Type.WHERE);
            SQLExtractor sqlExtractorOrderBy = new SQLExtractor(orderBy, SQLExtractor.Type.ORDER_BY);
            Map<String, String> namedParams = new HashMap<>();
            namedParams.put(NAMED_PARAM_WHERE, sqlExtractorWhere.extractWhere());
            namedParams.put(NAMED_PARAM_ORDER_BY, sqlExtractorOrderBy.extractOrderBy());
            namedParams.put(NAMED_PARAM_START, String.valueOf(startEntry));
            namedParams.put(NAMED_PARAM_END, String.valueOf(endEntry));
            namedParams.put(NAMED_PARAM_NUM, String.valueOf(endEntry - startEntry));

            retrieveProd = sqlStatements.getSQLString(GET_ORDER_KEY, namedParams);

        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the Order List", e);
        } catch (IOException e) {
            throw new DAOException("Could not properly retrieve the Order List", e);
        }

        Connection conn = null;
        PreparedStatement retrieve = null;
        ResultSet rs = null;
        Order order = null;
        try {
            conn = dataSource.getConnection();
            retrieve = conn.prepareStatement(retrieveProd);
            rs = retrieve.executeQuery();
            while (rs.next()) {
                order = new Order();
                order.setId(rs.getLong("id"));
                order.setFecha(rs.getTimestamp("take_date"));
                order.setValor(rs.getBigDecimal("value"));
                order.setIdClient(rs.getLong("idClient"));
                order.setIdWaitress(rs.getInt("idWaiter"));
                order.setTable(rs.getInt("idTable"));
                order.setCiclo(rs.getLong("idCycle"));
                order.setNota(rs.getString("notes"));
                order.setDeliveryType(rs.getInt("deliveryType"));
                order.setConsecutive(rs.getString("consecutive"));
                order.setStatus(rs.getInt("status"));
                orders.add(order);
            }
        } catch (SQLException e) {
            throw new DAOException("Could not proper retrieve the Order: " + e);
        } finally {
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(retrieve);
            DBManager.closeConnection(conn);
        }
        return orders;
    }

    public ArrayList<Order> getOrderLiteList(String where, String orderBy) throws DAOException {

        String retrieveProd;
        ArrayList<Order> orders = new ArrayList<>();
        try {
            SQLExtractor sqlExtractorWhere = new SQLExtractor(where, SQLExtractor.Type.WHERE);
            SQLExtractor sqlExtractorOrderBy = new SQLExtractor(orderBy, SQLExtractor.Type.ORDER_BY);
            Map<String, String> namedParams = new HashMap<>();
            namedParams.put(NAMED_PARAM_WHERE, sqlExtractorWhere.extractWhere());
            namedParams.put(NAMED_PARAM_ORDER_BY, sqlExtractorOrderBy.extractOrderBy());

            retrieveProd = sqlStatements.getSQLString(GET_ORDER_KEY, namedParams);

        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the Order List", e);
        } catch (IOException e) {
            throw new DAOException("Could not properly retrieve the Order List", e);
        }

        Connection conn = null;
        PreparedStatement retrieve = null;
        ResultSet rs = null;
        Order order = null;
        try {
            conn = dataSource.getConnection();
            retrieve = conn.prepareStatement(retrieveProd);
            rs = retrieve.executeQuery();
            while (rs.next()) {
                order = new Order();
                order.setId(rs.getLong("id"));
                order.setFecha(rs.getTimestamp("take_date"));
                order.setValor(rs.getBigDecimal("value"));
                order.setIdClient(rs.getLong("idClient"));
                order.setIdWaitress(rs.getInt("idWaiter"));
                order.setTable(rs.getInt("idTable"));
                order.setCiclo(rs.getLong("idCycle"));
                order.setNota(rs.getString("notes"));
                order.setDeliveryType(rs.getInt("deliveryType"));
                order.setConsecutive(rs.getString("consecutive"));
                order.setStatus(rs.getInt("status"));
                orders.add(order);
            }
        } catch (SQLException e) {
            throw new DAOException("Could not proper retrieve the Order: " + e);
        } finally {
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(retrieve);
            DBManager.closeConnection(conn);
        }
        return orders;
    }

    public ArrayList<Order> getOrderList(String where, String orderBy) throws DAOException {
        String retrieveProd;
        ArrayList<Order> orders = new ArrayList<>();
        try {
            SQLExtractor sqlExtractorWhere = new SQLExtractor(where, SQLExtractor.Type.WHERE);
            SQLExtractor sqlExtractorOrderBy = new SQLExtractor(orderBy, SQLExtractor.Type.ORDER_BY);
            Map<String, String> namedParams = new HashMap<>();
            namedParams.put(NAMED_PARAM_WHERE, sqlExtractorWhere.extractWhere());
            namedParams.put(NAMED_PARAM_ORDER_BY, sqlExtractorOrderBy.extractOrderBy());
            retrieveProd = sqlStatements.getSQLString(GET_ORDER_KEY, namedParams);

        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the Order List", e);
        } catch (IOException e) {
            throw new DAOException("Could not properly retrieve the Order List", e);
        }

        Connection conn = null;
        PreparedStatement retrieve = null;
        ResultSet rs = null, rs1 = null, rs2 = null, rs3 = null, rsx = null;
        Order order = null;
        try {
            conn = dataSource.getConnection();
            retrieve = conn.prepareStatement(retrieveProd);
            rs = retrieve.executeQuery();
            while (rs.next()) {
                order = new Order();
                order.setId(rs.getLong("id"));
                order.setFecha(rs.getTimestamp("take_date"));
                order.setValor(rs.getBigDecimal("value"));
                order.setIdClient(rs.getLong("idClient"));
                order.setIdWaitress(rs.getInt("idWaiter"));
                order.setTable(rs.getInt("idTable"));
                order.setCiclo(rs.getLong("idCycle"));
                order.setNota(rs.getString("notes"));
                order.setDeliveryType(rs.getInt("deliveryType"));
                order.setConsecutive(rs.getString("consecutive"));
                order.setStatus(rs.getInt("status"));

                Object[] parameters = {order.getId()};

                try {
                    retrieve = sqlStatements.buildSQLStatement(conn, GET_ORDER_PRODUCT_KEY, parameters);
                    rs1 = retrieve.executeQuery();
                    Product product;
                    while (rs1.next()) { //add products
                        product = new Product();
                        product.setId(rs1.getInt(1));
                        product.setName(rs1.getString(2));
                        product.setCode(rs1.getString(3));
                        product.setDescription(rs1.getString(4));
                        product.setPrice(rs1.getBigDecimal(5).doubleValue());
                        product.setImage(rs1.getString(6));
                        product.setCategory(rs1.getString(7));
                        product.setVariablePrice(rs1.getBoolean(8));
                        ProductoPed productoPed = new ProductoPed(product);

                        productoPed.setPrecio(rs1.getBigDecimal(9).doubleValue());
                        productoPed.setCantidad(rs1.getInt(10));
                        productoPed.setDelivery(rs1.getBoolean(13));
                        productoPed.setTermino(rs1.getString(14));
                        productoPed.setEntry(rs1.getBoolean(15));
                        productoPed.setEspecificaciones(rs1.getString(16));
                        productoPed.setStatus(rs1.getInt(17));
                        productoPed.setStations(rs1.getString(18));

                        int idProdPed = rs1.getInt(11);

                        Object[] parameters2 = {idProdPed};
                        int idPresentation = rs1.getInt(12);

                        Object[] parameters1 = {idPresentation};
                        try {
                            retrieve = sqlStatements.buildSQLStatement(conn, GET_PRESENTATION_KEY, parameters1);
                            rsx = retrieve.executeQuery();
                            Presentation pres = null;
                            while (rsx.next()) {
                                pres = new Presentation();
                                pres.setId(rsx.getInt(1));
                                pres.setIDProd(rsx.getInt(2));
                                pres.setSerie(rsx.getInt(3));
                                pres.setName(rsx.getString(4));
                                pres.setPrice(rsx.getDouble(5));
                                pres.setDefault(rsx.getBoolean(6));
                                pres.setEnabled(rs.getBoolean(7));
                            }
                            productoPed.setPresentation(pres);
                        } catch (SQLException e) {
                            throw new DAOException("Could not properly retrieve the presentation " + e);
                        } catch (IOException e) {
                            throw new DAOException("Could not properly retrieve the presentation: " + e);
                        }

                        try {
                            retrieve = sqlStatements.buildSQLStatement(conn, GET_ADDITIONAL_PRODUCT_KEY, parameters2);
                            rs2 = retrieve.executeQuery();
                            Additional addition;
                            while (rs2.next()) {

                                addition = new Additional();
                                addition.setId(rs2.getInt(1));
                                addition.setName(rs2.getString(2));
                                addition.setMeasure(rs2.getString(4));
                                addition.setPrecio(rs2.getBigDecimal(5).doubleValue());

                                int cant = rs2.getInt(6);

                                productoPed.addAdicional(addition, cant);
                            }
                        } catch (SQLException e) {
                            throw new DAOException("Could not properly retrieve the additional " + e);
                        } catch (IOException e) {
                            throw new DAOException("Could not properly retrieve the additional: " + e);

                        }

                        try {
                            retrieve = sqlStatements.buildSQLStatement(conn, GET_EXCLUSION_PRODUCT_KEY, parameters2);
                            rs3 = retrieve.executeQuery();
                            Ingredient ingredient;
                            while (rs3.next()) {

                                ingredient = new Ingredient();
                                ingredient.setId(rs3.getInt(1));
                                ingredient.setName(rs3.getString(2));
                                ingredient.setCode(rs3.getString(3));
                                ingredient.setMeasure(rs3.getString(4));

                                productoPed.addExclusion(ingredient);
                            }
                        } catch (SQLException e) {
                            throw new DAOException("Could not properly retrieve the additional " + e);
                        } catch (IOException e) {
                            throw new DAOException("Could not properly retrieve the additional: " + e);

                        }

                        if (order.getProducts().contains(productoPed)) {
                            int idx = order.getProducts().indexOf(productoPed);
                            int cant = productoPed.getCantidad();
                            ProductoPed prod = order.getProducts().get(idx);
                            prod.setCantidad(prod.getCantidad() + cant);
                            order.getProducts().set(idx, prod);
                        } else {
                            order.addProduct(productoPed);
                        }
                    }
                } catch (SQLException e) {
                    throw new DAOException("Could not properly retrieve the product " + e);
                } catch (IOException e) {
                    throw new DAOException("Could not properly retrieve the product: " + e);
                }

                orders.add(order);
            }
        } catch (SQLException e) {
            throw new DAOException("Could not proper retrieve the Order: " + e);
        } finally {
            DBManager.closeResultSet(rs3);
            DBManager.closeResultSet(rs2);
            DBManager.closeResultSet(rsx);
            DBManager.closeResultSet(rs1);
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(retrieve);
            DBManager.closeConnection(conn);
        }
        return orders;
    }

    public ArrayList<ProductoPed> getOrderProducts(long orderId) throws DAOException {

        ArrayList<ProductoPed> products = new ArrayList<>();

        Connection conn = null;
        PreparedStatement retrieve = null;
        ResultSet rs = null, rs1 = null, rs2 = null, rs3 = null, rsx = null;

        try {
            conn = dataSource.getConnection();

            Object[] parameters = {orderId};

            try {
                retrieve = sqlStatements.buildSQLStatement(conn, GET_ORDER_PRODUCT_KEY, parameters);
                rs1 = retrieve.executeQuery();

                Product product;
                while (rs1.next()) { //add products
                    product = new Product();
                    product.setId(rs1.getInt(1));
                    product.setName(rs1.getString(2));
                    product.setCode(rs1.getString(3));
                    product.setDescription(rs1.getString(4));
                    product.setPrice(rs1.getBigDecimal(5).doubleValue());
                    product.setImage(rs1.getString(6));
                    product.setCategory(rs1.getString(7));
                    product.setVariablePrice(rs1.getBoolean(8));
                    ProductoPed productoPed = new ProductoPed(product);

                    productoPed.setPrecio(rs1.getBigDecimal(9).doubleValue());
                    productoPed.setCantidad(rs1.getInt(10));
                    productoPed.setDelivery(rs1.getBoolean(13));
                    productoPed.setTermino(rs1.getString(14));
                    productoPed.setEntry(rs1.getBoolean(15));
                    productoPed.setEspecificaciones(rs1.getString(16));
                    productoPed.setStatus(rs1.getInt(17));
                    productoPed.setStations(rs1.getString(18));

                    int idProdPed = rs1.getInt(11);

                    Object[] parameters2 = {idProdPed};
                    int idPresentation = rs1.getInt(12);

                    Object[] parameters1 = {idPresentation};
                    try {
                        retrieve = sqlStatements.buildSQLStatement(conn, GET_PRESENTATION_KEY, parameters1);
                        rsx = retrieve.executeQuery();
                        Presentation pres = null;
                        while (rsx.next()) {
                            pres = new Presentation();
                            pres.setId(rsx.getInt(1));
                            pres.setIDProd(rsx.getInt(2));
                            pres.setSerie(rsx.getInt(3));
                            pres.setName(rsx.getString(4));
                            pres.setPrice(rsx.getDouble(5));
                            pres.setDefault(rsx.getBoolean(6));
                            pres.setEnabled(rsx.getBoolean(7));
                        }
                        productoPed.setPresentation(pres);
                    } catch (SQLException e) {
                        throw new DAOException("Could not properly retrieve the presentation " + e);
                    } catch (IOException e) {
                        throw new DAOException("Could not properly retrieve the presentation: " + e);
                    }

                    try {
                        retrieve = sqlStatements.buildSQLStatement(conn, GET_ADDITIONAL_PRODUCT_KEY, parameters2);
                        rs2 = retrieve.executeQuery();
                        Additional addition;
                        while (rs2.next()) {

                            addition = new Additional();
                            addition.setId(rs2.getInt(1));
                            addition.setName(rs2.getString(2));
                            addition.setMeasure(rs2.getString(4));
                            addition.setPrecio(rs2.getBigDecimal(5).doubleValue());

                            int cant = rs2.getInt(6);

                            productoPed.addAdicional(addition, cant);
                        }
                    } catch (SQLException e) {
                        throw new DAOException("Could not properly retrieve the additional " + e);
                    } catch (IOException e) {
                        throw new DAOException("Could not properly retrieve the additional: " + e);

                    }

                    try {
                        retrieve = sqlStatements.buildSQLStatement(conn, GET_EXCLUSION_PRODUCT_KEY, parameters2);
                        rs3 = retrieve.executeQuery();
                        Ingredient ingredient;
                        while (rs3.next()) {

                            ingredient = new Ingredient();
                            ingredient.setId(rs3.getInt(1));
                            ingredient.setName(rs3.getString(2));
                            ingredient.setCode(rs3.getString(3));
                            ingredient.setMeasure(rs3.getString(4));

                            productoPed.addExclusion(ingredient);
                        }
                    } catch (SQLException e) {
                        throw new DAOException("Could not properly retrieve the additional " + e);
                    } catch (IOException e) {
                        throw new DAOException("Could not properly retrieve the additional: " + e);

                    }

                    if (products.contains(productoPed)) {
                        int idx = products.indexOf(productoPed);
                        int cant = productoPed.getCantidad();
                        ProductoPed prod = products.get(idx);
                        prod.setCantidad(prod.getCantidad() + cant);
                        products.set(idx, prod);
                    } else {
                        products.add(productoPed);
                    }
                }
            } catch (SQLException e) {
                throw new DAOException("Could not properly retrieve the product " + e);
            } catch (IOException e) {
                throw new DAOException("Could not properly retrieve the product: " + e);
            }

        } catch (SQLException e) {
            throw new DAOException("Could not proper retrieve the Order: " + e);
        } finally {
            DBManager.closeResultSet(rs3);
            DBManager.closeResultSet(rs2);
            DBManager.closeResultSet(rsx);
            DBManager.closeResultSet(rs1);
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(retrieve);
            DBManager.closeConnection(conn);
        }
        return products;
    }

    public ArrayList<Order> getOrderListWhitProducts(String where, String orderBy) throws DAOException {
        String retrieveProd;
        ArrayList<Order> orders = new ArrayList<>();
        try {
            SQLExtractor sqlExtractorWhere = new SQLExtractor(where, SQLExtractor.Type.WHERE);
            SQLExtractor sqlExtractorOrderBy = new SQLExtractor(orderBy, SQLExtractor.Type.ORDER_BY);
            Map<String, String> namedParams = new HashMap<>();
            namedParams.put(NAMED_PARAM_WHERE, sqlExtractorWhere.extractWhere());
            namedParams.put(NAMED_PARAM_ORDER_BY, sqlExtractorOrderBy.extractOrderBy());
            retrieveProd = sqlStatements.getSQLString(GET_ORDER_KEY, namedParams);

        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the Order List", e);
        } catch (IOException e) {
            throw new DAOException("Could not properly retrieve the Order List", e);
        }

        Connection conn = null;
        PreparedStatement retrieve = null;
        ResultSet rs = null, rs1 = null, rs2 = null, rs3 = null, rsx = null;
        Order order = null;
        try {
            conn = dataSource.getConnection();
            retrieve = conn.prepareStatement(retrieveProd);
            rs = retrieve.executeQuery();
            while (rs.next()) {
                order = new Order();
                order.setId(rs.getLong("id"));
                order.setFecha(rs.getTimestamp("take_date"));
                order.setValor(rs.getBigDecimal("value"));
                order.setIdClient(rs.getLong("idClient"));
                order.setIdWaitress(rs.getInt("idWaiter"));
                order.setTable(rs.getInt("idTable"));
                order.setCiclo(rs.getLong("idCycle"));
                order.setNota(rs.getString("notes"));
                order.setDeliveryType(rs.getInt("deliveryType"));
                order.setConsecutive(rs.getString("consecutive"));
                order.setStatus(rs.getInt("status"));

                order.addProduct(new ProductoPed(new Product(rs.getInt(18), rs.getString(19), 0, "")));

                orders.add(order);
            }
        } catch (SQLException e) {
            throw new DAOException("Could not proper retrieve the Order: " + e);
        } finally {
            DBManager.closeResultSet(rs3);
            DBManager.closeResultSet(rs2);
            DBManager.closeResultSet(rsx);
            DBManager.closeResultSet(rs1);
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(retrieve);
            DBManager.closeConnection(conn);
        }
        return orders;
    }

    @Override
    public long addOrder(Order order) throws DAOException {
        if (order == null) {
            throw new IllegalArgumentException("Null order");
        }
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        long idOrder = 0;

        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            Object[] parameters = {
                order.getFecha(),
                order.getDeliveryType(),
                order.getConsecutive(),
                order.getValor(),
                order.getIdClient(),
                order.getIdWaitress(),
                order.getTable(),
                order.getCiclo(),
                order.getNota(),
                order.getStatus()
            };
            ps = sqlStatements.buildSQLStatement(conn, ADD_ORDER_KEY, parameters, Statement.RETURN_GENERATED_KEYS);
            ps.executeUpdate();

            try ( ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    idOrder = generatedKeys.getLong(1);
                }
            }

            Map<String, String> namedParams = new HashMap<>();
//            namedParams.put(JDBCDAOFactory.NAMED_PARAM_TABLE, "orders");
//            String query = sqlStatements.getSQLString(GET_MAX_ID_KEY, namedParams);
//            ps = conn.prepareStatement(query);
//            rs = ps.executeQuery();
//            while (rs.next()) {
//                idOrder = rs.getInt(1);
//            }

            List<ProductoPed> products = order.getProducts();

            for (int i = 0; i < products.size(); i++) {
                ProductoPed product = products.get(i);
                Object[] parameters1 = {
                    0,
                    idOrder,
                    product.getProduct().getId(),
                    product.hasPresentation() ? product.getPresentation().getId() : 0,
                    product.getPrecio(),
                    product.getCantidad(),
                    product.isDelivery(),
                    product.getTermino(),
                    product.isEntry(),
                    product.getEspecificaciones(),
                    product.getStatus()
                };
                ps = sqlStatements.buildSQLStatement(conn, JDBCInvoiceDAO.ADD_INVOICE_PRODUCT_KEY, parameters1);
                ps.executeUpdate();

                HashMap<Integer, HashMap> mData = product.getData();

                //fix issue para productos sin presentacion
                if (mData != null && !mData.isEmpty()) {
//                    double exist = Double.parseDouble(data.get("exist").toString());

                    Set<Integer> keys = mData.keySet();
                    for (Integer key : keys) {
                        HashMap data = mData.get(key);
                        boolean onlyDelivery = Boolean.parseBoolean(data.get("onlyDelivery").toString());
                        double quant = Double.parseDouble(data.get("quantity").toString());
                        double res = (quant * product.getCantidad() * -1);
                        if (order.getDeliveryType() == PanelPedido.TIPO_LOCAL && onlyDelivery) {
                            res = 0;
                        }
                        Object[] parameterx = {
                            res,
                            data.get("id")
                        };
                        ps = sqlStatements.buildSQLStatement(conn, JDBCUtilDAO.ADD_INVENTORY_QUANTITY_KEY, parameterx);
                        ps.executeUpdate();
                    }

                }

                int idProduct = 0;

                namedParams = new HashMap<>();
                namedParams.put(JDBCDAOFactory.NAMED_PARAM_TABLE, "invoice_product");
                String query2 = sqlStatements.getSQLString(GET_MAX_ID_KEY, namedParams);
                ps = conn.prepareStatement(query2);
                rs = ps.executeQuery();
                while (rs.next()) {
                    idProduct = rs.getInt(1);
                }

                ArrayList<AdditionalPed> additionals = product.getAdicionales();

                for (int j = 0; j < additionals.size(); j++) {
                    AdditionalPed additional = additionals.get(j);
                    Object[] parameters2 = {
                        idProduct,
                        additional.getAdditional().getId(),
                        additional.getAdditional().getPrecio(),
                        additional.getCantidad()
                    };
                    ps = sqlStatements.buildSQLStatement(conn, JDBCUtilDAO.ADD_ADDITIONAL_PRODUCT_KEY, parameters2);
                    ps.executeUpdate();
                }

                ArrayList<Ingredient> exclusions = product.getExclusiones();

                for (int k = 0; k < exclusions.size(); k++) {
                    Ingredient exclusion = exclusions.get(k);
                    Object[] parameters3 = {
                        idProduct,
                        exclusion.getId()
                    };
                    ps = sqlStatements.buildSQLStatement(conn, JDBCUtilDAO.ADD_EXCLUSION_PRODUCT_KEY, parameters3);
                    ps.executeUpdate();
                }
            }

            conn.commit();
        } catch (SQLException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot add Order", e);
        } catch (IOException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot add Order", e);
        } finally {
            DBManager.closeStatement(ps);
            DBManager.closeConnection(conn);
        }

        return idOrder;
    }

    @Override
    public void deleteOrder(int id) throws DAOException {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            Object[] parameters = {id};
            ps = sqlStatements.buildSQLStatement(conn, DELETE_ORDER_KEY, parameters);
            ps.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot delete the order", e);
        } catch (IOException e) {
            throw new DAOException("Cannot delete the order", e);
        } finally {
            DBManager.closeStatement(ps);
            DBManager.closeConnection(conn);
        }
    }

    @Override
    public void updateOrder(Order order) throws DAOException {
        Connection conn = null;
        PreparedStatement update = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            Object[] parameters = {
                order.getDeliveryType(),
                order.getValor(),
                order.getIdClient(),
                order.getIdWaitress(),
                order.getTable(),
                order.getCiclo(),
                order.getNota(),
                order.getStatus(),
                order.getId()
            };

            update = sqlStatements.buildSQLStatement(conn, UPDATE_ORDER_KEY, parameters);
            update.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Could not properly update the order", e);
        } catch (IOException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Could not properly update the order", e);
        } finally {
            DBManager.closeStatement(update);
            DBManager.closeConnection(conn);
        }
    }

    public ArrayList<Order> getOrderByQuery(String query) throws DAOException {

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null, rs1 = null, rs2 = null, rs3 = null, rsx = null;
        Order order = null;
        ArrayList<Order> orders = new ArrayList<>();
        try {
            conn = dataSource.getConnection();
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                order = new Order();
                order.setId(rs.getLong(1));
                order.setId(rs.getLong("id"));
                order.setFecha(rs.getTimestamp("take_date"));
                order.setValor(rs.getBigDecimal("value"));
                order.setIdClient(rs.getLong("idClient"));
                order.setIdWaitress(rs.getInt("idWaiter"));
                order.setTable(rs.getInt("idTable"));
                order.setCiclo(rs.getLong("idCycle"));
                order.setNota(rs.getString("notes"));
                order.setDeliveryType(rs.getInt("deliveryType"));
                order.setConsecutive(rs.getString("consecutive"));
                order.setStatus(rs.getInt("status"));
                orders.add(order);

                Object[] parameters = {order.getId()};

                try {
                    ps = sqlStatements.buildSQLStatement(conn, GET_ORDER_PRODUCT_KEY, parameters);
                    rs1 = ps.executeQuery();
                    Product product;
                    while (rs1.next()) {
                        product = new Product();
                        product.setId(rs1.getInt(1));
                        product.setName(rs1.getString(2));
                        product.setCode(rs1.getString(3));
                        product.setDescription(rs1.getString(4));
                        product.setPrice(rs1.getBigDecimal(5).doubleValue());
                        product.setImage(rs1.getString(6));
                        product.setCategory(rs1.getString(7));
                        product.setVariablePrice(rs1.getBoolean(8));

                        ProductoPed productoPed = new ProductoPed(product);
                        productoPed.setPrecio(rs1.getBigDecimal(9).doubleValue());
                        productoPed.setCantidad(rs1.getInt(10));
                        productoPed.setDelivery(rs1.getBoolean(13));
                        productoPed.setTermino(rs1.getString(14));
                        productoPed.setEntry(rs1.getBoolean(15));
                        productoPed.setEspecificaciones(rs1.getString(16));
                        productoPed.setStatus(rs1.getInt(17));
                        productoPed.setStations(rs1.getString(18));

                        int idProdPed = rs1.getInt(11);
                        int idPresentation = rs1.getInt(12);

                        Object[] parameters1 = {idPresentation};
                        try {
                            ps = sqlStatements.buildSQLStatement(conn, GET_PRESENTATION_KEY, parameters1);
                            rsx = ps.executeQuery();
                            Presentation pres;
                            while (rsx.next()) {
                                pres = new Presentation();
                                pres.setId(rsx.getInt(1));
                                pres.setIDProd(rsx.getInt(2));
                                pres.setSerie(rsx.getInt(3));
                                pres.setName(rsx.getString(4));
                                pres.setPrice(rsx.getDouble(5));
                                pres.setDefault(rsx.getBoolean(6));
                                pres.setEnabled(rs.getBoolean(7));
                                productoPed.setPresentation(pres);
                            }
                        } catch (SQLException e) {
                            System.out.println(e.getMessage());
                            throw new DAOException("Could not properly retrieve the presentation " + e);
                        } catch (IOException e) {
                            System.out.println(e.getMessage());
                            throw new DAOException("Could not properly retrieve the presentation: " + e);
                        }

                        Object[] parameters2 = {idProdPed};

                        try {
                            ps = sqlStatements.buildSQLStatement(conn, GET_ADDITIONAL_PRODUCT_KEY, parameters2);
                            rs2 = ps.executeQuery();
                            Additional addition;
                            while (rs2.next()) {

                                addition = new Additional();
                                addition.setId(rs2.getInt(1));
                                addition.setName(rs2.getString(2));
                                addition.setMeasure(rs2.getString(4));
                                addition.setPrecio(rs2.getBigDecimal(5).doubleValue());

                                int cant = rs2.getInt(6);

                                productoPed.addAdicional(addition, cant);
                            }
                        } catch (SQLException e) {
                            throw new DAOException("Could not properly retrieve the additional " + e);
                        } catch (IOException e) {
                            throw new DAOException("Could not properly retrieve the additional: " + e);

                        }

                        try {
                            ps = sqlStatements.buildSQLStatement(conn, GET_EXCLUSION_PRODUCT_KEY, parameters2);
                            rs3 = ps.executeQuery();
                            Ingredient ingredient;
                            while (rs3.next()) {

                                ingredient = new Ingredient();
                                ingredient.setId(rs3.getInt(1));
                                ingredient.setName(rs3.getString(2));
                                ingredient.setCode(rs3.getString(3));
                                ingredient.setMeasure(rs3.getString(4));

                                productoPed.addExclusion(ingredient);
                            }
                        } catch (SQLException e) {
                            throw new DAOException("Could not properly retrieve the additional " + e);
                        } catch (IOException e) {
                            throw new DAOException("Could not properly retrieve the additional: " + e);

                        }

                        if (order.getProducts().contains(productoPed)) {
                            int idx = order.getProducts().indexOf(productoPed);
                            int cant = productoPed.getCantidad();
                            ProductoPed prod = order.getProducts().get(idx);
                            prod.setCantidad(prod.getCantidad() + cant);
                            order.getProducts().set(idx, prod);
                        } else {
                            order.addProduct(productoPed);
                        }
                    }
                } catch (SQLException e) {
                    throw new DAOException("Could not properly retrieve the product " + e);
                } catch (IOException e) {
                    throw new DAOException("Could not properly retrieve the product: " + e);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the Order: " + e);
        } finally {
            DBManager.closeResultSet(rs3);
            DBManager.closeResultSet(rs2);
            DBManager.closeResultSet(rsx);
            DBManager.closeResultSet(rs1);
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(ps);
            DBManager.closeConnection(conn);
        }
        return orders;
    }

    public ArrayList<Object[]> getOrderByProductWhere(String where, String order) throws DAOException {
        String retrieveProdIn;
        ArrayList<Object[]> lista = new ArrayList<>();
        try {
            SQLExtractor sqlExtractorWhere = new SQLExtractor(where, SQLExtractor.Type.WHERE);;
            SQLExtractor sqlExtractorOrderBy = new SQLExtractor(order, SQLExtractor.Type.ORDER_BY);;
            Map<String, String> namedParams = new HashMap<>();
            namedParams.put(NAMED_PARAM_WHERE, sqlExtractorWhere.extractWhere());
            namedParams.put(NAMED_PARAM_ORDER_BY, sqlExtractorOrderBy.extractOrderBy());
            retrieveProdIn = sqlStatements.getSQLString(GET_ORDER_BY_PRODUCT_KEY, namedParams);
        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the Salida", e);
        } catch (IOException e) {
            throw new DAOException("Could not properly retrieve the Salida", e);
        }
        Connection conn = null;
        PreparedStatement retrieve = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            retrieve = conn.prepareStatement(retrieveProdIn);
            rs = retrieve.executeQuery();
            while (rs.next()) {
                Object[] datos = {
                    rs.getInt(1), //id
                    rs.getString(3), //factura
                    rs.getDouble(4), //cantidad
                    rs.getBigDecimal(5), //precio
                    rs.getTimestamp(9), //fecha
                    rs.getString(14), //proveedor
                    rs.getInt(6), //locacion
                    rs.getString(2), //producto
                };
                lista.add(datos);
            }
        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the Order: " + e);
        } finally {
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(retrieve);
            DBManager.closeConnection(conn);
        }
        return lista;

    }

    public void updateOrderFull(Order order, List<ProductoPed> oldProducts) throws DAOException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            Object[] parameters = {
                order.getDeliveryType(),
                order.getValor(),
                order.getIdClient(),
                order.getIdWaitress(),
                order.getTable(),
                order.getCiclo(),
                order.getNota(),
                order.getStatus(),
                order.getId()
            };

            ps = sqlStatements.buildSQLStatement(conn, UPDATE_ORDER_KEY, parameters);
            ps.executeUpdate();

            List<ProductoPed> products = order.getProducts();

            if (oldProducts != null && !oldProducts.equals(products)) {
                for (ProductoPed oldProduct : oldProducts) {
                    HashMap<Integer, HashMap> data = oldProduct.getData();
//                    System.out.println(Arrays.toString(data.entrySet().toArray()));
                }
            }

            for (int i = 0; i < products.size(); i++) {
                ProductoPed product = products.get(i);
                Object[] parameters1 = {
                    order.getId(),
                    product.getProduct().getId(),
                    product.hasPresentation() ? product.getPresentation().getId() : 0,
                    product.getPrecio(),
                    product.getCantidad()
                };
                ps = sqlStatements.buildSQLStatement(conn, ADD_ORDER_PRODUCT_KEY, parameters1);
                ps.executeUpdate();

                Map<String, String> namedParams = new HashMap<>();

                HashMap<Integer, HashMap> mData = product.getData();

                //fix issue para productos sin presentacion
                if (mData != null && !mData.isEmpty()) {
//                    double exist = Double.parseDouble(data.get("exist").toString());
                    Set<Integer> keys = mData.keySet();
                    for (Integer key : keys) {
                        HashMap data = mData.get(key);
                        double quant = Double.parseDouble(data.get("quantity").toString());
                        double res = (quant * product.getCantidad() * -1);
                        Object[] parameterx = {
                            res,
                            data.get("id")
                        };
                        ps = sqlStatements.buildSQLStatement(conn, JDBCUtilDAO.ADD_INVENTORY_QUANTITY_KEY, parameterx);
                        ps.executeUpdate();
                    }

                }

                int idProduct = 0;

                namedParams = new HashMap<>();
                namedParams.put(JDBCDAOFactory.NAMED_PARAM_TABLE, "order_product");
                String query2 = sqlStatements.getSQLString(GET_MAX_ID_KEY, namedParams);
                ps = conn.prepareStatement(query2);
                rs = ps.executeQuery();
                while (rs.next()) {
                    idProduct = rs.getInt(1);
                }

                ArrayList<AdditionalPed> additionals = product.getAdicionales();

                for (int j = 0; j < additionals.size(); j++) {
                    AdditionalPed additional = additionals.get(j);
                    Object[] parameters2 = {
                        idProduct,
                        additional.getAdditional().getId(),
                        additional.getAdditional().getPrecio(),
                        additional.getCantidad()
                    };
                    ps = sqlStatements.buildSQLStatement(conn, JDBCUtilDAO.ADD_ADDITIONAL_PRODUCT_KEY, parameters2);
                    ps.executeUpdate();
                }

                ArrayList<Ingredient> exclusions = product.getExclusiones();

                for (int k = 0; k < exclusions.size(); k++) {
                    Ingredient exclusion = exclusions.get(k);
                    Object[] parameters3 = {
                        idProduct,
                        exclusion.getId()
                    };
                    ps = sqlStatements.buildSQLStatement(conn, JDBCUtilDAO.ADD_EXCLUSION_PRODUCT_KEY, parameters3);
                    ps.executeUpdate();
                }
            }

//            conn.commit();
            conn.rollback();
        } catch (SQLException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Could not properly update the order", e);
        } catch (IOException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Could not properly update the order", e);
        } finally {
            DBManager.closeStatement(ps);
            DBManager.closeConnection(conn);
        }
    }

    public ArrayList<Order> getOrderLiteByQuery(String query) throws DAOException {

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Order order = null;
        ArrayList<Order> orders = new ArrayList<>();
        try {
            conn = dataSource.getConnection();
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                order = new Order();
                order.setId(rs.getLong("id"));
                order.setFecha(rs.getTimestamp("take_date"));
                order.setValor(rs.getBigDecimal("value"));
                order.setIdClient(rs.getLong("idClient"));
                order.setIdWaitress(rs.getInt("idWaiter"));
                order.setTable(rs.getInt("idTable"));
                order.setCiclo(rs.getLong("idCycle"));
                order.setNota(rs.getString("notes"));
                order.setDeliveryType(rs.getInt("deliveryType"));
                order.setConsecutive(rs.getString("consecutive"));
                order.setStatus(rs.getInt("status"));
                orders.add(order);

            }
        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the Order: " + e);
        } finally {
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(ps);
            DBManager.closeConnection(conn);
        }
        return orders;
    }

    public long addProductsOrder(long idOrder, List<ProductoPed> products) throws DAOException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);

            Map<String, String> namedParams = new HashMap<>();

            for (int i = 0; i < products.size(); i++) {
                ProductoPed product = products.get(i);
                Object[] parameters1 = {
                    0,
                    idOrder,
                    product.getProduct().getId(),
                    product.hasPresentation() ? product.getPresentation().getId() : 0,
                    product.getPrecio(),
                    product.getCantidad(),
                    product.isDelivery(),
                    product.getTermino(),
                    product.isEntry(),
                    product.getEspecificaciones(),
                    product.getStatus()
                };
                ps = sqlStatements.buildSQLStatement(conn, JDBCInvoiceDAO.ADD_INVOICE_PRODUCT_KEY, parameters1);
                ps.executeUpdate();

                HashMap<Integer, HashMap> mData = product.getData();

                //fix issue para productos sin presentacion
                if (mData != null && !mData.isEmpty()) {
//                    double exist = Double.parseDouble(data.get("exist").toString());

                    Set<Integer> keys = mData.keySet();
                    for (Integer key : keys) {
                        HashMap data = mData.get(key);
                        boolean onlyDelivery = Boolean.parseBoolean(data.get("onlyDelivery").toString());
                        double quant = Double.parseDouble(data.get("quantity").toString());
                        double res = (quant * product.getCantidad() * -1);
                        if (onlyDelivery) {
                            res = 0;
                        }
                        Object[] parameterx = {
                            res,
                            data.get("id")
                        };
                        ps = sqlStatements.buildSQLStatement(conn, JDBCUtilDAO.ADD_INVENTORY_QUANTITY_KEY, parameterx);
                        ps.executeUpdate();
                    }

                }

                int idProduct = 0;

                namedParams = new HashMap<>();
                namedParams.put(JDBCDAOFactory.NAMED_PARAM_TABLE, "invoice_product");
                String query2 = sqlStatements.getSQLString(GET_MAX_ID_KEY, namedParams);
                ps = conn.prepareStatement(query2);
                rs = ps.executeQuery();
                while (rs.next()) {
                    idProduct = rs.getInt(1);
                }

                ArrayList<AdditionalPed> additionals = product.getAdicionales();

                for (int j = 0; j < additionals.size(); j++) {
                    AdditionalPed additional = additionals.get(j);
                    Object[] parameters2 = {
                        idProduct,
                        additional.getAdditional().getId(),
                        additional.getAdditional().getPrecio(),
                        additional.getCantidad()
                    };
                    ps = sqlStatements.buildSQLStatement(conn, JDBCUtilDAO.ADD_ADDITIONAL_PRODUCT_KEY, parameters2);
                    ps.executeUpdate();
                }

                ArrayList<Ingredient> exclusions = product.getExclusiones();

                for (int k = 0; k < exclusions.size(); k++) {
                    Ingredient exclusion = exclusions.get(k);
                    Object[] parameters3 = {
                        idProduct,
                        exclusion.getId()
                    };
                    ps = sqlStatements.buildSQLStatement(conn, JDBCUtilDAO.ADD_EXCLUSION_PRODUCT_KEY, parameters3);
                    ps.executeUpdate();
                }
            }

            conn.commit();
        } catch (SQLException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot add Order", e);
        } catch (IOException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot add Order", e);
        } finally {
            DBManager.closeStatement(ps);
            DBManager.closeConnection(conn);
        }

        return idOrder;
    }

}
