/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bacon.persistence.JDBC;

import com.bacon.DBManager;
import com.bacon.domain.Item;
import static com.bacon.persistence.JDBC.JDBCUtilDAO.GET_MAX_ID_KEY;
import com.bacon.persistence.SQLExtractor;
import com.bacon.persistence.SQLLoader;
import com.bacon.persistence.dao.DAOException;
import com.bacon.persistence.dao.ItemDAO;
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
public class JDBCItemDAO implements ItemDAO {

    public static final String TABLE_NAME = "inventory";
    public static final String NAMED_PARAM_WHERE = "{where}";
    public static final String NAMED_PARAM_ORDER_BY = "{orderby}";
    private static final Logger logger = Logger.getLogger(JDBCItemDAO.class.getCanonicalName());
    private final DataSource dataSource;
    private final SQLLoader sqlStatements;
    protected static final String CREATE_INVENTORY_TABLE_KEY = "CREATE_INVENTORY_TABLE";
    protected static final String ADD_ITEM_KEY = "ADD_ITEM";
    protected static final String UPDATE_ITEM_KEY = "UPDATE_ITEM";
    protected static final String GET_ITEM_KEY = "GET_ITEM";
    protected static final String DELETE_ITEM_KEY = "DELETE_ITEM";
    protected static final String DELETE_ITEM_PRES_KEY = "DELETE_ITEM_PRES";
    public static final String ADD_INVENTORY_PRODUCT_KEY = "ADD_INVENTORY_PRODUCT";
    public static final String ADD_INVENTORY_PRESENTATION_KEY = "ADD_INVENTORY_PRESENTATION";

