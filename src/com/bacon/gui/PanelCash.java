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
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EventObject;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import static javax.swing.BorderFactory.createLineBorder;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
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
    private BigDecimal total;
    public static final Logger logger = Logger.getLogger(PanelCash.class.getCanonicalName());
    private MyDefaultTableModel model;
    private DecimalFormat decimalFormat;
    private MyDefaultTableModel modelExt;

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

        String[] cols = new String[]{"ID", "Ticket", "Fecha", "Valor", "Tipo", "Mesa", "Mesero", "Facturar"};
        model = new MyDefaultTableModel(cols, 0);

        String[] cols2 = new String[]{"Tipo", "Categoria", "Valor", "Nota"};
        modelExt = new MyDefaultTableModel(cols2, 0);

        lbFacturas.setText("Facturas");
        lbGastos.setText("Extras");

        btRefresh.setActionCommand(AC_REFRESH);
        btRefresh.addActionListener(this);
        btRefresh.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "update.png", 32, 32)));

        btAddGasto.setActionCommand(AC_ADD_GASTO);
        btAddGasto.addActionListener(this);
        btAddGasto.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "add1.png", 24, 24)));

        decimalFormat = app.getDCFORM_W();

        tableInvoices.setModel(model);
        tableInvoices.setRowHeight(22);
        int[] colW = new int[]{10, 40, 100, 80, 50, 50, 50, 40};
        for (int i = 0; i < colW.length; i++) {
            tableInvoices.getColumnModel().getColumn(i).setMinWidth(colW[i]);
            tableInvoices.getColumnModel().getColumn(i).setPreferredWidth(colW[i]);
            tableInvoices.getColumnModel().getColumn(i).setCellRenderer(new TablaCellRenderer(true));
        }

        tableExtras.setModel(modelExt);
        tableExtras.setRowHeight(22);
        int[] colWE = new int[]{40, 100, 80};
        for (int i = 0; i < colWE.length; i++) {
            tableExtras.getColumnModel().getColumn(i).setMinWidth(colWE[i]);
            tableExtras.getColumnModel().getColumn(i).setPreferredWidth(colWE[i]);
            tableExtras.getColumnModel().getColumn(i).setCellRenderer(new TablaCellRenderer(true));
        }

        TablaCellRenderer rightRenderer = new TablaCellRenderer(true);
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        tableInvoices.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);

        tableInvoices.getColumnModel().getColumn(model.getColumnCount() - 1).setCellEditor(new BotonEditor(tableInvoices, this, "AC_MOD_USER"));
        tableInvoices.getColumnModel().getColumn(model.getColumnCount() - 1).setCellRenderer(new ButtonCellRenderer("Ver"));

        Color color1 = new Color(45, 167, 72);
        lbTit1.setText("Ventas");
        lbTit1.setOpaque(true);
        lbTit1.setForeground(color1);
        lbTit1.setBorder(BorderFactory.createLineBorder(color1.darker(), 1, true));
        lbData1.setOpaque(true);
        lbData1.setBorder(BorderFactory.createLineBorder(color1.darker(), 1, true));
        lbData1.setBackground(color1.brighter());

        Color color2 = new Color(167, 45, 72);
        lbTit2.setText("Salidas");
        lbTit2.setOpaque(true);
        lbTit2.setForeground(color2);
        lbData2.setOpaque(true);
        lbTit2.setBorder(BorderFactory.createLineBorder(color2.darker(), 1, true));
        lbData2.setBorder(BorderFactory.createLineBorder(color2.darker(), 1, true));
        lbData2.setBackground(color2.brighter());

        Color color3 = new Color(45, 72, 167);
        lbTit3.setText("Resultado");
        lbTit3.setOpaque(true);
        lbTit3.setForeground(color3);
        lbTit3.setBorder(BorderFactory.createLineBorder(color3.darker(), 1, true));
        lbData3.setOpaque(true);
        lbData3.setBorder(BorderFactory.createLineBorder(color3.darker(), 1, true));
        lbData3.setBackground(color3.brighter());

        Color color4 = new Color(45, 172, 167);
        lbTit4.setText("Inicial");
        lbTit4.setOpaque(true);
        lbTit4.setForeground(color4);
        lbTit4.setBorder(BorderFactory.createLineBorder(color4.darker(), 1, true));
        lbData4.setOpaque(true);
        lbData4.setBorder(BorderFactory.createLineBorder(color4.darker(), 1, true));
        lbData4.setBackground(color4.brighter());

        Color color5 = new Color(15, 112, 67);
        lbTit5.setText("Entradas");
        lbTit5.setOpaque(true);
        lbTit5.setForeground(color5);
        lbTit5.setBorder(BorderFactory.createLineBorder(color5.darker(), 1, true));
        lbData5.setOpaque(true);
        lbData5.setBorder(BorderFactory.createLineBorder(color5.darker(), 1, true));
        lbData5.setBackground(color5.brighter());

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

