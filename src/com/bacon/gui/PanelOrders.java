package com.bacon.gui;

import com.bacon.Aplication;
import com.bacon.Configuration;
import com.bacon.GUIManager;
import com.bacon.domain.ConfigDB;
import com.bacon.domain.Cycle;
import com.bacon.domain.Ingredient;
import com.bacon.domain.Item;
import com.bacon.domain.Order;
import com.bacon.domain.Permission;
import com.bacon.domain.Presentation;
import com.bacon.domain.Product;
import com.bacon.domain.ProductoPed;
import com.bacon.domain.Table;
import com.bacon.domain.Waiter;
import static com.bacon.gui.PanelPedido.TIPO_LOCAL;
import com.bacon.gui.util.MyPopupListener;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListSelectionModel;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;
import org.apache.commons.collections.keyvalue.MultiKey;
import org.apache.commons.collections.map.MultiValueMap;
import org.dz.MyDefaultTableModel;
import org.dz.MyDialogEsc;
import org.dz.PanelCapturaMod;

/**
 *
 * @author lrod
 */
public class PanelOrders extends PanelCapturaMod implements
        ActionListener, ListSelectionListener, TableModelListener, PropertyChangeListener {

    private final Aplication app;
    private MyDefaultTableModel modelTable;
    private JPopupMenu popupTable;
    private MyPopupListener popupListenerTable;
    private JMenuItem itemDelete;
    private SpinnerNumberModel spModel;
    private DecimalFormat DCFORM_P;

    private List<ProductoPed> products;
    private Map<Integer, Map> productsOld;
    private HashMap<Long, Object[]> checkInventory;
    private MultiValueMap mapInventory;
    private boolean block;
    private CardLayout cardLayout;
    private JPanel pnContenTab1;
    private ProductoPed product;
    private JPanel pnIngredients;
    private JPanel pnCoccion;
    private JPanel pnCooking;
    private int tipo;
    private BigDecimal totalOrder;
    private Table selTable;
    private ImageIcon iconCancel;
    private ImageIcon iconBack;
    private boolean mod = false;
    private Order order;

    /**
     * Creates new form PanelOrders
     *
     * @param app
     */
    public PanelOrders(Aplication app) {
        this.app = app;
        products = new ArrayList<>();
        productsOld = new HashMap<>();
        checkInventory = new HashMap<>();
        mapInventory = new MultiValueMap();
        tipo = TIPO_LOCAL;
        initComponents();
        createComponents();
    }

    private void createComponents() {
        UIDefaults defaults = UIManager.getLookAndFeelDefaults();
        if (defaults.get("Table.alternateRowColor") == null) {
            defaults.put("Table.alternateRowColor", new Color(240, 240, 240));
        }
        lbInfo.setBorder(BorderFactory.createEtchedBorder());

        Font font = new Font("Arial", 1, 18);

        String[] cols = {"Cant", "Producto", "Unidad", "V. Total", "LLevar"};
        DCFORM_P = (DecimalFormat) NumberFormat.getInstance();
        DCFORM_P.applyPattern("$ ###,###,###");
        Color color = new Color(184, 25, 2);

        modelTable = new MyDefaultTableModel(cols, 0);

        tbProducts.setModel(modelTable);

        tbProducts.getTableHeader().setReorderingAllowed(false);

        int height = 35; // + (showExclusions ? 15 : 0);        
        tbProducts.setRowHeight(height);
        tbProducts.setFont(new Font("Tahoma", 0, 14));
        modelTable.addTableModelListener(this);

        popupTable = new JPopupMenu();
        popupListenerTable = new MyPopupListener(popupTable, true);
        itemDelete = new JMenuItem("Eliminar...");
        itemDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int r = tbProducts.getSelectedRow();
                ProductoPed pp = (ProductoPed) tbProducts.getValueAt(r, 1);
                modelTable.removeRow(r);
//                boolean del = products.remove(pp);
            }
        });
        popupTable.add(itemDelete);

        Font fontTabla = new Font("Sans", 1, 16);

        FormatRenderer formatRenderer = new FormatRenderer(DCFORM_P);
        formatRenderer.setFont(fontTabla);
        formatRenderer.setForeground(color);
        ProductRenderer prodRenderer = new ProductRenderer(BoxLayout.Y_AXIS);

        int[] colW = new int[]{40, 220, 70, 80, 30};
        for (int i = 0; i < colW.length; i++) {
            tbProducts.getColumnModel().getColumn(i).setMinWidth(colW[i]);
            tbProducts.getColumnModel().getColumn(i).setPreferredWidth(colW[i]);
        }

        spModel = new SpinnerNumberModel(1, 1, 100, 1);
        tbProducts.getColumnModel().getColumn(0).setCellEditor(new SpinnerEditor(spModel));
        tbProducts.getColumnModel().getColumn(0).setCellRenderer(new SpinnerRenderer(fontTabla));
        tbProducts.getColumnModel().getColumn(1).setCellRenderer(prodRenderer);
        tbProducts.getColumnModel().getColumn(2).setCellRenderer(formatRenderer);
        tbProducts.getColumnModel().getColumn(3).setCellRenderer(formatRenderer);
        tbProducts.getColumnModel().getColumn(4).setCellRenderer(new CheckCellRenderer());
        tbProducts.getColumnModel().getColumn(4).setCellEditor(tbProducts.getDefaultEditor(Boolean.class));

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                tbProducts.clearSelection();
            }
        };
        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        tbProducts.registerKeyboardAction(actionListener, stroke, JComponent.WHEN_FOCUSED);

        ListSelectionModel selectionModel = new DefaultListSelectionModel();
        selectionModel.addListSelectionListener(this);

        tbProducts.setSelectionModel(selectionModel);

        Font fontBtns = new Font("Sans", 1, 14);
        btAdd.setText("+");
        btAdd.setFocusPainted(false);
        btAdd.setActionCommand(AC_ADD_QUANTITY);
        btAdd.addActionListener(this);
        btAdd.setFont(fontBtns);
        btAdd.setMargin(new Insets(0, 0, 0, 0));
        btMinus.setText("-");
        btMinus.setFocusPainted(false);
        btMinus.setActionCommand(AC_MINUS_QUANTITY);
        btMinus.addActionListener(this);
        btMinus.setFont(fontBtns);
        btMinus.setMargin(new Insets(0, 0, 0, 0));
        lbQuantity.setText("1");
        lbQuantity.setFont(fontBtns.deriveFont(16f));
        lbQuantity.setForeground(Color.BLUE.darker());
        lbQuantity.setHorizontalAlignment(SwingConstants.CENTER);
        lbQuantity.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        ImageIcon icon = new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "delete.png", 12, 12));
        btDelete.setMargin(new Insets(0, 0, 0, 0));
        btDelete.setIcon(icon);
        btDelete.setActionCommand(AC_DELETE_ITEM);
        btDelete.addActionListener(this);
        btDelete.setText("Eliminar");

        icon = new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "edit.png", 12, 12));
        btModify.setMargin(new Insets(0, 0, 0, 0));
        btModify.setFocusPainted(false);
        btModify.setIcon(icon);
        btModify.setActionCommand(AC_MODIFY_ITEM);
        btModify.addActionListener(this);
        btModify.setText("Modificar");

        tgDelivery.setText("Llevar");
        tgDelivery.setMargin(new Insets(0, 0, 0, 0));
        tgDelivery.setActionCommand(AC_CHECK_DELIVERY);
        tgDelivery.addActionListener(this);
        tgDelivery.setFocusPainted(false);

        tgEntry.setText("Entrada");
        tgEntry.setMargin(new Insets(0, 0, 0, 0));
        tgEntry.setActionCommand(AC_CHECK_ENTRY);
        tgEntry.addActionListener(this);
        tgEntry.setFocusPainted(false);

        icon = new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "trash.png", 12, 12));
        btClear.setMargin(new Insets(0, 0, 0, 0));
        btClear.setIcon(icon);
        btClear.setActionCommand(AC_CLEAR_ORDER);
        btClear.addActionListener(this);

        icon = new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "file-send.png", 20, 20));
        btSend.setIcon(icon);
        btSend.setMargin(new Insets(0, 0, 0, 0));
        btSend.setText("Enviar");
        btSend.setActionCommand(AC_SEND_ORDER);
        btSend.addActionListener(this);

        icon = new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "file-warning.png", 20, 20));
        btStay.setIcon(icon);
        btStay.setMargin(new Insets(0, 0, 0, 0));
        btStay.setText("Enviar");
        btStay.setActionCommand(AC_SEND_ORDER);
        btStay.addActionListener(this);
        btStay.setVisible(false);

        iconCancel = new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "cancel.png", 20, 20));
        iconBack = new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "back.png", 20, 20));
        btCancel.setIcon(icon);
        btCancel.setMargin(new Insets(0, 0, 0, 0));
        btCancel.setText("Cancelar");
        btCancel.setActionCommand(AC_CANCEL_ORDER);
        btCancel.addActionListener(this);

        btCancelMods.setIcon(iconCancel);
        btCancelMods.setMargin(new Insets(0, 0, 0, 0));
