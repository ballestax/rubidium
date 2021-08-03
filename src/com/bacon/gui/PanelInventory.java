package com.bacon.gui;

import com.bacon.Aplication;
import com.bacon.GUIManager;
import com.bacon.ProgAction;
import com.bacon.domain.Item;
import com.bacon.domain.Presentation;
import com.bacon.domain.Product;
import com.bacon.gui.util.MyPopupListener;
import static com.bacon.gui.PanelSelItem.AC_ADD_ITEM_TO_TABLE;
import com.bacon.gui.util.Registro;
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
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JTable;
import javax.swing.JPopupMenu;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import org.apache.log4j.Logger;
import org.bx.gui.MyDefaultTableModel;
import org.dz.PanelCapturaMod;
import java.awt.Desktop;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.swing.BoxLayout;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.dz.MyDialogEsc;

/**
 *
 * @author lrod
 */
public class PanelInventory extends PanelCapturaMod implements ActionListener, ListSelectionListener {

    private final Aplication app;
    private MyDefaultTableModel model;
    private PanelReportProductDetail pnDetail;
    public static final Logger logger = Logger.getLogger(PanelInventory.class.getCanonicalName());

    private JPopupMenu popupTable;
    private MyPopupListener popupListenerTabla;
    private Registro regSearch;
    private Registro regFilters;

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

        Font f = new Font("Sans", 1, 11);

