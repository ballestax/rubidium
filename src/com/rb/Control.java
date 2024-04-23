/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.  
 */
package com.rb;

import com.rb.domain.Additional;
import com.rb.domain.CashMov;
import com.rb.domain.Category;
import com.rb.domain.Client;
import com.rb.domain.Conciliacion;
import com.rb.domain.ConfigDB;
import com.rb.domain.Cycle;
import com.rb.domain.Ingredient;
import com.rb.domain.InventoryEvent;
import com.rb.domain.Invoice;
import com.rb.domain.Item;
import com.rb.domain.Location;
import com.rb.domain.Order;
import com.rb.domain.Permission;
import com.rb.domain.Presentation;
import com.rb.domain.Product;
import com.rb.domain.ProductoPed;
import com.rb.domain.Rol;
import com.rb.domain.Station;
import com.rb.domain.Table;
import com.rb.domain.User;
import com.rb.domain.Waiter;
import com.rb.gui.PanelPedido;
import com.rb.persistence.JDBC.JDBCAdditionalDAO;
import com.rb.persistence.JDBC.JDBCClientDAO;
import com.rb.persistence.JDBC.JDBCConciliacionDAO;
import com.rb.persistence.JDBC.JDBCConfigDAO;
import com.rb.persistence.JDBC.JDBCDAOFactory;
import com.rb.persistence.JDBC.JDBCIngredientDAO;
import com.rb.persistence.JDBC.JDBCInvoiceDAO;
import com.rb.persistence.JDBC.JDBCItemDAO;
import com.rb.persistence.JDBC.JDBCLocationDAO;
import com.rb.persistence.JDBC.JDBCOrderDAO;
import com.rb.persistence.JDBC.JDBCProductDAO;
import com.rb.persistence.JDBC.JDBCUserDAO;
import com.rb.persistence.JDBC.JDBCUtilDAO;
import com.rb.persistence.dao.DAOException;
import com.rb.persistence.dao.DAOFactory;
import com.rb.persistence.dao.RemoteUserResultsInterface;
import com.rb.persistence.dao.UserRetrieveException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author ballestax
 */
public class Control {

    private Aplication app;
    public static final Logger logger = LogManager.getLogger(Control.class.getCanonicalName());

    public Control(Aplication app) {
        this.app = app;
    }

    public void initDatabase() {
        try {

            logger.debug("Init database...");

            /*if (Aplication.INSTALL_DB) {
                //preguntas la contraseña y crea la database
                new JDBCDAOFactory().createDatabase();
            } else {
                //crea la database from properties pass
                new JDBCDAOFactory().createDatabaseFromProperties();
            }*/
            JDBCUserDAO userDAO = (JDBCUserDAO) DAOFactory.getInstance().getUserDAO();
            userDAO.init();

            JDBCConfigDAO configDAO = (JDBCConfigDAO) DAOFactory.getInstance().getConfigDAO();
            configDAO.init();

            JDBCProductDAO prodDAO = (JDBCProductDAO) DAOFactory.getInstance().getProductDAO();
            prodDAO.init();

            JDBCIngredientDAO ingDAO = (JDBCIngredientDAO) DAOFactory.getInstance().getIngredientDAO();
            ingDAO.init();

            JDBCAdditionalDAO addDAO = (JDBCAdditionalDAO) DAOFactory.getInstance().getAdditionalDAO();
            addDAO.init();

            JDBCInvoiceDAO invoiceDAO = (JDBCInvoiceDAO) DAOFactory.getInstance().getInvoiceDAO();
            invoiceDAO.init();

            JDBCClientDAO clientDAO = (JDBCClientDAO) DAOFactory.getInstance().getClientDAO();
            clientDAO.init();

            JDBCItemDAO itemDAO = (JDBCItemDAO) DAOFactory.getInstance().getItemDAO();
            itemDAO.init();

            JDBCConciliacionDAO conciliacionDAO = (JDBCConciliacionDAO) DAOFactory.getInstance().getConciliacionDAO();
            conciliacionDAO.init();

            JDBCLocationDAO locationDAO = (JDBCLocationDAO) DAOFactory.getInstance().getLocationDAO();
            locationDAO.init();

            JDBCOrderDAO orderDAO = (JDBCOrderDAO) DAOFactory.getInstance().getOrderDAO();
            orderDAO.init();

            JDBCUtilDAO utilDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            utilDAO.init();

        } catch (Exception e) {
            logger.error("Error initializing database", e);
            GUIManager.showErrorMessage(null, e, "Error inicializando la database");
        }
    }

    public User verifyUser(String user, char[] pass) {
        try {
            JDBCUserDAO userDAO = (JDBCUserDAO) DAOFactory.getInstance().getUserDAO();
            //userDAO.init();
            return userDAO.checkPassword(user, String.valueOf(pass));
        } catch (DAOException ex) {
            logger.error("Error checking user: " + user, ex);
        }
        return null;
    }

    public boolean tableUserEmpty() {
        try {
            JDBCUserDAO userDAO = (JDBCUserDAO) DAOFactory.getInstance().getUserDAO();
            //userDAO.init();
//            System.out.println(userDAO.checkTableEmpty());
            return userDAO.checkTableEmpty() == 0;
        } catch (DAOException ex) {
            logger.error("Error checking table users is empty: ", ex);
        }
        return false;
    }

    public boolean tableRolEmpty() {
        try {
            JDBCUserDAO userDAO = (JDBCUserDAO) DAOFactory.getInstance().getUserDAO();
            //userDAO.init();
//            System.out.println(userDAO.checkTableEmpty());
            return userDAO.checkTableEmpty() == 0;
        } catch (DAOException ex) {
            logger.error("Error checking table users is empty: ", ex);
        }
        return false;
    }

    public boolean checkEmpty(String table) {
        try {
            JDBCUtilDAO utilDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            return utilDAO.checkTableEmpty(table);
        } catch (DAOException ex) {
            logger.error("Error checking table " + table + " is empty: ", ex);
        }
        return false;
    }

    public void addConfig(ConfigDB config) {
        try {
            JDBCConfigDAO configDAO = (JDBCConfigDAO) DAOFactory.getInstance().getConfigDAO();
            configDAO.addConfigDB(config);
        } catch (Exception e) {
            logger.error("Error adding config.", e);
        }
    }

    public ConfigDB getConfigLocal(String clave) {
        try {
            String userName = app.getUser().getUsername();
            String userDevice = Aplication.getUserDevice();
            JDBCConfigDAO configDAO = (JDBCConfigDAO) DAOFactory.getInstance().getConfigDAO();
            return configDAO.getConfigDB(clave, userName, userDevice);
        } catch (Exception e) {
            logger.error("Error getting config.", e);
            return new ConfigDB();
        }
    }

