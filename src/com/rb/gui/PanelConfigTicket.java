/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rb.gui;

import com.rb.Aplication;
import com.rb.Configuration;
import static com.rb.MyConstants.CF_FACTURA_ACTUAL;
import static com.rb.MyConstants.CF_FACTURA_FINAL;
import static com.rb.MyConstants.CF_FACTURA_INICIAL;
import com.rb.domain.ConfigDB;
import com.rb.gui.util.Registro;
import com.github.anastaciocintra.escpos.EscPosConst;
import com.github.anastaciocintra.escpos.Style;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.BoxLayout;

/**
 *
 * @author ballestax
 */
public class PanelConfigTicket extends javax.swing.JPanel implements ActionListener {

    public static final String ACTION_APPLY = "ACTION_SAVE";

    private final Aplication app;
    private String selectedPrinter;
    private String printerName;
    private Registro regName;
    private Registro regFont1;
    private Registro regID;
    private Registro regFont2;
    private Registro regPhone;
    private Registro regFont3;
    private Registro regAddress;
    private Registro regFont4;
    private Registro regCustom1;
    private Registro regCustom2;
    private Registro regInvoiceInit;
    private Registro regInvoiceEnd;
    private Registro regInvoice;
    private Registro regQualityMsg;
    private Registro regQualityScl;
    private ConfigCont cContQuality;

    /**
     * Creates new form PanelConfigMotor
     *
     * @param app
     */
    public PanelConfigTicket(Aplication app) {
        this.app = app;
        initComponents();
        createComponents();
    }