    public JDBCItemDAO(DataSource dataSource, SQLLoader sqlStatements) throws DAOException {
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
            ps = sqlStatements.buildSQLStatement(conn, CREATE_INVENTORY_TABLE_KEY);
            ps.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot create inventory table", e);
        } catch (IOException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot create inventory table", e);
        } finally {
            DBManager.closeStatement(ps);
            DBManager.closeConnection(conn);
        }
    }

    public Item getItemBy(String query) throws DAOException {
        return getItemList(query, "").get(0);
    }

    public ArrayList<Item> getItemsBy(String query) throws DAOException {
        return getItemList(query, "");
    }

    @Override
    public Item getItem(int id) throws DAOException {
        return getItemBy("id=" + id);
    }

    @Override
    public ArrayList<Item> getItemList() throws DAOException {
        return getItemList("", "");
    }

    public ArrayList<Item> getItemList(String where, String orderBy) throws DAOException {
        String retrieveItems;
        ArrayList<Item> items = new ArrayList<>();
        try {
            SQLExtractor sqlExtractorWhere = new SQLExtractor(where, SQLExtractor.Type.WHERE);
            SQLExtractor sqlExtractorOrderBy = new SQLExtractor(orderBy, SQLExtractor.Type.ORDER_BY);
            Map<String, String> namedParams = new HashMap<>();
            namedParams.put(NAMED_PARAM_WHERE, sqlExtractorWhere.extractWhere());
            namedParams.put(NAMED_PARAM_ORDER_BY, sqlExtractorOrderBy.extractOrderBy());
            retrieveItems = sqlStatements.getSQLString(GET_ITEM_KEY, namedParams);

        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the Item List", e);
        } catch (IOException e) {
            throw new DAOException("Could not properly retrieve the Item List", e);
        }

        Connection conn = null;
        PreparedStatement retrieve = null;
        ResultSet rs = null;
        Item item = null;
        try {
            conn = dataSource.getConnection();
            retrieve = conn.prepareStatement(retrieveItems);
            rs = retrieve.executeQuery();

            while (rs.next()) {
                item = new Item();
                item.setId(rs.getInt("id"));
                item.setName(rs.getString("name"));
                item.setQuantity(rs.getDouble("quantity"));
                item.setMeasure(rs.getString("measure"));
                item.setCost(rs.getBigDecimal("cost"));
                item.setPrice(rs.getBigDecimal("price"));
                item.setLocation(rs.getInt("location"));
                item.setStock(rs.getDouble("stock"));
                item.setStockMin(rs.getDouble("stockMin"));
                item.setAverage(rs.getBigDecimal("average"));
                item.setInit(rs.getDouble("init"));
                item.setOnlyDelivery(rs.getBoolean("onlyDelivery"));
                item.setSnapshot(rs.getBoolean("snapshot"));
                item.setCreatedTime(rs.getDate("createdTime"));
                item.setUpdateTime(rs.getDate("lastUpdatedTime"));
                item.setUser(rs.getString("user"));
                item.setTags(rs.getString("tags"));
                items.add(item);
            }
        } catch (SQLException e) {
            throw new DAOException("Could not proper retrieve the Item: " + e);
        } finally {
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(retrieve);
            DBManager.closeConnection(conn);
        }
        return items;
    }

    @Override
    public void addItem(Item item) throws DAOException {
        if (item == null) {
            throw new IllegalArgumentException("Null item");
        }
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            Object[] parameters = {
                item.getName(),
                item.getQuantity(),
                item.getMeasure(),
                item.getCost(),
                item.getPrice(),
                item.getLocation(),
                item.getStock(),
                item.getStockMin(),
                item.getAverage(),
                item.getInit(),
                item.isOnlyDelivery(),
<<<<<<< HEAD
                item.getTagsSt(),                
=======
                item.isSnapshot(),
>>>>>>> add_feature_snapshot
                item.getUser()
            };
            ps = sqlStatements.buildSQLStatement(conn, ADD_ITEM_KEY, parameters);
            ps.executeUpdate();

            int idItem = 0;

            Map<String, String> namedParams = new HashMap<>();
            namedParams.put(JDBCDAOFactory.NAMED_PARAM_TABLE, "inventory");
            String query = sqlStatements.getSQLString(GET_MAX_ID_KEY, namedParams);
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                idItem = rs.getInt(1);
            }

            List<Object[]> presList = item.getPresentations();

            for (int i = 0; i < presList.size(); i++) {
                int idProd = Integer.parseInt(presList.get(i)[0].toString());
                int idPres = Integer.parseInt(presList.get(i)[1].toString());
                double cant = Double.parseDouble(presList.get(i)[2].toString());

                if (idPres != 0) {
                    Object[] parameters1 = {idItem, idProd, idPres, cant};
                    ps = sqlStatements.buildSQLStatement(conn, ADD_INVENTORY_PRESENTATION_KEY, parameters1);
                    
                }else{
                    Object[] parameters1 = {idItem, idProd, cant};
                    ps = sqlStatements.buildSQLStatement(conn, ADD_INVENTORY_PRODUCT_KEY, parameters1);
    
                
                }
                ps.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot add Item", e);
        } catch (IOException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot add Item", e);
        } finally {
            DBManager.closeStatement(ps);
            DBManager.closeConnection(conn);
        }
    }

    @Override
    public void deleteItem(int id) throws DAOException {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            Object[] parameters = {id};
            ps = sqlStatements.buildSQLStatement(conn, DELETE_ITEM_KEY, parameters);
            ps.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot delete the item", e);
        } catch (IOException e) {
            throw new DAOException("Cannot delete the item", e);
        } finally {
            DBManager.closeStatement(ps);
            DBManager.closeConnection(conn);
        }
    }

    @Override
    public void updateItem(Item item) throws DAOException {
        Connection conn = null;
        PreparedStatement update = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            Object[] parameters = {
                item.getCost(),
                item.getPrice(),                
                item.getStock(),
                item.getStockMin(),
                item.isOnlyDelivery(),
<<<<<<< HEAD
                item.getTagsSt(),
=======
                item.isSnapshot(),
>>>>>>> add_feature_snapshot
                item.getId()
            };
            update = sqlStatements.buildSQLStatement(conn, UPDATE_ITEM_KEY, parameters);
            update.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Could not properly update the item", e);
        } catch (IOException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Could not properly update the item", e);
        } finally {
            DBManager.closeStatement(update);
            DBManager.closeConnection(conn);
        }
    }
    
    public void updateItemPres(Item item) throws DAOException {
        if (item == null) {
            throw new IllegalArgumentException("Null item");
        }
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            
            Object[] parameters = {item.getId()};
//            System.out.println("deleting id:"+item.getId());
            ps = sqlStatements.buildSQLStatement(conn, DELETE_ITEM_PRES_KEY, parameters);
            ps.executeUpdate();
            
            long idItem = item.getId();
            
            List<Object[]> presList = item.getPresentations();

            for (int i = 0; i < presList.size(); i++) {
                int idProd = Integer.parseInt(presList.get(i)[0].toString());
                int idPres = Integer.parseInt(presList.get(i)[1].toString());
                double cant = Double.parseDouble(presList.get(i)[2].toString());

                if (idPres != 0) {
                    Object[] parameters1 = {idItem, idProd, idPres, cant};
                    ps = sqlStatements.buildSQLStatement(conn, ADD_INVENTORY_PRESENTATION_KEY, parameters1);
                }else{
                    Object[] parameters1 = {idItem, idProd, cant};
                    ps = sqlStatements.buildSQLStatement(conn, ADD_INVENTORY_PRODUCT_KEY, parameters1);
                }
                ps.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot update Item pres", e);
        } catch (IOException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot update Item pres", e);
        } finally {
            DBManager.closeStatement(ps);
            DBManager.closeConnection(conn);
        }
    }

}