    public ConfigDB getConfig(String clave) {
        try {
            JDBCConfigDAO configDAO = (JDBCConfigDAO) DAOFactory.getInstance().getConfigDAO();
            return configDAO.getConfigDB(clave);
        } catch (Exception e) {
            logger.error("Error getting config.", e);
            return new ConfigDB();
        }
    }

    public void updateConfig(ConfigDB config) {
        try {
            JDBCConfigDAO configDAO = (JDBCConfigDAO) DAOFactory.getInstance().getConfigDAO();
            configDAO.updateConfigDB(config);
        } catch (Exception e) {
            logger.error("Error updating config.", e);
        }
    }

    public boolean existConfig(String code, String user, String device) {
        try {
            JDBCConfigDAO configDAO = (JDBCConfigDAO) DAOFactory.getInstance().getConfigDAO();
            return configDAO.existConfig(code, user, device) >= 1;
        } catch (Exception e) {
            logger.error("Error checking config.", e);
            return false;
        }
    }

    public int existClave(String tabla, String columna, String clave) {
        try {
            JDBCUtilDAO utilDAO = (JDBCUtilDAO) JDBCDAOFactory.getInstance().getUtilDAO();
            return utilDAO.existClave(tabla, columna, clave);
        } catch (Exception e) {
            return -1;
        }
    }

    public int existClaveMult(String tabla, String columna, String where) {
        try {
            JDBCUtilDAO utilDAO = (JDBCUtilDAO) JDBCDAOFactory.getInstance().getUtilDAO();
            return utilDAO.existClaveMult(tabla, columna, where);
        } catch (Exception e) {
            return -1;
        }
    }

    public int getMaxIDTabla(String tabla) {
        try {
            JDBCUtilDAO utilDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            return utilDAO.getMaxID(tabla);
        } catch (Exception e) {
            logger.error("Error getting maxID for: " + tabla, e);
            return 0;
        }
    }

    public Object getMaxValue(String tabla, String field) {
        try {
            JDBCUtilDAO utilDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            return utilDAO.getMaxValue(tabla, field);
        } catch (DAOException e) {
            logger.error("Error getting max value for: " + tabla, e);
            return 0;
        }
    }

    public boolean addPermission(Permission permission) {
        try {
            JDBCUtilDAO utilDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            utilDAO.addPermission(permission);
            return true;
        } catch (DAOException ex) {
            String msg = "Error adding permission: " + permission.getName();
            logger.error(msg, ex);
            GUIManager.showErrorMessage(null, msg, "Error");
            return false;
        }
    }

    public boolean addPermissionRole(Rol rol, ArrayList<Permission> permissions) {
        try {
            JDBCUtilDAO utilDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            utilDAO.addPermissionRole(rol, permissions);
            return true;
        } catch (DAOException ex) {
            String msg = "Error adding permissions to rol: " + rol.getName();
            logger.error(msg, ex);
            GUIManager.showErrorMessage(null, msg, "Error");
            return false;
        }
    }

    public ArrayList<Permission> getPermissionList() {
        try {
            JDBCUtilDAO utilDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            return utilDAO.getPermissionList("");
        } catch (DAOException ex) {
            logger.error("Error getting permissions.", ex);
            return null;
        }
    }

    public ArrayList<Permission> getPermissionByRole(Rol role) {
        try {
            JDBCUtilDAO utilDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            return utilDAO.getPermissionByRole(role);
        } catch (DAOException ex) {
            logger.error("Error getting permissions.", ex);
            return null;
        }
    }

    public ArrayList<Permission> getPermissionList(int[] ids) {

        StringBuilder stb = new StringBuilder();
        for (int id : ids) {
            stb.append("id=").append(id).append(" or ");
        }
        stb.delete(stb.length() - 4, stb.length());
        try {
            JDBCUtilDAO utilDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            return utilDAO.getPermissionList(stb.toString());
        } catch (DAOException ex) {
            logger.error("Error getting permissions.", ex);
            return null;
        }
    }

    public Rol getRol(String name) {
        try {
            JDBCUtilDAO utilDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            return utilDAO.getRole(name);
        } catch (DAOException ex) {
            logger.error("Error getting role.", ex);
            return null;
        }
    }

    public User getUser(String name) {
        try {
            RemoteUserResultsInterface rUsers = ((JDBCUserDAO) DAOFactory.getInstance().getUserDAO()).retrieveUsers("username='" + name + "'", "");
            List<User> items = rUsers.getItems(0, 1);
            return items.get(0);
        } catch (DAOException ex) {
            logger.error("Error getting user.", ex);
            return null;
        } catch (RemoteException ex) {
            logger.error("Error getting User.", ex);
        } catch (UserRetrieveException ex) {
            logger.error("Error getting User.", ex);
        }
        return null;
    }

    public Object[] getRolByID(int id) {
        try {
            JDBCUtilDAO utilDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            return utilDAO.getRole("id", String.valueOf(id));
        } catch (DAOException ex) {
            logger.error("Error getting role.", ex);
            return null;
        }
    }

    public Object[] getRolByName(String name) {
        try {
            JDBCUtilDAO utilDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            return utilDAO.getRole("name", name);
        } catch (DAOException ex) {
            logger.error("Error getting role.", ex);
            return null;
        }
    }

    public boolean addRol(Rol rol) {
        try {
            JDBCUtilDAO utilDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            utilDAO.addRole(rol);
            return true;
        } catch (DAOException ex) {
            logger.error("Error adding role.", ex);
            return false;
        }
    }

    public ArrayList<Rol> getRolesList() {
        try {
            JDBCUtilDAO utilDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            return utilDAO.getRolesList();
        } catch (DAOException ex) {
            logger.error("Error getting roles list.", ex);
            return null;
        }
    }

    public boolean deletePermissionByRole(int id) {
        try {
            JDBCUtilDAO utilDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            utilDAO.deletePermissionsByRole(id);
            return true;
        } catch (DAOException ex) {
            logger.error("Error deleting permission y role: " + id, ex);
            GUIManager.showErrorMessage(null, "Error eliminando permisos", "Error");
            return false;
        }
    }

    public boolean addRoleUser(User user, Rol rol) {
        try {
            JDBCUtilDAO utilDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            utilDAO.assignRoleToUser(user, rol);
            return true;
        } catch (DAOException ex) {
            String msg = "Error adding rol to user: " + rol.getName() + ":" + user.getUsername();
            logger.error(msg, ex);
            GUIManager.showErrorMessage(null, msg, "Error");
            return false;
        }
    }

    public boolean hasPermission(User user, Permission perm) {
        if (perm == null) {
            return false;
        }
        try {
            JDBCUtilDAO utilDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            int val = utilDAO.hasPermission(user.getId(), perm.getId());
            return val == 1;
        } catch (DAOException ex) {
            String msg = "Error gettin info about permission: " + user.getUsername() + ":" + perm.getName();
            logger.error(msg, ex);
            GUIManager.showErrorMessage(null, msg, "Error");
        }
        return false;
    }

