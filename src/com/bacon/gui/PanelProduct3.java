/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bacon.gui;

import com.bacon.Aplication;
import com.bacon.Configuration;
import com.bacon.domain.Presentation;
import com.bacon.domain.Product;
import com.bacon.domain.ProductoPed;
import static com.bacon.gui.PanelCustomPedido.AC_CUSTOM_ADD;
import static com.bacon.gui.PanelProduct2.AC_ADD_CUSTOM;
import static com.bacon.gui.PanelProduct2.AC_ADD_QUICK;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import org.dz.PanelCapturaMod;

/**
 *
 * @author lrod
 */
public class PanelProduct3 extends PanelCapturaMod implements ActionListener {

    private Product product;
    private final Aplication app;
    private JPopupMenu popPres;

    /**
     * Creates new form PanelProduct
     *
     * @param app
     * @param product
     */
    public PanelProduct3(Aplication app, Product product) {
        this.app = app;
        this.product = product;
        initComponents();
        createComponents();
    }

    private void createComponents() {

        String image = product.getImage();

        String path = app.getConfiguration().getProperty(Configuration.PATH_IMG, "img");

        path = Aplication.getDirPics();

        ImageIcon icon = null;
        try {
            icon = new ImageIcon(app.getImgManager().getBufImagen(path + "/" + image, 65, 65));
        } catch (Exception e) {
            icon = new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "no-photo.png", 65, 65));
        }

        Font font1 = new Font("Sans", 1, 12);
        Font font2 = new Font("Serif", 2, 10);
        Font font3 = new Font("Tahoma", 1, 14);

        NumberFormat NF = DecimalFormat.getCurrencyInstance();
        NF.setMaximumFractionDigits(0);

        Color color = new Color(125, 11, 7);

        int MAX_LENGTH = 20;
        String pName = product.getName();
        if (pName.length() > MAX_LENGTH) {
            pName = pName.substring(0, MAX_LENGTH) + "..";
        }
        lbName.setText(pName.toUpperCase());
        lbName.setToolTipText(product.getName().toUpperCase());
        lbName.setFont(font1);
        lbName.setForeground(Color.blue.darker().darker());
        lbName.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, color),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)
        ));

        lbCategory.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, color));
        lbCategory.setForeground(color.darker().darker());
        lbCategory.setFont(font2);
        lbCategory.setText(product.getCategory().toUpperCase());

        lbPrice.setFont(font3);
        lbPrice.setForeground(Color.red.darker());
        lbPrice.setText(NF.format(product.getPrice()));

        lbImage.setIcon(icon);
        btAddQuick.setActionCommand(PanelProduct2.AC_ADD_QUICK);
        btAddQuick.setMargin(new Insets(1, 1, 1, 1));
        btAddQuick.setFocusPainted(false);
        btAddQuick.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "add1.png", 15, 15)));
        btAddQuick.addActionListener(this);

        btAddCustom.setActionCommand(PanelProduct2.AC_ADD_CUSTOM);
        btAddCustom.setMargin(new Insets(1, 1, 1, 1));
        btAddCustom.setFocusPainted(false);
        btAddCustom.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "process-accept.png", 15, 15)));
        btAddCustom.addActionListener(this);

        popPres = new JPopupMenu();

        ArrayList<Presentation> presList = app.getControl().getPresentationsByProduct(product.getId());

        for (Presentation press : presList) {
            String html = "<html><font color='#610034'size=4>" + press.getName().toUpperCase() + "</font> [<font color='#3d0021'>" + app.DCFORM_P.format(press.getPrice()) + "</font>]</html>";
            JMenuItem item = new JMenuItem(html);
            item.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(4, 2, 2, 2), BorderFactory.createMatteBorder(0, 0, 1, 0, Color.DARK_GRAY)));
            popPres.add(item);
            item.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ProductoPed prod = new ProductoPed(product);
                    prod.setPresentation(press);
                    pcs.firePropertyChange(AC_CUSTOM_ADD, new Object[]{1, press.getPrice()}, prod);
                }
            });
        }

        if (product.isVariablePrice()) {
            List<Double> rankProductsByVarPriceList = app.getControl().getRankProductsByVarPriceList(product.getId(), 6)
                    .stream().sorted().collect(Collectors.toList());

            for (Double price : rankProductsByVarPriceList) {
                if (product.getPrice() == price) {
                    continue;
                }
                String name = price >= 1000 ? (app.DCFORM_W.format(price / 1000) + "K") : String.valueOf(price);
                String html = "<html><font color='#610034'size=4>" + name.toUpperCase() + "</font> [<font color='#3d0021'>" + app.DCFORM_P.format(price) + "</font>]</html>";
                JMenuItem item = new JMenuItem(html);
                item.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(4, 2, 2, 2), BorderFactory.createMatteBorder(0, 0, 1, 0, Color.DARK_GRAY)));
                popPres.add(item);
                item.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        ProductoPed prod = new ProductoPed(product);
                        prod.setPrecio(price);
                        pcs.firePropertyChange(AC_CUSTOM_ADD, new Object[]{1, price}, prod);
                    }
                });
            }
            JMenuItem item = new JMenuItem("<html><font color='#61AA34'size=4>" + "OTROS..." + "</font></html>");
            item.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(4, 2, 2, 2), BorderFactory.createMatteBorder(0, 0, 1, 0, Color.DARK_GRAY)));
            item.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    app.getGuiManager().showCustomPedido(product, app);
                }
            });
            popPres.add(item);
        }

        btAddCustom.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    popPres.show(btAddCustom, 0, 0);
                }
            }

        });

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (AC_ADD_QUICK.equals(e.getActionCommand())) {

            pcs.firePropertyChange(AC_ADD_QUICK, null, product);
        } else if (AC_ADD_CUSTOM.equals(e.getActionCommand())) {
            app.getGuiManager().showCustomPedido(product, app);
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

        lbImage = new javax.swing.JLabel();
        lbName = new javax.swing.JLabel();
        lbPrice = new javax.swing.JLabel();
        btAddCustom = new javax.swing.JButton();
        btAddQuick = new javax.swing.JButton();
        lbCategory = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(255, 128, 0), new java.awt.Color(164, 62, 1)));
        setMaximumSize(new java.awt.Dimension(32767, 85));
        setPreferredSize(new java.awt.Dimension(120, 100));

        lbImage.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lbPrice.setText("jLabel2");

        lbCategory.setText("jLabel2");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lbName, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbCategory, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(lbPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(btAddCustom, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(btAddQuick, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(2, 2, 2)
                        .addComponent(lbImage, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(2, 2, 2))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btAddCustom, btAddQuick});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(lbName, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lbCategory)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbPrice)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btAddCustom, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btAddQuick, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(lbImage, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btAddCustom;
    private javax.swing.JButton btAddQuick;
    private javax.swing.JLabel lbCategory;
    private javax.swing.JLabel lbImage;
    private javax.swing.JLabel lbName;
    private javax.swing.JLabel lbPrice;
    // End of variables declaration//GEN-END:variables

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }
}