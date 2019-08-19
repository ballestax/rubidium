/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bacon.gui;

import com.bacon.Aplication;
import com.bacon.domain.Product;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.balx.ColorDg;
import org.balx.SpinnerNumberModelo;
import org.dz.PanelCapturaMod;

/**
 *
 * @author lrod
 */
public class PanelCustomPedido extends PanelCapturaMod {

    private final Aplication app;
    private final Product product;
    private DecimalFormat DCFORM_P;

    /**
     * Creates new form PanelCustomPedido
     * @param app
     */
    public PanelCustomPedido(Aplication app, Product product) {
        this.app = app;
        this.product = product;
        initComponents();
        createComponents();
    }

    private void createComponents() {
        String image = product.getImage();

        ImageIcon icon = new ImageIcon(app.getImgManager().getImagen(image, 100, 100));

        Font font1 = new Font("Tahoma", 1, 17);
        Font font2 = new Font("Serif", 2, 12);
        Font font3 = new Font("Sans", 1, 16);

        NumberFormat NF = DecimalFormat.getCurrencyInstance();
        NF.setMaximumFractionDigits(0);

        lbImage.setIcon(icon);
        lbName.setFont(font1);
        lbName.setText(product.getName().toUpperCase());
        lbName.setOpaque(true);
        lbName.setForeground(Color.blue.darker().darker());
        lbName.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.red));
        lbDescription.setFont(new Font("Serif", 0, 10));
        lbDescription.setFont(font2);
        lbDescription.setText("<html><p>" + product.getDescription() + "</p></html>");
        lbDescription.setOpaque(true);
//        lbDescription.setForeground(Color.gray);
        lbPrice.setText(NF.format(product.getPrice()));
        lbPrice.setFont(font3);
        lbPrice.setOpaque(true);
        lbPrice.setForeground(Color.red.darker());
        lbPrice.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 2, Color.red));
        
        
        DCFORM_P = (DecimalFormat) NumberFormat.getInstance();
        DCFORM_P.applyPattern("$ ###,###,###");
        
        lbCant.setText("Cantidad");
        spCantidad.setModel(new SpinnerNumberModelo(1, 1, 10, 1));
        spCantidad.setForeground(Color.red.darker());
        spCantidad.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int value = (int) spCantidad.getValue();
                double total = value * product.getPrice();
                lbInfo.setText(value+ " "+product.getName()+" x "+DCFORM_P.format(total));
            }
        });
        
        btConfirm.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "success.png", 10, 10)));
        btConfirm.setBackground(new Color(153, 255, 153));
        btConfirm.setMargin(new Insets(1, 1, 1, 1));
        btConfirm.setFont(new Font("Arial", 1, 11));
//        btConfirm.setActionCommand(AC_CONFIRMAR_PEDIDO);
//        btConfirm.addActionListener(this);
        btConfirm.setText("CONFIRMAR");
        
        lbInfo.setText(spCantidad.getValue()+ " "+product.getName());
        
        lbTitle1.setText("Ingredientes");
        lbTitle1.setBackground(ColorDg.colorAleatorio().getColor1());
        panel1.setLayout(new GridLayout(3, 3));
        ArrayList<String> ings = new ArrayList<>();
        ings.add("Tomate");
        ings.add("Lechuga");
        ings.add("Salsa de tomate");
        ings.add("Salsa de la casa");
        ings.add("Tocineta");
        ings.add("Queso americano");
        ings.add("Queso mozarella");
        
        for (int i = 0; i < ings.size(); i++) {
            String ing = ings.get(i);
            JCheckBox check = new JCheckBox("sin "+ing);
            panel1.add(check);
        }
        
        lbTitle2.setText("Adicionales");
        lbTitle2.setBackground(ColorDg.colorAleatorio().getColor1());
        panel2.setLayout(new GridLayout(3, 3));
        ArrayList<String> adds = new ArrayList<>();
        adds.add("Chorizo");
        adds.add("Queso");
        adds.add("Tocineta");
        adds.add("Pepinillos");
        
        
        for (int i = 0; i < adds.size(); i++) {
            String add = adds.get(i);
            JCheckBox check = new JCheckBox(add);
            panel2.add(check);
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
        lbDescription = new javax.swing.JLabel();
        lbPrice = new javax.swing.JLabel();
        lbTitle1 = new javax.swing.JLabel();
        panel1 = new javax.swing.JPanel();
        lbTitle2 = new javax.swing.JLabel();
        panel2 = new javax.swing.JPanel();
        spCantidad = new javax.swing.JSpinner();
        lbCant = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        taObs = new javax.swing.JTextArea();
        lbInfo = new javax.swing.JLabel();
        btConfirm = new javax.swing.JButton();

        lbName.setFont(new java.awt.Font("Ubuntu", 1, 17)); // NOI18N
        lbName.setText("jLabel1");
        lbName.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lbDescription.setText("jLabel1");
        lbDescription.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lbPrice.setFont(new java.awt.Font("Ubuntu", 1, 17)); // NOI18N
        lbPrice.setText("jLabel1");
        lbPrice.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lbTitle1.setText("jLabel1");
        lbTitle1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        lbTitle1.setOpaque(true);

        panel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout panel1Layout = new javax.swing.GroupLayout(panel1);
        panel1.setLayout(panel1Layout);
        panel1Layout.setHorizontalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panel1Layout.setVerticalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 95, Short.MAX_VALUE)
        );

        lbTitle2.setText("jLabel1");
        lbTitle2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        lbTitle2.setOpaque(true);

        panel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout panel2Layout = new javax.swing.GroupLayout(panel2);
        panel2.setLayout(panel2Layout);
        panel2Layout.setHorizontalGroup(
            panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panel2Layout.setVerticalGroup(
            panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 104, Short.MAX_VALUE)
        );

        spCantidad.setFont(new java.awt.Font("Ubuntu", 0, 36)); // NOI18N

        lbCant.setFont(new java.awt.Font("Ubuntu", 0, 12)); // NOI18N
        lbCant.setText("jLabel1");
        lbCant.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        taObs.setColumns(20);
        taObs.setRows(5);
        jScrollPane1.setViewportView(taObs);

        lbInfo.setText("jLabel1");
        lbInfo.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btConfirm.setText("jButton1");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbTitle1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lbImage, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lbDescription, javax.swing.GroupLayout.PREFERRED_SIZE, 329, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(spCantidad, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lbPrice, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE)
                            .addComponent(lbCant, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(panel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbTitle2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lbInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lbImage, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lbName, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbPrice, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbDescription, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lbCant)
                                .addGap(0, 0, 0)
                                .addComponent(spCantidad)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbTitle1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbTitle2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbInfo, javax.swing.GroupLayout.DEFAULT_SIZE, 41, Short.MAX_VALUE)
                    .addComponent(btConfirm))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btConfirm;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbCant;
    private javax.swing.JLabel lbDescription;
    private javax.swing.JLabel lbImage;
    private javax.swing.JLabel lbInfo;
    private javax.swing.JLabel lbName;
    private javax.swing.JLabel lbPrice;
    private javax.swing.JLabel lbTitle1;
    private javax.swing.JLabel lbTitle2;
    private javax.swing.JPanel panel1;
    private javax.swing.JPanel panel2;
    private javax.swing.JSpinner spCantidad;
    private javax.swing.JTextArea taObs;
    // End of variables declaration//GEN-END:variables

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
