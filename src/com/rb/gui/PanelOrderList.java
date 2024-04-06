package com.rb.gui;

import com.rb.Aplication;
import com.rb.controllers.InvoiceController;
import com.rb.domain.AdditionalPed;
import com.rb.domain.Order;
import com.rb.domain.ProductoPed;
import com.rb.domain.Waiter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListSelectionModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import org.dz.MyDefaultTableModel;
import org.dz.PanelCapturaMod;
import org.dz.Resources;
import org.ocpsoft.prettytime.PrettyTime;

/**
 *
 * @author lrod
 */
public class PanelOrderList extends PanelCapturaMod implements ActionListener, ListSelectionListener {

    private final Aplication app;
    private List<Order> orderslList;
    private MyDefaultTableModel model;
    private JLabel labelInfo;
    private PrettyTime pt;
    private JButton btFactura;
    private final InvoiceController invoiceController;
    private Order order;
    private JButton btGenInvoice;

    /**
     * Creates new form PanelOrdersList
     *
     * @param app
     */
    public PanelOrderList(Aplication app) {
        this.app = app;
        invoiceController = new InvoiceController(app);
        initComponents();
        createComponents();
    }

    private void createComponents() {

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_F5) {
                    populateList();
                }
                return false;
            }
        });

        pt = new PrettyTime(new Locale("es"));

        String[] colNames = {"1", "2", "3", "4", "5", "6"};
        model = new MyDefaultTableModel(colNames, 0);
        tbOrders.setModel(model);
        tbOrders.setRowHeight(35);

        ImageIcon iconPrint = new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "Printer-orange.png", 20, 20));
        ImageIcon iconTickets = new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "tickets.png", 20, 20));
        ImageIcon iconCancel = new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "cancel.png", 20, 20));
        ImageIcon iconFacturar = new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "autoship.png", 20, 20));

        DefaultListSelectionModel selectionModel = new DefaultListSelectionModel();
        selectionModel.addListSelectionListener(this);

        tbOrders.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbOrders.setSelectionModel(selectionModel);
        tbOrders.getTableHeader().setReorderingAllowed(false);

//        TimeCellRenderer timeRenderer = new TimeCellRenderer();
        int[] colW = new int[]{50, 150, 80, 100, 50, 40};
        for (int i = 0; i < colW.length; i++) {
            tbOrders.getColumnModel().getColumn(i).setMinWidth(colW[i]);
            tbOrders.getColumnModel().getColumn(i).setPreferredWidth(colW[i]);
            tbOrders.getColumnModel().getColumn(i).setCellRenderer(new OrderCellRenderer());
        }

