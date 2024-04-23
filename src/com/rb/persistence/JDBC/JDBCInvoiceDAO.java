/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rb.persistence.JDBC;

import static com.rb.persistence.JDBC.JDBCUtilDAO.GET_MAX_ID_KEY;

import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.rb.DBManager;
import com.rb.domain.Additional;
import com.rb.domain.AdditionalPed;
import com.rb.domain.Ingredient;
import com.rb.domain.Invoice;
import com.rb.domain.OtherProduct;
import com.rb.domain.Presentation;
import com.rb.domain.Product;
import com.rb.domain.ProductoPed;
import com.rb.gui.PanelPedido;
import com.rb.persistence.SQLExtractor;
import com.rb.persistence.SQLLoader;
import com.rb.persistence.dao.DAOException;
import com.rb.persistence.dao.InvoiceDAO;
import java.sql.Statement;

/**
 *
 * @author LuisR
 */
public class JDBCInvoiceDAO implements InvoiceDAO {

    public static final String TABLE_NAME = "invoices";
    public static final String NAMED_PARAM_WHERE = "{where}";
    public static final String NAMED_PARAM_ORDER_BY = "{orderby}";
    public static final String NAMED_PARAM_START = "{start}";
    public static final String NAMED_PARAM_END = "{end}";
    public static final String NAMED_PARAM_NUM = "{num}";
    private static final Logger logger = LogManager.getLogger(JDBCInvoiceDAO.class.getCanonicalName());
    private final DataSource dataSource;
    private final SQLLoader sqlStatements;
    protected static final String CREATE_INVOICES_TABLE_KEY = "CREATE_INVOICES_TABLE";
    protected static final String ADD_INVOICE_KEY = "ADD_INVOICE";
    protected static final String UPDATE_INVOICE_KEY = "UPDATE_INVOICE";
    protected static final String GET_INVOICE_KEY = "GET_INVOICE";
    protected static final String DELETE_INVOICE_KEY = "DELETE_INVOICE";
    protected static final String ADD_INVOICE_PRODUCT_KEY = "ADD_INVOICE_PRODUCT";
    protected static final String GET_INVOICE_PRODUCT_KEY = "GET_INVOICE_PRODUCT";
    protected static final String GET_PRESENTATION_KEY = "GET_PRESENTATION";
    protected static final String GET_ADDITIONAL_PRODUCT_KEY = "GET_ADDITIONAL_PRODUCT";
    protected static final String GET_EXCLUSION_PRODUCT_KEY = "GET_EXCLUSION_PRODUCT";
    protected static final String ADD_INVOICE_OTHER_PRODUCT_KEY = "ADD_INVOICE_OTHER_PRODUCT";
    protected static final String GET_INVOICE_BY_PRODUCT_KEY = "GET_INVOICE_BY_PRODUCT";

