/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rb.gui;

import com.rb.Aplication;
import com.rb.Configuration;
import com.rb.GUIManager;
import com.rb.domain.ConfigDB;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.BoxLayout;

/**
 *
 * @author ballestax
 */
public class PanelConfigPrint extends javax.swing.JPanel implements ActionListener {

    public static final String AC_APPLY_CHANGES = "AC_APPLY_CHANGES";
    public static final String AC_REFRESH_PRINTERS = "AC_REFRESH_PRINTERS";
    public static final String AC_SEL_PRINTER = "AC_SEL_PRINTER";

    private final Aplication app;
    private String selectedPrinter;
    private String printerName;

    /**
     * Creates new form PanelConfigMotor
     *
     * @param app
     */
    public PanelConfigPrint(Aplication app) {
        this.app = app;
        initComponents();
        createComponents();
    }

    private void createComponents() {

        lbTitle.setText("Configurar impresion");

        lbInfo.setText("Selecccione la impresora POS");

        ConfigDB config = app.getControl().getConfigLocal(com.rb.Configuration.PRINTER_SELECTED);
        printerName = config != null ? config.getValor() : app.getConfiguration().getProperty(com.rb.Configuration.PRINTER_SELECTED, "");
        lbPrinter.setText("<html>Impresora seleccionada: <font color=blue>" + printerName + "</font></html>");

        refreshPrinters();
        regPrinter.setActionCommand(AC_SEL_PRINTER);
        regPrinter.addActionListener(this);

        btApply.setText("Aplicar");
        btApply.setActionCommand(AC_APPLY_CHANGES);
        btApply.addActionListener(this);
    }
    

    private PrintService[] listPrinters() {
        return PrintServiceLookup.lookupPrintServices(null, null);
    }

    private void refreshPrinters() {
        PrintService[] listPrinters = listPrinters();
        regPrinter.setText(listPrinters);
        regPrinter.setActionCommand(AC_SEL_PRINTER);
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
        lbInfo = new javax.swing.JLabel();
        btApply = new javax.swing.JButton();
        lbPrinter = new javax.swing.JLabel();
        regPrinter = new com.rb.gui.util.Registro(BoxLayout.X_AXIS, "Impresora", new String[0]);

        lbTitle.setBackground(java.awt.Color.lightGray);
        lbTitle.setOpaque(true);

        lbInfo.setText("jLabel2");
        lbInfo.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lbPrinter.setText("jLabel1");
        lbPrinter.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lbTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbInfo, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                    .addComponent(lbPrinter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btApply, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(regPrinter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(lbTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lbInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(regPrinter, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbPrinter, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btApply, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(119, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btApply;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel lbInfo;
    private javax.swing.JLabel lbPrinter;
    private javax.swing.JLabel lbTitle;
    private com.rb.gui.util.Registro regPrinter;
    // End of variables declaration//GEN-END:variables

    @Override
    public void actionPerformed(ActionEvent e) {
        String userName = app.getUser().getUsername();
        String userDevice = Aplication.getUserDevice();
        if (AC_APPLY_CHANGES.equals(e.getActionCommand())) {
            String value = printerName;
            app.getControl().addConfig(new ConfigDB(Configuration.PRINTER_SELECTED, ConfigDB.STRING, value, userName, userDevice));
        } else if (AC_SEL_PRINTER.equals(e.getActionCommand())) {
            PrintService printer = (PrintService) regPrinter.getSelectedItem();
            lbPrinter.setText("<html>Impresora seleccionada: <font color=blue>" + printer.getName() + "</font></html>");
            String value = printer.getName();
            app.getControl().addConfig(new ConfigDB(Configuration.PRINTER_SELECTED, ConfigDB.STRING, value, userName, userDevice));
            selectedPrinter = value;
        } else if (AC_REFRESH_PRINTERS.equals(e.getActionCommand())) {
            refreshPrinters();
        }
    }
}
