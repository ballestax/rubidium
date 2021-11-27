package com.bacon.gui;

import com.bacon.Aplication;
import com.bacon.GUIManager;
import com.bacon.domain.Cycle;
import com.bacon.domain.Ingredient;
import com.bacon.domain.Item;
import com.bacon.domain.Order;
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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListSelectionModel;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
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

    /**
     * Creates new form PanelOrders
     *
     * @param app
     */
    public PanelOrders(Aplication app) {
        this.app = app;
        products = new ArrayList<>();
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
        btModify.setIcon(icon);
        btModify.setActionCommand(AC_MODIFY_ITEM);
        btModify.addActionListener(this);
        btModify.setText("Modificar");

        tgDelivery.setText("Llevar");
        tgDelivery.setActionCommand(AC_CHECK_DELIVERY);
        tgDelivery.addActionListener(this);

        tgEntry.setText("Entrada");
        tgEntry.setActionCommand(AC_CHECK_ENTRY);
        tgEntry.addActionListener(this);

        icon = new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "clean.png", 12, 12));
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

        icon = new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "file-pause.png", 20, 20));
        btHold.setIcon(icon);
        btHold.setMargin(new Insets(0, 0, 0, 0));
        btHold.setText("Enviar");
        btHold.setActionCommand(AC_SEND_ORDER);
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

        pnCardContain.add(pnContenTab, "1");

        pnCardContain.setVisible(false);

    }
    public static final String AC_CHECK_DELIVERY = "AC_CHECK_DELIVERY";
    public static final String AC_MODIFY_ITEM = "AC_MODIFY_ITEM";

    public static final String AC_CLEAR_ORDER = "AC_CLEAR_ORDER";
    public static final String AC_SEND_ORDER = "AC_SEND_ORDER";
    public static final String AC_DELETE_ITEM = "AC_DELETE_ITEM";
    public static final String AC_MINUS_QUANTITY = "AC_MINUS_QUANTITY";
    public static final String AC_ADD_QUANTITY = "AC_ADD_QUANTITY";

    public void addProduct(Product producto, double precio, Presentation pres) {
        ProductoPed productoPed = new ProductoPed(producto);
        productoPed.setPresentation(pres);
        productoPed.setPrecio(precio);
        addProductPed(productoPed, 1, precio);
    }

    public void addProductPed(ProductoPed productPed, int cantidad, double price) {
        addProductPed(productPed, cantidad, price, -1);
    }

    public void addProductPed(ProductoPed productPed, int cantidad, double price, int rowSelected) {
        if (block) {
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
                    Boolean.FALSE
                });