    public JDBCInvoiceDAO(DataSource dataSource, SQLLoader sqlStatements) throws DAOException {
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
            ps = sqlStatements.buildSQLStatement(conn, CREATE_INVOICES_TABLE_KEY);
            ps.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot create Invoice table", e);
        } catch (IOException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot create Invoice table", e);
        } finally {
            DBManager.closeStatement(ps);
            DBManager.closeConnection(conn);
        }
    }

    public Invoice getInvoiceBy(String query) throws DAOException {
        String retrieveProd;
        try {
            SQLExtractor sqlExtractorWhere = new SQLExtractor(query, SQLExtractor.Type.WHERE);
            Map<String, String> namedParams = new HashMap<String, String>();
            namedParams.put(NAMED_PARAM_WHERE, sqlExtractorWhere.extractWhere());
            retrieveProd = sqlStatements.getSQLString(GET_INVOICE_KEY, namedParams);

        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the invoice", e);
        } catch (IOException e) {
            throw new DAOException("Could not properly retrieve the invoice", e);
        }
        Connection conn = null;
        PreparedStatement retrieve = null;
        ResultSet rs = null, rs1 = null, rs2 = null, rs3 = null, rsx = null;
        Invoice invoice = null;
        try {
            conn = dataSource.getConnection();
            retrieve = conn.prepareStatement(retrieveProd);
            rs = retrieve.executeQuery();
            while (rs.next()) {
                invoice = new Invoice();
                invoice.setId(rs.getLong(1));
                invoice.setFactura(rs.getString(2));
                invoice.setFecha(rs.getTimestamp(3));
                invoice.setTipoEntrega(rs.getInt(4));
                invoice.setValor(rs.getBigDecimal(5));
                invoice.setNumDeliverys(rs.getInt(6));
                invoice.setValorDelivery(rs.getBigDecimal(7));
                invoice.setDescuento(rs.getDouble(8));
                invoice.setIdCliente(rs.getLong(9));
                invoice.setIdWaitress(rs.getInt(10));
                invoice.setTable(rs.getInt(11));
                invoice.setCiclo(rs.getLong(12));
                invoice.setNota(rs.getString(13));
                invoice.setService(rs.getBoolean(14));
                invoice.setPorcService(rs.getDouble(15));
                invoice.setStatus(rs.getInt(16));

                Object[] parameters = {invoice.getFactura()};

                try {
                    retrieve = sqlStatements.buildSQLStatement(conn, GET_INVOICE_PRODUCT_KEY, parameters);
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

                        invoice.addProduct(productoPed);
                    }
                } catch (SQLException e) {
                    throw new DAOException("Could not properly retrieve the product " + e);
                } catch (IOException e) {
                    throw new DAOException("Could not properly retrieve the product: " + e);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the Invoice: " + e);
        } finally {
            DBManager.closeResultSet(rs3);
            DBManager.closeResultSet(rs2);
            DBManager.closeResultSet(rsx);
            DBManager.closeResultSet(rs1);
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(retrieve);
            DBManager.closeConnection(conn);
        }
        return invoice;
    }

    @Override
    public Invoice getInvoice(int id) throws DAOException {
        return getInvoiceBy("id=" + id);
    }

    @Override
    public ArrayList<Invoice> getInvoiceList() throws DAOException {
        return getInvoiceList("", "");
    }

    public ArrayList<Invoice> getInvoiceLiteList(String where, String orderBy, int startEntry, int endEntry) throws DAOException {

        if (startEntry < 0) {
            startEntry = 0;
        }

        if (endEntry < 0) {
            endEntry = 0;
        }

        if (endEntry <= startEntry) {
            return new ArrayList<Invoice>();
        }

        String retrieveProd;
        ArrayList<Invoice> invoices = new ArrayList<>();
        try {
            SQLExtractor sqlExtractorWhere = new SQLExtractor(where, SQLExtractor.Type.WHERE);
            SQLExtractor sqlExtractorOrderBy = new SQLExtractor(orderBy, SQLExtractor.Type.ORDER_BY);
            Map<String, String> namedParams = new HashMap<>();
            namedParams.put(NAMED_PARAM_WHERE, sqlExtractorWhere.extractWhere());
            namedParams.put(NAMED_PARAM_ORDER_BY, sqlExtractorOrderBy.extractOrderBy());
            namedParams.put(NAMED_PARAM_START, String.valueOf(startEntry));
            namedParams.put(NAMED_PARAM_END, String.valueOf(endEntry));
            namedParams.put(NAMED_PARAM_NUM, String.valueOf(endEntry - startEntry));

            retrieveProd = sqlStatements.getSQLString(GET_INVOICE_KEY, namedParams);

        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the Invoice List", e);
        } catch (IOException e) {
            throw new DAOException("Could not properly retrieve the Invoice List", e);
        }

        Connection conn = null;
        PreparedStatement retrieve = null;
        ResultSet rs = null;
        Invoice invoice = null;
        try {
            conn = dataSource.getConnection();
            retrieve = conn.prepareStatement(retrieveProd);
            rs = retrieve.executeQuery();
            while (rs.next()) {
                invoice = new Invoice();
                invoice.setId(rs.getLong(1));
                invoice.setFactura(rs.getString(2));
                invoice.setFecha(rs.getTimestamp(3));
                invoice.setTipoEntrega(rs.getInt(4));
                invoice.setValor(rs.getBigDecimal(5));
                invoice.setNumDeliverys(rs.getInt(6));
                invoice.setValorDelivery(rs.getBigDecimal(7));
                invoice.setDescuento(rs.getDouble(8));
                invoice.setIdCliente(rs.getLong(9));
                invoice.setIdWaitress(rs.getInt(10));
                invoice.setTable(rs.getInt(11));
                invoice.setCiclo(rs.getLong(12));
                invoice.setNota(rs.getString(13));
                invoice.setService(rs.getBoolean(14));
                invoice.setPorcService(rs.getDouble(15));
                invoice.setStatus(rs.getInt(16));
                invoices.add(invoice);
            }
        } catch (SQLException e) {
            throw new DAOException("Could not proper retrieve the Invoice: " + e);
        } finally {
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(retrieve);
            DBManager.closeConnection(conn);
        }
        return invoices;
    }

    public ArrayList<Invoice> getInvoiceLiteList(String where, String orderBy) throws DAOException {

        String retrieveProd;
        ArrayList<Invoice> invoices = new ArrayList<>();
        try {
            SQLExtractor sqlExtractorWhere = new SQLExtractor(where, SQLExtractor.Type.WHERE);
            SQLExtractor sqlExtractorOrderBy = new SQLExtractor(orderBy, SQLExtractor.Type.ORDER_BY);
            Map<String, String> namedParams = new HashMap<>();
            namedParams.put(NAMED_PARAM_WHERE, sqlExtractorWhere.extractWhere());
            namedParams.put(NAMED_PARAM_ORDER_BY, sqlExtractorOrderBy.extractOrderBy());

            retrieveProd = sqlStatements.getSQLString(GET_INVOICE_KEY, namedParams);

        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the Invoice List", e);
        } catch (IOException e) {
            throw new DAOException("Could not properly retrieve the Invoice List", e);
        }

        Connection conn = null;
        PreparedStatement retrieve = null;
        ResultSet rs = null;
        Invoice invoice = null;
        try {
            conn = dataSource.getConnection();
            retrieve = conn.prepareStatement(retrieveProd);
            rs = retrieve.executeQuery();
            while (rs.next()) {
                invoice = new Invoice();
                invoice.setId(rs.getLong(1));
                invoice.setFactura(rs.getString(2));
                invoice.setFecha(rs.getTimestamp(3));
                invoice.setTipoEntrega(rs.getInt(4));
                invoice.setValor(rs.getBigDecimal(5));
                invoice.setNumDeliverys(rs.getInt(6));
                invoice.setValorDelivery(rs.getBigDecimal(7));
                invoice.setDescuento(rs.getDouble(8));
                invoice.setIdCliente(rs.getLong(9));
                invoice.setIdWaitress(rs.getInt(10));
                invoice.setTable(rs.getInt(11));
                invoice.setCiclo(rs.getLong(12));
                invoice.setNota(rs.getString(13));
                invoice.setService(rs.getBoolean(14));
                invoice.setPorcService(rs.getDouble(15));
                invoice.setStatus(rs.getInt(16));
                invoices.add(invoice);
            }
        } catch (SQLException e) {
            throw new DAOException("Could not proper retrieve the Invoice: " + e);
        } finally {
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(retrieve);
            DBManager.closeConnection(conn);
        }
        return invoices;
    }

    public ArrayList<Invoice> getInvoiceList(String where, String orderBy) throws DAOException {
        String retrieveProd;
        ArrayList<Invoice> invoices = new ArrayList<>();
        try {
            SQLExtractor sqlExtractorWhere = new SQLExtractor(where, SQLExtractor.Type.WHERE);
            SQLExtractor sqlExtractorOrderBy = new SQLExtractor(orderBy, SQLExtractor.Type.ORDER_BY);
            Map<String, String> namedParams = new HashMap<>();
            namedParams.put(NAMED_PARAM_WHERE, sqlExtractorWhere.extractWhere());
            namedParams.put(NAMED_PARAM_ORDER_BY, sqlExtractorOrderBy.extractOrderBy());
            retrieveProd = sqlStatements.getSQLString(GET_INVOICE_KEY, namedParams);

        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the Invoice List", e);
        } catch (IOException e) {
            throw new DAOException("Could not properly retrieve the Invoice List", e);
        }

        Connection conn = null;
        PreparedStatement retrieve = null;
        ResultSet rs = null, rs1 = null, rs2 = null, rs3 = null, rsx = null;
        Invoice invoice = null;
        try {
            conn = dataSource.getConnection();
            retrieve = conn.prepareStatement(retrieveProd);
            rs = retrieve.executeQuery();
            while (rs.next()) {
                invoice = new Invoice();
                invoice.setId(rs.getLong(1));
                invoice.setFactura(rs.getString(2));
                invoice.setFecha(rs.getTimestamp(3));
                invoice.setTipoEntrega(rs.getInt(4));
                invoice.setValor(rs.getBigDecimal(5));
                invoice.setNumDeliverys(rs.getInt(6));
                invoice.setValorDelivery(rs.getBigDecimal(7));
                invoice.setDescuento(rs.getDouble(8));
                invoice.setIdCliente(rs.getLong(9));
                invoice.setIdWaitress(rs.getInt(10));
                invoice.setTable(rs.getInt(11));
                invoice.setCiclo(rs.getLong(12));
                invoice.setNota(rs.getString(13));
                invoice.setService(rs.getBoolean(14));
                invoice.setPorcService(rs.getDouble(15));
                invoice.setStatus(rs.getInt(16));

                Object[] parameters = {invoice.getFactura()};

                try {
                    retrieve = sqlStatements.buildSQLStatement(conn, GET_INVOICE_PRODUCT_KEY, parameters);
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
//                        System.out.println("ADD to :" + invoice.getFactura() + " ->" + productoPed.getProduct().getName());
                        invoice.addProduct(productoPed);
                    }
                } catch (SQLException e) {
                    throw new DAOException("Could not properly retrieve the product " + e);
                } catch (IOException e) {
                    throw new DAOException("Could not properly retrieve the product: " + e);
                }

                invoices.add(invoice);
            }
        } catch (SQLException e) {
            throw new DAOException("Could not proper retrieve the Invoice: " + e);
        } finally {
            DBManager.closeResultSet(rs3);
            DBManager.closeResultSet(rs2);
            DBManager.closeResultSet(rsx);
            DBManager.closeResultSet(rs1);
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(retrieve);
            DBManager.closeConnection(conn);
        }
        return invoices;
    }

    public ArrayList<Invoice> getInvoiceListWhitProducts(String where, String orderBy) throws DAOException {
        String retrieveProd;
        ArrayList<Invoice> invoices = new ArrayList<>();
        try {
            SQLExtractor sqlExtractorWhere = new SQLExtractor(where, SQLExtractor.Type.WHERE);
            SQLExtractor sqlExtractorOrderBy = new SQLExtractor(orderBy, SQLExtractor.Type.ORDER_BY);
            Map<String, String> namedParams = new HashMap<>();
            namedParams.put(NAMED_PARAM_WHERE, sqlExtractorWhere.extractWhere());
            namedParams.put(NAMED_PARAM_ORDER_BY, sqlExtractorOrderBy.extractOrderBy());
            retrieveProd = sqlStatements.getSQLString(GET_INVOICE_KEY, namedParams);

        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the Invoice List", e);
        } catch (IOException e) {
            throw new DAOException("Could not properly retrieve the Invoice List", e);
        }

        Connection conn = null;
        PreparedStatement retrieve = null;
        ResultSet rs = null, rs1 = null, rs2 = null, rs3 = null, rsx = null;
        Invoice invoice = null;
        try {
            conn = dataSource.getConnection();
            retrieve = conn.prepareStatement(retrieveProd);
            rs = retrieve.executeQuery();
            while (rs.next()) {
                invoice = new Invoice();
                invoice.setId(rs.getLong(1));
                invoice.setFactura(rs.getString(2));
                invoice.setFecha(rs.getTimestamp(3));
                invoice.setTipoEntrega(rs.getInt(4));
                invoice.setValor(rs.getBigDecimal(5));
                invoice.setNumDeliverys(rs.getInt(6));
                invoice.setValorDelivery(rs.getBigDecimal(7));
                invoice.setDescuento(rs.getDouble(8));
                invoice.setIdCliente(rs.getLong(9));
                invoice.setIdWaitress(rs.getInt(10));
                invoice.setTable(rs.getInt(11));
                invoice.setCiclo(rs.getLong(12));
                invoice.setNota(rs.getString(13));
                invoice.setService(rs.getBoolean(14));
                invoice.setPorcService(rs.getDouble(15));
                invoice.setStatus(rs.getInt(16));

                invoice.addProduct(new ProductoPed(new Product(rs.getInt(18), rs.getString(19), 0, "")));

                invoices.add(invoice);
            }
        } catch (SQLException e) {
            throw new DAOException("Could not proper retrieve the Invoice: " + e);
        } finally {
            DBManager.closeResultSet(rs3);
            DBManager.closeResultSet(rs2);
            DBManager.closeResultSet(rsx);
            DBManager.closeResultSet(rs1);
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(retrieve);
            DBManager.closeConnection(conn);
        }
        return invoices;
    }

    @Override
    public void addInvoice(Invoice invoice) throws DAOException {
        if (invoice == null) {
            throw new IllegalArgumentException("Null invoice");
        }
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            Object[] parameters = {
                invoice.getFactura(),
                new java.sql.Timestamp(invoice.getFecha().getTime()),
                invoice.getTipoEntrega(),
                invoice.getValor(),
                invoice.getNumDeliverys(),
                invoice.getValorDelivery(),
                invoice.getDescuento(),
                invoice.getIdCliente(),
                invoice.getIdWaitress(),
                invoice.getTable(),
                invoice.getCiclo(),
                invoice.getNota(),
                invoice.isService(),
                invoice.getPorcService(),
                invoice.getStatus()
            };
            System.out.println("parameters = " + Arrays.toString(parameters));
            ps = sqlStatements.buildSQLStatement(conn, ADD_INVOICE_KEY, parameters, Statement.RETURN_GENERATED_KEYS);

            ps.executeUpdate();

            long idInvoice = 0;
            ResultSet rsk = ps.getGeneratedKeys();
            if (rsk.next()) {
                idInvoice = rsk.getLong(1);
            }

//            Map<String, String> namedParams = new HashMap<>();
//            namedParams.put(JDBCDAOFactory.NAMED_PARAM_TABLE, "invoices");
//            String query = sqlStatements.getSQLString(GET_MAX_ID_KEY, namedParams);
//            ps = conn.prepareStatement(query);
//            rs = ps.executeQuery();
//            while (rs.next()) {
//                idInvoice = rs.getInt(1);
//            }
            List<ProductoPed> products = invoice.getProducts();

            for (int i = 0; i < products.size(); i++) {
                ProductoPed product = products.get(i);
                Object[] parameters1 = {
                    idInvoice,
                    0,
                    product.getProduct().getId(),
                    product.hasPresentation() ? product.getPresentation().getId() : 0,
                    product.getPrecio(),
                    product.getCantidad()
                };
                ps = sqlStatements.buildSQLStatement(conn, ADD_INVOICE_PRODUCT_KEY, parameters1);
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
                        if (invoice.getTipoEntrega() == PanelPedido.TIPO_LOCAL && onlyDelivery) {
                            res = 0;
                        }
                        Object[] parameterx = {
                            res,
                            data.get("id")
                        };
                        ps = sqlStatements.buildSQLStatement(conn, JDBCUtilDAO.ADD_INVENTORY_QUANTITY_KEY, parameterx, Statement.RETURN_GENERATED_KEYS);
                        ps.executeUpdate();
                    }

                }

                long idProduct = 0;
                rsk = ps.getGeneratedKeys();
                if (rsk.next()) {
                    idProduct = rsk.getLong(1);
                }

