package com.rb.gui;

import com.rb.Aplication;
import com.rb.ProgAction;
import com.rb.domain.Cycle;
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
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;
import org.apache.commons.lang3.StringUtils;
import org.dz.MyDefaultTableModel;
import org.dz.PanelCapturaMod;
import org.dz.TextFormatter;

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
    private JTextField tfCycle;
    private ProgAction acSearchCycle;
    private Cycle cycle;
    private SwingWorker sw;

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

        tfCycle = new JTextField();
        tfCycle.addActionListener(this);
        tfCycle.setActionCommand(AC_SEARCH_CYCLE);
        acSearchCycle = new ProgAction("",
                new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "search.png", 12, 12)),
                "s", 's') {
            @Override
            public void actionPerformed(ActionEvent ev) {
                String stCycle = tfCycle.getText();
                if (stCycle.isEmpty()) {
                    regCycle.setBorderToError();
                    return;
                }
                regCycle.setBorderToNormal();
                cycle = app.getControl().getCycle(Integer.valueOf(stCycle));
                if (cycle != null) {
                    createLabelTitle(cycle);
                    loadSnapshotItems(cycle);
                } else {
                    createLabelTitle(null);
                    clearTable();
                }
            }
        };

        lbTitle.setBorder(BorderFactory.createEtchedBorder());

        btPrev.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "previus_page.png", 16, 16)));
        btNext.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "next_page.png", 16, 16)));

        btPrev.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long id = cycle.getId();
                System.out.println("id:" + id);
                tfCycle.setText(String.valueOf(--id));
                acSearchCycle.actionPerformed(new ActionEvent(this, 1, AC_SEARCH_CYCLE));
            }
        });

        btNext.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long id = cycle.getId();
                if(cycle.isOpened())return;
                tfCycle.setText(String.valueOf(++id));
                acSearchCycle.actionPerformed(new ActionEvent(this, 2, AC_SEARCH_CYCLE));
            }
        });

        chVerCalc.setText("Ver. Calculada");
        chVerCalc.setSelected(true);
        chVerCalc.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadSnapshotItems(cycle);
            }
        });

        regCycle.setComponent(tfCycle);
        regCycle.setLabelText("Ciclo");
        regCycle.setAction(acSearchCycle);
        regCycle.setDocument(TextFormatter.getIntegerLimiter());

        cycle = app.getControl().getLastCycle();
        regCycle.setText(String.valueOf(cycle.getId()));
        loadSnapshotItems(cycle);

    }
    public static final String AC_SEARCH_CYCLE = "AC_SEARCH_CYCLE";

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
        stb.append("<html><font size=+1 color='#823EF0'>Snapshot</font>");
        if (ciclo == null) {
            lbTitle1.setText(stb.toString());
            lbTitle.setText("");
            return;
        }
        stb.append("   Ciclo:<font size=+1 color=blue>").append(ciclo.getId()).append("</font></html>");
        lbTitle1.setText(stb.toString());
        stb = new StringBuilder("<html>");
        stb.append("<p>")
                .append("Inicio: <font color='#F70570' size=4>").append(app.DF_FULL3.format(ciclo.getInit())).append("</font>")
                .append("  Hasta: <font color='#FA1005'size=4>")
                .append(ciclo.getEnd() != null ? app.DF_FULL3.format(ciclo.getEnd()) : app.DF_FULL3.format(new Date()))
                .append("</font>").append("</p>");
        stb.append("</html>");
        lbTitle.setText(stb.toString());
    }

    private void loadSnapshotItems(Cycle cycle) {
//        SwingWorker sw;
        if(sw !=null && !sw.isDone()){
            sw.cancel(true);
            model.setRowCount(0);
        }

        sw = new SwingWorker() {

            @Override
            protected Object doInBackground() throws Exception {
//                if(runningSw!=null) runningSw.cancel(true);              
                model.setRowCount(0);

                createLabelTitle(cycle);
                ArrayList<Map> itemSnapList = app.getControl().getItemSnapshotList("cycle_id=" + cycle.getId(), "i.name");

                boolean saved = false;
                if (!cycle.isOpened() && !chVerCalc.isSelected()) {
                    saved = true;
                }

                for (Map map : itemSnapList) {

                    double res = 0;
                    HashMap<String, Double> data = getSnapshotData(map, cycle);
                    if (saved) {
                        double sIns = Double.parseDouble(map.get("ins").toString());
                        double sOuts = Double.parseDouble(map.get("outs").toString());
                        double sConc = Double.parseDouble(map.get("conc").toString());
                        double sales = Double.parseDouble(map.get("sales").toString());
                        res = data.get("quantity") + sIns - sOuts - sales + sConc;
                    }

                    if (showFinal) {
                        model.addRow(new Object[]{
                            map.get("item_id"),
                            StringUtils.upperCase(map.get("name").toString()),
                            data.get("quantity"),
                            saved ? map.get("ins") : data.get("income"),
                            saved ? map.get("outs") : data.get("outcome"),
                            saved ? map.get("conc") : data.get("conciliation"),
                            saved ? map.get("sales") : data.get("sales"),
                            saved ? res : data.get("result"),
                            saved ? map.get("real") : data.get("real")});
                    } else {
                        model.addRow(new Object[]{
                            map.get("item_id"),
                            StringUtils.upperCase(map.get("name").toString()),
                            data.get("quantity"),
                            saved ? map.get("ins") : data.get("income"),
                            saved ? map.get("outs") : data.get("outcome"),
                            saved ? map.get("conc") : data.get("conciliation"),
                            saved ? map.get("sales") : data.get("sales"),
                            data.get("result")
                        });
                    }
                    model.setRowEditable(model.getRowCount() - 1, false);
                }
                return true;
            }
            
        };

//        runningSw = sw;
        sw.execute();

    }

    private void clearTable() {
        model.setRowCount(0);
    }

    /**
     *
     * @param map ItemSnapshot data
     * @param cycle
     * @return
     */
    public HashMap<String, Double> getSnapshotData(Map map, Cycle cycle) {
        return app.getControl().getSnapshotData(map, cycle);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (AC_REFRESH_LIST.equals(e.getActionCommand())) {
            loadSnapshotItems(cycle);
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
            loadSnapshotItems(cycle);
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
        regCycle = new org.dz.Registro(BoxLayout.X_AXIS, "Ciclo", "",50);
        lbTitle1 = new javax.swing.JLabel();
        chVerCalc = new javax.swing.JCheckBox();
        btPrev = new javax.swing.JButton();
        btNext = new javax.swing.JButton();

        lbTitle.setText("jLabel1");

        tableItems.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane2.setViewportView(tableItems);

        lbTitle1.setText("jLabel1");

        chVerCalc.setText("jCheckBox1");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 780, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbTitle1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lbTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(8, 8, 8)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(regCycle, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btPrev, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(3, 3, 3)
                                .addComponent(btNext, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(tgViewResult, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(chVerCalc))))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btRefresh, tgViewResult});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbTitle1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chVerCalc))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(tgViewResult, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(regCycle, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btRefresh, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btPrev)
                    .addComponent(btNext))
                .addGap(5, 5, 5)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 395, Short.MAX_VALUE)
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btRefresh, tgViewResult});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btNext, btPrev, regCycle});

    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btNext;
    private javax.swing.JButton btPrev;
    private javax.swing.JButton btRefresh;
    private javax.swing.JCheckBox chVerCalc;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lbTitle;
    private javax.swing.JLabel lbTitle1;
    private org.dz.Registro regCycle;
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
