/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bacon.gui;

import com.bacon.Aplication;
import com.bacon.domain.Item;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import javax.swing.DefaultListSelectionModel;
import javax.swing.ImageIcon;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.dz.MyDefaultTableModel;
import org.dz.PanelCaptura;
import org.dz.TextFormatter;

/**
 *
 * @author ballestax
 */
public class PanelSelItem extends PanelCaptura implements ActionListener, CaretListener, ListSelectionListener, PropertyChangeListener {

    private final Aplication app;
    private String selectedCategory;
    private boolean filtroActivo;
    private MyDefaultTableModel modelo;
    private Item selectedItem;
    private boolean onlyOneItem;

    public static final String ACTION_UPDATE_LIST = "ACTION_UPDATE_LIST";
    public static final String ACTION_NEW_PRODUCT = "ACTION_NEW_PRODUCT";
    private DefaultListSelectionModel selectionModel;

    /**
     * Creates new form GuiSelProduct
     *
     * @param app
     * @param pcl
     */
    public PanelSelItem(Aplication app, PropertyChangeListener pcl) {
        this.app = app;
        initComponents();
        createComponents();
        addPropertyChangeListener(pcl);
    }

    private void createComponents() {

        selectedItem = null;
        onlyOneItem = false;

        String[] columnNames = {"ID", "Nombre", "Cantidad"};
        modelo = new MyDefaultTableModel(columnNames, 0);
        tabla.setModel(modelo);
        tabla.setRowHeight(25);

        TablaCellRenderer tRenderer = new TablaCellRenderer(false, 0, app.getDCFORM_W());

        int[] colW = new int[]{20, 200, 30};
        for (int i = 0; i < colW.length; i++) {
            tabla.getColumnModel().getColumn(i).setMinWidth(colW[i]);
            tabla.getColumnModel().getColumn(i).setPreferredWidth(colW[i]);
        }
        tabla.getColumnModel().getColumn(2).setCellRenderer(tRenderer);

        selectionModel = new DefaultListSelectionModel();
        selectionModel.addListSelectionListener(this);
        tabla.setSelectionModel(selectionModel);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setFont(new Font("Tahoma", 0, 15));

        
        tfCantidad.setDocument(TextFormatter.getDoubleLimiter());
        tfCantidad.addCaretListener(new Caret());
        tfCantidad.addCaretListener(this);
        tfValorUnit.setDocument(TextFormatter.getDoubleLimiter());
        tfValorUnit.addCaretListener(new Caret());
        tfValorUnit.addCaretListener(this);
        tfValorTotal.setDocument(TextFormatter.getDoubleLimiter());
        tfValorTotal.addCaretListener(new Caret());
        tfValorTotal.addCaretListener(this);

        btCancel.setActionCommand(Aplication.ACTION_CANCEL_PANEL);
        btCancel.addActionListener(app);

        tfFilter.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                filtrar();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filtrar();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filtrar();
            }
        });

        btClear.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "delete-icon.png", 18, 18)));
        btClear.setActionCommand(PanelListPedidos.ACTION_CLEAR_SEARCH);
        btClear.addActionListener(this);

        btAccept.setActionCommand(AC_ADD_ITEM_TO_TABLE);
        btAccept.addActionListener(this);

        filtroActivo = true;
        tbFiltrar.setEnabled(false);
        tbFiltrar.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                filtroActivo = tbFiltrar.isSelected();
                tfFilter.setEnabled(filtroActivo);
                filtrar();
            }
        });

        lbResumen.setOpaque(true);
        lbResumen.setBackground(new java.awt.Color(225, 226, 237));

        btNewProducto.setActionCommand(ACTION_NEW_PRODUCT);
        btNewProducto.addActionListener(this);
        btNewProducto.setText("");

        btUpdate.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "update.png", 18, 18)));
        btUpdate.setActionCommand(ACTION_UPDATE_LIST);
        btUpdate.addActionListener(this);
