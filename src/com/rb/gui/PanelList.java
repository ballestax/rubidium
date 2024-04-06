/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rb.gui;

import com.rb.Aplication;
import com.rb.GUIManager;
import com.rb.ProgAction;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import org.dz.MyDefaultTableModel;
import org.dz.PanelCapturaMod;

/**
 *
 * @author LuisR
 */
public class PanelList<T> extends PanelCapturaMod implements ListSelectionListener, TableModelListener {

    private final Aplication app;
    private ArrayList<ProgAction> actions;
    private MyDefaultTableModel modelo;
    private ProgAction acSelect, acAdd, acEdit, acDelete;
    private final String title;
    public static final String AC_SELECTED = "AC_SELECTED";
    public static final String AC_ADD = "AC_ADD";
    public static final String AC_EDIT = "AC_EDIT";
    public static final String AC_DELETE = "AC_DELETE";
    private List lista;

    /**
     * Creates new form PanelList
     *
     * @param app
     * @param title
     * @param listener
     * @param lista
     */
    public PanelList(Aplication app, String title, PropertyChangeListener listener, List lista) {
        this.app = app;
        this.title = title;
        this.lista = lista;
        initComponents();
        createComponents();
        addPropertyChangeListener(listener);
    }

    private void createComponents() {

        lbTitle.setText(title);
        toolbar.setFloatable(false);

        acSelect = new ProgAction("Seleccionar",
                null, "Seleccionar", 's') {
            @Override
            public void actionPerformed(ActionEvent e) {
                seleccionar();
            }
        };

        acAdd = new ProgAction("Agregar",
                null, "Agregar", 'a') {
            @Override
            public void actionPerformed(ActionEvent e) {
                agregar();
            }
        };
        acEdit = new ProgAction("Editar",
                null, "Editar", 'e') {
            @Override
            public void actionPerformed(ActionEvent e) {
                editar();
            }
        };

        acDelete = new ProgAction("Eliminar",
                null, "Eliminar", 'b') {
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminar();
            }
        };
        acSelect.setEnabled(false);
        acEdit.setEnabled(false);
        acDelete.setEnabled(false);

        toolbar.add(acSelect);
        toolbar.add(acAdd);
        toolbar.add(acEdit);
        toolbar.add(acDelete);

        String colNames[] = {"N°", "Nombre"};
        modelo = new MyDefaultTableModel(colNames, 0);
        modelo.addTableModelListener(this);
        tabla.setModel(modelo);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        DefaultListSelectionModel selectionModel = new DefaultListSelectionModel();
        selectionModel.addListSelectionListener(this);
        tabla.setSelectionModel(selectionModel);

        for (int i = 0; i < lista.size(); i++) {
            Object get = lista.get(i);
            modelo.addRow(new Object[]{modelo.getRowCount() + 1, get.toString()});
            modelo.setRowEditable(modelo.getRowCount() - 1, false);
        }

    }

    public void seleccionar() {
        int selectedRow = tabla.getSelectedRow();
        if (selectedRow != -1) {
            String name = modelo.getValueAt(selectedRow, 1).toString();
            pcs.firePropertyChange(AC_SELECTED, null, name);
            cancelPanel();
        }
    }

    public void agregar() {
        String nombre = JOptionPane.showInputDialog(this, "Ingrese la nueva unidad de medida");
        if (lista.contains(nombre.toUpperCase())) {
            GUIManager.showErrorMessage(tabla, "Ya existe la unidad: " + nombre, "Error");
            return;
        }
        if (nombre != null && !nombre.isEmpty()) {
            pcs.firePropertyChange(AC_ADD, null, nombre);
            modelo.addRow(new Object[]{modelo.getRowCount() + 1, nombre});
            modelo.setRowEditable(modelo.getRowCount() - 1, false);
            lista = new ArrayList(Arrays.asList(modelo.getColumn(1)));
        }
    }

    public void editar() {
        int selectedRow = tabla.getSelectedRow();
        modelo.setCellEditable(selectedRow, 1, true);
        boolean success = tabla.editCellAt(selectedRow, 1);
        if (success) {
            boolean toggle = false;
            boolean extend = false;
            tabla.changeSelection(selectedRow, 1, toggle, extend);
        }
        modelo.setCellEditable(selectedRow, 1, false);
    }

    public void eliminar() {
        int selectedRow = tabla.getSelectedRow();
        String name = modelo.getValueAt(selectedRow, 1).toString();
        modelo.removeRow(selectedRow);
        lista = new ArrayList(Arrays.asList(modelo.getColumn(1)));
        pcs.firePropertyChange(AC_DELETE, null, name);
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
        tabla = new javax.swing.JTable();
        lbTitle = new javax.swing.JLabel();

        toolbar.setRollover(true);

        tabla.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(tabla);

        lbTitle.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(toolbar, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE)
                            .addComponent(lbTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(toolbar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbTitle;
    private javax.swing.JTable tabla;
    private javax.swing.JToolBar toolbar;
    // End of variables declaration//GEN-END:variables

    @Override
    public void reset() {

    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        boolean selected = tabla.getSelectedRow() != -1;
        acSelect.setEnabled(selected);
        acEdit.setEnabled(selected);
        acDelete.setEnabled(selected);
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        int row = tabla.getSelectedRow();
        if (e.getType() == TableModelEvent.UPDATE) {
            String name = lista.get(row).toString();
            row = e.getLastRow();
            String name1 = modelo.getValueAt(row, 1).toString();
            pcs.firePropertyChange(AC_EDIT, name, name1);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        
    }
}
