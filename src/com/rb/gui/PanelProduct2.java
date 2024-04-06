/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rb.gui;

import com.rb.Aplication;
import com.rb.Configuration;
import com.rb.domain.Presentation;
import com.rb.domain.Product;
import com.rb.domain.ProductoPed;
import static com.rb.gui.PanelCustomPedido.AC_CUSTOM_ADD;
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
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.dz.PanelCapturaMod;

/**
 *
 * @author lrod
 */
public class PanelProduct2 extends PanelCapturaMod implements ActionListener {

    private Product product;
    private final Aplication app;
    public static final Logger logger = Logger.getLogger(PanelProduct2.class.getCanonicalName());
    private JPopupMenu popPres;

    /**
     * Creates new form PanelProduct
     *
     * @param app
     * @param product
     */
    public PanelProduct2(Aplication app, Product product) {
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
            icon = new ImageIcon(app.getImgManager().getBufImagen(path + "/" + image, 100, 100));
        } catch (Exception e) {
            icon = new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "no-photo.png", 100, 100));
        }

        Font font1 = new Font("Tahoma", 1, 15);
        Font font2 = new Font("Serif", 2, 11);
        Font font3 = new Font("Sans", 1, 16);
        Font font4 = new Font("Tahoma", 2, 11);

        NumberFormat NF = DecimalFormat.getCurrencyInstance();
        NF.setMaximumFractionDigits(0);

        Color color = new Color(125, 11, 7);

        lbImage.setIcon(icon);

        lbName.setFont(font1);

        int MAX_LENGTH = 22;
        String pName = product.getName();
        if (pName.length() > MAX_LENGTH) {
            pName = pName.substring(0, MAX_LENGTH) + "..";
        }
        lbName.setText(pName.toUpperCase());
        lbName.setToolTipText(product.getName().toUpperCase());
        lbName.setOpaque(true);
        lbName.setForeground(Color.blue.darker().darker());
        lbName.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, color),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)
        ));

        lbCategory.setBorder(BorderFactory.createLineBorder(color, 1, true));
        lbCategory.setFont(font4);
        lbCategory.setText(product.getCategory().toUpperCase());
        lbCategory.setOpaque(true);
        lbCategory.setForeground(color.darker().darker());
//        lbCategory.setBackground(color.brighter().brighter().brighter());

        lbDescription.setFont(new Font("Serif", 0, 10));
        lbDescription.setFont(font2);
        lbDescription.setText("<html><p>" + StringUtils.capitalize(product.getDescription()) + "</p></html>");
        lbDescription.setOpaque(true);
        lbDescription.setForeground(Color.gray);

        lbPrice.setText(NF.format(product.getPrice()));
        lbPrice.setFont(font3);
        lbPrice.setOpaque(true);
        lbPrice.setForeground(Color.red.darker());
        lbPrice.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.RED));

        btAdd.setActionCommand(AC_ADD_QUICK);
        btAdd.setMargin(new Insets(1, 1, 1, 1));
        btAdd.setFocusPainted(false);
        btAdd.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "add1.png", 15, 15)));
        btAdd.addActionListener(this);

        btAddCustom.setActionCommand(AC_ADD_CUSTOM);
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
    public static final String AC_ADD_CUSTOM = "AC_ADD_CUSTOM";
    public static final String AC_ADD_QUICK = "AC_ADD_QUICK";

    @Override
    public void actionPerformed(ActionEvent e) {
        if (AC_ADD_QUICK.equals(e.getActionCommand())) {
            pcs.firePropertyChange(AC_ADD_QUICK, null, product);
        } else if (AC_ADD_CUSTOM.equals(e.getActionCommand())) {
            app.getGuiManager().showCustomPedido(product, app);

        }
    }

    @Override
    public String toString() {
        return "Product[\n"
                + "name:" + product.getName() + "\n"
                + "price:" + product.getPrice() + "\n"
                + "]";

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
        lbDescription = new javax.swing.JLabel();
        lbName = new javax.swing.JLabel();
        lbPrice = new javax.swing.JLabel();
        btAdd = new javax.swing.JButton();
        btAddCustom = new javax.swing.JButton();
        lbCategory = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));

        setBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(255, 128, 0), new java.awt.Color(164, 62, 1)));
        setMaximumSize(new java.awt.Dimension(264, 110));
        setPreferredSize(new java.awt.Dimension(264, 110));

        lbImage.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lbDescription.setToolTipText("");
        lbDescription.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        lbPrice.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbDescription, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lbPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btAddCustom, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lbName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lbCategory, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(6, 6, 6)
                .addComponent(lbImage, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(filler1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(lbCategory, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lbImage, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lbName, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbDescription, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(lbPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btAdd, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btAddCustom, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btAdd;
    private javax.swing.JButton btAddCustom;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JLabel lbCategory;
    private javax.swing.JLabel lbDescription;
    private javax.swing.JLabel lbImage;
    private javax.swing.JLabel lbName;
    private javax.swing.JLabel lbPrice;
    // End of variables declaration//GEN-END:variables

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
    }
}
