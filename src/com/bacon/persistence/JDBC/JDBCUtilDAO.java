/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bacon.persistence.JDBC;

import com.bacon.Aplication;
import com.bacon.DBManager;
import com.bacon.domain.Category;
import com.bacon.domain.Cycle;
import com.bacon.domain.Ingredient;
import com.bacon.domain.InventoryEvent;
import com.bacon.domain.Item;
import com.bacon.domain.OtherProduct;
import com.bacon.domain.Permission;
import com.bacon.domain.Presentation;
import com.bacon.domain.Rol;
import com.bacon.domain.Table;
import com.bacon.domain.User;
import com.bacon.domain.Waiter;
import com.bacon.persistence.SQLExtractor;
import com.bacon.persistence.SQLLoader;
import com.bacon.persistence.dao.DAOException;
import com.bacon.persistence.dao.UtilDAO;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 *
 * @author ballestax
 */
public class JDBCUtilDAO implements UtilDAO {

    private DataSource dataSource;
    private SQLLoader sqlStatements;

    public static final String NAMED_PARAM_WHERE = "{where}";
    public static final String NAMED_PARAM_ORDER_BY = "{orderby}";
    public static final String COUNT_TABLE_KEY = "CHECK_TABLE";
    public static final String TABLE_NAME = "types_vehicle";
    public static final String CREATE_RESPONSIBLES_TABLE_KEY = "CREATE_RESPONSIBLES_TABLE";
    public static final String CREATE_ROLES_TABLE_KEY = "CREATE_ROLES_TABLE";
    public static final String CREATE_ROLE_USER_TABLE_KEY = "CREATE_ROLE_USER_TABLE";
    public static final String CREATE_PERMISSIONS_TABLE_KEY = "CREATE_PERMISSIONS_TABLE";
    public static final String GET_ROLES_LIST_KEY = "GET_ROLES_LIST";
    public static final String INSERT_ROL_KEY = "INSERT_ROL";
    public static final String GET_ROLE_KEY = "GET_ROLE";
    public static final String DELETE_ROLE_KEY = "DELETE_ROL";
    public static final String GET_PERMISSIONS_LIST_KEY = "GET_PERMISSIONS_LIST";
    public static final String INSERT_PERMISSION_KEY = "INSERT_PERMISSION";
    public static final String UPDATE_ROL_KEY = "UPDATE_ROL";
    public static final String INSERT_PERMISSION_ROLE_KEY = "INSERT_PERMISSION_ROLE";
    public static final String CREATE_PERMISSION_ROLE_TABLE_KEY = "CREATE_PERMISSION_ROLE_TABLE";
    public static final String GET_PERMISSION_ROLE_LIST_KEY = "GET_PERMISSION_ROLE_LIST";
    public static final String GET_PERMISSION_BY_ROLE_LIST_KEY = "GET_PERMISSION_BY_ROLE_LIST";
    public static final String DELETE_PERMISSION_ROLE_KEY = "DELETE_PERMISSION_ROLE";
    public static final String CREATE_PRODUCT_INGREDIENT_TABLE_KEY = "CREATE_PRODUCT_INGREDIENT_TABLE";
    public static final String GET_INGREDIENTS_BY_PRODUCT_KEY = "GET_INGREDIENTS_BY_PRODUCT";
    public static final String GET_PRESENTATIONS_BY_PRODUCT_KEY = "GET_PRESENTATIONS_BY_PRODUCT";
    public static final String GET_ALL_PRESENTATIONS_BY_PRODUCT_KEY = "GET_ALL_PRESENTATIONS_BY_PRODUCT";
    public static final String GET_PRODUCTS_BY_VARPRICE_RANK_LIST_KEY = "GET_PRODUCTS_BY_VARPRICE_RANK_LIST";

    public static final String CREATE_WAITERS_TABLE_KEY = "CREATE_WAITERS_TABLE";
    public static final String CREATE_TABLES_TABLE_KEY = "CREATE_TABLES_TABLE";

    public static final String GET_TABLES_LIST_KEY = "GET_TABLES";
    public static final String GET_WAITERS_LIST_KEY = "GET_WAITERS";

    public static final String CREATE_CYCLES_TABLE_KEY = "CREATE_CYCLES_TABLE";
    public static final String ADD_CYCLE_KEY = "ADD_CYCLE";
    public static final String GET_CYCLE_KEY = "GET_CYCLE";
    public static final String UPDATE_CYCLE_KEY = "UPDATE_CYCLE";

    public static final String CREATE_INVENTORY_SNAPSHOT_TABLE_KEY = "CREATE_INVENTORY_SNAPSHOT_TABLE";
    public static final String ADD_INVENTARY_SNAPSHOT_ITEM_KEY = "ADD_INVENTORY_SNAPSHOT_ITEM";
    public static final String GET_INVENTORY_SNAPSHOT_ITEM_KEY = "GET_INVENTORY_SNAPSHOT_ITEM";
    public static final String COUNT_ITEM_SNAP_EVENT_KEY = "COUNT_ITEM_SNAP_EVENT";
    public static final String COUNT_ITEM_CONCILIATION_EVENT_KEY = "COUNT_ITEM_CONCILIATION_EVENT";

    public static final String CREATE_INVOICE_PRODUCT_TABLE_KEY = "CREATE_INVOICE_PRODUCT_TABLE";
    public static final String CREATE_ADDITIONAL_PRODUCT_TABLE_KEY = "CREATE_ADDITIONAL_PRODUCT_TABLE";
    public static final String ADD_ADDITIONAL_PRODUCT_KEY = "ADD_ADDITIONAL_PRODUCT";
    public static final String CREATE_EXCLUSION_PRODUCT_TABLE_KEY = "CREATE_EXCLUSION_PRODUCT_TABLE";
    public static final String ADD_EXCLUSION_PRODUCT_KEY = "ADD_EXCLUSION_PRODUCT";
    public static final String CREATE_PRESENTATION_PRODUCT_TABLE_KEY = "CREATE_PRESENTATION_PRODUCT_TABLE";
    public static final String ADD_PRESENTATION_PRODUCT_KEY = "ADD_PRESENTATION_PRODUCT";
    public static final String GET_PRESENTATION_BY_DEFAULT_KEY = "GET_PRESENTATION_BY_DEFAULT";
    public static final String GET_PRESENTATION_KEY = "GET_PRESENTATION";

    public static final String CREATE_OTHER_PRODUCTS_TABLE_KEY = "CREATE_OTHER_PRODUCTS_TABLE";
    public static final String ADD_OTHER_PRODUCT_KEY = "ADD_OTHER_PRODUCT";

    public static final String CREATE_INVOICE_OTHER_PRODUCT_TABLE_KEY = "CREATE_INVOICE_OTHER_PRODUCT_TABLE";
    public static final String ADD_INVOICE_OTHER_PRODUCT_KEY = "ADD_INVOICE_OTHER_PRODUCT";

    public static final String CREATE_EXPENSES_INCOMES_TABLE_KEY = "CREATE_EXPENSES_INCOMES_TABLE";
    public static final String ADD_EXPENSE_INCOME_KEY = "ADD_EXPENSE_INCOME";
    public static final String CREATE_EXPENSES_CATEGORIES_TABLE_KEY = "CREATE_EXPENSES_CATEGORIES_TABLE";

    public static final String CHECK_INVENTORY_KEY = "CHECK_INVENTORY";
    public static final String CHECK_INVENTORY_PRODUCT_KEY = "CHECK_INVENTORY_PRODUCT";

    protected static final String CHECK_TABLE_EMPTY_KEY = "CHECK_TABLE";
    protected static final String INSERT_ROLE_USER_KEY = "INSERT_ROLE_USER";
    protected static final String HAS_PERMISSION_KEY = "HAS_PERMISSION";
    protected static final String GET_USER_ROLE_KEY = "GET_USER_ROLE";

    public static final String GET_FIRST_REGISTRO_KEY = "GET_FIRST_REGISTRO";
    public static final String GET_LAST_REGISTRO_KEY = "GET_LAST_REGISTRO";

    public static final String GET_CATEGORIES_SORTED_KEY = "GET_CATEGORIES_SORTED";
    public static final String GET_ALL_CATEGORIES_KEY = "GET_ALL_CATEGORIES";
    public static final String GET_CATEGORY_KEY = "GET_CATEGORY";

    public static final String CREATE_UNITS_TABLE_KEY = "CREATE_UNITS_TABLE";
    public static final String ADD_UNIT_KEY = "ADD_UNIT";
    public static final String GET_UNIT_KEY = "GET_UNIT";
    public static final String DELETE_UNIT_KEY = "DELETE_UNIT";
    public static final String UPDATE_UNIT_KEY = "UPDATE_UNIT";

    public static final String CREATE_PAYMENTS_TABLE_KEY = "CREATE_PAYMENTS_TABLE";
    public static final String ADD_PAYMENT_KEY = "ADD_PAYMENT";
    public static final String GET_PAYMENT_KEY = "GET_PAYMENT";
    public static final String DELETE_PAYMENT_KEY = "DELETE_PAYMENT";
    public static final String UPDATE_PAYMENT_KEY = "UPDATE_PAYMENT";

    public static final String CREATE_INVENTORY_PRODUCT_TABLE_KEY = "CREATE_INVENTORY_PRODUCT_TABLE";
    public static final String UPDATE_INVENTORY_QUANTITY_KEY = "UPDATE_INVENTORY_QUANTITY";
    public static final String ADD_INVENTORY_QUANTITY_KEY = "ADD_INVENTORY_QUANTITY";

    public static final String GET_TAGS_INVENTORY_LIST_KEY = "GET_TAGS_INVENTORY_LIST";

    public static final String CREATE_INVENTORY_REGISTER_TABLE_KEY = "CREATE_INVENTORY_REGISTER_TABLE";
    public static final String ADD_INVENTORY_EVENT_KEY = "ADD_INVENTORY_EVENT";
    public static final String GET_INVENTORY_EVENT_LIST_KEY = "GET_INVENTORY_EVENT_LIST";

    public static final String GET_PRESENTATIONS_OUT_INVENTORY_KEY = "GET_PRESENTATIONS_OUT_INVENTORY";
    public static final String GET_PRODUCTS_OUT_INVENTORY_KEY = "GET_PRODUCTS_OUT_INVENTORY";

