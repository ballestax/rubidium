package com.bacon.gui;

import com.bacon.Aplication;
import com.bacon.GUIManager;
import com.bacon.domain.Presentation;
import com.bacon.domain.Product;
import com.bacon.domain.ProductoPed;
import static com.bacon.gui.PanelPedido.TIPO_LOCAL;
import com.bacon.gui.util.MyPopupListener;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListSelectionModel;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import org.apache.commons.collections.keyvalue.MultiKey;
import org.apache.commons.collections.map.MultiValueMap;
import org.dz.MyDefaultTableModel;
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
        initComponents();
        createComponents();
    }

    private void createComponents() {

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

        icon = new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "send.png", 12, 12));
        btSend.setIcon(icon);
        btSend.setMargin(new Insets(0, 0, 0, 0));
        btSend.setText("Enviar");
        btSend.setActionCommand(AC_SEND_ORDER);
        btSend.addActionListener(this);

    }
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
                productPed.setCantidad(cantidad);
                products.add(productPed);
                double totalProd = (producto.isVariablePrice() || productPed.hasPresentation() ? price : producto.getPrice()) + productPed.getValueAdicionales();
                modelTable.addRow(new Object[]{
                    cantidad,
                    productPed,
                    totalProd,
                    totalProd * cantidad,
                    false
                });
                if (productPed.hasAdditionals()) {
                    int size = 11 * (int) Math.ceil(productPed.getAdicionales().size() / 2.0);
                    tbProducts.setRowHeight(modelTable.getRowCount() - 1, 35 + size);
                }
                int row = modelTable.getRowCount() - 1;
                modelTable.setRowEditable(row, false);
                modelTable.setCellEditable(row, 0, true);
                tbProducts.getSelectionModel().addSelectionInterval(row, row);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }

//        checkAllInventory();
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        registro1 = new org.dz.Registro();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbProducts = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        btMinus = new javax.swing.JButton();
        lbQuantity = new javax.swing.JLabel();
        btAdd = new javax.swing.JButton();
        btDelete = new javax.swing.JButton();
        btSend = new javax.swing.JButton();
        btHold = new javax.swing.JButton();
        btStay = new javax.swing.JButton();

        jLabel1.setText("jLabel1");

        tbProducts.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(tbProducts);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btMinus, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(lbQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(btAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btMinus, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbQuantity)
                    .addComponent(btAdd)
                    .addComponent(btDelete, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(33, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btAdd, btMinus, lbQuantity});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(registro1, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btStay, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btHold, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btSend, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(registro1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 282, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btSend, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btHold, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btStay, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btAdd;
    private javax.swing.JButton btDelete;
    private javax.swing.JButton btHold;
    private javax.swing.JButton btMinus;
    private javax.swing.JButton btSend;
    private javax.swing.JButton btStay;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbQuantity;
    private org.dz.Registro registro1;
    private javax.swing.JTable tbProducts;
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
            int r = tbProducts.getSelectedRow();
            if (r != -1) {
                ProductoPed pp = (ProductoPed) tbProducts.getValueAt(r, 1);
                modelTable.removeRow(r);
                boolean del = products.remove(pp);
            }
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        int row = tbProducts.getSelectedRow();
        if (row >= 0) {
            try {
                int quantity = Integer.parseInt(modelTable.getValueAt(row, 0).toString());
                lbQuantity.setText(String.valueOf(quantity));
            } catch (Exception ex) {
            }
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

    private void calcularValores() {

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

}
