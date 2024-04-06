/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rb.gui;

import com.rb.Aplication;
import com.rb.GUIManager;
import com.rb.MyConstants;
import static com.rb.MyConstants.FILTER_NUM_INT_DIFFERENT;
import static com.rb.MyConstants.FILTER_NUM_INT_GREATER_EQUAL;
import static com.rb.MyConstants.FILTER_NUM_INT_LESS;
import static com.rb.MyConstants.FILTER_NUM_INT_LESS_EQUAL;
import com.rb.domain.Product;
import com.rb.gui.util.MultiSpanCellTable;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;

import javax.swing.ImageIcon;
import javax.swing.SwingWorker;
import org.apache.log4j.Logger;
import static javax.swing.BorderFactory.createLineBorder;
import org.dz.MyDefaultTableModel;
import org.dz.PanelCaptura;
import org.dz.Registro;
import org.dz.TextFormatter;


/**
 *
 * @author ballestax
 */
public class GuiPanelSelProduct extends PanelCaptura implements ListSelectionListener, ActionListener {

    private Aplication app;
    private MyDefaultTableModel model;
    private JTable tableStock;
    private JButton btnAceptar;
    private JButton btnCancelar;
    private TableRowSorter<MyDefaultTableModel> lastSorter;
    private JTextField tfProducto;
    private JLabel lbProducto;
    private JLabel lbExistencias;
    private JTextField tfPrecioUnd;
    private JTextField tfCantidad;
    //private AttributiveCellTableModel ml;
    private MultiSpanCellTable table;
    private DecimalFormat decimalFormat;
    private Registro regPrecioUnd;
    private HashMap<Integer, String> hashIdStock;
    private boolean mostrarAgotados;
    private JCheckBox chAgotados;
    private JButton btnTransferencia;
    private static final Logger log = Logger.getLogger(GuiPanelSelProduct.class.getCanonicalName());
    private JButton btnUpdate;
    private JButton btnConciliacion;
    private JButton btAddQuick;
    private JButton btAddCustom;

    public GuiPanelSelProduct(Aplication app, PropertyChangeListener pcl) {
        this.app = app;
        initComponents();
        addPropertyChangeListener(pcl);
    }

    private void initComponents() {

        decimalFormat = app.getDCFORM_W();

        setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        JLabel lbFiltrar = new JLabel("Filtrar");
        tfProducto = new JTextField();
        String[] tipos = new String[]{"Todo", "Codigo", "Nombre"};
        JComboBox cbTipo = new JComboBox(new DefaultComboBoxModel(tipos));
        String[] cols = new String[]{"Codigo", "Producto.", "Categoria", "Precio Unid."};
        model = new MyDefaultTableModel(cols, 0);
        tableStock = new JTable(model) {

            @Override
            public Class<?> getColumnClass(int column) {
                switch (column) {
//                    case 2:
                    case 3:
                        return Double.class;
                    default:
                        return String.class;
                }
//                return super.getColumnClass(column); //To change body of generated methods, choose Tools | Templates.
            }

        };

        tableStock.setRowHeight(22);
        int[] colW = new int[]{40, 200, 80, 80};
        for (int i = 0; i < colW.length; i++) {
            tableStock.getColumnModel().getColumn(i).setMinWidth(colW[i]);
            tableStock.getColumnModel().getColumn(i).setPreferredWidth(colW[i]);
            tableStock.getColumnModel().getColumn(i).setCellRenderer(new TablaCellRenderer(true));
        }

        TablaCellRenderer rightRenderer = new TablaCellRenderer(true);
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);        
        tableStock.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);