    public String getUseRole(int userID) {
        try {
            JDBCUtilDAO utilDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            return utilDAO.getUserRole(userID);
        } catch (DAOException ex) {
            String msg = "Error gettin info about role: " + userID;
            logger.error(msg, ex);
            GUIManager.showErrorMessage(null, msg, "Error");
        }
        return null;
    }

    public Permission getPermissionByName(String permissionName) {
        try {
            JDBCUtilDAO utilDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            ArrayList<Permission> permissionList = utilDAO.getPermissionList("name='" + permissionName + "'");
            if (permissionList != null && !permissionList.isEmpty()) {
                return permissionList.get(0);
            }
        } catch (DAOException ex) {
            logger.error("Error getting permissions.", ex);
        }
        return null;
    }

    public boolean addProduct(Product product) {
        try {
            JDBCProductDAO prodDAO = (JDBCProductDAO) DAOFactory.getInstance().getProductDAO();
            prodDAO.addProduct(product);
            return true;
        } catch (DAOException ex) {
            String msg = "Error adding product: " + product.getName();
            logger.error(msg, ex);
            GUIManager.showErrorMessage(null, msg, "Error");
            return false;
        }
    }

    public boolean updateProduct(Product prod) {
        try {
            JDBCProductDAO prodDAO = (JDBCProductDAO) DAOFactory.getInstance().getProductDAO();
            prodDAO.updateProduct(prod);
            return true;
        } catch (DAOException ex) {
            logger.error("Error updating Product.", ex);
            return false;
        }
    }

    public ArrayList<Product> getProductsList(String where, String order) {
        try {
            JDBCProductDAO prodDAO = (JDBCProductDAO) DAOFactory.getInstance().getProductDAO();
            return prodDAO.getProductList(where, order);
        } catch (DAOException ex) {
            logger.error("Error getting Products list.", ex);
            return null;
        }
    }

    public Product getProductByCode(String code) {
        try {
            JDBCProductDAO prodDAO = (JDBCProductDAO) DAOFactory.getInstance().getProductDAO();
            return prodDAO.getProductBy("code='" + code + "'");
        } catch (DAOException ex) {
            logger.error("Error getting Products list.", ex);
            return null;
        }
    }

    public Product getProductById(long id) {
        try {
            JDBCProductDAO prodDAO = (JDBCProductDAO) DAOFactory.getInstance().getProductDAO();
            return prodDAO.getProductBy("id=" + String.valueOf(id));
        } catch (DAOException ex) {
            logger.error("Error getting Products list.", ex);
            return null;
        }
    }

    public Product getProductByPressId(long pressID) {
        try {
            JDBCProductDAO prodDAO = (JDBCProductDAO) DAOFactory.getInstance().getProductDAO();
            return prodDAO.getProductByPressID("pp.id=" + pressID);
        } catch (DAOException ex) {
            logger.error("Error getting Products list.", ex);
            return null;
        }
    }

    public ArrayList<Ingredient> getIngredientList(String where) {
        try {
            JDBCIngredientDAO ingDAO = (JDBCIngredientDAO) DAOFactory.getInstance().getIngredientDAO();
            return ingDAO.getIngredientList(where, "");
        } catch (DAOException ex) {
            logger.error("Error getting Ingredients list.", ex);
            return null;
        }
    }

    public Ingredient getIngredient(String code) {
        try {
            JDBCIngredientDAO ingDAO = (JDBCIngredientDAO) DAOFactory.getInstance().getIngredientDAO();
            return ingDAO.getIngredientBy("code='" + code + "'");
        } catch (DAOException ex) {
            logger.error("Error getting Ingredients list.", ex);
            return null;
        }
    }

    public ArrayList<Ingredient> getIngredientsByProduct(String code) {
        try {
            JDBCUtilDAO utilDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            return utilDAO.getIngredientsByProduct(code);
        } catch (DAOException ex) {
            logger.error("Error getting Ingredients list.", ex);
            return null;
        }
    }

    public ArrayList<Additional> getAdditionalList(String where, String order) {
        try {
            JDBCAdditionalDAO addDAO = (JDBCAdditionalDAO) DAOFactory.getInstance().getAdditionalDAO();
            return addDAO.getAdditionalList(where, order);
        } catch (DAOException ex) {
            logger.error("Error getting Additional list.", ex);
            return null;
        }
    }

    public ArrayList<Table> getTableslList(String where, String order) {
        try {
            JDBCUtilDAO addDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            return addDAO.getTablesList(where, order);
        } catch (DAOException ex) {
            logger.error("Error getting Tables list.", ex);
            return null;
        }
    }
    
    public void updateTable(Table table) {
        try {
            JDBCUtilDAO utilDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            utilDAO.updateTable(table);
        } catch (Exception e) {
            logger.error("Error updating config.", e);
        }
    }
    
    public boolean updateTableStatus(Table table) {
        try {
            JDBCUtilDAO utilDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            return utilDAO.updateTableStatus(table);
        } catch (Exception e) {
            logger.error("Error updating config.", e);
            return false;
        }
    }

    public ArrayList<Waiter> getWaitresslList(String where, String order) {
        try {
            JDBCUtilDAO addDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            return addDAO.getWaitersList(where, order);
        } catch (DAOException ex) {
            logger.error("Error getting Waiters list.", ex);
            return null;
        }
    }

    public Waiter getWaitressByID(int id) {
        try {
            JDBCUtilDAO addDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            ArrayList<Waiter> waitersList = addDAO.getWaitersList("id=" + id, "");
            return waitersList.isEmpty() ? null : waitersList.get(0);
        } catch (DAOException ex) {
            logger.error("Error getting Waiter.", ex);
            return null;
        }
    }

    public Table getTableByID(int id) {
        try {
            JDBCUtilDAO addDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            ArrayList<Table> tableList = addDAO.getTablesList("id=" + id, "");
            return tableList.isEmpty() ? null : tableList.get(0);
        } catch (DAOException ex) {
            logger.error("Error getting Table.", ex);
            return null;
        }
    }

    public Client getClient(String cell) {
        try {
            JDBCClientDAO clientDAO = (JDBCClientDAO) DAOFactory.getInstance().getClientDAO();
            return clientDAO.getClientByCell(cell);
        } catch (DAOException ex) {
            logger.error("Error getting Table.", ex);
            return null;
        }
    }

    public ArrayList<Client> getClientList(String where, String order) {
        try {
            JDBCClientDAO clientDAO = (JDBCClientDAO) DAOFactory.getInstance().getClientDAO();
            return clientDAO.getClientListBy(where, order);
        } catch (DAOException ex) {
            logger.error("Error getting Table.", ex);
            return null;
        }
    }

    public int contarRows(String sql) {
        try {
            JDBCUtilDAO utilDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            return utilDAO.countTableRows(sql);
        } catch (DAOException ex) {
            logger.error("Error couting rows.", ex);
            GUIManager.showErrorMessage(null, "Error consultando numero de registros", "Error");
            return -1;
        }
    }

