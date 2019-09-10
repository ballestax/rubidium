/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bacon.gui;

import com.bacon.Aplication;
import com.bacon.domain.Product;
import com.bacon.domain.ProductoPed;
import com.bacon.gui.util.MyPopupListener;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import org.apache.log4j.Logger;
import org.bx.gui.MyDefaultTableModel;
import org.dz.PanelCapturaMod;
import org.dz.TextFormatter;

/**
 *
 * @author lrod
 */
public class PanelPedido extends PanelCapturaMod implements ActionListener, TableModelListener, PropertyChangeListener {

    private final Aplication app;
    private org.dzur.gui.MyListModel model;
    private MyDefaultTableModel modeloTb;
    private SpinnerNumberModel spModel;
    private DecimalFormat DCFORM_P;
    private BigDecimal totalFact;
    private String[] entregas;
    private String[] tiempos;
    private ArrayList<ProductoPed> productos;
    public static final Logger logger = Logger.getLogger(PanelPedido.class.getCanonicalName());
    private JPopupMenu popupTabla;
    private MyPopupListener popupListenerTabla;

    /**
     * Creates new form PanelPedido
     */
    public PanelPedido(Aplication app) {
        this.app = app;
        productos = new ArrayList<>();
        initComponents();
        createComponents();
    }

