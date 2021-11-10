package com.bacon.gui;

import com.bacon.Aplication;
import com.bacon.domain.Cycle;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.text.Format;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import static javax.swing.BorderFactory.createLineBorder;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;
import org.apache.commons.lang3.StringUtils;
import org.dz.MyDefaultTableModel;
import org.dz.PanelCapturaMod;

/**
 *
 * @author lrod
 */
public class PanelSnapShot extends PanelCapturaMod implements ActionListener {

    private final Aplication app;
    private MyDefaultTableModel model;
    private Font fontTable;
    private boolean showFinal = false;

    private final String[] colNames = new String[]{"N°", "Item", "Inicio", "Entradas", "Salidas", "Conciliaciones", "Ventas", "Cantidad"};
    private final String[] colNames1 = new String[]{"N°", "Item", "Inicio", "Entradas", "Salidas", "Conciliaciones", "Ventas", "Cantidad", "Real"};
    private SwingWorker runningSw;

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

        model = new MyDefaultTableModel(colNames, 0);
        tableItems.setModel(model);
        tableItems.setRowHeight(24);
        fontTable = new Font("Sans serif", 1, 15);
        tableItems.setFont(fontTable);
        tableItems.getTableHeader().setReorderingAllowed(false);

        arrangeTable();

        btRefresh.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "refresh.png", 24, 24)));
        btRefresh.setActionCommand(AC_REFRESH_LIST);
        btRefresh.addActionListener(this);
        btRefresh.setToolTipText("Actualizar");

        tgViewResult.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "paperclip.png", 24, 24)));
        tgViewResult.setActionCommand(AC_SHOW_FINAL);
        tgViewResult.addActionListener(this);
        tgViewResult.setToolTipText("Ver final");

        loadSnapshotItems();

    }

    private void arrangeTable() {
        TablaCellRenderer tRenderer = new TablaCellRenderer(true, app.getDCFORM_P(), 7);
        tRenderer.setFont(fontTable);
        tRenderer.addColumnColor(2, new Color(255, 240, 180));
        tRenderer.addColumnColor(7, new Color(175, 174, 255));
        tRenderer.addColumnColor(8, new Color(185, 255, 185));
        tRenderer.setColComparations(-1, -1);

        int[] colW = new int[]{4, 150, 10, 10, 10, 10, 10, 10, 10};
        TablaCellRenderer tRenderer2 = new TablaCellRenderer(true, null, 7);
        tRenderer2.setColComparations(-1, -1);
        for (int i = 0; i < colW.length - (showFinal ? 0 : 1); i++) {
            tableItems.getColumnModel().getColumn(i).setMinWidth(colW[i]);
            tableItems.getColumnModel().getColumn(i).setPreferredWidth(colW[i]);
            tableItems.getColumnModel().getColumn(i).setCellRenderer(tRenderer2);
        }

        tableItems.getColumnModel().getColumn(2).setCellRenderer(tRenderer);
        tableItems.getColumnModel().getColumn(3).setCellRenderer(tRenderer);
        tableItems.getColumnModel().getColumn(4).setCellRenderer(tRenderer);
        tableItems.getColumnModel().getColumn(5).setCellRenderer(tRenderer);
        tableItems.getColumnModel().getColumn(6).setCellRenderer(tRenderer);
        tableItems.getColumnModel().getColumn(7).setCellRenderer(tRenderer);
        if (showFinal) {
            tRenderer.setColComparations(7, 8);
            tRenderer2.setColComparations(7, 8);
            tableItems.getColumnModel().getColumn(8).setCellRenderer(tRenderer);
        }

    }
    private static final String AC_SHOW_FINAL = "AC_SHOW_FINAL";
    private static final String AC_REFRESH_LIST = "AC_REFRESH_LIST";

    private void createLabelTitle(Cycle ciclo) {
        StringBuilder stb = new StringBuilder();
        stb.append("<html>");
        stb.append("<font size=+1 color='#823EF0'>Snapshot</font><br>");
        stb.append("<p color='#13541C'>Ciclo:<font size=+1 color=blue>").append(ciclo.getId()).append("</font>")
                .append("  Inicio: <font color='#F70570' size=+1>").append(app.DF_FULL3.format(ciclo.getInit())).append("</font>")
                .append("  Hasta: <font color='#FA1005'size=+1>")
                .append(ciclo.getEnd() != null ? app.DF_FULL3.format(ciclo.getEnd()) : app.DF_FULL3.format(new Date()))
                .append("</font>").append("</p>");
        stb.append("</html>");
        lbTitle.setText(stb.toString());
    }

    private void loadSnapshotItems() {
        SwingWorker sw;
        sw = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
//                if(runningSw!=null) runningSw.cancel(true);
                model.setRowCount(0);
                Cycle lastCycle = app.getControl().getLastCycle();
                createLabelTitle(lastCycle);
                ArrayList<Map> itemSnapList = app.getControl().getItemSnapshotList("cycle_id=" + lastCycle.getId(), "i.name");

                for (Map map : itemSnapList) {
                    double outs = 0;
                    ArrayList<Object[]> presentationsByItem = app.getControl().getPresentationsByItem(Long.valueOf(map.get("item_id").toString()));

                    boolean onlyDelivery = Boolean.parseBoolean(map.get("onlyDelivery").toString()); // item is only delivery
                    for (Object[] get : presentationsByItem) {
                        int idPres = Integer.parseInt(get[0].toString());
                        int idProd = Integer.parseInt(get[1].toString());
                        long idItem = Long.valueOf(map.get("item_id").toString());
//                        System.out.println(idPres + "::" + idProd);
                        if (idPres == 0) { //producto sin presentacion
                            ArrayList<Object[]> productsOutInventory = app.getControl().getProductsOutInventoryList(idProd, idItem, lastCycle.getInit());
//                            productsOutInventory.stream().map(data -> Double.parseDouble(data[2].toString())).reduce(outs, Double::sum);
                            for (int j = 0; j < productsOutInventory.size(); j++) {
                                Object[] data = productsOutInventory.get(j);
                                double quantity = Double.parseDouble(data[2].toString());
                                int delType = Integer.parseInt(data[3].toString());
                                outs += quantity * (onlyDelivery && delType == PanelPedido.TIPO_LOCAL ? 0 : 1.0); // excluir locales solo para llevar
                            }
                        } else {

                            ArrayList<Object[]> presentationsOutInventory = app.getControl().getPresentationsOutInventoryList(idPres, idItem, lastCycle.getInit());

//                            presentationsOutInventory.stream().map(data -> Double.parseDouble(data[3].toString())).reduce(outs, Double::sum);
//                            System.out.println(map.get("name") + "::" + presentationsOutInventory.size());
                            for (int j = 0; j < presentationsOutInventory.size(); j++) {
                                Object[] data = presentationsOutInventory.get(j);
                                double quantity = Double.parseDouble(data[3].toString());
                                int delType = Integer.parseInt(data[4].toString());
                                outs += quantity * (onlyDelivery && delType == PanelPedido.TIPO_LOCAL ? 0 : 1.0); // excluir locales solo para llevar
                            }
                        }
                    }

                    Map countIn = app.getControl().countItemSnap(Long.valueOf(map.get("item_id").toString()), 1, lastCycle.getId());
                    Map countOut = app.getControl().countItemSnap(Long.valueOf(map.get("item_id").toString()), 2, lastCycle.getId());
                    Map countConc = app.getControl().countItemConciliations(Long.valueOf(map.get("item_id").toString()), lastCycle.getId());

                    double quantity = Double.parseDouble(map.get("quantity").toString());
                    double sIns = Double.parseDouble(countIn.get("sum").toString());
                    double sOuts = Double.parseDouble(countOut.get("sum").toString());
                    double sConc = Double.parseDouble(countConc.get("sum").toString());
                    double res = quantity + sIns - sOuts - outs + sConc;
                    double real = Double.parseDouble(map.get("real").toString());
                    if (showFinal) {
                        model.addRow(new Object[]{map.get("item_id"), StringUtils.upperCase(map.get("name").toString()), quantity, sIns, sOuts, sConc, outs, res, real});
                    } else {
                        model.addRow(new Object[]{map.get("item_id"), StringUtils.upperCase(map.get("name").toString()), quantity, sIns, sOuts, sConc, outs, res});
                    }
                    model.setRowEditable(model.getRowCount() - 1, false);
                }
                return true;
            }
        };