//        populateTabla("");
        lbData1.setText("0");
        lbData2.setText("0");
        lbData3.setText("0");
        lbData5.setText("0");

        loadCycle();

    }

    public void loadCycle() {
        if (cycle == null) {
            cycle = app.getControl().getLastCycle();
        }
        showCycle(cycle);
    }
    private static final String AC_ADD_GASTO = "AC_ADD_GASTO";
    public static final String AC_CLOSE_CYCLE = "AC_CLOSE_CYCLE";
    public static final String AC_NEW_CYCLE = "AC_NEW_CYCLE";
    public static final String AC_REFRESH = "AC_REFRESH";

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

        populateTabla("");
    }

    private void populateTabla(String query) {

        SwingWorker sw = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {

                model.setRowCount(0);

                ArrayList<Invoice> invoiceslList = app.getControl().getInvoiceslList("ciclo=" + cycle.getId(), "sale_date DESC");
                total = new BigDecimal(0);
                int totalProducts = 0;
                int anuladas = 0;
                int ct = 1;
                for (int i = 0; i < invoiceslList.size(); i++) {
                    Invoice invoice = invoiceslList.get(i);
                    Waiter waiter = app.getControl().getWaitersByID(invoice.getIdWaitress());
                    Table table = app.getControl().getTableByID(invoice.getTable());
                    if (invoice.getStatus() != Invoice.ST_ANULADA) {
                        total = total.add(invoice.getValor());
                        totalProducts += invoice.getProducts().size();
//                        servicio += invoice.getValueService();
                        model.addRow(new Object[]{
                            ct++,
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
                    } else {
                        anuladas++;
                    }
                }

                return true;
            }

            @Override
            protected void done() {
                app.getGuiManager().setDefaultCursor();

                lbData1.setText("<html><font size=4>" + app.DCFORM_P.format(total.doubleValue()) + "</font></html>");

                lbData3.setText("<html><font size=4>" + app.DCFORM_P.format(cycle.getInitialBalance().add(total).doubleValue()) + "</font></html>");
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
        } else if (AC_REFRESH.equals(e.getActionCommand())) {
            loadCycle();
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
        btRefresh = new javax.swing.JButton();
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
        lbData5 = new javax.swing.JLabel();
        lbTit5 = new javax.swing.JLabel();
        btAddGasto = new javax.swing.JButton();
        lbGastos = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tableExtras = new javax.swing.JTable();

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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                    .addComponent(lbInit, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbEnd, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btCloseCiclo, btNewCiclo});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {lbEnd, lbInit});

        tableInvoices.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(tableInvoices);

        lbFacturas.setBackground(java.awt.Color.gray);
        lbFacturas.setText("jLabel1");
        lbFacturas.setOpaque(true);

        cbSelInvoices.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbSelInvoicesActionPerformed(evt);
            }
        });

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

        lbData5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbData5.setText("jLabel2");

        lbTit5.setText("jLabel1");

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
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lbData5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbTit5, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbData2, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbTit2, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lbData3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbTit3, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {lbData1, lbData2, lbData3, lbData4, lbTit1, lbTit2, lbTit3, lbTit4});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(lbTit5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbData5, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lbGastos.setBackground(java.awt.Color.gray);
        lbGastos.setText("jLabel1");
        lbGastos.setOpaque(true);

        tableExtras.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane3.setViewportView(tableExtras);

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
                                .addComponent(lbFacturas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(1, 1, 1)
                                .addComponent(cbSelInvoices, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lbGastos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(1, 1, 1)
                                .addComponent(btAddGasto, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbFacturas, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbSelInvoices, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbGastos, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btAddGasto))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addComponent(jScrollPane3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btAddGasto, cbSelInvoices, lbFacturas, lbGastos});

    }// </editor-fold>//GEN-END:initComponents

    private void cbSelInvoicesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbSelInvoicesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbSelInvoicesActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btAddGasto;
    private javax.swing.JButton btCloseCiclo;
    private javax.swing.JButton btNewCiclo;
    private javax.swing.JButton btRefresh;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox<String> cbSelInvoices;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lbData1;
    private javax.swing.JLabel lbData2;
    private javax.swing.JLabel lbData3;
    private javax.swing.JLabel lbData4;
    private javax.swing.JLabel lbData5;
    private javax.swing.JLabel lbEnd;
    private javax.swing.JLabel lbFacturas;
    private javax.swing.JLabel lbGastos;
    private javax.swing.JLabel lbInit;
    private javax.swing.JLabel lbStatus;
    private javax.swing.JLabel lbTit1;
    private javax.swing.JLabel lbTit2;
    private javax.swing.JLabel lbTit3;
    private javax.swing.JLabel lbTit4;
    private javax.swing.JLabel lbTit5;
    private javax.swing.JTable tableExtras;
    private javax.swing.JTable tableInvoices;
    // End of variables declaration//GEN-END:variables

    public class BotonEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {

        private JTextField campo;
        Boolean currentValue;
        JButton button;
        protected static final String EDIT = "edit";
        private JTable tabla;
        private ActionListener acList;
        private String acCommand;

        public BotonEditor(JTable tabla, ActionListener listener, String acCommand) {
            button = new JButton();
            button.setBorderPainted(false);
            this.tabla = tabla;
            this.acList = listener;
            this.acCommand = acCommand;
            button.setActionCommand(acCommand);
            button.addActionListener(BotonEditor.this);

        }

        @Override
        public boolean isCellEditable(EventObject e) {
            if (e instanceof MouseEvent) {
                return ((MouseEvent) e).getClickCount() >= 1;
            }
            return true;
        }

        @Override
        public Object getCellEditorValue() {
            return currentValue;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            currentValue = (Boolean) value;
            return button;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            final int c = tabla.getEditingColumn();
            final int f = tabla.getEditingRow();
            if (f != -1 && c != -1) {
                int row = tabla.convertRowIndexToModel(f);
                String code = model.getValueAt(row, 1).toString();
                Invoice invoice = app.getControl().getInvoiceByCode(code);
                app.getGuiManager().reviewFacture(invoice);

            }
            try {
                fireEditingStopped();
            } catch (Exception ex) {
                System.err.println(ex.getMessage());
            }
        }
    }

    public class ButtonCellRenderer extends JButton implements TableCellRenderer {

        public ButtonCellRenderer(String text) {
            setText(text);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

            if (isSelected) {
                setForeground(Color.black);
                setBackground(table.getSelectionBackground());
                if (hasFocus) {
                    setBorder(BorderFactory.createLineBorder(Color.darkGray));
                } else {
                    setBorder(createLineBorder(Color.lightGray));
                }
            } else {
                setBackground(table.getBackground());
                setForeground(Color.black);
                setBorder(UIManager.getBorder("Table.cellBorder"));
            }
            return this;
        }
    }

}
