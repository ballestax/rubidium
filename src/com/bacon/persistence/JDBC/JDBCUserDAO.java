/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bacon.persistence.JDBC;

import com.bacon.DBManager;
import com.bacon.persistence.dao.DAOException;
import com.bacon.domain.User;
import com.bacon.domain.User.AccessLevel;
import com.bacon.persistenc.SQLExtractor;
import com.bacon.persistenc.SQLLoader;
import com.bacon.persistence.dao.RemoteUserResultsInterface;
import com.bacon.persistence.dao.UserDAO;
import com.bacon.persistence.dao.UserRetrieveException;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;

/**
 *
 * @author ballestax
 */
public class JDBCUserDAO implements UserDAO {

    public static final String TABLE_NAME = "users";
    private String NAMED_PARAM_WHERE;
    private static final Logger logger = Logger.getLogger(JDBCUserDAO.class.getCanonicalName());
    private final DataSource dataSource;
    private final SQLLoader sqlStatements;
    protected static final String CREATE_USERS_TABLE_KEY = "CREATE_USERS_TABLE";
    protected static final String INSERT_USER_KEY = "INSERT_USER";
    protected static final String UPDATE_USER_USERNAME_KEY = "UPDATE_USER_USERNAME";
    protected static final String UPDATE_USER_ACCESS_KEY = "UPDATE_USER_ACCESS";
    protected static final String UPDATE_USER_PASS_KEY = "UPDATE_USER_PASSWORD";
    protected static final String GET_USER_KEY = "GET_USER";
    protected static final String DELETE_USER_KEY = "DELETE_USER";
    protected static final String CHECK_PASSWORD_KEY = "CHECK_USER_PASSWORD";
    protected static final String CHECK_TABLE_EMPTY_KEY = "CHECK_TABLE_USERS";

    public JDBCUserDAO(BasicDataSource dataSource, SQLLoader sqlStatements) throws DAOException {
        this.dataSource = dataSource;
        this.sqlStatements = sqlStatements;
    }

