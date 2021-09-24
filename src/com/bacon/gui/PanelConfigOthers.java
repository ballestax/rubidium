/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bacon.gui;

import com.bacon.Aplication;
import com.bacon.Configuration;
import com.bacon.domain.ConfigDB;
import com.bacon.gui.util.Registro;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.BoxLayout;
import org.dz.TextFormatter;

/**
 *
 * @author ballestax
 */
public class PanelConfigOthers extends javax.swing.JPanel implements ActionListener {

    public static final String ACTION_APPLY = "ACTION_SAVE";

    private final Aplication app;
    private String selectedPrinter;
    private String printerName;
    private Registro regDelivery;
    private Registro regDocName;
    private Registro regNumZeros;
    private Registro regPrefix;
    private Registro regAllowFact;
    private Registro regAllowPreview;
    private Registro regShowExclusions;

    /**
     * Creates new form PanelConfigMotor
     *
     * @param app
     */
    public PanelConfigOthers(Aplication app) {
        this.app = app;
        initComponents();
        createComponents();
        loadData();
    }

    private void createComponents() {
        jPanel1.setLayout(new BoxLayout(jPanel1, BoxLayout.Y_AXIS));

        regDelivery = new Registro(BoxLayout.X_AXIS, "Domicilio", "", 100);
        regDelivery.setDocument(TextFormatter.getDoubleLimiter());
        ConfigCont cCont = new ConfigCont(app);
        cCont.setBackgroundTitle(new Color(200,210,220));
        cCont.setTitle("Valor del domicilio");
        cCont.addCampo(regDelivery);
        jPanel1.add(cCont);
        jPanel1.add(Box.createVerticalStrut(5));

        regDocName = new Registro(BoxLayout.X_AXIS, "Domicilio", "", 100);
        cCont = new ConfigCont(app);
        cCont.setBackgroundTitle(new Color(200,210,220));
        cCont.setTitle("Nombre del documento");
        cCont.addCampo(regDocName);
        jPanel1.add(cCont);
        jPanel1.add(Box.createVerticalStrut(5));

        regNumZeros = new Registro(BoxLayout.X_AXIS, "Ceros", "", 100);
        regNumZeros.setDocument(TextFormatter.getIntegerLimiter());
        cCont = new ConfigCont(app);
        cCont.setBackgroundTitle(new Color(200,210,220));
        cCont.setTitle("Numero de ceros a formatear el consecutivo");
        cCont.addCampo(regNumZeros);
        jPanel1.add(cCont);
        jPanel1.add(Box.createVerticalStrut(5));

        regPrefix = new Registro(BoxLayout.X_AXIS, "Prefijo", "", 100);
        cCont = new ConfigCont(app);
        cCont.setBackgroundTitle(new Color(200,210,220));
        cCont.setTitle("Prefijo del consecutivo");
        cCont.addCampo(regPrefix);
        jPanel1.add(cCont);
        jPanel1.add(Box.createVerticalStrut(5));

        regAllowFact = new Registro(BoxLayout.X_AXIS, "Permitir", false, 100);
        regAllowFact.setFontCampo(new Font("Arial", 0, 16));
        cCont = new ConfigCont(app);
        cCont.setBackgroundTitle(new Color(200,210,220));
        cCont.setTitle("Permitir facturar sin existencias");
        cCont.addCampo(regAllowFact);
        jPanel1.add(cCont);
        jPanel1.add(Box.createVerticalStrut(5));

        regAllowPreview = new Registro(BoxLayout.X_AXIS, "Permitir", false, 100);
        regAllowPreview.setFontCampo(new Font("Arial", 0, 16));
        cCont = new ConfigCont(app);
        cCont.setBackgroundTitle(new Color(200,210,220));
        cCont.setTitle("Permitir imprimir pedido previo");
        cCont.addCampo(regAllowPreview);
        jPanel1.add(cCont);
        jPanel1.add(Box.createVerticalStrut(5));

        regShowExclusions = new Registro(BoxLayout.X_AXIS, "Permitir", false, 100);
        regShowExclusions.setFontCampo(new Font("Arial", 0, 16));
        cCont = new ConfigCont(app);
        cCont.setBackgroundTitle(new Color(200,210,220));
        cCont.setTitle("Mostrar exclusiones y notas del producto");
        cCont.addCampo(regShowExclusions);
        jPanel1.add(cCont);
        jPanel1.add(Box.createVerticalStrut(5));

        btApply.setText("Aplicar");
        btApply.setActionCommand(ACTION_APPLY);
        btApply.addActionListener(this);
    }