//        tableStock.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);
//        tableStock.getColumnModel().getColumn(5).setCellRenderer(rightRenderer);
        tableStock.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        DefaultListSelectionModel selectionModel = new DefaultListSelectionModel();
        selectionModel.addListSelectionListener(this);
        tableStock.setSelectionModel(selectionModel);

        tableStock.setAutoCreateRowSorter(true);
        btnAceptar = new JButton("Aceptar");
        btnAceptar.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int row = tableStock.getSelectedRow();
                if (row != -1) {
                    double cant = Double.parseDouble(tfCantidad.getText());
                    double inv = Double.parseDouble(tableStock.getValueAt(row, 2).toString());
                    if (cant > inv) {
                        GUIManager.showErrorMessage(table, "Cantidad ingresada supera la cantidad en inventario", "Producto insuficiente");
                    } else {

//                        int idLoc = app.getControl().getLocation(loc).getId();
//
//                        ProductoInventario product = app.getControl().getProductoInventario(ref, idLoc);
//                        product.setCantidad(Double.parseDouble(tfCantidad.getText()));
//                        product.setPrecioVenta(new BigDecimal(tfPrecioUnd.getText()));
//                        pcs.firePropertyChange(Aplication.ACTION_ADD_PRODUCT_TO_TABLE, null, product);
                        getRootPane().getParent().setVisible(false);
                        reset();
                    }
                }
            }
        });
        btnAceptar.setEnabled(false);
        btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                getRootPane().getParent().setVisible(false);
            }
        });

        chAgotados = new JCheckBox("Mostrar agotados");
        chAgotados.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarAgotados = chAgotados.isSelected();
                update();
            }
        });
        chAgotados.setVisible(false);

        btnUpdate = new JButton(new ImageIcon(app.getImgManager().getImagen("gui/img/view-refresh.png", 16, 16)));
        btnUpdate.setActionCommand(AC_UPDATE_LIST);
        btnUpdate.addActionListener(this);

        btAddQuick = new JButton();
        btAddQuick.setActionCommand(PanelProduct2.AC_ADD_QUICK);
        btAddQuick.setMargin(new Insets(1, 1, 1, 1));
        btAddQuick.setFocusPainted(false);
        btAddQuick.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "add1.png", 15, 15)));
        btAddQuick.addActionListener(this);

        btAddCustom = new JButton();
        btAddCustom.setActionCommand(PanelProduct2.AC_ADD_CUSTOM);
        btAddCustom.setMargin(new Insets(1, 1, 1, 1));
        btAddCustom.setFocusPainted(false);
        btAddCustom.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "process-accept.png", 15, 15)));
        btAddCustom.addActionListener(this);

        mostrarAgotados = false;
        chAgotados.setSelected(false);

        Box boxRow1 = new Box(BoxLayout.X_AXIS);
        boxRow1.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        boxRow1.add(lbFiltrar);
        boxRow1.add(Box.createHorizontalStrut(3));
        //boxRow1.add(cbTipo);
        boxRow1.add(tfProducto);
        boxRow1.add(chAgotados);
        boxRow1.add(btnUpdate);

        tfProducto.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                filtrar(tfProducto.getText().toString(), 1, MyConstants.FILTER_TEXT_INT_CONTAINS);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filtrar(tfProducto.getText().toString(), 1, MyConstants.FILTER_TEXT_INT_CONTAINS);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filtrar(tfProducto.getText().toString(), 1, MyConstants.FILTER_TEXT_INT_CONTAINS);
            }
        }
        );
        JScrollPane scroll = new JScrollPane(tableStock);
        JPanel pnProductSelected = new JPanel();
        pnProductSelected.setBorder(BorderFactory.createLineBorder(Color.gray));
        lbProducto = new JLabel("Producto:");
        lbExistencias = new JLabel("Cantidad en inventario:");
        tfPrecioUnd = new JTextField();
        tfPrecioUnd.setDocument(TextFormatter.getDoubleLimiter());
        tfCantidad = new JTextField();
        tfCantidad.setDocument(TextFormatter.getDoubleLimiter());

        tfPrecioUnd.addCaretListener(new CaretListener() {

            @Override
            public void caretUpdate(CaretEvent e) {

                btnAceptar.setEnabled(tableStock.getSelectedRow() >= 0
                        && !(tfCantidad.getText().isEmpty() || tfPrecioUnd.getText().isEmpty()));
            }
        });

        tfCantidad.addCaretListener(new CaretListener() {

            @Override
            public void caretUpdate(CaretEvent e) {

                btnAceptar.setEnabled(tableStock.getSelectedRow() >= 0
                        && !(tfCantidad.getText().isEmpty() || tfPrecioUnd.getText().isEmpty()));
            }
        });
        Registro regCantidad = new Registro(BoxLayout.X_AXIS, "Cantidad", tfCantidad);
        regPrecioUnd = new Registro(BoxLayout.X_AXIS, "Precio Und.", tfPrecioUnd);

        Box boxRow2 = new Box(BoxLayout.X_AXIS);
        boxRow2.setBorder(BorderFactory.createLineBorder(Color.gray));
        //boxRow2.add(btnTransferencia);
        //boxRow2.add(btnConciliacion);
        boxRow2.add(Box.createHorizontalGlue());
        boxRow2.add(btnCancelar);
        boxRow2.add(Box.createHorizontalStrut(5));
        boxRow2.add(btnAceptar);

        pnProductSelected.setLayout(new GridBagLayout());
        pnProductSelected.add(lbProducto, new GridBagConstraints(0, 0, 2, 1, 0.1, 0.0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        pnProductSelected.add(lbExistencias, new GridBagConstraints(0, 1, 2, 1, 0.1, 0.0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        pnProductSelected.add(btAddQuick, new GridBagConstraints(3, 0, 1, 2, 0.1, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
        pnProductSelected.add(btAddCustom, new GridBagConstraints(4, 0, 1, 2, 0.1, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
        pnProductSelected.add(regCantidad, new GridBagConstraints(0, 2, 2, 1, 0.1, 0.0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
//        pnProductSelected.add(tfCantidad, new GridBagConstraints(1, 2, 1, 1, 0.1, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        pnProductSelected.add(regPrecioUnd, new GridBagConstraints(3, 2, 2, 1, 0.1, 0.0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
//        pnProductSelected.add(tfPrecioUnd, new GridBagConstraints(3, 2, 1, 1, 0.1, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
//        pnProductSelected.add(boxRow2, new GridBagConstraints(0, 3, 4, 1, 0.1, 0.0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(5, 2, 5, 6), 0, 0));

        hashIdStock = new HashMap<>();

        Box boxRow3 = new Box(BoxLayout.Y_AXIS);
        boxRow3.setBorder(BorderFactory.createLineBorder(Color.gray));
        boxRow3.add(pnProductSelected);
        boxRow3.add(Box.createHorizontalStrut(5));
        boxRow3.add(boxRow2);

        setLayout(new BorderLayout());
        add(boxRow1, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(boxRow3, BorderLayout.SOUTH);
        loadProductsInStock();

    }
    public static final String AC_UPDATE_LIST = "AC_UPDATE_LIST";
    public static final String AC_NEW_TRANSFERENCIA = "AC_NEW_TRANSFERENCIA";
    public static final String AC_NEW_CONCILIACION = "AC_NEW_CONCILIACION";

    private void update() {
        model.setRowCount(0);
        loadProductsInStock();
    }

    private void loadProductsInStock() {
        log.debug("Start load products");
        SwingWorker sw = new SwingWorker<Object, Object>() {
            @Override
            protected Object doInBackground() throws Exception {

                ArrayList<Product> productsList = app.getControl().getProductsList("","");

                hashIdStock.clear();
                int k = 0;
                for (int i = 0; i < productsList.size(); i++) {
                    Product product = productsList.get(i);

                    double precio = product.getPrice();
                    Object[] data = new Object[]{
                        product.getCode(),
                        product.getName(),
                        product.getCategory(),
                        decimalFormat.format(precio)
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
                }
                return true;
            }
        };

        sw.execute();

    }

    @Override
    public void reset() {
        tableStock.getSelectionModel().clearSelection();
        tfCantidad.setText("");
        tfPrecioUnd.setText("");
        tfProducto.setText("");
        model.setRowCount(0);
        loadProductsInStock();
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (tableStock.getSelectedRow() >= 0) {
            int row = tableStock.getSelectedRow();
            String price = tableStock.getValueAt(row, 3).toString();
            lbProducto.setText("<html>Producto:<font color=blue size=+1> " + tableStock.getValueAt(row, 1).toString().toUpperCase() + "</font></html>");

            String html = "<html><table border=0><tr>"; //<td colspan=8>Cantidad en inventario</td></tr><tr>";

//
//            for (int i = 0; i < cantidades.size(); i++) {
//                Object[] get = cantidades.get(i);
            String color = "#C6ECBC"; // "#F0E5AB";
            html += "<td bgcolor=" + color + ">";
            html += price;
            html += "    </td><td> </td>";
//
//            }
            html += "</tr></table></html>";
            lbExistencias.setText(html);
            String value = tableStock.getValueAt(row, 3).toString();

            //calcular el precio mas reptido en ventas
            double productPrecio = 100;//app.getControl().getProductPrecio("producto='" + ref + "'");
            tfPrecioUnd.setText(String.valueOf(productPrecio));

            btnAceptar.setEnabled(!(tfCantidad.getText().isEmpty() || tfPrecioUnd.getText().isEmpty()));

        } else {
            lbProducto.setText("<html>Producto:</html>");
            lbExistencias.setText("<html>Cantidad en inventario:</html>");
            tfPrecioUnd.setText("");

            setEnabled(false);
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
        tableStock.setRowSorter(sorter);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (AC_UPDATE_LIST.equals(e.getActionCommand())) {
            int row = tableStock.getSelectedRow();
            update();
            if (row >= 0) {
                try {
                    tableStock.addRowSelectionInterval(row, row);
                } catch (Exception ex) {
                }
            }
        }
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
                setBackground(tableStock.getSelectionBackground());
                if (hasFocus) {
                    setBorder(BorderFactory.createLineBorder(Color.darkGray));
                } else {
                    setBorder(createLineBorder(Color.lightGray));
                }
            } else {
                setBackground(tableStock.getBackground());
                setForeground(!agotado ? Color.black : Color.red);
                setBorder(UIManager.getBorder("Table.cellBorder"));
            }
            return this;
        }
    }
}
