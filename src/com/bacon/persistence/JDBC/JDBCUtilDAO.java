/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bacon.persistence.JDBC;

import com.bacon.DBManager;
import com.bacon.domain.Ingredient;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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

    public static final String CREATE_WAITERS_TABLE_KEY = "CREATE_WAITERS_TABLE";
    public static final String CREATE_TABLES_TABLE_KEY = "CREATE_TABLES_TABLE";

    public static final String GET_TABLES_LIST_KEY = "GET_TABLES";
    public static final String GET_WAITERS_LIST_KEY = "GET_WAITERS";

    public static final String CREATE_INVOICE_PRODUCT_TABLE_KEY = "CREATE_INVOICE_PRODUCT_TABLE";
    public static final String CREATE_ADDITIONAL_PRODUCT_TABLE_KEY = "CREATE_ADDITIONAL_PRODUCT_TABLE";
    public static final String ADD_ADDITIONAL_PRODUCT_KEY = "ADD_ADDITIONAL_PRODUCT";
    public static final String CREATE_EXCLUSION_PRODUCT_TABLE_KEY = "CREATE_EXCLUSION_PRODUCT_TABLE";
    public static final String ADD_EXCLUSION_PRODUCT_KEY = "ADD_EXCLUSION_PRODUCT";
    public static final String CREATE_PRESENTATION_PRODUCT_TABLE_KEY = "CREATE_PRESENTATION_PRODUCT_TABLE";
    public static final String ADD_PRESENTATION_PRODUCT_KEY = "ADD_PRESENTATION_PRODUCT";
    public static final String GET_PRESENTATION_BY_DEFAULT_KEY = "GET_PRESENTATION_BY_DEFAULT";

    protected static final String CHECK_TABLE_EMPTY_KEY = "CHECK_TABLE";
    protected static final String INSERT_ROLE_USER_KEY = "INSERT_ROLE_USER";
    protected static final String HAS_PERMISSION_KEY = "HAS_PERMISSION";
    protected static final String GET_USER_ROLE_KEY = "GET_USER_ROLE";

    public static final String GET_FIRST_REGISTRO_KEY = "GET_FIRST_REGISTRO";

    private static final Logger logger = Logger.getLogger(JDBCUtilDAO.class.getCanonicalName());

    public static final String NAMED_PARAM_KEY = "{key}";
    public static final String GET_MAX_ID_KEY = "GET_MAX_ID";
    public static final String EXIST_CLAVE_KEY = "EXIST_CLAVE";

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

        TABLE_NAME = "invoice_product";
        createTable(TABLE_NAME, CREATE_INVOICE_PRODUCT_TABLE_KEY);

        TABLE_NAME = "additional_product";
        createTable(TABLE_NAME, CREATE_ADDITIONAL_PRODUCT_TABLE_KEY);

        TABLE_NAME = "exclusion_product";
        createTable(TABLE_NAME, CREATE_EXCLUSION_PRODUCT_TABLE_KEY);

        TABLE_NAME = "presentation_product";
        createTable(TABLE_NAME, CREATE_PRESENTATION_PRODUCT_TABLE_KEY);

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
//            Object[] parameters = {code};
//            pSt = sqlStatements.buildSQLStatement(conn, EXIST_CLAVE_KEY, parameters);
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
                System.out.println("adding:" + perm + " to role:" + role);
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
            throw new DAOException("Could not properly retrieve registrp date", e);
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
                pres.setIDProd(rs.getInt(1));
                pres.setSerie(rs.getInt(2));
                pres.setName(rs.getString(3));
                pres.setPrice(rs.getDouble(4));
                pres.setDefault(rs.getBoolean(5));
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
                pres.setIDProd(rs.getInt(1));
                pres.setSerie(rs.getInt(2));
                pres.setName(rs.getString(3));
                pres.setPrice(rs.getDouble(4));
                pres.setDefault(rs.getBoolean(5));

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

}
