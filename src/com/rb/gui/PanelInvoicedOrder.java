package com.rb.gui;

import com.rb.Aplication;
import com.rb.Configuration;
import com.rb.Utiles;
import com.rb.domain.ConfigDB;
import com.rb.domain.Cycle;
import com.rb.domain.Invoice;
import com.rb.domain.Order;
import com.rb.domain.ProductoPed;
import com.rb.gui.util.TableSelectCellRenderer;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import org.apache.commons.lang3.StringUtils;
import org.dz.ListSelection;
import org.dz.MyDefaultTableModel;
import org.dz.PanelCapturaMod;

/**
 *
 * @author lrod
 */
public class PanelInvoicedOrder extends PanelCapturaMod implements TableModelListener, ActionListener {

    private final Aplication app;
    private MyDefaultTableModel model;
    private ListSelection listaSeleccion;
    private Order order;
    private int ajusteRegistros;

    /**
     * Creates new form PanelInvoicedOrder
     */
    public PanelInvoicedOrder(Aplication app) {
        this.app = app;
        initComponents();
        createComponents();
    }

    public void setOrder(Order order) {
        this.order = order;
        loadData();
    }

    private void createComponents() {

        String[] colNames = {"Sel", "Producto", "Cantidad", "V. Unit.", "V. Total"};
        ArrayList<String> asList = new ArrayList<>(Arrays.asList(colNames));

        ProductRenderer prodRenderer = new ProductRenderer(BoxLayout.Y_AXIS);
        prodRenderer.setIconPainted(false);
        prodRenderer.setSelectionBackgroundColor(TableSelectCellRenderer.getCOLOR_CHECK());
        prodRenderer.setMarked(true);

        model = new MyDefaultTableModel(asList.toArray(), 0);
        tableProducts.setModel(model);
        tableProducts.getTableHeader().setReorderingAllowed(false);
        listaSeleccion = new ListSelection(tableProducts);

        tableProducts.setRowHeight(24);
        tableProducts.getTableHeader().addMouseListener(listaSeleccion);
        model.addTableModelListener(this);

        int[] colW = {5, 220, 20, 30, 40};

        for (int i = 0; i < tableProducts.getColumnCount(); i++) {
            tableProducts.getColumnModel().getColumn(i).setCellRenderer(new TableSelectCellRenderer(true));
            tableProducts.getColumnModel().getColumn(i).setMinWidth(colW[i]);
            tableProducts.getColumnModel().getColumn(i).setPreferredWidth(colW[i]);
        }

        tableProducts.getColumnModel().getColumn(0).setHeaderRenderer(listaSeleccion);
        tableProducts.getColumnModel().getColumn(0).setCellEditor(tableProducts.getDefaultEditor(Boolean.class));
        tableProducts.getColumnModel().getColumn(1).setCellRenderer(prodRenderer);

        btFacturar.setText("Generar Factura");
        btFacturar.setActionCommand(AC_GENERATE_INVOICE);
        btFacturar.addActionListener(this);
        
        labelInfo.setOpaque(true);
        labelInfo.setBackground(Color.yellow);

        SwingWorker sw = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                publish(calculateProximoRegistro());
                return true;
            }

