/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bacon.gui;

import com.bacon.Aplication;
import com.bacon.MyConstants;
import static com.bacon.MyConstants.CF_FACTURA_ACTUAL;
import static com.bacon.MyConstants.CF_FACTURA_FINAL;
import static com.bacon.MyConstants.CF_FACTURA_INICIAL;
import com.bacon.domain.ConfigDB;
import com.github.anastaciocintra.escpos.EscPosConst;
import com.github.anastaciocintra.escpos.Style;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

        lbTitle.setText("Configurar Factura");

        Color color1 = new Color(225, 176, 206);

        Style font1 = new Style().setFontSize(Style.FontSize._1, Style.FontSize._1).setJustification(EscPosConst.Justification.Center);
        Style font2 = new Style().setFontSize(Style.FontSize._2, Style.FontSize._2).setJustification(EscPosConst.Justification.Center);
        Style font3 = new Style().setFontSize(Style.FontSize._3, Style.FontSize._3).setJustification(EscPosConst.Justification.Center);

        Style[] fuentes = {font1, font2, font3};

        lbInfo1.setText("Nombre");
        lbInfo1.setBackground(color1);
        String propName = app.getConfiguration().getProperty(com.bacon.Configuration.BS_NAME, "NOMBRE");
        regText1.setText(propName);
        regText1.setLabelText("Nombre:");
        regFont1.setText(FONTS.values());

        lbInfo2.setText("Identificacion");
        lbInfo2.setBackground(color1);
        String propID = app.getConfiguration().getProperty(com.bacon.Configuration.BS_ID, "000000000");
        regText2.setText(propID);
        regText2.setLabelText("Identificacion:");
        regFont2.setText(FONTS.values());

        lbInfo3.setText("Direccion");
        lbInfo3.setBackground(color1);
        String propAddress = app.getConfiguration().getProperty(com.bacon.Configuration.BS_ADDRESS, "Direccion");
        regText3.setText(propAddress);
        regText3.setLabelText("Direccion");
        regFont3.setText(FONTS.values());

        lbInfo4.setText("Telefono");
        lbInfo4.setBackground(color1);
        String propPhone = app.getConfiguration().getProperty(com.bacon.Configuration.BS_PHONE, "300000000");
        regText4.setText(propPhone);
        regText4.setLabelText("Telefono");
        regFont4.setText(FONTS.values());

        lbInfo5.setText("Personalizado 1");
        lbInfo5.setBackground(color1);
        String propCustom1 = app.getConfiguration().getProperty(com.bacon.Configuration.BS_CUSTOM_TOP, "Personalizado 1");
        regText5.setText(propCustom1);
        regText5.setLabelText("Personalizado1:");
        regFont5.setText(FONTS.values());

        lbInfo6.setText("Personalizado 2");
        lbInfo6.setBackground(color1);
        String propCustom2 = app.getConfiguration().getProperty(com.bacon.Configuration.BS_CUSTOM_BOTTON, "Personalizado 2");
        regText6.setText(propCustom2);
        regText6.setLabelText("Personalizado2:");
        regFont6.setText(FONTS.values());

        String propQualityEnabled = app.getConfiguration().getProperty(com.bacon.Configuration.BS_CUSTOM_QUALITY_ENABLED, "false");
        boolean enabled = Boolean.parseBoolean(propQualityEnabled);
        jCheckBox1.setText("Calidad del servicio");
        jCheckBox1.setBackground(color1);
        jCheckBox1.setSelected(enabled);
        regText10.setEnabled(enabled);
        regText11.setEnabled(enabled);
        regFont7.setEnabled(enabled);
        jCheckBox1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean selected = jCheckBox1.isSelected();
                regText10.setEnabled(selected);
                regText11.setEnabled(selected);
                regFont7.setEnabled(selected);
            }
        });

        String propQualityService = app.getConfiguration().getProperty(com.bacon.Configuration.BS_CUSTOM_QUALITY_MSG, "");
        regText10.setText(propQualityService);
        regText10.setLabelText("Mensaje:");
        String propQualityScale = app.getConfiguration().getProperty(com.bacon.Configuration.BS_CUSTOM_QUALITY_SCALE, "");
        regText11.setText(propQualityScale);
        regText11.setLabelText("Escala:");
        regFont7.setText(FONTS.values());

        lbInfo7.setText("Consecutivo facturas");
        lbInfo7.setBackground(color1);
        ConfigDB config = app.getControl().getConfig(CF_FACTURA_INICIAL);
        regText7.setText(config != null ? config.getValor() : "0");
        regText7.setLabelText("Inicial:");

        ConfigDB config1 = app.getControl().getConfig(CF_FACTURA_FINAL);
        regText8.setText(config1 != null ? config1.getValor() : "0");
        regText8.setLabelText("Final:");

        ConfigDB config2 = app.getControl().getConfig(CF_FACTURA_ACTUAL);
        regText9.setText(config2 != null ? config2.getValor() : "0");
        regText9.setLabelText("Factura actual:");

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
        lbInfo1 = new javax.swing.JLabel();
        btApply = new javax.swing.JButton();
        regText1 = new com.bacon.gui.util.Registro(BoxLayout.X_AXIS, "reg1", "", 100);
        regFont1 = new com.bacon.gui.util.Registro(BoxLayout.X_AXIS, "Fuente", new Object[0]);
        lbInfo2 = new javax.swing.JLabel();
        regText2 = new com.bacon.gui.util.Registro(BoxLayout.X_AXIS, "Reg2", "",100);
        regFont2 = new com.bacon.gui.util.Registro(BoxLayout.X_AXIS, "Fuente", new Object[0]);
        lbInfo3 = new javax.swing.JLabel();
        regText3 = new com.bacon.gui.util.Registro(BoxLayout.X_AXIS, "Reg3", "", 100);
        regFont3 = new com.bacon.gui.util.Registro(BoxLayout.X_AXIS, "Fuente", new Object[0]);
        lbInfo4 = new javax.swing.JLabel();
        regText4 = new com.bacon.gui.util.Registro(BoxLayout.X_AXIS, "Reg4", "",100);
        regFont4 = new com.bacon.gui.util.Registro(BoxLayout.X_AXIS, "Fuente", new Object[0]);
        lbInfo5 = new javax.swing.JLabel();
        regText5 = new com.bacon.gui.util.Registro(BoxLayout.X_AXIS, "Reg5", "",100);
        regFont5 = new com.bacon.gui.util.Registro(BoxLayout.X_AXIS, "Fuente", new Object[0]);
        lbInfo6 = new javax.swing.JLabel();
        regText6 = new com.bacon.gui.util.Registro(BoxLayout.X_AXIS, "Reg6", "",100);
        regFont6 = new com.bacon.gui.util.Registro(BoxLayout.X_AXIS, "Fuente", new Object[0]);
        lbInfo7 = new javax.swing.JLabel();
        regText7 = new com.bacon.gui.util.Registro(BoxLayout.X_AXIS, "Reg6", "",100);
        regText8 = new com.bacon.gui.util.Registro(BoxLayout.X_AXIS, "Reg6", "",100);
        regText9 = new com.bacon.gui.util.Registro(BoxLayout.X_AXIS, "Reg6", "",100);
        regText10 = new com.bacon.gui.util.Registro(BoxLayout.X_AXIS, "Reg6", "",100);
        regFont7 = new com.bacon.gui.util.Registro(BoxLayout.X_AXIS, "Fuente", new Object[0]);
        jCheckBox1 = new javax.swing.JCheckBox();
        regText11 = new com.bacon.gui.util.Registro(BoxLayout.X_AXIS, "Reg6", "",100);

        lbTitle.setBackground(java.awt.Color.lightGray);
        lbTitle.setOpaque(true);

        lbInfo1.setBackground(new java.awt.Color(226, 175, 206));
        lbInfo1.setText("jLabel2");
        lbInfo1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        lbInfo1.setOpaque(true);

        lbInfo2.setText("jLabel2");
        lbInfo2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        lbInfo2.setOpaque(true);

        lbInfo3.setText("jLabel2");
        lbInfo3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        lbInfo3.setOpaque(true);

        lbInfo4.setText("jLabel2");
        lbInfo4.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        lbInfo4.setOpaque(true);

        lbInfo5.setText("jLabel2");
        lbInfo5.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        lbInfo5.setOpaque(true);

        lbInfo6.setText("jLabel2");
        lbInfo6.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        lbInfo6.setOpaque(true);

        lbInfo7.setText("jLabel2");
        lbInfo7.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        lbInfo7.setOpaque(true);

        jCheckBox1.setText("jCheckBox1");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lbTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCheckBox1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbInfo1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btApply, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(regText1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(regFont1, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lbInfo2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(regText2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(regFont2, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lbInfo3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(regText3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(regFont3, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lbInfo4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(regText4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(regFont4, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lbInfo5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(regText5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(regFont5, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lbInfo6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(regText6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(regFont6, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lbInfo7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(regText7, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(regText8, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(regText9, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(regText10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(regFont7, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(regText11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {regText7, regText8, regText9});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(lbTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lbInfo1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(regText1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(regFont1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbInfo2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(regText2, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(regFont2, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbInfo3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(regText3, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(regFont3, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbInfo4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(regText4, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(regFont4, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbInfo5, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(regText5, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(regFont5, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbInfo6, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(regText6, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(regFont6, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addComponent(jCheckBox1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(regText10, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(regFont7, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2)
                .addComponent(regText11, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbInfo7, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(regText7, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(regText8, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(regText9, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btApply, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btApply;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel lbInfo1;
    private javax.swing.JLabel lbInfo2;
    private javax.swing.JLabel lbInfo3;
    private javax.swing.JLabel lbInfo4;
    private javax.swing.JLabel lbInfo5;
    private javax.swing.JLabel lbInfo6;
    private javax.swing.JLabel lbInfo7;
    private javax.swing.JLabel lbTitle;
    private com.bacon.gui.util.Registro regFont1;
    private com.bacon.gui.util.Registro regFont2;
    private com.bacon.gui.util.Registro regFont3;
    private com.bacon.gui.util.Registro regFont4;
    private com.bacon.gui.util.Registro regFont5;
    private com.bacon.gui.util.Registro regFont6;
    private com.bacon.gui.util.Registro regFont7;
    private com.bacon.gui.util.Registro regText1;
    private com.bacon.gui.util.Registro regText10;
    private com.bacon.gui.util.Registro regText11;
    private com.bacon.gui.util.Registro regText2;
    private com.bacon.gui.util.Registro regText3;
    private com.bacon.gui.util.Registro regText4;
    private com.bacon.gui.util.Registro regText5;
    private com.bacon.gui.util.Registro regText6;
    private com.bacon.gui.util.Registro regText7;
    private com.bacon.gui.util.Registro regText8;
    private com.bacon.gui.util.Registro regText9;
    // End of variables declaration//GEN-END:variables

    @Override
    public void actionPerformed(ActionEvent e) {
        if (ACTION_APPLY.equals(e.getActionCommand())) {
            String value = regText1.getText();
            app.getConfiguration().setProperty(com.bacon.Configuration.BS_NAME, value);

            value = regText2.getText();
            app.getConfiguration().setProperty(com.bacon.Configuration.BS_ID, value);

            value = regText3.getText();
            app.getConfiguration().setProperty(com.bacon.Configuration.BS_ADDRESS, value);

            value = regText4.getText();
            app.getConfiguration().setProperty(com.bacon.Configuration.BS_PHONE, value);

            value = regText5.getText();
            app.getConfiguration().setProperty(com.bacon.Configuration.BS_CUSTOM_TOP, value);

            value = regText6.getText();
            app.getConfiguration().setProperty(com.bacon.Configuration.BS_CUSTOM_BOTTON, value);

            value = regText7.getText();
            app.getControl().addConfig(new ConfigDB(CF_FACTURA_INICIAL, ConfigDB.INTEGER, value));

            value = regText8.getText();
            app.getControl().addConfig(new ConfigDB(CF_FACTURA_FINAL, ConfigDB.INTEGER, value));

            value = regText9.getText();
            app.getControl().addConfig(new ConfigDB(CF_FACTURA_ACTUAL, ConfigDB.INTEGER, value));

            value = regText10.getText();
            app.getConfiguration().setProperty(com.bacon.Configuration.BS_CUSTOM_QUALITY_MSG, value);
            
            value = regText11.getText();
            app.getConfiguration().setProperty(com.bacon.Configuration.BS_CUSTOM_QUALITY_SCALE, value);

            boolean selected = jCheckBox1.isSelected();
            app.getConfiguration().setProperty(com.bacon.Configuration.BS_CUSTOM_QUALITY_ENABLED, Boolean.toString(selected));

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
