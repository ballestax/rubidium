/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rb.persistence.JDBC;

import com.rb.Aplication;
import com.rb.DBManager;
import com.rb.GUIManager;
import com.rb.TimeWaste;
import com.rb.persistence.dao.DAOException;
import com.rb.persistence.SQLLoader;
import com.rb.persistence.dao.AdditionalDAO;
import com.rb.persistence.dao.ClientDAO;
import com.rb.persistence.dao.ConciliacionDAO;
import com.rb.persistence.dao.ConfigDAO;
import com.rb.persistence.dao.DAOFactory;
import com.rb.persistence.dao.IngredientDAO;
import com.rb.persistence.dao.InvoiceDAO;
import com.rb.persistence.dao.ItemDAO;
import com.rb.persistence.dao.LocationDAO;
import com.rb.persistence.dao.OrderDAO;
import com.rb.persistence.dao.ProductDAO;
import com.rb.persistence.dao.UserDAO;
import com.rb.persistence.dao.UtilDAO;
import java.beans.PropertyVetoException;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.log4j.Logger;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.properties.EncryptableProperties;

/**
 *
 * @author ballestax
 */
public class JDBCDAOFactory extends DAOFactory {

    public static final String CONFIG_FILE = "config/jdbc-dao.properties";
    public static final String CONFIG_FILE_LOCAL = "config/jdbc-dao-local.properties";
    public static final String PROPERTIES_SQL_FILE = "database.sql.filename";
    public static final String PROPERTIES_DB_PREFIJO = "database.prefijo";
    public static final String PROPERTIES_DB_NAME = "database.name";
    public static final String PROPERTIES_DB_HOST = "database.host";
    public static final String PROPERTIES_DB_DRIVER = "database.driver";
    public static final String PROPERTIES_DB_URL = "database.url";
    public static final String PROPERTIES_DB_USERNAME = "database.username";
    public static final String PROPERTIES_DB_PASSWORD = "database.password";

    public static final String COUNT_TABLE_KEY = "COUNT_TABLE";
    public static final String NAMED_PARAM_QUERY = "{query}";
    public static final String CHECK_TABLE_KEY = "CHECK_TABLE";
    public static final String DROP_TABLE_KEY = "DROP_TABLE";
    public static final String TRUNCATE_TABLE_KEY = "TRUNCATE_TABLE";
    public static final String NAMED_PARAM_TABLE = "{table}";

    private static final Logger logger = Logger.getLogger(JDBCDAOFactory.class.getCanonicalName());

    private BasicDataSource dataSource;
    private SQLLoader sqlStatements;

    private final String DRIVER = "org.mariadb.jdbc.Driver";
    private String pass;

