package com.bacon.gui;

import com.bacon.Aplication;
import com.bacon.domain.Cycle;
import com.bacon.domain.Item;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import org.bx.gui.MyDefaultTableModel;
import org.dz.PanelCapturaMod;

/**
 *
 * @author lrod
 */
public class PanelSnapShot extends PanelCapturaMod implements ActionListener {

    private final Aplication app;
    private MyDefaultTableModel model;
    private Font fontTable;

    /**
     * Creates new form PanelSnapShot
     *
     * @param app
     */
    public PanelSnapShot(Aplication app) {
        this.app = app;
        initComponents();
        createComponents();
    }

    public void createComponents() {

        String[] colNames = new String[]{"N°", "Item", "Inicio", "Entradas", "Salidas", "Ventas", "Cantidad"};

        model = new MyDefaultTableModel(colNames, 0);
        tableItems.setModel(model);
        tableItems.setRowHeight(24);
        fontTable = new Font("Sans serif", 0, 15);
        tableItems.setFont(fontTable);

//        PanelInventory.TablaCellRenderer tRenderer = new PanelInventory.TablaCellRenderer(true, app.getDCFORM_P());
        int[] colW = new int[]{4, 150, 10, 10, 10, 10, 10};
        for (int i = 0; i < colW.length; i++) {
            tableItems.getColumnModel().getColumn(i).setMinWidth(colW[i]);
            tableItems.getColumnModel().getColumn(i).setPreferredWidth(colW[i]);
//            tableItems.getColumnModel().getColumn(i).setCellRenderer(new PanelInventory.TablaCellRenderer(true, null));
        }

        tableItems.getColumn("Cantidad").setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                setFont(fontTable);
                setText(value.toString());
                setBackground(isSelected ? Color.pink.darker() : Color.pink);
                return this;
            }
        });

        btRefresh.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "refresh.png", 24, 24)));
        btRefresh.setActionCommand(AC_REFRESH_LIST);
        btRefresh.addActionListener(this);

        loadSnapshotItems();

    }
    private static final String AC_REFRESH_LIST = "AC_REFRESH_LIST";

    private void loadSnapshotItems() {

        SwingWorker sw;
        sw = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                model.setRowCount(0);
                Cycle lastCycle = app.getControl().getLastCycle();
                ArrayList<Map> itemSnapList = app.getControl().getItemSnapshotList("cycle_id=" + lastCycle.getId(), "i.name");

                for (Map map : itemSnapList) {
                    double outs = 0;
                    ArrayList<Object[]> presentationsByItem = app.getControl().getPresentationsByItem(Long.valueOf(map.get("item_id").toString()));

                    for (Object[] get : presentationsByItem) {
                        int idPres = Integer.parseInt(get[0].toString());
                        int idProd = Integer.parseInt(get[1].toString());
//                        System.out.println(idPres + "::" + idProd);
                        if (idPres == 0) { //producto sin presentacion
                            ArrayList<Object[]> productsOutInventory = app.getControl().getProductsOutInventoryList(idProd, lastCycle.getInit());
//                            productsOutInventory.stream().map(data -> Double.parseDouble(data[2].toString())).reduce(outs, Double::sum);
                            for (int j = 0; j < productsOutInventory.size(); j++) {
                                Object[] data = productsOutInventory.get(j);
                                double quantity = Double.parseDouble(data[2].toString());
                                outs += quantity;
                            }
                        } else {
                            long idItem = Long.valueOf(map.get("item_id").toString());
                            ArrayList<Object[]> presentationsOutInventory = app.getControl().getPresentationsOutInventoryList(idPres, idItem, lastCycle.getInit());

//                            presentationsOutInventory.stream().map(data -> Double.parseDouble(data[3].toString())).reduce(outs, Double::sum);
//                            System.out.println(map.get("name") + "::" + presentationsOutInventory.size());
                            for (int j = 0; j < presentationsOutInventory.size(); j++) {
                                Object[] data = presentationsOutInventory.get(j);
                                double quantity = Double.parseDouble(data[3].toString());
                                outs += quantity;
                            }
                        }
                    }
//                    System.out.println("outs = " + outs);

                    Map countIn = app.getControl().countItemSnap(Long.valueOf(map.get("item_id").toString()), 1, lastCycle.getId());
                    Map countOut = app.getControl().countItemSnap(Long.valueOf(map.get("item_id").toString()), 2, lastCycle.getId());
                    model.addRow(new Object[]{map.get("item_id"), map.get("name"), map.get("quantity"), countIn.get("sum"), countOut.get("sum"), outs, map.get("quantity")});
                    model.setRowEditable(model.getRowCount() - 1, false);
                }
                return true;
            }
        };

        sw.execute();

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (AC_REFRESH_LIST.equals(e.getActionCommand())) {
            loadSnapshotItems();
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

        lbTitle = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableItems = new javax.swing.JTable();
        btRefresh = new javax.swing.JButton();

        lbTitle.setText("jLabel1");

        tableItems.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(tableItems);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 765, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lbTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(2, 2, 2)
                        .addComponent(btRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lbTitle, javax.swing.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE)
                    .addComponent(btRefresh, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(52, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btRefresh;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbTitle;
    private javax.swing.JTable tableItems;
    // End of variables declaration//GEN-END:variables

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }
}
