/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bacon.gui;

import com.bacon.domain.Category;
import com.bacon.Aplication;
import com.bacon.domain.Product;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.Box;
import org.apache.log4j.Logger;
import org.dz.PanelCapturaMod;

/**
 *
 * @author lrod
 */
public class PanelCategory extends PanelCapturaMod implements PropertyChangeListener {

    private final Aplication app;
    private Category category;
    private ArrayList<Product> products;
    public static final Logger logger = Logger.getLogger(PanelCategory.class.getCanonicalName());
    private int oldSize;
    private int view;
    private String selectedSort;

    public static final String ORDEN_ID = "ORDEN_ID";
    public static final String ORDEN_ALPHA = "ALFABETICO";
    public static final String ORDEN_PRICE = "PRECIO";
    public static final String ORDEN_RATING = "RATING";

    /**
     * Creates new form PanelCategory
     *
     * @param category
     * @param products
     * @param app
     */
    public PanelCategory(Category category, ArrayList products, Aplication app) {
        this.app = app;
        pcs = new PropertyChangeSupport(this);
        this.category = category;
        this.products = products;
        this.selectedSort = "1";
        initComponents();
        createComponents();
    }

    private void createComponents() {
        lbTitle.setText(category.getName());
        lbTitle.setOpaque(true);
        lbTitle.setBorder(BorderFactory.createEtchedBorder());
        lbTitle.setBackground(new Color(84, 36, 0, 130));

        lbTitle.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }
        });

        lbTitle.setVisible(false);

        showView2();

        oldSize = products.size();
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
        if (products == null) {
            pnItems.removeAll();
            return;
        }
        if (view == 1) {
            showView1();
        } else {
            showView2();
        }
    }

    public void showView2() {
        view = 2;
        app.getGuiManager().setWaitCursor();
        pnItems.removeAll();
        pnItems.setLayout(new GridBagLayout());

        if (products != null) {
            int COLS = 3;
            int LX = products.size() / COLS;
            int LY = (int) Math.ceil(products.size() / COLS);
            int c = 0;
            int i = 0, j = 0;
            for (i = 0; i <= LY; i++) {
                for (j = 0; j < COLS; j++) {
                    if (c >= products.size()) {
                        break;
                    }
                    Product prod = products.get(c);
                    PanelProduct pnProd = new PanelProduct(app, prod);
//                    pnProd.addPropertyChangeListener(this);
                    pnProd.addPropertyChangeListener(app.getGuiManager().getPanelPedido());
                    pnItems.add(pnProd, new GridBagConstraints(j, i, 1, 1, 0.1, 0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
                    c++;
                }
            }
            pnItems.add(Box.createVerticalGlue(), new GridBagConstraints(j + 1, i, 1, 1, 0, 0.1, GridBagConstraints.SOUTH, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 1, 1));

        }
        pnItems.updateUI();
        app.getGuiManager().setDefaultCursor();
    }

    public void showView1() {
        view = 1;
        app.getGuiManager().setWaitCursor();
        pnItems.removeAll();
        pnItems.setLayout(new GridLayout(0, 2, 10, 10));

        if (products != null) {
            for (int i = 0; i < products.size(); i++) {
                Product prod = products.get(i);
                PanelProduct2 pnProd = new PanelProduct2(app, prod);
                pnProd.addPropertyChangeListener(this);
                pnItems.add(pnProd);
            }
        }

        pnItems.add(Box.createVerticalGlue());
        pnItems.add(Box.createVerticalGlue());
        pnItems.updateUI();
        app.getGuiManager().setDefaultCursor();
    }

    public void resizePanel() {

        if (pnItems != null) {
            int width = pnItems.getWidth();

            if (oldSize != products.size()) {
                if (width > 0) {
                    int h = 120 * ((products.size() + 1) / 2);

                    pnItems.setSize(width, h);
                }
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

        if (null != evt.getPropertyName()) {
            switch (evt.getPropertyName()) {
                case PanelProduct2.AC_ADD_QUICK:
                    pcs.firePropertyChange(evt.getPropertyName(), null, evt.getNewValue());
                    break;
                case PanelTopSearch.AC_SELECT_VIEW1:
                    showView1();
                    break;
                case PanelTopSearch.AC_SELECT_VIEW2:
                    showView2();
                    break;
                case PanelTopSearch.AC_CHANGE_SORT:
                    changeSort(evt.getNewValue().toString());
                    break;
                default:
                    break;
            }
        }
    }

    public void changeSort(String sort) {
        System.out.println("sort = " + sort);
        if (!selectedSort.equals(sort)) {
            selectedSort = sort;
            if (selectedSort.equalsIgnoreCase(ORDEN_ALPHA)) {
                setProducts(app.getControl().getProductsList("enabled=1", "name"));
            } else if (selectedSort.equalsIgnoreCase(ORDEN_PRICE)) {
                setProducts(app.getControl().getProductsList("enabled=1", "price, name"));
            } else {
                setProducts(app.getControl().getProductsList("enabled=1", ""));
            }

        }

    }

    public String getSelectedSort() {
        return selectedSort;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lbTitle = new javax.swing.JLabel();
        pnItems = new javax.swing.JPanel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));

        pnItems.setLayout(new java.awt.GridLayout(1, 0));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(filler1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbTitle, javax.swing.GroupLayout.DEFAULT_SIZE, 811, Short.MAX_VALUE)
                    .addComponent(pnItems, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnItems, javax.swing.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.Box.Filler filler1;
    private javax.swing.JLabel lbTitle;
    private javax.swing.JPanel pnItems;
    // End of variables declaration//GEN-END:variables

}