    private void createComponents() {

        jPanel2.setLayout(new BoxLayout(jPanel2, BoxLayout.Y_AXIS));

        lbTitle.setText("Configurar Factura");

        Color color1 = new Color(225, 176, 206);
        
        Font font = new Font("Sans",1,16);

        Style font1 = new Style().setFontSize(Style.FontSize._1, Style.FontSize._1).setJustification(EscPosConst.Justification.Center);
        Style font2 = new Style().setFontSize(Style.FontSize._2, Style.FontSize._2).setJustification(EscPosConst.Justification.Center);
        Style font3 = new Style().setFontSize(Style.FontSize._3, Style.FontSize._3).setJustification(EscPosConst.Justification.Center);

        Style[] fuentes = {font1, font2, font3};

        int space = 10;
        int lWidth = 110;

        regName = new Registro(BoxLayout.X_AXIS, "Nombre", "", lWidth);
        regName.setFontCampo(font);
        regName.setBackground(color1);
        regFont1 = new Registro(BoxLayout.X_AXIS, "Fuente", new Object[0]);
        regFont1.setText(FONTS.values());
        regFont1.setBackground(color1);

        ConfigCont cCont = new ConfigCont(app);
        cCont.setBackgroundTitle(new Color(200, 210, 220));
        cCont.setTitle("Nombre del establecimiento");
        Box boxHoriz = new Box(BoxLayout.X_AXIS);
        boxHoriz.add(regName);
        boxHoriz.add(regFont1);
        cCont.addCampo(boxHoriz);

        jPanel2.add(cCont);
        jPanel2.add(Box.createVerticalStrut(space));

        regID = new Registro(BoxLayout.X_AXIS, "Identificacion", "", lWidth);
        regID.setBackground(color1);
        regID.setFontCampo(font);
        regFont2 = new Registro(BoxLayout.X_AXIS, "Fuente", new Object[0]);
        regFont2.setText(FONTS.values());
        regFont2.setBackground(color1);
        cCont = new ConfigCont(app);
        cCont.setBackgroundTitle(new Color(200, 210, 220));
        cCont.setTitle("Identificacion");
        boxHoriz = new Box(BoxLayout.X_AXIS);
        boxHoriz.add(regID);
        boxHoriz.add(regFont2);
        cCont.addCampo(boxHoriz);

        jPanel2.add(cCont);
        jPanel2.add(Box.createVerticalStrut(space));

        regPhone = new Registro(BoxLayout.X_AXIS, "Telefono", "", lWidth);
        regPhone.setBackground(color1);
        regPhone.setFontCampo(font);
        regFont3 = new Registro(BoxLayout.X_AXIS, "Fuente", new Object[0]);
        regFont3.setText(FONTS.values());
        regFont3.setBackground(color1);
        cCont = new ConfigCont(app);
        cCont.setBackgroundTitle(new Color(200, 210, 220));
        cCont.setTitle("Telefono");
        boxHoriz = new Box(BoxLayout.X_AXIS);
        boxHoriz.add(regPhone);
        boxHoriz.add(regFont3);
        cCont.addCampo(boxHoriz);

        jPanel2.add(cCont);
        jPanel2.add(Box.createVerticalStrut(space));

        regAddress = new Registro(BoxLayout.X_AXIS, "Direccion", "", lWidth);
        regAddress.setBackground(color1);
        regAddress.setFontCampo(font);
        regFont4 = new Registro(BoxLayout.X_AXIS, "Fuente", new Object[0]);
        regFont4.setText(FONTS.values());
        regFont4.setBackground(color1);
        cCont = new ConfigCont(app);
        cCont.setBackgroundTitle(new Color(200, 210, 220));
        cCont.setTitle("Direccion");
        boxHoriz = new Box(BoxLayout.X_AXIS);
        boxHoriz.add(regAddress);
        boxHoriz.add(regFont4);
        cCont.addCampo(boxHoriz);

        jPanel2.add(cCont);
        jPanel2.add(Box.createVerticalStrut(space));

        regCustom1 = new Registro(BoxLayout.X_AXIS, "Personalizado 1", "", lWidth);
        regCustom1.setBackground(color1);
        regCustom1.setFontCampo(font);
        regFont4 = new Registro(BoxLayout.X_AXIS, "Fuente", new Object[0]);
        regFont4.setText(FONTS.values());
        regFont4.setBackground(color1);
        cCont = new ConfigCont(app);
        cCont.setBackgroundTitle(new Color(200, 210, 220));
        cCont.setTitle("Campo personalizado superior");
        boxHoriz = new Box(BoxLayout.X_AXIS);
        boxHoriz.add(regCustom1);
        boxHoriz.add(regFont4);
        cCont.addCampo(boxHoriz);

        jPanel2.add(cCont);
        jPanel2.add(Box.createVerticalStrut(space));

        regCustom2 = new Registro(BoxLayout.X_AXIS, "Personsalizado 2", "", lWidth);
        regCustom2.setBackground(color1);
        regCustom2.setFontCampo(font);
        regFont4 = new Registro(BoxLayout.X_AXIS, "Fuente", new Object[0]);
        regFont4.setText(FONTS.values());
        regFont4.setBackground(color1);
        cCont = new ConfigCont(app);
        cCont.setBackgroundTitle(new Color(200, 210, 220));
        cCont.setTitle("Campo personalizado inferior");
        boxHoriz = new Box(BoxLayout.X_AXIS);
        boxHoriz.add(regCustom2);
        boxHoriz.add(regFont4);
        cCont.addCampo(boxHoriz);

        jPanel2.add(cCont);
        jPanel2.add(Box.createVerticalStrut(space));

        regQualityMsg = new Registro(BoxLayout.X_AXIS, "Mensaje", "", lWidth);
        regQualityMsg.setBackground(color1);
        regQualityMsg.setFontCampo(font);
        regQualityScl = new Registro(BoxLayout.X_AXIS, "Escala", "", lWidth);
        regQualityScl.setBackground(color1);
        regQualityScl.setFontCampo(font);
        cContQuality = new ConfigCont(app, true);
        cContQuality.setBackgroundTitle(new Color(200, 210, 220));
        cContQuality.setTitle("Calidad del servicio");
        cContQuality.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean selected = cContQuality.isSelected();
                regQualityMsg.setEnabled(selected);
                regQualityScl.setEnabled(selected);
            }
        });

        boxHoriz = new Box(BoxLayout.Y_AXIS);
        boxHoriz.add(regQualityMsg);
        boxHoriz.add(Box.createVerticalStrut(4));
        boxHoriz.add(regQualityScl);
        cContQuality.addCampo(boxHoriz);

        jPanel2.add(cContQuality);
        jPanel2.add(Box.createVerticalStrut(space));

        regInvoiceInit = new Registro(BoxLayout.X_AXIS, "Inicial", "", lWidth);
        regInvoiceInit.setBackground(color1);
        regInvoiceInit.setFontCampo(font);
        regInvoiceEnd = new Registro(BoxLayout.X_AXIS, "Final", "", lWidth);
        regInvoiceEnd.setBackground(color1);
        regInvoiceEnd.setFontCampo(font);
        regInvoice = new Registro(BoxLayout.X_AXIS, "Actual", "", lWidth);
        regInvoice.setBackground(color1);
        regInvoice.setFontCampo(font);

        cCont = new ConfigCont(app);
        cCont.setBackgroundTitle(new Color(200, 210, 220));
        cCont.setTitle("Consecutivo de facturas");
        boxHoriz = new Box(BoxLayout.X_AXIS);
        boxHoriz.add(regInvoiceInit);
        boxHoriz.add(regInvoiceEnd);
        boxHoriz.add(regInvoice);
        cCont.addCampo(boxHoriz);

        jPanel2.add(cCont);
        jPanel2.add(Box.createVerticalGlue());
        jPanel2.add(Box.createVerticalStrut(space));

        ConfigDB config = app.getControl().getConfigLocal(com.rb.Configuration.BS_NAME);
        regName.setText(config != null ? config.getValor() : "NOMBRE");
        config = app.getControl().getConfigLocal(com.rb.Configuration.BS_ID);
        regID.setText(config != null ? config.getValor() : "000000000-0");
        config = app.getControl().getConfigLocal(com.rb.Configuration.BS_PHONE);
        regPhone.setText(config != null ? config.getValor() : "300 0000000");
        config = app.getControl().getConfigLocal(com.rb.Configuration.BS_ADDRESS);
        regAddress.setText(config != null ? config.getValor() : "Direcion");
        config = app.getControl().getConfigLocal(com.rb.Configuration.BS_CUSTOM_TOP);
        regCustom1.setText(config != null ? config.getValor() : "Personalizado 1");
        config = app.getControl().getConfigLocal(com.rb.Configuration.BS_CUSTOM_BOTTON);
        regCustom2.setText(config != null ? config.getValor() : "Personalizado 2");
                        
        config = app.getControl().getConfigLocal(com.rb.Configuration.BS_CUSTOM_QUALITY_MSG);
        regQualityMsg.setText(config != null ? config.getValor() : "");
        config = app.getControl().getConfigLocal(com.rb.Configuration.BS_CUSTOM_QUALITY_SCALE);
        regQualityScl.setText(config != null ? config.getValor() : "");
        

        config = app.getControl().getConfigLocal(CF_FACTURA_INICIAL);
        regInvoiceInit.setText(config != null ? config.getValor() : "1");
        config = app.getControl().getConfigLocal(CF_FACTURA_FINAL);
        regInvoiceEnd.setText(config != null ? config.getValor() : "1");
        config = app.getControl().getConfigLocal(CF_FACTURA_ACTUAL);
        regInvoice.setText(config != null ? config.getValor() : "1");

        config = app.getControl().getConfigLocal(com.rb.Configuration.BS_CUSTOM_QUALITY_ENABLED);
        boolean selected = Boolean.valueOf(config != null ? config.getValor() : "false");
        cContQuality.setSelected(selected);        
        regQualityMsg.setEnabled(selected);
        regQualityScl.setEnabled(selected);

        btApply.setText("Aplicar");
        btApply.setActionCommand(ACTION_APPLY);
        btApply.addActionListener(this);
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
        lbTitle = new javax.swing.JLabel();
        btApply = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jPanel2 = new javax.swing.JPanel();

        lbTitle.setBackground(java.awt.Color.lightGray);
        lbTitle.setOpaque(true);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 542, Short.MAX_VALUE)
        );

        jScrollPane2.setViewportView(jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lbTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 578, Short.MAX_VALUE)
                        .addComponent(btApply, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane2))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(lbTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 542, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btApply, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btApply;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lbTitle;
    // End of variables declaration//GEN-END:variables

    @Override
    public void actionPerformed(ActionEvent e) {
        if (ACTION_APPLY.equals(e.getActionCommand())) {
            String userName = app.getUser().getUsername();
            String userDevice = Aplication.getUserDevice();
            
            String value = regName.getText();
            app.getControl().addConfig(new ConfigDB(Configuration.BS_NAME, ConfigDB.STRING, value, userName, userDevice));

            value = regID.getText();
//            app.getConfiguration().setProperty(com.rb.Configuration.BS_ID, value);
            app.getControl().addConfig(new ConfigDB(Configuration.BS_ID, ConfigDB.STRING, value, userName, userDevice));

            value = regAddress.getText();
//            app.getConfiguration().setProperty(com.rb.Configuration.BS_ADDRESS, value);
            app.getControl().addConfig(new ConfigDB(Configuration.BS_ADDRESS, ConfigDB.STRING, value, userName, userDevice));

            value = regPhone.getText();
            app.getControl().addConfig(new ConfigDB(Configuration.BS_PHONE, ConfigDB.STRING, value, userName, userDevice));
//            app.getConfiguration().setProperty(com.rb.Configuration.BS_PHONE, value);

            value = regCustom1.getText();
            app.getControl().addConfig(new ConfigDB(Configuration.BS_CUSTOM_TOP, ConfigDB.STRING, value, userName, userDevice));
//            app.getConfiguration().setProperty(com.rb.Configuration.BS_CUSTOM_TOP, value);

            value = regCustom2.getText();
            app.getControl().addConfig(new ConfigDB(Configuration.BS_CUSTOM_BOTTON, ConfigDB.STRING, value, userName, userDevice));
//            app.getConfiguration().setProperty(com.rb.Configuration.BS_CUSTOM_BOTTON, value);

            value = regInvoiceInit.getText();
            app.getControl().addConfig(new ConfigDB(CF_FACTURA_INICIAL, ConfigDB.INTEGER, value, userName, userDevice));

            value = regInvoiceEnd.getText();
            app.getControl().addConfig(new ConfigDB(CF_FACTURA_FINAL, ConfigDB.INTEGER, value, userName, userDevice));

            value = regInvoice.getText();
            app.getControl().addConfig(new ConfigDB(CF_FACTURA_ACTUAL, ConfigDB.INTEGER, value, userName, userDevice));

            value = regQualityMsg.getText();
            app.getControl().addConfig(new ConfigDB(Configuration.BS_CUSTOM_QUALITY_MSG, ConfigDB.STRING, value, userName, userDevice));
//            app.getConfiguration().setProperty(com.rb.Configuration.BS_CUSTOM_QUALITY_MSG, value);

            value = regQualityScl.getText();
            app.getControl().addConfig(new ConfigDB(Configuration.BS_CUSTOM_QUALITY_SCALE, ConfigDB.STRING, value, userName, userDevice));
//            app.getConfiguration().setProperty(com.rb.Configuration.BS_CUSTOM_QUALITY_SCALE, value);

            boolean selected = cContQuality.isSelected();
            app.getControl().addConfig(new ConfigDB(Configuration.BS_CUSTOM_QUALITY_ENABLED, ConfigDB.BOOLEAN, String.valueOf(selected), userName, userDevice));
            app.getConfiguration().setProperty(com.rb.Configuration.BS_CUSTOM_QUALITY_ENABLED, Boolean.toString(selected));

            app.getConfiguration().save();
        }
    }

    public enum FONTS {
        FONT_1(new Style().setFontSize(Style.FontSize._1, Style.FontSize._1).setJustification(EscPosConst.Justification.Center)),
        FONT_2(new Style().setFontSize(Style.FontSize._1, Style.FontSize._1).setJustification(EscPosConst.Justification.Center)),
        FONT_3(new Style().setFontSize(Style.FontSize._1, Style.FontSize._1).setJustification(EscPosConst.Justification.Center));

        private final Style style;

        private FONTS(Style style) {
            this.style = style;
        }
    }
}