    public int countInvoices(String where) {
        String query = "select * from invoices " + (where != null && !where.isEmpty() ? "where " + where : "");
        return contarRows(query);
    }

    public ArrayList<Invoice> getInvoicesLitelList(String where, String order, int init, int end) {
        try {
            JDBCInvoiceDAO invoiceDAO = (JDBCInvoiceDAO) DAOFactory.getInstance().getInvoiceDAO();
            return invoiceDAO.getInvoiceLiteList(where, order, init, end);
        } catch (DAOException ex) {
            logger.error("Error getting Invoices list.", ex);
            return null;
        }
    }

    public ArrayList<Invoice> getInvoicesLitelList(String where, String order) {
        try {
            JDBCInvoiceDAO invoiceDAO = (JDBCInvoiceDAO) DAOFactory.getInstance().getInvoiceDAO();
            return invoiceDAO.getInvoiceLiteList(where, order);
        } catch (DAOException ex) {
            logger.error("Error getting Invoices list.", ex);
            return null;
        }
    }

    public ArrayList<Invoice> getInvoiceslList(String where, String order) {
        try {
            JDBCInvoiceDAO invoiceDAO = (JDBCInvoiceDAO) DAOFactory.getInstance().getInvoiceDAO();
            return invoiceDAO.getInvoiceList(where, order);
        } catch (DAOException ex) {
            logger.error("Error getting Invoices list.", ex);
            return null;
        }
    }

    public ArrayList<Invoice> getInvoiceslListWhitProducts(String where, String order) {
        try {
            JDBCInvoiceDAO invoiceDAO = (JDBCInvoiceDAO) DAOFactory.getInstance().getInvoiceDAO();
            return invoiceDAO.getInvoiceList(where, order);
        } catch (DAOException ex) {
            logger.error("Error getting Invoices list.", ex);
            return null;
        }
    }

    public Invoice getInvoiceByCode(String code) {
        try {
            JDBCInvoiceDAO invoiceDAO = (JDBCInvoiceDAO) DAOFactory.getInstance().getInvoiceDAO();
            return invoiceDAO.getInvoiceBy("code='" + code + "'");
        } catch (DAOException ex) {
            logger.error("Error getting Invoices list.", ex);
            return null;
        }
    }

    public boolean addInvoice(Invoice invoice) {
        try {
            JDBCInvoiceDAO invoiceDAO = (JDBCInvoiceDAO) DAOFactory.getInstance().getInvoiceDAO();
            invoiceDAO.addInvoice(invoice);
            return true;
        } catch (DAOException ex) {
            String msg = "Error adding invoice";
            logger.error(msg, ex);
            GUIManager.showErrorMessage(null, msg, "Error");
            return false;
        }
    }

    public void updateInvoice(Invoice invoice) {
        try {
            JDBCInvoiceDAO invoiceDAO = (JDBCInvoiceDAO) DAOFactory.getInstance().getInvoiceDAO();
            invoiceDAO.updateInvoice(invoice);
        } catch (DAOException ex) {
            String msg = "Error updating invoice";
            logger.error(msg, ex);
            GUIManager.showErrorMessage(null, msg, "Error");
        }
    }

    public void updateInvoiceFull(Invoice invoice, List<ProductoPed> oldProducts) {
        try {
            JDBCInvoiceDAO invoiceDAO = (JDBCInvoiceDAO) DAOFactory.getInstance().getInvoiceDAO();
            invoiceDAO.updateInvoiceFull(invoice, oldProducts);
        } catch (DAOException ex) {
            String msg = "Error updating invoice full";
            logger.error(msg, ex);
            GUIManager.showErrorMessage(null, msg, "Error");
        }
    }

    public void anulateInvoice(Invoice invoice) {
        try {
            JDBCInvoiceDAO invoiceDAO = (JDBCInvoiceDAO) DAOFactory.getInstance().getInvoiceDAO();
            invoiceDAO.updateInvoice(invoice);
        } catch (DAOException ex) {
            String msg = "Error updating invoice";
            logger.error(msg, ex);
            GUIManager.showErrorMessage(null, msg, "Error");
        }
    }

    public Date getPrimerRegistro(String tabla, String field) {
        try {
            JDBCUtilDAO utilDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            return utilDAO.getFirstEntrada(tabla, field);
        } catch (DAOException ex) {
            logger.error("Error getting Salida.", ex);
            GUIManager.showErrorMessage(null, "Error consultando registro", "Error");
            return null;
        }
    }

    public boolean addClient(Client client) {
        try {
            JDBCClientDAO clientDAO = (JDBCClientDAO) DAOFactory.getInstance().getClientDAO();
            clientDAO.addClient(client);
            return true;
        } catch (DAOException ex) {
            String msg = "Error adding client";
            logger.error(msg, ex);
            GUIManager.showErrorMessage(null, msg, "Error");
            return false;
        }
    }

    public boolean updateClient(Client client) {
        try {
            JDBCClientDAO clientDAO = (JDBCClientDAO) DAOFactory.getInstance().getClientDAO();
            clientDAO.updateClient(client);
            return true;
        } catch (DAOException ex) {
            String msg = "Error updating client";
            logger.error(msg, ex);
            GUIManager.showErrorMessage(null, msg, "Error");
            return false;
        }
    }

    public ArrayList<Presentation> getPresentationsByProduct(long idProduct) {
        try {
            JDBCUtilDAO utilDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            return utilDAO.getPresentationsByProduct(idProduct);
        } catch (DAOException ex) {
            logger.error("Error getting Presentations list.", ex);
            return null;
        }
    }

    public ArrayList<Presentation> getAllPresentationsByProduct(long idProduct) {
        try {
            JDBCUtilDAO utilDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            return utilDAO.getAllPresentationsByProduct(idProduct);
        } catch (DAOException ex) {
            logger.error("Error getting Presentations list.", ex);
            return null;
        }
    }

    public Presentation getPresentationsById(long idPres) {
        try {
            JDBCUtilDAO utilDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            return utilDAO.getPresentationByID(idPres);
        } catch (DAOException ex) {
            logger.error("Error getting Presentation.", ex);
            return null;
        }
    }

    public Presentation getPresentationsByDefault(long idProduct) {
        try {
            JDBCUtilDAO utilDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            return utilDAO.getPresentationDefault(idProduct);
        } catch (DAOException ex) {
            logger.error("Error getting Presentations list.", ex);
            return null;
        }
    }

    public boolean addCycle(Cycle cycle) {
        try {
            JDBCUtilDAO utilDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            utilDAO.addCycle(cycle);
            return true;
        } catch (DAOException ex) {
            String msg = "Error adding cycle";
            logger.error(msg, ex);
            GUIManager.showErrorMessage(null, msg, "Error");
            return false;
        }
    }