        JButton btAdd = new JButton("Agregar");
        btAdd.setFont(f);
        btAdd.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "shopping-basket-add.png", 24, 24)));
        btAdd.setActionCommand(AC_SHOW_ADD_ITEM);
        btAdd.addActionListener(this);

        JButton btLoad = new JButton("Cargar");
        btLoad.setFont(f);
        btLoad.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "shopping-basket-accept.png", 24, 24)));
        btLoad.setActionCommand(AC_LOAD_ITEM);
        btLoad.addActionListener(this);

        JButton btDesc = new JButton("Descargar");
        btDesc.setFont(f);
        btDesc.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "shopping-basket-remove.png", 24, 24)));
        btDesc.setActionCommand(AC_DOWNLOAD_ITEM);
        btDesc.addActionListener(this);

        JButton btRefresh = new JButton("Actualizar");
        btRefresh.setFont(f);
        btRefresh.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "shopping-basket-refresh.png", 24, 24)));
        btRefresh.setActionCommand(AC_REFRESH_ITEMS);
        btRefresh.addActionListener(this);

        JButton btConciliation = new JButton("Conciliar");
        btConciliation.setFont(f);
        btConciliation.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "shopping-basket-prohibit.png", 24, 24)));
        btConciliation.setActionCommand(AC_ADD_CONCILIATION);
        btConciliation.addActionListener(this);

        JButton btExport = new JButton("Exportar");
        btExport.setFont(f);
        btExport.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "export-file.png", 24, 24)));
        btExport.setActionCommand(AC_EXPORT_TO);
        btExport.addActionListener(this);

        ImageIcon searchIcon = new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "search.png", 16, 16));
        ImageIcon clearIcon = new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "cancel.png", 24, 24));
        ProgAction actionSearch = new ProgAction("",
                clearIcon, "", 's') {
            @Override
            public void actionPerformed(ActionEvent ev) {
                cleanSearch();
            }
        };
        regSearch = new Registro(BoxLayout.X_AXIS, "", "", 150, actionSearch);
        regSearch.setLabelIcon(searchIcon);
        regSearch.setLabelHorizontalAlignment(SwingConstants.RIGHT);
        regSearch.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                filtrar();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filtrar();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filtrar();
            }
        });

        List<String> tagsInventoryList = app.getControl().getTAGSInventoryList("");
        
        //split(,) la lista de tags de cada item, lo pasa a lowecase y filtra que no este vacio el string
        Set<String> listTags = tagsInventoryList.stream().flatMap(Pattern.compile(",")::splitAsStream).map(tag-> tag.trim()).filter(tag -> !tag.isEmpty()).collect(Collectors.toSet());
        
        List filters = new ArrayList();
        filters.add(FILTER_ITEM_TODOS);
        filters.add(FILTER_ITEM_STOCK_REGULAR);
        filters.add(FILTER_ITEM_STOCK_MINIMO);
        filters.add(FILTER_ITEM_AGOTADOS);
        for (String listTag : listTags) {
            filters.add(FILTER_ITEM_TAGS+listTag.toUpperCase());
        }

        regFilters = new Registro(BoxLayout.X_AXIS, "Items", new String[1], 50);
        regFilters.setHeight(32);
        regFilters.setText(filters.toArray());
        regFilters.setActionCommand(AC_CHANGE_ITEMS);
        regFilters.addActionListener(this);

        panelButtons.add(regSearch);
        panelButtons.add(regFilters);
        panelButtons.add(btAdd);
        panelButtons.add(btLoad);
        panelButtons.add(btDesc);
        panelButtons.add(btRefresh);
        panelButtons.add(btConciliation);
        panelButtons.add(btExport);

        String[] colNames = new String[]{"N°", "Item", "Cantidad", "Medida", "Cost", "Price", "Min", "Cost. Total"};

        model = new MyDefaultTableModel(colNames, 0);
        tableItems.setModel(model);
        tableItems.setRowHeight(24);
        tableItems.setFont(new Font("Tahoma", 0, 16));

        DefaultListSelectionModel selectionModel = new DefaultListSelectionModel();
        selectionModel.addListSelectionListener(this);

        tableItems.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableItems.setSelectionModel(selectionModel);

        TablaCellRenderer tRenderer = new TablaCellRenderer(true, app.getDCFORM_P());

        int[] colW = new int[]{4, 150, 20, 10, 20, 20, 10, 30};
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
//        tableItems.getColumnModel().getColumn(8).setCellRenderer(tRenderer);
//        tableItems.getColumnModel().getColumn(model.getColumnCount() - 1).setCellEditor(new BotonEditor(tableItems, this, "AC_MOD_USER"));
//        tableItems.getColumnModel().getColumn(model.getColumnCount() - 1).setCellRenderer(new ButtonCellRenderer("Ver"));

        popupTable = new JPopupMenu();
        popupListenerTabla = new MyPopupListener(popupTable, true);
        JMenuItem item1 = new JMenuItem("Ver");
        item1.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int r = tableItems.getSelectedRow();
                String id = tableItems.getValueAt(r, 0).toString();
                showPanelEditItem(id);
            }

        });
        JMenuItem itemCargar = new JMenuItem("Cargar");
        itemCargar.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int r = tableItems.getSelectedRow();
                String id = tableItems.getValueAt(r, 0).toString();
                Item item = app.getControl().getItemWhere("id=" + id);
                app.getGuiManager().showPanelSelItem(item, PanelInventory.this);
            }
        });

        JMenuItem itemDescargar = new JMenuItem("Descargar");
        itemDescargar.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int r = tableItems.getSelectedRow();
                String id = tableItems.getValueAt(r, 0).toString();
                Item item = app.getControl().getItemWhere("id=" + id);
                app.getGuiManager().showPanelDownItem(item, PanelInventory.this);
            }
        });

        JMenuItem itemLinks = new JMenuItem("Enlaces");
        itemLinks.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int r = tableItems.getSelectedRow();
                String id = tableItems.getValueAt(r, 0).toString();
                Item item = app.getControl().getItemWhere("id=" + id);
                ArrayList<Object[]> presentations = app.getControl().getPresentationsByItem(item.getId());
                StringBuilder htmlText = new StringBuilder("<html>");

                if (!presentations.isEmpty()) {
                    htmlText.append("<table  width=\"100%\" cellspacing=\"0\" border=\"1\">");
                    htmlText.append("<tr bgcolor=\"#A4C1FF\">");
                    htmlText.append("<td>Producto</td><td>Presentación</td><td>Cantidad</td></tr>");
                } else {
                    htmlText.append("<br><br><font color=red size=+1>  El item: <STRONG>").append(item.getName().toUpperCase()).append("</STRONG> no tiene enlaces.  </font><br><br>");
                }

                for (Object[] presentation : presentations) {
                    Object[] data = presentation;
                    long idPres = Long.parseLong(data[0].toString());
                    long idProd = Long.parseLong(data[1].toString());
                    String quantity = data[2].toString();
                    Product prod = app.getControl().getProductById(idProd);
                    if (idPres == 0) {
                        htmlText.append("<tr><td>").append(prod.getName().toUpperCase()).append("<td>---")
                                .append("<td>").append(quantity).append("</tr>");
                    } else {
                        Presentation press = app.getControl().getPresentationsById(idPres);
                        if (press != null && press.isEnabled()) {
                            Product productById = app.getControl().getProductByPressId((idPres));
                            htmlText.append("<tr><td>").append(productById.getName().toUpperCase()).append("<td>")
                                    .append(press.getName().toUpperCase()).append("<td>").append(quantity).append("</tr>");
                        }
                    }
                }
                htmlText.append("</table></html>");

                JLabel labelInfo = new JLabel(htmlText.toString());
                labelInfo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                JButton btModificar = new JButton("Modificar");