//        btUpdate.setText("Actualizar");

        tfFilter.requestFocusInWindow();

    }

    public void updateListaProducts() {
        populateModel(app.getControl().getItemList("", "name"));
    }

    private void filtrar() {
        String text = tfFilter.getText();
        ArrayList<Item> listItems = app.getControl().getItemList("", "name");
//        }
        if (filtroActivo) {
            ArrayList<Item> listFiltered = new ArrayList();
            for (int i = 0; i < listItems.size(); i++) {
                if (listItems.get(i).getName().toUpperCase().contains(text.toUpperCase())) {
                    listFiltered.add(listItems.get(i));
                }
            }
            populateModel(listFiltered);
            return;
        }
        populateModel(listItems);
    }

    private void populateModel(ArrayList<Item> items) {
        modelo.setRowCount(0);
        SwingWorker sw = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                for (int i = 0; i < items.size(); i++) {
                    Item item = items.get(i);
                    modelo.addRow(new Object[]{item.getId(), item.getName(), item.getQuantity()});
                    modelo.setRowEditable(modelo.getRowCount() - 1, false);
                }
                return true;
            }
        };
        sw.execute();

    }

    public void setItem(Item item) {
        selectedItem = item;
        if (item != null) {
            onlyOneItem = true;
            enableOnlyOneItem(true);
            showResumen(selectedItem);
            tfCantidad.requestFocus();
        }
    }

    private void enableOnlyOneItem(boolean enable) {
        jScrollPane1.setVisible(!enable);
        jPanel2.setVisible(!enable);
        btUpdate.setEnabled(!enable);

    }

    public Item getSelectedItem() {
        boolean valido = true;
        if (tfCantidad.getText().trim().isEmpty()
                || Integer.parseInt(tfCantidad.getText()) < 1) {
            tfCantidad.setBorder(bordeError);
            valido = false;
        }
        if (tfValorUnit.getText().trim().isEmpty()) {
            tfValorUnit.setBorder(bordeError);
            valido = false;
        }
        if (tfValorTotal.getText().trim().isEmpty()) {
            tfValorTotal.setBorder(bordeError);
            valido = false;
        }
        if (valido && selectedItem != null) {
            try {
                selectedItem.setQuantity(Integer.parseInt(tfCantidad.getText()));
            } catch (Exception e) {
            }
            return selectedItem;
        }
        return null;
    }

    public Item getItem() {
        int row = tabla.getSelectedRow();
        boolean valido = true;
        if (row == -1) {
            tabla.setBorder(bordeError);
            valido = false;
        }
        if (tfCantidad.getText().trim().isEmpty()
                || Integer.parseInt(tfCantidad.getText()) < 1) {
            tfCantidad.setBorder(bordeError);
            valido = false;
        }
        if (tfValorUnit.getText().trim().isEmpty()) {
            tfValorUnit.setBorder(bordeError);
            valido = false;
        }
        if (tfValorTotal.getText().trim().isEmpty()) {
            tfValorTotal.setBorder(bordeError);
            valido = false;
        }
        Item item = null;
        if (valido) {
            item = app.getControl().getItemWhere("id=" + modelo.getValueAt(row, 0));
            try {
                item.setQuantity(Integer.parseInt(tfCantidad.getText()));
//                item.setPrice(new BigDecimal(tfValorUnit.getText()));
            } catch (NumberFormatException e) {
            }
        }
        return item;
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
        btAccept = new javax.swing.JButton();
        btCancel = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabla = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        lbResumen = new javax.swing.JLabel();
        tfValorUnit = new javax.swing.JTextField();
        tfValorTotal = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        tfCantidad = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        tbFiltrar = new javax.swing.JToggleButton();
        tfFilter = new javax.swing.JTextField();
        btNewProducto = new javax.swing.JButton();
        btClear = new javax.swing.JButton();
        btUpdate = new javax.swing.JButton();

        jLabel1.setBackground(new java.awt.Color(185, 204, 204));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Productos");
        jLabel1.setOpaque(true);

        btAccept.setText("Aceptar");

        btCancel.setText("Cancelar");

        tabla.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(tabla);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lbResumen.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Resumen", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        jLabel5.setText("Valor Unitario:");

        jLabel2.setText("Cantidad:");

        jLabel6.setText("Valor Total:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lbResumen, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(5, 5, 5)
                        .addComponent(tfCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfValorUnit, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfValorTotal)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(tfCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(tfValorUnit, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(tfValorTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lbResumen, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {tfCantidad, tfValorTotal, tfValorUnit});

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        tbFiltrar.setText("Filtrar:");
        tbFiltrar.setFocusable(false);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tbFiltrar, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tfFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(btClear, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btNewProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(tfFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbFiltrar)
                    .addComponent(btNewProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btClear, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btNewProducto, tbFiltrar, tfFilter});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 679, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addContainerGap())))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btCancel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btAccept)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btUpdate, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 229, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btAccept, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btUpdate, jLabel1});

    }// </editor-fold>//GEN-END:initComponents
    public static final String AC_ADD_ITEM_TO_TABLE = "AC_ADD_ITEM_TO_TABLE";

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btAccept;
    private javax.swing.JButton btCancel;
    private javax.swing.JButton btClear;
    private javax.swing.JButton btNewProducto;
    private javax.swing.JButton btUpdate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbResumen;
    private javax.swing.JTable tabla;
    private javax.swing.JToggleButton tbFiltrar;
    private javax.swing.JTextField tfCantidad;
    private javax.swing.JTextField tfFilter;
    private javax.swing.JTextField tfValorTotal;
    private javax.swing.JTextField tfValorUnit;
    // End of variables declaration//GEN-END:variables

    @Override
    public void reset() {
        tfCantidad.setText("");
        tfValorUnit.setText("");
        tfFilter.setText("");
//        cbCategory.setSelectedIndex(0);
        tabla.setBorder(bordeNormal);
        tfFilter.requestFocus();
        populateModel(app.getControl().getItemList("", "name"));
        onlyOneItem = false;
        enableOnlyOneItem(false);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(ACTION_NEW_PRODUCT)) {
            populateModel(app.getControl().getItemList("", ""));
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        int row = tabla.getSelectedRow();
        if (row != -1) {
            tabla.scrollRectToVisible(tabla.getCellRect(tabla.getSelectedRow(), 0, false));
            tabla.setBorder(bordeNormal);
            selectedItem = app.getControl().getItemWhere("id=" + modelo.getValueAt(row, 0));
            showResumen(selectedItem);
        } else {
            lbResumen.setText("<html>Item:</html>");
        }
    }

    public void showResumen(Item item) {
        if (item != null) {
            tfValorUnit.setText(item.getCost().toString());
            updateResumen();
        }
    }

    private void updateResumen() {
        double cantidad = 0, precio = 0, total = 0;
        String suma = "";
        try {
            cantidad = Double.parseDouble(tfCantidad.getText());
            precio = Double.parseDouble(tfValorUnit.getText());
            total = cantidad * precio;
            double res = selectedItem.getQuantity() + cantidad;
            suma = String.valueOf(selectedItem.getQuantity()) + "<font color=red>+</font>" + cantidad + "<font color=red>=</font><font color=#2a9d8f>" + res + "</font>";
        } catch (Exception e) {
            if(selectedItem!=null)
            suma = "<font color=#2a9d8f>" + selectedItem.getQuantity() + "</font>";
        }
        lbResumen.setText("<html><table cellspacing=1 border=0>"
                + "<tr><td>Producto: </td><td colspan=5><font color=blue size=+1>" + selectedItem.getName().toUpperCase() + ""
                + " <font color=red size=+1>(</font>" + suma + "<font color=red size=+1>)</font></tr>"
                + "<tr><td>Cantidad: </td><td><font color=blue size=+1>" + cantidad + "</font></td>"
                + "<td>Precio:</td><td><font color=blue size=+1>" + app.getDCFORM_P().format(precio) + "</font></td>"
                + "<td>Total:</td><td><font color=blue size=+1>" + app.getDCFORM_P().format(total) + "</font></td></tr></table></html>");

    }

    @Override
    public void caretUpdate(CaretEvent e) {
        if (e.getSource().equals(tfCantidad)) {
            double cantidad = 0, precio = 0, total = 0;
            try {
                cantidad = Double.parseDouble(tfCantidad.getText());
                precio = Double.parseDouble(tfValorUnit.getText());
                total = cantidad * precio;
                if (cantidad==0){
                    tfValorUnit.setText(String.valueOf(selectedItem.getPrice()));
                }
                tfValorTotal.setText(app.getDCFORM_W().format(total));
            } catch (Exception ex) {

            }
            updateResumen();
        } else if (e.getSource().equals(tfValorUnit)) {
            double cantidad = 0, precio = 0, total = 0;
            try {
                cantidad = Double.parseDouble(tfCantidad.getText());
                precio = Double.parseDouble(tfValorUnit.getText());
                total = cantidad * precio;
                if (Double.isNaN(Double.parseDouble(tfValorUnit.getText()))) {
                    tfValorUnit.setText(getSelectedItem().getPrice().toString());
                }
                tfValorTotal.setText(app.getDCFORM_W().format(total));
            } catch (Exception ex) {
                tfValorTotal.setText("");
            }
            updateResumen();
        } else if (e.getSource().equals(tfValorTotal)) {
            double cantidad = 0, precio = 0, total = 0;
            try {
                cantidad = Double.parseDouble(tfCantidad.getText());
                total = Double.parseDouble(tfValorTotal.getText());
                precio = selectedItem.getPrice().doubleValue();
                if (cantidad != 0) {
                    precio = total / cantidad;
                }
                tfValorUnit.setText(app.getDCFORM_W().format(precio));
            } catch (Exception ex) {
                tfValorUnit.setText("");
            }
            updateResumen();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (ACTION_UPDATE_LIST.equals(e.getActionCommand())) {
            updateListaProducts();
            if (filtroActivo) {
                filtrar();
            }
        } else if (PanelListPedidos.ACTION_CLEAR_SEARCH.equals(e.getActionCommand())) {
            tfFilter.setText("");
        } else if (AC_ADD_ITEM_TO_TABLE.equals(e.getActionCommand())) {
            Item item = onlyOneItem ? getSelectedItem() : getItem();
            if (item != null) {
                app.getControl().addItemToInventory(item.getId(), item.getQuantity());
                app.getControl().addInventoryRegister(item, 1, item.getQuantity());
                pcs.firePropertyChange(AC_ADD_ITEM_TO_TABLE, null, item);
                getRootPane().getParent().setVisible(false);
            }
        }
    }
}
