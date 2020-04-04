/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bacon.gui;

import com.bacon.Aplication;
import com.bacon.MyConstants;
import com.bacon.domain.Cycle;
import com.bacon.domain.Invoice;
import com.bacon.domain.Table;
import com.bacon.domain.Waiter;
import java.awt.Color;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import org.apache.log4j.Logger;
import org.bx.gui.MyDefaultTableModel;
import org.dz.PanelCapturaMod;

/**
 *
 * @author lrod
 */
public class PanelCash extends PanelCapturaMod implements ActionListener, PropertyChangeListener {
    
    private final Aplication app;
    private Cycle cycle;
    public static final Logger logger = Logger.getLogger(PanelCash.class.getCanonicalName());
    private MyDefaultTableModel model;
    private DecimalFormat decimalFormat;

    /**
     * Creates new form PanelCash
     *
     * @param app
     */
    public PanelCash(Aplication app, Cycle cycle) {
        this.app = app;
        this.cycle = cycle;
        initComponents();
        createComponents();
    }
    
    private void createComponents() {
        
        if (cycle == null) {
            cycle = app.getControl().getLastCycle();
            showCycle(cycle);
        }
        
        lbFacturas.setText("Facturas");
        lbGastos.setText("Gastos");
        
        decimalFormat = app.getDCFORM_W();
        
        String[] cols = new String[]{"Ticket", "Fecha", "Valor", "Tipo", "Mesa", "Mesero", "Facturar"};
        
        model = new MyDefaultTableModel(cols, 0);
        tableInvoices.setModel(model);
        
        tableInvoices.setRowHeight(22);
        int[] colW = new int[]{40, 100, 80, 50, 50, 50, 40};
        for (int i = 0; i < colW.length; i++) {
            tableInvoices.getColumnModel().getColumn(i).setMinWidth(colW[i]);
            tableInvoices.getColumnModel().getColumn(i).setPreferredWidth(colW[i]);
            tableInvoices.getColumnModel().getColumn(i).setCellRenderer(new TablaCellRenderer(true));
        }
        
        TablaCellRenderer rightRenderer = new TablaCellRenderer(true);
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        tableInvoices.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);
        
        Color color1 = new Color(45, 167, 72);
        lbTit1.setText("Ventas");
        lbTit1.setOpaque(true);
        lbTit1.setForeground(color1);
        lbTit1.setBorder(BorderFactory.createLineBorder(color1.darker(), 1, true));
        lbData1.setOpaque(true);
        lbData1.setBorder(BorderFactory.createLineBorder(color1.darker(), 1, true));
        lbData1.setBackground(color1);
        
        Color color2 = new Color(167, 45, 72);
        lbTit2.setText("Gastos");
        lbTit2.setOpaque(true);
        lbTit2.setForeground(color2);
        lbData2.setOpaque(true);
        lbTit2.setBorder(BorderFactory.createLineBorder(color2.darker(), 1, true));
        lbData2.setBorder(BorderFactory.createLineBorder(color2.darker(), 1, true));
        lbData2.setBackground(color2);
        
        Color color3 = new Color(45, 72, 167);
        lbTit3.setText("Resultado");
        lbTit3.setOpaque(true);
        lbTit3.setForeground(color3);
        lbTit3.setBorder(BorderFactory.createLineBorder(color3.darker(), 1, true));
        lbData3.setOpaque(true);
        lbData3.setBorder(BorderFactory.createLineBorder(color3.darker(), 1, true));
        lbData3.setBackground(color3);
        
        Color color4 = new Color(45, 172, 167);
        lbTit4.setText("Inicial");
        lbTit4.setOpaque(true);
        lbTit4.setForeground(color4);
        lbTit4.setBorder(BorderFactory.createLineBorder(color4.darker(), 1, true));
        lbData4.setOpaque(true);
        lbData4.setBorder(BorderFactory.createLineBorder(color4.darker(), 1, true));
        lbData4.setBackground(color4);
        
