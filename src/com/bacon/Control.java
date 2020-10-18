/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.  
 */
package com.bacon;

import com.bacon.domain.Additional;
import com.bacon.domain.Category;
import com.bacon.domain.Client;
import com.bacon.domain.Conciliacion;
import com.bacon.domain.ConfigDB;
import com.bacon.domain.Cycle;
import com.bacon.domain.Ingredient;
import com.bacon.domain.InventoryEvent;
import com.bacon.domain.Invoice;
import com.bacon.domain.Item;
import com.bacon.domain.Location;
import com.bacon.domain.Permission;
import com.bacon.domain.Presentation;
import com.bacon.domain.Product;
import com.bacon.domain.ProductoPed;
import com.bacon.domain.Rol;
import com.bacon.domain.Table;
import com.bacon.domain.User;
import com.bacon.domain.Waiter;
import com.bacon.persistence.JDBC.JDBCAdditionalDAO;
import com.bacon.persistence.JDBC.JDBCClientDAO;
import com.bacon.persistence.JDBC.JDBCConciliacionDAO;
import com.bacon.persistence.JDBC.JDBCConfigDAO;
import com.bacon.persistence.JDBC.JDBCDAOFactory;
import com.bacon.persistence.JDBC.JDBCIngredientDAO;
import com.bacon.persistence.JDBC.JDBCInvoiceDAO;
import com.bacon.persistence.JDBC.JDBCItemDAO;
import com.bacon.persistence.JDBC.JDBCLocationDAO;
import com.bacon.persistence.JDBC.JDBCProductDAO;
import com.bacon.persistence.JDBC.JDBCUserDAO;
import com.bacon.persistence.JDBC.JDBCUtilDAO;
import com.bacon.persistence.dao.DAOException;
import com.bacon.persistence.dao.DAOFactory;
import com.bacon.persistence.dao.RemoteUserResultsInterface;
import com.bacon.persistence.dao.UserRetrieveException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import org.apache.log4j.Logger;

/**
 *
 * @author ballestax
 */
public class Control {

    private Aplication app;
    public static final Logger logger = Logger.getLogger(Control.class.getCanonicalName());

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

    public ConfigDB getConfig(String clave) {
        try {
            JDBCConfigDAO configDAO = (JDBCConfigDAO) DAOFactory.getInstance().getConfigDAO();
            return configDAO.getConfigDB(clave);
        } catch (Exception e) {
            logger.error("Error getting config.", e);
            return null;
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

    public boolean existConfig(String code) {
        try {
            JDBCConfigDAO configDAO = (JDBCConfigDAO) DAOFactory.getInstance().getConfigDAO();
            return configDAO.existConfig(code) >= 1;
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

    public int getMaxIDTabla(String tabla) {
        try {
            JDBCUtilDAO utilDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            return utilDAO.getMaxID(tabla);
        } catch (Exception e) {
            logger.error("Error getting maxID for: " + tabla, e);
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
            return utilDAO.getPermissionList("1");
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
            java.util.logging.Logger.getLogger(Control.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UserRetrieveException ex) {
            java.util.logging.Logger.getLogger(Control.class.getName()).log(Level.SEVERE, null, ex);
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
//        if (perm == null) {
//            return false;
//        }
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

    public ArrayList<Waiter> getWaiterslList(String where, String order) {
        try {
            JDBCUtilDAO addDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            return addDAO.getWaitersList(where, order);
        } catch (DAOException ex) {
            logger.error("Error getting Waiters list.", ex);
            return null;
        }
    }

    public Waiter getWaitersByID(int id) {
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

    public ArrayList<Invoice> getInvoiceslList(String where, String order) {
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

    public Cycle getCycle(int id) {
        try {
            JDBCUtilDAO utilDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            return utilDAO.getCyclesList("id=" + id, "").get(0);
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
            return utilDAO.getCyclesList("", "init DESC").get(0);
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

    public ArrayList<Category> getCategorieslList() {
        try {
            JDBCUtilDAO utilDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            return utilDAO.getCategoriesSorted();
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

    public void addInventoryRegister(Item item, int event, double quantity) {
        try {
            JDBCUtilDAO utilDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            utilDAO.addInventoryRegister(item, event, quantity);
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

    public void restoreInventory(List<ProductoPed> listProductoPeds) {
        List<ProductoPed> list = listProductoPeds;
        for (int idx = 0; idx < list.size(); idx++) {
            ProductoPed pPed = list.get(idx);
            Presentation pres = pPed.getPresentation();
            HashMap<Integer, HashMap> mData = app.getControl().checkInventory(pres.getId());
            for (Integer key : mData.keySet()) {
                HashMap data = mData.get(key);
                int id = Integer.parseInt(data.get("id").toString());
                double quantity = Double.parseDouble(data.get("quantity").toString());
                app.getControl().addItemToInventory(id, quantity * pPed.getCantidad());
                logger.debug("Update inventory: " + pPed.getProduct().getName() + ":: " + data.get("name") + "-> idItem:" + id + "[" + quantity + "]");
            }
        }
    }

    public ArrayList<Object[]> getProductsOutInventoryList(long idProd, Date start) {
        try {
            JDBCUtilDAO utilDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            return utilDAO.getProductsOutInventory(idProd, start);
        } catch (DAOException ex) {
            logger.error("Error getting product out list.", ex);
            GUIManager.showErrorMessage(null, "Error consultando lista de salida de productos", "Error");
            return null;
        }
    }

    public ArrayList<Object[]> getPresentationsOutInventoryList(long idPres, long idItem, Date start) {
        try {
            JDBCUtilDAO utilDAO = (JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO();
            return utilDAO.getPresentationOutInventory(idPres, idItem, start);
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
            return invoiceByQuery.get(0);
        } catch (DAOException ex) {
            logger.error("Error getting invoice.", ex);
            GUIManager.showErrorMessage(null, "Error consultando factura", "Error");
            return null;
        }

    }
    

}