    private void createComponents() {

        Color color = new Color(184, 25, 2);
        Font font = new Font("Arial", 1, 18);

        DCFORM_P = (DecimalFormat) NumberFormat.getInstance();
        DCFORM_P.applyPattern("$ ###,###,###");

        lbTitle.setText("Pedido");

        btDelete.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "trash.png", 18, 18)));
        btDelete.setActionCommand(AC_DELETE_PEDIDO);
        btDelete.addActionListener(this);
        btDelete.setFocusPainted(false);

        regCelular.setLabelText("Celular:");
        regCelular.setText("3006052119");

        regDireccion.setLabelText("Direccion");
        regDireccion.setText("Calle 24 6-116");

        regDescuento.setLabelText("Des");
        regDescuento.setLabelFontSize(11);
        regSubtotal.setLabelText("Subtotal");
        regSubtotal.setEditable(false);
        regTotal.setLabelText("Total");
        regTotal.setEditable(false);

        tiempos = new String[]{"Pronto", "Especifica"};
        regTiempo.setText(tiempos);

        entregas = new String[]{"Domicilio", "Local", "Para llevar"};
        regDomicilio.setText(entregas);
        regDomicilio.setActionCommand(AC_CHANGE_DOMICILIO);
        regDomicilio.addActionListener(this);
        regDomicilio.setSelected(0);

        lbEntregas.setHorizontalAlignment(SwingConstants.RIGHT);
        lbEntregas.setFont(font);
        lbEntregas.setText(DCFORM_P.format(2000));
        lbDescuento1.setHorizontalAlignment(SwingConstants.RIGHT);
        lbDescuento1.setFont(font);
        lbDescuento1.setText("$0");

        regDescuento.setDocument(TextFormatter.getDoubleLimiter());
        regDescuento.setTextAligment(SwingConstants.CENTER);
        regDescuento.setText("0");

        regDescuento.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
            }

            @Override
            public void focusLost(FocusEvent e) {
                calcularValores();
            }
        });

        regSubtotal.setTextAligment(SwingConstants.RIGHT);
        regSubtotal.setForeground(color);
        regSubtotal.setFontCampo(font);
        regSubtotal.setText(DCFORM_P.format(0));

        regTotal.setTextAligment(SwingConstants.RIGHT);
        regTotal.setForeground(color);
        regTotal.setFontCampo(font);
        regTotal.setText(DCFORM_P.format(0));

        lbTiempos.setBorder(BorderFactory.createLineBorder(Color.darkGray, 1, true));
        lbDescuento1.setBorder(BorderFactory.createLineBorder(Color.darkGray, 1, true));
        lbEntregas.setBorder(BorderFactory.createLineBorder(Color.darkGray, 1, true));

        btConfirm.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "success.png", 10, 10)));
        btConfirm.setBackground(new Color(153, 255, 153));
        btConfirm.setMargin(new Insets(1, 1, 1, 1));
        btConfirm.setFont(new Font("Arial", 1, 11));
        btConfirm.setActionCommand(AC_CONFIRMAR_PEDIDO);
        btConfirm.addActionListener(this);
        btConfirm.setText("CONFIRMAR");

        String[] cols = {"Cant", "Producto", "Unidad", "Valor"};

        modeloTb = new MyDefaultTableModel(cols, 1);

        tbListado.setModel(modeloTb);
        tbListado.setRowHeight(60);
        tbListado.setFont(new Font("Tahoma", 0, 14));
        modeloTb.addTableModelListener(this);

        popupTabla = new JPopupMenu();
        popupListenerTabla = new MyPopupListener(popupTabla, true);
        JMenuItem item1 = new JMenuItem("Eliminar...");
        item1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int r = tbListado.getSelectedRow();
                String tb = tbListado.getValueAt(r, 0).toString();
                modeloTb.removeRow(r);
                productos.remove(r);

            }
        });
        popupTabla.add(item1);

        tbListado.addMouseListener(popupListenerTabla);

        tbListado.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 2) {
                    int rowAtPoint = tbListado.rowAtPoint(e.getPoint());
                    JOptionPane.showMessageDialog(tbListado, "Info");
                }
            }
        });

        Font fontTabla = new Font("Sans", 1, 16);

        FormatRenderer formatRenderer = new FormatRenderer(DCFORM_P);
        formatRenderer.setFont(fontTabla);
        formatRenderer.setForeground(color);
        ProductRenderer prodRenderer = new ProductRenderer(BoxLayout.Y_AXIS);

        int[] colW = new int[]{40, 220, 70, 80};
        for (int i = 0; i < colW.length; i++) {
            tbListado.getColumnModel().getColumn(i).setMinWidth(colW[i]);
            tbListado.getColumnModel().getColumn(i).setPreferredWidth(colW[i]);
        }

        spModel = new SpinnerNumberModel(1, 1, 100, 1);

        tbListado.getColumnModel()
                .getColumn(0).setCellEditor(new SpinnerEditor(spModel));
        tbListado.getColumnModel()
                .getColumn(0).setCellRenderer(new SpinnerRenderer(fontTabla));

        tbListado.getColumnModel()
                .getColumn(1).setCellRenderer(prodRenderer);
        tbListado.getColumnModel()
                .getColumn(2).setCellRenderer(formatRenderer);
        tbListado.getColumnModel()
                .getColumn(3).setCellRenderer(formatRenderer);

        ArrayList<Object[]> data = new ArrayList<>();

