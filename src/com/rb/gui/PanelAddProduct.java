package com.rb.gui;

import com.rb.Aplication;
import com.rb.MyConstants;
import static com.rb.MyConstants.FILTER_NUM_INT_DIFFERENT;
import static com.rb.MyConstants.FILTER_NUM_INT_GREATER_EQUAL;
import static com.rb.MyConstants.FILTER_NUM_INT_LESS;
import static com.rb.MyConstants.FILTER_NUM_INT_LESS_EQUAL;
import com.rb.domain.Presentation;
import com.rb.domain.Product;
import com.rb.domain.ProductoPed;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.EventObject;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import static javax.swing.BorderFactory.createLineBorder;
import javax.swing.DefaultListSelectionModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import org.apache.log4j.Logger;
import org.dz.MyDefaultTableModel;
import org.dz.PanelCapturaMod;


/**
 *
 * @author lrod
 */
public class PanelAddProduct extends PanelCapturaMod implements ActionListener, ListSelectionListener {

    private final Aplication app;
    private DecimalFormat decimalFormat;
    private MyDefaultTableModel model;
    private boolean mostrarAgotados;
    private TableRowSorter<MyDefaultTableModel> lastSorter;

    private ArrayList<ProductoPed> productos;

    private static final Logger log = Logger.getLogger(PanelAddProduct.class.getCanonicalName());

    /**
     * Creates new form PanelAddProduct
     * @param app
     */
    public PanelAddProduct(Aplication app, PropertyChangeListener pcl) {
        this.app = app;
        initComponents();
        createComponents();
        addPropertyChangeListener(pcl);
    }

    private void createComponents() {

        productos = new ArrayList<>();

        decimalFormat = app.getDCFORM_W();

        String[] cols = new String[]{"Codigo", "Producto", "Categoria", "Precio Unid.", "Agregar", "Agregar"};

        model = new MyDefaultTableModel(cols, 0);
        tbProducts.setModel(model);

        tbProducts.setRowHeight(22);
        int[] colW = new int[]{40, 200, 80, 80, 50, 50};
        for (int i = 0; i < colW.length; i++) {
            tbProducts.getColumnModel().getColumn(i).setMinWidth(colW[i]);
            tbProducts.getColumnModel().getColumn(i).setPreferredWidth(colW[i]);
            tbProducts.getColumnModel().getColumn(i).setCellRenderer(new TablaCellRenderer(true));
        }

        TablaCellRenderer rightRenderer = new TablaCellRenderer(true);
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        tbProducts.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);

