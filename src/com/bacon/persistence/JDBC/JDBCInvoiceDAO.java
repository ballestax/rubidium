/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bacon.persistence.JDBC;

import com.bacon.DBManager;
import com.bacon.domain.AdditionalPed;
import com.bacon.domain.Ingredient;
import com.bacon.domain.Invoice;
import com.bacon.domain.ProductoPed;
import static com.bacon.persistence.JDBC.JDBCUtilDAO.GET_MAX_ID_KEY;
import com.bacon.persistence.SQLExtractor;
import com.bacon.persistence.SQLLoader;
import com.bacon.persistence.dao.DAOException;
import com.bacon.persistence.dao.InvoiceDAO;
import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 *
 * @author LuisR
 */
public class JDBCInvoiceDAO implements InvoiceDAO {

    public static final String TABLE_NAME = "invoices";
    public static final String NAMED_PARAM_WHERE = "{where}";
    public static final String NAMED_PARAM_ORDER_BY = "{orderby}";
    private static final Logger logger = Logger.getLogger(JDBCInvoiceDAO.class.getCanonicalName());
    private final DataSource dataSource;
    private final SQLLoader sqlStatements;
    protected static final String CREATE_INVOICES_TABLE_KEY = "CREATE_INVOICES_TABLE";
    protected static final String ADD_INVOICE_KEY = "ADD_INVOICE";
    protected static final String UPDATE_INVOICE_KEY = "UPDATE_INVOICE";
    protected static final String GET_INVOICE_KEY = "GET_INVOICE";
    protected static final String DELETE_INVOICE_KEY = "DELETE_INVOICE";
    protected static final String ADD_INVOICE_PRODUCT_KEY = "ADD_INVOICE_PRODUCT";

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
        String retrieveImporter;
        try {
            SQLExtractor sqlExtractorWhere = new SQLExtractor(query, SQLExtractor.Type.WHERE);;
            Map<String, String> namedParams = new HashMap<String, String>();
            namedParams.put(NAMED_PARAM_WHERE, sqlExtractorWhere.extractWhere());
            retrieveImporter = sqlStatements.getSQLString(GET_INVOICE_KEY, namedParams);

        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the invoice", e);
        } catch (IOException e) {
            throw new DAOException("Could not properly retrieve the invoice", e);
        }
        Connection conn = null;
        PreparedStatement retrieve = null;
        ResultSet rs = null;
        Invoice invoice = null;
        try {
            conn = dataSource.getConnection();
            retrieve = conn.prepareStatement(retrieveImporter);
            rs = retrieve.executeQuery();
            while (rs.next()) {
                invoice = new Invoice();
                invoice.setId(rs.getLong(1));
                invoice.setFactura(rs.getString(2));
                invoice.setFecha(rs.getDate(3));
                invoice.setTipoEntrega(rs.getInt(4));
                invoice.setValor(rs.getBigDecimal(5));
                invoice.setValorDelivery(rs.getBigDecimal(6));
                invoice.setDescuento(rs.getDouble(7));
                invoice.setIdCliente(rs.getLong(8));
                invoice.setIdWaitress(rs.getInt(9));
                invoice.setTable(rs.getInt(10));
                invoice.setCiclo(rs.getLong(11));                
                invoice.setNota(rs.getString(12));
            }
        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the Invoice: " + e);
        } finally {
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

    public ArrayList<Invoice> getInvoiceList(String where, String orderBy) throws DAOException {
        String retrieveInvoices;
        ArrayList<Invoice> invoices = new ArrayList<>();
        try {
            SQLExtractor sqlExtractorWhere = new SQLExtractor(where, SQLExtractor.Type.WHERE);;
            SQLExtractor sqlExtractorOrderBy = new SQLExtractor(orderBy, SQLExtractor.Type.ORDER_BY);;
            Map<String, String> namedParams = new HashMap<>();
            namedParams.put(NAMED_PARAM_WHERE, sqlExtractorWhere.extractWhere());
            namedParams.put(NAMED_PARAM_ORDER_BY, sqlExtractorOrderBy.extractOrderBy());
            retrieveInvoices = sqlStatements.getSQLString(GET_INVOICE_KEY, namedParams);

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
            retrieve = conn.prepareStatement(retrieveInvoices);
            rs = retrieve.executeQuery();

            while (rs.next()) {
                invoice = new Invoice();

                invoice.setId(rs.getLong(1));
                invoice.setFactura(rs.getString(2));
                invoice.setFecha(rs.getDate(3));
                invoice.setTipoEntrega(rs.getInt(4));
                invoice.setValor(rs.getBigDecimal(5));
                invoice.setValorDelivery(rs.getBigDecimal(6));
                invoice.setDescuento(rs.getDouble(7));
                invoice.setIdCliente(rs.getLong(8));
                invoice.setIdWaitress(rs.getInt(9));
                invoice.setTable(rs.getInt(10));
                invoice.setCiclo(rs.getLong(11));                
                invoice.setNota(rs.getString(12));

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
                invoice.getFecha(),
                invoice.getTipoEntrega(),
                invoice.getValor(),
                invoice.getValorDelivery(),
                invoice.getDescuento(),
                invoice.getIdCliente(),                
                invoice.getIdWaitress(),
                invoice.getTable(),
                invoice.getCiclo(),
                invoice.getNota()
            };
            ps = sqlStatements.buildSQLStatement(conn, ADD_INVOICE_KEY, parameters);

            ps.executeUpdate();

            int idInvoice = 0;

            Map<String, String> namedParams = new HashMap<>();
            namedParams.put(JDBCDAOFactory.NAMED_PARAM_TABLE, "invoices");
            String query = sqlStatements.getSQLString(GET_MAX_ID_KEY, namedParams);
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                idInvoice = rs.getInt(1);
            }

            List<ProductoPed> products = invoice.getProducts();

            for (int i = 0; i < products.size(); i++) {
                ProductoPed product = products.get(i);
                Object[] parameters1 = {
                    idInvoice,
                    product.getProduct().getId(),
                    product.getProduct().getPrice(),
                    product.getCantidad()
                };
                ps = sqlStatements.buildSQLStatement(conn, ADD_INVOICE_PRODUCT_KEY, parameters1);
                ps.executeUpdate();

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
                    Ingredient eclusion = exclusions.get(k);
                    Object[] parameters3 = {
                        idProduct,
                        eclusion.getId()
                    };
                    ps = sqlStatements.buildSQLStatement(conn, JDBCUtilDAO.ADD_EXCLUSION_PRODUCT_KEY, parameters3);
                    ps.executeUpdate();
                }
            }

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
                invoice.getFecha(),
                invoice.getTipoEntrega(),
                invoice.getValor(),
                invoice.getValorDelivery(),
                invoice.getDescuento(),
                invoice.getIdCliente(),
                invoice.getIdWaitress(),
                invoice.getTable(),
                invoice.getCiclo()
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
        String retrieve = query;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Invoice invoice = null;
        ArrayList<Invoice> invoices = new ArrayList<>();
        try {
            conn = dataSource.getConnection();
            ps = conn.prepareStatement(retrieve);
            rs = ps.executeQuery();
            while (rs.next()) {
                invoice = new Invoice();
                invoice.setId(rs.getLong(1));
                invoice.setFactura(rs.getString(2));
                invoice.setFecha(rs.getDate(3));
                invoice.setTipoEntrega(rs.getInt(4));
                invoice.setValor(rs.getBigDecimal(5));
                invoice.setValorDelivery(rs.getBigDecimal(6));
                invoice.setDescuento(rs.getDouble(7));
                invoice.setIdCliente(rs.getLong(8));
                invoice.setIdWaitress(rs.getInt(9));
                invoice.setTable(rs.getInt(10));
                invoice.setCiclo(rs.getLong(11));                
                invoice.setNota(rs.getString(12));
                invoices.add(invoice);
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