    public boolean addCycleAndSnapshot(Cycle cycle) {
        try {
            JDBCUtilDAO utilDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            utilDAO.addCycleAndCreateSnapshot(cycle);
            return true;
        } catch (DAOException ex) {
            String msg = "Error adding cycle";
            logger.error(msg, ex);
            GUIManager.showErrorMessage(null, msg, "Error");
            return false;
        }
    }

    public Cycle getCycle(int id) {
        try {
            JDBCUtilDAO utilDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            ArrayList<Cycle> cyclesList = utilDAO.getCyclesList("id=" + id, "");
            if (!cyclesList.isEmpty()) {
                return cyclesList.get(0);
            } else {
                return null;
            }

        } catch (DAOException ex) {
            String msg = "Error getting cycle";
            logger.error(msg, ex);
            GUIManager.showErrorMessage(null, msg, "Error");
            return null;
        }
    }

    public Cycle getLastCycle() {
        try {
            JDBCUtilDAO utilDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            return utilDAO.getLastCycle("","");
        } catch (DAOException ex) {
            String msg = "Error getting cycle";
            logger.error(msg, ex);
            GUIManager.showErrorMessage(null, msg, "Error");
            return null;
        }
    }

    public boolean updateCycle(Cycle cycle) {
        try {
            JDBCUtilDAO utilDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            utilDAO.updateCycle(cycle);
            return true;
        } catch (DAOException ex) {
            String msg = "Error getting cycle";
            logger.error(msg, ex);
            GUIManager.showErrorMessage(null, msg, "Error");
            return false;
        }
    }

    public boolean addOtherProduct(String name, String desc, double price) {
        try {
            JDBCUtilDAO utilDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            utilDAO.addOtherProduct(name, desc, price);
            return true;
        } catch (DAOException ex) {
            String msg = "Error adding other product";
            logger.error(msg, ex);
            GUIManager.showErrorMessage(null, msg, "Error");
            return false;
        }
    }

    public ArrayList<Category> getCategoriesList() {
        try {
            JDBCUtilDAO utilDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            return utilDAO.getCategoriesSorted();
        } catch (DAOException ex) {
            logger.error("Error getting Categories list.", ex);
            return null;
        }
    }

    public Category getCategory(String name) {
        try {
            JDBCUtilDAO utilDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            return utilDAO.getCategory(name);
        } catch (DAOException ex) {
            logger.error("Error getting Categories list.", ex);
            return null;
        }
    }

    public ArrayList<Category> getAllCategoriesList() {
        try {
            JDBCUtilDAO utilDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            return utilDAO.getAllCategories();
        } catch (DAOException ex) {
            logger.error("Error getting Categories list.", ex);
            return null;
        }
    }

    public ArrayList<Object[]> getInvoiceByProductListWhere(String where, String order) {
        try {
            JDBCInvoiceDAO salidaDAO = (JDBCInvoiceDAO) JDBCDAOFactory.getInstance().getInvoiceDAO();
            return salidaDAO.getInvoiceByProductWhere(where, order);
        } catch (DAOException ex) {
            logger.error("Error getting invoice by product list.", ex);
            GUIManager.showErrorMessage(null, "Error consultando lista de facturas por productos", "Error");
            return null;
        }
    }

    public ArrayList<Item> getItemList(String where, String order) {
        try {
            JDBCItemDAO itemDAO = (JDBCItemDAO) DAOFactory.getInstance().getItemDAO();
            return itemDAO.getItemList(where, order);
        } catch (DAOException ex) {
            logger.error("Error getting Item list.", ex);
            return null;
        }
    }

    public ArrayList<Map> getItemSnapshotList(String where, String order) {
        try {
            JDBCUtilDAO utilDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            return utilDAO.getItemSnapshotList(where, order);
        } catch (DAOException ex) {
            logger.error("Error getting Item Snapshot list.", ex);
            return null;
        }
    }

    public Map countItemSnap(long idItem, int EVENT, long idCycle) {
        try {
            JDBCUtilDAO utilDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            return utilDAO.countItemSnapEvents(idItem, EVENT, idCycle);
        } catch (DAOException ex) {
            logger.error("Error getting Item Snapshot count.", ex);
            return null;
        }
    }

    public Map countItemConciliations(long idItem, long idCycle) {
        try {
            JDBCUtilDAO utilDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            return utilDAO.countItemConciliationEvents(idItem, idCycle);
        } catch (DAOException ex) {
            logger.error("Error getting Item Conciliations count.", ex);
            return null;
        }
    }

    public Item getItemWhere(String where) {
        return getItemList(where, "").get(0);
    }

    public boolean addItem(Item item) {
        try {
            JDBCItemDAO itemDAO = (JDBCItemDAO) DAOFactory.getInstance().getItemDAO();
            itemDAO.addItem(item);
            return true;
        } catch (DAOException ex) {
            logger.error("Error adding Item.", ex);
            return false;
        }
    }

    public boolean updateItem(Item item) {
        try {
            JDBCItemDAO itemDAO = (JDBCItemDAO) DAOFactory.getInstance().getItemDAO();
            itemDAO.updateItem(item);
            return true;
        } catch (DAOException ex) {
            logger.error("Error updating Item pres.", ex);
            return false;
        }
    }

    public boolean updateItemPres(Item item) {
        try {
            JDBCItemDAO itemDAO = (JDBCItemDAO) DAOFactory.getInstance().getItemDAO();
            itemDAO.updateItemPres(item);
            return true;
        } catch (DAOException ex) {
            logger.error("Error updating Item pres.", ex);
            return false;
        }
    }

    public boolean updateItemAll(Item item) {
        try {
            JDBCItemDAO itemDAO = (JDBCItemDAO) DAOFactory.getInstance().getItemDAO();
            itemDAO.updateItem(item);
            itemDAO.updateItemPres(item);
            return true;
        } catch (DAOException ex) {
            logger.error("Error updating Item pres.", ex);
            return false;
        }
    }

    public void deleteItem(long id) {
        try {
            JDBCItemDAO itemDAO = (JDBCItemDAO) DAOFactory.getInstance().getItemDAO();
            itemDAO.deleteItem(id);
        } catch (DAOException ex) {
            logger.error("Error deleting item.", ex);
            GUIManager.showErrorMessage(null, "Error eliminando item", "Error");
        }
    }

    public ArrayList<String> getUnitsList(String where, String order) {
        try {
            JDBCUtilDAO utilDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            return utilDAO.getUnitList(where, order);
        } catch (DAOException ex) {
            logger.error("Error getting units list.", ex);
            GUIManager.showErrorMessage(null, "Error consultando lista de unidades", "Error");
            return null;
        }
    }

    public ArrayList<String> getCategoriesList(String where, String order) {
        try {
            JDBCUtilDAO utilDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            return utilDAO.getCategoriesList(where, order);
        } catch (DAOException ex) {
            logger.error("Error getting units list.", ex);
            GUIManager.showErrorMessage(null, "Error consultando lista de unidades", "Error");
            return null;
        }
    }

