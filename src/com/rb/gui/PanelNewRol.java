/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rb.gui;

import com.rb.gui.util.TableSelectCellRenderer;
import com.rb.Aplication;
import com.rb.Utiles;
import com.rb.domain.Permission;
import com.rb.domain.Rol;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import org.dz.ListSelection;
import org.dz.MyDefaultTableModel;
import org.dz.PanelCaptura;


/**
 *
 * @author LUIS
 */
public class PanelNewRol extends PanelCaptura implements ActionListener, TableModelListener {

    private final Aplication app;

    public static final String AC_NEW_ROL = "AC_NEW_ROL";
    private static final String USERNAME_PATTERN = "^[a-z0-9_-]{3,15}$";
    private String title;
    private Pattern pattern;
    private Matcher matcher;
    private Rol role;

    private MyDefaultTableModel modelo;
    private JTable tabla;
    private ListSelection listaSeleccion;

    /**
     * Creates new form PanelModPassword
     */
    public PanelNewRol(Aplication app, Rol role) {
        this.app = app;
        this.role = role;
        initComponents();
        createComponents();
    }

    public PanelNewRol(Aplication app) {
        this(app, null);
    }

    private void createComponents() {
        btCancel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                getRootPane().getParent().setVisible(false);
            }
        });

        setTitle("");

        String[] colNames = {"Sel", "ID", "Permiso"};

        tabla = new JTable();

        modelo = new MyDefaultTableModel(colNames, 0);
        tabla.setModel(modelo);
        modelo.addTableModelListener(this);
        tabla.setRowHeight(24);
        tabla.setFont(new Font("Tahoma", 0, 14));

        int[] colW = {5, 5, 150};

        for (int i = 0; i < tabla.getColumnCount(); i++) {
            tabla.getColumnModel().getColumn(i).setCellRenderer(new TableSelectCellRenderer(true));
            tabla.getColumnModel().getColumn(i).setMinWidth(colW[i]);
            tabla.getColumnModel().getColumn(i).setPreferredWidth(colW[i]);
        }
        tabla.getTableHeader().setReorderingAllowed(false);
        listaSeleccion = new ListSelection(tabla);
        tabla.getTableHeader().addMouseListener(listaSeleccion);

        tabla.getColumnModel().getColumn(0).setHeaderRenderer(listaSeleccion);
        tabla.getColumnModel().getColumn(0).setCellEditor(tabla.getDefaultEditor(Boolean.class));

        scPermisos.getViewport().add(tabla);
        btAcept.setActionCommand(AC_NEW_ROL);
        btAcept.addActionListener(this);

        pattern = Pattern.compile(USERNAME_PATTERN);

        if (role != null) {
            tfRolName.setText(role.getName());
            tfRol.setText(role.getDisplayName());
            tfRolDescription.setText(role.getDescription());
        }

        ArrayList<Permission> permissionByRole = role != null ? app.getControl().getPermissionByRole(role) : null;
        
        loadPermissions(permissionByRole);
    }

    public int[] getSelectedsRows() {
        int[] sel = new int[modelo.getRowCount()];
        Arrays.fill(sel, -1);
        for (int i = 0; i < modelo.getRowCount(); i++) {
            if ((Boolean) modelo.getValueAt(i, 0) == true) {
                sel[i] = Integer.valueOf(modelo.getValueAt(i, 1).toString());
            }
        }
        sel = Utiles.truncar(sel, 0, Integer.MAX_VALUE);
        Arrays.sort(sel);
        return sel;
    }

    public ArrayList<Permission> getPermissions() {
        int[] selectedsRows = getSelectedsRows();
        ArrayList<Permission> permissionList = new ArrayList<>();
        for (int selectedsRow : selectedsRows) {
            permissionList = app.getControl().getPermissionList(selectedsRows);
        }
        return permissionList;
    }

    private void updateTabla() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                tabla.updateUI();
            }
        });
    }

    public boolean validate(final String username) {
        matcher = pattern.matcher(username);
        return matcher.matches();
    }

    public void setTitle(String title) {
        this.title = title;
        jLabel1.setText("Crear un nuevo rol y asignar permisos");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        btAcept = new javax.swing.JButton();
        btCancel = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        tfRolName = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tfRolDescription = new javax.swing.JTextArea();
        tfRol = new javax.swing.JTextField();
        scPermisos = new javax.swing.JScrollPane();

        jLabel1.setBackground(java.awt.Color.lightGray);
        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel1.setOpaque(true);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Rol:");

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Descripcion:");

        btAcept.setText("Aceptar");

        btCancel.setText("Cancelar");

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("Nombre:");

        tfRolName.setNextFocusableComponent(tfRol);

        tfRolDescription.setColumns(20);
        tfRolDescription.setRows(5);
        jScrollPane1.setViewportView(tfRolDescription);

        tfRol.setNextFocusableComponent(tfRolDescription);

        scPermisos.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEtchedBorder(), javax.swing.BorderFactory.createTitledBorder("Permisos")));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addGap(22, 22, 22)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane1)
                            .addComponent(tfRolName)
                            .addComponent(tfRol, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(scPermisos, javax.swing.GroupLayout.DEFAULT_SIZE, 426, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btAcept, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel2, jLabel3, jLabel4});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(tfRolName, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(tfRol, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(scPermisos, javax.swing.GroupLayout.PREFERRED_SIZE, 331, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btAcept, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btAcept;
    private javax.swing.JButton btCancel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane scPermisos;
    private javax.swing.JTextField tfRol;
    private javax.swing.JTextArea tfRolDescription;
    private javax.swing.JTextField tfRolName;
    // End of variables declaration//GEN-END:variables

    @Override
    public void reset() {
        tfRol.setBorder(bordeNormal);
        tfRolName.setBorder(bordeNormal);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (AC_NEW_ROL.equals(e.getActionCommand())) {
            reset();
            Rol rol = parsearCampos();
            if (role != null) {
                rol.setId(role.getId());
            }
            getPermissions();
            if (rol != null) {
                if (role != null) {
                    pcs.firePropertyChange(AC_UPDATE_ROL, rol, getPermissions());
                } else {
                    pcs.firePropertyChange(AC_NEW_ROL, rol, getPermissions());
                }
            }
            getRootPane().getParent().setVisible(false);
        }
    }

    public static final String AC_UPDATE_ROL = "AC_UPDATE_ROL";

    private Rol parsearCampos() {
        Rol rol = null;
        boolean valido = true;
        int LU = tfRolName.getText().length();
        if (tfRolName.getText().trim().isEmpty()) {
            tfRolName.setBorder(bordeError);
            valido = false;
        } else if (LU < 4) {
            JOptionPane.showMessageDialog(null, "Nombre de usuario muy corto. (Minimo 4 letras)");
            valido = false;
        } else if (!validate(tfRolName.getText())) {
            JOptionPane.showMessageDialog(null, "Nombre de usuario no valido");
            valido = false;
        }
        if (tfRol.getText().isEmpty()) {
            tfRol.setBorder(bordeError);
            valido = false;
        }

        if (valido) {
            rol = new Rol();
            rol.setName(tfRolName.getText().trim());
            rol.setDisplayName(tfRol.getText().trim());
            rol.setDescription(tfRolDescription.getText().trim());
        }
        return rol;
    }

    private void loadPermissions(ArrayList<Permission> permissionByRole) {
        SwingWorker sw = new SwingWorker<Object, Object[]>() {

            @Override
            protected Object doInBackground() throws Exception {
                ArrayList<Permission> permissionList = app.getControl().getPermissionList();
                for (int i = 0; i < permissionList.size(); i++) {
                    Permission perm = permissionList.get(i);
                    boolean val = false;
                    if (permissionByRole != null) {
                        for (int j = 0; j < permissionByRole.size(); j++) {
                            if (perm.getName().equals(permissionByRole.get(j).getName())) {
                                val = true;
                                break;
                            }
                        }
                    }
                    publish(new Object[]{perm, val});
                }
                return true;
            }

            @Override
            protected void process(List<Object[]> chunks) {
                for (int i = 0; i < chunks.size(); i++) {
                    Object[] data = chunks.get(i);
                    Permission perm = (Permission) data[0];

                    modelo.addRow(new Object[]{
                        Boolean.valueOf(String.valueOf(data[1])),
                        perm.getId(),
                        perm.getDisplayName()
                    });
                }
            }
        };
        sw.execute();
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        if (e.getSource().equals(modelo)) {
            if (e.getType() == TableModelEvent.UPDATE) {

            }
        }
        updateTabla();
    }

}