    public JDBCDAOFactory() throws DAOException, PropertyVetoException {
        try {
            StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
            encryptor.setPassword(String.valueOf(new TimeWaste().cst()));
            EncryptableProperties properties = new EncryptableProperties(encryptor);
//            Properties properties = new Properties();

            if (Aplication.isLocal()) {
                properties.load(new FileInputStream(CONFIG_FILE_LOCAL));
            } else {
                properties.load(new FileInputStream(CONFIG_FILE));
            }

            String prefijo = properties.getProperty(PROPERTIES_DB_PREFIJO);
            String host = properties.getProperty(PROPERTIES_DB_HOST);

            String file = properties.getProperty(PROPERTIES_SQL_FILE);
            String dbName = properties.getProperty(PROPERTIES_DB_NAME);

            pass = properties.getProperty(PROPERTIES_DB_PASSWORD);

            setupDataSource(properties);
            createDatabaseFromProperties();
            

            sqlStatements = new SQLLoader(file, dbName);

        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void clean() throws DAOException {
        Connection conn = null;
        ResultSet rs = null;
        String table = null;
        try {
            conn = dataSource.getConnection();
            rs = conn.getMetaData().getTables(null, null, "%", new String[]{"TABLE"});

            while (rs.next()) {
                try {
                    table = rs.getString("TABLE_NAME");
                    logger.info("Deleting table: " + rs.getString("TABLE_NAME"));
                    DBManager.deleteTable(table, conn, sqlStatements);
                    logger.info("Success!");
                } catch (Exception e) {
                    logger.warn("Could not delete table: " + rs.getString("TABLE_NAME") + ": " + e);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Could not delete tables.", e);
        } finally {
            DBManager.closeResultSet(rs);
            DBManager.closeConnection(conn);
        }
    }

    public void truncate(String table) throws DAOException {
        Connection conn = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            rs = conn.getMetaData().getTables(null, null, "%", new String[]{"TABLE"});

            while (rs.next()) {
                try {
                    table = rs.getString("TABLE_NAME");
                    logger.info("Deleting table: " + rs.getString("TABLE_NAME"));
                    DBManager.deleteTable(table, conn, sqlStatements);
                    logger.info("Success!");
                } catch (Exception e) {
                    logger.warn("Could not delete table: " + rs.getString("TABLE_NAME") + ": " + e);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Could not delete tables.", e);
        } finally {
            DBManager.closeResultSet(rs);
            DBManager.closeConnection(conn);
        }
    }

    @Override
    public synchronized void close() {
        try {
            dataSource.close();
        } catch (SQLException ex) {
        }
    }

    public BasicDataSource getDataSource() {
//        dataSource.setUrl("jdbc:mysql://localhost:3306/" + Aplication.DATABASE);
        return dataSource;
    }

    public void setDataSource(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public SQLLoader getSqlStatements() {
        return sqlStatements;
    }
//
//    public void setSqlStatements(SQLLoader sqlStatements) {
//        this.sqlStatements = sqlStatements;
//    }

    private synchronized void setupDataSource(Properties properties) throws PropertyVetoException, IOException {
        try {
            String driver = properties.getProperty(PROPERTIES_DB_DRIVER);
            String prefijo = properties.getProperty(PROPERTIES_DB_PREFIJO);
            String host = properties.getProperty(PROPERTIES_DB_HOST);
            String url = properties.getProperty(PROPERTIES_DB_URL);
            String username = properties.getProperty(PROPERTIES_DB_USERNAME);
            String password = properties.getProperty(PROPERTIES_DB_PASSWORD);

            dataSource = new BasicDataSource();
            dataSource.setDriverClassName(driver);
            dataSource.setUrl(prefijo + "://" + host + "/" + url);
            dataSource.setUsername(username);
            dataSource.setPassword(password);
            dataSource.setValidationQuery("SELECT 1");
            dataSource.setTestOnBorrow(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private synchronized void checkCreateDatabase() throws SQLException, DAOException {
        boolean checkDBExists = DBManager.checkDBExists(Aplication.DATABASE, dataSource.getConnection());
        if (!checkDBExists) {
            dataSource.getConnection().prepareStatement("CREATE DATABASE IF NOT EXISTS " + Aplication.DATABASE
                    + " CHARACTER SET utf8 COLLATE utf8_general_ci").execute();
        }
    }

    private char[] asKPassword() {
        JPasswordField pass = new JPasswordField();
        String texto = "<html><p>Ingrese la contraseña del usuario root de su base de datos Mysql.</p>"
                + "<p>Para iniciar el programa debe tener mysql instalado</p>"
                + "<p></p>"
                + "<html>";

        Box contenedor = new Box(BoxLayout.Y_AXIS);
        contenedor.add(new JLabel(texto));
        contenedor.add(pass);
        JOptionPane.showMessageDialog(null, contenedor, "Inicializando Campaign Time", JOptionPane.INFORMATION_MESSAGE);
        return pass.getPassword();
    }

    public final void createDatabase() {
        createDatabase(asKPassword());
    }

    public final void createDatabaseFromProperties() {
        if (Aplication.INSTALL_DB) {
            createDatabase(pass.toCharArray());
        }
    }

    public final void createDatabase(char[] pass) {
        Connection con = null;
        try {
            //crear la database
            //System.err.println(DRIVER);
            Class.forName(DRIVER);
            try {
                con = dataSource.getConnection();
                if (DBManager.checkDBExists(Aplication.DATABASE, con)) {
                    logger.debug("Skipping create database. Already exist:" + Aplication.DATABASE);
                    return;
                }
            } catch (Exception e) {
                logger.debug(e);
            }
            logger.debug("Creating database:" + Aplication.DATABASE);
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/?user=" + Aplication.DB_USER + "&password=" + String.copyValueOf(pass));
            Statement statement = con.createStatement();
            statement.executeUpdate("CREATE DATABASE IF NOT EXISTS " + Aplication.DATABASE + " CHARACTER SET utf8 COLLATE utf8_general_ci");
        } catch (ClassNotFoundException | SQLException e) {
            logger.error("Error creating database", e);
            GUIManager.showErrorMessage(null, "Error creando la  base de datos.", "Error");
//            System.exit(1);
        }
    }

    //preguntar password mysql
    public UserDAO getUserDAO() throws DAOException {
        return new JDBCUserDAO(getDataSource(), sqlStatements);
    }

    @Override
    public UtilDAO getUtilDAO() throws DAOException {
        return new JDBCUtilDAO(getDataSource(), sqlStatements);
    }

    @Override
    public ConfigDAO getConfigDAO() throws DAOException {
        return new JDBCConfigDAO(getDataSource(), sqlStatements);
    }

    @Override
    public ProductDAO getProductDAO() throws DAOException {
        return new JDBCProductDAO(getDataSource(), sqlStatements);
    }

    @Override
    public IngredientDAO getIngredientDAO() throws DAOException {
        return new JDBCIngredientDAO(getDataSource(), sqlStatements);
    }

    @Override
    public AdditionalDAO getAdditionalDAO() throws DAOException {
        return new JDBCAdditionalDAO(getDataSource(), sqlStatements);
    }

    @Override
    public InvoiceDAO getInvoiceDAO() throws DAOException {
        return new JDBCInvoiceDAO(getDataSource(), sqlStatements);
    }

    @Override
    public ClientDAO getClientDAO() throws DAOException {
        return new JDBCClientDAO(getDataSource(), sqlStatements);
    }

    @Override
    public ItemDAO getItemDAO() throws DAOException {
        return new JDBCItemDAO(getDataSource(), sqlStatements);
    }

    @Override
    public ConciliacionDAO getConciliacionDAO() throws DAOException {
        return new JDBCConciliacionDAO(dataSource, sqlStatements);
    }

    @Override
    public LocationDAO getLocationDAO() throws DAOException {
        return new JDBCLocationDAO(dataSource, sqlStatements);
    }

    @Override
    public OrderDAO getOrderDAO() throws DAOException {
        return new JDBCOrderDAO(getDataSource(), sqlStatements);
    }
    
    

}