    public void addUnit(String nombre) {
        try {
            JDBCUtilDAO utilDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            utilDAO.addUnit(nombre);
        } catch (DAOException ex) {
            logger.error("Error adding unit.", ex);
            GUIManager.showErrorMessage(null, "Error agregando unidad", "Error");
        }
    }

    public void deleteUnit(String nombre) {
        try {
            JDBCUtilDAO utilDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            utilDAO.deleteUnit(nombre);
        } catch (DAOException ex) {
            logger.error("Error deleting unit.", ex);
            GUIManager.showErrorMessage(null, "Error eliminando unidad", "Error");
        }
    }

    public void updateUnit(String nombre, String id) {
        try {
            JDBCUtilDAO utilDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            utilDAO.updateUnit(nombre, id);
        } catch (DAOException ex) {
            logger.error("Error updating unit.", ex);
            GUIManager.showErrorMessage(null, "Error updating unidad", "Error");
        }
    }

    public void addCategory(String nombre) {
        try {
            JDBCUtilDAO utilDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            utilDAO.addCategory(nombre);
        } catch (DAOException ex) {
            logger.error("Error adding unit.", ex);
            GUIManager.showErrorMessage(null, "Error agregando unidad", "Error");
        }
    }

    public void updateCategory(String nombre, String id) {
        try {
            JDBCUtilDAO utilDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            utilDAO.updateCategory(nombre, id);
        } catch (DAOException ex) {
            logger.error("Error updating unit.", ex);
            GUIManager.showErrorMessage(null, "Error updating unidad", "Error");
        }
    }

    public void addPresentation(Presentation pres) {
        try {
            JDBCUtilDAO utilDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            utilDAO.addPresentation(pres);
        } catch (DAOException ex) {
            logger.error("Error adding unit.", ex);
            GUIManager.showErrorMessage(null, "Error agregando presentacion", "Error");
        }
    }

    public boolean updatePresentation(Presentation pres) {
        try {
            JDBCUtilDAO utilDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            return utilDAO.updatePresentation(pres);
        } catch (DAOException ex) {
            logger.error("Error updating press.", ex);
            GUIManager.showErrorMessage(null, "Error actualizando presentacion", "Error");
            return false;
        }
    }

    public boolean updatePresentationToDefault(Presentation pres) {
        try {
            JDBCUtilDAO utilDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            return utilDAO.updatePresentationToDefault(pres);
        } catch (DAOException ex) {
            logger.error("Error updating press.", ex);
            GUIManager.showErrorMessage(null, "Error actualizando presentacion", "Error");
            return false;
        }
    }

    public HashMap<Integer, HashMap> checkInventory(int idPres) {
        try {
            JDBCUtilDAO utilDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            return utilDAO.checkInventory(idPres);
        } catch (DAOException ex) {
            logger.error("Error getting data.", ex);
            GUIManager.showErrorMessage(null, "Error getting data", "Error");
            return null;
        }
    }

    public HashMap<Integer, HashMap> checkInventoryProduct(long idProd) {
        try {
            JDBCUtilDAO utilDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            return utilDAO.checkInventoryProduct(idProd);
        } catch (DAOException ex) {
            logger.error("Error getting data.", ex);
            GUIManager.showErrorMessage(null, "Error getting data", "Error");
            return null;
        }
    }

    public void addItemToInventory(long idItem, double quantity) {
        try {
            JDBCUtilDAO utilDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            utilDAO.addInventoryQuantity(idItem, quantity);
        } catch (DAOException ex) {
            logger.error("Error getting data.", ex);
            GUIManager.showErrorMessage(null, "Error getting data", "Error");
        }
    }

//    public ArrayList<Item> getItemsBySql(String query) {
//        try {
//            JDBCItemDAO itemDAO = (JDBCItemDAO) JDBCDAOFactory.getInstance().getItemDAO();
//            return itemDAO.getItemsBy(query);
//        } catch (Exception e) {
//            logger.error("Error getting items list by sql");
//            GUIManager.showErrorMessage(null, "Error consultando lista de items", "Error");
//            return null;
//        }
//    }
    public ArrayList<Location> getLocationList(String where, String order) {
        try {
            JDBCLocationDAO locationDAO = (JDBCLocationDAO) DAOFactory.getInstance().getLocationDAO();
            return locationDAO.getLocationList(where, order);
        } catch (DAOException ex) {
            logger.error("Error getting location list.", ex);
            GUIManager.showErrorMessage(null, "Error consultando lista de locations", "Error");
            return null;
        }
    }

    public ArrayList<Conciliacion> getConciliacionList(String where, String order) {
        try {
            JDBCConciliacionDAO concDao = (JDBCConciliacionDAO) JDBCDAOFactory.getInstance().getConciliacionDAO();
            return concDao.getConciliacionList(where, order);
        } catch (Exception ex) {
            logger.error("Error getting salida by conciliacion list.", ex);
            GUIManager.showErrorMessage(null, "Error consultando lista de conciliaciones", "Error");
            return null;
        }
    }

    public void addInventoryRegister(Item item, int event, double quantity, int idUser) {
        try {
            JDBCUtilDAO utilDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            utilDAO.addInventoryRegister(item, event, quantity, idUser);
        } catch (DAOException ex) {
            logger.error("Error adding event register.", ex);
            GUIManager.showErrorMessage(null, "Error adding event register", "Error");
        }
    }

    public ArrayList<InventoryEvent> getInventoryRegisterList(String where, String order) {
        try {
            JDBCUtilDAO utilDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            return utilDAO.getRegisterEventList(where, order);
        } catch (DAOException ex) {
            logger.error("Error getting event list.", ex);
            GUIManager.showErrorMessage(null, "Error consultando lista de eventos", "Error");
            return null;
        }
    }

    public List<String> getTAGSInventoryList(String where) {
        try {
            JDBCUtilDAO utilDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            return utilDAO.getTagsInventoryList(where);
        } catch (DAOException ex) {
            logger.error("Error getting tags list.", ex);
            GUIManager.showErrorMessage(null, "Error consultando lista de tags", "Error");
            return null;
        }
    }

    public void restoreInventory(List<ProductoPed> listProductoPeds) {
        List<ProductoPed> list = listProductoPeds;

        for (int idx = 0; idx < list.size(); idx++) {
            ProductoPed pPed = list.get(idx);
            HashMap<Integer, HashMap> mData = null;
            if (pPed.hasPresentation()) {
                mData = app.getControl().checkInventory(pPed.getPresentation().getId());
            } else {
                mData = app.getControl().checkInventoryProduct(pPed.getProduct().getId());
            }
            for (Integer key : mData.keySet()) {
                HashMap data = mData.get(key);
                int id = Integer.parseInt(data.get("id").toString());
                double quantity = Double.parseDouble(data.get("quantity").toString());
                app.getControl().addItemToInventory(id, quantity * pPed.getCantidad());
                logger.warn("Update inventory: " + pPed.getProduct().getName() + ":: " + data.get("name") + "-> idItem:" + id + "[" + quantity + "]");
            }

        }
    }