//        runningSw = sw;
        sw.execute();

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (AC_REFRESH_LIST.equals(e.getActionCommand())) {
            loadSnapshotItems();
        } else if (AC_SHOW_FINAL.equals(e.getActionCommand())) {
            showFinal = tgViewResult.isSelected();
            if (showFinal) {
                model = new MyDefaultTableModel(colNames1, 0);
                tableItems.setModel(model);

            } else {
                model = new MyDefaultTableModel(colNames, 0);
                tableItems.setModel(model);
            }
            arrangeTable();
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
        btRefresh = new javax.swing.JButton();
        tgViewResult = new javax.swing.JToggleButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tableItems = new javax.swing.JTable();

        lbTitle.setText("jLabel1");

        tableItems.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane2.setViewportView(tableItems);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 775, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lbTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tgViewResult, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btRefresh, tgViewResult});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lbTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btRefresh, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tgViewResult, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 448, Short.MAX_VALUE)
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btRefresh, tgViewResult});

    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btRefresh;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lbTitle;
    private javax.swing.JTable tableItems;
    private javax.swing.JToggleButton tgViewResult;
    // End of variables declaration//GEN-END:variables

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }

    public class TablaCellRenderer extends JLabel implements TableCellRenderer {

        boolean isBordered = true;
        private boolean agotada, warning;
        private int status;
//        protected enum status {Color.black; Color.blue; Color.orange; Color.red};
        private Format formatter;
        private final Color ORANGE = new Color(200, 105, 0);
        private int colInd = 0;
        private int colComp1, colComp2;
        private Map<Integer, Color> columnColor = new HashMap<>();

        public TablaCellRenderer(boolean isBordered, Format formatter, int colInd) {
            super();
            this.isBordered = isBordered;
            this.formatter = formatter;
            agotada = false;
            warning = false;
            this.colInd = colInd;

//            setFont(new Font("tahoma", 1, 14));
            setOpaque(true);
        }

        @Override
        public void setFont(Font font) {
            super.setFont(font); //To change body of generated methods, choose Tools | Templates.
        }

        public void addColumnColor(int col, Color color) {
            columnColor.put(col, color);
            repaint();
        }

        public void setColComparations(int col1, int col2) {
            colComp1 = col1;
            colComp2 = col2;
        }

        public void setFormatter(Format formatter) {
            this.formatter = formatter;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            int r = table.convertRowIndexToModel(row);

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
                double val1 = 0, val2 = 0;
                try {
                    cant = Double.parseDouble(model.getValueAt(r, colInd).toString());
                    if (colComp1 >= 0 && colComp2 >= 0) {
                        val1 = Double.parseDouble(model.getValueAt(r, colComp1).toString());
                        val2 = Double.parseDouble(model.getValueAt(r, colComp2).toString());
                    }

                } catch (Exception e) {
                }
                agotada = cant <= 0;
                warning = Double.compare(val1, val2) != 0;
            }
            if (isSelected) {
                setForeground(!agotada ? warning ? ORANGE : Color.black : Color.red);
                setBackground(tableItems.getSelectionBackground());
                if (columnColor.containsKey(column)) {
                    setBackground(columnColor.get(column).darker());
                }
                if (hasFocus) {
                    setBorder(BorderFactory.createLineBorder(Color.darkGray));
                } else {
                    setBorder(createLineBorder(Color.lightGray));
                }
            } else {
                setBackground(tableItems.getBackground());
                if (columnColor.containsKey(column)) {
                    setBackground(columnColor.get(column));
                }
                setForeground(!agotada ? warning ? ORANGE : Color.black : Color.red);
//                setBackground(agotada || warning ? getForeground().brighter().brighter().brighter() : tableItems.getBackground());
                setBorder(UIManager.getBorder("Table.cellBorder"));
            }
            return this;
        }
    }
}