//                btModificar.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 10));
                btModificar.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        showPanelEditItem(String.valueOf(item.getId()));
                    }

                });

                MyDialogEsc dialog = new MyDialogEsc(app.getGuiManager().getFrame());
                dialog.setTitle(item.getName().toUpperCase());
                dialog.setLayout(new BorderLayout());
                dialog.add(labelInfo, BorderLayout.CENTER);
                dialog.add(btModificar, BorderLayout.SOUTH);
                dialog.pack();
                dialog.setLocationRelativeTo(null);
                dialog.setVisible(true);

            }
        });

        popupTable.add(item1);
        popupTable.add(itemCargar);
        popupTable.add(itemDescargar);
        popupTable.add(itemLinks);

        tableItems.addMouseListener(popupListenerTabla);

        pnDetail = new PanelReportProductDetail(app);

        jPanel1.setLayout(new BorderLayout());
        jPanel1.add(pnDetail);

        populateTable();

    }
    private static final String FILTER_ITEM_AGOTADOS = "AGOTADOS";
    private static final String FILTER_ITEM_TAGS = "TAG: ";
    private static final String FILTER_ITEM_STOCK_REGULAR = "STOCK REGULAR";
    private static final String FILTER_ITEM_STOCK_MINIMO = "STOCK MINIMO";
    private static final String FILTER_ITEM_TODOS = "TODOS";
    private static final String AC_CHANGE_ITEMS = "AC_CHANGE_ITEMS";
    public static final String AC_EXPORT_TO = "AC_EXPORT_TO";
    public static final String AC_ADD_CONCILIATION = "AC_ADD_CONCILIATION";
    public static final String AC_REFRESH_ITEMS = "AC_REFRESH_ITEMS";
    public static final String AC_LOAD_ITEM = "AC_LOAD_ITEM";
    public static final String AC_SHOW_ADD_ITEM = "AC_SHOW_ADD_ITEM";
    public static final String AC_DOWNLOAD_ITEM = "AC_DOWNLOAD_ITEM";

    private void showPanelEditItem(String id) {
        Item item = app.getControl().getItemWhere("id=" + id);
        app.getGuiManager().showPanelAddItem(PanelInventory.this, item);
    }

    private void cleanSearch() {
        regSearch.setText("");
    }

    private void filtrar() {
        String text = regSearch.getText();
        ArrayList<Item> listItems = app.getControl().getItemList("", "name");

        List<Item> listFiltered = listItems.stream().filter(item -> item.getName().toUpperCase().contains(text.toUpperCase())).collect(Collectors.toList());

        populateTable(listFiltered);
    }

    private void filtrarItems(String filtro) {
        System.out.println("Filtro:"+filtro );
        System.out.println("::"+filtro.substring(5).toLowerCase());
        Predicate<Item> filterAgotados = itm -> itm.getQuantity() <= 0;
        Predicate<Item> filterMinimo = itm -> itm.getQuantity() <= itm.getStockMin();
        Predicate<Item> filterRegular = itm -> itm.getQuantity() > itm.getStockMin();
        Predicate<Item> filterTags = itm -> itm.getTagsSt().contains(filtro.substring(5).toLowerCase());

        ArrayList<Item> listItems = app.getControl().getItemList("", "name");
        List<Item> listFiltered = listItems;
        if (FILTER_ITEM_AGOTADOS.equals(filtro)) {
            listFiltered = listItems.stream().filter(filterAgotados).collect(Collectors.toList());
        } else if (FILTER_ITEM_STOCK_MINIMO.equals(filtro)) {
            listFiltered = listItems.stream().filter(filterMinimo.and(filterAgotados.negate())).collect(Collectors.toList());
        } else if (FILTER_ITEM_STOCK_REGULAR.equals(filtro)) {
            listFiltered = listItems.stream().filter(filterRegular).collect(Collectors.toList());
        } else if (FILTER_ITEM_TAGS.equals(filtro.substring(0, 5))) {
            System.out.println("filter tags");
            listFiltered = listItems.stream().filter(filterTags).collect(Collectors.toList());
        }
        populateTable(listFiltered);
    }

    private void populateTable() {
        populateTable(app.getControl().getItemList("", "name"));
    }

    private void populateTable(List<Item> itemList) {

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
                        item.getCostTotal()
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

        jSplitPane1.setDividerLocation(700);
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
            .addGap(0, 187, Short.MAX_VALUE)
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
            app.getGuiManager().showPanelAddItem(this, null);
//        } else if (AC_SHOW_EDIT_ITEM.equals(e.getActionCommand())) {

        } else if (AC_LOAD_ITEM.equals(e.getActionCommand())) {
            app.getGuiManager().showPanelSelItem(this);
        } else if (AC_REFRESH_ITEMS.equals(e.getActionCommand())) {
            filtrar();
        } else if (AC_ADD_CONCILIATION.equals(e.getActionCommand())) {
            app.getGuiManager().showPanelConciliacion(true);
        } else if (AC_DOWNLOAD_ITEM.equals(e.getActionCommand())) {
            app.getGuiManager().showPanelDownItem(this);
        } else if (AC_CHANGE_ITEMS.equals(e.getActionCommand())) {
            filtrarItems(regFilters.getText());
        }
        if (e.getActionCommand().equals(AC_EXPORT_TO)) {
            System.out.println("exportando..");
            SwingWorker tarea = new SwingWorker() {
                @Override
                protected Object doInBackground() throws Exception {
                    try {
                        String tipo = "Inventario";
                        String file = app.getDirDocuments() + File.separator + "Reporte_" + tipo + "_" + app.getFormatoFecha().format(new Date()) + ".xlsx";
                        app.getXlsManager().exportarTabla(model, "Reporte " + tipo, file, PanelInventory.this);
                        Desktop.getDesktop().open(new File(file));
                    } catch (Exception ex) {
                        GUIManager.showErrorMessage(null, "Error intentando abrir el archivo.\n" + ex.getMessage(), "Error");
                    }
                    return true;
                }
            };
            tarea.execute();
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        int row = tableItems.getSelectedRow();
        if (row < 0) {
            pnDetail.showInfoProduct(null);
        }
        try {
            Object id = model.getValueAt(row, 0);
            Item item = app.getControl().getItemWhere("id=" + id);
            pnDetail.showInfoProduct(item);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }

    public class TablaCellRenderer extends JLabel implements TableCellRenderer {

        boolean isBordered = true;
        private boolean agotada, warning;
        private int status;
//        protected enum status {Color.black; Color.blue; Color.orange; Color.red};
        private final Format formatter;
        private final Color ORANGE = new Color(244, 145, 0);

        public TablaCellRenderer(boolean isBordered, Format formatter) {
            super();
            this.isBordered = isBordered;
            this.formatter = formatter;
            agotada = false;
            warning = false;
            setFont(new Font("tahoma", 1, 14));
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            int r = table.convertRowIndexToModel(row);
            int col = 2;
            int col2 = 6;

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
                double min = 0;
                try {
                    cant = Double.parseDouble(model.getValueAt(r, col).toString());
                    min = Double.parseDouble(model.getValueAt(r, col2).toString());

                } catch (Exception e) {
                }
                agotada = cant <= 0;
                warning = cant <= min;
            }
            if (isSelected) {
                setForeground(!agotada ? warning ? ORANGE : Color.black : Color.red);
                setBackground(tableItems.getSelectionBackground());
                if (hasFocus) {
                    setBorder(BorderFactory.createLineBorder(Color.darkGray));
                } else {
                    setBorder(createLineBorder(Color.lightGray));
                }
            } else {
                setBackground(tableItems.getBackground());
                setForeground(!agotada ? warning ? ORANGE : Color.black : Color.red);
//                setBackground(agotada || warning ? getForeground().brighter().brighter().brighter() : tableItems.getBackground());
                setBorder(UIManager.getBorder("Table.cellBorder"));
            }
            return this;
        }
    }

}
