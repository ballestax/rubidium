/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rb.gui;

import com.rb.gui.util.MyPopupListener;
import com.rb.Aplication;
import com.rb.GUIManager;
import com.rb.domain.Permission;
import com.rb.domain.Rol;
import com.rb.domain.User;
import com.rb.persistence.JDBC.JDBCDAOFactory;
import com.rb.persistence.JDBC.JDBCUserDAO;
import com.rb.persistence.JDBC.JDBCUtilDAO;
import com.rb.persistence.dao.DAOException;
import com.rb.persistence.dao.DAOFactory;
import com.rb.persistence.dao.RemoteUserResultsInterface;
import com.rb.persistence.dao.UserRetrieveException;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import static javax.swing.BorderFactory.createLineBorder;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import org.dz.MyDefaultTableModel;
import org.dz.MyTableCellRenderer;


/**
 *
 * @author ballestax
 */
public class PanelAdminUsers extends javax.swing.JPanel implements ActionListener, PropertyChangeListener {

    private final Aplication app;
    private MyDefaultTableModel model;
    public static final String AC_MOD_USER = "AC_MOD_USER";
    public static final String AC_MOD_ROL = "AC_MOD_ROL";
    private JButton btNewUser;
    private JPopupMenu popupTable;
    private MyPopupListener popupListenerTabla, popupListenerTabla2;
    private JButton btUpdate;
    private JButton btNewRol;
    private MyDefaultTableModel model2;
    private JPopupMenu popupTable2;

    /**
     * Creates new form PanelAdminUsers
     *
     * @param app
     */
    public PanelAdminUsers(Aplication app) {
        this.app = app;
        initComponents();
        createComponents();
    }

