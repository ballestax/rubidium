/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rb.persistence.JDBC;

import com.rb.DBManager;
import com.rb.domain.Ingredient;
import com.rb.persistence.SQLExtractor;
import com.rb.persistence.SQLLoader;
import com.rb.persistence.dao.DAOException;
import com.rb.persistence.dao.IngredientDAO;
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
public class JDBCIngredientDAO implements IngredientDAO {

    public static final String TABLE_NAME = "ingredients";
    public static final String NAMED_PARAM_WHERE = "{where}";
    public static final String NAMED_PARAM_ORDER_BY = "{orderby}";
    private static final Logger logger = Logger.getLogger(JDBCIngredientDAO.class.getCanonicalName());
    private final DataSource dataSource;
    private final SQLLoader sqlStatements;
    protected static final String CREATE_INGREDIENTS_TABLE_KEY = "CREATE_INGREDIENTS_TABLE";
    protected static final String ADD_INGREDIENT_KEY = "ADD_INGREDIENT";
    protected static final String UPDATE_INGREDIENT_KEY = "UPDATE_INGREDIENT";
    protected static final String GET_INGREDIENT_KEY = "GET_INGREDIENT";
    protected static final String DELETE_INGREDIENT_KEY = "DELETE_INGREDIENT";

    public JDBCIngredientDAO(DataSource dataSource, SQLLoader sqlStatements) throws DAOException {
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
            ps = sqlStatements.buildSQLStatement(conn, CREATE_INGREDIENTS_TABLE_KEY);
            ps.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot create Ingredient table", e);
        } catch (IOException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot create Ingredient table", e);
        } finally {
            DBManager.closeStatement(ps);
            DBManager.closeConnection(conn);
        }
    }

    public Ingredient getIngredientBy(String query) throws DAOException {
        String retrieveImporter;
        try {
            SQLExtractor sqlExtractorWhere = new SQLExtractor(query, SQLExtractor.Type.WHERE);;
            Map<String, String> namedParams = new HashMap<String, String>();
            namedParams.put(NAMED_PARAM_WHERE, sqlExtractorWhere.extractWhere());
            retrieveImporter = sqlStatements.getSQLString(GET_INGREDIENT_KEY, namedParams);

        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the ingredient", e);
        } catch (IOException e) {
            throw new DAOException("Could not properly retrieve the ingredient", e);
        }
        Connection conn = null;
        PreparedStatement retrieve = null;
        ResultSet rs = null;
        Ingredient ingredient = null;
        try {
            conn = dataSource.getConnection();
            retrieve = conn.prepareStatement(retrieveImporter);
            rs = retrieve.executeQuery();
            while (rs.next()) {
                ingredient = new Ingredient();
                ingredient.setId(rs.getInt(1));
                ingredient.setName(rs.getString(2));
                ingredient.setCode(rs.getString(3));
                ingredient.setMeasure(rs.getString(4));
            }
        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the Ingredient: " + e);
        } finally {
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(retrieve);
            DBManager.closeConnection(conn);
        }
        return ingredient;
    }

    @Override
    public Ingredient getIngredient(int id) throws DAOException {
        return getIngredientBy("id=" + id);
    }

    @Override
    public ArrayList<Ingredient> getIngredientList() throws DAOException {
        return getIngredientList("", "");
    }

    public ArrayList<Ingredient> getIngredientList(String where, String orderBy) throws DAOException {
        String retrieveIngredients;
        ArrayList<Ingredient> ingredients = new ArrayList<>();
        try {
            SQLExtractor sqlExtractorWhere = new SQLExtractor(where, SQLExtractor.Type.WHERE);;
            SQLExtractor sqlExtractorOrderBy = new SQLExtractor(orderBy, SQLExtractor.Type.ORDER_BY);;
            Map<String, String> namedParams = new HashMap<>();
            namedParams.put(NAMED_PARAM_WHERE, sqlExtractorWhere.extractWhere());
            namedParams.put(NAMED_PARAM_ORDER_BY, sqlExtractorOrderBy.extractOrderBy());
            retrieveIngredients = sqlStatements.getSQLString(GET_INGREDIENT_KEY, namedParams);

        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the Ingredient List", e);
        } catch (IOException e) {
            throw new DAOException("Could not properly retrieve the Ingredient List", e);
        }

        Connection conn = null;
        PreparedStatement retrieve = null;
        ResultSet rs = null;
        Ingredient ingredient = null;
        try {
            conn = dataSource.getConnection();
            retrieve = conn.prepareStatement(retrieveIngredients);
            rs = retrieve.executeQuery();

            while (rs.next()) {
                ingredient = new Ingredient();
                ingredient.setId(rs.getInt(1));
                ingredient.setName(rs.getString(2));
                ingredient.setCode(rs.getString(3));
                ingredient.setMeasure(rs.getString(4));
                ingredients.add(ingredient);
            }
        } catch (SQLException e) {
            throw new DAOException("Could not proper retrieve the Ingredient: " + e);
        } finally {
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(retrieve);
            DBManager.closeConnection(conn);
        }
        return ingredients;
    }

    @Override
    public void addIngredient(Ingredient ingredient) throws DAOException {
        if (ingredient == null) {
            throw new IllegalArgumentException("Null ingredient");
        }
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            Object[] parameters = {
                ingredient.getName(),
                ingredient.getCode(),
                ingredient.getMeasure()
            };
            ps = sqlStatements.buildSQLStatement(conn, ADD_INGREDIENT_KEY, parameters);

            ps.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot add Ingredient", e);
        } catch (IOException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot add Ingredient", e);
        } finally {
            DBManager.closeStatement(ps);
            DBManager.closeConnection(conn);
        }
    }

    @Override
    public void deleteIngredient(int id) throws DAOException {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            Object[] parameters = {id};
            ps = sqlStatements.buildSQLStatement(conn, DELETE_INGREDIENT_KEY, parameters);
            ps.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Cannot delete the ingredient", e);
        } catch (IOException e) {
            throw new DAOException("Cannot delete the ingredient", e);
        } finally {
            DBManager.closeStatement(ps);
            DBManager.closeConnection(conn);
        }
    }

    @Override
    public void updateIngredient(Ingredient ingredient) throws DAOException {
        Connection conn = null;
        PreparedStatement update = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            Object[] parameters = {
                ingredient.getName(),
                ingredient.getCode(),
                ingredient.getMeasure()
            };
            update = sqlStatements.buildSQLStatement(conn, UPDATE_INGREDIENT_KEY, parameters);
            update.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Could not properly update the ingredient", e);
        } catch (IOException e) {
            DBManager.rollbackConn(conn);
            throw new DAOException("Could not properly update the ingredient", e);
        } finally {
            DBManager.closeStatement(update);
            DBManager.closeConnection(conn);
        }
    }

    public ArrayList<Ingredient> getIngredientByQuery(String query) throws DAOException {
        String retrieve = query;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Ingredient ingredient = null;
        ArrayList<Ingredient> ingredients = new ArrayList<>();
        try {
            conn = dataSource.getConnection();
            ps = conn.prepareStatement(retrieve);
            rs = ps.executeQuery();
            while (rs.next()) {
                ingredient = new Ingredient();
                ingredient.setId(rs.getInt(1));
                ingredient.setName(rs.getString(2));
                ingredient.setCode(rs.getString(3));
                ingredient.setMeasure(rs.getString(4));
                ingredients.add(ingredient);
            }
        } catch (SQLException e) {
            throw new DAOException("Could not properly retrieve the Ingredient: " + e);
        } finally {
            DBManager.closeResultSet(rs);
            DBManager.closeStatement(ps);
            DBManager.closeConnection(conn);
        }
        return ingredients;
    }

}
