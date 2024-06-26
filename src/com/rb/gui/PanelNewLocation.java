/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rb.gui;


import com.rb.Aplication;
import com.rb.domain.Location;
import com.rb.persistence.JDBC.JDBCDAOFactory;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import org.dz.MyDefaultTableModel;
import org.dz.PanelCapturaMod;
import org.dz.UpperCaseTextField;

/**
 *
 * @author LuisR
 */
public class PanelNewLocation extends PanelCapturaMod implements ActionListener {

    private final Aplication app;
    private MyDefaultTableModel modelo;

    public static final String AC_ADD_LOCATION = "AC_ADD_LOCATION";

    /**
     * Creates new form PnNewLocation
     * @param app
     */
    public PanelNewLocation(Aplication app) {
        this.app = app;
        initComponents();
        createComponents();
    }

    private void createComponents() {
        btAdd.setActionCommand(AC_ADD_LOCATION);
        btAdd.addActionListener(this);
        btSalir.setActionCommand(Aplication.ACTION_CANCEL_PANEL);
        btSalir.addActionListener(this);

        String[] cols = {"N°", "Nombre", "Direccion", "Punto Venta"};
        modelo = new MyDefaultTableModel(cols, 0);
        tableLocations.setModel(modelo);
        tableLocations.setRowHeight(24);
//        tableLocations.setFont(Aplication.DEFAULT_FONT_TF);
        loadLocations();

    }

    @Override
    public void reset() {
        tfName.setText("");
        tfName.setBorder(bordeNormal);
        tfAddress.setText("");
    }

    private Location checkLocation() {

        Location location = null;
        boolean valido = true;
        if (tfName.getText().trim().isEmpty()) {
            tfName.setBorder(bordeError);
            valido = false;
        }
        if (valido) {
            location = new Location();
            location.setName(tfName.getText().trim());
            location.setAddress(tfAddress.getText().trim());
            location.setSalePoint(chPuntoVnta.isSelected());
        }
        return location;
    }

    private void loadLocations() {
        ArrayList<Location> locationList = app.getControl().getLocationList("", "id");
        modelo.setRowCount(0);
        for (int i = 0; i < locationList.size(); i++) {
            Location loc = locationList.get(i);
            modelo.addRow(new Object[]{loc.getId(), loc.getName(), loc.getAddress(), loc.isSalePoint()?"SI":"NO"});
            modelo.setRowEditable(modelo.getRowCount() - 1, false);
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

        jLabel1 = new javax.swing.JLabel();
        tfName = new UpperCaseTextField();
        jLabel2 = new javax.swing.JLabel();
        tfAddress = new UpperCaseTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableLocations = new javax.swing.JTable();
        btAdd = new javax.swing.JButton();
        btSalir = new javax.swing.JButton();
        chPuntoVnta = new javax.swing.JCheckBox();

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("Nombre:");

        tfName.setNextFocusableComponent(tfAddress);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Direccion:");

        tfAddress.setNextFocusableComponent(chPuntoVnta);

        tableLocations.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(tableLocations);

        btAdd.setText("Agregar");
        btAdd.setNextFocusableComponent(btSalir);

        btSalir.setText("Salir");

        chPuntoVnta.setText("Punto de venta");
        chPuntoVnta.setNextFocusableComponent(btAdd);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jScrollPane1)
                .addGap(10, 10, 10))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btSalir)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(tfName, javax.swing.GroupLayout.PREFERRED_SIZE, 359, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(tfAddress)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(chPuntoVnta)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btAdd)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel2});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(tfName, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(tfAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chPuntoVnta)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(btAdd, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btSalir)
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {tfAddress, tfName});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel1, jLabel2});

    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btAdd;
    private javax.swing.JButton btSalir;
    private javax.swing.JCheckBox chPuntoVnta;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tableLocations;
    private javax.swing.JTextField tfAddress;
    private javax.swing.JTextField tfName;
    // End of variables declaration//GEN-END:variables

    @Override
    public void actionPerformed(ActionEvent e) {
        if (Aplication.ACTION_CANCEL_PANEL.equals(e.getActionCommand())) {
            cancelPanel();
        } else if (AC_ADD_LOCATION.equals(e.getActionCommand())) {
            Location loc = checkLocation();
            if (loc != null) {
                try {
                    JDBCDAOFactory.getInstance().getLocationDAO().addLocation(loc);
                    pcs.firePropertyChange(AC_ADD_LOCATION, null, 1);                    
                } catch (Exception ex) {
                    app.getGuiManager().showError(ex.getMessage());
                }
                modelo.addRow(new Object[]{modelo.getRowCount() + 1, loc.getName(), loc.getAddress(),loc.isSalePoint()?"SI":"NO"});
                modelo.setRowEditable(modelo.getRowCount() - 1, false);
                reset();
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
