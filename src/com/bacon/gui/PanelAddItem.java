package com.bacon.gui;

import com.bacon.Aplication;
import com.bacon.ProgAction;
import com.bacon.Utiles;
import com.bacon.domain.Item;
import com.bacon.domain.Location;
import com.bacon.domain.Presentation;
import com.bacon.domain.Product;
import com.bacon.gui.util.TableSelectCellRenderer;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import org.apache.commons.lang3.StringUtils;
import org.dz.ListSelection;
import org.dz.MyDefaultTableModel;
import org.dz.PanelCaptura;
import org.dz.TextFormatter;
import org.xhtmlrenderer.util.Util;

/**
 *
 * @author lrod
 */
public class PanelAddItem extends PanelCaptura implements ActionListener, PropertyChangeListener, TableModelListener {

    private final Aplication app;
    private ProgAction acAddUnit, acAddLocation;
    private MyDefaultTableModel model;
    private ListSelection listaSeleccion;
    private int widthLabel;
    private long itemId;

    public static final int COL_SEL1 = 1;
    public static final int COL_SEL2 = 3;
    private ImageIcon iconEdit;
    private ImageIcon iconCancel;
    private ImageIcon iconDelete;
    private ImageIcon iconSave;
    private ImageIcon iconUpdate;

    /**
     * Creates new form PanelAddItem
     *
     * @param app
     */
    public PanelAddItem(Aplication app) {
        this.app = app;
        this.itemId = 0;
        initActions();
        initComponents();
        createComponents();

    }