    public static final String GET_PRESENTATIONS_BY_ITEM_KEY = "GET_ITEM_PRESENTATIONS";

    public static final String GET_PRODUCTS_SALES_KEY = "GET_PRODUCTS_SALES";

    public static final String CREATE_CATEGORIES_TABLE_KEY = "CREATE_CATEGORIES_TABLE";
    public static final String GET_CATEGORIES_LIST_KEY = "GET_CATEGORY_PROD";
    public static final String ADD_CATEGORY_KEY = "ADD_CATEGORY";
    public static final String UPDATE_CATEGORY_KEY = "UPDATE_CATEGORY";
    
    public static final String GET_EXPENSES_CATEGORIES_LIST_KEY = "GET_EXPENSES_CATEGORY";
    public static final String ADD_EXPENSES_CATEGORY_KEY = "ADD_EXPENSES_CATEGORY";
    public static final String UPDATE_EXPENSES_CATEGORY_KEY = "UPDATE_EXPENSES_CATEGORY";

    public static final String ADD_PRESENTATION_KEY = "ADD_PRESENTATION";
    public static final String UPDATE_PRESENTATION_KEY = "UPDATE_PRESENTATION";
    public static final String UPDATE_PRESENTATION_TO_DEFAULT_KEY = "UPDATE_PRESENTATION_TO_DEFAULT";
    public static final String UPDATE_PRESENTATION_TO_DEFAULT_BY_ID_KEY = "UPDATE_PRESENTATION_TO_DEFAULT_BY_ID";

    private static final Logger logger = Logger.getLogger(JDBCUtilDAO.class.getCanonicalName());

    public static final String NAMED_PARAM_KEY = "{key}";
    public static final String GET_MAX_ID_KEY = "GET_MAX_ID";
    public static final String EXIST_CLAVE_KEY = "EXIST_CLAVE";
    public static final String EXIST_CLAVE_MULT_KEY = "EXIST_CLAVE_MULT";

    public JDBCUtilDAO(DataSource dataSource, SQLLoader sqlStatements) {
        this.dataSource = dataSource;
        this.sqlStatements = sqlStatements;
    }

    public void init() throws DAOException {

        String TABLE_NAME = "roles";
        createTable(TABLE_NAME, CREATE_ROLES_TABLE_KEY);

        TABLE_NAME = "permissions";
        createTable(TABLE_NAME, CREATE_PERMISSIONS_TABLE_KEY);

        TABLE_NAME = "permission_role";
        createTable(TABLE_NAME, CREATE_PERMISSION_ROLE_TABLE_KEY);

        TABLE_NAME = "role_user";
        createTable(TABLE_NAME, CREATE_ROLE_USER_TABLE_KEY);

        TABLE_NAME = "product_ingredient";
        createTable(TABLE_NAME, CREATE_PRODUCT_INGREDIENT_TABLE_KEY);

        TABLE_NAME = "waiters";
        createTable(TABLE_NAME, CREATE_WAITERS_TABLE_KEY);

        TABLE_NAME = "tables";
        createTable(TABLE_NAME, CREATE_TABLES_TABLE_KEY);

        TABLE_NAME = "cycles";
        createTable(TABLE_NAME, CREATE_CYCLES_TABLE_KEY);

        TABLE_NAME = "invoice_product";
        createTable(TABLE_NAME, CREATE_INVOICE_PRODUCT_TABLE_KEY);

        TABLE_NAME = "additional_product";
        createTable(TABLE_NAME, CREATE_ADDITIONAL_PRODUCT_TABLE_KEY);

        TABLE_NAME = "exclusion_product";
        createTable(TABLE_NAME, CREATE_EXCLUSION_PRODUCT_TABLE_KEY);

        TABLE_NAME = "presentation_product";
        createTable(TABLE_NAME, CREATE_PRESENTATION_PRODUCT_TABLE_KEY);

        TABLE_NAME = "other_products";
        createTable(TABLE_NAME, CREATE_OTHER_PRODUCTS_TABLE_KEY);

        TABLE_NAME = "invoice_otherproduct";
        createTable(TABLE_NAME, CREATE_INVOICE_OTHER_PRODUCT_TABLE_KEY);

        TABLE_NAME = "expenses_incomes";
        createTable(TABLE_NAME, CREATE_EXPENSES_INCOMES_TABLE_KEY);

        TABLE_NAME = "expenses_categories";
        createTable(TABLE_NAME, CREATE_EXPENSES_CATEGORIES_TABLE_KEY);

        TABLE_NAME = "units";
        createTable(TABLE_NAME, CREATE_UNITS_TABLE_KEY);

        TABLE_NAME = "payments";
        createTable(TABLE_NAME, CREATE_PAYMENTS_TABLE_KEY);

        TABLE_NAME = "inventory_product";
        createTable(TABLE_NAME, CREATE_INVENTORY_PRODUCT_TABLE_KEY);

        TABLE_NAME = "inventory_register";
        createTable(TABLE_NAME, CREATE_INVENTORY_REGISTER_TABLE_KEY);

        TABLE_NAME = "inventory_snapshot";
        createTable(TABLE_NAME, CREATE_INVENTORY_SNAPSHOT_TABLE_KEY);

        TABLE_NAME = "categories";
        createTable(TABLE_NAME, CREATE_CATEGORIES_TABLE_KEY);

    }