//        data.add(new Object[]{1, new String[]{"Doble carne", "+Queso americano", "Sin verduras"},
//            17000, 18000});
//        data.add(new Object[]{2, new String[]{"Rib 57", "", ""},
//            18000, 36000});
//        data.add(new Object[]{1, new String[]{"Chori", "", ""},
//            15000, 15000});
        populateTabla(data);

        calcularValores();
    }
    public static final String AC_DELETE_PEDIDO = "AC_DELETE_PEDIDO";
    public static final String AC_CHANGE_DOMICILIO = "AC_CHANGE_DOMICILIO";
    public static final String AC_CONFIRMAR_PEDIDO = "AC_CONFIRMAR_PEDIDO";

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println(e.getActionCommand());
        if (AC_CONFIRMAR_PEDIDO.equals(e.getActionCommand())) {
            calcularValores();
        } else if (AC_CHANGE_DOMICILIO.equals(e.getActionCommand())) {
            String dom = regDomicilio.getText();
            if (entregas[0].equals(dom)) {
                lbEntregas.setText(DCFORM_P.format(2000));
            } else {
                lbEntregas.setText(DCFORM_P.format(0));
            }
            calcularValores();
        } else if (AC_DELETE_PEDIDO.equals(e.getActionCommand())) {
            clearPedido();
        }
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        if (e.getType() == TableModelEvent.UPDATE) {
            if (e.getColumn() == 0) {
                tbListado.setValueAt(calculatePrecio(e.getLastRow()), e.getLastRow(), 3);
            }
        } else if (e.getType() == TableModelEvent.INSERT) {
            tbListado.setValueAt(calculatePrecio(e.getLastRow()), e.getLastRow(), 3);
        }

        calcularValores();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        logger.debug("last:" + evt.getPropertyName() + ":" + evt.getPropagationId());
        if (PanelProduct2.AC_ADD_QUICK.equals(evt.getPropertyName())) {
            Product prod = (Product) evt.getNewValue();
            addProduct(prod);
        } else if (PanelCustomPedido.AC_CUSTOM_ADD.equals(evt.getPropertyName())) {
            ProductoPed prodPed = (ProductoPed) evt.getNewValue();
            int cant = (int) evt.getOldValue();
            addProductPed(prodPed, cant);

        }
    }

    private void populateTabla(ArrayList<Object[]> list) {
        SwingWorker sw = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                modeloTb.setRowCount(0);
                for (int i = 0; i < list.size(); i++) {
                    try {
                        Object[] data = list.get(i);
                        int cant = Integer.parseInt(data[1].toString());
                        Product prd = (Product) data[0];

                        modeloTb.addRow(new Object[]{
                            cant,
                            prd.getName(),
                            prd.getPrice(),
                            prd.getPrice() * cant
                        });

                        modeloTb.setRowEditable(modeloTb.getRowCount() - 1, false);
                        modeloTb.setCellEditable(modeloTb.getRowCount() - 1, 0, true);
                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                    }
                }
                return true;
            }
        };
        sw.execute();
    }

    public void addProduct(Product producto) {
        ProductoPed productoPed = new ProductoPed(producto);
        if (productos.contains(productoPed)) {
            int row = productos.indexOf(productoPed);
            int cant = Integer.valueOf(modeloTb.getValueAt(row, 0).toString());
            modeloTb.setValueAt(cant + 1, row, 0);
        } else {
            try {
                productos.add(productoPed);
                modeloTb.addRow(new Object[]{
                    1,
                    productoPed,
                    producto.getPrice(),
                    producto.getPrice()
                });

                modeloTb.setRowEditable(modeloTb.getRowCount() - 1, false);
                modeloTb.setCellEditable(modeloTb.getRowCount() - 1, 0, true);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    public void addProductPed(ProductoPed productPed, int cantidad) {
        Product producto = productPed.getProduct();
        if (productos.contains(productPed)) {
            try {
                int row = productos.indexOf(productPed);
                int cant = Integer.valueOf(modeloTb.getValueAt(row, 0).toString());
                modeloTb.setValueAt(cant + cantidad, row, 0);
            } catch (Exception e) {
            }

        } else {
            try {
                productos.add(productPed);
                double totalProd = producto.getPrice() + productPed.getValueAdicionales();
                modeloTb.addRow(new Object[]{
                    cantidad,
                    productPed,
                    totalProd,
                    totalProd
                });

                modeloTb.setRowEditable(modeloTb.getRowCount() - 1, false);
                modeloTb.setCellEditable(modeloTb.getRowCount() - 1, 0, true);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    private void clearPedido() {
        productos.clear();
        modeloTb.setRowCount(0);
        regDomicilio.setSelected(0);
        regCelular.setText("");
        regDireccion.setText("");
        regDescuento.setText("0");

        calcularValores();
    }

    private void calcularValores() {
        double subtotal = calculateTotal();
        regSubtotal.setText(DCFORM_P.format(subtotal));
        double descuento = subtotal * calcularDescuento() / 100;
        lbDescuento1.setText(DCFORM_P.format(descuento > 0 ? descuento * -1 : descuento));
        double domicilio = 0;
        try {
            domicilio = DCFORM_P.parse(lbEntregas.getText()).doubleValue();
        } catch (Exception e) {
        }

        regTotal.setText(DCFORM_P.format(subtotal + domicilio - descuento));
    }

    private double calcularDescuento() {
        double desc = 0;
        try {
            Double value = Double.parseDouble(regDescuento.getText());
            desc = value;
        } catch (NumberFormatException e) {
            System.err.println("ex.parse number: " + e.getMessage());
        }
        return desc;
    }

    private double calculateTotal() {
        int ROWS = tbListado.getRowCount();
        double total = 0;
        for (int i = 0; i < ROWS; i++) {
            double valorProductos = 0;
            try {
                Double value = Double.parseDouble(tbListado.getValueAt(i, 3).toString());
                valorProductos = value;
            } catch (Exception e) {
                System.err.println("ex.parse number: " + e.getMessage());
            }
            total += valorProductos;
        }
        totalFact = new BigDecimal(total);
        return total;
    }

    private double calculatePrecio(int row) {
        double total = 0;
        try {
            Double cant = Double.parseDouble(modeloTb.getValueAt(row, 0).toString());
            Double value = Double.parseDouble(modeloTb.getValueAt(row, 2).toString());
            total = cant * value;
        } catch (NumberFormatException e) {
            System.err.println("ex.parse number: " + e.getMessage());
        }
        return total;
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
        regCelular = new com.celectoral.Registro(BoxLayout.X_AXIS,"","", 100);
        regDireccion = new com.celectoral.Registro(BoxLayout.X_AXIS,"","",100);
        btConfirm = new javax.swing.JButton();
        btDelete = new javax.swing.JButton();
        regDescuento = new com.celectoral.Registro(BoxLayout.X_AXIS,"","");
        regTotal = new com.celectoral.Registro(BoxLayout.X_AXIS,"","",60);
        regSubtotal = new com.celectoral.Registro(BoxLayout.X_AXIS, "","",60);
        lbTiempos = new javax.swing.JLabel();
        regTiempo = new com.celectoral.Registro(BoxLayout.X_AXIS, "Tiempo",new String[1],60);
        regDomicilio = new com.celectoral.Registro(BoxLayout.X_AXIS, "Entrega",new String[1],60);
        lbDescuento1 = new javax.swing.JLabel();
        lbEntregas = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tbListado = new javax.swing.JTable();

        lbTitle.setFont(new java.awt.Font("Ubuntu", 1, 18)); // NOI18N
        lbTitle.setText("jLabel1");
        lbTitle.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        tbListado.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane2.setViewportView(tbListado);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(regDireccion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(regTiempo, javax.swing.GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE)
                                    .addComponent(regDomicilio, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(1, 1, 1)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(lbTiempos, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                                    .addComponent(lbEntregas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addComponent(btConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(regDescuento, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(regSubtotal, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbDescuento1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(regTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(12, 12, 12))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(regCelular, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lbTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 411, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(regCelular, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(regDireccion, javax.swing.GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(regSubtotal, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(regTiempo, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbTiempos, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(regDomicilio, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(regDescuento, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                    .addComponent(lbDescuento1, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbEntregas, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(regTotal, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                    .addComponent(btConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {regCelular, regDireccion});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {lbDescuento1, lbEntregas, lbTiempos, regDescuento, regDomicilio, regSubtotal, regTiempo, regTotal});

    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btConfirm;
    private javax.swing.JButton btDelete;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lbDescuento1;
    private javax.swing.JLabel lbEntregas;
    private javax.swing.JLabel lbTiempos;
    private javax.swing.JLabel lbTitle;
    private com.celectoral.Registro regCelular;
    private com.celectoral.Registro regDescuento;
    private com.celectoral.Registro regDireccion;
    private com.celectoral.Registro regDomicilio;
    private com.celectoral.Registro regSubtotal;
    private com.celectoral.Registro regTiempo;
    private com.celectoral.Registro regTotal;
    private javax.swing.JTable tbListado;
    // End of variables declaration//GEN-END:variables
}
