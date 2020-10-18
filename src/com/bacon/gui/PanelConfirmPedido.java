package com.bacon.gui;

import com.bacon.Aplication;
import com.bacon.Configuration;
import com.bacon.GUIManager;
import com.bacon.domain.Client;
import com.bacon.domain.Invoice;
import com.bacon.domain.ProductoPed;
import com.bacon.domain.Table;
import com.bacon.domain.Waiter;
import static com.bacon.gui.PanelPedido.TIPO_DOMICILIO;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 *
 * @author lrod
 */
public class PanelConfirmPedido extends javax.swing.JPanel implements ActionListener, PropertyChangeListener {

    private final Aplication app;
    private Invoice invoice;
    private Waiter waiter;
    private Table table;
    private Client client;

    /**
     * Creates new form PanelConfirmPedido
     *
     * @param app
     * @param invoice
     */
    public PanelConfirmPedido(Aplication app, Invoice invoice) {
        this.app = app;
        this.invoice = invoice;
        initComponents();
        createComponents();
        setupInvoice();

    }

    public void createComponents() {

        btPrint.setToolTipText("Imprimir");
        btPrint.setMargin(new Insets(2, 2, 2, 2));
        btPrint.setFocusPainted(false);
        btPrint.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "Printer-orange.png", 32, 32)));
        btPrint.setActionCommand(AC_PRINT_BILL);
        btPrint.addActionListener(this);

        btPrint2.setToolTipText("Guia");
        btPrint2.setMargin(new Insets(2, 2, 2, 2));
        btPrint2.setFocusPainted(false);
        btPrint2.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "email-info.png", 32, 32)));
        btPrint2.setActionCommand(AC_PRINT_GUIDE);
        btPrint2.addActionListener(this);

        btConfirm.setText("Guardar");
        btConfirm.setActionCommand(AC_SAVE_BILL);
        btConfirm.addActionListener(this);

        btAdd.setToolTipText("Agregar productos");
        btAdd.setMargin(new Insets(2, 2, 2, 2));
        btAdd.setFocusPainted(false);
        btAdd.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "add-item.png", 32, 32)));
        btAdd.setActionCommand(AC_ADD_PRODUCT);
        btAdd.addActionListener(this);

        btAnulate.setToolTipText("Anular");
        btAnulate.setMargin(new Insets(2, 2, 2, 2));
        btAnulate.setFocusPainted(false);
        btAnulate.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "cancel.png", 32, 32)));
        btAnulate.setActionCommand(AC_ANULATE_BILL);
        btAnulate.addActionListener(this);
        btAnulate.setEnabled(invoice.getStatus() != Invoice.ST_ANULADA);

