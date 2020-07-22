package com.bacon.gui;

import com.bacon.Aplication;
import com.bacon.domain.Item;
import static com.bacon.gui.PanelSelItem.AC_ADD_ITEM_TO_TABLE;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.text.Format;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import static javax.swing.BorderFactory.createLineBorder;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import org.bx.gui.MyDefaultTableModel;
import org.dz.PanelCapturaMod;

/**
 *
 * @author lrod
 */
public class PanelInventory extends PanelCapturaMod implements ActionListener, ListSelectionListener {

    private final Aplication app;

    private MyDefaultTableModel model;
    
    private PanelReportProductDetail pnDetail;

    /**
     * Creates new form PanelReportSales
     *
     * @param app
     */
    public PanelInventory(Aplication app) {
        this.app = app;
        initComponents();
        createComponents();
    }

    private void createComponents() {

        panelButtons.setLayout(new FlowLayout(FlowLayout.LEFT));

        JButton btAdd = new JButton("ADD");
        btAdd.setActionCommand(AC_SHOW_ADD_ITEM);
        btAdd.addActionListener(this);

        JButton btLoad = new JButton("LOAD");
        btLoad.setActionCommand(AC_LOAD_ITEM);
        btLoad.addActionListener(this);

        JButton btRefresh = new JButton("REFRESH");
        btRefresh.setActionCommand(AC_REFRESH_ITEMS);
        btRefresh.addActionListener(this);

        JButton btConciliation = new JButton("CONCILIACION");
        btConciliation.setActionCommand(AC_ADD_CONCILIATION);
        btConciliation.addActionListener(this);

        panelButtons.add(btAdd);
        panelButtons.add(btLoad);
        panelButtons.add(btRefresh);
        panelButtons.add(btConciliation);

        String[] colNames = new String[]{"N°", "Item", "Cantidad", "Medida", "Cost", "Price", "Stock min", "Stock max"};

        model = new MyDefaultTableModel(colNames, 0);
        tableItems.setModel(model);
        tableItems.setRowHeight(24);
        tableItems.setFont(new Font("Tahoma", 0, 16));
        
        
        DefaultListSelectionModel selectionModel = new DefaultListSelectionModel();
        selectionModel.addListSelectionListener(this);
        
        tableItems.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableItems.setSelectionModel(selectionModel);

        TablaCellRenderer tRenderer = new TablaCellRenderer(true, app.getDCFORM_P());

        int[] colW = new int[]{10, 120, 20, 20, 20, 20, 20, 20};
        for (int i = 0; i < colW.length; i++) {
            tableItems.getColumnModel().getColumn(i).setMinWidth(colW[i]);
            tableItems.getColumnModel().getColumn(i).setPreferredWidth(colW[i]);
            tableItems.getColumnModel().getColumn(i).setCellRenderer(new TablaCellRenderer(true, null));
        }
        tableItems.getColumnModel().getColumn(2).setCellRenderer(tRenderer);
        tableItems.getColumnModel().getColumn(4).setCellRenderer(tRenderer);
        tableItems.getColumnModel().getColumn(5).setCellRenderer(tRenderer);
        tableItems.getColumnModel().getColumn(6).setCellRenderer(tRenderer);
        tableItems.getColumnModel().getColumn(7).setCellRenderer(tRenderer);
//        tableItems.getColumnModel().getColumn(model.getColumnCount() - 1).setCellEditor(new BotonEditor(tableItems, this, "AC_MOD_USER"));
//        tableItems.getColumnModel().getColumn(model.getColumnCount() - 1).setCellRenderer(new ButtonCellRenderer("Ver"));

        pnDetail = new PanelReportProductDetail(app);

        jPanel1.setLayout(new BorderLayout());
        jPanel1.add(pnDetail);

        populateTable();

    }
    public static final String AC_ADD_CONCILIATION = "AC_ADD_CONCILIATION";
    public static final String AC_REFRESH_ITEMS = "AC_REFRESH_ITEMS";
    public static final String AC_LOAD_ITEM = "AC_LOAD_ITEM";
    public static final String AC_SHOW_ADD_ITEM = "AC_SHOW_ADD_ITEM";

