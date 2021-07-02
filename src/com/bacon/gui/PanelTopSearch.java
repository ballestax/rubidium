/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bacon.gui;

import com.bacon.MyConstants;
import com.bacon.Aplication;
import com.bacon.Configuration;
import com.bacon.domain.Product;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.dz.PanelCaptura;

/**
 *
 * @author lrod
 */
public class PanelTopSearch extends PanelCaptura implements ActionListener {

    private final Aplication app;
    public static final String AC_CLEAR_FIELD = "AC_CLEAR_FIELD";

    /**
     * Creates new form PanelTopSearch
     *
     * @param app
     */
    public PanelTopSearch(Aplication app) {
        this.app = app;
        initComponents();

        Font font1 = new Font("Sans", 1, 14);

        btBuscar.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "cancel.png", 18, 18)));
        btBuscar.setActionCommand(AC_CLEAR_FIELD);
        btBuscar.addActionListener(this);

        btCustomProduct.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "add1.png", 18, 18)));
        btCustomProduct.setActionCommand(AC_SEND_PIN);
        btCustomProduct.addActionListener(this);
//        btCustomProduct.setEnabled(false);

        regSearch.setFontCampo(font1);

        regSearch.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                filtrar(regSearch.getText().toUpperCase(), MyConstants.FILTER_TEXT_INT_CONTAINS);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filtrar(regSearch.getText().toUpperCase(), MyConstants.FILTER_TEXT_INT_CONTAINS);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filtrar(regSearch.getText().toUpperCase(), MyConstants.FILTER_TEXT_INT_CONTAINS);
            }
        });

        btView1.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "packing1.png", 20, 20)));
        btView1.setActionCommand(AC_SELECT_VIEW1);
        btView1.addActionListener(this);

//        btView1.setForeground(colorLocal);
        btView2.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "packing2.png", 20, 20)));
        btView2.setActionCommand(AC_SELECT_VIEW2);
        btView2.addActionListener(this);
//        btView2.setForeground(colorDelivery);

        btView2.setSelected(true);

        btRefresh.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "update.png", 20, 20)));
        btRefresh.setActionCommand(AC_REFRESH_PRODUCTS);
        btRefresh.addActionListener(this);

        lbSort.setText("");
        lbSort.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "view-filter.png", 20, 20)));

        DefaultComboBoxModel model = new DefaultComboBoxModel();
        model.addElement("----");
        model.addElement(PanelCategory.ORDEN_ALPHA);
        model.addElement(PanelCategory.ORDEN_PRICE);
//        model.addElement(PanelCategory.ORDEN_RATING); 

        String prop = app.getConfiguration().getProperty(Configuration.PROD_ORDER, "----");

        model.setSelectedItem(prop);

        cbSort.setModel(model);
        cbSort.setActionCommand(AC_CHANGE_SORT);
        cbSort.addActionListener(this);

//        cbSort.setEnabled(false);
//        lbSort.setEnabled(false);
    }
    private static final String AC_SEND_PIN = "AC_SEND_PIN";
    public static final String AC_REFRESH_PRODUCTS = "AC_REFRESH_PRODUCTS";
    public static final String AC_CHANGE_SORT = "AC_CHANGE_SORT";
    public static final String AC_SELECT_VIEW2 = "AC_SELECT_VIEW2";
    public static final String AC_SELECT_VIEW1 = "AC_SELECT_VIEW1";
    public static final String AC_ADD_CUSTOM_PRODUCT = "AC_ADD_CUSTOM_PRODUCT";

    private void filtrar(String text, int filter) {
        if (text.trim().length() > 2) {
            String SCAPE = "LIKE \'%" + text.toLowerCase() + "%\'";
            ArrayList<Product> productsList = app.getControl().getProductsList("name " + SCAPE + " or category " + SCAPE, "");
//            ArrayList<Product> productsList = app.getControl().getProductsList("name " + SCAPE);
            pcs.firePropertyChange(AC_FILTER_PRODUCTS, null, productsList);
        } else {
            pcs.firePropertyChange(AC_FILTER_PRODUCTS, null, null);
        }

    }
    public static final String AC_FILTER_PRODUCTS = "AC_FILTER_PRODUCTS";

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        regSearch = new com.bacon.gui.util.Registro(BoxLayout.X_AXIS, "Buscar", "");
        btBuscar = new javax.swing.JButton();
        btCustomProduct = new javax.swing.JButton();
        btView2 = new javax.swing.JToggleButton();
        btView1 = new javax.swing.JToggleButton();
        cbSort = new javax.swing.JComboBox<>();
        lbSort = new javax.swing.JLabel();
        btRefresh = new javax.swing.JButton();

        setBorder(javax.swing.BorderFactory.createEtchedBorder());

        buttonGroup1.add(btView2);

        buttonGroup1.add(btView1);

        lbSort.setText("jLabel1");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(regSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(btBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 54, Short.MAX_VALUE)
                .addComponent(lbSort)
                .addGap(3, 3, 3)
                .addComponent(cbSort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btCustomProduct, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btView1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(btView2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(cbSort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btCustomProduct, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btView1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btView2, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(regSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbSort)
                    .addComponent(btRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btBuscar, btCustomProduct, btView1, btView2, cbSort, lbSort, regSearch});

    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btBuscar;
    private javax.swing.JButton btCustomProduct;
    private javax.swing.JButton btRefresh;
    private javax.swing.JToggleButton btView1;
    private javax.swing.JToggleButton btView2;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox<String> cbSort;
    private javax.swing.JLabel lbSort;
    private com.bacon.gui.util.Registro regSearch;
    // End of variables declaration//GEN-END:variables

    @Override
    public void reset() {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (AC_CLEAR_FIELD.equals(e.getActionCommand())) {
            regSearch.setText("");
            regSearch.getComponent().requestFocus();

        } else if (AC_SELECT_VIEW1.equals(e.getActionCommand())) {
            pcs.firePropertyChange(AC_SELECT_VIEW1, null, null);

        } else if (AC_SELECT_VIEW2.equals(e.getActionCommand())) {
            pcs.firePropertyChange(AC_SELECT_VIEW2, null, null);
        } else if (AC_ADD_CUSTOM_PRODUCT.equals(e.getActionCommand())) {

            PanelPedido panelPedido = app.getGuiManager().getPanelPedido();
            app.getGuiManager().showPanelAddOtherProduct(panelPedido);
        } else if (AC_CHANGE_SORT.equals(e.getActionCommand())) {
            app.getConfiguration().setProperty(Configuration.PROD_ORDER, cbSort.getSelectedItem().toString(), true);
            pcs.firePropertyChange(AC_CHANGE_SORT, null, cbSort.getSelectedItem());
        } else if (AC_REFRESH_PRODUCTS.equals(e.getActionCommand())) {
            pcs.firePropertyChange(AC_REFRESH_PRODUCTS, null, null);
        } else if (AC_SEND_PIN.equals(e.getActionCommand())) {
            String propPrinter = app.getConfiguration().getProperty(Configuration.PRINTER_SELECTED);
            app.getPrinterService().sendPulsePin(propPrinter);
        }

    }

}