    public final void init() throws DAOException, RemoteException {
        // Create the users table if it does not already exist
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            if (DBManager.tableExists(TABLE_NAME, conn)) {
                return;
            }
            ps = sqlStatements.buildSQLStatement(conn, CREATE_USERS_TABLE_KEY);
            ps.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot create User table", e);
        } catch (IOException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot create User table", e);
        } finally {
            DBManager.closeStatement(ps);
            DBManager.closeConnection(conn);
        }
    }

    @Override
    public boolean userExists(String username) throws DAOException {
        if (username == null) {
            throw new IllegalArgumentException("Null username");
        }

        username = username.trim().toLowerCase();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();

            Map<String, String> namedParams = new HashMap<String, String>();
            String where = new SQLExtractor("username = '" + username + "'", SQLExtractor.Type.WHERE).extractWhere();
            namedParams.put(NAMED_PARAM_WHERE, where);

            ps = sqlStatements.buildSQLStatement(conn, GET_USER_KEY, namedParams);
            rs = ps.executeQuery();

            if (rs.next() && rs.getString(2).equalsIgnoreCase(username)) {
                return true;
            }
        } catch (SQLException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot get Usernames", e);
        } catch (IOException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot get Usernames", e);
        } finally {
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(ps);
            DBManager.closeConnection(conn);
        }

        return false;
    }

    @Override
    public void addUser(User user) throws DAOException {
        if (user == null) {
            throw new IllegalArgumentException("Null user");
        }
        addUser(user.getUsername(), user.getPassword(), user.getAccessLevel());
    }

    @Override
    public void addUser(String username, String passwordHash) throws DAOException {
        addUser(username, passwordHash, User.DEFAULT_ACCESS_LEVEL);
    }

    @Override
    public void addUser(String username, String passwordHash, AccessLevel accessLevel) throws DAOException {
        if (username == null) {
            throw new IllegalArgumentException("Invalid username (null)");
        } else if (passwordHash == null) {
            throw new IllegalArgumentException("Invalid password (null)");
        } else if (accessLevel == null) {
            throw new IllegalArgumentException("Invalid access level (null)");
        }

        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);

            // Make case insensitive
            Object[] parameters = {username.trim().toLowerCase(), passwordHash, accessLevel.toString()};
            ps = sqlStatements.buildSQLStatement(conn, INSERT_USER_KEY, parameters);
            ps.executeUpdate();
            conn.commit();
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
    }

    @Override
    public void updateUser(int userId, String username, String passwordHash, AccessLevel accessLevel) throws DAOException {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);

            int updated = 0;
            if (username != null) {
                logger.fine("Updating user (" + userId + ") username");
                updated--;
                Object[] parameters = {username.trim().toLowerCase(), userId};
                ps = sqlStatements.buildSQLStatement(conn, UPDATE_USER_USERNAME_KEY, parameters);
                updated += ps.executeUpdate();
            }
            if (passwordHash != null) {
                logger.fine("Updating user (" + userId + ") password");
                updated--;
                Object[] parameters = {passwordHash, userId};
                ps = sqlStatements.buildSQLStatement(conn, UPDATE_USER_PASS_KEY, parameters);
                updated += ps.executeUpdate();
            }
            if (accessLevel != null) {
                logger.fine("Updating user (" + userId + ") access level");
                updated--;
                Object[] parameters = {accessLevel.toString(), userId};
                ps = sqlStatements.buildSQLStatement(conn, UPDATE_USER_ACCESS_KEY, parameters);
                updated += ps.executeUpdate();
            }

            conn.commit();

            if (updated == 0) {
                return;
            }

            DBManager.rollbackConn(conn); // Don't allow partial update to the user
            throw new DAOException("Failed to update user");
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
    }

    @Override
    public void deleteUser(String username) throws DAOException {
        if (username == null) {
            throw new IllegalArgumentException("Null username");
        }

        String user = username.trim().toLowerCase();

        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            Object[] parameters = {username.trim().toLowerCase()};
            ps = sqlStatements.buildSQLStatement(conn, DELETE_USER_KEY, parameters);
            ps.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot delete the User", e);
        } catch (IOException e) {
            throw new DAOException("Cannot delete the User", e);
        } finally {
            DBManager.closeStatement(ps);
            DBManager.closeConnection(conn);
        }
    }

    @Override
    public User checkPassword(String username, String passwordHash) throws DAOException {
        if (username == null || passwordHash == null) {
            throw new IllegalArgumentException("Invalid userId/password (null)");
        }

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs;

        try {
            conn = dataSource.getConnection();
            Object[] parameters = {username.trim().toLowerCase(), passwordHash};
            ps = sqlStatements.buildSQLStatement(conn, CHECK_PASSWORD_KEY, parameters);
            rs = ps.executeQuery();

            // Return a User hash comparison is correct
            if (rs.next()) {
                return new User(rs.getInt(2), username, null, User.AccessLevel.valueOf(rs.getString(3)));
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

        // If hashes 
        return null;
    }

    public boolean isTableUsersEmpty() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs;
        try {
            conn = dataSource.getConnection();
            ps = sqlStatements.buildSQLStatement(conn, CHECK_TABLE_EMPTY_KEY);
            rs = ps.executeQuery();
            boolean check = false;
            while (rs.next()) {
                check = rs.getInt(1) == 0;
            }
            return check;
        } catch (IOException ex) {
            Logger.getLogger(JDBCUserDAO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(JDBCUserDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public int checkTableEmpty() throws DAOException {

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs;

        try {
            conn = dataSource.getConnection();
            ps = sqlStatements.buildSQLStatement(conn, CHECK_TABLE_EMPTY_KEY);
            rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
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
        return -1;
    }

    public RemoteUserResultsInterface retrieveUsers() throws DAOException, RemoteException {
        logger.fine("Getting all users");
        return retrieveUsers(null, null);
    }

    public RemoteUserResultsInterface retrieveUsers(String whereClause, String orderByClause) throws DAOException, RemoteException {
        return new RemoteJDBCUserResults(sqlStatements, dataSource, whereClause, orderByClause);
    }

    public synchronized List<User> getItems(int startEntry, int endEntry) throws UserRetrieveException {
        String retrieveUsers = null;
        if (startEntry < 0) {
            startEntry = 0;
        }

        if (endEntry < 0) {
            endEntry = 0;
        }

        if (endEntry <= startEntry) {
            return new ArrayList<User>();
        }

        ArrayList<User> results = new ArrayList<User>();

        Connection conn = null;
        PreparedStatement retrieve = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();

            retrieve = conn.prepareStatement(retrieveUsers);
            retrieve.setInt(1, startEntry);
            retrieve.setInt(2, endEntry - startEntry);
            rs = retrieve.executeQuery();

            while (rs.next()) {
                int id = rs.getInt(1);
                String username = rs.getString(2);
                AccessLevel accessLevel;

                try {
                    accessLevel = AccessLevel.valueOf(rs.getString(3));
                } catch (IllegalArgumentException e) {
                    accessLevel = User.DEFAULT_ACCESS_LEVEL;
                }

                results.add(new User(id, username, null, accessLevel));
            }
        } catch (SQLException e) {
            throw new UserRetrieveException("Could not properly retrieve the Users: " + e);
        } finally {
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(retrieve);
            DBManager.closeConnection(conn);
        }

        return results;
    }

}