    public void restoreInventory(List<ProductoPed> listProductoPeds, int tipo) {
        List<ProductoPed> list = listProductoPeds;

        for (int idx = 0; idx < list.size(); idx++) {
            ProductoPed pPed = list.get(idx);
            HashMap<Integer, HashMap> mData = null;
            if (pPed.hasPresentation()) {
                mData = app.getControl().checkInventory(pPed.getPresentation().getId());
            } else {
                mData = app.getControl().checkInventoryProduct(pPed.getProduct().getId());
            }
            for (Integer key : mData.keySet()) {
                HashMap data = mData.get(key);
                int id = Integer.parseInt(data.get("id").toString());
                double quantity = Double.parseDouble(data.get("quantity").toString());
                boolean isLocal = tipo == PanelPedido.TIPO_LOCAL;
                boolean isOnlyDel = Boolean.parseBoolean(data.get("onlyDelivery").toString());
                double val = quantity * pPed.getCantidad() * (isLocal && isOnlyDel ? 0 : 1);
                app.getControl().addItemToInventory(id, val);
                logger.debug("Update inventory: " + pPed.getProduct().getName() + ":: " + data.get("name") + "-> idItem:" + id + "[" + val + "]");
            }

        }
    }

    public ArrayList<Object[]> getProductsOutInventoryList(long idProd, long idItem, Date start) {
        return getProductsOutInventoryList(idProd, idItem, start, new Date());
    }

    public ArrayList<Object[]> getProductsOutInventoryList(long idProd, long idItem, Date start, Date end) {
        try {
            JDBCUtilDAO utilDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            return utilDAO.getProductsOutInventory(idProd, idItem, start, end);
        } catch (DAOException ex) {
            logger.error("Error getting product out list.", ex);
            GUIManager.showErrorMessage(null, "Error consultando lista de salida de productos", "Error");
            return null;
        }
    }

    public ArrayList<Object[]> getPresentationsOutInventoryList(long idPres, long idItem, Date start) {
        return getPresentationsOutInventoryList(idPres, idItem, start, new Date());
    }

    public ArrayList<Object[]> getPresentationsOutInventoryList(long idPres, long idItem, Date start, Date end) {
        try {
            JDBCUtilDAO utilDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            return utilDAO.getPresentationOutInventory(idPres, idItem, start, end);
        } catch (DAOException ex) {
            logger.error("Error getting presentation out list.", ex);
            GUIManager.showErrorMessage(null, "Error consultando lista de salida de productos por presentacion", "Error");
            return null;
        }
    }

    public ArrayList<Object[]> getPresentationsByItem(long idItem) {
        try {
            JDBCUtilDAO utilDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            return utilDAO.getPresentationsByItem(idItem);
        } catch (DAOException ex) {
            logger.error("Error getting presentation by item.", ex);
            GUIManager.showErrorMessage(null, "Error consultando lista presentacion por item", "Error");
            return null;
        }
    }

    public List<Double> getRankProductsByVarPriceList(long idItem, int limit) {
        try {
            JDBCUtilDAO utilDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            return utilDAO.getRankProductsByVarPriceList(idItem, limit);
        } catch (DAOException ex) {
            logger.error("Error getting rank of products by price.", ex);
            GUIManager.showErrorMessage(null, "Error consultando rank products by var price", "Error");
            return null;
        }
    }

    public ArrayList<Object[]> getProductsSales(Date start, Date end) {
        try {
            JDBCUtilDAO utilDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            return utilDAO.getProductsSales(start, end);
        } catch (DAOException ex) {
            logger.error("Error getting products sales.", ex);
            GUIManager.showErrorMessage(null, "Error consultando lista productos vendidos", "Error");
            return null;
        }
    }

    public Invoice getLastDelivery(String cel) {
        try {

            String query = "SELECT * FROM invoices WHERE idClient=?? ORDER BY code DESC LIMIT 1";
            query = query.replace("??", cel);

            JDBCInvoiceDAO invoiceDAO = (JDBCInvoiceDAO) DAOFactory.getInstance().getInvoiceDAO();
            ArrayList<Invoice> invoiceByQuery = invoiceDAO.getInvoiceByQuery(query);

            return !invoiceByQuery.isEmpty() ? invoiceByQuery.get(0) : null;
        } catch (DAOException ex) {
            logger.error("Error getting invoice.", ex);
            GUIManager.showErrorMessage(null, "Error consultando factura", "Error");
            return null;
        }
    }

    public ArrayList<Invoice> getInvoicesLiteWhitProduct(String querycomp) {
        try {

            String query = "select i.id, i.code, i.sale_date, i.deliveryType, i.value, "
                    + "i.numDeliverys, i.valueDelivery, i.discount, i.idClient, "
                    + "i.idMesero, i.mesa,i.ciclo, i.notes, i.isservice, i.service_porc, "
                    + "i.status, i.lastUpdatedTime, p.id, p.name "
                    + "from invoices i, invoice_product ip, products p"
                    + " WHERE i.id  = ip.id_invoice AND p.id = ip.id_product ??2"
                    + " GROUP BY i.code"
                    + " ORDER  BY i.sale_date DESC";

//            query = query.replace("??1", (idProd <= 0 ? "" : "AND p.id=" + String.valueOf(idProd)));
            query = query.replace("??2", querycomp.isEmpty() ? "" : "AND " + querycomp);

//            System.out.println("query = " + query);
            JDBCInvoiceDAO invoiceDAO = (JDBCInvoiceDAO) DAOFactory.getInstance().getInvoiceDAO();
            ArrayList<Invoice> invoiceByQuery = invoiceDAO.getInvoiceByQuery(query);

            return invoiceByQuery;

        } catch (Exception ex) {
            logger.error("Error getting invoice.", ex);
            GUIManager.showErrorMessage(null, "Error consultando facturas", "Error");
            return null;

        }
    }

    public ArrayList<CashMov.Category> getExpensesCategoriesList(String where, String orderBy) {
        try {
            JDBCUtilDAO utilDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            return utilDAO.getExpensesCategoriesList(where, orderBy);
        } catch (DAOException ex) {
            logger.error("Error getting Expenses Categories list.", ex);
            return null;
        }
    }

    public void addExpenseCategory(String nombre) {
        try {
            JDBCUtilDAO utilDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            utilDAO.addExpenseCategory(nombre);
        } catch (DAOException ex) {
            logger.error("Error adding unit.", ex);
            GUIManager.showErrorMessage(null, "Error agregando unidad", "Error");
        }
    }

    public void updateExpenseCategory(String nombre, String id) {
        try {
            JDBCUtilDAO utilDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            utilDAO.updateExpensesCategory(nombre, id);
        } catch (DAOException ex) {
            logger.error("Error updating unit.", ex);
            GUIManager.showErrorMessage(null, "Error updating unidad", "Error");
        }
    }

