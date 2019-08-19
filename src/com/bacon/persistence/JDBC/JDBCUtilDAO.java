/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bacon.persistence.JDBC;


import com.bacon.DBManager;
import com.bacon.domain.Permission;
import com.bacon.domain.Rol;
import com.bacon.domain.User;
import com.bacon.persistenc.SQLExtractor;
import com.bacon.persistenc.SQLLoader;
import com.bacon.persistence.dao.DAOException;
import com.bacon.persistence.dao.UtilDAO;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
    protected static final String CHECK_TABLE_EMPTY_KEY = "CHECK_TABLE";
    protected static final String INSERT_ROLE_USER_KEY = "INSERT_ROLE_USER";
    protected static final String HAS_PERMISSION_KEY = "HAS_PERMISSION";
    protected static final String GET_USER_ROLE_KEY = "GET_USER_ROLE";

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
                System.out.println("adding:"+ perm +" to role:"+role);
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
    
}