//        btCancelMods.setText("Cancelar");
        btCancelMods.setActionCommand(AC_CANCEL_MODS);
        btCancelMods.addActionListener(this);
        btCancelMods.setVisible(false);

        icon = new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "file-pause.png", 20, 20));
        btHold.setIcon(icon);
        btHold.setMargin(new Insets(0, 0, 0, 0));
        btHold.setText("Enviar");
        btHold.setActionCommand(AC_SEND_AND_HOLD_ORDER);
        btHold.addActionListener(this);

        regSubtotal.setTextAligment(SwingConstants.RIGHT);
        regSubtotal.setForeground(color);
        regSubtotal.setFontCampo(font);
        regSubtotal.setLabelText("Subtotal");
        regSubtotal.setText(DCFORM_P.format(0));

//        iconOk = new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "package-accept.png", 18, 18));
//        iconWarning = new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "package-warning.png", 18, 18));
        ImageIcon iconDefault = new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "package-info.png", 18, 18));

        btShowInventary.setIcon(iconDefault);
        btShowInventary.setActionCommand(AC_SHOW_INVENTORY);
        btShowInventary.addActionListener(this);

        lbIndicator.setBorder(BorderFactory.createEtchedBorder());
        lbIndicator.setOpaque(true);
        lbIndicator.setVisible(false);

        font = new Font("Sans", 1, 16);
        regTable.setFontCampo(font);
        regWaiter.setFontCampo(font);
        ((JComboBox) regWaiter.getComponent()).setRenderer(new WaiterListCellRenderer());
        regWaiter.setActionCommand(AC_CHANGE_SELECTED);
        regWaiter.addActionListener(this);

        pnCardContain.add(pnContenTab, "1");

        pnCardContain.setVisible(false);

        loadDatas();

    }
    public static final String AC_SEND_AND_HOLD_ORDER = "AC_SEND_AND_HOLD_ORDER";
    public Font font;

    public void loadDatas() {
        SwingWorker sw = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                ArrayList<Waiter> waitresslList = app.getControl().getWaitresslList("status=1", "name");
                regWaiter.setText(waitresslList.toArray());

                ArrayList<Table> tablelList = app.getControl().getTableslList("", "id");
                regTable.setText(tablelList.toArray());
                return true;
            }
        };

        sw.execute();
    }

    public void setupData(Waiter waiter, Table table) {
        regWaiter.setSelected(waiter);
        regWaiter.setEditable(false);
        regTable.setSelected(table);
        regTable.setEditable(false);
        selTable = table;

        modelTable.setRowCount(0);
        mapInventory.clear();
        products.clear();
        productsOld.clear();
        block = false;

        int status = 0;
        if (table.getIdOrder() > 0) {

            lbInfo.setText("<html><font color=blue>Orden: </font><font color=blue size=+1>#" + table.getIdOrder() + "</font></html>");

            List<Order> orderslList = app.getControl().getOrderslList("id=" + table.getIdOrder(), "");
            if (orderslList.size() > 0) {
                Order order = orderslList.get(0);
                for (ProductoPed product : order.getProducts()) {
                    addProductPed(product, product.getCantidad(), product.getPrecio(), false);
                }
                status = order.getStatus();

                productsOld = copyProductsToMap(order.getProducts());
            }

            block = true;            
            btSend.setEnabled(false);
            btStay.setEnabled(false);
            btCancel.setIcon(iconBack);
            btCancel.setText("Regresar");
            btCancel.setActionCommand(AC_BACK_TO_TABLES);
            btClear.setEnabled(false);
            lbStatus.setBackground(new Color(200, 200, 220));
            lbStatus.setText("<html><font color=#22a>Pedido enviado a cocina</font></html>");

        } else {
            btCancel.setActionCommand(AC_CANCEL_ORDER);
            btCancel.setIcon(iconCancel);
            btCancel.setText("Cancelar");
            lbStatus.setBackground(null);
            lbStatus.setText("");
            btSend.setEnabled(true);
            btStay.setEnabled(true);
            btHold.setEnabled(true);
        }
    }

    public Map<Integer, Map> copyProductsToMap(List<ProductoPed> list) {
        Map map = new HashMap<Integer, Map>();
        list.stream().forEach(prod -> {
            Map value = new HashMap();
            value.put("cant", prod.getCantidad());
            value.put("prod", prod);
            map.put(prod.hashCode(), value);
        });

        return map;
    }

    public static final String AC_CHECK_DELIVERY = "AC_CHECK_DELIVERY";
    public static final String AC_MODIFY_ITEM = "AC_MODIFY_ITEM";

    public static final String AC_CLEAR_ORDER = "AC_CLEAR_ORDER";
    public static final String AC_SEND_ORDER = "AC_SEND_ORDER";
    public static final String AC_CANCEL_ORDER = "AC_CANCEL_ORDER";
    public static final String AC_CANCEL_MODS = "AC_CANCEL_MODS";
    public static final String AC_DELETE_ITEM = "AC_DELETE_ITEM";
    public static final String AC_MINUS_QUANTITY = "AC_MINUS_QUANTITY";
    public static final String AC_ADD_QUANTITY = "AC_ADD_QUANTITY";

    public void addProduct(Product product, double price, Presentation pres) {
        ProductoPed productPed = new ProductoPed(product);
        productPed.setPresentation(pres);
        productPed.setPrecio(price);
        productPed.setTermino("");
        productPed.setEspecificaciones("");
        addProductPed(productPed, 1, price);
    }

    public void addProductPed(ProductoPed productPed, int cantidad, double price) {
        addProductPed(productPed, cantidad, price, -1, true);
    }

    public void addProductPed(ProductoPed productPed, int cantidad, double price, boolean edit) {
        addProductPed(productPed, cantidad, price, -1, edit);
    }

    public void addProductPed(ProductoPed productPed, int cantidad, double price, int rowSelected, boolean edit) {
        if (block & !edit) {
            GUIManager.showErrorMessage(null, "El pedido esta cerrado no se puede agregar más productos", "Pedido cerrado");
            return;
        }

        Product producto = productPed.getProduct();

        if (productPed.hasPresentation()) {
            HashMap<Integer, HashMap> mapData = app.getControl().checkInventory(productPed.getPresentation().getId());
            productPed.setData(mapData);
            if (mapData != null && !mapData.isEmpty()) {
                Set<Integer> keys = mapData.keySet();
                for (Integer key : keys) {
                    HashMap data = mapData.get(key);
                    double res = Double.valueOf(data.get("quantity").toString()) * cantidad;
                    MultiKey mKey = new MultiKey(data.get("id"), productPed.hashCode());
                    mapInventory.put(mKey, res);
                }
            }
            checkInventory();
        } else {
            HashMap<Integer, HashMap> mapData = app.getControl().checkInventoryProduct(productPed.getProduct().getId());
            productPed.setData(mapData);
            if (mapData != null && !mapData.isEmpty()) {
                Set<Integer> keys = mapData.keySet();
                for (Integer key : keys) {
                    HashMap data = mapData.get(key);
                    double res = Double.valueOf(data.get("quantity").toString()) * cantidad;
                    MultiKey mKey = new MultiKey(data.get("id"), productPed.hashCode());
                    mapInventory.put(mKey, res);
                }
            }
            checkInventory();
        }

        tbProducts.getSelectionModel().clearSelection();

        //si el pedido esta bloqueado contar como una adicion posterior
        if (block) {
            productPed.setStatus(ProductoPed.ST_MOD_ADD_CANT);
            mod = true;
            btSend.setEnabled(true);
            btHold.setEnabled(true);
        }

        if (products.contains(productPed) && price == productPed.getPrecio()) {
            try {
                if (rowSelected != -1) {
                    modelTable.removeRow(rowSelected);
                }
                int row = products.indexOf(productPed);
                int cant = Integer.valueOf(modelTable.getValueAt(row, 0).toString());
                modelTable.setValueAt(cant + cantidad, row, 0);
                productPed.setCantidad(cantidad);
                products.set(row, productPed);
                tbProducts.setValueAt(productPed, row, 1);
                tbProducts.getSelectionModel().addSelectionInterval(row, row);
            } catch (Exception e) {
            }
        } else {
            try {
                if (rowSelected != -1) {
                    modelTable.removeRow(rowSelected);
                }
                productPed.setCantidad(cantidad);
                products.add(productPed);
                double totalProd = (producto.isVariablePrice() || productPed.hasPresentation() ? price : producto.getPrice()) + productPed.getValueAdicionales();
                modelTable.addRow(new Object[]{
                    cantidad,
                    productPed,
                    totalProd,
                    totalProd * cantidad,
                    productPed.isDelivery()
                });

                int row = modelTable.getRowCount() - 1;
                modelTable.setRowEditable(row, false);
                modelTable.setCellEditable(row, 0, edit);
                modelTable.setCellEditable(row, 4, edit);
                tbProducts.getSelectionModel().addSelectionInterval(row, row);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
            if (block) {
                productPed.setStatus(ProductoPed.ST_NEW_ADD);
                btCancelMods.setVisible(false);
                btDelete.setEnabled(true);
            }
        }

//        checkAllInventory();
    }

    public static void updateRowHeights(int column, int width, JTable table) {
        for (int row = 0; row < table.getRowCount(); row++) {
            int rowHeight = table.getRowHeight();
            Component comp = table.prepareRenderer(table.getCellRenderer(row, column), row, column);
            Dimension d = comp.getPreferredSize();
            // first set the size to the new width
            comp.setSize(new Dimension(width, d.height));
            // then get the preferred size
            d = comp.getPreferredSize();
            rowHeight = Math.max(rowHeight, d.height);
            // finally set the height of the table
            table.setRowHeight(row, rowHeight);
        }
    }

    private boolean verifyOrder() {
        try {
            tbProducts.getCellEditor().stopCellEditing();
        } catch (Exception e) {
        }

        //checkCiclo
        Cycle lastCycle = app.getControl().getLastCycle();
        if (lastCycle.getStatus() == Cycle.CLOSED) {
            GUIManager.showErrorMessage(null, "El ciclo: " + lastCycle.getId() + " esta cerrado\n"
                    + "Empiece un nuevo ciclo para facturar", "Ciclo cerrado");
            return false;
        }

        if (products.isEmpty()) {
            GUIManager.showErrorMessage(null, "No hay productos en la lista", "Pedido vacio");
            return false;
        }

        if (!checkAllInventory()) {
            ConfigDB config = app.getControl().getConfig(Configuration.INVOICE_OUT_STOCK);
            String property = config != null ? config.getValor() : "false";
            boolean permit = Boolean.valueOf(property);
            GUIManager.showErrorMessage(null, "Los productos exceden las existencias en inventario.\n"
                    + "Esta " + (permit ? "habilitado" : "deshabilitado") + " facturar sin existencias", "Producto agotado");
            if (!permit) {
                return false;
            }
        }

        Waiter waitres = (Waiter) regWaiter.getSelectedItem();
        Table table = (Table) regTable.getSelectedItem();
        boolean validate = true;
        if (TIPO_LOCAL == tipo) {
            if (waitres == null || regTable.getSelected() < 0) {
                regWaiter.setBorderToError();
                validate = false;
            }
            if (table == null) {
                regTable.setBorderToError();
                validate = false;
            }
        }

        if (!validate) {
            return false;
        }

        verifyQuantitys();

        Order order = new Order();

        Cycle cycle = app.getControl().getLastCycle();

        order.setCiclo(cycle != null ? cycle.getId() : 0);
        order.setFecha(new Date());

        if (waitres != null) {
            order.setIdWaitress(waitres.getId());
        }
        if (table != null) {
            order.setTable(table.getId());
        }

        order.setIdClient(1L);
//        }

        String tipoEntrega = PanelPedido.ENTREGA_LOCAL;

        order.setDeliveryType(PanelPedido.TIPO_LOCAL);
        for (int i = 0; i < products.size(); i++) {
            ProductoPed get = products.get(i);
        }

        order.setConsecutive(generateConsecutive(PanelPedido.TIPO_LOCAL));

        order.setProducts(products);

        order.setValor(totalOrder);

        this.order = order;
        long idOrder = sendOrder(order);

        if (idOrder > 0) {
            selTable.setStatus(Table.TABLE_ST_PEDIDO_EN_COCINA);
            pcs.firePropertyChange(PanelTakeOrders.AC_UPDATE_TABLE, idOrder, selTable);
        }

        return true;

    }

    private synchronized boolean checkAllInventory() {

        HashMap<Integer, Double> simpInv = checkInventory();
        Set<Integer> keys = simpInv.keySet();

        boolean band;

        for (Integer next : keys) {
            Item item = app.getControl().getItemWhere("id=" + next);
            Double cant = simpInv.get(next);
            band = item.getQuantity() < cant;
            if (tipo != TIPO_LOCAL && band) {
                return false;
            } else if (!item.isOnlyDelivery() && band) {
                return false;
            }
        }
        return true;
    }

    public Order getOrder() {
        String mesa = "";
        String mesero = "";

        Waiter waitres = (Waiter) regWaiter.getSelectedItem();
        Table table = (Table) regTable.getSelectedItem();
        boolean validate = true;

        if (!validate) {
//            return false;
        }

//        celular = regCelular.getText();
//        direccion = regDireccion.getText();
        verifyQuantitys();

        Order order = new Order();

        Cycle cycle = app.getControl().getLastCycle();

        order.setCiclo(cycle != null ? cycle.getId() : 0);
        order.setFecha(new Date());

        if (waitres != null) {
            order.setIdWaitress(waitres.getId());
        }
        if (table != null) {
            order.setTable(table.getId());
        }

//        if (!celular.isEmpty()) {
//            Client client = new Client(celular);
//            client.addAddress(direccion);
//
//            int existClave = app.getControl().existClave("clients", "cellphone", celular);
//
//            if (existClave > 0) {
//                app.getControl().updateClient(client);
//            } else {
//                app.getControl().addClient(client);
//            }
//
//            invoice.setIdCliente(Long.parseLong(celular));
//        } else {
        order.setIdClient(1L);
//        }

        String tipoEntrega = PanelPedido.ENTREGA_LOCAL;

        order.setDeliveryType(PanelPedido.TIPO_LOCAL);
        for (int i = 0; i < products.size(); i++) {
            ProductoPed get = products.get(i);
        }

        order.setConsecutive(generateConsecutive(PanelPedido.TIPO_LOCAL));

        order.setProducts(products);

        order.setValor(totalOrder);
        return order;
    }

    private void verifyQuantitys() {
        for (int i = 0; i < products.size(); i++) {
            ProductoPed pp = products.get(i);
            int cant = (int) modelTable.getValueAt(i, 0);
            if (pp.getCantidad() != cant) {
                products.get(i).setCantidad(cant);
            }
        }
    }

    private HashMap<Integer, Double> checkInventory() {
        Set keySet = mapInventory.keySet();
        Iterator<MultiKey> it = keySet.iterator();
        HashMap<Integer, Double> invSimple = new HashMap<>();
        while (it.hasNext()) {
            MultiKey key = it.next();
            int id = (int) key.getKey(0);
            ArrayList<Double> vals = (ArrayList) mapInventory.get(key);
            double sum = 0;
            for (int i = 0; i < vals.size(); i++) {
                sum += vals.get(i);
            }
            if (invSimple.containsKey(id)) {
                Double val = invSimple.get(id);
                invSimple.put(id, val + sum);
            } else {
                invSimple.put(id, sum);
            }
        }
        return invSimple;
    }

    private void loadIngredients() {
        List<Ingredient> ingredients = Collections.EMPTY_LIST;
        if (product != null) {
            ingredients = app.getControl().getIngredientsByProduct(product.getProduct().getCode());
        }
        pnIngredients.removeAll();
        for (int i = 0; i < ingredients.size(); i++) {
            Ingredient ing = ingredients.get(i);
            if (ing.isOpcional()) {
                JCheckBox check = new JCheckBox("Sin " + ing.getName());
                check.setActionCommand(ing.getCode());
                check.setBorderPainted(true);
                check.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createEtchedBorder(),
                        BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));
                pnIngredients.add(check);
            }
        }
    }

    private void loadCooking() {
        List<String> cookingList = Collections.EMPTY_LIST;
        if (product != null) {
            String[] cooking = {"1/2", "3/4", "BA"};
            cookingList = Arrays.asList(cooking);
        }
        pnCooking.removeAll();
        ButtonGroup btgCooks = new ButtonGroup();
        for (int i = 0; i < cookingList.size(); i++) {
            String cook = cookingList.get(i);
            JRadioButton rbCook = new JRadioButton(cook);
//                check.setActionCommand(cook);
            rbCook.setBorderPainted(true);
            rbCook.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEtchedBorder(),
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));
            btgCooks.add(rbCook);
            pnCooking.add(rbCook);

        }
    }

    public void setStatusProducts(List<ProductoPed> products, int status) {
        products.stream().forEach(prod -> prod.setStatus(status));
    }

    public List<ProductoPed> diffProducts(List<ProductoPed> products) {
        List<ProductoPed> productDiff = new ArrayList<>();
        products.stream().forEach(prod -> {
            prod.setStatus(ProductoPed.ST_SENDED_MOD);
            if (productsOld.containsKey(prod.hashCode())) {
                int cantOld = (Integer) productsOld.get(prod.hashCode()).get("cant");
                int cantNew = prod.getCantidad();
                if (cantNew > cantOld) {
                    prod.setCantidad(cantNew - cantOld);
                    productDiff.add(prod);
                }
            } else {
                productDiff.add(prod);
            }
        });
        return productDiff;
    }

    public boolean checkChanges(List<ProductoPed> products) {
        for (ProductoPed prod : products) {
            System.out.println("Analizing prod:" + prod.getProduct().getName() + "::" + prod.getCantidad());
            if (productsOld.containsKey(prod.hashCode())) {
                Map map = productsOld.get(prod.hashCode());
                System.out.println("in Old:" + ((ProductoPed) map.get("prod")).getProduct().getName());
                int cantOld = (Integer) productsOld.get(prod.hashCode()).get("cant");
                int cantNew = prod.getCantidad();
                System.out.println("Old:" + cantOld + "<>New:" + cantNew);
                System.out.println("Res:" + (cantNew != cantOld));
                if (cantNew != cantOld) {
                    return true;
                }
            } else {
                System.out.println("out Old:" + true);
                return true;
            }
        }
        System.out.println("nothing false");
        return false;
    }

    private String htmlInfoInventory(HashMap<Integer, Double> simpInv) {

        Set<Integer> keys = simpInv.keySet();

        StringBuilder stb = new StringBuilder();
        stb.append("<html><table border=1>");
        stb.append("<thead>");
        stb.append("<td>ITEM</td><td>INVENTARIO</td><td>CANTIDAD</td>");
        stb.append("</thead>");
        stb.append("<tbody>");
        for (Integer next : keys) {
            Item item = app.getControl().getItemWhere("id=" + next);
            Double cant = (item.isOnlyDelivery() && tipo == TIPO_LOCAL) ? 0 : simpInv.get(next);
            String color = (item.isOnlyDelivery() && tipo == TIPO_LOCAL) ? "#ff30a5" : (item.getQuantity() >= cant) ? "#00ff32" : "#ff4400";
            stb.append("<tr>")
                    .append("<td><font size=+1 color=").append(color).append(">").append(item.getName().toUpperCase()).append("</font></td>")
                    .append("<td><font size=+1> ").append(item.getQuantity()).append("</font></td>")
                    .append("<td><font size=+1 color=blue>").append(cant).append("</font></td>")
                    .append("</tr>");
        }
        stb.append("</tbody>");
        stb.append("</table></html>");

        return stb.toString();
    }

    public void checkBlockAndPermisions() {

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnContenTab = new javax.swing.JPanel();
        btMinus = new javax.swing.JButton();
        lbQuantity = new javax.swing.JLabel();
        btAdd = new javax.swing.JButton();
        btDelete = new javax.swing.JButton();
        btModify = new javax.swing.JButton();
        tgEntry = new javax.swing.JToggleButton();
        tgDelivery = new javax.swing.JToggleButton();
        btCancelMods = new javax.swing.JButton();
        regWaiter = new org.dz.Registro(BoxLayout.X_AXIS, "Mesero", new String[1], 60);
        lbInfo = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbProducts = new javax.swing.JTable();
        btSend = new javax.swing.JButton();
        btHold = new javax.swing.JButton();
        btStay = new javax.swing.JButton();
        pnTotals = new javax.swing.JPanel();
        regSubtotal = new com.bacon.gui.util.Registro(BoxLayout.X_AXIS, "","",60);
        lbStatus = new javax.swing.JLabel();
        regTable = new org.dz.Registro(BoxLayout.X_AXIS, "Mesa", new String[1], 60);
        pnCardContain = new javax.swing.JPanel();
        btClear = new javax.swing.JButton();
        btShowInventary = new javax.swing.JButton();
        lbIndicator = new javax.swing.JLabel();
        btCancel = new javax.swing.JButton();

        pnContenTab.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout pnContenTabLayout = new javax.swing.GroupLayout(pnContenTab);
        pnContenTab.setLayout(pnContenTabLayout);
        pnContenTabLayout.setHorizontalGroup(
            pnContenTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnContenTabLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(btMinus, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(lbQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(btAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btCancelMods, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                .addComponent(tgDelivery, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tgEntry, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btModify, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pnContenTabLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btAdd, btCancelMods});

        pnContenTabLayout.setVerticalGroup(
            pnContenTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnContenTabLayout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(pnContenTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btMinus, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbQuantity)
                    .addComponent(btAdd)
                    .addComponent(btDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btModify, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tgEntry, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tgDelivery, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btCancelMods, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(30, Short.MAX_VALUE))
        );

        pnContenTabLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btAdd, btCancelMods, btDelete, btMinus, lbQuantity});

        tbProducts.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(tbProducts);

        regSubtotal.setMinimumSize(new java.awt.Dimension(160, 31));
        regSubtotal.setPreferredSize(new java.awt.Dimension(160, 31));

        lbStatus.setOpaque(true);

        javax.swing.GroupLayout pnTotalsLayout = new javax.swing.GroupLayout(pnTotals);
        pnTotals.setLayout(pnTotalsLayout);
        pnTotalsLayout.setHorizontalGroup(
            pnTotalsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnTotalsLayout.createSequentialGroup()
                .addComponent(lbStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(regSubtotal, javax.swing.GroupLayout.DEFAULT_SIZE, 345, Short.MAX_VALUE))
        );
        pnTotalsLayout.setVerticalGroup(
            pnTotalsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnTotalsLayout.createSequentialGroup()
                .addGroup(pnTotalsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(regSubtotal, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pnCardContain.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnCardContain.setLayout(new java.awt.CardLayout());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(pnTotals, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btStay, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btHold, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btSend, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1))
            .addGroup(layout.createSequentialGroup()
                .addComponent(lbInfo, javax.swing.GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(regWaiter, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(regTable, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btShowInventary, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btClear, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(pnCardContain, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(lbIndicator, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lbInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(regWaiter, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(regTable, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btClear, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btShowInventary, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2)
                .addComponent(lbIndicator, javax.swing.GroupLayout.PREFERRED_SIZE, 7, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE)
                .addGap(2, 2, 2)
                .addComponent(pnTotals, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnCardContain, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btSend, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btHold, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btStay, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btClear, btShowInventary, lbInfo, regTable, regWaiter});

    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btAdd;
    private javax.swing.JButton btCancel;
    private javax.swing.JButton btCancelMods;
    private javax.swing.JButton btClear;
    private javax.swing.JButton btDelete;
    private javax.swing.JButton btHold;
    private javax.swing.JButton btMinus;
    private javax.swing.JButton btModify;
    private javax.swing.JButton btSend;
    private javax.swing.JButton btShowInventary;
    private javax.swing.JButton btStay;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbIndicator;
    private javax.swing.JLabel lbInfo;
    private javax.swing.JLabel lbQuantity;
    private javax.swing.JLabel lbStatus;
    private javax.swing.JPanel pnCardContain;
    private javax.swing.JPanel pnContenTab;
    private javax.swing.JPanel pnTotals;
    private com.bacon.gui.util.Registro regSubtotal;
    private org.dz.Registro regTable;
    private org.dz.Registro regWaiter;
    private javax.swing.JTable tbProducts;
    private javax.swing.JToggleButton tgDelivery;
    private javax.swing.JToggleButton tgEntry;
    // End of variables declaration//GEN-END:variables

    @Override
    public void actionPerformed(ActionEvent e) {
        if (AC_ADD_QUANTITY.equals(e.getActionCommand())) {
            try {
                int quantity = Integer.parseInt(lbQuantity.getText());
                ProductoPed productSelected = getProductSelected();
                int row = tbProducts.getSelectedRow();
                int status = productSelected.getStatus();
                if (status == ProductoPed.ST_SENDED
                        || status == ProductoPed.ST_MOD_MIN_CANT
                        || status == ProductoPed.ST_MOD_ADD_CANT) {

                    Map value = productsOld.get(productSelected.hashCode());
                    if (value != null) {
                        Integer cant = (Integer) value.get("cant");
                        if (cant == quantity + 1) {
                            productSelected.setStatus(ProductoPed.ST_SENDED);
                            tbProducts.setValueAt(productSelected, row, 1);
                            btCancelMods.setVisible(false);
                        } else {
                            productSelected.setStatus(ProductoPed.ST_MOD_ADD_CANT);
                            tbProducts.setValueAt(productSelected, row, 1);
                            btCancelMods.setVisible(true);
                            enableSends(true);
                        }
                    }
                }
                if (quantity < 100) {
                    quantity++;
                    lbQuantity.setText(String.valueOf(quantity));
                    tbProducts.setValueAt(quantity, row, 0);
                    enableSends(checkChanges(products));
                    btMinus.setEnabled(true);
                }
                if (quantity == 100) {
                    btAdd.setEnabled(false);
                }
            } catch (Exception ex) {
            }
        } else if (AC_MINUS_QUANTITY.equals(e.getActionCommand())) {
            try {
                int quantity = Integer.parseInt(lbQuantity.getText());
                ProductoPed productSelected = getProductSelected();

                int row = tbProducts.getSelectedRow();
                int status = productSelected.getStatus();
                if (status == ProductoPed.ST_SENDED
                        || status == ProductoPed.ST_MOD_MIN_CANT
                        || status == ProductoPed.ST_MOD_ADD_CANT) {
                    Map value = productsOld.get(productSelected.hashCode());
                    if (value != null) {
                        Integer cant = (Integer) value.get("cant");
                        if (cant == quantity - 1) {
                            productSelected.setStatus(ProductoPed.ST_SENDED);
                            tbProducts.setValueAt(productSelected, row, 1);
                            btCancelMods.setVisible(false);
                        } else {
                            productSelected.setStatus(ProductoPed.ST_MOD_MIN_CANT);
                            tbProducts.setValueAt(productSelected, row, 1);
                            btCancelMods.setVisible(true);
                            enableSends(true);
                        }
                    }
                }
                if (quantity > 1) {
                    quantity--;
                    lbQuantity.setText(String.valueOf(quantity));
                    tbProducts.setValueAt(quantity, row, 0);
                    enableSends(checkChanges(products));
                    btAdd.setEnabled(true);
                }
                if (quantity == 1) {
                    btMinus.setEnabled(false);
                }

            } catch (Exception ex) {
            }
        } else if (AC_DELETE_ITEM.equals(e.getActionCommand())) {
            IntStream.of(tbProducts.getSelectedRows())
                    .boxed()
                    .sorted(Collections.reverseOrder())
                    .map(tbProducts::convertRowIndexToModel)
                    .forEach(modelTable::removeRow);
            if (tbProducts.getRowCount() > 0) {
                enableSends(checkChanges(products));
            }
        } else if (AC_CANCEL_ITEM.equals(e.getActionCommand())) {
            ProductoPed productSelected = getProductSelected();
            productSelected.setStatus(ProductoPed.ST_AVOIDED);
            int row = tbProducts.getSelectedRow();
            tbProducts.setValueAt(productSelected, row, 1);
            
        } else if (AC_SEND_ORDER.equals(e.getActionCommand())) {
            if (verifyOrder()) {
                app.getGuiManager().getPanelTakeOrders().showTables();
                disableButtonInSend();
                lbStatus.setBackground(new Color(200, 200, 220));
                lbStatus.setText("<html><font color=#22a>Pedido enviado a cocina</font></html>");
            }

        } else if (AC_SEND_AND_HOLD_ORDER.equals(e.getActionCommand())) {
            if (verifyOrder()) {
                disableButtonInSend();
                lbStatus.setBackground(new Color(200, 200, 220));
                lbStatus.setText("<html><font color=#22a>Pedido enviado a cocina</font></html>");
            }
        } else if (AC_CLEAR_ORDER.equals(e.getActionCommand())) {
            modelTable.setRowCount(0);
            mapInventory.clear();
            products.clear();
        } else if (AC_MODIFY_ITEM.equals(e.getActionCommand())) {
            ProductoPed productSelected = getProductSelected();
            int row = tbProducts.getSelectedRow();
            if (productSelected != null) {
                app.getGuiManager().showCustomPedido(productSelected, this, row);
            }
            enableSends(checkChanges(products));
        } else if (AC_SHOW_INVENTORY.equals(e.getActionCommand())) {
            HashMap<Integer, Double> inventory = checkInventory();
            if (!inventory.isEmpty()) {
                String htmlInv = htmlInfoInventory(inventory);
                MyDialogEsc dialog = new MyDialogEsc(app.getGuiManager().getFrame());
                dialog.add(new JLabel(htmlInv));
                dialog.pack();
                dialog.setLocationRelativeTo(null);
                dialog.setVisible(true);
            }
        } else if (AC_CHECK_DELIVERY.equals(e.getActionCommand())) {
            int[] selectedRows = tbProducts.getSelectedRows();
            if (selectedRows.length == 1) {
                int row = selectedRows[0];
                boolean check = Boolean.parseBoolean(modelTable.getValueAt(row, 4).toString());
                ProductoPed prod = (ProductoPed) modelTable.getValueAt(row, 1);
                prod.setDelivery(!check);
                modelTable.setValueAt(!check, selectedRows[0], 4);
            } else {
                for (int row : selectedRows) {
                    boolean check = Boolean.parseBoolean(modelTable.getValueAt(row, 4).toString());
                    if (!check) {
                        ProductoPed prod = (ProductoPed) modelTable.getValueAt(row, 1);
                        prod.setDelivery(!check);
                        modelTable.setValueAt(true, row, 4);
                    }
                }
            }
        } else if (AC_CHECK_ENTRY.equals(e.getActionCommand())) {
            int[] selectedRows = tbProducts.getSelectedRows();
            if (selectedRows.length == 1) {
                int row = selectedRows[0];
                int cRow = tbProducts.convertRowIndexToModel(row);
                ProductoPed prod = (ProductoPed) modelTable.getValueAt(cRow, 1);
                prod.setEntry(tgEntry.isSelected());
                modelTable.setValueAt(prod, cRow, 1);
            }
        } else if (AC_CHANGE_SELECTED.equals(e.getActionCommand())) {
            Waiter waitres = (Waiter) regWaiter.getSelectedItem();
            Color color = Color.BLACK;
            try {
                color = Color.decode(waitres.getColor());
            } catch (Exception ex) {
            }
            regWaiter.setForeground(color);
            lbIndicator.setBackground(color);
            lbIndicator.setVisible(regWaiter.getSelected() >= 0);

        } else if (AC_CANCEL_ORDER.equals(e.getActionCommand())) {
            if (products.size() > 0) {
                String msg = "<html>El pedido no esta vacio, <br>¿Desea cancelar todo y volver al menu de seleccion de mesas?</html>";

                int showConfirmDialog = JOptionPane.showConfirmDialog(null, msg, "Advertencia", JOptionPane.YES_NO_OPTION);
                if (showConfirmDialog == JOptionPane.YES_OPTION) {
                    modelTable.setRowCount(0);
                    mapInventory.clear();
                    products.clear();
//                    app.getGuiManager().getPanelTakeOrders().showTables();
                } else {
                    return;
                }
            }
//            } else {
            app.getGuiManager().getPanelTakeOrders().showTables();
            selTable.setStatus(Table.TABLE_ST_LIMPIA);
            pcs.firePropertyChange(PanelTakeOrders.AC_CLEAR_TABLE, null, selTable);

        } else if (AC_BACK_TO_TABLES.equals(e.getActionCommand())) {
            System.out.println("back to tables");
            if (products.isEmpty()) {
                selTable.setStatus(Table.TABLE_ST_LIMPIA);
                pcs.firePropertyChange(PanelTakeOrders.AC_CLEAR_TABLE, null, selTable);
            }
            app.getGuiManager().getPanelTakeOrders().showTables();

        } else if (AC_CANCEL_MODS.equals(e.getActionCommand())) {
            int[] selectedRows = tbProducts.getSelectedRows();
            for (int row : selectedRows) {
                int cRow = tbProducts.convertRowIndexToModel(row);
                ProductoPed prodSelected = getProductSelected();
                Map value = productsOld.get(prodSelected.hashCode());
                if (value != null) {
                    Integer cant = (Integer) value.get("cant");
                    ProductoPed prod = (ProductoPed) value.get("prod");
                    prod.setStatus(ProductoPed.ST_SENDED);
                    lbQuantity.setText(String.valueOf(cant));
                    btMinus.setEnabled(cant > 1);
                    btAdd.setEnabled(cant < 100);
                    modelTable.setValueAt(cant, cRow, 0);
                    modelTable.setValueAt(prod, cRow, 1);
                    btCancelMods.setVisible(false);
                }
            }
            enableSends(checkChanges(products));
        }
    }
    public static final String AC_CANCEL_ITEM = "AC_CANCEL_ITEM";

    public void disableButtonInSend() {
        btSend.setEnabled(false);
        btHold.setEnabled(false);
        btStay.setEnabled(false);
        btCancel.setIcon(iconBack);
        btCancel.setText("Regresar");
        btCancel.setActionCommand(AC_BACK_TO_TABLES);
        btCancelMods.setVisible(false);
        disableEditProducts();
        block = true;
    }

    private void disableEditProducts() {
        int rows = modelTable.getRowCount();
        for (int row = 0; row < rows; row++) {
            modelTable.setRowEditable(row, false);
        }
    }

    private void enableSends(boolean enable) {
        btHold.setEnabled(enable);
        btSend.setEnabled(enable);
    }

    public String generateConsecutive(int type) {
        int num = 0;
        String pref = "C";
        if (type == PanelPedido.TIPO_LOCAL) {
            ConfigDB config = app.getControl().getConfig(Configuration.CONSECUTIVE_LOCAL);
            if (config != null) {
                String cons = config.getValor();
                num = Integer.parseInt(cons.substring(1));
            }
            config = app.getControl().getConfig(Configuration.PREF_CONS_LOCAL);
            if (config != null) {
                pref = config.getValor();
            }
        } else {
            ConfigDB config = app.getControl().getConfig(Configuration.CONSECUTIVE_DELIVERY);
            if (config != null) {
                String cons = config.getValor();
                num = Integer.parseInt(cons.substring(1));
            }
            config = app.getControl().getConfig(Configuration.PREF_CONS_DELIVERY);
            if (config != null) {
                pref = config.getValor();
            }
        }

        num++;
        String consecutive = pref.toUpperCase() + com.bacon.Utiles.getNumeroFormateado(num, 2);

        return consecutive;

    }

    public long sendOrder(Order order) {
        long idOrder = 0;
        if (order != null && !order.isEmpty()) {
            if (!mod) {
                setStatusProducts(order.getProducts(), ProductoPed.ST_SENDED);
                idOrder = app.getControl().addOrder(order);
                if (idOrder != 0) {
                    order.setId(idOrder);
                    MultiValueMap classify = classify(order.getProducts());

                    ConfigDB config = app.getControl().getConfig(Configuration.PRINTER_SELECTED);
                    String propPrinter = config != null ? config.getValor() : "";
                    classify.keySet().forEach(station -> {
                        List<ProductoPed> list = (List<ProductoPed>) classify.getCollection(station);
                        String nameStation = app.getControl().getStation(Integer.valueOf(station.toString()));
                        app.getPrinterService().imprimirPedidoStations(order, list, nameStation, propPrinter, false);
                    });
                } else {
                    setStatusProducts(order.getProducts(), ProductoPed.ST_NORMAL);
                }

            } else {
                idOrder = selTable.getIdOrder();
                List<ProductoPed> diffProducts = diffProducts(order.getProducts());
                MultiValueMap classify = classify(diffProducts);
                app.getControl().addProductOrder(idOrder, diffProducts);
                ConfigDB config = app.getControl().getConfig(Configuration.PRINTER_SELECTED);
                String propPrinter = config != null ? config.getValor() : "";
                classify.keySet().forEach(station -> {
                    List<ProductoPed> list = (List<ProductoPed>) classify.getCollection(station);
                    String nameStation = app.getControl().getStation(Integer.valueOf(station.toString()));
                    app.getPrinterService().imprimirPedidoStations(order, list, nameStation, propPrinter, true);
                });
            }

            productsOld = copyProductsToMap(app.getControl().getOrderProducts(idOrder));

        }
        return idOrder;
    }
    public static final String AC_BACK_TO_TABLES = "AC_BACK_TO_TABLES";
    public static final String AC_CHANGE_SELECTED = "AC_CHANGE_SELECTED";

    @Override
    public void valueChanged(ListSelectionEvent e) {
        int[] selecteds = tbProducts.getSelectedRows();
        int length = selecteds.length;
        if (length == 1) {
            int row = selecteds[0];
            if (row >= 0) {
                try {
                    int quantity = Integer.parseInt(modelTable.getValueAt(row, 0).toString());
                    boolean paraLlevar = Boolean.parseBoolean(modelTable.getValueAt(row, 4).toString());
                    lbQuantity.setText(String.valueOf(quantity));
                    product = (ProductoPed) modelTable.getValueAt(row, 1);
                    tgDelivery.setSelected(paraLlevar);
                    if (product != null) {
                        tgEntry.setSelected(product.isEntry());
                    }
                    int status = product.getStatus();
                    btCancelMods.setVisible(status == ProductoPed.ST_MOD_ADD_CANT || status == ProductoPed.ST_MOD_MIN_CANT);

                    if(product.getStatus() == ProductoPed.ST_SENDED || product.getStatus() == ProductoPed.ST_SENDED_MOD){
                        btDelete.setText("Cancelar");
                        btDelete.setActionCommand(AC_CANCEL_ITEM);
                    }
                    
                    Permission perm = app.getControl().getPermissionByName("allow-cancel-product-order");
                    btDelete.setEnabled(app.getControl().hasPermission(app.getUser(), perm) || !block || status == ProductoPed.ST_NEW_ADD);

                    perm = app.getControl().getPermissionByName("allow-modify-product-order");
                    btModify.setEnabled(app.getControl().hasPermission(app.getUser(), perm) || !block);
                    btAdd.setEnabled(app.getControl().hasPermission(app.getUser(), perm) || !block);
                    btMinus.setEnabled(app.getControl().hasPermission(app.getUser(), perm) || !block);

                    btMinus.setEnabled(quantity > 1);
                    btAdd.setEnabled(quantity < 100);

                    pnCardContain.setVisible(true);
                } catch (Exception ex) {
                    System.err.println("ex:" + ex);
                }
            }
        } else if (length > 1) {
            int sum = 0;
            for (int sel : selecteds) {
                sum += Integer.parseInt(modelTable.getValueAt(sel, 0).toString());
                lbQuantity.setText(String.valueOf(sum));
            }
            btMinus.setEnabled(false);
            btAdd.setEnabled(false);
            btModify.setEnabled(false);
        } else {
            pnCardContain.setVisible(false);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        System.out.println("evt:" + evt.getPropertyName());
        if (PanelProduct2.AC_ADD_QUICK.equals(evt.getPropertyName())) {
            Product prod = (Product) evt.getNewValue();
            Presentation pres = app.getControl().getPresentationsByDefault(prod.getId());
            addProduct(prod, prod.getPrice(), pres);
        } else if (PanelCustomPedido.AC_CUSTOM_ADD.equals(evt.getPropertyName())) {
            ProductoPed prodPed = (ProductoPed) evt.getNewValue();
            int cant = (int) ((Object[]) evt.getOldValue())[0];
            double price = (double) ((Object[]) evt.getOldValue())[1];
            if (prodPed.getPresentation() != null) {
                price = prodPed.getPresentation().getPrice();
            }
            addProductPed(prodPed, cant, price);
        } else if (PanelCustomPedido.AC_CUSTOM_MOD.equals(evt.getPropertyName())) {
            ProductoPed prodPed = (ProductoPed) evt.getNewValue();
            int cant = (int) ((Object[]) evt.getOldValue())[0];
            double price = (double) ((Object[]) evt.getOldValue())[1];
            int row = (int) ((Object[]) evt.getOldValue())[2];
            if (prodPed.getPresentation() != null) {
                price = prodPed.getPresentation().getPrice();
            }
            addProductPed(prodPed, cant, price, row, true);
        }
    }

    @Override
    public void tableChanged(TableModelEvent e) {

        switch (e.getType()) {
            case TableModelEvent.UPDATE:
                if (e.getColumn() == 0) {
                    tbProducts.setValueAt(calculatePrecio(e.getLastRow()), e.getLastRow(), 3);
                    int cant = Integer.parseInt(tbProducts.getValueAt(e.getLastRow(), 0).toString());
                    ProductoPed prd = products.get(e.getLastRow());
                    prd.setCantidad(cant);
                    tbProducts.setValueAt(prd, e.getLastRow(), 1);
                    lbQuantity.setText(String.valueOf(cant));

                    //update map inventory
                    SwingWorker sw = new SwingWorker() {
                        @Override
                        protected Object doInBackground() throws Exception {
                            HashMap<Integer, HashMap> mData = prd.getData();
                            Set<Integer> keys = mData.keySet();
                            for (Integer key : keys) {
                                HashMap data = mData.get(key);
                                double res = Double.valueOf(data.get("quantity").toString()) * cant;
                                MultiKey mKey = new MultiKey(data.get("id"), prd.hashCode());
                                mapInventory.remove(mKey);
                                mapInventory.put(mKey, res);
                            }
                            return true;
                        }
                    };
                    sw.execute();
//                    checkInventory();
                } else if (e.getColumn() == 4) {
                    boolean paraLlevar = Boolean.parseBoolean(modelTable.getValueAt(e.getLastRow(), 4).toString());
                    tgDelivery.setSelected(paraLlevar);
                }
                break;
            case TableModelEvent.INSERT:
                tbProducts.setValueAt(calculatePrecio(e.getLastRow()), e.getLastRow(), 3);
                break;
            case TableModelEvent.DELETE:
                try {
                ProductoPed rem = products.remove(e.getLastRow());
                HashMap<Integer, HashMap> mData = rem.getData();
                Set<Integer> keys = mData.keySet();
                for (Integer key : keys) {
                    HashMap data = mData.get(key);
                    MultiKey mKey = new MultiKey(data.get("id"), rem.hashCode());
                    Object remove = mapInventory.remove(mKey);
                }
                checkInventory();
            } catch (Exception ex) {
            }
            break;
            default:
                break;
        }

        calcularValores();

    }
    public static final String AC_CHECK_ENTRY = "AC_CHECK_ENTRY";
    public static final String AC_SHOW_INVENTORY = "AC_SHOW_INVENTORY";

    public ProductoPed getProductSelected() {
        int COL = 1;
        ProductoPed prod = null;
        int row = tbProducts.getSelectedRow();

        if (row >= 0) {
            prod = (ProductoPed) modelTable.getValueAt(row, COL);
        }
        return prod;
    }

    private void calcularValores() {
        double subtotal = calculateTotal();
        regSubtotal.setText(DCFORM_P.format(subtotal));
    }

    private double calculateTotal() {
        int ROWS = tbProducts.getRowCount();
        double total = 0;
        for (int i = 0; i < ROWS; i++) {
            double valorProductos = 0;
            try {
                Double value = Double.parseDouble(tbProducts.getValueAt(i, 3).toString());
                valorProductos = value;
            } catch (Exception e) {
                System.err.println("ex.parse number Total: " + e.getMessage());
            }
            total += valorProductos;
        }
        totalOrder = new BigDecimal(total);
        return total;
    }

    private double calculatePrecio(int row) {
        double total = 0;
        try {
            Double cant = Double.parseDouble(modelTable.getValueAt(row, 0).toString());
            Double value = Double.parseDouble(modelTable.getValueAt(row, 2).toString());
            total = cant * value;
        } catch (NumberFormatException e) {
            System.err.println("ex.parse number Price: " + e.getMessage());
        }
        return total;
    }

    public class CheckCellRenderer extends JComponent implements TableCellRenderer {

        private Color COLOR_CHECK = new Color(145, 100, 45);

        public void CheckCellRenderer() {
            COLOR_CHECK = new Color(100, 220, 159);

            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

            JCheckBox component = (JCheckBox) table.getDefaultRenderer(Boolean.class).
                    getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            component.setFont(new Font("sans", 0, 24));

//            if ((Boolean) value) {
            component.setBackground(row % 2 == 0 ? table.getBackground() : UIManager.getColor("Table.alternateRowColor"));
            component.setForeground(Color.white);

//                    if (hasFocus) {
//                        component.setBorder(BorderFactory.createLineBorder(Color.red));
//                    } else {
//                        component.setBorder(BorderFactory.createLineBorder(Color.ORANGE));
//                    }
//                } else {
//                    component.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.ORANGE));
//                }
            if (isSelected) {
                component.setForeground(table.getSelectionForeground());
                component.setBackground(table.getSelectionBackground());
                if (hasFocus) {
                    component.setBorder(BorderFactory.createLineBorder(Color.darkGray));
                } else {
                    component.setBorder(BorderFactory.createLineBorder(Color.lightGray));
                }
            } else {
                component.setBackground(row % 2 == 0 ? table.getBackground() : UIManager.getColor("Table.alternateRowColor"));
                component.setForeground(table.getForeground());
                component.setBorder(UIManager.getBorder("Table.cellBorder"));
            }
//        }
            return component;

        }
    }

    public MultiValueMap classify(List<ProductoPed> products) {
        MultiValueMap map = new MultiValueMap();
        for (ProductoPed prod : products) {
            String stationList = app.getControl().getProductStations(prod.getProduct().getId());
            if (!stationList.isEmpty()) {
                String[] stations = stationList.split(",");
                for (String station : stations) {
                    map.put(station, prod);
                }
            }
        }
        return map;
    }
}