    private void createComponents() {
        toolbar.setFloatable(false);
        toolbar2.setFloatable(false);

        btNewUser = new JButton("Nuevo Usuario");
        btNewUser.setActionCommand("ACTION_NEW_USER");
        btNewUser.addActionListener(this);

        btUpdate = new JButton("Actualizar");
        btUpdate.setActionCommand("ACTION_UPDATE");
        btUpdate.addActionListener(this);

        btNewRol = new JButton("Nuevo Rol");
        btNewRol.setActionCommand("ACTION_NEW_ROL");
        btNewRol.addActionListener(this);

        toolbar.add(btNewUser);
        toolbar.add(btUpdate);

        toolbar2.add(btNewRol);

        String[] colNames = {"N°", "Nombre", "Rol", "Modificar"};
        model = new MyDefaultTableModel(colNames, 0);
        tableUsers.setModel(model);
        tableUsers.setRowHeight(25);

        int[] colW = {20, 100, 70, 50};
        for (int i = 0; i < colW.length; i++) {
            tableUsers.getColumnModel().getColumn(i).setMinWidth(colW[i]);
            tableUsers.getColumnModel().getColumn(i).setPreferredWidth(colW[i]);
            tableUsers.getColumnModel().getColumn(i).setCellRenderer(new MyTableCellRenderer(true));
        }
        tableUsers.getColumnModel().getColumn(model.getColumnCount() - 1).setCellEditor(new BotonEditor(tableUsers, this, AC_MOD_USER));
        tableUsers.getColumnModel().getColumn(model.getColumnCount() - 1).setCellRenderer(new ButtonCellRenderer("Modificar"));

        popupTable = new JPopupMenu();
        popupListenerTabla = new MyPopupListener(popupTable, true);
        JMenuItem item1 = new JMenuItem("Eliminar");
        item1.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int r = tableUsers.getSelectedRow();
                try {
                    String username = tableUsers.getValueAt(r, 1).toString();
                    RemoteUserResultsInterface user = ((JDBCUserDAO) JDBCDAOFactory.getInstance().getUserDAO()).retrieveUsers("username='" + username + "'", "");
                    User get = user.getItems(0, 1).get(0);
                    if (!get.getAccessLevel().equals(User.AccessLevel.ADMIN)) {
                        String msg = "Se va a eliminar el usuario: " + get.getUsername() + "\n"
                                + "Desea continuar?";
                        int opc = JOptionPane.showConfirmDialog(null, msg, "Advertencia", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                        if (opc == JOptionPane.OK_OPTION) {
                            ((JDBCUserDAO) JDBCDAOFactory.getInstance().getUserDAO()).deleteUser(username);
                            loadUsers();
                        }
                    } else {
                        GUIManager.showErrorMessage(null, "No se puede eliminar el usuario administrador", "Error");
                    }
                } catch (Exception ex) {
                    GUIManager.showErrorMessage(null, "Error al intentar borrar el usuario", "Eliminar usuario");
                }

            }
        });
        popupTable.add(item1);
        tableUsers.addMouseListener(popupListenerTabla);

        loadUsers();

        colNames = new String[]{"N°", "Rol", "Permisos", "Modificar"};
        model2 = new MyDefaultTableModel(colNames, 0);
        tableRoles.setModel(model2);
        tableRoles.setRowHeight(25);

        colW = new int[]{20, 100, 70, 50};
        for (int i = 0; i < colW.length; i++) {
            tableRoles.getColumnModel().getColumn(i).setMinWidth(colW[i]);
            tableRoles.getColumnModel().getColumn(i).setPreferredWidth(colW[i]);
            tableRoles.getColumnModel().getColumn(i).setCellRenderer(new MyTableCellRenderer(true));
        }
        tableRoles.getColumnModel().getColumn(model2.getColumnCount() - 1).setCellEditor(new BotonEditor(tableRoles, this, AC_MOD_ROL));
        tableRoles.getColumnModel().getColumn(model2.getColumnCount() - 1).setCellRenderer(new ButtonCellRenderer("Modificar"));

        popupTable2 = new JPopupMenu();
        popupListenerTabla2 = new MyPopupListener(popupTable2, true);
        JMenuItem item2 = new JMenuItem("Eliminar");
        item2.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int r = tableRoles.getSelectedRow();
                try {
                    String rolName = tableRoles.getValueAt(r, 1).toString();
                    Rol role = ((JDBCUtilDAO) JDBCDAOFactory.getInstance().getUtilDAO()).getRole(rolName);
                    String msg = "Se va a eliminar el rol: " + role.getDisplayName() + " (" + role.getName() + ")\n"
                            + "Desea continuar?";
                    int opc = JOptionPane.showConfirmDialog(null, msg, "Advertencia", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                    if (opc == JOptionPane.OK_OPTION) {
                        ((JDBCUtilDAO) JDBCDAOFactory.getInstance().getUtilDAO()).deleteRole(rolName);
                        loadRoles();
                    }
                } catch (Exception ex) {
                    GUIManager.showErrorMessage(null, "Error al intentar borrar el rol", "Eliminar rol");
                }
            }
        });
        popupTable2.add(item2);
        tableRoles.addMouseListener(popupListenerTabla2);

        loadRoles();

    }

    private void mostrarCambioDePass(String name) {
        app.getGuiManager().showModPassword(name, this);
    }

    private void mostrarUpdateRol(String name) {
        Rol rol = app.getControl().getRol(name.toLowerCase());
        app.getGuiManager().showNewRol(this, rol);
    }

    private void loadUsers() {
        try {
            RemoteUserResultsInterface rUsers = ((JDBCUserDAO) DAOFactory.getInstance().getUserDAO()).retrieveUsers();
            List<User> items = rUsers.getItems(0, 30);
//            List<User> items = ((JDBCUserDAO) DAOFactory.getInstance().getUserDAO()).getItems(1, 30);
////            User user = app.getUser();
//            System.err.println("users:"+items.size());
            model.setRowCount(0);
            for (int i = 0; i < items.size(); i++) {
                User user = items.get(i);                
                model.addRow(new Object[]{
                    user.getId(),
                    user.getUsername(),
                    app.getControl().getUseRole(user.getId()),
//                    user.getAccessLevel().toString(),
                    
                    true
                });
                model.setRowEditable(model.getRowCount() - 1, false);
                model.setCellEditable(model.getRowCount() - 1, 3, true);
            }

        } catch (RemoteException ex) {
            System.err.println(ex.getMessage());
        } catch (DAOException ex) {
            System.err.println(ex.getMessage());
        } catch (UserRetrieveException ex) {
            System.err.println(ex.getMessage());
        }
    }

    private void loadRoles() {
        try {
            ArrayList<Rol> roles = ((JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO()).getRolesList();

            model2.setRowCount(0);
            for (int i = 0; i < roles.size(); i++) {
                Rol rol = roles.get(i);
                model2.addRow(new Object[]{
                    rol.getId(),
                    rol.getName(),
                    rol.getDisplayName(),
                    true
                });
                model2.setRowEditable(model2.getRowCount() - 1, false);
                model2.setCellEditable(model2.getRowCount() - 1, 3, true);
            }

        } catch (DAOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        toolbar = new javax.swing.JToolBar();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableUsers = new javax.swing.JTable();
        toolbar2 = new javax.swing.JToolBar();
        jScrollPane2 = new javax.swing.JScrollPane();
        tableRoles = new javax.swing.JTable();

        toolbar.setRollover(true);

        tableUsers.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(tableUsers);

        toolbar2.setRollover(true);

        tableRoles.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane2.setViewportView(tableRoles);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(toolbar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(toolbar2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 531, Short.MAX_VALUE)
                        .addGap(13, 13, 13))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(toolbar, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(toolbar2, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tableRoles;
    private javax.swing.JTable tableUsers;
    private javax.swing.JToolBar toolbar;
    private javax.swing.JToolBar toolbar2;
    // End of variables declaration//GEN-END:variables

    @Override
    public void actionPerformed(ActionEvent e) {
        if (ACTION_NEW_USER.equals(e.getActionCommand())) {
            app.getGuiManager().showNewUser(this);
        } else if (ACTION_UPDATE.equals(e.getActionCommand())) {
            loadUsers();
            loadRoles();
        } else if (ACTION_NEW_ROL.equals(e.getActionCommand())) {
            app.getGuiManager().showNewRol(this, null);
        }
    }
    public static final String ACTION_NEW_ROL = "ACTION_NEW_ROL";
    public static final String ACTION_UPDATE = "ACTION_UPDATE";
    public static final String ACTION_NEW_USER = "ACTION_NEW_USER";

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(PanelChangePassword.AC_CHANGE_PASS)) {
            if (evt.getPropertyName() != null) {
                char[] pass = (char[]) evt.getNewValue();
                String user = evt.getOldValue().toString();
                try {
                    RemoteUserResultsInterface rUsers = ((JDBCUserDAO) DAOFactory.getInstance().getUserDAO()).retrieveUsers("username='" + user + "'", "");
                    List<User> items = rUsers.getItems(0, 1);
                    ((JDBCUserDAO) JDBCDAOFactory.getInstance().getUserDAO()).updateUser(items.get(0).getId(), null, String.valueOf(pass), null);
                } catch (Exception e) {
                    GUIManager.showErrorMessage(null, "Error al intentar cambiar la contraseña", "Error");
                }
            }
        } else if (evt.getPropertyName().equals(PanelNewUser.AC_NEW_USER)) {
            String username = ((Object[]) evt.getOldValue())[0].toString();
            char[] pass = (char[]) ((Object[]) evt.getOldValue())[1];
            Rol rol = (Rol) evt.getNewValue();
            try {
                ((JDBCUserDAO) DAOFactory.getInstance().getUserDAO()).addUser(username, String.valueOf(pass));
                User user = app.getControl().getUser(username);
                if (user != null && rol != null) {
                    ((JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO()).assignRoleToUser(user, rol);
                }
                loadUsers();

            } catch (DAOException e) {
                GUIManager.showErrorMessage(null, "Error al intentar crear el usuario", "Error");
            }
        } else if (evt.getPropertyName().equals(PanelNewRol.AC_NEW_ROL)) {
            ArrayList<Permission> permissions = (ArrayList<Permission>) evt.getNewValue();
            Rol role = (Rol) evt.getOldValue();

//            System.out.println(Arrays.toString(permissions.toArray()));

            try {
                ((JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO()).addRole(role);
                Rol rol = ((JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO()).getRole(role.getName());
                ((JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO()).addPermissionRole(rol, permissions);
                loadRoles();

            } catch (Exception e) {
                GUIManager.showErrorMessage(null, "Error al intentar crear el rol", "Error");
            }
        } else if (evt.getPropertyName().equals(PanelNewRol.AC_UPDATE_ROL)) {
            ArrayList<Permission> perms = (ArrayList<Permission>) evt.getNewValue();
            Rol role = (Rol) evt.getOldValue();
            try {
                ((JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO()).updateRol(role);
                ((JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO()).deletePermissionsByRole(role.getId());
                ((JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO()).addPermissionRole(role, perms);
                loadRoles();

            } catch (Exception e) {
                GUIManager.showErrorMessage(null, "Error al intentar actualizar el rol", "Error");
            }
        }
    }

    public class ButtonCellRenderer extends JButton implements TableCellRenderer {

        public ButtonCellRenderer(String text) {
            setText(text);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

            if (isSelected) {
                setForeground(Color.black);
                setBackground(table.getSelectionBackground());
                if (hasFocus) {
                    setBorder(BorderFactory.createLineBorder(Color.darkGray));
                } else {
                    setBorder(createLineBorder(Color.lightGray));
                }
            } else {
                setBackground(table.getBackground());
                setForeground(Color.black);
                setBorder(UIManager.getBorder("Table.cellBorder"));
            }
            return this;
        }
    }

    public class BotonEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {

        private JTextField campo;
        Boolean currentValue;
        JButton button;
        protected static final String EDIT = "edit";
        private JTable tabla;
        private ActionListener acList;
        private String acCommand;

        public BotonEditor(JTable tabla, ActionListener listener, String acCommand) {
            button = new JButton();
            button.setBorderPainted(false);
            this.tabla = tabla;
            this.acList = listener;
            this.acCommand = acCommand;
            button.setActionCommand(acCommand);
            button.addActionListener(BotonEditor.this);
        }

        @Override
        public boolean isCellEditable(EventObject e) {
            if (e instanceof MouseEvent) {
                return ((MouseEvent) e).getClickCount() >= 1;
            }
            return true;
        }

        @Override
        public Object getCellEditorValue() {
            return currentValue;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            currentValue = (Boolean) value;
            return button;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            final int c = tabla.getEditingColumn();
            final int f = tabla.getEditingRow();
            if (f != -1 && c != -1) {
                int row = tabla.convertRowIndexToModel(f);
                String name = tabla.getModel().getValueAt(row, 1).toString();
                if (AC_MOD_USER.equals(e.getActionCommand())) {
                    mostrarCambioDePass(name);
                } else if (AC_MOD_ROL.equals(e.getActionCommand())) {
                    mostrarUpdateRol(name);
                }
            }
            try {
                fireEditingStopped();
            } catch (Exception ex) {
                System.err.println(ex.getMessage());
            }
        }
    }

}