    private void createComponents() {

        Font font = new Font("Sans", 1, 16);

        iconEdit = new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "edit.png", 20, 20));
        iconCancel = new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "cancel.png", 20, 20));
        iconDelete = new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "delete-icon.png", 14, 14));
        iconSave = new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "ok-icon.png", 14, 14));
        iconUpdate = new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "update.png", 14, 14));

        regQuantity.setFontCampo(font);
        regQuantity.setDocument(TextFormatter.getDoubleLimiter());

        List<String> units = app.getControl().getUnitsList("", "name").stream().map(name -> name.toUpperCase()).collect(Collectors.toList());
        regMeseure.setText(units.toArray());
        regMeseure.setFontCampo(font);

        ArrayList<Location> locations = app.getControl().getLocationList("", "name");
        regLocation.setText(locations.toArray());
        regLocation.setFontCampo(font);

        regName.setFontCampo(font);
        regStockMax.setFontCampo(font);
        regStockMax.setDocument(TextFormatter.getDoubleLimiter());
        regStockMin.setFontCampo(font);
        regStockMin.setDocument(TextFormatter.getDoubleLimiter());
        regPrice.setFontCampo(font);
        regPrice.setDocument(TextFormatter.getDoubleLimiter());
        regCost.setFontCampo(font);
        regCost.setDocument(TextFormatter.getDoubleLimiter());

        chSaveExit.setFont(new Font("Sans", 1, 10));
        chSaveExit.setText("Guardar y salir");

        btSave.setFont(new Font("Sans", 1, 11));
        btSave.setText("Guardar");
        btSave.setIcon(iconSave);
        btSave.setActionCommand(AC_SAVE_ITEM);
        btSave.addActionListener(this);

        String[] colNames = {"Sel", "ID", "Producto", "ID pres.", "Presentacion", "Cantidad"};
        ArrayList<String> asList = new ArrayList<>(Arrays.asList(colNames));

        model = new MyDefaultTableModel(asList.toArray(), 0);
        tableProducts.setModel(model);
        tableProducts.getTableHeader().setReorderingAllowed(false);
        listaSeleccion = new ListSelection(tableProducts);

        tableProducts.setRowHeight(24);
        tableProducts.getTableHeader().addMouseListener(listaSeleccion);
        model.addTableModelListener(this);

        int[] colW = {5, 20, 120, 20, 100, 20};

        for (int i = 0; i < tableProducts.getColumnCount(); i++) {
            tableProducts.getColumnModel().getColumn(i).setCellRenderer(new TableSelectCellRenderer(true));
            tableProducts.getColumnModel().getColumn(i).setMinWidth(colW[i]);
            tableProducts.getColumnModel().getColumn(i).setPreferredWidth(colW[i]);
        }

        tableProducts.getColumnModel().getColumn(0).setHeaderRenderer(listaSeleccion);
        tableProducts.getColumnModel().getColumn(0).setCellEditor(tableProducts.getDefaultEditor(Boolean.class));

        btRefresh.setActionCommand(AC_REFRESH_PRODUCTS);
        btRefresh.addActionListener(this);
        btRefresh.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "refresh.png", 16, 16)));

        btEdit.setActionCommand(AC_ALLOW_EDIT);
        btEdit.addActionListener(this);
        btEdit.setIcon(iconEdit);
        btEdit.setVisible(false);

        btDelete.setIcon(iconDelete);
        btDelete.setActionCommand(AC_DELETE_ITEM);
        btDelete.addActionListener(this);
        btDelete.setText("Eliminar");

        chOnlyDelivery.setText("Solo domicilios");
        chOnlyDelivery.setFont(font);

        chSnapShot.setText("Snapshot");
        chSnapShot.setFont(font);

        lbTitle.setText("Productos y presentaciones");
        lbTitle.setOpaque(true);
        lbTitle.setBackground(org.dz.Utiles.colorAleatorio(180, 210));

        loadProducts();

    }
    public static final String AC_DELETE_ITEM = "AC_DELETE_ITEM";
    public static final String AC_ALLOW_EDIT = "AC_ALLOW_EDIT";
    public static final String AC_REFRESH_PRODUCTS = "AC_REFRESH_PRODUCTS";
    public static final String AC_SAVE_ITEM = "AC_SAVE_ITEM";
    public static final String AC_UPDATE_ITEM = "AC_UPDATE_ITEM";

    private void loadProducts() {
        SwingWorker sw = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                model.setRowCount(0);
                ArrayList<Product> productos = app.getControl().getProductsList("", "category DESC, name");
                for (int i = 0; i < productos.size(); i++) {
                    Product p = productos.get(i);
                    ArrayList<Presentation> press = app.getControl().getPresentationsByProduct(p.getId());
                    if (!press.isEmpty()) {
                        for (int j = 0; j < press.size(); j++) {
                            model.addRow(new Object[]{false, p.getId(), p.getName().toUpperCase(), press.get(j).getId(), press.get(j).getName().toUpperCase(), 1});
                        }
                    } else {
                        model.addRow(new Object[]{false, p.getId(), p.getName().toUpperCase(), "--", "--", 1});
                    }
                    model.setRowEditable(model.getRowCount() - 1, false);
                    model.setCellEditable(model.getRowCount() - 1, 0, true);
                    model.setCellEditable(model.getRowCount() - 1, 5, true);
                }
                return true;
            }

            @Override
            protected void done() {
                if (itemId != 0) {
                    loadPresentationsByItem(itemId);
                }
            }

        };
        sw.execute();

    }

    public ArrayList<String> getUnitsList() {
        ArrayList<String> unidadList = app.getControl().getUnitsList("", "name");
        return unidadList;
    }

    public ArrayList<Location> getLocationsList() {
        ArrayList<Location> locationList = app.getControl().getLocationList("", "name");
        return locationList;
    }

    public final void initActions() {

        widthLabel = 75;

        ImageIcon icon = new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "add1.png", 24, 24));
        acAddUnit = new ProgAction("", icon, "Agregar medida", 'm') {
            public void actionPerformed(ActionEvent e) {
                app.getGuiManager().showPanelNewUnit("Unidades de medida", PanelAddItem.this, getUnitsList());
            }
        };

        icon = new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "add1.png", 24, 24));
        acAddLocation = new ProgAction("", icon, "Agregar locacion", 'l') {
            public void actionPerformed(ActionEvent e) {
                app.getGuiManager().showPanelNewLocation(PanelAddItem.this);
            }
        };
    }

    public void setItem(Item item) {
        reset();
        if (item != null) {
            itemId = item.getId();

            regName.setText(item.getName());
            regName.setEditable(false);
            regMeseure.setText(item.getMeasure());
            regMeseure.setEnabled(false);
//            regLocation.setSelected(item.getLocation());
            regLocation.setEnabled(false);
            regQuantity.setText(String.valueOf(item.getQuantity()));
            regQuantity.setEditable(false);
            regStockMin.setText(String.valueOf(item.getStockMin()));
            regStockMin.setEditable(false);
            regStockMax.setText(String.valueOf(item.getStock()));
            regStockMax.setEditable(false);
            regCost.setText(String.valueOf(item.getCost()));
            regCost.setEditable(false);
            regPrice.setText(String.valueOf(item.getPrice()));
            regPrice.setEditable(false);

            regTags.setText(item.getTagsSt());
            chOnlyDelivery.setSelected(item.isOnlyDelivery());
            chOnlyDelivery.setEnabled(false);
            chSnapShot.setSelected(item.isSnapshot());
            chSnapShot.setEnabled(false);

            btEdit.setVisible(true);

            loadPresentationsByItem(item.getId());
        }
    }

    public void loadPresentationsByItem(long idItem) throws NumberFormatException {
        ArrayList<Object[]> presentations = app.getControl().getPresentationsByItem(idItem);

        for (int i = 0; i < presentations.size(); i++) {
            Object[] pres = presentations.get(i);
            int idPres = Integer.parseInt(pres[0].toString());
            int idProd = Integer.parseInt(pres[1].toString());
            String quantity = pres[2].toString();
            if (idPres == 0) {
                for (int j = 0; j < model.getRowCount(); j++) {
                    int rProd = Integer.parseInt(model.getValueAt(j, 1).toString());
                    if (rProd == idProd) {
                        model.setValueAt(true, j, 0);
                        model.setValueAt(quantity, j, 5);
                    }
                }
            } else {
                for (int j = 0; j < model.getRowCount(); j++) {
                    if (Util.isNumber(model.getValueAt(j, 3).toString())) {
                        int rPres = Integer.parseInt(model.getValueAt(j, 3).toString());
                        if (rPres == idPres) {
                            model.setValueAt(true, j, 0);
                            model.setValueAt(quantity, j, 5);
                        }
                    }
                }
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

        jSplitPane1 = new javax.swing.JSplitPane();
        pnLeft = new javax.swing.JPanel();
        labelInfo = new javax.swing.JLabel();
        btSave = new javax.swing.JButton();
        regStockMin = new com.bacon.gui.util.Registro(BoxLayout.X_AXIS, "Stock min", "", widthLabel);
        regCost = new com.bacon.gui.util.Registro(BoxLayout.X_AXIS, "Costo", "", widthLabel);
        regPrice = new com.bacon.gui.util.Registro(BoxLayout.X_AXIS, "Precio", "", widthLabel);
        btDelete = new javax.swing.JButton();
        regName = new com.bacon.gui.util.Registro(BoxLayout.X_AXIS, "Nombre", "", widthLabel);
        regStockMax = new com.bacon.gui.util.Registro(BoxLayout.X_AXIS, "Stock max", "", widthLabel);
        regMeseure = new com.bacon.gui.util.Registro(BoxLayout.X_AXIS, "Medida", new Object[1], acAddUnit,widthLabel);
        regTags = new com.bacon.gui.util.Registro(BoxLayout.X_AXIS, "Tags", new JTextArea(""), widthLabel);
        btEdit = new javax.swing.JButton();
        regQuantity = new com.bacon.gui.util.Registro(BoxLayout.X_AXIS, "Cantidad", "", widthLabel);
        chSnapShot = new javax.swing.JCheckBox();
        regLocation = new com.bacon.gui.util.Registro(BoxLayout.X_AXIS, "Locacion", new Object[1], acAddLocation,widthLabel);
        chOnlyDelivery = new javax.swing.JCheckBox();
        chSaveExit = new javax.swing.JCheckBox();
        pnRight = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableProducts = new javax.swing.JTable();
        btRefresh = new javax.swing.JButton();
        lbTitle = new javax.swing.JLabel();

        regName.setNextFocusableComponent(regMeseure);

        regMeseure.setNextFocusableComponent(regQuantity);

        chSnapShot.setText("jCheckBox1");

        regLocation.setNextFocusableComponent(regQuantity);

        chOnlyDelivery.setText("jCheckBox1");

        javax.swing.GroupLayout pnLeftLayout = new javax.swing.GroupLayout(pnLeft);
        pnLeft.setLayout(pnLeftLayout);
        pnLeftLayout.setHorizontalGroup(
            pnLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnLeftLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(regMeseure, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnLeftLayout.createSequentialGroup()
                        .addComponent(regStockMin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(regStockMax, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pnLeftLayout.createSequentialGroup()
                        .addComponent(regCost, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(regPrice, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(regQuantity, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnLeftLayout.createSequentialGroup()
                        .addComponent(chSaveExit, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(35, 35, 35)
                        .addComponent(btDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btSave, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(regLocation, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(regTags, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(labelInfo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnLeftLayout.createSequentialGroup()
                        .addComponent(regName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(1, 1, 1)
                        .addComponent(btEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnLeftLayout.createSequentialGroup()
                        .addGroup(pnLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chOnlyDelivery, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chSnapShot, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        pnLeftLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btDelete, btSave});

        pnLeftLayout.setVerticalGroup(
            pnLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnLeftLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(regName, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(regMeseure, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(regLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(regQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(regStockMax, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(regStockMin, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(regCost, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(regPrice, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(regTags, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(chOnlyDelivery)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chSnapShot)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btSave, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btDelete, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chSaveExit, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jSplitPane1.setLeftComponent(pnLeft);

        tableProducts.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(tableProducts);

        javax.swing.GroupLayout pnRightLayout = new javax.swing.GroupLayout(pnRight);
        pnRight.setLayout(pnRightLayout);
        pnRightLayout.setHorizontalGroup(
            pnRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnRightLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnRightLayout.createSequentialGroup()
                        .addComponent(lbTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 444, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnRightLayout.setVerticalGroup(
            pnRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnRightLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3)
                .addComponent(jScrollPane1)
                .addContainerGap())
        );

        jSplitPane1.setRightComponent(pnRight);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 910, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btDelete;
    private javax.swing.JButton btEdit;
    private javax.swing.JButton btRefresh;
    private javax.swing.JButton btSave;
    private javax.swing.JCheckBox chOnlyDelivery;
    private javax.swing.JCheckBox chSaveExit;
    private javax.swing.JCheckBox chSnapShot;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JLabel labelInfo;
    private javax.swing.JLabel lbTitle;
    private javax.swing.JPanel pnLeft;
    private javax.swing.JPanel pnRight;
    private com.bacon.gui.util.Registro regCost;
    private com.bacon.gui.util.Registro regLocation;
    private com.bacon.gui.util.Registro regMeseure;
    private com.bacon.gui.util.Registro regName;
    private com.bacon.gui.util.Registro regPrice;
    private com.bacon.gui.util.Registro regQuantity;
    private com.bacon.gui.util.Registro regStockMax;
    private com.bacon.gui.util.Registro regStockMin;
    private com.bacon.gui.util.Registro regTags;
    private javax.swing.JTable tableProducts;
    // End of variables declaration//GEN-END:variables

    public void modoActualizarItem() {
        btSave.setIcon(iconUpdate);
        btSave.setText("Actualizar");
        btSave.setActionCommand(AC_UPDATE_ITEM);
        btDelete.setVisible(true);
        chOnlyDelivery.setEnabled(true);
        chSnapShot.setEnabled(true);
    }

    public void modoNuevoItem() {
        btDelete.setVisible(false);
        btSave.setIcon(iconSave);
        btSave.setText("Guardar");
        btSave.setActionCommand(AC_SAVE_ITEM);
    }

    @Override
    public void reset() {
        regName.setText("");
        regName.setEnabled(true);
        regName.setEditable(true);
        regMeseure.setText("");
        regMeseure.setEnabled(true);
        regPrice.setText("");
        regPrice.setEnabled(true);
        regPrice.setEditable(true);
        regCost.setText("");
        regCost.setEnabled(true);
        regCost.setEditable(true);
        regQuantity.setText("");
        regQuantity.setEnabled(true);
        regQuantity.setEditable(true);
        regStockMax.setText("");
        regStockMax.setEnabled(true);
        regStockMax.setEditable(true);
        regStockMin.setText("");
        regStockMin.setEnabled(true);
        regStockMin.setEditable(true);
        regLocation.setEnabled(true);
        chOnlyDelivery.setEnabled(true);
        chOnlyDelivery.setSelected(false);
        chSnapShot.setEnabled(true);
        chSnapShot.setSelected(false);
        regTags.setText("");
        regTags.setEnabled(true);
        btEdit.setVisible(false);
        btEdit.setActionCommand(AC_ALLOW_EDIT);
        btEdit.setIcon(iconEdit);

        for (int i = 0; i < tableProducts.getRowCount(); ++i) {
            model.setValueAt(Boolean.FALSE, i, 0);
            model.setValueAt(1, i, 5);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (AC_SAVE_ITEM.equals(e.getActionCommand())) {
            Item item = parseItem();
            if (item != null) {
                app.getControl().addItem(item);
                pcs.firePropertyChange(AC_ADD_ITEM, null, item);
                if (chSaveExit.isSelected()) {
                    cancelPanel();
                } else {
                    allowEdit(false);
                    btEdit.setVisible(true);
                    btEdit.setIcon(iconEdit);
                    btEdit.setActionCommand(AC_ALLOW_EDIT);
                }
            }
        } else if (AC_UPDATE_ITEM.equals(e.getActionCommand())) {
            Item item = parseItem();
            if (item != null) {
                app.getControl().updateItemAll(item);
                pcs.firePropertyChange(AC_UPDATE_ITEM, null, item);
                if (chSaveExit.isSelected()) {
                    cancelPanel();
                } else {
                    allowEdit(false);
                    btEdit.setIcon(iconEdit);
                    btEdit.setActionCommand(AC_ALLOW_EDIT);
                }
            }
        } else if (AC_REFRESH_PRODUCTS.equals(e.getActionCommand())) {
            loadProducts();
        } else if (AC_ALLOW_EDIT.equals(e.getActionCommand())) {
            allowEdit(true);
            btEdit.setIcon(iconCancel);
            btEdit.setActionCommand(AC_CANCEL_EDIT);
        } else if (AC_CANCEL_EDIT.equals(e.getActionCommand())) {
            allowEdit(false);
            btEdit.setIcon(iconEdit);
            btEdit.setActionCommand(AC_ALLOW_EDIT);
        } else if (AC_DELETE_ITEM.equals(e.getActionCommand())) {
            int confirm = JOptionPane.showConfirmDialog(this, "Desea borrar el item permanentemente", 
                    "Eliminar item", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.OK_OPTION) {
                app.getControl().deleteItem(itemId);
                reset();
                cancelPanel();
                pcs.firePropertyChange(AC_DELETE_ITEM, null, null);
            }
        }
    }

    public void allowEdit(boolean edit) {
        regName.setEditable(edit);
        regLocation.setEnabled(edit);
        regMeseure.setEnabled(edit);
        regPrice.setEditable(edit);
        regStockMax.setEditable(edit);
        regStockMin.setEditable(edit);
        regCost.setEditable(edit);
    }
    public static final String AC_CANCEL_EDIT = "AC_CANCEL EDIT";
    public static final String AC_ADD_ITEM = "AC_ADD_ITEM";

    public void cancelPanel() {
        getRootPane().getParent().setVisible(false);
        reset();
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        updateTabla();
    }

    private Item parseItem() {
        Item item = null;
        boolean validate = true;
        if (regName.getText().isEmpty()) {
            regName.setBorderToError();
            validate = false;
        }

        if (regMeseure.getText().isEmpty()) {
            regMeseure.setBorderToError();
            validate = false;
        }

        if (regLocation.getText().isEmpty()) {
            regLocation.setBorderToError();
            validate = false;
        }

        if (regQuantity.getText().isEmpty()) {
            regQuantity.setBorderToError();
            validate = false;
        }

        if (validate) {
            item = new Item();
            item.setName(regName.getText().toLowerCase());
            item.setMeasure(regMeseure.getText().toLowerCase());
            item.setLocation(((Location) regLocation.getObject()).getId());
            item.setQuantity(Double.parseDouble(regQuantity.getText()));
            try {
                Double stockMin = Double.parseDouble(regStockMin.getText());
                item.setStockMin(stockMin);
            } catch (NumberFormatException e) {
                item.setStockMin(0);
            }
            try {
                Double stockMax = Double.parseDouble(regStockMax.getText());
                item.setStock(stockMax);
            } catch (NumberFormatException e) {
                item.setStock(0);
            }
            try {
                BigDecimal cost = new BigDecimal(regCost.getText());
                item.setCost(cost);
            } catch (NumberFormatException e) {
                item.setCost(BigDecimal.ZERO);
            }
            try {
                BigDecimal price = new BigDecimal(regPrice.getText());
                item.setPrice(price);
            } catch (NumberFormatException e) {
                item.setPrice(BigDecimal.ZERO);
            }

            item.setInit(item.getQuantity());
            item.setOnlyDelivery(chOnlyDelivery.isSelected());
            item.setSnapshot(chSnapShot.isSelected());
//            item.setLocation(0);
            item.setAverage(BigDecimal.ZERO);
            item.setUser(app.getUser().getUsername().toLowerCase());
            item.setTags(regTags.getText().toLowerCase());
            ArrayList<Object[]> selecteds = getSelecteds();
            for (int i = 0; i < selecteds.size(); i++) {
                Object[] dat = selecteds.get(i);
                String dat1 = dat[1].toString();
                dat1 = StringUtils.isNumeric(dat1) ? dat1 : "0";
                item.addPresentations(Integer.parseInt(dat[0].toString()), Integer.parseInt(dat1), Double.parseDouble(dat[2].toString()));
            }
        }
        if (itemId != 0) {
            item.setId(itemId);
        }
        return item;
    }

    private void updateTabla() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                tableProducts.updateUI();
            }
        });
    }

    private ArrayList<Object[]> getSelecteds() {
        ArrayList<Object[]> prods = new ArrayList<>();
        int[] selectedsRows = getSelectedsRows();
        for (int i = 0; i < selectedsRows.length; i++) {
            Object[] data = {
                model.getValueAt(selectedsRows[i], COL_SEL1).toString(),
                model.getValueAt(selectedsRows[i], COL_SEL2).toString(),
                model.getValueAt(selectedsRows[i], 5).toString(),};
            prods.add(data);
        }
        return prods;
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

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (PanelList.AC_SELECTED.equals(evt.getPropertyName())) {
            regMeseure.setText(evt.getNewValue().toString());
        } else if (PanelList.AC_ADD.equals(evt.getPropertyName())) {
            app.getControl().addUnit(evt.getNewValue().toString());
            updateUnitList();
        } else if (PanelList.AC_EDIT.equals(evt.getPropertyName())) {
            app.getControl().updateUnit(evt.getNewValue().toString(), evt.getOldValue().toString());
            updateUnitList();
        } else if (PanelList.AC_DELETE.equals(evt.getPropertyName())) {
            app.getControl().deleteUnit(evt.getNewValue().toString());
            updateUnitList();
        } else if (PanelNewLocation.AC_ADD_LOCATION.equals(evt.getPropertyName())) {

        }
    }

    private void updateUnitList() {
        ArrayList<String> unidadList = getUnitsList();
        regMeseure.setText(unidadList.toArray(new String[1]));
    }

    private void updateLocationList() {
        ArrayList<Location> location = getLocationsList();
        regLocation.setText(location.toArray(new String[1]));
    }

}