            @Override
            protected void process(List chunks) {
                String registro = "0";
                for (Object chunk : chunks) {
                    registro = (String) chunk;
                }
                labelInfo.setText("<html><font>" + registro + "</font></html>");
            }

        };
        sw.execute();

    }
    private static final String AC_GENERATE_INVOICE = "AC_GENERATE_INVOICE";

    public void loadData() {
        if (order != null) {
            List<ProductoPed> products = order.getProducts();
            for (ProductoPed product : products) {
                model.addRow(new Object[]{
                    true,
                    product,
                    app.DCFORM_P.format(product.getCantidad()),
                    app.DCFORM_P.format(product.getPrecio()),
                    app.DCFORM_P.format(product.getPrecio() * product.getCantidad())
                });
                model.setRowEditable(model.getRowCount() - 1, false);

            }
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

        jScrollPane1 = new javax.swing.JScrollPane();
        tableProducts = new javax.swing.JTable();
        btFacturar = new javax.swing.JButton();
        labelInfo = new javax.swing.JLabel();

        tableProducts.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(tableProducts);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 565, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btFacturar))
                    .addComponent(labelInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 313, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btFacturar, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (AC_GENERATE_INVOICE.equals(ae.getActionCommand())) {
            JOptionPane.showMessageDialog(null, "Se generara la factura: ", "Generar factura", JOptionPane.OK_CANCEL_OPTION);
            if (app.getControl().addInvoice(createInvoice())) {
                System.out.println("Invoice creado");

            } else {
                System.out.println("Invoice no creado");
            }
        }
    }

    public Invoice createInvoice() {
        Invoice invoice = new Invoice();
        invoice.setFactura(calculateProximoRegistro());

        Cycle cycle = app.getControl().getLastCycle();

        invoice.setCiclo(cycle != null ? cycle.getId() : 0);
        invoice.setFecha(new Date());

        invoice.setTable(order.getTable());
        invoice.setIdWaitress(order.getIdWaitress());

        invoice.setValor(order.getValor());

        invoice.setProducts(order.getProducts());
        
        invoice.setDescuento(0.0);
        invoice.setValorDelivery(BigDecimal.ZERO);
        
        invoice.setIdCliente(1L);

        return invoice;

    }

    private String calculateProximoRegistro() {
        ConfigDB config = app.getControl().getConfigLocal(Configuration.PREFIX_INVOICES);
        String prefijo = config != null ? config.getValor() : "";

        //get el numero de ceros a la izquierda para formatear el numero
        int ceros = 0;
        try {
            ceros = Integer.parseInt(app.getConfiguration().getProperty("cf.zeros", "0"));
        } catch (NumberFormatException e) {
        }
//        int rows = app.getControl().contarRows("select id from invoices");
        Object maxValue = app.getControl().getMaxValue("invoices", "code");

        Integer value = 0;
        try {
            value = Integer.parseInt(StringUtils.getDigits(maxValue.toString()));
        } catch (Exception e) {
        }

        String codigo = prefijo + com.rb.Utiles.getNumeroFormateado(value + ajusteRegistros + 1, ceros);
        int existClave = app.getControl().existClave("invoices", "code", "'" + codigo + "'");

        while (existClave >= 1) {
            //Comprobar si se esta creando una clave repetida por eliminacion de registros
            //Si esta repetida ajustar el valor y guardar el ajuste para la proxima insercion
            ajusteRegistros++;
            codigo = prefijo + com.rb.Utiles.getNumeroFormateado(value + ajusteRegistros + 1, ceros);
            existClave = app.getControl().existClave("invoices", "code", "'" + codigo + "'");
        }
        return codigo;
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btFacturar;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel labelInfo;
    private javax.swing.JTable tableProducts;
    // End of variables declaration//GEN-END:variables

    @Override
    public void tableChanged(TableModelEvent e) {
        if (e.getColumn() == 0) {
            System.out.println("here:" + e.getColumn());
            int row = e.getLastRow();
            if (Boolean.valueOf(model.getValueAt(row, 0).toString())) {
                ((ProductRenderer) tableProducts.getCellRenderer(row, 1)).setBackground(Color.red);
            } else {
                ((ProductRenderer) tableProducts.getCellRenderer(row, 1)).setBackground(Color.yellow);
            }
            updateTabla();
        }

    }

    private void updateTabla() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                tableProducts.updateUI();
            }
        });
    }

    public int[] getSelectedsRows() {
        int[] sel = new int[model.getRowCount()];
        Arrays.fill(sel, -1);
        for (int i = 0; i < model.getRowCount(); i++) {
            if ((Boolean) model.getValueAt(i, 0) == true) {
                sel[i] = i;
            }
        }
        sel = Utiles.truncar(sel, 0, Integer.MAX_VALUE);
        Arrays.sort(sel);
        return sel;
    }
}
