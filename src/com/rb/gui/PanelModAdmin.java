/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor. 
 */
package com.rb.gui;

import com.rb.Aplication;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

/**
 *
 * @author ballestax
 */
public class PanelModAdmin extends javax.swing.JPanel {

    private Aplication app;
    private JTabbedPane tabPane;

    /**
     * Creates new form PanelAdminModule
     */
    public PanelModAdmin(Aplication app) {

        this.app = app;
        initComponents();
        customInit();
    }

    private void customInit() {
        setLayout(new BorderLayout());

        tabPane = new JTabbedPane();
        tabPane.setTabPlacement(JTabbedPane.LEFT);

        addTab("Copia de seguridad", app.getGuiManager().getPanelAdminBackup());
        addTab("Configurar", app.getGuiManager().getPanelAdminConfig());
        addTab("Usuarios", app.getGuiManager().getPanelAdminUsers());

    }

    public void addTab(String tab, JComponent comp) {
        JLabel titleTab;
        titleTab = new JLabel();
        titleTab.setHorizontalAlignment(SwingConstants.LEFT);
        titleTab.setPreferredSize(new Dimension(200, 30));
        tabPane.addTab(tab, comp);
        titleTab.setText("<html><p align=left><font size=+1>" + tab + "</p></html>");
        tabPane.setTabComponentAt(tabPane.getTabCount() - 1, titleTab);
        add(tabPane, SwingConstants.CENTER);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
