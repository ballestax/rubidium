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
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import org.apache.commons.lang3.StringUtils;
import org.bx.TextFormatter;
import org.bx.gui.ListSelection;
import org.bx.gui.MyDefaultTableModel;
import org.dz.PanelCaptura;
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
        
        regQuantity.setFontCampo(font);
        regQuantity.setDocument(TextFormatter.getDoubleLimiter());
        
        ArrayList<String> units = app.getControl().getUnitsList("", "name");
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
//        btSave.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "save.png", 24, 24)));
        btSave.setActionCommand(AC_SAVE_ITEM);
        btSave.addActionListener(this);
        
        btUpdate.setFont(new Font("Sans", 1, 11));
        btUpdate.setText("Actualizar");
//        btUpdate.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "save.png", 24, 24)));
        btUpdate.setActionCommand(AC_UPDATE_ITEM);
        btUpdate.addActionListener(this);
        
        String[] colNames = {"Sel", "ID", "Producto", "ID pres.", "Presentacion", "Cantidad"};
        ArrayList<String> asList = new ArrayList<>(Arrays.asList(colNames));
//        asList.add(loc);
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
        
        chOnlyDelivery.setText("Solo domicilios");
        chOnlyDelivery.setFont(font);
        
        loadProducts();
        
    }
    public static final String AC_SAVE_ITEM = "AC_SAVE_ITEM";           
    public static final String AC_UPDATE_ITEM = "AC_UPDATE_ITEM";
    
    private void loadProducts() {
        SwingWorker sw = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
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
            System.out.println("Setting itemId:" + itemId);
            regName.setText(item.getName());
            regName.setEnabled(false);
            regMeseure.setText(item.getMeasure());
            regMeseure.setEnabled(false);
//            regLocation.setSelected(item.getLocation());
            regLocation.setEnabled(false);
            regQuantity.setText(String.valueOf(item.getQuantity()));
            regQuantity.setEnabled(false);
            regStockMin.setText(String.valueOf(item.getStockMin()));
            regStockMin.setEnabled(false);
            regStockMax.setText(String.valueOf(item.getStock()));
            regStockMax.setEnabled(false);
            regCost.setText(String.valueOf(item.getCost()));
            regCost.setEnabled(false);
            regPrice.setText(String.valueOf(item.getPrice()));
            regPrice.setEnabled(false);
            chOnlyDelivery.setSelected(item.isOnlyDelivery());
            chOnlyDelivery.setEnabled(false);
            
            ArrayList<Object[]> presentations = app.getControl().getPresentationsByItem(item.getId());

//            List<Object[]> presentations = item.getPresentations();
//            model.setValueAt(true, 0, 0);
            for (int i = 0; i < presentations.size(); i++) {
                Object[] pres = presentations.get(i);
                int idPres = Integer.parseInt(pres[0].toString());
                int idProd = Integer.parseInt(pres[1].toString());
                String quantity = pres[2].toString();
//                System.out.println(Arrays.toString(pres));
                if (idPres == 0) {
                    for (int j = 0; j < model.getRowCount(); j++) {
                        int rProd = Integer.parseInt(model.getValueAt(j, 1).toString());
//                        System.out.println("::"+rProd+"::"+idProd+":"+(rProd == idProd));
                        if (rProd == idProd) {
                            model.setValueAt(true, j, 0);
                            model.setValueAt(quantity, j, 5);
                        }
                    }
                } else {
                    for (int j = 0; j < model.getRowCount(); j++) {
                        if (Util.isNumber(model.getValueAt(j, 3).toString())) {
                            int rPres = Integer.parseInt(model.getValueAt(j, 3).toString());
//                            System.out.println("::"+rPres+"::"+idPres+":"+(rPres == idPres));
                            if (rPres == idPres) {
                                model.setValueAt(true, j, 0);
                                model.setValueAt(quantity, j, 5);
                            }
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

        regName = new org.dz.Registro(BoxLayout.X_AXIS, "Nombre", "", widthLabel);
        regMeseure = new org.dz.Registro(BoxLayout.X_AXIS, "Medida", new Object[1], acAddUnit,widthLabel);
        regQuantity = new org.dz.Registro(BoxLayout.X_AXIS, "Cantidad", "", widthLabel);
        regStockMax = new org.dz.Registro(BoxLayout.X_AXIS, "Stock max", "", widthLabel);
        regStockMin = new org.dz.Registro(BoxLayout.X_AXIS, "Stock min", "", widthLabel);
        jScrollPane1 = new javax.swing.JScrollPane();
        tableProducts = new javax.swing.JTable();
        regPrice = new org.dz.Registro(BoxLayout.X_AXIS, "Precio", "", widthLabel);
        regCost = new org.dz.Registro(BoxLayout.X_AXIS, "Costo", "", widthLabel);
        labelInfo = new javax.swing.JLabel();
        btSave = new javax.swing.JButton();
        chSaveExit = new javax.swing.JCheckBox();
        regLocation = new org.dz.Registro(BoxLayout.X_AXIS, "Locacion", new Object[1], acAddLocation,widthLabel);
        btUpdate = new javax.swing.JButton();
        chOnlyDelivery = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();

        regName.setNextFocusableComponent(regMeseure);

        regMeseure.setLabelFont(new Font("arial",0,11));
        regMeseure.setNextFocusableComponent(regQuantity);

        tableProducts.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(tableProducts);

        chSaveExit.setText("jCheckBox1");

        regMeseure.setLabelFont(new Font("arial",0,11));
        regLocation.setNextFocusableComponent(regQuantity);

        chOnlyDelivery.setText("jCheckBox1");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(regMeseure, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(regStockMin, javax.swing.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(regStockMax, javax.swing.GroupLayout.DEFAULT_SIZE, 184, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(regCost, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(regPrice, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(regName, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(regQuantity, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(labelInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(chSaveExit)
                        .addGap(49, 49, 49)
                        .addComponent(btUpdate, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btSave, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE))
                    .addComponent(regLocation, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(chOnlyDelivery, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 484, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(regName, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(regMeseure, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(regLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(regQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(regStockMax, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(regStockMin, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(regCost, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(regPrice, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(chOnlyDelivery)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btSave, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chSaveExit, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btUpdate, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(13, 13, 13))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btSave;
    private javax.swing.JButton btUpdate;
    private javax.swing.JCheckBox chOnlyDelivery;
    private javax.swing.JCheckBox chSaveExit;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel labelInfo;
    private org.dz.Registro regCost;
    private org.dz.Registro regLocation;
    private org.dz.Registro regMeseure;
    private org.dz.Registro regName;
    private org.dz.Registro regPrice;
    private org.dz.Registro regQuantity;
    private org.dz.Registro regStockMax;
    private org.dz.Registro regStockMin;
    private javax.swing.JTable tableProducts;
    // End of variables declaration//GEN-END:variables

    
    public void modoActualizarItem(){
        btSave.setVisible(false);
        btUpdate.setVisible(true);
        chOnlyDelivery.setEnabled(true);
    }
    
    public void modoNuevoItem(){
        btSave.setVisible(true);
        btUpdate.setVisible(false);
    }
    
    
    @Override
    public void reset() {
        regName.setText("");
        regName.setEnabled(true);
        regMeseure.setText("");
        regMeseure.setEnabled(true);
        regPrice.setText("");
        regPrice.setEnabled(true);
        regCost.setText("");
        regCost.setEnabled(true);
        regQuantity.setText("");
        regQuantity.setEnabled(true);
        regStockMax.setText("");
        regStockMax.setEnabled(true);
        regStockMin.setText("");
        regStockMin.setEnabled(true);
        regLocation.setEnabled(true);
        chOnlyDelivery.setEnabled(true);
        chOnlyDelivery.setSelected(false);
        
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

//                Item item1 = app.getControl().getItemWhere("name='"+item.getName()+"'");
                pcs.firePropertyChange(AC_ADD_ITEM, null, item);
                if (chSaveExit.isSelected()) {
                    cancelPanel();
                } else {
                    reset();
                }
            }
        } else if (AC_UPDATE_ITEM.equals(e.getActionCommand())) {
            Item item = parseItem();
            if (item != null) {
                app.getControl().updateItemAll(item);

//                Item item1 = app.getControl().getItemWhere("name='"+item.getName()+"'");
//                pcs.firePropertyChange(AC_ADD_ITEM, null, item);
                if (chSaveExit.isSelected()) {
                    cancelPanel();
                } else {
                    reset();
                }
            }
        }
    }
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
//            item.setLocation(0);
            item.setAverage(BigDecimal.ZERO);
            item.setUser(app.getUser().getUsername().toLowerCase());
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