    public void addExpenseIncome(HashMap data) {
        try {
            JDBCUtilDAO utilDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            utilDAO.addExpenseIncome(data);
        } catch (DAOException ex) {
            logger.error("Error adding expense-income.", ex);
            GUIManager.showErrorMessage(null, "Error agregando expense-income", "Error");
        }
    }

    public void deleteExpenseCategory(String nombre) {
        try {
            JDBCUtilDAO utilDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            utilDAO.deleteExpenseCategory(nombre);
        } catch (DAOException ex) {
            logger.error("Error deleting category.", ex);
            GUIManager.showErrorMessage(null, "Error eliminando categoria", "Error");
        }
    }

    public void saveSnapshotData(Cycle cycle) {
        ArrayList<Map> itemSnapList = app.getControl().getItemSnapshotList("cycle_id=" + cycle.getId(), "i.name");
        try {
            JDBCUtilDAO utilDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            for (Map map : itemSnapList) {
                HashMap<String, Double> data = getSnapshotData(map, cycle);
                Long id = Long.parseLong(map.get("id").toString());
                utilDAO.updateSnapshotItem(id, data);
            }
        } catch (DAOException ex) {
            logger.error("Error updating snapshot.", ex);
            GUIManager.showErrorMessage(null, "Error actualizando datos del snapshot", "Error");
        }
    }

    public HashMap<String, Double> getSnapshotData(Map map, Cycle lastCycle) {
        double outs = 0;
        ArrayList<Object[]> presentationsByItem = app.getControl().getPresentationsByItem(Long.valueOf(map.get("item_id").toString()));

        Date end = new Date();
        if (!lastCycle.isOpened()) {
            end = lastCycle.getEnd();
        }

        boolean onlyDelivery = Boolean.parseBoolean(map.get("onlyDelivery").toString()); // item is only delivery
        for (Object[] get : presentationsByItem) {
            long idPres = Long.parseLong(get[0].toString());
            long idProd = Long.parseLong(get[1].toString());
            long idItem = Long.valueOf(map.get("item_id").toString());
            if (idPres == 0) { //producto sin presentacion
                ArrayList<Object[]> productsOutInventory = app.getControl().getProductsOutInventoryList(idProd, idItem, lastCycle.getInit(), end);
                for (int j = 0; j < productsOutInventory.size(); j++) {
                    Object[] data = productsOutInventory.get(j);
                    double quantity = Double.parseDouble(data[2].toString());
                    int delType = Integer.parseInt(data[3].toString());
                    outs += quantity * (onlyDelivery && delType == PanelPedido.TIPO_LOCAL ? 0 : 1.0); // excluir locales solo para llevar
                }
            } else {
                ArrayList<Object[]> presentationsOutInventory = app.getControl().getPresentationsOutInventoryList(idPres, idItem, lastCycle.getInit(), end);
                for (int j = 0; j < presentationsOutInventory.size(); j++) {
                    Object[] data = presentationsOutInventory.get(j);
                    double quantity = Double.parseDouble(data[3].toString());
                    int delType = Integer.parseInt(data[4].toString());
                    outs += quantity * (onlyDelivery && delType == PanelPedido.TIPO_LOCAL ? 0 : 1.0); // excluir locales solo para llevar
                }
            }
        }

        Map countIn = app.getControl().countItemSnap(Long.valueOf(map.get("item_id").toString()), 1, lastCycle.getId());
        Map countOut = app.getControl().countItemSnap(Long.valueOf(map.get("item_id").toString()), 2, lastCycle.getId());
        Map countConc = app.getControl().countItemConciliations(Long.valueOf(map.get("item_id").toString()), lastCycle.getId());

        double quantity = Double.parseDouble(map.get("quantity").toString());
        double sIns = Double.parseDouble(countIn.get("sum").toString());
        double sOuts = Double.parseDouble(countOut.get("sum").toString());
        double sConc = Double.parseDouble(countConc.get("sum").toString());
        double res = quantity + sIns - sOuts - outs + sConc;
        double real = Double.parseDouble(map.get("real").toString());

        HashMap<String, Double> data = new HashMap<>();
        data.put("quantity", quantity);
        data.put("income", sIns);
        data.put("outcome", sOuts);
        data.put("conciliation", sConc);
        data.put("sales", outs);
        data.put("result", res);
        data.put("real", real);

        return data;
    }

    public long addOrder(Order order) {
        try {
            JDBCOrderDAO orderDAO = (JDBCOrderDAO) DAOFactory.getInstance().getOrderDAO();
            return orderDAO.addOrder(order);
        } catch (DAOException ex) {
            logger.error("Error adding order.", ex);
            GUIManager.showErrorMessage(null, "Error agregando orden", "Error");
        }
        return 0;
    }

    public long addProductOrder(long idOrder, List<ProductoPed> products) {
        try {
            JDBCOrderDAO orderDAO = (JDBCOrderDAO) DAOFactory.getInstance().getOrderDAO();
            return orderDAO.addProductsOrder(idOrder, products);
        } catch (DAOException ex) {
            logger.error("Error adding products order.", ex);
            GUIManager.showErrorMessage(null, "Error agregando products orden", "Error");
        }
        return 0;
    }

    public List<Order> getOrderslList(String where, String order) {
        try {
            JDBCOrderDAO orderList = (JDBCOrderDAO) DAOFactory.getInstance().getOrderDAO();
            return orderList.getOrderList(where, order);
        } catch (DAOException ex) {
            logger.error("Error getting Order list.", ex);
            return null;
        }
    }

    public List<ProductoPed> getOrderProducts(long id) {
        try {
            JDBCOrderDAO orderList = (JDBCOrderDAO) DAOFactory.getInstance().getOrderDAO();
            return orderList.getOrderProducts(id);
        } catch (DAOException ex) {
            logger.error("Error getting Order products.", ex);
            return null;
        }
    }

    public String getProductStations(long idProduct) {
        try {
            JDBCUtilDAO utilList = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            return utilList.getProductStations(idProduct);
        } catch (DAOException ex) {
            logger.error("Error getting stations by product.", ex);
            return null;
        }
    }

    public String getStationByID(int idStation) {
        try {
            JDBCUtilDAO utilList = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            return utilList.getStationByID(idStation);
        } catch (DAOException ex) {
            logger.error("Error getting stations by product.", ex);
            return null;
        }
    }

    public List<Station> getStationsList() {
        try {
            JDBCUtilDAO utilList = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            return utilList.getStationsList("","");
        } catch (DAOException ex) {
            logger.error("Error getting stations list.", ex);
            return null;
        }
    }

    public int countUninvoicedProducts(long idOrder) {
        try {
            JDBCUtilDAO utilDao = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            return utilDao.countUninvoicedProducts(idOrder);
        } catch (Exception e) {
            logger.error("Error counting uninvoiced products.", e);
            return -1;
        }
    }

}