//                if (productPed.hasAdditionals()) {
//                    int size = 11 * (int) Math.ceil(productPed.getAdicionales().size() / 2.0);
//                    tbProducts.setRowHeight(modelTable.getRowCount() - 1, 35 + size);
//                }
                int row = modelTable.getRowCount() - 1;
                modelTable.setRowEditable(row, false);
                modelTable.setCellEditable(row, 0, true);
                modelTable.setCellEditable(row, 4, true);
                tbProducts.getSelectionModel().addSelectionInterval(row, row);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
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

    public Order getOrder() {
        String mesa = "";
        String mesero = "";

        Waiter waitres = new Waiter("LOCAL", 1);//(Waiter) regMesera.getSelectedItem();
        Table table = new Table("1", 1);//(Table) regMesa.getSelectedItem();
        boolean validate = true;
//        if (TIPO_LOCAL == tipo) {
//            if (waitres == null || regMesera.getSelected() < 1) {
//                regMesera.setBorderToError();
//                validate = false;
//            }
//            if (table == null) {
//                regMesa.setBorderToError();
//                validate = false;
//            }
//        } else {
//            if (regCelular.getText().isEmpty()) {
//                regCelular.setBorderToError();
//                validate = false;
//            }
//            if (regDireccion.getText().isEmpty()) {
//                regDireccion.setBorderToError();
//                validate = false;
//            }
//        }

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
        for (int i = 0; i < products.size(); i++) {
            ProductoPed get = products.get(i);
        }

        order.setProducts(products);

//        invoice.setValor(totalFact);
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
        registro1 = new org.dz.Registro();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbProducts = new javax.swing.JTable();
        btSend = new javax.swing.JButton();
        btHold = new javax.swing.JButton();
        btStay = new javax.swing.JButton();
        pnTotals = new javax.swing.JPanel();
        regSubtotal = new com.bacon.gui.util.Registro(BoxLayout.X_AXIS, "","",60);
        registro2 = new org.dz.Registro();
        pnCardContain = new javax.swing.JPanel();
        btClear = new javax.swing.JButton();
        btShowInventary = new javax.swing.JButton();

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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 72, Short.MAX_VALUE)
                .addComponent(tgDelivery, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tgEntry, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btModify, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
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
                    .addComponent(tgDelivery, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(30, Short.MAX_VALUE))
        );

        pnContenTabLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btAdd, btDelete, btMinus, lbQuantity});

        jLabel1.setText("jLabel1");

        tbProducts.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(tbProducts);

        regSubtotal.setMinimumSize(new java.awt.Dimension(160, 31));
        regSubtotal.setPreferredSize(new java.awt.Dimension(160, 31));

        javax.swing.GroupLayout pnTotalsLayout = new javax.swing.GroupLayout(pnTotals);
        pnTotals.setLayout(pnTotalsLayout);
        pnTotalsLayout.setHorizontalGroup(
            pnTotalsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnTotalsLayout.createSequentialGroup()
                .addGap(310, 310, 310)
                .addComponent(regSubtotal, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
        );
        pnTotalsLayout.setVerticalGroup(
            pnTotalsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnTotalsLayout.createSequentialGroup()
                .addComponent(regSubtotal, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btStay, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btHold, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btSend, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1))
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(registro1, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(registro2, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btShowInventary, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btClear, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(pnCardContain, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {registro1, registro2});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(registro1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(registro2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btClear, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btShowInventary, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 275, Short.MAX_VALUE)
                .addGap(2, 2, 2)
                .addComponent(pnTotals, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnCardContain, javax.swing.GroupLayout.DEFAULT_SIZE, 41, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btSend, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btHold, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btStay, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btAdd;
    private javax.swing.JButton btClear;
    private javax.swing.JButton btDelete;
    private javax.swing.JButton btHold;
    private javax.swing.JButton btMinus;
    private javax.swing.JButton btModify;
    private javax.swing.JButton btSend;
    private javax.swing.JButton btShowInventary;
    private javax.swing.JButton btStay;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbQuantity;
    private javax.swing.JPanel pnCardContain;
    private javax.swing.JPanel pnContenTab;
    private javax.swing.JPanel pnTotals;
    private com.bacon.gui.util.Registro regSubtotal;
    private org.dz.Registro registro1;
    private org.dz.Registro registro2;
    private javax.swing.JTable tbProducts;
    private javax.swing.JToggleButton tgDelivery;
    private javax.swing.JToggleButton tgEntry;
    // End of variables declaration//GEN-END:variables

    @Override
    public void actionPerformed(ActionEvent e) {
        if (AC_ADD_QUANTITY.equals(e.getActionCommand())) {
            try {
                int quantity = Integer.parseInt(lbQuantity.getText());
                if (quantity < 100) {
                    quantity++;
                    lbQuantity.setText(String.valueOf(quantity));
                    int row = tbProducts.getSelectedRow();
                    tbProducts.setValueAt(quantity, row, 0);
                }
            } catch (Exception ex) {
            }
        } else if (AC_MINUS_QUANTITY.equals(e.getActionCommand())) {
            try {
                int quantity = Integer.parseInt(lbQuantity.getText());
                if (quantity > 1) {
                    quantity--;
                    lbQuantity.setText(String.valueOf(quantity));
                    int row = tbProducts.getSelectedRow();
                    tbProducts.setValueAt(quantity, row, 0);
                }
            } catch (Exception ex) {
            }
        } else if (AC_DELETE_ITEM.equals(e.getActionCommand())) {
            IntStream.of(tbProducts.getSelectedRows())
                    .boxed()
                    .sorted(Collections.reverseOrder())
                    .map(tbProducts::convertRowIndexToModel)
                    .forEach(modelTable::removeRow);

        } else if (AC_SEND_ORDER.equals(e.getActionCommand())) {
            Order order = getOrder();
            if (order != null && !order.isEmpty()) {
                app.getControl().addOrder(getOrder());
            }
        } else if (AC_CLEAR_ORDER.equals(e.getActionCommand())) {
            modelTable.setRowCount(0);
            products.clear();
        } else if (AC_MODIFY_ITEM.equals(e.getActionCommand())) {
            ProductoPed productSelected = getProductSelected();
            int row = tbProducts.getSelectedRow();
            if (productSelected != null) {
                app.getGuiManager().showCustomPedido(productSelected, this, row);
            }
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
                modelTable.setValueAt(!check, selectedRows[0], 4);
            } else {
                for (int row : selectedRows) {
                    boolean check = Boolean.parseBoolean(modelTable.getValueAt(row, 4).toString());

                    if (!check) {
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
        }
    }

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
                    tgEntry.setSelected(product.isEntry());
                    btMinus.setEnabled(true);
                    btAdd.setEnabled(true);
                    btModify.setEnabled(true);
                    pnCardContain.setVisible(true);
                } catch (Exception ex) {
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
            addProductPed(prodPed, cant, price, row);
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
}