        btNewCiclo.setMargin(new Insets(2, 2, 2, 2));
        btNewCiclo.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "open.png", 32, 32)));
        btNewCiclo.setActionCommand(AC_NEW_CYCLE);
        btNewCiclo.addActionListener(this);
        
        btCloseCiclo.setMargin(new Insets(2, 2, 2, 2));
        btCloseCiclo.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "close.png", 32, 32)));
        btCloseCiclo.setActionCommand(AC_CLOSE_CYCLE);
        btCloseCiclo.addActionListener(this);
        
        Border bordeOut = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, color3, color2);
        Border bordeIn = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        
        jLabel1.setText("Ciclo de caja");
        
        jLabel2.setBorder(BorderFactory.createCompoundBorder(bordeOut, BorderFactory.createEmptyBorder(2, 8, 2, 8)));
        
        lbInit.setBorder(BorderFactory.createCompoundBorder(bordeOut, bordeIn));
        
        lbEnd.setBorder(BorderFactory.createCompoundBorder(bordeOut, bordeIn));
        
        populateTabla("");
        
        
        lbData1.setText("0");
        lbData2.setText("0");
        lbData3.setText("0");
    }
    public static final String AC_CLOSE_CYCLE = "AC_CLOSE_CYCLE";
    public static final String AC_NEW_CYCLE = "AC_NEW_CYCLE";
    
    private void showCycle(Cycle cycle) {
        jLabel2.setText("<html><font color=blue size=5>" + cycle.getId() + "</font></html>");
        
        Color colorStatus = cycle.getStatus() == 1 ? Color.GREEN : Color.RED;
        lbStatus.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, colorStatus, colorStatus.darker()));
        lbStatus.setText("<html><font color=green>" + (cycle.getStatus() == 1 ? "Abierto" : "Cerrado") + "</font></html>");
        lbInit.setText("<html>Apertura:<br><font color=red size=4>" + app.DF_FULL3.format(cycle.getInit()) + "</font></html>");
        if (cycle.getEnd() != null) {
            lbEnd.setText("<html>Cierre:<br><font color=green size=4>" + app.DF_FULL3.format(cycle.getEnd()) + "</font></html>");
        } else {
            lbEnd.setText("<html>Cierre:<br><font color=green size=4>" + "" + "</font></html>");
        }
        
        lbData4.setText("<html><font size=4>" + app.DCFORM_P.format(cycle.getInitialBalance().doubleValue()) + "</font></html>");
        
        if (cycle.getStatus() == 1) {
            btNewCiclo.setEnabled(false);
            btCloseCiclo.setEnabled(true);
        } else {
            btNewCiclo.setEnabled(true);
            btCloseCiclo.setEnabled(false);
        }
    }
    
    private void populateTabla(String query) {
        
        SwingWorker sw = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                model.setRowCount(0);
                
                ArrayList<Invoice> invoiceslList = app.getControl().getInvoiceslList("", "sale_date DESC");
                BigDecimal total = new BigDecimal(0);
                int totalProducts = 0;
                for (int i = 0; i < invoiceslList.size(); i++) {
                    Invoice invoice = invoiceslList.get(i);
                    Waiter waiter = app.getControl().getWaitersByID(invoice.getIdWaitress());
                    Table table = app.getControl().getTableByID(invoice.getTable());
                    total = total.add(invoice.getValor());
                    totalProducts += invoice.getProducts().size();
                    
                    model.addRow(new Object[]{
                        invoice.getFactura(),
                        app.DF_FULL2.format(invoice.getFecha()),
                        app.DCFORM_P.format(invoice.getValor()),
                        MyConstants.TIPO_PEDIDO[invoice.getTipoEntrega() - 1],
                        //invoice.getIdCliente(),
                        table != null ? table.getName() : "-",
                        waiter != null ? waiter.getName() : "-",
                        true
                    });
                    
                    model.setRowEditable(model.getRowCount() - 1, false);
                    model.setCellEditable(model.getRowCount() - 1, model.getColumnCount() - 1, true);
                }
                
                return true;
            }
            
            @Override
            protected void done() {
                app.getGuiManager().setDefaultCursor();
            }
            
        };
        app.getGuiManager().setWaitCursor();
        sw.execute();
        
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (AC_NEW_CYCLE.equals(e.getActionCommand())) {
            app.getGuiManager().showPanelNewCycle(this);
        } else if (AC_CLOSE_CYCLE.equals(e.getActionCommand())) {
            if (cycle.getStatus() == 1) {
                cycle.setStatus(0);
                cycle.setEnd(new Date());
                app.getControl().updateCycle(cycle);
                showCycle(cycle);
            }
        }
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        logger.debug("last:" + evt.getPropertyName() + ":" + evt.getPropagationId());
        if (AC_NEW_CYCLE.equals(evt.getPropertyName())) {
            Cycle cycle = (Cycle) evt.getNewValue();
            this.cycle = cycle;
            showCycle(cycle);
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        btNewCiclo = new javax.swing.JButton();
        btCloseCiclo = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        lbInit = new javax.swing.JLabel();
        lbEnd = new javax.swing.JLabel();
        lbStatus = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableInvoices = new javax.swing.JTable();
        lbFacturas = new javax.swing.JLabel();
        cbSelInvoices = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        lbTit1 = new javax.swing.JLabel();
        lbData1 = new javax.swing.JLabel();
        lbTit2 = new javax.swing.JLabel();
        lbData2 = new javax.swing.JLabel();
        lbTit3 = new javax.swing.JLabel();
        lbData3 = new javax.swing.JLabel();
        lbTit4 = new javax.swing.JLabel();
        lbData4 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        lbGastos = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        listGastos = new javax.swing.JList<>();

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setBackground(java.awt.Color.lightGray);
        jLabel1.setText("jLabel1");
        jLabel1.setOpaque(true);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("jLabel2");

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        lbInit.setText("jLabel3");
        lbInit.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        lbInit.setBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(1, 17, 95), new java.awt.Color(10, 18, 180)));

        lbEnd.setText("jLabel4");
        lbEnd.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        lbEnd.setBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(1, 17, 95), new java.awt.Color(10, 18, 180)));

        lbStatus.setText("jLabel1");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btNewCiclo, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
                    .addComponent(btCloseCiclo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(12, 12, 12)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lbInit, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbEnd, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btCloseCiclo, btNewCiclo});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btNewCiclo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lbStatus)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(btCloseCiclo, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addComponent(jSeparator1)
                    .addComponent(lbEnd, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbInit, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btCloseCiclo, btNewCiclo});

        tableInvoices.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(tableInvoices);

        lbFacturas.setText("jLabel1");

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lbTit1.setText("jLabel1");

        lbData1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbData1.setText("jLabel2");

        lbTit2.setText("jLabel1");

        lbData2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbData2.setText("jLabel2");

        lbTit3.setText("jLabel1");

        lbData3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbData3.setText("jLabel2");

        lbTit4.setText("jLabel1");

        lbData4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbData4.setText("jLabel2");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lbTit4, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
                    .addComponent(lbData4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lbData1, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbTit1, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbData2, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbTit2, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lbData3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbTit3, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(289, 289, 289))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {lbData1, lbData2, lbData3, lbData4, lbTit1, lbTit2, lbTit3, lbTit4});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(lbTit3)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(lbData3, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(lbTit2)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(lbData2, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(lbTit1)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(lbData1, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(lbTit4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbData4, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(27, Short.MAX_VALUE))
        );

        jButton1.setText("jButton1");

        lbGastos.setText("jLabel1");

        jScrollPane2.setViewportView(listGastos);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lbFacturas, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(cbSelInvoices, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jButton1))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lbGastos, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jScrollPane2)))
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lbFacturas, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cbSelInvoices, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lbGastos, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 282, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btCloseCiclo;
    private javax.swing.JButton btNewCiclo;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox<String> cbSelInvoices;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lbData1;
    private javax.swing.JLabel lbData2;
    private javax.swing.JLabel lbData3;
    private javax.swing.JLabel lbData4;
    private javax.swing.JLabel lbEnd;
    private javax.swing.JLabel lbFacturas;
    private javax.swing.JLabel lbGastos;
    private javax.swing.JLabel lbInit;
    private javax.swing.JLabel lbStatus;
    private javax.swing.JLabel lbTit1;
    private javax.swing.JLabel lbTit2;
    private javax.swing.JLabel lbTit3;
    private javax.swing.JLabel lbTit4;
    private javax.swing.JList<String> listGastos;
    private javax.swing.JTable tableInvoices;
    // End of variables declaration//GEN-END:variables
}
