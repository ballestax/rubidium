package com.bacon.gui;

import com.bacon.Aplication;
import com.bacon.MyConstants;
import com.bacon.ProgAction;
import com.bacon.domain.Category;
import com.bacon.domain.Product;
import static com.bacon.gui.PanelInventory.logger;
import static com.bacon.gui.PanelTopSearch.AC_CLEAR_FIELD;
import com.bacon.gui.util.MyPopupListener;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.text.Format;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import static javax.swing.BorderFactory.createLineBorder;
import javax.swing.BoxLayout;
import javax.swing.DefaultListSelectionModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import org.bx.gui.MyDefaultTableModel;
import org.dz.PanelCapturaMod;

/**
 *
 * @author lrod
 */
public class PanelProducts extends PanelCapturaMod implements ActionListener, ListSelectionListener {

    private final Aplication app;
    private MyDefaultTableModel model;
    private JPopupMenu popupTable;
    private MyPopupListener popupListenerTabla;
    private TableRowSorter<MyDefaultTableModel> lastSorter;
    private JTextArea textArea;
    private ProgAction acNewCategory;

    /**
     * Creates new form PanelProducts
     *
     * @param app
     */
    public PanelProducts(Aplication app) {
        this.app = app;
        textArea = new JTextArea(5, 1);
        initComponents();
        createComponents();
    }

    private void createComponents() {

        String[] cols = new String[]{"ID", "Nombre", "Categoria", "Precio", "Status"};
        model = new MyDefaultTableModel(cols, 0);

        acNewCategory = new ProgAction("C", null, "New category", 'c');

        regCat.setAction(acNewCategory);

        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        JScrollPane scroll = new JScrollPane(textArea,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        regDesc.setComponent(scroll);

//        regCat.setText(app.getControl().getCategoriesList().toArray());

        tbProducts.setModel(model);
        tbProducts.setRowHeight(22);
//        tbProducts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        DefaultListSelectionModel selectionModel = new DefaultListSelectionModel();
        selectionModel.addListSelectionListener(this);
        selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tbProducts.setSelectionModel(selectionModel);

        Font f = new Font("Sans", 0, 14);
        TablaCellRenderer tRenderer = new TablaCellRenderer(true, app.getDCFORM_P());

        int[] colW = new int[]{10, 100, 25, 20};
        for (int i = 0; i < colW.length; i++) {
            tbProducts.getColumnModel().getColumn(i).setMinWidth(colW[i]);
            tbProducts.getColumnModel().getColumn(i).setPreferredWidth(colW[i]);
            tbProducts.getColumnModel().getColumn(i).setCellRenderer(new TablaCellRenderer(true, null));
        }
        tbProducts.getColumnModel().getColumn(3).setCellRenderer(tRenderer);

        popupTable = new JPopupMenu();
        popupListenerTabla = new com.bacon.gui.util.MyPopupListener(popupTable, true);
        JMenuItem item1 = new JMenuItem("Pagar");
        item1.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int r = tbProducts.getSelectedRow();
            }

        });
        popupTable.add(item1);

        tbProducts.addMouseListener(popupListenerTabla);

        btBuscar.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "cancel.png", 18, 18)));
        btBuscar.setActionCommand(AC_CLEAR_FIELD);
        btBuscar.addActionListener(this);
//        regSearch.setFontCampo(font1);
        regSearch.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                filtrar(regSearch.getText().toUpperCase(), 1, MyConstants.FILTER_TEXT_INT_CONTAINS);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filtrar(regSearch.getText().toUpperCase(), 1, MyConstants.FILTER_TEXT_INT_CONTAINS);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filtrar(regSearch.getText().toUpperCase(), 1, MyConstants.FILTER_TEXT_INT_CONTAINS);
            }
        });

        btRefreshList.setText("Refresh");
        btRefreshList.setActionCommand(AC_REFRESH_LIST);
        btRefreshList.addActionListener(this);

        ArrayList<Category> categoriesList = app.getControl().getCategoriesList();
        categoriesList.add(0, new Category("TODOS"));
        categoriesList.forEach((c) -> ((Category) c).setName(c.getName().toUpperCase()));
        regFilterCat.setText(categoriesList.toArray());
        regFilterCat.setActionCommand(AC_CHANGE_CATEGORY);
        regFilterCat.addActionListener(this);

