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
import com.rb.domain.Waiter;
import com.rb.persistence.JDBC.JDBCDAOFactory;
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
public class PanelAdminWaiters extends javax.swing.JPanel implements ActionListener, PropertyChangeListener {

    private final Aplication app;
    private MyDefaultTableModel model;
    private JPopupMenu popupTable;
    private MyPopupListener popupListenerTabla;

    public static final String AC_MOD_WAITER = "AC_MOD_WAITER";
 

    /**
     * Creates new form PanelAdminUsers
     *
     * @param app
     */
    public PanelAdminWaiters(Aplication app) {
        this.app = app;
        initComponents();
        createComponents();
    }

    private void createComponents() {
        toolbar.setFloatable(false);
        toolbar2.setFloatable(false);

        
        String[] colNames = {"N°", "Nombre", "Rol", "Modificar"};
        model = new MyDefaultTableModel(colNames, 0);
        tableWaiters.setModel(model);
        tableWaiters.setRowHeight(25);

        int[] colW = {20, 100, 70, 50};
        for (int i = 0; i < colW.length; i++) {
            tableUsers.getColumnModel().getColumn(i).setMinWidth(colW[i]);
            tableUsers.getColumnModel().getColumn(i).setPreferredWidth(colW[i]);
            tableUsers.getColumnModel().getColumn(i).setCellRenderer(new MyTableCellRenderer(true));
        }
        tableUsers.getColumnModel().getColumn(model.getColumnCount() - 1).setCellEditor(new BotonEditor(tableUsers, this, AC_MOD_WAITER));
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

        loadWaiters();

    }

    private void loadWaiters() {
        try {
            ArrayList<Waiter> waiters = ((JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO()).getWaitersList("", "");

            model.setRowCount(0);
            for (int i = 0; i < waiters.size(); i++) {
                Waiter waiter = waiters.get(i);
                model.addRow(new Object[]{
                    waiter.getId(),
                    waiter.getName(),
                    waiter.getStatus(),
                    waiter.getColor(),
                    true
                });
                model.setRowEditable(model.getRowCount() - 1, false);
                model.setCellEditable(model.getRowCount() - 1, 3, true);
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
        if (ACTION_NEW_WAITER.equals(e.getActionCommand())) {
            //TODO New Waiter
        } else if (ACTION_UPDATE_WAITER.equals(e.getActionCommand())) {
            loadWaiters();
        }
    }
    public static final String ACTION_UPDATE_WAITER = "ACTION_UPDATE_WAITER";
    public static final String ACTION_NEW_WAITER = "ACTION_NEW_WAITER";

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(PanelNewUser.AC_NEW_USER)) {
            String username = ((Object[]) evt.getOldValue())[0].toString();
            char[] pass = (char[]) ((Object[]) evt.getOldValue())[1];
            Rol rol = (Rol) evt.getNewValue();
            try {
                ((JDBCUserDAO) DAOFactory.getInstance().getUserDAO()).addUser(username, String.valueOf(pass));
                User user = app.getControl().getUser(username);
                if (user != null && rol != null) {
                    ((JDBCUtilDAO) DAOFactory.getInstance().getUtilDAO()).assignRoleToUser(user, rol);
                }
                loadWaiters();

            } catch (DAOException e) {
                GUIManager.showErrorMessage(null, "Error al intentar crear el usuario", "Error");
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
                // TODO
            }
            try {
                fireEditingStopped();
            } catch (Exception ex) {
                System.err.println(ex.getMessage());
            }
        }
    }

}
