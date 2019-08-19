/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.  
 */
package com.bacon;

import com.bacon.domain.ConfigDB;
import com.bacon.domain.Permission;
import com.bacon.domain.Rol;
import com.bacon.domain.User;
import com.bacon.persistence.JDBC.JDBCConfigDAO;
import com.bacon.persistence.JDBC.JDBCDAOFactory;
import com.bacon.persistence.JDBC.JDBCUserDAO;
import com.bacon.persistence.JDBC.JDBCUtilDAO;
import com.bacon.persistence.dao.DAOException;
import com.bacon.persistence.dao.DAOFactory;
import com.bacon.persistence.dao.RemoteUserResultsInterface;
import com.bacon.persistence.dao.UserRetrieveException;
import java.rmi.RemoteException;
import java.util.ArrayList;
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

}