//        tbOrders.getColumnModel().getColumn(3).setCellRenderer(timeRenderer);
        labelInfo = new JLabel();

        panelDetail.setLayout(new BorderLayout());

        JPanel panelInfo = new JPanel(new BorderLayout());
        Box boxButtons = new Box(BoxLayout.X_AXIS);

        btGenInvoice = new JButton("Facturar");
        btGenInvoice.setIcon(iconFacturar);
        btGenInvoice.setActionCommand(AC_FACTURAR);
        btGenInvoice.addActionListener(this);

        JButton btCancelar = new JButton("Cancelar");
        btCancelar.setIcon(iconCancel);
        JButton btGuia = new JButton("Guia");
        btGuia.setIcon(iconPrint);
        btFactura = new JButton("Factura");
        btFactura.setIcon(iconPrint);

        JButton btComandas = new JButton("Comandas");
        btComandas.setIcon(iconTickets);

        JComboBox cbComandas = new JComboBox();

        boxButtons.add(btGenInvoice);
        boxButtons.add(Box.createHorizontalStrut(5));
        boxButtons.add(btCancelar);
        boxButtons.add(Box.createHorizontalStrut(5));
        boxButtons.add(btComandas);
        boxButtons.add(Box.createHorizontalStrut(5));
        boxButtons.add(btGuia);
        boxButtons.add(Box.createHorizontalStrut(5));
        boxButtons.add(btFactura);

        JScrollPane scroll = new JScrollPane();
        scroll.setViewportView(labelInfo);

        panelInfo.add(scroll, BorderLayout.CENTER);
        panelInfo.add(boxButtons, BorderLayout.SOUTH);

        panelDetail.add(panelInfo);

        splitPane.setLeftComponent(scTableOrders);
        splitPane.setRightComponent(panelDetail);

        populateList();

    }
    public static final String AC_FACTURAR = "AC_FACTURAR";

    public void populateList() {

        orderslList = app.getControl().getOrderslList("", "take_date DESC");
        model.setRowCount(0);
        PrettyTime pt = new PrettyTime(new Locale("es"));
        app.getGuiManager().setWaitCursor();
        SwingWorker sw = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                for (Order order : orderslList) {
                    model.addRow(new Object[]{
                        order.getId(),
                        order,
                        order.getValor(),
                        order.getFecha(),
                        pt.formatDuration(pt.calculatePreciseDuration(order.getFecha())),
                        order.getStatus(),
                        true
                    });
                }
                app.getGuiManager().setDefaultCursor();
                return true;
            }
        };

        sw.execute();
    }

    public void showTable(Order order) {
        if (order != null) {

            String color = "#34cdaa";
            Waiter waiter = app.getControl().getWaitressByID(order.getIdWaitress());

            StringBuilder str = new StringBuilder();
            str.append("<html><table width=\"600\" cellspacing=\"0\" border=\"1\">");
            str.append("<tr>");
            str.append("<td bgcolor=").append(color).append(">").append("ID").append("</td>");
            str.append("<td>").append(order.getId()).append("</td></tr>");
            str.append("<td bgcolor=").append(color).append(">").append("Consecutivo").append("</td>");
            str.append("<td>").append(order.getConsecutive()).append("</td></tr>");
            if (order.getDeliveryType() == PanelPedido.TIPO_LOCAL) {
                str.append("<tr><td bgcolor=").append(color).append(">").append("Mesa").append("</td>");
                str.append("<td>").append(order.getTable()).append("</td></tr>");
                str.append("<tr><td bgcolor=").append(color).append(">").append("Mesero").append("</td>");
                str.append("<td>").append(waiter.getName().toUpperCase()).append("</td></tr>");
            } else {
                str.append("<tr><td bgcolor=").append(color).append(">").append("Cliente").append("</td>");
                str.append("<td>").append(order.getIdClient()).append("</td></tr>");
            }
            str.append("<tr><td bgcolor=").append(color).append(">").append("Fecha").append("</td>");
            str.append("<td>").append(app.DF_FULL3.format(order.getFecha())).append("</td></tr>");
            str.append("<tr><td bgcolor=").append(color).append(">").append("Transcurrido").append("</td>");
            str.append("<td>").append(pt.formatDuration(pt.calculatePreciseDuration(order.getFecha()))).append("</td></tr>");
            str.append("</table>");

            str.append("<br><br><br>");

            List<ProductoPed> productos = order.getProducts();
//            StringBuilder str = new StringBuilder();
            str.append("<table width=\"600\" cellspacing=\"0\" border=\"1\">");
            str.append("<html><table width=\"600\" cellspacing=\"0\" border=\"1\">");
            str.append("<tr bgcolor=\"#A4C1FF\">");
            str.append("<td>").append("Producto").append("</td>");
//            str.append("<td>").append("Codigo").append("</td>");
            str.append("<td>").append("Cant.").append("</td>");
            str.append("<td>").append("V. Uni").append("</td>");
            str.append("<td>").append("V. total").append("</td></tr>");

            double total = 0;

            for (ProductoPed product : productos) {

                int cantidad = product.getCantidad();
                double price = product.getValueAdicionales() + product.getPrecio();
                total += cantidad * price;
                str.append("<tr><td bgcolor=\"#F6FFDB\">").append((product.getPresentation() != null
                        ? (product.getProduct().getName() + " (" + product.getPresentation().getName() + ")") : product.getProduct().getName()).toUpperCase());
                for (AdditionalPed adicional : product.getAdicionales()) {
                    str.append("<br><font color=blue size=2> +").append(adicional.getAdditional().getName())
                            .append("(x").append(adicional.getCantidad()).append(")").append("</font>");
                }

                str.append("<br>").append(product.hasExcluisones() ? "Sin: " : "").append("<font color=red size=2>").append(product.getStExclusiones()).append("</font></td>");
//                str.append("<td bgcolor=\"#FFFFFF\">").append(product.getProduct().getCode()).append("</td>");
                str.append("<td bgcolor=\"#FFFFFF\" align=\"right\">").append(app.DCFORM_P.format(cantidad)).append("</td>");
                str.append("<td bgcolor=\"#FFFFFF\" align=\"right\">").append(app.DCFORM_P.format(price)).append("</td>");
                str.append("<td bgcolor=\"#FFFFFF\" align=\"right\">").append(app.DCFORM_P.format(cantidad * price)).append("</td>");
                str.append("</tr>");
            }
            str.append("</table></html>");

            labelInfo.setText(str.toString());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (AC_FACTURAR.equals(e.getActionCommand())) {
            if (order != null) {
//                invoiceController.orderInvoice(order);
                app.getGuiManager().showPanelInvoicedOrder(this, order);
            }
        }

    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        int row = tbOrders.getSelectedRow();
        if (row < 0) {
            showTable(null);
            order = null;
        }
        try {
            order = (Order) model.getValueAt(row, 1);
            showTable(order);
        } catch (Exception ex) {
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

        panelDetail = new javax.swing.JPanel();
        scTableOrders = new javax.swing.JScrollPane();
        tbOrders = new javax.swing.JTable();
        panelTop = new javax.swing.JPanel();
        splitPane = new javax.swing.JSplitPane();
        lbStatus = new javax.swing.JLabel();

        javax.swing.GroupLayout panelDetailLayout = new javax.swing.GroupLayout(panelDetail);
        panelDetail.setLayout(panelDetailLayout);
        panelDetailLayout.setHorizontalGroup(
            panelDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        panelDetailLayout.setVerticalGroup(
            panelDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        tbOrders.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        scTableOrders.setViewportView(tbOrders);

        panelTop.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout panelTopLayout = new javax.swing.GroupLayout(panelTop);
        panelTop.setLayout(panelTopLayout);
        panelTopLayout.setHorizontalGroup(
            panelTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panelTopLayout.setVerticalGroup(
            panelTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );

        lbStatus.setText("jLabel1");
        lbStatus.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 654, Short.MAX_VALUE)
            .addComponent(panelTop, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(lbStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelTop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(splitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 381, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lbStatus;
    private javax.swing.JPanel panelDetail;
    private javax.swing.JPanel panelTop;
    private javax.swing.JScrollPane scTableOrders;
    private javax.swing.JSplitPane splitPane;
    private javax.swing.JTable tbOrders;
    // End of variables declaration//GEN-END:variables

    public class TimeCellRenderer implements TableCellRenderer {

        private BoxContain box;
        private final JLabel lbDate;
        private final JLabel lbTime;

        public DateFormat DF_TIME_FULL = new SimpleDateFormat("HH:mm:ss");

        public TimeCellRenderer() {
            box = new BoxContain(BoxLayout.X_AXIS);

            box.setOpaque(true);

            Border borde = BorderFactory.createLineBorder(Color.red);

            lbDate = new JLabel();
//            lbDate.setBorder(borde);
            lbDate.setOpaque(true);
            lbDate.setPreferredSize(new Dimension(80, 30));

            lbTime = new JLabel();
            lbTime.setOpaque(true);
//            lbTime.setBorder(borde);
            lbTime.setPreferredSize(new Dimension(80, 30));

            box.add(lbDate);
            box.add(Box.createHorizontalStrut(10));
            box.add(Box.createHorizontalGlue());
            box.add(lbTime);

        }

        private void setBackground(Color color) {
            box.setBackground(color);
            lbTime.setBackground(color);
            lbDate.setBackground(color);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

            if (value != null && value instanceof Date) {
                Date date = (Date) value;
                lbDate.setText(DF_TIME_FULL.format(date));
                PrettyTime pt = new PrettyTime(new Locale("es"));
                lbTime.setText(pt.formatDuration(pt.calculatePreciseDuration(date)));

//                Duration get = pt.calculatePreciseDuration(date).get(0);
//                long quantity = get.getQuantity();
//                TimeUnit unit = get.getUnit();                        
//                lbTime.setText(quantity+" "+unit.toString());
            }

            if (isSelected) {
                setBackground(new Color(226, 200, 230));
            } else {
                setBackground(Color.white);
            }

            return box;
        }

    }

    public class OrderCellRenderer implements TableCellRenderer {

        JLabel label;

        JButton btn;

        public OrderCellRenderer() {
            label = new JLabel();
            label.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 3));
            label.setOpaque(true);
            btn = new JButton();
            btn.setPreferredSize(new Dimension(28, 28));
            btn.setMaximumSize(new Dimension(28, 28));
            btn.setIcon(new ImageIcon(Resources.getImagen("gui/img/icons/navigate-down.png", Aplication.class, 20, 20)));
            btn.setActionCommand(AC_DISPLAY_OPTIONS);

        }
        public static final String AC_DISPLAY_OPTIONS = "AC_DISPLAY_OPTIONS";

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

            if (value != null) {
                switch (column) {
                    case 0:
                        label.setText("<html><font color=blue size=+1>#" + value + "</html>");
                        break;
                    case 1:
                        String html = "<html>Domicilio<br><font color=red>3006052119<font></html>";
                        Order order = (Order) value;
                        if (order.getDeliveryType() == PanelPedido.TIPO_LOCAL) {
                            html = "<html>Local<br><font color=red>Mesa: " + order.getTable() + "<font></html>";
                        } else if (order.getDeliveryType() == PanelPedido.TIPO_DOMICILIO) {
                            html = "<html>Domicilio<br><font color=red>" + order.getIdClient() + "<font></html>";
                        }
                        label.setText(String.valueOf(html));
                        break;
                    case 2:
                        String format = "0";
                        if (value instanceof BigDecimal) {
                            format = app.DCFORM_P.format((BigDecimal) value);
                        }
                        label.setHorizontalAlignment(SwingConstants.RIGHT);
                        label.setText(String.valueOf(format));

                        break;
                    case 3:
                        String fecha = "0";
                        if (value instanceof Date) {
                            fecha = app.DF_FULL4.format(value);
                        }
                        label.setText(fecha);
                        break;
                    case 4:
                        label.setText(String.valueOf(value));
                        label.setToolTipText(String.valueOf(value));
                        break;
                    default:
                        label.setText(String.valueOf(value));
                        break;
                }
            } else {
                label.setText("");
            }
            if (isSelected) {
                label.setBackground(new Color(226, 200, 230));
            } else {
                label.setBackground(Color.white);
            }

            if (column == 5) {
                return btn;
            }
            return label;
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