    private void loadData() {
        ConfigDB config = app.getControl().getConfig(Configuration.DELIVERY_VALUE);
        double deliveryValue = config != null ? (double) config.castValor() : 0;
        regDelivery.setText(app.getDCFORM_W().format(deliveryValue));

        config = app.getControl().getConfig(Configuration.DOCUMENT_NAME);
        String docName = config != null ? config.getValor() : "";
        regDocName.setText(docName);

        config = app.getControl().getConfig(Configuration.ZEROS_INVOICES);
        int zeros = config != null ? (int) config.castValor() : 0;
        regNumZeros.setText(app.getDCFORM_W().format(zeros));

        config = app.getControl().getConfig(Configuration.PREFIX_INVOICES);
        String prefix = config != null ? config.getValor() : "";
        regPrefix.setText(prefix);

        config = app.getControl().getConfig(Configuration.PRINT_PREV_DELIVERY);
        boolean showPrev = config != null ? (boolean) config.castValor() : false;
        regAllowPreview.setSelected(showPrev);

        config = app.getControl().getConfig(Configuration.SHOW_EXCLUSIONS);
        boolean showExclusions = config != null ? (boolean) config.castValor() : false;
        regShowExclusions.setSelected(showExclusions);

        config = app.getControl().getConfig(Configuration.INVOICE_OUT_STOCK);
        boolean showOutStock = config != null ? (boolean) config.castValor() : false;
        regAllowFact.setSelected(showOutStock);

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        btApply = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 473, Short.MAX_VALUE)
        );

        jScrollPane1.setViewportView(jPanel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 337, Short.MAX_VALUE)
                        .addComponent(btApply, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 473, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btApply, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btApply;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

    @Override
    public void actionPerformed(ActionEvent e) {
        if (ACTION_APPLY.equals(e.getActionCommand())) {
            String value = regDelivery.getText();
            app.getControl().addConfig(new ConfigDB(com.bacon.Configuration.DELIVERY_VALUE, ConfigDB.DOUBLE, value));

            boolean selected = regAllowPreview.isSelected();
//            app.getConfiguration().setProperty(com.bacon.Configuration.PRINT_PREV_DELIVERY, String.valueOf(selected));
            app.getControl().addConfig(new ConfigDB(com.bacon.Configuration.PRINT_PREV_DELIVERY, ConfigDB.BOOLEAN, String.valueOf(selected)));

            boolean selected2 = regShowExclusions.isSelected();
//            app.getConfiguration().setProperty(com.bacon.Configuration.SHOW_EXCLUSIONS, String.valueOf(selected2));
            app.getControl().addConfig(new ConfigDB(com.bacon.Configuration.SHOW_EXCLUSIONS, ConfigDB.BOOLEAN, String.valueOf(selected2)));

            boolean selected3 = regAllowFact.isSelected();
//            app.getConfiguration().setProperty(com.bacon.Configuration.INVOICE_OUT_STOCK, String.valueOf(selected3));
            app.getControl().addConfig(new ConfigDB(com.bacon.Configuration.INVOICE_OUT_STOCK, ConfigDB.BOOLEAN, String.valueOf(selected3)));

            String prefix = regPrefix.getText();
            app.getControl().addConfig(new ConfigDB(com.bacon.Configuration.PREFIX_INVOICES, ConfigDB.STRING, prefix));

            String ceros = regNumZeros.getText();
            app.getControl().addConfig(new ConfigDB(com.bacon.Configuration.ZEROS_INVOICES, ConfigDB.STRING, ceros));

            String docName = regDocName.getText();
            app.getControl().addConfig(new ConfigDB(com.bacon.Configuration.DOCUMENT_NAME, ConfigDB.STRING, docName));

            app.getConfiguration().save();
        }
    }
}