    private void populateTable() {

        ArrayList<Item> itemList = app.getControl().getItemList("", "");

        SwingWorker sw = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                model.setRowCount(0);
                for (int i = 0; i < itemList.size(); i++) {
                    Item item = itemList.get(i);
                    model.addRow(new Object[]{
                        item.getId(),
                        item.getName(),
                        item.getQuantity(),
                        item.getMeasure(),
                        item.getCost(),
                        item.getPrice(),
                        item.getStockMin(),
                        item.getStock()
                    });
                    model.setRowEditable(model.getRowCount() - 1, false);
                }
                return true;
            }
        };
        sw.execute();
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
        jScrollPane1 = new javax.swing.JScrollPane();
        tableItems = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        panelButtons = new javax.swing.JPanel();

        jSplitPane1.setDividerLocation(500);
        jSplitPane1.setResizeWeight(0.5);

        tableItems.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(tableItems);

        jSplitPane1.setLeftComponent(jScrollPane1);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 385, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jSplitPane1.setRightComponent(jPanel1);

        javax.swing.GroupLayout panelButtonsLayout = new javax.swing.GroupLayout(panelButtons);
        panelButtons.setLayout(panelButtonsLayout);
        panelButtonsLayout.setHorizontalGroup(
            panelButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panelButtonsLayout.setVerticalGroup(
            panelButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 27, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 899, Short.MAX_VALUE)
                    .addComponent(panelButtons, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelButtons, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(jSplitPane1)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JPanel panelButtons;
    private javax.swing.JTable tableItems;
    // End of variables declaration//GEN-END:variables

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (PanelAddItem.AC_ADD_ITEM.equals(evt.getPropertyName())) {
            populateTable();
        } else if (AC_ADD_ITEM_TO_TABLE.equals(evt.getPropertyName())) {
            populateTable();
        } else if (PanelNewConciliacion.ACTION_SAVE_CONCILIACION.equals(evt.getPropertyName())) {
            populateTable();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (AC_SHOW_ADD_ITEM.equals(e.getActionCommand())) {
            app.getGuiManager().showPanelAddItem(this);
        } else if (AC_LOAD_ITEM.equals(e.getActionCommand())) {
            app.getGuiManager().showPanelSelItem(this);
        } else if (AC_REFRESH_ITEMS.equals(e.getActionCommand())) {
            populateTable();
        } else if (AC_ADD_CONCILIATION.equals(e.getActionCommand())) {
            app.getGuiManager().showPanelConciliacion(true);
        }

    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        int lInd = e.getLastIndex();
        Item item = app.getControl().getItemWhere("id="+model.getValueAt(lInd, 0));
//        valido = prod != null;
        pnDetail.showInfoProduct(item);
    }

    public class TablaCellRenderer extends JLabel implements TableCellRenderer {

        boolean isBordered = true;
        private boolean agotada;
        private int status;
//        protected enum status {Color.black; Color.blue; Color.orange; Color.red};
        private final Format formatter;

        public TablaCellRenderer(boolean isBordered, Format formatter) {
            super();
            this.isBordered = isBordered;
            this.formatter = formatter;
            agotada = false;
            setFont(new Font("tahoma", 0, 14));
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            int r = table.convertRowIndexToModel(row);
            int col = 2;

            if (value != null) {
                if (formatter != null) {
                    try {
                        setHorizontalAlignment(SwingConstants.RIGHT);
                        value = formatter.format(value);
                    } catch (IllegalArgumentException e) {
                    }
                }
                setText(value.toString().toUpperCase());

                double cant = 0;
                try {
                    cant = Double.parseDouble(model.getValueAt(r, col).toString());

                } catch (Exception e) {
                }
                agotada = cant <= 0;
            }
            if (isSelected) {
                setForeground(!agotada ? Color.black : Color.red);
                setBackground(tableItems.getSelectionBackground());
                if (hasFocus) {
                    setBorder(BorderFactory.createLineBorder(Color.darkGray));
                } else {
                    setBorder(createLineBorder(Color.lightGray));
                }
            } else {
                setBackground(tableItems.getBackground());
                setForeground(!agotada ? Color.black : Color.red);
                setBorder(UIManager.getBorder("Table.cellBorder"));
            }
            return this;
        }
    }

}