//        pnCtrl.setLayout(new FlowLayout());
        pnCtrl.add(btRefreshList);

        populateTable("");

    }
    private static final String AC_CHANGE_CATEGORY = "AC_CHANGE_CATEGORY";

    private static final String AC_REFRESH_LIST = "AC_REFRESH_LIST";

    public void filtrar(final String text, final int columna, final int tFilter) {
        RowFilter<Object, Object> filterText = new RowFilter<Object, Object>() {
            @Override
            public boolean include(RowFilter.Entry entry) {
                if (text.equals("")) {
                    return true;
                }
                if (MyConstants.FILTER_TEXT_INT_START == tFilter) {
                    return entry.getStringValue(columna).toLowerCase().startsWith(text.toLowerCase());
                } else if (tFilter == MyConstants.FILTER_TEXT_INT_CONTAINS) {
                    return entry.getStringValue(columna).toLowerCase().contains(text.toLowerCase());
                } else {
                    return entry.getStringValue(columna).equalsIgnoreCase(text);
                }
            }
        };

        TableRowSorter<MyDefaultTableModel> sorter = new TableRowSorter<>(model);
        sorter.setSortable(0, false);
        sorter.setSortable(3, false);
        sorter.setSortable(4, false);
//        sorter.setComparator(3, new COmpara);
        if (tFilter <= 3) {
            sorter.setRowFilter(filterText);
        }
        lastSorter = sorter;
        tbProducts.setRowSorter(sorter);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (AC_REFRESH_LIST.equals(e.getActionCommand())) {
            populateTable("");
        } else if (AC_CLEAR_FIELD.equals(e.getActionCommand())) {
            regSearch.setText("");
            regSearch.getComponent().requestFocus();
        } else if (AC_CHANGE_CATEGORY.equals(e.getActionCommand())) {
            String cat = regFilterCat.getText();
            if (!"TODOS".equals(cat)) {
                populateTable("category='" + cat.toLowerCase() + "'");
            } else {
                populateTable("");
            }
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {

        int row = e.getLastIndex();
        if (row < 0) {
//            pnDetail.showInfoProduct(null);
        }
        try {
            String id = model.getValueAt(row, 0).toString();
            Product prod = app.getControl().getProductById(Long.valueOf(id));
            System.out.println(prod);
//            pnDetail.showInfoProduct(item);
            showProduct(prod);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }

    private void showProduct(Product prod) {
        regName.setText(prod.getName());
        regCode.setText(prod.getCode());
//        Category cat = app.getControl().getCategory(prod.getCategory());
        regCat.setText(prod.getCategory());
        regPrice.setText(app.DCFORM_P.format(prod.getPrice()));
//        System.out.println("::"+prod.getDescription());
        regDesc.setText(prod.getDescription());
        jLabel1.setText(String.valueOf(prod.getId()));

    }

    private void populateTable(String where) {
        populateTable(where, "name,price");
    }

    private void populateTable(String where, String order) {

        SwingWorker<Boolean, Product> sw = new SwingWorker<Boolean, Product>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                model.setRowCount(0);
                ArrayList<Product> productsList = app.getControl().getProductsList(where, order);
                for (Product product : productsList) {
                    publish(product);
                }
                return true;
            }

            @Override
            protected void process(List<Product> chunks) {
                for (Product prod : chunks) {
                    model.addRow(new Object[]{
                        prod.getId(),
                        prod.getName(),
                        prod.getCategory(),
                        prod.getPrice(),
                        prod.isEnabled()
                    });
                    model.setRowEditable(model.getRowCount() - 1, false);
                }
            }

            @Override
            protected void done() {
                System.out.println("Loaded all products");
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
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        regName = new com.bacon.gui.util.Registro(BoxLayout.X_AXIS, "Nombre","", 70);
        regCat = new com.bacon.gui.util.Registro(BoxLayout.X_AXIS, "Categoria", "", 70);
        regPrice = new com.bacon.gui.util.Registro(BoxLayout.X_AXIS, "Precio", "",70);
        regDesc = new com.bacon.gui.util.Registro(BoxLayout.Y_AXIS, "Descripcion", textArea,70);
        regCode = new com.bacon.gui.util.Registro(BoxLayout.Y_AXIS, "Codigo","", 70);
        jPanel2 = new javax.swing.JPanel();
        pnCtrl = new javax.swing.JPanel();
        btBuscar = new javax.swing.JButton();
        regSearch = new com.bacon.gui.util.Registro(BoxLayout.X_AXIS, "Buscar", "",60);
        btRefreshList = new javax.swing.JButton();
        regFilterCat = new com.bacon.gui.util.Registro(BoxLayout.X_AXIS, "Categoria", new String[]{}, 70);
        jScrollPane1 = new javax.swing.JScrollPane();
        tbProducts = new javax.swing.JTable();

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setText("jLabel1");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(regDesc, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE)
                    .addComponent(regPrice, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(regCat, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(regName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(regCode, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(regCode, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(regName, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(regCat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(regPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(regDesc, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {regCat, regName, regPrice});

        jSplitPane1.setRightComponent(jPanel1);

        pnCtrl.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btRefreshList.setText("jButton1");

        javax.swing.GroupLayout pnCtrlLayout = new javax.swing.GroupLayout(pnCtrl);
        pnCtrl.setLayout(pnCtrlLayout);
        pnCtrlLayout.setHorizontalGroup(
            pnCtrlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnCtrlLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(regSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(btBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(regFilterCat, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 35, Short.MAX_VALUE)
                .addComponent(btRefreshList)
                .addContainerGap())
        );
        pnCtrlLayout.setVerticalGroup(
            pnCtrlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnCtrlLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnCtrlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(regSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btRefreshList)
                    .addComponent(regFilterCat, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pnCtrlLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btRefreshList, regFilterCat, regSearch});

        tbProducts.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(tbProducts);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addComponent(pnCtrl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnCtrl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                .addContainerGap())
        );

        jSplitPane1.setLeftComponent(jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 476, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btBuscar;
    private javax.swing.JButton btRefreshList;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JPanel pnCtrl;
    private com.bacon.gui.util.Registro regCat;
    private com.bacon.gui.util.Registro regCode;
    private com.bacon.gui.util.Registro regDesc;
    private com.bacon.gui.util.Registro regFilterCat;
    private com.bacon.gui.util.Registro regName;
    private com.bacon.gui.util.Registro regPrice;
    private com.bacon.gui.util.Registro regSearch;
    private javax.swing.JTable tbProducts;
    // End of variables declaration//GEN-END:variables

    public class TablaCellRenderer extends JLabel implements TableCellRenderer {

        boolean isBordered = true;
        private boolean disabled;
        private final Format formatter;

        public TablaCellRenderer(boolean isBordered, Format formatter) {
            super();
            this.isBordered = isBordered;
            this.formatter = formatter;
            disabled = false;
            setFont(new Font("tahoma", 0, 12));
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            int r = table.convertRowIndexToModel(row);
            if ("false".equals(table.getModel().getValueAt(r, 4).toString())) {
                disabled = true;
            } else {
                disabled = false;
            }

            if (value != null) {
                if (formatter != null) {
                    try {
                        setHorizontalAlignment(SwingConstants.RIGHT);
                        value = formatter.format(value);
                    } catch (IllegalArgumentException e) {
                    }
                }
                setText(value.toString().toUpperCase());
            }
            if (isSelected) {
                setForeground(!disabled ? Color.black : Color.red);
                setBackground(tbProducts.getSelectionBackground());
                if (hasFocus) {
                    setBorder(BorderFactory.createLineBorder(Color.darkGray));
                } else {
                    setBorder(createLineBorder(Color.lightGray));
                }
            } else {
                setBackground(tbProducts.getBackground());
                setForeground(!disabled ? Color.black : Color.red);
                setBorder(UIManager.getBorder("Table.cellBorder"));
            }
            return this;
        }
    }

}