//                namedParams = new HashMap<>();
//                namedParams.put(JDBCDAOFactory.NAMED_PARAM_TABLE, "invoice_product");
//                String query2 = sqlStatements.getSQLString(GET_MAX_ID_KEY, namedParams);
//                ps = conn.prepareStatement(query2);
//                rs = ps.executeQuery();
//                while (rs.next()) {
//                    idProduct = rs.getInt(1);
//                }
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

//            List<OtherProduct> otherProducts = invoice.getOtherProducts();
//
//            for (int i = 0; i < otherProducts.size(); i++) {
//                ProductoPed product = new ProductoPed(otherProducts.get(i));
//                Object[] parameters1 = {
//                    idInvoice,
//                    product.getProduct().getId(),
//                    product.getCantidad(),
//                    product.getPrecio()
//                };
//                ps = sqlStatements.buildSQLStatement(conn, ADD_INVOICE_OTHER_PRODUCT_KEY, parameters1);
//                ps.executeUpdate();
//            }
            conn.commit();
        } catch (SQLException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot add Invoice", e);
        } catch (IOException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot add Invoice", e);
        } finally {
            DBManager.closeStatement(ps);
            DBManager.closeConnection(conn);
        }
    }

    public void addOrderToInvoice(Invoice invoice) throws DAOException {
        if (invoice == null) {
            throw new IllegalArgumentException("Null invoice");
        }
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        long idInvoice = 0;

        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            Object[] parameters = {
                invoice.getFactura(),
                new java.sql.Timestamp(invoice.getFecha().getTime()),
                invoice.getTipoEntrega(),
                invoice.getValor(),
                invoice.getNumDeliverys(),
                invoice.getValorDelivery(),
                invoice.getDescuento(),
                invoice.getIdCliente(),
                invoice.getIdWaitress(),
                invoice.getTable(),
                invoice.getCiclo(),
                invoice.getNota(),
                invoice.isService(),
                invoice.getPorcService(),
                invoice.getStatus()
            };
            System.out.println("parameters = " + Arrays.toString(parameters));
            ps = sqlStatements.buildSQLStatement(conn, ADD_INVOICE_KEY, parameters, Statement.RETURN_GENERATED_KEYS);

            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    idInvoice = generatedKeys.getLong(1);
                }
            }

            List<ProductoPed> products = invoice.getProducts();

            for (int i = 0; i < products.size(); i++) {
                ProductoPed product = products.get(i);
                Object[] parameters1 = {
                    idInvoice,
                    0,
                    product.getProduct().getId(),
                    product.hasPresentation() ? product.getPresentation().getId() : 0,
                    product.getPrecio(),
                    product.getCantidad()
                };
                ps = sqlStatements.buildSQLStatement(conn, ADD_INVOICE_PRODUCT_KEY, parameters1);
                ps.executeUpdate();
            }
            conn.commit();
        } catch (SQLException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot add Order to Invoice", e);
        } catch (IOException e) {
            DBManager.rollbackConn(conn); 
            throw new DAOException("Cannot add Order to Invoice", e);
        } finally {
            DBManager.closeStatement(ps);
            DBManager.closeConnection(conn);
        }
    }

    @Override
    public void deleteInvoice(int id) throws DAOException {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            Object[] parameters = {id};
            ps = sqlStatements.buildSQLStatement(conn, DELETE_INVOICE_KEY, parameters);
            ps.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot delete the invoice", e);
        } catch (IOException e) {
            throw new DAOException("Cannot delete the invoice", e);
        } finally {
            DBManager.closeStatement(ps);
            DBManager.closeConnection(conn);
        }
    }

    @Override
    public void updateInvoice(Invoice invoice) throws DAOException {
        Connection conn = null;
        PreparedStatement update = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            Object[] parameters = {
                invoice.getTipoEntrega(),
                invoice.getValor(),
                invoice.getNumDeliverys(),
                invoice.getValorDelivery(),
                invoice.getDescuento(),
                invoice.getIdCliente(),
                invoice.getIdWaitress(),
                invoice.getTable(),
                invoice.getCiclo(),
                invoice.isService(),
                invoice.getPorcService(),
                invoice.getStatus(),
                invoice.getFactura()
            };

            update = sqlStatements.buildSQLStatement(conn, UPDATE_INVOICE_KEY, parameters);
            update.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Could not properly update the invoice", e);
        } catch (IOException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Could not properly update the invoice", e);
        } finally {
            DBManager.closeStatement(update);
            DBManager.closeConnection(conn);
        }
    }

    public ArrayList<Invoice> getInvoiceByQuery(String query) throws DAOException {

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null, rs1 = null, rs2 = null, rs3 = null, rsx = null;
        Invoice invoice = null;
        ArrayList<Invoice> invoices = new ArrayList<>();
        try {
            conn = dataSource.getConnection();
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                invoice = new Invoice();
                invoice.setId(rs.getLong(1));
                invoice.setFactura(rs.getString(2));
                invoice.setFecha(rs.getTimestamp(3));
                invoice.setTipoEntrega(rs.getInt(4));
                invoice.setValor(rs.getBigDecimal(5));
                invoice.setNumDeliverys(rs.getInt(6));
                invoice.setValorDelivery(rs.getBigDecimal(7));
                invoice.setDescuento(rs.getDouble(8));
                invoice.setIdCliente(rs.getLong(9));
                invoice.setIdWaitress(rs.getInt(10));
                invoice.setTable(rs.getInt(11));
                invoice.setCiclo(rs.getLong(12));
                invoice.setNota(rs.getString(13));
                invoice.setService(rs.getBoolean(14));
                invoice.setPorcService(rs.getDouble(15));
                invoice.setStatus(rs.getInt(16));
                invoices.add(invoice);

                Object[] parameters = {invoice.getFactura()};

                try {
                    ps = sqlStatements.buildSQLStatement(conn, GET_INVOICE_PRODUCT_KEY, parameters);
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

                        invoice.addProduct(productoPed);
                    }
                } catch (SQLException e) {
                    throw new DAOException("Could not properly retrieve the product " + e);
                } catch (IOException e) {
                    throw new DAOException("Could not properly retrieve the product: " + e);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the Invoice: " + e);
        } finally {
            DBManager.closeResultSet(rs3);
            DBManager.closeResultSet(rs2);
            DBManager.closeResultSet(rsx);
            DBManager.closeResultSet(rs1);
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(ps);
            DBManager.closeConnection(conn);
        }
        return invoices;
    }

    public ArrayList<Object[]> getInvoiceByProductWhere(String where, String order) throws DAOException {
        String retrieveProdIn;
        ArrayList<Object[]> lista = new ArrayList<>();
        try {
            SQLExtractor sqlExtractorWhere = new SQLExtractor(where, SQLExtractor.Type.WHERE);;
            SQLExtractor sqlExtractorOrderBy = new SQLExtractor(order, SQLExtractor.Type.ORDER_BY);;
            Map<String, String> namedParams = new HashMap<>();
            namedParams.put(NAMED_PARAM_WHERE, sqlExtractorWhere.extractWhere());
            namedParams.put(NAMED_PARAM_ORDER_BY, sqlExtractorOrderBy.extractOrderBy());
            retrieveProdIn = sqlStatements.getSQLString(GET_INVOICE_BY_PRODUCT_KEY, namedParams);
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
            throw new DAOException("Could not properly retrieve the Invoice: " + e);
        } finally {
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(retrieve);
            DBManager.closeConnection(conn);
        }
        return lista;

    }

    public void updateInvoiceFull(Invoice invoice, List<ProductoPed> oldProducts) throws DAOException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            Object[] parameters = {
                invoice.getTipoEntrega(),
                invoice.getValor(),
                invoice.getNumDeliverys(),
                invoice.getValorDelivery(),
                invoice.getDescuento(),
                invoice.getIdCliente(),
                invoice.getIdWaitress(),
                invoice.getTable(),
                invoice.getCiclo(),
                invoice.isService(),
                invoice.getPorcService(),
                invoice.getStatus(),
                invoice.getFactura()
            };

            ps = sqlStatements.buildSQLStatement(conn, UPDATE_INVOICE_KEY, parameters);
            ps.executeUpdate();

            List<ProductoPed> products = invoice.getProducts();

            if (oldProducts != null && !oldProducts.equals(products)) {
                for (ProductoPed oldProduct : oldProducts) {
                    HashMap<Integer, HashMap> data = oldProduct.getData();
                    System.out.println(Arrays.toString(data.entrySet().toArray()));
                }
            }

            for (int i = 0; i < products.size(); i++) {
                ProductoPed product = products.get(i);
                Object[] parameters1 = {
                    invoice.getId(),
                    product.getProduct().getId(),
                    product.hasPresentation() ? product.getPresentation().getId() : 0,
                    product.getPrecio(),
                    product.getCantidad()
                };
                ps = sqlStatements.buildSQLStatement(conn, ADD_INVOICE_PRODUCT_KEY, parameters1);
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

//            conn.commit();
            conn.rollback();
        } catch (SQLException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Could not properly update the invoice", e);
        } catch (IOException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Could not properly update the invoice", e);
        } finally {
            DBManager.closeStatement(ps);
            DBManager.closeConnection(conn);
        }
    }

    public ArrayList<Invoice> getInvoiceLiteByQuery(String query) throws DAOException {

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Invoice invoice = null;
        ArrayList<Invoice> invoices = new ArrayList<>();
        try {
            conn = dataSource.getConnection();
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                invoice = new Invoice();
                invoice.setId(rs.getLong(1));
                invoice.setFactura(rs.getString(2));
                invoice.setFecha(rs.getTimestamp(3));
                invoice.setTipoEntrega(rs.getInt(4));
                invoice.setValor(rs.getBigDecimal(5));
                invoice.setNumDeliverys(rs.getInt(6));
                invoice.setValorDelivery(rs.getBigDecimal(7));
                invoice.setDescuento(rs.getDouble(8));
                invoice.setIdCliente(rs.getLong(9));
                invoice.setIdWaitress(rs.getInt(10));
                invoice.setTable(rs.getInt(11));
                invoice.setCiclo(rs.getLong(12));
                invoice.setNota(rs.getString(13));
                invoice.setService(rs.getBoolean(14));
                invoice.setPorcService(rs.getDouble(15));
                invoice.setStatus(rs.getInt(16));
                invoices.add(invoice);

                invoice.addProduct(new ProductoPed(new Product(rs.getInt(18), rs.getString(19), 0, "")));

            }
        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the Invoice: " + e);
        } finally {
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(ps);
            DBManager.closeConnection(conn);
        }
        return invoices;
    }

}
