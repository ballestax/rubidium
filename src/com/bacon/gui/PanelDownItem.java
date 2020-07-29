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
import org.balx.TextFormato;
import org.bx.gui.MyDefaultTableModel;
import org.bx.gui.PanelCaptura;

/**
 *
 * @author ballestax
 */
public class PanelDownItem extends PanelCaptura implements ActionListener, CaretListener, ListSelectionListener, PropertyChangeListener {

    private final Aplication app;
    private String selectedCategory;
    private boolean filtroActivo;
    private MyDefaultTableModel modelo;
    private String nota;

    public static final String ACTION_UPDATE_LIST = "ACTION_UPDATE_LIST";
    public static final String ACTION_NEW_PRODUCT = "ACTION_NEW_PRODUCT";

    /**
     * Creates new form GuiSelProduct
     *
     * @param app
     * @param pcl
     */
    public PanelDownItem(Aplication app, PropertyChangeListener pcl) {
        this.app = app;
        initComponents();
        createComponents();
        addPropertyChangeListener(pcl);
    }

    private void createComponents() {

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

        DefaultListSelectionModel selectionModel = new DefaultListSelectionModel();
        selectionModel.addListSelectionListener(this);
        tabla.setSelectionModel(selectionModel);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setFont(new Font("Tahoma", 0, 15));

        org.balx.TextFormato docF = new TextFormato();
        tfCantidad.setDocument(docF.getLimitadorNumeros());
        tfCantidad.addCaretListener(new Caret());
        tfCantidad.addCaretListener(this);

//        tfNota.setDocument(TextFormatter.getDoubleLimiter());
//        tfNota.addCaretListener(new Caret());
//        tfNota.addCaretListener(this);
        lbTitle.setText("<html><font color=blue size=+1>Descargar de inventario</font></html>");

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

        btUpdate.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "update.png", 18, 18)));
        btUpdate.setActionCommand(ACTION_UPDATE_LIST);
        btUpdate.addActionListener(this);
//        btUpdate.setText("Actualizar");

        tfFilter.requestFocusInWindow();

    }

    public void updateListaProducts() {
        populateModel(app.getControl().getItemList("", ""));
    }

    private void filtrar() {
        String text = tfFilter.getText();
        ArrayList<Item> listItems = app.getControl().getItemList("", "");
//        }
        if (filtroActivo) {
            ArrayList<Item> listFiltered = new ArrayList();
            for (int i = 0; i < listItems.size(); i++) {
                if (listItems.get(i).getName().toUpperCase().contains(text.toUpperCase())) {
                    System.out.println(listItems.get(i).getName());
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

    public Item getItem() {
        int row = tabla.getSelectedRow();
        boolean valido = true;
        if (row == -1) {
            tabla.setBorder(bordeError);
            valido = false;
        }
        if (tfCantidad.getText().trim().isEmpty()) {
            tfCantidad.setBorder(bordeError);
            valido = false;
        }
        
        Item item = null;
        if (valido) {
            item = app.getControl().getItemWhere("id=" + modelo.getValueAt(row, 0));
            try {
                item.setQuantity(Integer.parseInt(tfCantidad.getText()));
//                item.setPrice(new BigDecimal(tfValorUnit.getText()));
                nota = tfNota.getText();
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
        tfNota = new javax.swing.JTextField();
        tfCantidad = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        lbTitle = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        tbFiltrar = new javax.swing.JToggleButton();
        tfFilter = new javax.swing.JTextField();
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

        lbResumen.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel2.setText("Cantidad:");

        jLabel6.setText("Nota");

        lbTitle.setText("jLabel3");
        lbTitle.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbResumen, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(5, 5, 5)
                        .addComponent(tfCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfNota)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addComponent(lbTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbResumen, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(tfCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(tfNota, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {tfCantidad, tfNota});

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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(tfFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbFiltrar)
                    .addComponent(btClear, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {tbFiltrar, tfFilter});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 679, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 204, Short.MAX_VALUE)
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
    private javax.swing.JButton btUpdate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbResumen;
    private javax.swing.JLabel lbTitle;
    private javax.swing.JTable tabla;
    private javax.swing.JToggleButton tbFiltrar;
    private javax.swing.JTextField tfCantidad;
    private javax.swing.JTextField tfFilter;
    private javax.swing.JTextField tfNota;
    // End of variables declaration//GEN-END:variables

    @Override
    public void reset() {
        tfCantidad.setText("");
        tfFilter.setText("");
//        cbCategory.setSelectedIndex(0);
        tabla.setBorder(bordeNormal);
        tfFilter.requestFocus();
        populateModel(app.getControl().getItemList("", ""));
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(ACTION_NEW_PRODUCT)) {
            populateModel(app.getControl().getItemList("", ""));
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (tabla.getSelectedRow() != -1) {
            tabla.scrollRectToVisible(tabla.getCellRect(tabla.getSelectedRow(), 0, false));
            tabla.setBorder(bordeNormal);
            showResumen();
        } else {
            lbResumen.setText("<html>Item:</html>");
        }
    }

    public void showResumen() {
        int row = tabla.getSelectedRow();
        Item item = app.getControl().getItemWhere("id=" + tabla.getValueAt(row, 0));
        double quantity = item.getQuantity();
        if (row != -1) {
            double cantidad = 0;
            boolean pass = true;
            try {
                cantidad = Double.parseDouble(tfCantidad.getText());
                pass = quantity >= cantidad;
            } catch (Exception e) {
            }
            lbResumen.setText("<html><table cellspacing=1 border=0>"
                    + "<tr><td>Item: </td><td colspan=5><font color=blue size=+1>" + tabla.getValueAt(row, 1).toString().toUpperCase() + "</td></tr>"
                    + "<tr><td>Existencias: </td><td><font color=blue size=+1>" + quantity + "</font></td></tr>"
                    + "<tr><td>Cantidad: </td><td><font color=" + (pass ? "blue" : "red") + " size=+1>" + cantidad + "</font></td></tr></table></html>");
        }
    }

    @Override
    public void caretUpdate(CaretEvent e) {
        if (e.getSource().equals(tfCantidad)) {
            showResumen();
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
            Item item = getItem();
            if (item != null) {
                app.getControl().addItemToInventory(item.getId(), item.getQuantity() * -1);
                app.getControl().addInventoryRegister(item, 2, item.getQuantity());
                pcs.firePropertyChange(AC_ADD_ITEM_TO_TABLE, null, item);
                getRootPane().getParent().setVisible(false);
            }
        }
    }
}