    private void createTable(String tableName, String JDBC_KEY) throws DAOException {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            if (DBManager.tableExists(tableName, conn)) {
                return;
            }
            ps = sqlStatements.buildSQLStatement(conn, JDBC_KEY);
            ps.executeUpdate();
            conn.commit();
        } catch (SQLException | IOException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot create " + tableName + " table", e);
        } finally {
            DBManager.closeStatement(ps);
            DBManager.closeConnection(conn);
        }
    }

    public int countTableRows(String sql) throws DAOException {
        Connection conn = null;
        int count = 0;
        try {
            conn = dataSource.getConnection();
            count = DBManager.countTable(sql, conn, sqlStatements);
        } catch (SQLException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Could not properly retrieve the current TaskID", e);
        } finally {
            DBManager.closeConnection(conn);
        }
        return count;
    }

    public int existClave(String table, String column, String code) throws DAOException {
        String retrieve;
        try {
            Map<String, String> namedParams = new HashMap<String, String>();
            namedParams.put(JDBCDAOFactory.NAMED_PARAM_TABLE, table);
            namedParams.put(JDBCDAOFactory.NAMED_PARAM_QUERY, column);
            namedParams.put(NAMED_PARAM_KEY, code);
            retrieve = sqlStatements.getSQLString(EXIST_CLAVE_KEY, namedParams);
        } catch (IOException e) {
            throw new DAOException("Could not properly retrieve the outdated count", e);
        }
        Connection conn = null;
        PreparedStatement pSt = null;
        ResultSet rs = null;
        int count = 0;
        try {
            conn = dataSource.getConnection();
            pSt = conn.prepareStatement(retrieve);
            rs = pSt.executeQuery();

            while (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the outdated count: " + e);
        } finally {
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(pSt);
            DBManager.closeConnection(conn);
        }
        return count;
    }

    public int existClaveMult(String table, String column, String where) throws DAOException {
        String retrieve;
        try {
            Map<String, String> namedParams = new HashMap<String, String>();
            namedParams.put(JDBCDAOFactory.NAMED_PARAM_TABLE, table);
            namedParams.put(JDBCDAOFactory.NAMED_PARAM_QUERY, column);
            namedParams.put(NAMED_PARAM_KEY, where);
            retrieve = sqlStatements.getSQLString(EXIST_CLAVE_MULT_KEY, namedParams);
            System.out.println("retrieve = " + retrieve);
        } catch (IOException e) {
            throw new DAOException("Could not properly retrieve the outdated count", e);
        }
        Connection conn = null;
        PreparedStatement pSt = null;
        ResultSet rs = null;
        int count = 0;
        try {
            conn = dataSource.getConnection();
            pSt = conn.prepareStatement(retrieve);
            rs = pSt.executeQuery();

            while (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the outdated count: " + e);
        } finally {
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(pSt);
            DBManager.closeConnection(conn);
        }
        return count;
    }

    public int getMaxID(String table) throws DAOException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query;
        int maxID = 0;
        try {
            Map<String, String> namedParams = new HashMap<>();
            namedParams.put(JDBCDAOFactory.NAMED_PARAM_TABLE, table);
            query = sqlStatements.getSQLString(GET_MAX_ID_KEY, namedParams);
            conn = dataSource.getConnection();
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                maxID = rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DAOException("Cannot get max id for: " + table, e);
        } catch (IOException e) {
            throw new DAOException("Cannot get max id for: " + table, e);
        } finally {
            DBManager.closeStatement(ps);
            DBManager.closeConnection(conn);
        }
        return maxID;
    }

    public ArrayList<Rol> getRolesList() throws DAOException {
        String retrieveList;
        try {
            retrieveList = sqlStatements.getSQLString(GET_ROLES_LIST_KEY);
        } catch (IOException e) {
            throw new DAOException("Could not properly retrieve the roles list", e);
        }
        Connection conn = null;
        PreparedStatement retrieve = null;
        ResultSet rs = null;
        ArrayList<Rol> roles = new ArrayList<>();
        try {
            conn = dataSource.getConnection();
            retrieve = conn.prepareStatement(retrieveList);
            rs = retrieve.executeQuery();
            while (rs.next()) {
                Rol rol = new Rol();
                rol.setId(rs.getInt(1));
                rol.setName(rs.getString(2));
                rol.setDisplayName(rs.getString(3));
                rol.setDescription(rs.getString(4));
                roles.add(rol);
            }
        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the roles list: " + e);
        } finally {
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(retrieve);
            DBManager.closeConnection(conn);
        }
        return roles;
    }

    public void addRole(Rol role) throws DAOException {
        if (role == null) {
            throw new IllegalArgumentException("Null rol");
        }
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dataSource.getConnection();

            conn.setAutoCommit(false);
            Object[] parameters = {
                role.getName(),
                role.getDisplayName(),
                role.getDescription()
            };
            ps = sqlStatements.buildSQLStatement(conn, INSERT_ROL_KEY, parameters);

            ps.executeUpdate();

            conn.commit();
            logger.log(Level.INFO, "Added rol:" + role.getName());
        } catch (SQLException | IOException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot add Rol", e);
        } finally {
            DBManager.closeStatement(ps);
            DBManager.closeConnection(conn);
        }
    }

    public void addPermissionRole(Rol role, ArrayList<Permission> permissions) throws DAOException {
        if (role == null) {
            throw new IllegalArgumentException("Null rol");
        }
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            for (int i = 0; i < permissions.size(); i++) {
                Permission perm = permissions.get(i);

                Object[] parameters = {
                    perm.getId(),
                    role.getId(),};
                ps = sqlStatements.buildSQLStatement(conn, INSERT_PERMISSION_ROLE_KEY, parameters);
                ps.executeUpdate();
            }
            conn.commit();
            logger.log(Level.INFO, "Added permission_role:" + role.getName());
        } catch (SQLException | IOException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot add permission_role", e);
        } finally {
            DBManager.closeStatement(ps);
            DBManager.closeConnection(conn);
        }
    }

    public Rol getRole(String name) throws DAOException {
        String retrieveList = null;
        try {
            SQLExtractor sqlExtractorWhere = new SQLExtractor("name='" + name + "'", SQLExtractor.Type.WHERE);;
            Map<String, String> namedParams = new HashMap<String, String>();
            namedParams.put(NAMED_PARAM_WHERE, sqlExtractorWhere.extractWhere());
            retrieveList = sqlStatements.getSQLString(GET_ROLES_LIST_KEY, namedParams);
        } catch (IOException e) {
            throw new DAOException("Could not properly retrieve the role", e);
        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the role: " + e);
        }
        Connection conn = null;
        PreparedStatement retrieve = null;
        ResultSet rs = null;
        Rol rol = new Rol();
        try {
            conn = dataSource.getConnection();
            retrieve = conn.prepareStatement(retrieveList);
            rs = retrieve.executeQuery();
            while (rs.next()) {
                rol.setId(rs.getInt(1));
                rol.setName(rs.getString(2));
                rol.setDisplayName(rs.getString(3));
                rol.setDescription(rs.getString(4));
            }
        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the roles list: " + e);
        } finally {
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(retrieve);
            DBManager.closeConnection(conn);
        }
        return rol;
    }

    public Object[] getRole(String field, String value) throws DAOException {
        String retrieveList = null;
        try {
            SQLExtractor sqlExtractorWhere = new SQLExtractor(field + "='" + value + "'", SQLExtractor.Type.WHERE);;
            Map<String, String> namedParams = new HashMap<String, String>();
            namedParams.put(NAMED_PARAM_WHERE, sqlExtractorWhere.extractWhere());
            retrieveList = sqlStatements.getSQLString(GET_ROLE_KEY, namedParams);
        } catch (IOException e) {
            throw new DAOException("Could not properly retrieve the role", e);
        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the role: " + e);
        }
        Connection conn = null;
        PreparedStatement retrieve = null;
        ResultSet rs = null;
        Object[] data = new Object[2];
        try {
            conn = dataSource.getConnection();
            retrieve = conn.prepareStatement(retrieveList);
            rs = retrieve.executeQuery();
            while (rs.next()) {
                data[0] = (rs.getInt(1));
                data[1] = (rs.getString(2));
            }
        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the role " + e);
        } finally {
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(retrieve);
            DBManager.closeConnection(conn);
        }
        return data;
    }

    public void deleteRole(String name) throws DAOException {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            Object[] parameters = {name};
            ps = sqlStatements.buildSQLStatement(conn, DELETE_ROLE_KEY, parameters);
            ps.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot delete the role", e);
        } catch (IOException e) {
            throw new DAOException("Cannot delete the role", e);
        } finally {
            DBManager.closeStatement(ps);
        }
    }

    public ArrayList<Permission> getPermissionList(String where) throws DAOException {
        String retrieveList;
        try {
            SQLExtractor sqlExtractorWhere = new SQLExtractor(where, SQLExtractor.Type.WHERE);;
            Map<String, String> namedParams = new HashMap<>();
            namedParams.put(NAMED_PARAM_WHERE, sqlExtractorWhere.extractWhere());
            retrieveList = sqlStatements.getSQLString(GET_PERMISSIONS_LIST_KEY, namedParams);
        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the Permission list", e);
        } catch (IOException e) {
            throw new DAOException("Could not properly retrieve the Permission list", e);
        }
        Connection conn = null;
        PreparedStatement retrieve = null;
        ResultSet rs = null;
        ArrayList<Permission> permissions = new ArrayList<>();
        try {
            conn = dataSource.getConnection();
            retrieve = conn.prepareStatement(retrieveList);
            rs = retrieve.executeQuery();
            while (rs.next()) {
                Permission permission = new Permission();
                permission.setId(rs.getInt(1));
                permission.setName(rs.getString(2));
                permission.setDisplayName(rs.getString(3));
                permission.setDescription(rs.getString(4));
                permissions.add(permission);
            }
        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the permissions list: " + e);
        } finally {
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(retrieve);
            DBManager.closeConnection(conn);
        }
        return permissions;
    }

    public ArrayList<Permission> getPermissionByRole(Rol rol) throws DAOException {
        String retrieveList;
        ArrayList<Permission> permissions = new ArrayList<>();
        Connection conn = null;
        PreparedStatement retrieve = null;
        ResultSet rs = null;
        Object[] parameters = {rol.getId()};
        try {
            conn = dataSource.getConnection();
            retrieve = sqlStatements.buildSQLStatement(conn, GET_PERMISSION_BY_ROLE_LIST_KEY, parameters);
            rs = retrieve.executeQuery();
            while (rs.next()) {
                Permission permission = new Permission();
                permission.setId(rs.getInt(1));
                permission.setName(rs.getString(2));
                permission.setDisplayName(rs.getString(3));
                permission.setDescription(rs.getString(4));
                permissions.add(permission);
            }
        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the permissions list: " + e);
        } catch (IOException e) {
            throw new DAOException("Could not properly retrieve the permissions list: " + e);
        } finally {
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(retrieve);
            DBManager.closeConnection(conn);
        }
        return permissions;
    }

    public void addPermission(Permission permission) throws DAOException {
        if (permission == null) {
            throw new IllegalArgumentException("Null permission");
        }
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            Object[] parameters = {
                permission.getName(),
                permission.getDisplayName(),
                permission.getDescription()
            };
            ps = sqlStatements.buildSQLStatement(conn, INSERT_PERMISSION_KEY, parameters);
            ps.executeUpdate();
            conn.commit();
        } catch (SQLException | IOException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot add Permission", e);
        } finally {
            DBManager.closeStatement(ps);
            DBManager.closeConnection(conn);
        }
    }

    public void updateRol(Rol rol) throws DAOException {
        Connection conn = null;
        PreparedStatement update = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            Object[] parameters = {
                rol.getName(),
                rol.getDisplayName(),
                rol.getDescription(),
                rol.getId()
            };
            update = sqlStatements.buildSQLStatement(conn, UPDATE_ROL_KEY, parameters);
            update.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Could not properly update the rol", e);
        } catch (IOException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Could not properly update the rol", e);
        } finally {
            DBManager.closeStatement(update);
            DBManager.closeConnection(conn);
        }
    }

    public void deletePermissionsByRole(int id) throws DAOException {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            Object[] parameters = {id};
            ps = sqlStatements.buildSQLStatement(conn, DELETE_PERMISSION_ROLE_KEY, parameters);
            ps.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot delete the permission to role", e);
        } catch (IOException e) {
            throw new DAOException("Cannot delete the permission to role", e);
        } finally {
            DBManager.closeStatement(ps);
        }
    }

    public boolean checkTableEmpty(String table) throws DAOException {

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs;

        try {
            Map<String, String> namedParams = new HashMap<>();
            namedParams.put(JDBCDAOFactory.NAMED_PARAM_TABLE, table);
            conn = dataSource.getConnection();
            ps = sqlStatements.buildSQLStatement(conn, CHECK_TABLE_EMPTY_KEY, namedParams);
            rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) == 0;
            }

        } catch (SQLException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot add User", e);
        } catch (IOException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot add User", e);
        } finally {
            DBManager.closeStatement(ps);
            DBManager.closeConnection(conn);
        }
        return false;
    }

    public void assignRoleToUser(User user, Rol role) throws DAOException {
        if (role == null) {
            throw new IllegalArgumentException("Null rol");
        }
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            Object[] parameters = {
                user.getId(),
                role.getId(),};
            ps = sqlStatements.buildSQLStatement(conn, INSERT_ROLE_USER_KEY, parameters);
            ps.executeUpdate();
            conn.commit();
            logger.log(Level.INFO, "Assigned role to user:" + user.getUsername() + ":" + role.getName());
        } catch (SQLException | IOException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot add user_role", e);
        } finally {
            DBManager.closeStatement(ps);
            DBManager.closeConnection(conn);
        }
    }

    public int hasPermission(int userID, int permissionID) throws DAOException {
        int result = -1;
        Connection conn = null;
        PreparedStatement retrieve = null;
        ResultSet rs = null;
        Object[] parameters = {userID, permissionID};
        try {
            conn = dataSource.getConnection();
            retrieve = sqlStatements.buildSQLStatement(conn, HAS_PERMISSION_KEY, parameters);
            rs = retrieve.executeQuery();
            while (rs.next()) {
                result = rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DAOException("Could not properly get has permissions: " + e);
        } catch (IOException e) {
            throw new DAOException("Could not properly get has permissions: " + e);
        } finally {
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(retrieve);
            DBManager.closeConnection(conn);
        }
        return result;
    }

    public String getUserRole(int userID) throws DAOException {
        String result = null;
        Connection conn = null;
        PreparedStatement retrieve = null;
        ResultSet rs = null;
        Object[] parameters = {userID};
        try {
            conn = dataSource.getConnection();
            retrieve = sqlStatements.buildSQLStatement(conn, GET_USER_ROLE_KEY, parameters);
            rs = retrieve.executeQuery();
            while (rs.next()) {
                result = rs.getString(1);
            }
        } catch (SQLException e) {
            throw new DAOException("Could not properly get user role: " + e);
        } catch (IOException e) {
            throw new DAOException("Could not properly get user role: " + e);
        } finally {
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(retrieve);
            DBManager.closeConnection(conn);
        }
        return result;
    }

    public ArrayList<Ingredient> getIngredientsByProduct(String code) throws DAOException {
        String retrieveList;
        ArrayList<Ingredient> ingredients = new ArrayList<>();
        Connection conn = null;
        PreparedStatement retrieve = null;
        ResultSet rs = null;
        Object[] parameters = {code};
        try {
            conn = dataSource.getConnection();
            retrieve = sqlStatements.buildSQLStatement(conn, GET_INGREDIENTS_BY_PRODUCT_KEY, parameters);
            rs = retrieve.executeQuery();
            while (rs.next()) {
                Ingredient ing = new Ingredient();
                ing.setId(rs.getInt(1));
                ing.setCode(rs.getString(2));
                ing.setName(rs.getString(3));
                ing.setMeasure(rs.getString(4));
                ing.setQuantity(rs.getInt(5));
                ing.setOpcional(rs.getBoolean(6));
                ingredients.add(ing);
            }
        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the ingredients list: " + e);
        } catch (IOException e) {
            throw new DAOException("Could not properly retrieve the ingredients list: " + e);
        } finally {
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(retrieve);
            DBManager.closeConnection(conn);
        }
        return ingredients;
    }

    public ArrayList<Table> getTablesList(String where, String order) throws DAOException {
        String retrieveList;
        try {
            SQLExtractor sqlExtractorWhere = new SQLExtractor(where, SQLExtractor.Type.WHERE);;
            SQLExtractor sqlExtractorOrder = new SQLExtractor(order, SQLExtractor.Type.ORDER_BY);
            Map<String, String> namedParams = new HashMap<>();
            namedParams.put(NAMED_PARAM_WHERE, sqlExtractorWhere.extractWhere());
            namedParams.put(NAMED_PARAM_ORDER_BY, sqlExtractorOrder.extractOrderBy());
            retrieveList = sqlStatements.getSQLString(GET_TABLES_LIST_KEY, namedParams);
        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the Tables list", e);
        } catch (IOException e) {
            throw new DAOException("Could not properly retrieve the Tables list", e);
        }
        Connection conn = null;
        PreparedStatement retrieve = null;
        ResultSet rs = null;
        ArrayList<Table> tables = new ArrayList<>();
        try {
            conn = dataSource.getConnection();
            retrieve = conn.prepareStatement(retrieveList);
            rs = retrieve.executeQuery();
            while (rs.next()) {
                Table table = new Table();
                table.setId(rs.getInt(1));
                table.setName(rs.getString(2));
                table.setStatus(rs.getInt(3));
                tables.add(table);
            }
        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the tables list: " + e);
        } finally {
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(retrieve);
            DBManager.closeConnection(conn);
        }
        return tables;
    }

    public ArrayList<Waiter> getWaitersList(String where, String order) throws DAOException {
        String retrieveList;
        try {
            SQLExtractor sqlExtractorWhere = new SQLExtractor(where, SQLExtractor.Type.WHERE);
            SQLExtractor sqlExtractorOrder = new SQLExtractor(order, SQLExtractor.Type.ORDER_BY);
            Map<String, String> namedParams = new HashMap<>();
            namedParams.put(NAMED_PARAM_WHERE, sqlExtractorWhere.extractWhere());
            namedParams.put(NAMED_PARAM_ORDER_BY, sqlExtractorOrder.extractOrderBy());
            retrieveList = sqlStatements.getSQLString(GET_WAITERS_LIST_KEY, namedParams);
        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the Waiters list", e);
        } catch (IOException e) {
            throw new DAOException("Could not properly retrieve the Waiters list", e);
        }
        Connection conn = null;
        PreparedStatement retrieve = null;
        ResultSet rs = null;
        ArrayList<Waiter> waiters = new ArrayList<>();
        try {
            conn = dataSource.getConnection();
            retrieve = conn.prepareStatement(retrieveList);
            rs = retrieve.executeQuery();
            while (rs.next()) {
                Waiter waiter = new Waiter();
                waiter.setId(rs.getInt(1));
                waiter.setName(rs.getString(2));
                waiter.setStatus(rs.getInt(3));
                waiter.setColor(rs.getString("color"));
                waiters.add(waiter);
            }
        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the waiters list: " + e);
        } finally {
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(retrieve);
            DBManager.closeConnection(conn);
        }
        return waiters;
    }

    public Date getFirstEntrada(String tabla, String field) throws DAOException {
        String retrieve;
        try {
            Map<String, String> namedParams = new HashMap<String, String>();
            namedParams.put(JDBCDAOFactory.NAMED_PARAM_TABLE, tabla);
            namedParams.put(JDBCDAOFactory.NAMED_PARAM_QUERY, field);
            retrieve = sqlStatements.getSQLString(EXIST_CLAVE_KEY, namedParams);
            retrieve = sqlStatements.getSQLString(GET_FIRST_REGISTRO_KEY, namedParams);
        } catch (IOException e) {
            throw new DAOException("Could not properly retrieve registro date", e);
        }
        Connection conn = null;
        PreparedStatement pSt = null;
        ResultSet rs = null;
        Date fecha = null;
        try {
            conn = dataSource.getConnection();
            pSt = conn.prepareStatement(retrieve);
            rs = pSt.executeQuery();
            while (rs.next()) {
                fecha = rs.getDate(1);
            }
        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the entrada date: " + e);
        } finally {
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(pSt);
            DBManager.closeConnection(conn);
        }
        return fecha;
    }

    public Date getLastEntrada(String tabla, String field) throws DAOException {
        String retrieve;
        try {
            Map<String, String> namedParams = new HashMap<String, String>();
            namedParams.put(JDBCDAOFactory.NAMED_PARAM_TABLE, tabla);
            namedParams.put(JDBCDAOFactory.NAMED_PARAM_QUERY, field);
            retrieve = sqlStatements.getSQLString(GET_LAST_REGISTRO_KEY, namedParams);
        } catch (IOException e) {
            throw new DAOException("Could not properly retrieve registro date", e);
        }
        Connection conn = null;
        PreparedStatement pSt = null;
        ResultSet rs = null;
        Date fecha = null;
        try {
            conn = dataSource.getConnection();
            pSt = conn.prepareStatement(retrieve);
            rs = pSt.executeQuery();
            while (rs.next()) {
                fecha = rs.getDate(1);
            }
        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the entrada date: " + e);
        } finally {
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(pSt);
            DBManager.closeConnection(conn);
        }
        return fecha;
    }

    public Object getMaxValue(String tabla, String field) throws DAOException {
        String retrieve;
        try {
            Map<String, String> namedParams = new HashMap<String, String>();
            namedParams.put(JDBCDAOFactory.NAMED_PARAM_TABLE, tabla);
            namedParams.put(JDBCDAOFactory.NAMED_PARAM_QUERY, field);
            retrieve = sqlStatements.getSQLString(GET_LAST_REGISTRO_KEY, namedParams);
        } catch (IOException e) {
            throw new DAOException("Could not properly retrieve registro date", e);
        }
        Connection conn = null;
        PreparedStatement pSt = null;
        ResultSet rs = null;
        Object max = null;
        try {
            conn = dataSource.getConnection();
            pSt = conn.prepareStatement(retrieve);
            rs = pSt.executeQuery();
            while (rs.next()) {
                max = rs.getObject(1);
            }
        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the entrada date: " + e);
        } finally {
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(pSt);
            DBManager.closeConnection(conn);
        }
        return max;
    }

    public ArrayList<Presentation> getPresentationsByProduct(long id) throws DAOException {
        String retrieveList;
        ArrayList<Presentation> presentations = new ArrayList<>();
        Connection conn = null;
        PreparedStatement retrieve = null;
        ResultSet rs = null;
        Object[] parameters = {id};
        try {
            conn = dataSource.getConnection();
            retrieve = sqlStatements.buildSQLStatement(conn, GET_PRESENTATIONS_BY_PRODUCT_KEY, parameters);

            rs = retrieve.executeQuery();
            while (rs.next()) {
                Presentation pres = new Presentation();
                pres.setId(rs.getInt(1));
                pres.setIDProd(rs.getInt(2));
                pres.setSerie(rs.getInt(3));
                pres.setName(rs.getString(4));
                pres.setPrice(rs.getDouble(5));
                pres.setDefault(rs.getBoolean(6));
                pres.setEnabled(rs.getBoolean(7));
                presentations.add(pres);
            }
        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the presentations list: " + e);
        } catch (IOException e) {
            throw new DAOException("Could not properly retrieve the presentations list: " + e);
        } finally {
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(retrieve);
            DBManager.closeConnection(conn);
        }
        return presentations;
    }

    public ArrayList<Presentation> getAllPresentationsByProduct(long id) throws DAOException {
        ArrayList<Presentation> presentations = new ArrayList<>();
        Connection conn = null;
        PreparedStatement retrieve = null;
        ResultSet rs = null;
        Object[] parameters = {id};
        try {
            conn = dataSource.getConnection();
            retrieve = sqlStatements.buildSQLStatement(conn, GET_ALL_PRESENTATIONS_BY_PRODUCT_KEY, parameters);

            rs = retrieve.executeQuery();
            while (rs.next()) {
                Presentation pres = new Presentation();
                pres.setId(rs.getInt(1));
                pres.setIDProd(rs.getInt(2));
                pres.setSerie(rs.getInt(3));
                pres.setName(rs.getString(4));
                pres.setPrice(rs.getDouble(5));
                pres.setDefault(rs.getBoolean(6));
                pres.setEnabled(rs.getBoolean(7));
                presentations.add(pres);
            }
        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the presentations list: " + e);
        } catch (IOException e) {
            throw new DAOException("Could not properly retrieve the presentations list: " + e);
        } finally {
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(retrieve);
            DBManager.closeConnection(conn);
        }
        return presentations;
    }

    public Presentation getPresentationDefault(long id) throws DAOException {
        Presentation pres = null;
        Connection conn = null;
        PreparedStatement retrieve = null;
        ResultSet rs = null;
        Object[] parameters = {id, 1};
        try {
            conn = dataSource.getConnection();
            retrieve = sqlStatements.buildSQLStatement(conn, GET_PRESENTATION_BY_DEFAULT_KEY, parameters);

            rs = retrieve.executeQuery();
            while (rs.next()) {
                pres = new Presentation();
                pres.setId(rs.getInt(1));
                pres.setIDProd(rs.getInt(2));
                pres.setSerie(rs.getInt(3));
                pres.setName(rs.getString(4));
                pres.setPrice(rs.getDouble(5));
                pres.setDefault(rs.getBoolean(6));
                pres.setEnabled(rs.getBoolean(7));
            }
        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the presentations list: " + e);
        } catch (IOException e) {
            throw new DAOException("Could not properly retrieve the presentations list: " + e);
        } finally {
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(retrieve);
            DBManager.closeConnection(conn);
        }
        return pres;
    }

    public Presentation getPresentation(long id) throws DAOException {
        Presentation pres = null;
        Connection conn = null;
        PreparedStatement retrieve = null;
        ResultSet rs = null;
        Object[] parameters = {id, 1};
        try {
            conn = dataSource.getConnection();
            retrieve = sqlStatements.buildSQLStatement(conn, GET_PRESENTATION_BY_DEFAULT_KEY, parameters);

            rs = retrieve.executeQuery();
            while (rs.next()) {
                pres = new Presentation();
                pres.setId(rs.getInt(1));
                pres.setIDProd(rs.getInt(2));
                pres.setSerie(rs.getInt(3));
                pres.setName(rs.getString(4));
                pres.setPrice(rs.getDouble(5));
                pres.setDefault(rs.getBoolean(6));
                pres.setEnabled(rs.getBoolean(7));
            }
        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the presentations list: " + e);
        } catch (IOException e) {
            throw new DAOException("Could not properly retrieve the presentations list: " + e);
        } finally {
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(retrieve);
            DBManager.closeConnection(conn);
        }
        return pres;
    }

    public Presentation getPresentationByID(long id) throws DAOException {
        Presentation pres = null;
        Connection conn = null;
        PreparedStatement retrieve = null;
        ResultSet rs = null;
        Object[] parameters = {id};
        try {
            conn = dataSource.getConnection();
            retrieve = sqlStatements.buildSQLStatement(conn, GET_PRESENTATION_KEY, parameters);

            rs = retrieve.executeQuery();
            while (rs.next()) {
                pres = new Presentation();
                pres.setId(rs.getInt(1));
                pres.setIDProd(rs.getInt(2));
                pres.setSerie(rs.getInt(3));
                pres.setName(rs.getString(4));
                pres.setPrice(rs.getDouble(5));
                pres.setDefault(rs.getBoolean(6));
                pres.setEnabled(rs.getBoolean(7));
            }
        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the presentations list: " + e);
        } catch (IOException e) {
            throw new DAOException("Could not properly retrieve the presentations list: " + e);
        } finally {
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(retrieve);
            DBManager.closeConnection(conn);
        }
        return pres;
    }

    public void addCycle(Cycle cycle) throws DAOException {
        if (cycle == null) {
            throw new IllegalArgumentException("Null Cycle");
        }
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            Object[] parameters = {
                cycle.getInitialBalance()
            };
            ps = sqlStatements.buildSQLStatement(conn, ADD_CYCLE_KEY, parameters);
            ps.executeUpdate();
            conn.commit();
        } catch (SQLException | IOException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot add Cycle", e);
        } finally {
            DBManager.closeStatement(ps);
            DBManager.closeConnection(conn);
        }
    }

    public void addCycleAndCreateSnapshot(Cycle cycle) throws DAOException {
        if (cycle == null) {
            throw new IllegalArgumentException("Null Cycle");
        }
        Connection conn = null;
        PreparedStatement ps = null, ps2 = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            Object[] parameters = {
                cycle.getInitialBalance()
            };
            ps = sqlStatements.buildSQLStatement(conn, ADD_CYCLE_KEY, parameters, Statement.RETURN_GENERATED_KEYS);
            ps.executeUpdate();

            long cycleId = 0;
            try ( ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    cycleId = generatedKeys.getLong(1);
                }
            }

            Statement statement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = statement.executeQuery("SELECT * FROM inventory WHERE snapshot=1");

            while (rs.next()) {
                Object[] parameters2 = {rs.getLong("id"), cycleId, rs.getDouble("quantity"), 0, 0, 0, 0, 0, rs.getInt("onlyDelivery")};
                ps2 = sqlStatements.buildSQLStatement(conn, ADD_INVENTARY_SNAPSHOT_ITEM_KEY, parameters2);
                ps2.executeUpdate();
            }
            conn.commit();
        } catch (SQLException | IOException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot add Cycle", e);
        } finally {
            DBManager.closeStatement(ps);
            DBManager.closeStatement(ps2);
            DBManager.closeConnection(conn);
        }
    }

    public ArrayList<Cycle> getCyclesList(String where, String order) throws DAOException {
        String retrieveList;
        ArrayList<Cycle> cycles = new ArrayList<>();
        try {
            SQLExtractor sqlExtractorWhere = new SQLExtractor(where, SQLExtractor.Type.WHERE);
            SQLExtractor sqlExtractorOrder = new SQLExtractor(order, SQLExtractor.Type.ORDER_BY);
            Map<String, String> namedParams = new HashMap<>();
            namedParams.put(NAMED_PARAM_WHERE, sqlExtractorWhere.extractWhere());
            namedParams.put(NAMED_PARAM_ORDER_BY, sqlExtractorOrder.extractOrderBy());
            retrieveList = sqlStatements.getSQLString(GET_CYCLE_KEY, namedParams);
        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the Cycles list", e);
        } catch (IOException e) {
            throw new DAOException("Could not properly retrieve the Cycles list", e);
        }

        Cycle cycle = null;
        Connection conn = null;
        PreparedStatement retrieve = null;
        ResultSet rs = null;

        try {
            conn = dataSource.getConnection();
            retrieve = conn.prepareStatement(retrieveList);
            rs = retrieve.executeQuery();
            while (rs.next()) {
                cycle = new Cycle();
                cycle.setId(rs.getInt(1));
                cycle.setInit(rs.getTimestamp(2));
                cycle.setEnd(rs.getTimestamp(3));
                cycle.setInitialBalance(rs.getBigDecimal(4));
                cycle.setStatus(rs.getInt(5));
                cycles.add(cycle);
            }
        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the cycle: " + e);
        } finally {
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(retrieve);
            DBManager.closeConnection(conn);
        }
        return cycles;
    }

    public void updateCycle(Cycle cycle) throws DAOException {
        Connection conn = null;
        PreparedStatement update = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            Object[] parameters = {
                cycle.getEnd(),
                cycle.getStatus(),
                cycle.getId()
            };
            update = sqlStatements.buildSQLStatement(conn, UPDATE_CYCLE_KEY, parameters);
            update.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Could not properly update the cycle", e);
        } catch (IOException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Could not properly update the cycle", e);
        } finally {
            DBManager.closeStatement(update);
            DBManager.closeConnection(conn);
        }
    }

    public void addOtherProduct(String name, String desc, double price) throws DAOException {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            Object[] parameters = {
                name, desc, price
            };
            ps = sqlStatements.buildSQLStatement(conn, ADD_OTHER_PRODUCT_KEY, parameters);
            ps.executeUpdate();
            conn.commit();
        } catch (SQLException | IOException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot add other product", e);
        } finally {
            DBManager.closeStatement(ps);
            DBManager.closeConnection(conn);
        }
    }

    public void addOtherProductInvoice(long idInvoice, OtherProduct otherProduct, int cantidad) throws DAOException {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            Object[] parameters = {
                idInvoice, otherProduct.getId(), cantidad, otherProduct.getPrice()
            };
            ps = sqlStatements.buildSQLStatement(conn, ADD_INVOICE_OTHER_PRODUCT_KEY, parameters);
            ps.executeUpdate();
            conn.commit();
        } catch (SQLException | IOException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot add other product to invoice", e);
        } finally {
            DBManager.closeStatement(ps);
            DBManager.closeConnection(conn);
        }
    }

    public ArrayList<Category> getCategoriesSorted() throws DAOException {
        String retrieveList;
        try {
            retrieveList = sqlStatements.getSQLString(GET_CATEGORIES_SORTED_KEY);
        } catch (IOException e) {
            throw new DAOException("Could not properly retrieve the categories list", e);
        }
        Connection conn = null;
        PreparedStatement retrieve = null;
        ResultSet rs = null;
        ArrayList<Category> categories = new ArrayList<>();
        try {
            conn = dataSource.getConnection();
            retrieve = conn.prepareStatement(retrieveList);
            rs = retrieve.executeQuery();
            while (rs.next()) {
                Category category = new Category(rs.getString(1));
                categories.add(category);
            }
        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the categories list: " + e);
        } finally {
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(retrieve);
            DBManager.closeConnection(conn);
        }
        return categories;
    }

    public Category getCategory(String name) throws DAOException {
        String retrieveList;
        try {
            SQLExtractor sqlExtractorWhere = new SQLExtractor("p.category='" + name + "'", SQLExtractor.Type.WHERE);

            Map<String, String> namedParams = new HashMap<>();
            namedParams.put(NAMED_PARAM_WHERE, sqlExtractorWhere.extractWhere());
            retrieveList = sqlStatements.getSQLString(GET_CATEGORY_KEY, namedParams);
        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the Category", e);
        } catch (IOException e) {
            throw new DAOException("Could not properly retrieve the Category", e);
        }

        Connection conn = null;
        PreparedStatement retrieve = null;
        ResultSet rs = null;
        Category category = null;
        try {
            conn = dataSource.getConnection();
            retrieve = conn.prepareStatement(retrieveList);
            rs = retrieve.executeQuery();
            while (rs.next()) {
                category = new Category(rs.getString(1));
            }
        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the categories list: " + e);
        } finally {
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(retrieve);
            DBManager.closeConnection(conn);
        }
        return category;
    }

    public ArrayList<Category> getAllCategories() throws DAOException {
        String retrieveList;
        try {
            retrieveList = sqlStatements.getSQLString(GET_ALL_CATEGORIES_KEY);
        } catch (IOException e) {
            throw new DAOException("Could not properly retrieve the categories list", e);
        }
        Connection conn = null;
        PreparedStatement retrieve = null;
        ResultSet rs = null;
        ArrayList<Category> categories = new ArrayList<>();
        try {
            conn = dataSource.getConnection();
            retrieve = conn.prepareStatement(retrieveList);
            rs = retrieve.executeQuery();
            while (rs.next()) {
                Category category = new Category(rs.getString(1));
                categories.add(category);
            }
        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the categories list: " + e);
        } finally {
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(retrieve);
            DBManager.closeConnection(conn);
        }
        return categories;
    }

    public void addUnit(String unidad) throws DAOException {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            Object[] parameters = {
                unidad
            };
            ps = sqlStatements.buildSQLStatement(conn, ADD_UNIT_KEY, parameters);
            ps.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot add Unit", e);
        } catch (IOException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot add Unit", e);
        } finally {
            DBManager.closeStatement(ps);
            DBManager.closeConnection(conn);
        }
    }

    public ArrayList<String> getUnitList(String where, String orderBy) throws DAOException {
        String retrieveUnit;
        ArrayList<String> unidades = new ArrayList<>();
        try {
            SQLExtractor sqlExtractorWhere = new SQLExtractor(where, SQLExtractor.Type.WHERE);;
            SQLExtractor sqlExtractorOrderBy = new SQLExtractor(orderBy, SQLExtractor.Type.ORDER_BY);;
            Map<String, String> namedParams = new HashMap<>();
            namedParams.put(NAMED_PARAM_WHERE, sqlExtractorWhere.extractWhere());
            namedParams.put(NAMED_PARAM_ORDER_BY, sqlExtractorOrderBy.extractOrderBy());
            retrieveUnit = sqlStatements.getSQLString(GET_UNIT_KEY, namedParams);

        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the Unites List", e);
        } catch (IOException e) {
            throw new DAOException("Could not properly retrieve the Unites List", e);
        }

        Connection conn = null;
        PreparedStatement retrieve = null;
        ResultSet rs = null;
        String unidad = null;
        try {
            conn = dataSource.getConnection();
            retrieve = conn.prepareStatement(retrieveUnit);
            rs = retrieve.executeQuery();

            while (rs.next()) {
                unidad = rs.getString(2);
                unidades.add(unidad);
            }
        } catch (SQLException e) {
            throw new DAOException("Could not proper retrieve the Unit: " + e);
        } finally {
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(retrieve);
            DBManager.closeConnection(conn);
        }
        return unidades;
    }

    public void deleteUnit(String name) throws DAOException {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            Object[] parameters = {name};
            ps = sqlStatements.buildSQLStatement(conn, DELETE_UNIT_KEY, parameters);
            ps.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot delete the unidad", e);
        } catch (IOException e) {
            throw new DAOException("Cannot delete the unidad", e);
        } finally {
            DBManager.closeStatement(ps);
            DBManager.closeConnection(conn);
        }
    }

    public void updateUnit(String unidad, String id) throws DAOException {
        Connection conn = null;
        PreparedStatement update = null;
        String idUnid = "'" + unidad + "'";
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            Object[] parameters = {
                unidad,
                id
            };
            update = sqlStatements.buildSQLStatement(conn, UPDATE_UNIT_KEY, parameters);
            update.executeUpdate();
            conn.commit();
        } catch (SQLException | IOException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Could not properly update the Unit", e);
        } finally {
            DBManager.closeStatement(update);
            DBManager.closeConnection(conn);
        }
    }

    public void addCategory(String category) throws DAOException {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            Object[] parameters = {
                category, 1
            };
            ps = sqlStatements.buildSQLStatement(conn, ADD_CATEGORY_KEY, parameters);
            ps.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot add Category", e);
        } catch (IOException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot add Category", e);
        } finally {
            DBManager.closeStatement(ps);
            DBManager.closeConnection(conn);
        }
    }

    public void updateCategory(String category, String id) throws DAOException {
        Connection conn = null;
        PreparedStatement update = null;
        String idCategory = "'" + category + "'";
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            Object[] parameters = {
                category,
                id
            };
            update = sqlStatements.buildSQLStatement(conn, UPDATE_CATEGORY_KEY, parameters);
            update.executeUpdate();
            conn.commit();
        } catch (SQLException | IOException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Could not properly update the Category", e);
        } finally {
            DBManager.closeStatement(update);
            DBManager.closeConnection(conn);
        }
    }

    public void addPresentation(Presentation presentation) throws DAOException {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            Object[] parameters = {
                presentation.getName(),
                presentation.getPrice(),
                presentation.getSerie(),
                presentation.isDefault(),
                presentation.isEnabled(),
                presentation.getIDProd()
            };
            ps = sqlStatements.buildSQLStatement(conn, ADD_PRESENTATION_KEY, parameters);
            ps.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot add Presentation", e);
        } catch (IOException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot add Presentation", e);
        } finally {
            DBManager.closeStatement(ps);
            DBManager.closeConnection(conn);
        }
    }

    public boolean updatePresentation(Presentation pres) throws DAOException {
        Connection conn = null;
        PreparedStatement update = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            Object[] parameters = {
                pres.getName(),
                pres.getPrice(),
                pres.getSerie(),
                pres.isDefault(),
                pres.isEnabled(),
                pres.getId()
            };
            update = sqlStatements.buildSQLStatement(conn, UPDATE_PRESENTATION_KEY, parameters);
            update.executeUpdate();
            conn.commit();
            return true;
        } catch (SQLException | IOException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Could not properly update the Category", e);
        } finally {
            DBManager.closeStatement(update);
            DBManager.closeConnection(conn);
        }
    }

    public boolean updatePresentationToDefault(Presentation pres) throws DAOException {
        Connection conn = null;
        PreparedStatement update = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            Object[] parameters = {
                0,
                pres.getIDProd()
            };
            update = sqlStatements.buildSQLStatement(conn, UPDATE_PRESENTATION_TO_DEFAULT_KEY, parameters);
            update.executeUpdate();

            Object[] parameters1 = {
                1,
                pres.getIDProd(),
                pres.getId()
            };
            update = sqlStatements.buildSQLStatement(conn, UPDATE_PRESENTATION_TO_DEFAULT_BY_ID_KEY, parameters1);
            update.executeUpdate();

            conn.commit();
            return true;
        } catch (SQLException | IOException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Could not properly update the Category", e);
        } finally {
            DBManager.closeStatement(update);
            DBManager.closeConnection(conn);
        }
    }

    public HashMap<Integer, HashMap> checkInventory(int idPres) throws DAOException {
        HashMap<Integer, HashMap> mData = new HashMap<>();
        HashMap data = null;
        Connection conn = null;
        PreparedStatement retrieve = null;
        ResultSet rs = null;
        Object[] parameters = {idPres};
        try {
            conn = dataSource.getConnection();
            retrieve = sqlStatements.buildSQLStatement(conn, CHECK_INVENTORY_KEY, parameters);
            rs = retrieve.executeQuery();
            while (rs.next()) {
                data = new HashMap<>();
                int id = rs.getInt("id");
                data.put("id", id);
                data.put("name", rs.getString("name"));
                data.put("pres", rs.getString("pres"));
                data.put("measure", rs.getString("measure"));
                data.put("exist", rs.getDouble("exist"));
                data.put("idPres", rs.getInt("idPres"));
                data.put("quantity", rs.getDouble("quantity"));
                data.put("onlyDelivery", rs.getBoolean("onlyDelivery"));
                mData.put(id, data);
            }
        } catch (SQLException | IOException e) {
            throw new DAOException("Could not properly retrieve the presentations list: " + e);
        } finally {
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(retrieve);
            DBManager.closeConnection(conn);
        }
        return mData;
    }

    public HashMap<Integer, HashMap> checkInventoryProduct(long idProd) throws DAOException {
        HashMap<Integer, HashMap> mData = new HashMap<>();
        HashMap data = null;
        Connection conn = null;
        PreparedStatement retrieve = null;
        ResultSet rs = null;
        Object[] parameters = {idProd};
        try {
            conn = dataSource.getConnection();
            retrieve = sqlStatements.buildSQLStatement(conn, CHECK_INVENTORY_PRODUCT_KEY, parameters);
            rs = retrieve.executeQuery();
            while (rs.next()) {
                data = new HashMap<>();
                int id = rs.getInt("id");
                data.put("id", id);
                data.put("name", rs.getString("name"));
                data.put("pres", rs.getString("pres"));
                data.put("measure", rs.getString("measure"));
                data.put("exist", rs.getDouble("exist"));
                data.put("idPres", rs.getInt("idPres"));
                data.put("quantity", rs.getDouble("quantity"));
                data.put("onlyDelivery", rs.getBoolean("onlyDelivery"));
                mData.put(id, data);
            }
        } catch (SQLException | IOException e) {
            throw new DAOException("Could not properly retrieve the products list: " + e);
        } finally {
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(retrieve);
            DBManager.closeConnection(conn);
        }
        return mData;
    }

    public void addInventoryQuantity(long id, double quantity) throws DAOException {
        updateInventoryQuantity(id, quantity, ADD_INVENTORY_QUANTITY_KEY);
    }

    public void setInventoryQuantity(long id, double quantity) throws DAOException {
        updateInventoryQuantity(id, quantity, UPDATE_INVENTORY_QUANTITY_KEY);
    }

    public void updateInventoryQuantity(long id, double quantity, String KEY) throws DAOException {
        Connection conn = null;
        PreparedStatement update = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            Object[] parameters = {
                quantity,
                id
            };
            update = sqlStatements.buildSQLStatement(conn, KEY, parameters);
            update.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Could not properly update the Item", e);
        } catch (IOException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Could not properly update the Item", e);
        } finally {
            DBManager.closeStatement(update);
            DBManager.closeConnection(conn);
        }
    }

    public void addInventoryRegister(Item item, int EVENT, double quantity) throws DAOException {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            Object[] parameters = {
                item.getId(),
                EVENT,
                quantity
            };
            ps = sqlStatements.buildSQLStatement(conn, ADD_INVENTORY_EVENT_KEY, parameters);
            ps.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot add Inventory event ", e);
        } catch (IOException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot add Inventory vent", e);
        } finally {
            DBManager.closeStatement(ps);
            DBManager.closeConnection(conn);
        }
    }

    public ArrayList<InventoryEvent> getRegisterEventList(String where, String orderBy) throws DAOException {
        String retrieveEvent;
        ArrayList<InventoryEvent> events = new ArrayList<>();
        try {
            SQLExtractor sqlExtractorWhere = new SQLExtractor(where, SQLExtractor.Type.WHERE);
            SQLExtractor sqlExtractorOrderBy = new SQLExtractor(orderBy, SQLExtractor.Type.ORDER_BY);
            Map<String, String> namedParams = new HashMap<>();
            namedParams.put(NAMED_PARAM_WHERE, sqlExtractorWhere.extractWhere());
            namedParams.put(NAMED_PARAM_ORDER_BY, sqlExtractorOrderBy.extractOrderBy());
            retrieveEvent = sqlStatements.getSQLString(GET_INVENTORY_EVENT_LIST_KEY, namedParams);
        } catch (SQLException | IOException e) {
            throw new DAOException("Could not properly retrieve the events List", e);
        }

        Connection conn = null;
        PreparedStatement retrieve = null;
        ResultSet rs = null;
        InventoryEvent event = null;
        try {
            conn = dataSource.getConnection();
            retrieve = conn.prepareStatement(retrieveEvent);
            rs = retrieve.executeQuery();

            while (rs.next()) {
                event = new InventoryEvent();
                event.setId(rs.getLong("id"));
                event.setIdItem(rs.getLong("idItem"));
                event.setEvent(rs.getInt("event"));
                event.setQuantity(rs.getDouble("quantity"));
                event.setLastUpdate(rs.getTimestamp("lastUpdatedTime"));
                events.add(event);
            }
        } catch (SQLException e) {
            throw new DAOException("Could not proper retrieve the Unit: " + e);
        } finally {
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(retrieve);
            DBManager.closeConnection(conn);
        }
        return events;
    }

    public ArrayList<Object[]> getProductsOutInventory(long idProd, long idItem, Date start) throws DAOException {
        ArrayList<Object[]> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement retrieve = null;
        ResultSet rs = null;
        Object[] parameters = {idProd, idItem, Aplication.DF_SQL_TS.format(start), new Date()};
        try {
            conn = dataSource.getConnection();
            retrieve = sqlStatements.buildSQLStatement(conn, GET_PRODUCTS_OUT_INVENTORY_KEY, parameters);
            rs = retrieve.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                double quantity = rs.getDouble("quantity");
                int type = rs.getInt("deliveryType");
                list.add(new Object[]{id, name, quantity, type});
            }
        } catch (SQLException | IOException e) {
            throw new DAOException("Could not properly retrieve the products out: " + e);
        } finally {
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(retrieve);
            DBManager.closeConnection(conn);
        }
        return list;
    }

    public ArrayList<Object[]> getPresentationOutInventory(long idPres, long idItem, Date start) throws DAOException {
        ArrayList<Object[]> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement retrieve = null;
        ResultSet rs = null;
        Object[] parameters = {idPres, idItem, Aplication.DF_SQL_TS.format(start), new Date(),};
        try {
            conn = dataSource.getConnection();
            retrieve = sqlStatements.buildSQLStatement(conn, GET_PRESENTATIONS_OUT_INVENTORY_KEY, parameters);
            rs = retrieve.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("prod_id");
                String name = rs.getString("name");
                String press = rs.getString("pres");
                double quantity = rs.getDouble("quantity");
                int type = rs.getInt("deliveryType");
                list.add(new Object[]{id, name, press, quantity, type});
            }
        } catch (SQLException | IOException e) {
            throw new DAOException("Could not properly retrieve the products out: " + e);
        } finally {
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(retrieve);
            DBManager.closeConnection(conn);
        }
        return list;
    }

    public ArrayList<Object[]> getPresentationsByItem(long idItem) throws DAOException {
        ArrayList<Object[]> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement retrieve = null;
        ResultSet rs = null;
        Object[] parameters = {idItem};
        try {
            conn = dataSource.getConnection();
            retrieve = sqlStatements.buildSQLStatement(conn, GET_PRESENTATIONS_BY_ITEM_KEY, parameters);
            rs = retrieve.executeQuery();
            while (rs.next()) {
                int idPres = rs.getInt("presentation_id");
                int idProd = rs.getInt("product_id");
                double quantity = rs.getDouble("quantity");
                list.add(new Object[]{idPres, idProd, quantity});
            }
        } catch (SQLException | IOException e) {
            throw new DAOException("Could not properly retrieve the item presentations: " + e);
        } finally {
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(retrieve);
            DBManager.closeConnection(conn);
        }
        return list;
    }

    public ArrayList<Object[]> getProductsSales(Date start, Date end) throws DAOException {
        ArrayList<Object[]> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement retrieve = null;
        ResultSet rs = null;
        Object[] parameters = {start, end};
        try {
            conn = dataSource.getConnection();
            retrieve = sqlStatements.buildSQLStatement(conn, GET_PRODUCTS_SALES_KEY, parameters);
            rs = retrieve.executeQuery();
            while (rs.next()) {
                int idProd = rs.getInt("idProd");
                String cat = rs.getString("cat");
                String name = rs.getString("name");
                String pres = rs.getString("pres");
                pres = pres == null ? "" : pres;
                double quantity = rs.getDouble("quantity");
                double total = rs.getDouble("total");
                list.add(new Object[]{idProd, cat.toUpperCase(), name.toUpperCase(), pres.toUpperCase(), quantity, total});
            }
        } catch (SQLException | IOException e) {
            throw new DAOException("Could not properly retrieve the products sales: " + e);
        } finally {
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(retrieve);
            DBManager.closeConnection(conn);
        }
        return list;

    }

    public void addPayment(String payment) throws DAOException {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            Object[] parameters = {
                payment
            };
            ps = sqlStatements.buildSQLStatement(conn, ADD_PAYMENT_KEY, parameters);
            ps.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot add Payment", e);
        } catch (IOException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot add Payment", e);
        } finally {
            DBManager.closeStatement(ps);
            DBManager.closeConnection(conn);
        }
    }

    public ArrayList<String> getPaymentList(String where, String orderBy) throws DAOException {
        String retrievePayment;
        ArrayList<String> payments = new ArrayList<>();
        try {
            SQLExtractor sqlExtractorWhere = new SQLExtractor(where, SQLExtractor.Type.WHERE);
            SQLExtractor sqlExtractorOrderBy = new SQLExtractor(orderBy, SQLExtractor.Type.ORDER_BY);
            Map<String, String> namedParams = new HashMap<>();
            namedParams.put(NAMED_PARAM_WHERE, sqlExtractorWhere.extractWhere());
            namedParams.put(NAMED_PARAM_ORDER_BY, sqlExtractorOrderBy.extractOrderBy());
            retrievePayment = sqlStatements.getSQLString(GET_PAYMENT_KEY, namedParams);

        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the Paymentes List", e);
        } catch (IOException e) {
            throw new DAOException("Could not properly retrieve the Paymentes List", e);
        }

        Connection conn = null;
        PreparedStatement retrieve = null;
        ResultSet rs = null;
        String payment = null;
        try {
            conn = dataSource.getConnection();
            retrieve = conn.prepareStatement(retrievePayment);
            rs = retrieve.executeQuery();

            while (rs.next()) {
                payment = rs.getString(2);
                payments.add(payment);
            }
        } catch (SQLException e) {
            throw new DAOException("Could not proper retrieve the Payment: " + e);
        } finally {
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(retrieve);
            DBManager.closeConnection(conn);
        }
        return payments;
    }

    public void deletePayment(String name) throws DAOException {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            Object[] parameters = {name};
            ps = sqlStatements.buildSQLStatement(conn, DELETE_PAYMENT_KEY, parameters);
            ps.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot delete the unidad", e);
        } catch (IOException e) {
            throw new DAOException("Cannot delete the unidad", e);
        } finally {
            DBManager.closeStatement(ps);
            DBManager.closeConnection(conn);
        }
    }

    public void updatePayment(String payment, String id) throws DAOException {
        Connection conn = null;
        PreparedStatement update = null;
        String idPayment = "'" + payment + "'";
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            Object[] parameters = {
                payment,
                id
            };
            update = sqlStatements.buildSQLStatement(conn, UPDATE_PAYMENT_KEY, parameters);
            update.executeUpdate();
            conn.commit();
        } catch (SQLException | IOException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Could not properly update the Payment", e);
        } finally {
            DBManager.closeStatement(update);
            DBManager.closeConnection(conn);
        }
    }

    public List<String> getTagsInventoryList(String where) throws DAOException {
        String retrievePayment;
        List<String> tagsList = new ArrayList<>();
        try {
            SQLExtractor sqlExtractorWhere = new SQLExtractor(where, SQLExtractor.Type.WHERE);
            Map<String, String> namedParams = new HashMap<>();
            namedParams.put(NAMED_PARAM_WHERE, sqlExtractorWhere.extractWhere());
            retrievePayment = sqlStatements.getSQLString(GET_TAGS_INVENTORY_LIST_KEY, namedParams);

        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the tags List", e);
        } catch (IOException e) {
            throw new DAOException("Could not properly retrieve the tags List", e);
        }

        Connection conn = null;
        PreparedStatement retrieve = null;
        ResultSet rs = null;
        String tags = null;
        try {
            conn = dataSource.getConnection();
            retrieve = conn.prepareStatement(retrievePayment);
            rs = retrieve.executeQuery();

            while (rs.next()) {
                tags = rs.getString("tags");
                tagsList.add(tags);
            }
        } catch (SQLException e) {
            throw new DAOException("Could not proper retrieve the Payment: " + e);
        } finally {
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(retrieve);
            DBManager.closeConnection(conn);
        }
        return tagsList;
    }

    public ArrayList<Map> getItemSnapshotList(String where, String orderBy) throws DAOException {
        String retrieve;
        ArrayList<Map> snapItems = new ArrayList<>();
        try {
            SQLExtractor sqlExtractorWhere = new SQLExtractor(where, SQLExtractor.Type.WHERE);
            SQLExtractor sqlExtractorOrderBy = new SQLExtractor(orderBy, SQLExtractor.Type.ORDER_BY);
            Map<String, String> namedParams = new HashMap<>();
            namedParams.put(NAMED_PARAM_WHERE, sqlExtractorWhere.extractWhere());
            namedParams.put(NAMED_PARAM_ORDER_BY, sqlExtractorOrderBy.extractOrderBy());
            retrieve = sqlStatements.getSQLString(GET_INVENTORY_SNAPSHOT_ITEM_KEY, namedParams);

        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the Unites List", e);
        } catch (IOException e) {
            throw new DAOException("Could not properly retrieve the Unites List", e);
        }

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Map dataItemSnap = null;
        try {
            conn = dataSource.getConnection();
            ps = conn.prepareStatement(retrieve);
            rs = ps.executeQuery();

            while (rs.next()) {
                dataItemSnap = new HashMap();
                dataItemSnap.put("id", rs.getLong("s.id"));
                dataItemSnap.put("item_id", rs.getLong("s.item_id"));
                dataItemSnap.put("cycle_id", rs.getLong("s.cycle_id"));
                dataItemSnap.put("quantity", rs.getDouble("s.quantity"));
                dataItemSnap.put("ins", rs.getDouble("s.ins"));
                dataItemSnap.put("outs", rs.getDouble("s.outs"));
                dataItemSnap.put("sales", rs.getDouble("s.sales"));
                dataItemSnap.put("name", rs.getString("i.name"));
                dataItemSnap.put("onlyDelivery", rs.getBoolean("s.isOnlyDelivery"));
                dataItemSnap.put("real", rs.getDouble("i.quantity"));
                snapItems.add(dataItemSnap);
            }
        } catch (SQLException e) {
            throw new DAOException("Could not proper retrieve the snap items: " + e);
        } finally {
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(ps);
            DBManager.closeConnection(conn);
        }
        return snapItems;
    }

    public Map countItemSnapEvents(long idItem, int EVENT, long idCycle) throws DAOException {
        String retrieve;
        ArrayList<Map> snapItems = new ArrayList<>();

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Map dataItemSnap = null;

        try {
            SQLExtractor sqlExtractorWhere = new SQLExtractor("id=" + idCycle, SQLExtractor.Type.WHERE);
            SQLExtractor sqlExtractorOrderBy = new SQLExtractor("id", SQLExtractor.Type.ORDER_BY);
            Map<String, String> namedParams = new HashMap<>();
            namedParams.put(NAMED_PARAM_WHERE, sqlExtractorWhere.extractWhere());
            namedParams.put(NAMED_PARAM_ORDER_BY, sqlExtractorOrderBy.extractOrderBy());
            retrieve = sqlStatements.getSQLString(GET_CYCLE_KEY, namedParams);
//            System.out.println("retrieve :: " + retrieve);

            conn = dataSource.getConnection();
            ps = conn.prepareStatement(retrieve);
            rs = ps.executeQuery();
            rs.last();
            Date dInit = rs.getTimestamp("init");
            Date dEnd = new Date(); //Problematico ver historico
            Object[] parameters = {idItem, EVENT, dInit, dEnd};

            ps = sqlStatements.buildSQLStatement(conn, COUNT_ITEM_SNAP_EVENT_KEY, parameters);

            rs = ps.executeQuery();
//            System.out.println("::"+ps);
            while (rs.next()) {
                dataItemSnap = new HashMap();
                dataItemSnap.put("item_id", idItem);
                dataItemSnap.put("sum", rs.getDouble("sum"));

            }
        } catch (SQLException e) {
            throw new DAOException("Could not proper retrieve the snap items: " + e);
        } catch (IOException ex) {
            throw new DAOException("Could not proper retrieve the snap items: " + ex);
        } finally {
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(ps);
            DBManager.closeConnection(conn);
        }
        return dataItemSnap;
    }

    public Map countItemConciliationEvents(long idItem, long idCycle) throws DAOException {
        String retrieve;
        ArrayList<Map> snapItems = new ArrayList<>();

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Map dataItemSnap = null;

        try {
            SQLExtractor sqlExtractorWhere = new SQLExtractor("id=" + idCycle, SQLExtractor.Type.WHERE);
            SQLExtractor sqlExtractorOrderBy = new SQLExtractor("id", SQLExtractor.Type.ORDER_BY);
            Map<String, String> namedParams = new HashMap<>();
            namedParams.put(NAMED_PARAM_WHERE, sqlExtractorWhere.extractWhere());
            namedParams.put(NAMED_PARAM_ORDER_BY, sqlExtractorOrderBy.extractOrderBy());
            retrieve = sqlStatements.getSQLString(GET_CYCLE_KEY, namedParams);
//            System.out.println("retrieve :: " + retrieve);

            conn = dataSource.getConnection();
            ps = conn.prepareStatement(retrieve);
            rs = ps.executeQuery();
            rs.last();
            Date dInit = rs.getTimestamp("init");
            Date dEnd = new Date(); //Problematico ver historico
            Object[] parameters = {idItem, dInit, dEnd};

            ps = sqlStatements.buildSQLStatement(conn, COUNT_ITEM_CONCILIATION_EVENT_KEY, parameters);

            rs = ps.executeQuery();
//            System.out.println("::"+ps);
            while (rs.next()) {
                dataItemSnap = new HashMap();
                dataItemSnap.put("item_id", idItem);
                dataItemSnap.put("sum", rs.getDouble("sum"));

            }
        } catch (SQLException e) {
            throw new DAOException("Could not proper retrieve the snap conciliation items: " + e);
        } catch (IOException ex) {
            throw new DAOException("Could not proper retrieve the snap conciliation items: " + ex);
        } finally {
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(ps);
            DBManager.closeConnection(conn);
        }
        return dataItemSnap;
    }

    public List<Double> getRankProductsByVarPriceList(long prodID, int limit) throws DAOException {
        ArrayList<Double> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement retrieve = null;
        ResultSet rs = null;
        Object[] parameters = {prodID, limit};
        try {
            conn = dataSource.getConnection();
            retrieve = sqlStatements.buildSQLStatement(conn, GET_PRODUCTS_BY_VARPRICE_RANK_LIST_KEY, parameters);
            rs = retrieve.executeQuery();
            while (rs.next()) {
                double price = rs.getDouble("price");
                list.add(price);
            }
        } catch (SQLException | IOException e) {
            throw new DAOException("Could not properly retrieve the rank of products: " + e);
        } finally {
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(retrieve);
            DBManager.closeConnection(conn);
        }
        return list;

    }

    public ArrayList<String> getCategoriesList(String where, String orderBy) throws DAOException {
        String retrieveUnit;
        ArrayList<String> categories = new ArrayList<>();
        try {
            SQLExtractor sqlExtractorWhere = new SQLExtractor(where, SQLExtractor.Type.WHERE);
            SQLExtractor sqlExtractorOrderBy = new SQLExtractor(orderBy, SQLExtractor.Type.ORDER_BY);
            Map<String, String> namedParams = new HashMap<>();
            namedParams.put(NAMED_PARAM_WHERE, sqlExtractorWhere.extractWhere());
            namedParams.put(NAMED_PARAM_ORDER_BY, sqlExtractorOrderBy.extractOrderBy());
            retrieveUnit = sqlStatements.getSQLString(GET_CATEGORIES_LIST_KEY, namedParams);

        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the Categories List", e);
        } catch (IOException e) {
            throw new DAOException("Could not properly retrieve the Categories List", e);
}

        Connection conn = null;
        PreparedStatement retrieve = null;
        ResultSet rs = null;
        String category = null;
        try {
            conn = dataSource.getConnection();
            retrieve = conn.prepareStatement(retrieveUnit);
            rs = retrieve.executeQuery();

            while (rs.next()) {
                category = rs.getString("name");
                categories.add(category);
            }
        } catch (SQLException e) {
            throw new DAOException("Could not proper retrieve the Unit: " + e);
        } finally {
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(retrieve);
            DBManager.closeConnection(conn);
        }
        return categories;
    }
    
    public ArrayList<String> getExpensesCategoriesList(String where, String orderBy) throws DAOException {
        String retrieveUnit;
        ArrayList<String> categories = new ArrayList<>();
        try {
            SQLExtractor sqlExtractorWhere = new SQLExtractor(where, SQLExtractor.Type.WHERE);
            SQLExtractor sqlExtractorOrderBy = new SQLExtractor(orderBy, SQLExtractor.Type.ORDER_BY);
            Map<String, String> namedParams = new HashMap<>();
            namedParams.put(NAMED_PARAM_WHERE, sqlExtractorWhere.extractWhere());
            namedParams.put(NAMED_PARAM_ORDER_BY, sqlExtractorOrderBy.extractOrderBy());
            retrieveUnit = sqlStatements.getSQLString(GET_EXPENSES_CATEGORIES_LIST_KEY, namedParams);

        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the Categories List", e);
        } catch (IOException e) {
            throw new DAOException("Could not properly retrieve the Categories List", e);
        }

        Connection conn = null;
        PreparedStatement retrieve = null;
        ResultSet rs = null;
        String category = null;
        try {
            conn = dataSource.getConnection();
            retrieve = conn.prepareStatement(retrieveUnit);
            rs = retrieve.executeQuery();

            while (rs.next()) {
                category = rs.getString("category");
                categories.add(category);
            }
        } catch (SQLException e) {
            throw new DAOException("Could not proper retrieve the Unit: " + e);
        } finally {
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(retrieve);
            DBManager.closeConnection(conn);
        }
        return categories;
    }
    
    public void addExpenseCategory(String category) throws DAOException {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            Object[] parameters = {
                category
            };
            ps = sqlStatements.buildSQLStatement(conn, ADD_EXPENSES_CATEGORY_KEY, parameters);
            ps.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot add Category", e);
        } catch (IOException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot add Category", e);
        } finally {
            DBManager.closeStatement(ps);
            DBManager.closeConnection(conn);
        }
    }

    public void updateExpensesCategory(String category, String id) throws DAOException {
        Connection conn = null;
        PreparedStatement update = null;
        String idCategory = "'" + category + "'";
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            Object[] parameters = {
                category,
                id
            };
            update = sqlStatements.buildSQLStatement(conn, UPDATE_EXPENSES_CATEGORY_KEY, parameters);
            update.executeUpdate();
            conn.commit();
        } catch (SQLException | IOException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Could not properly update the Category", e);
        } finally {
            DBManager.closeStatement(update);
            DBManager.closeConnection(conn);
        }
    }
    
    public void addExpenseIncome(HashMap data) throws DAOException {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            Object[] parameters = {
                data.get("TYPE"),
                data.get("DESC"),
                data.get("VALUE"),
                data.get("NOTE"),
                data.get("CYCLE"),
                data.get("CATEGORY")
            };
            ps = sqlStatements.buildSQLStatement(conn, ADD_EXPENSE_INCOME_KEY, parameters);
            ps.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot add Payment", e);
        } catch (IOException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot add Payment", e);
        } finally {
            DBManager.closeStatement(ps);
            DBManager.closeConnection(conn);
        }
    }

}
