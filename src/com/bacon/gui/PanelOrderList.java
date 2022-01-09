package com.bacon.gui;

import com.bacon.Aplication;
import com.bacon.domain.AdditionalPed;
import com.bacon.domain.Item;
import com.bacon.domain.Order;
import com.bacon.domain.ProductoPed;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import org.dz.MyDefaultTableModel;
import org.dz.Resources;
import org.ocpsoft.prettytime.PrettyTime;

/**
 *
 * @author lrod
 */
public class PanelOrderList extends javax.swing.JPanel implements ListSelectionListener {

    private final Aplication app;
    private List<Order> orderslList;
    private MyDefaultTableModel model;
    private JLabel labelInfo;

    /**
     * Creates new form PanelOrdersList
     *
     * @param app
     */
    public PanelOrderList(Aplication app) {
        this.app = app;
        initComponents();
        createComponents();
    }

    private void createComponents() {

        String[] colNames = {"1", "2", "3", "4", "5", "6"};
        model = new MyDefaultTableModel(colNames, 0);
        tbOrders.setModel(model);
        tbOrders.setRowHeight(35);

        DefaultListSelectionModel selectionModel = new DefaultListSelectionModel();
        selectionModel.addListSelectionListener(this);

        tbOrders.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbOrders.setSelectionModel(selectionModel);
        tbOrders.getTableHeader().setReorderingAllowed(false);

        TimeCellRenderer timeRenderer = new TimeCellRenderer();

        int[] colW = new int[]{50, 150, 80, 100, 50, 40};
        for (int i = 0; i < colW.length; i++) {
            tbOrders.getColumnModel().getColumn(i).setMinWidth(colW[i]);
            tbOrders.getColumnModel().getColumn(i).setPreferredWidth(colW[i]);
            tbOrders.getColumnModel().getColumn(i).setCellRenderer(new OrderCellRenderer());
        }

        tbOrders.getColumnModel().getColumn(3).setCellRenderer(timeRenderer);

        labelInfo = new JLabel();

        panelDetail.setLayout(new BorderLayout());

        JPanel panelInfo = new JPanel(new BorderLayout());
        Box boxButtons = new Box(BoxLayout.X_AXIS);
        JButton btGenInvoice = new JButton("Facturar");
        JButton btCancelar = new JButton("Cancelar");
        JButton btComandas = new JButton("Camandas");
        JButton btGuia = new JButton("Guia");
        JButton btFactura = new JButton("Factura");
        boxButtons.add(btGenInvoice);
        boxButtons.add(btCancelar);
        boxButtons.add(btComandas);
        boxButtons.add(btGuia);
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

    public void populateList() {

        orderslList = app.getControl().getOrderslList("", "");

        SwingWorker sw = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                for (Order order : orderslList) {
                    model.addRow(new Object[]{
                        order.getId(),
                        order,
                        order.getValor(),
                        order.getFecha(),
                        order.getStatus(),
                        true
                    });
                }
                return true;
            }
        };

        sw.execute();
    }

    public void showTable(Order order) {
        List<ProductoPed> productos = order.getProducts();
        StringBuilder str = new StringBuilder();
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

    @Override
    public void valueChanged(ListSelectionEvent e) {
        int row = tbOrders.getSelectedRow();
        if (row < 0) {
            showTable(null);
        }
        try {
            Order order = (Order) model.getValueAt(row, 1);
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
            lbDate.setBorder(borde);
            lbDate.setOpaque(true);
            lbDate.setPreferredSize(new Dimension(80, 30));

            lbTime = new JLabel();
            lbTime.setOpaque(true);
            lbTime.setBorder(borde);
            lbTime.setPreferredSize(new Dimension(80, 30));

            box.add(lbDate);
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
                        label.setText(String.valueOf(format));
                        break;
                    case 3:

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

}