        tbProducts.getColumnModel().getColumn(model.getColumnCount() - 1).setCellEditor(new BotonEditor(tbProducts, this, PanelProduct2.AC_ADD_QUICK));
        ImageIcon icon1 = new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "add1.png", 15, 15));
        tbProducts.getColumnModel().getColumn(model.getColumnCount() - 1).setCellRenderer(new ButtonCellRenderer("", icon1));
        tbProducts.getColumnModel().getColumn(model.getColumnCount() - 2).setCellEditor(new BotonEditor(tbProducts, this, PanelProduct2.AC_ADD_CUSTOM));
        ImageIcon icon2 = new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "process-accept.png", 15, 15));
        tbProducts.getColumnModel().getColumn(model.getColumnCount() - 2).setCellRenderer(new ButtonCellRenderer("", icon2));

        DefaultListSelectionModel selectionModel = new DefaultListSelectionModel();
        selectionModel.addListSelectionListener(this);
        selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbProducts.setSelectionModel(selectionModel);

        tbProducts.setAutoCreateRowSorter(true);

        chAgotados = new JCheckBox("Mostrar agotados");
        chAgotados.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarAgotados = chAgotados.isSelected();
                update();
            }
        });
        chAgotados.setVisible(false);

        mostrarAgotados = false;
        chAgotados.setSelected(false);

        lbFiltrar.setText("Filtrar:");

        tfFiltrar.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                filtrar(tfFiltrar.getText().toString(), 1, MyConstants.FILTER_TEXT_INT_CONTAINS);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filtrar(tfFiltrar.getText().toString(), 1, MyConstants.FILTER_TEXT_INT_CONTAINS);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filtrar(tfFiltrar.getText().toString(), 1, MyConstants.FILTER_TEXT_INT_CONTAINS);
            }
        });

        //update();
    }

    private void update() {
        model.setRowCount(0);
        loadProductsInStock();
    }

    private void loadProductsInStock() {
        log.debug("Start load products");
        SwingWorker sw = new SwingWorker<Object, Object>() {
            @Override
            protected Object doInBackground() throws Exception {

                ArrayList<Product> productsList = app.getControl().getProductsList("", "");

                int k = 0;
                for (int i = 0; i < productsList.size(); i++) {
                    Product product = productsList.get(i);

                    double precio = product.getPrice();
                    Object[] data = new Object[]{
                        product.getCode(),
                        product.getName(),
                        product.getCategory(),
                        decimalFormat.format(precio),
                        true,
                        true
                    };
                    double total = 1;

                    if (mostrarAgotados) {
                        model.insertRow(k, data);
                        model.setRowEditable(k, false);
                        k++;
                    } else if (total > 0) {
                        model.insertRow(k, data);
                        model.setRowEditable(k, false);
                        k++;
                    }
                    model.setCellEditable(k - 1, 4, true);
                    model.setCellEditable(k - 1, 5, true);
                }
                return true;
            }
        };
        sw.execute();
    }

    public void addProductPed(ProductoPed productPed, int cantidad, double price) {
        Product producto = productPed.getProduct();
        if (productos.contains(productPed) && price == productPed.getPrecio()) {
            try {
                int row = productos.indexOf(productPed);
//                int cant = Integer.valueOf(modeloTb.getValueAt(row, 0).toString());
//                modeloTb.setValueAt(cant + cantidad, row, 0);
                productPed.setCantidad(cantidad);
                productos.set(row, productPed);

            } catch (Exception e) {
            }

        } else {
            try {
                productPed.setCantidad(cantidad);
                productos.add(productPed);

                String text = lbInfo.getText();
                lbInfo.setText(text + "Se agrego: " + cantidad + " " + producto.getName() + " [" + price + "]\n");
//                double totalProd = (producto.isVariablePrice() || productPed.hasPresentation() ? price : producto.getPrice()) + productPed.getValueAdicionales();
//                modeloTb.addRow(new Object[]{
//                    cantidad,
//                    productPed,
//                    totalProd,
//                    totalProd * cantidad
//                });

//                modeloTb.setRowEditable(modeloTb.getRowCount() - 1, false);
//                modeloTb.setCellEditable(modeloTb.getRowCount() - 1, 0, true);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }

    }

    public void filtrar(final String text, final int columna, final int tFilter) {
        RowFilter<Object, Object> filterText = new RowFilter<Object, Object>() {
            @Override
            public boolean include(RowFilter.Entry entry) {
                if (text.equals("")) {
                    return true;
                }
                if (MyConstants.FILTER_TEXT_INT_START == tFilter) {
                    return entry.getStringValue(columna).startsWith(text);
                } else if (tFilter == MyConstants.FILTER_TEXT_INT_CONTAINS) {
                    System.out.println(columna + " -> contains:" + text);
                    return entry.getStringValue(columna).contains(text);
                } else {
                    return entry.getStringValue(columna).equals(text);
                }
            }
        };

        RowFilter<Object, Object> filterNum;
        filterNum = new RowFilter<Object, Object>() {
            @Override
            public boolean include(RowFilter.Entry entry) {
                int value;
                try {
                    value = Integer.parseInt(text);
                } catch (NumberFormatException e) {
                    return true;
                }
                if (tFilter == MyConstants.FILTER_NUM_INT_GREATER) {
                    return Integer.parseInt(entry.getStringValue(columna)) > value;
                } else if (tFilter == FILTER_NUM_INT_GREATER_EQUAL) {
                    return Integer.parseInt(entry.getStringValue(columna)) >= value;
                } else if (tFilter == FILTER_NUM_INT_LESS) {
                    return Integer.parseInt(entry.getStringValue(columna)) < value;
                } else if (tFilter == FILTER_NUM_INT_LESS_EQUAL) {
                    return Integer.parseInt(entry.getStringValue(columna)) <= value;
                } else if (tFilter == FILTER_NUM_INT_DIFFERENT) {
                    return Integer.parseInt(entry.getStringValue(columna)) != value;
                } else {
                    return Integer.parseInt(entry.getStringValue(columna)) == value;
                }
            }
        };

        TableRowSorter<MyDefaultTableModel> sorter = new TableRowSorter<>(model);
//        sorter.setComparator(3, new COmpara);
        if (tFilter <= 3) {
            sorter.setRowFilter(filterText);
        } else {
            sorter.setRowFilter(filterNum);
        }
        lastSorter = sorter;
        tbProducts.setRowSorter(sorter);
    }

    @Override
    public void reset() {
        productos.clear();
        tbProducts.getSelectionModel().clearSelection();
        lbDetail.setText("");
        lbInfo.setText("");
        model.setRowCount(0);
        loadProductsInStock();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (AC_UPDATE_LIST.equals(e.getActionCommand())) {
            int row = tbProducts.getSelectedRow();
            update();
            if (row >= 0) {
                try {
                    tbProducts.addRowSelectionInterval(row, row);
                } catch (Exception ex) {
                }
            }
        }
    }
    public static final String AC_UPDATE_LIST = "AC_UPDATE_LIST";

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (tbProducts.getSelectedRow() >= 0) {
            int row = tbProducts.getSelectedRow();
            Double price = Double.parseDouble(tbProducts.getValueAt(row, 3).toString());

            String color = "#26FFBC"; // "#F0E5AB";
            lbDetail.setText("<html>Producto:<font color=blue size=+1> " + tbProducts.getValueAt(row, 1).toString().toUpperCase() + "</font>"
                    + "<font color='" + color + "' size=+1> [" + decimalFormat.format(price) + "]</font>"
                    + "</html>");

            String value = tbProducts.getValueAt(row, 3).toString();

            //calcular el precio mas reptido en ventas
            double productPrecio = 100;//app.getControl().getProductPrecio("producto='" + ref + "'");

//            btnAceptar.setEnabled(!(tfCantidad.getText().isEmpty() || tfPrecioUnd.getText().isEmpty()));
        } else {
            lbDetail.setText("<html>Producto:</html>");
            setEnabled(false);
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

        lbFiltrar = new javax.swing.JLabel();
        tfFiltrar = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbProducts = new javax.swing.JTable();
        lbDetail = new javax.swing.JLabel();
        lbInfo = new javax.swing.JLabel();
        btCancel = new javax.swing.JButton();
        btSave = new javax.swing.JButton();
        chAgotados = new javax.swing.JCheckBox();

        lbFiltrar.setText("jLabel1");

        tbProducts.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(tbProducts);

        lbDetail.setText("jLabel2");
        lbDetail.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lbInfo.setText("jLabel3");
        lbInfo.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btCancel.setText("jButton2");

        btSave.setText("jButton2");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbDetail, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addGap(1, 1, 1))
                    .addComponent(lbInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btCancel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btSave))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lbFiltrar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfFiltrar, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 121, Short.MAX_VALUE)
                        .addComponent(chAgotados)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jButton1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lbFiltrar)
                            .addComponent(tfFiltrar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(chAgotados))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 181, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbDetail, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btCancel)
                    .addComponent(btSave)))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jButton1, tfFiltrar});

    }// </editor-fold>//GEN-END:initComponents

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public class TablaCellRenderer extends JLabel implements TableCellRenderer {

        boolean isBordered = true;
        private boolean agotado;

        public TablaCellRenderer(boolean isBordered) {
            super();
            this.isBordered = isBordered;
            agotado = false;
            setFont(new Font("tahoma", 0, 12));
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            int r = table.convertRowIndexToModel(row);
            if ("0.0".equals(table.getModel().getValueAt(r, 2).toString())) {
                agotado = true;
            } else {
                agotado = false;
            }

            if (value != null) {
                setText(value.toString().toUpperCase());
            }
            if (isSelected) {
                setForeground(!agotado ? Color.black : Color.red);
                setBackground(tbProducts.getSelectionBackground());
                if (hasFocus) {
                    setBorder(BorderFactory.createLineBorder(Color.darkGray));
                } else {
                    setBorder(createLineBorder(Color.lightGray));
                }
            } else {
                setBackground(tbProducts.getBackground());
                setForeground(!agotado ? Color.black : Color.red);
                setBorder(UIManager.getBorder("Table.cellBorder"));
            }
            return this;
        }
    }

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
                String code = model.getValueAt(row, 0).toString();                

                if (PanelProduct2.AC_ADD_QUICK.equals(e.getActionCommand())) {
                    Product product = app.getControl().getProductByCode(code);
                    Presentation pres = app.getControl().getPresentationsByDefault(product.getId());
                    ProductoPed productoPed = new ProductoPed(product);
                    addProductPed(productoPed, 1, pres.getPrice());
                } else if (PanelProduct2.AC_ADD_CUSTOM.equals(e.getActionCommand())) {

                }

            }
            try {
                fireEditingStopped();
            } catch (Exception ex) {
                System.err.println(ex.getMessage());
            }
        }
    }

    public class ButtonCellRenderer extends JButton implements TableCellRenderer {

        public ButtonCellRenderer(String text, Icon icon) {
            setText(text);
            if (icon != null) {
                setIcon(icon);
            }
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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btCancel;
    private javax.swing.JButton btSave;
    private javax.swing.JCheckBox chAgotados;
    private javax.swing.JButton jButton1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbDetail;
    private javax.swing.JLabel lbFiltrar;
    private javax.swing.JLabel lbInfo;
    private javax.swing.JTable tbProducts;
    private javax.swing.JTextField tfFiltrar;
    // End of variables declaration//GEN-END:variables
}