//        jScrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    }
    public static final String AC_PRINT_GUIDE = "AC_PRINT_GUIDE";
    public static final String AC_ANULATE_BILL = "AC_ANULATE_BILL";
    public static final String AC_SAVE_BILL = "AC_SAVE_BILL";
    public static final String AC_PRINT_BILL = "AC_PRINT_BILL";
    public static final String AC_ADD_PRODUCT = "AC_ADD_PRODUCT";

    public void setupInvoice() {
        if (invoice != null) {

            waiter = app.getControl().getWaitersByID(invoice.getIdWaitress());
            table = app.getControl().getTableByID(invoice.getTable());

            client = app.getControl().getClient(invoice.getIdCliente().toString());

//            System.out.println(client);
            lbTitle.setText("<html>PEDIDO: <font size=+1 color=" + (invoice.getStatus() == 0 ? "blue" : "red") + ">" + invoice.getFactura() + "</font></html>");
            lbStatus.setText("<html><font size=+1 color=" + (invoice.getStatus() == 0 ? "blue" : "red") + ">" + Invoice.STATUSES[invoice.getStatus()] + "</font></html>");

            StringBuilder textInfo = new StringBuilder("<html>");
            textInfo.append("<table  width=\"100%\" cellspacing=\"10\" border=\"1\">");
            textInfo.append("<tr><td>Fecha: <font color=blue>").append(app.DF_FULL2.format(invoice.getFecha())).append("</font></td>");
            if (invoice.getTipoEntrega() == PanelPedido.TIPO_LOCAL) {
                textInfo.append("<td>Mesa: <font color=blue>").append(table != null ? table.getName() : "-").append("</font></td>");
                textInfo.append("<td>Mesero: <font color=blue>").append(waiter != null ? waiter.getName() : "-").append("</font></td>");
            } else {
                textInfo.append("<td>Cliente: <font color=blue>").append(client != null ? client.getCellphone() : "-").append("</font></td>");
                textInfo.append("<td>Direccion: <font color=blue>").append(client != null && !client.getAddresses().isEmpty() ? client.getAddresses().get(0) : "-").append("</font></td>");
            }

            lbTipo.setText("<html><font size=+1 color=green>" + PanelPedido.TIPO_PEDIDO[invoice.getTipoEntrega() - 1] + "</html>");

            textInfo.append("</tr></table></html>");
//            System.out.println(textInfo.toString());
            lbInfo.setText(textInfo.toString());

            StringBuilder str = new StringBuilder();
            str.append("<html><table width=\"600\" cellspacing=\"0\" border=\"1\">");
            str.append("<tr bgcolor=\"#A4C1FF\">");
            str.append("<td>").append("Producto").append("</td>");
//            str.append("<td>").append("Codigo").append("</td>");
            str.append("<td>").append("Cant.").append("</td>");
            str.append("<td>").append("V. Uni").append("</td>");
            str.append("<td>").append("V. total").append("</td></tr>");
            List<ProductoPed> productos = invoice.getProducts();

            double total = 0;

            for (ProductoPed product : productos) {

                int cantidad = product.getCantidad();
                double price = product.getValueAdicionales() + product.getPrecio();
                total += cantidad * price;
                str.append("<tr><td bgcolor=\"#F6FFDB\">").append((product.getPresentation() != null
                        ? (product.getProduct().getName() + " (" + product.getPresentation().getName() + ")") : product.getProduct().getName()).toUpperCase());
                str.append("<br><font size=2>").append(product.getStAdicionales()).append("</font>");
                str.append("<br>").append(product.hasExcluisones() ? "Sin: " : "").append("<font color=red size=2>").append(product.getStExclusiones()).append("</font></td>");
//                str.append("<td bgcolor=\"#FFFFFF\">").append(product.getProduct().getCode()).append("</td>");
                str.append("<td bgcolor=\"#FFFFFF\" align=\"right\">").append(app.DCFORM_P.format(cantidad)).append("</td>");
                str.append("<td bgcolor=\"#FFFFFF\" align=\"right\">").append(app.DCFORM_P.format(price)).append("</td>");
                str.append("<td bgcolor=\"#FFFFFF\" align=\"right\">").append(app.DCFORM_P.format(cantidad * price)).append("</td>");
                str.append("</tr>");
            }
            str.append("</table></html>");

            lbTable.setText(str.toString());

            if (invoice.getTipoEntrega() == TIPO_DOMICILIO) {
                BigDecimal valorDelivery = invoice.getValorDelivery();
                lbOthers.setText("<html>Domicilio: <font size=+1 color=blue>" + app.getCurrencyFormat().format(valorDelivery) + "</html>");
                total = total + valorDelivery.doubleValue();
            }

            if (invoice.getTipoEntrega() == PanelPedido.TIPO_LOCAL) {
                double serv = invoice.getValor().doubleValue() * invoice.getPorcService() / 100.0;
                lbOthers.setText("<html>Servicio voluntario: <font size=+1 color=blue>" + app.getCurrencyFormat().format(serv) + "</html>");
                total = total + serv;
            }

                lbTotal.setText("<html>Total<br><font size=+1 color=red>" + app.getCurrencyFormat().format(total) + "</html>");

            updateUI();
        }
    }

    public void updateInvoice() {
        Invoice inv = app.getControl().getInvoiceByCode(this.invoice.getFactura());
        if (inv != null) {
            this.invoice = inv;
            setupInvoice();
            btAnulate.setEnabled(invoice.getStatus() != Invoice.ST_ANULADA);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (AC_PRINT_BILL.equals(e.getActionCommand())) {
            String propPrinter = app.getConfiguration().getProperty(Configuration.PRINTER_SELECTED);
            if (propPrinter.isEmpty()) {
                GUIManager.showErrorMessage(null, "No ha seleccionado una impresora valida para imprimir", "Impresora no encontrada");
                return;
            }
            String printerName = propPrinter;
            app.getPrinterService().imprimirFactura(invoice, printerName);
        } else if (AC_SAVE_BILL.equals(e.getActionCommand())) {
//            app.getControl().addInvoice(invoice);
        } else if (AC_ADD_PRODUCT.equals(e.getActionCommand())) {
            app.getGuiManager().showPanelAddProduct(this);
        } else if (AC_ANULATE_BILL.equals(e.getActionCommand())) {
            anularFactura();

        } else if (AC_PRINT_GUIDE.equals(e.getActionCommand())) {
            String propPrinter = app.getConfiguration().getProperty(Configuration.PRINTER_SELECTED);
            if (propPrinter.isEmpty()) {
                GUIManager.showErrorMessage(null, "No ha seleccionado una impresora valida para imprimir", "Impresora no encontrada");
                return;
            }
            String printerName = propPrinter;
            app.getPrinterService().imprimirGuide(invoice, printerName);
        }

    }

    public void anularFactura() {
//        String fact = invoice.getFactura();
//        Invoice inv = app.getControl().getInvoiceByCode(fact);
        StringBuilder msg = new StringBuilder();
        msg.append("<html>Esta seguro que desea anular la factura N° ");
        msg.append("<font color=blue>").append(invoice.getFactura());
        msg.append(" </font> del ");
        msg.append("<font color=blue>").append(app.DF_FULL2.format(invoice.getFecha())).append("</font>");
        msg.append("<p>Por valor de: ").append("<font color=blue>").append(app.getDCFORM_P().format(invoice.getValor())).append("</font></p></html>");
        msg.append("</html>");
        int opt = JOptionPane.showConfirmDialog(null, msg, "Advertencia", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
        if (opt == JOptionPane.OK_OPTION) {
            invoice.setStatus(Invoice.ST_ANULADA);
            app.getControl().updateInvoice(invoice);
            updateInvoice();
            List<ProductoPed> list = invoice.getProducts();
            app.getControl().restoreInventory(list);
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

        lbTitle = new javax.swing.JLabel();
        lbTotal = new javax.swing.JLabel();
        lbInfo = new javax.swing.JLabel();
        btConfirm = new javax.swing.JButton();
        btPrint = new javax.swing.JButton();
        btAdd = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        lbTable = new javax.swing.JLabel();
        lbOthers = new javax.swing.JLabel();
        lbTipo = new javax.swing.JLabel();
        lbStatus = new javax.swing.JLabel();
        btAnulate = new javax.swing.JButton();
        btPrint2 = new javax.swing.JButton();

        lbTitle.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lbTotal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbTotal.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lbInfo.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btPrintActionPerformed(evt);
            }
        });

        lbTable.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jScrollPane1.setViewportView(lbTable);

        lbOthers.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lbTipo.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbTipo.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lbStatus.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbStatus.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbOthers, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 249, Short.MAX_VALUE)
                                .addComponent(btAnulate, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(btPrint2, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btPrint, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(lbTotal, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lbTitle, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lbInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(1, 1, 1)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lbStatus, javax.swing.GroupLayout.DEFAULT_SIZE, 166, Short.MAX_VALUE)
                            .addComponent(lbTipo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lbTitle, javax.swing.GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE)
                    .addComponent(lbStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbTipo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbTotal, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbOthers, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btAnulate, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
                    .addComponent(btPrint2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btPrint, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btConfirm, javax.swing.GroupLayout.DEFAULT_SIZE, 39, Short.MAX_VALUE)
                    .addComponent(btAdd, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btAnulate, btConfirm, btPrint});

    }// </editor-fold>//GEN-END:initComponents

    private void btPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btPrintActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btPrintActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btAdd;
    private javax.swing.JButton btAnulate;
    private javax.swing.JButton btConfirm;
    private javax.swing.JButton btPrint;
    private javax.swing.JButton btPrint2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbInfo;
    private javax.swing.JLabel lbOthers;
    private javax.swing.JLabel lbStatus;
    private javax.swing.JLabel lbTable;
    private javax.swing.JLabel lbTipo;
    private javax.swing.JLabel lbTitle;
    private javax.swing.JLabel lbTotal;
    // End of variables declaration//GEN-END:variables

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
