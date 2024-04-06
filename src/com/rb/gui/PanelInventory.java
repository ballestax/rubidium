package com.rb.gui;

import com.rb.Aplication;
import com.rb.Configuration;
import com.rb.GUIManager;
import com.rb.ProgAction;
import com.rb.domain.ConfigDB;
import com.rb.domain.Item;
import com.rb.domain.Permission;
import com.rb.domain.Presentation;
import com.rb.domain.Product;
import static com.rb.gui.PanelSelItem.AC_ADD_ITEM_TO_TABLE;
import com.rb.gui.util.Registro;
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
import org.dz.PanelCapturaMod;
import java.awt.Desktop;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.BoxLayout;
import javax.swing.JToggleButton;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.dz.MyDefaultTableModel;
import org.dz.MyDialogEsc;

/**
 *
 * @author lrod
 */
public class PanelInventory extends PanelCapturaMod implements ActionListener, ListSelectionListener {

    private final Aplication app;
    private MyDefaultTableModel model;
    private PanelReportProductDetail pnDetail;
    public static final Logger LOGGER = Logger.getLogger(PanelInventory.class.getCanonicalName());

    private JPopupMenu popupTable;
    private MyPopupListener popupListenerTabla;
    private Registro regSearch;
    private Registro regFilters;
    private Registro regTags;
    private boolean filtered;
    private List listFiltered;
    private JMenuItem itemVer;
    private JMenuItem itemCargar;
    private JMenuItem itemDescargar;
    private JMenuItem itemLinks;
    private Object filterSelected;
    private Object tagSelected;
    private JToggleButton btShowDisable;
    private String stFilterDisable;
    private List<Item> localList;

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
        filtered = false;
        panelButtons.setLayout(new FlowLayout(FlowLayout.LEFT));

        Font f = new Font("Sans", 1, 11);

        String userName = app.getUser().getUsername();
        String userDevice = Aplication.getUserDevice();
        if (!app.getControl().existConfig(Configuration.SHOW_DISABLE_ITEMS, userName, userDevice)) {
            app.getControl().addConfig(new ConfigDB(Configuration.SHOW_DISABLE_ITEMS, ConfigDB.BOOLEAN, "true", userName, userDevice));
        }

        ConfigDB config = app.getControl().getConfig(Configuration.SHOW_DISABLE_ITEMS);
        boolean showDisableItems = config != null ? Boolean.parseBoolean(config.getValor()) : false;
        stFilterDisable = showDisableItems ? "" : "enabled=1";

        JButton btAdd = new JButton();
        btAdd.setName("Agregar");
        btAdd.setFont(f);
        btAdd.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "shopping-basket-add.png", 22, 22)));
        btAdd.setActionCommand(AC_SHOW_ADD_ITEM);
        btAdd.addActionListener(this);
        btAdd.setToolTipText("Agregar Item");

        JButton btLoad = new JButton();
        btLoad.setName("Cargar");
        btLoad.setFont(f);
        btLoad.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "shopping-basket-accept.png", 22, 22)));
        btLoad.setActionCommand(AC_LOAD_ITEM);
        btLoad.addActionListener(this);
        btLoad.setToolTipText("Cargar Item");

        JButton btDesc = new JButton();
        btDesc.setFont(f);
        btDesc.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "shopping-basket-remove.png", 22, 22)));
        btDesc.setActionCommand(AC_DOWNLOAD_ITEM);
        btDesc.addActionListener(this);
        btDesc.setToolTipText("Descargar Item");

        JButton btRefresh = new JButton();
        btRefresh.setFont(f);
        btRefresh.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "refresh.png", 22, 22)));
        btRefresh.setActionCommand(AC_REFRESH_ITEMS);
        btRefresh.addActionListener(this);
        btRefresh.setToolTipText("Refrescar lista");

        JButton btConciliation = new JButton();
        btConciliation.setFont(f);
        btConciliation.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "shopping-basket-prohibit.png", 22, 22)));
        btConciliation.setActionCommand(AC_ADD_CONCILIATION);
        btConciliation.addActionListener(this);
        btConciliation.setToolTipText("Conciliar Item");

        JButton btExport = new JButton();
        btExport.setFont(f);
        btExport.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "export-file.png", 22, 22)));
        btExport.setActionCommand(AC_EXPORT_TO);
        btExport.addActionListener(this);
        btExport.setToolTipText("Exportar lista");

        JButton btSnapShot = new JButton();
        btSnapShot.setFont(f);
        btSnapShot.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "camera-accept.png", 22, 22)));
        btSnapShot.setActionCommand(AC_SHOW_SNAPSHOT);
        btSnapShot.addActionListener(this);
        btSnapShot.setToolTipText("Ver Snapshot");

        JButton btPrintList = new JButton();
        btPrintList.setFont(f);
        btPrintList.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "Printer-orange.png", 22, 22)));
        btPrintList.setActionCommand(AC_PRINT_LIST);
        btPrintList.addActionListener(this);
        btPrintList.setToolTipText("Imprimir");

        btShowDisable = new JToggleButton();
        btShowDisable.setFont(f);
        btShowDisable.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "file-warning.png", 22, 22)));
        btShowDisable.setActionCommand(AC_SHOW_DISABLE);
        btShowDisable.addActionListener(this);
        btShowDisable.setToolTipText("Ver deshabilitados");
        btShowDisable.setSelected(showDisableItems);

        ImageIcon searchIcon = new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "search.png", 16, 16));
        ImageIcon clearIcon = new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "cancel.png", 22, 22));
        ProgAction actionSearch = new ProgAction("", clearIcon, "", 's') {
            @Override
            public void actionPerformed(ActionEvent ev) {
                cleanSearch();
            }
        };
        regSearch = new Registro(BoxLayout.X_AXIS, "", "", 110, actionSearch);
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

        List filters = loadFilters();
        regFilters = new Registro(BoxLayout.X_AXIS, "Items", new String[1], 50);
        regFilters.setHeight(32);
        regFilters.setText(filters.toArray());
        regFilters.setActionCommand(AC_CHANGE_ITEMS);
        regFilters.addActionListener(this);
        filterSelected = regFilters.getText();

        Set tags = loadTags();
        regTags = new Registro(BoxLayout.X_AXIS, "Tags", new String[1], 50);
        regTags.setHeight(32);
        regTags.setText(tags.toArray());
        regTags.setActionCommand(AC_CHANGE_TAGS);
        regTags.addActionListener(this);
        tagSelected = regTags.getText();

        panelButtons.add(regSearch);
        panelButtons.add(regFilters);
        panelButtons.add(regTags);
        panelButtons.add(btRefresh);
        panelButtons.add(btShowDisable);
        panelButtons.add(btAdd);
        panelButtons.add(btLoad);
        panelButtons.add(btDesc);
        panelButtons.add(btConciliation);
        panelButtons.add(btExport);
        panelButtons.add(btSnapShot);        
        panelButtons.add(btPrintList);

        String[] colNames = new String[]{"N°", "Item", "Cantidad", "Medida", "Cost", "Price", "Min", "Cost. Total"};

        model = new MyDefaultTableModel(colNames, 0);
        tableItems.setModel(model);
        tableItems.setRowHeight(24);
        tableItems.setFont(new Font("Tahoma", 0, 16));

        DefaultListSelectionModel selectionModel = new DefaultListSelectionModel();
        selectionModel.addListSelectionListener(this);

        tableItems.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableItems.setSelectionModel(selectionModel);
        tableItems.getTableHeader().setReorderingAllowed(false);

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

        itemVer = new JMenuItem("Ver");
        itemVer.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int r = tableItems.getSelectedRow();
                String id = tableItems.getValueAt(r, 0).toString();
                showPanelEditItem(id);
            }

        });
        itemCargar = new JMenuItem("Cargar");
        itemCargar.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int r = tableItems.getSelectedRow();
                String id = tableItems.getValueAt(r, 0).toString();
                Item item = app.getControl().getItemWhere("id=" + id);
                app.getGuiManager().showPanelSelItem(item, PanelInventory.this);
            }
        });

        itemDescargar = new JMenuItem("Descargar");
        itemDescargar.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int r = tableItems.getSelectedRow();
                String id = tableItems.getValueAt(r, 0).toString();
                Item item = app.getControl().getItemWhere("id=" + id);
                app.getGuiManager().showPanelDownItem(item, PanelInventory.this);
            }
        });

        itemLinks = new JMenuItem("Enlaces");
        itemLinks.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int r = tableItems.getSelectedRow();
                String id = tableItems.getValueAt(r, 0).toString();
                showEnlaces(id);
            }

        });

        makePopup(-1);

        popupListenerTabla = new MyPopupListener();

        tableItems.addMouseListener(popupListenerTabla);

        pnDetail = new PanelReportProductDetail(app);

        jPanel1.setLayout(new BorderLayout());
        jPanel1.add(pnDetail);
       
        populateTable();

    }

    private JPopupMenu makePopup(int row) {
        boolean onSnap = false;
        boolean onEnab = false;
        if (row >= 0) {
            long idItem = Long.parseLong(tableItems.getValueAt(row, 0).toString());
            Item item = app.getControl().getItemWhere("id=" + idItem);
            if (item != null) {
                onSnap = item.isSnapshot();
                onEnab = item.isEnabled();
            }
        }
        String html = "<html>Snapshot <font color=" + (!onSnap ? "green" : "red") + ">[" + (!onSnap ? "ON" : "OFF") + "]</font><html>";
        JMenuItem itemSnapshot = new JMenuItem(html);
        itemSnapshot.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int r = tableItems.getSelectedRow();
                String id = tableItems.getValueAt(r, 0).toString();
                Item item = app.getControl().getItemWhere("id=" + id);
                item.setSnapshot(!item.isSnapshot());
                app.getControl().updateItem(item);
            }
        });

        String htmlEnab = "<html>Habilitado <font color=" + (!onEnab ? "green" : "red") + ">[" + (!onEnab ? "ON" : "OFF") + "]</font><html>";
        JMenuItem itemEnabled = new JMenuItem(htmlEnab);
        itemEnabled.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int r = tableItems.getSelectedRow();
                String id = tableItems.getValueAt(r, 0).toString();
                Item item = app.getControl().getItemWhere("id=" + id);
                item.setEnabled(!item.isEnabled());
                app.getControl().updateItem(item);
            }
        });

        JPopupMenu popupTable = new JPopupMenu();

        Permission perm = app.getControl().getPermissionByName("download-items-inventary");
        itemDescargar.setEnabled(app.getControl().hasPermission(app.getUser(), perm));

        perm = app.getControl().getPermissionByName("load-items-inventary");
        itemCargar.setEnabled(app.getControl().hasPermission(app.getUser(), perm));

        popupTable.add(itemVer);
        popupTable.addSeparator();
        popupTable.add(itemCargar);

        popupTable.add(itemDescargar);
        popupTable.addSeparator();
        popupTable.add(itemLinks);
        popupTable.addSeparator();
        popupTable.add(itemSnapshot);
        popupTable.addSeparator();
        popupTable.add(itemEnabled);

        return popupTable;
    }

    private Set loadTags() {
        List<String> tagsInventoryList = app.getControl().getTAGSInventoryList("");
        tagsInventoryList.add(0, FILTER_ITEM_TODOS);
        LinkedHashSet<String> listTags = new LinkedHashSet<>();
        if (!tagsInventoryList.isEmpty()) {
            //split(,) la lista de tags de cada item, lo pasa a lowercase y filtra que no este vacio el string
            listTags = tagsInventoryList.stream()
                    .flatMap(Pattern.compile(",")::splitAsStream)
                    .filter(tag -> !tag.isEmpty())
                    .map(tag -> FILTER_ITEM_TAGS + tag.toUpperCase().trim())
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        }
        return listTags;
    }

    private List loadFilters() {
        List filters = new ArrayList();
        filters.add(FILTER_ITEM_TODOS);
        filters.add(FILTER_ITEM_STOCK_REGULAR);
        filters.add(FILTER_ITEM_STOCK_MINIMO);
        filters.add(FILTER_ITEM_NO_AGOTADOS);
        filters.add(FILTER_ITEM_AGOTADOS);
        return filters;
    }
    private static final String FILTER_ITEM_AGOTADOS = "AGOTADOS";
    private static final String FILTER_ITEM_NO_AGOTADOS = "CON EXISTENCIAS";
    private static final String FILTER_ITEM_TAGS = "TAG: ";
    private static final String FILTER_ITEM_STOCK_REGULAR = "STOCK REGULAR";
    private static final String FILTER_ITEM_STOCK_MINIMO = "STOCK MINIMO";
    private static final String FILTER_ITEM_TODOS = "TODOS";
    private static final String AC_CHANGE_ITEMS = "AC_CHANGE_ITEMS";
    private static final String AC_CHANGE_TAGS = "AC_CHANGE_TAGS";
    public static final String AC_EXPORT_TO = "AC_EXPORT_TO";
    public static final String AC_SHOW_SNAPSHOT = "AC_SHOW_SNAPSHOT";
    public static final String AC_SHOW_DISABLE = "AC_SHOW_DISABLE";
    public static final String AC_ADD_CONCILIATION = "AC_ADD_CONCILIATION";
    public static final String AC_REFRESH_ITEMS = "AC_REFRESH_ITEMS";
    public static final String AC_LOAD_ITEM = "AC_LOAD_ITEM";
    public static final String AC_SHOW_ADD_ITEM = "AC_SHOW_ADD_ITEM";
    public static final String AC_DOWNLOAD_ITEM = "AC_DOWNLOAD_ITEM";
    public static final String AC_PRINT_LIST = "AC_PRINT_LIST";

    private void showPanelEditItem(String id) {
        Item item = app.getControl().getItemWhere("id=" + id);
        app.getGuiManager().showPanelAddItem(PanelInventory.this, item);
    }

    private void showEnlaces(String id) throws NumberFormatException {
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

    private void cleanSearch() {
        regSearch.setText("");
    }

    private void filtrar() {
        String text = regSearch.getText();
        boolean enable = text.isEmpty();
        regFilters.setEnabled(enable);
        regTags.setEnabled(enable);
        if (enable) {
            filtrarItems();
            return;
        }

        ArrayList<Item> listItems = app.getControl().getItemList(stFilterDisable, "name");

        List<Item> listFiltered = listItems.stream()
                .filter(item -> item.getName().toUpperCase().contains(text.toUpperCase()))
                .collect(Collectors.toList());

        populateTable(listFiltered);
    }

    private void filtrarItems(String filtro) {
        Predicate<Item> filterAgotados = itm -> itm.getQuantity() <= 0;
        Predicate<Item> filterMinimo = itm -> itm.getQuantity() <= itm.getStockMin();
        Predicate<Item> filterRegular = itm -> itm.getQuantity() > itm.getStockMin();

        List<Item> listItems;
        if (filtered) {
            listItems = listFiltered;
        } else {
            listItems = app.getControl().getItemList(stFilterDisable, "name");
        }
        List listRes = new ArrayList();
        if (FILTER_ITEM_AGOTADOS.equals(filtro)) {
            listFiltered = listItems.stream().filter(filterAgotados).collect(Collectors.toList());
            filtered = true;
        } else if (FILTER_ITEM_STOCK_MINIMO.equals(filtro)) {
            listFiltered = listItems.stream().filter(filterMinimo.and(filterAgotados.negate())).collect(Collectors.toList());
            filtered = true;
        } else if (FILTER_ITEM_STOCK_REGULAR.equals(filtro)) {
            listFiltered = listItems.stream().filter(filterRegular).collect(Collectors.toList());
            filtered = true;
        } else {
            filtered = false;
            listFiltered = app.getControl().getItemList(stFilterDisable, "name");
        }
        populateTable(listFiltered);
    }

    private void filtrarItems() {

        String filtro = regFilters.getText();
        String tag = regTags.getText();

        Predicate<Item> filterAgotados = itm -> itm.getQuantity() <= 0;
        Predicate<Item> filterNoAgotados = itm -> itm.getQuantity() > 0;
        Predicate<Item> filterMinimo = itm -> itm.getQuantity() <= itm.getStockMin();
        Predicate<Item> filterRegular = itm -> itm.getQuantity() > itm.getStockMin();
        Predicate<Item> filterTags = itm -> itm.getTagsSt().contains(tag.substring(5).toLowerCase());

        ArrayList<Item> listItems = app.getControl().getItemList(stFilterDisable, "name");
        Stream<Item> stream = listItems.stream();

        if (regFilters.getSelected() > 0) {
            if (FILTER_ITEM_AGOTADOS.equals(filtro)) {
                stream = stream.filter(filterAgotados);
            } else if (FILTER_ITEM_NO_AGOTADOS.equals(filtro)) {
                stream = stream.filter(filterNoAgotados);
            } else if (FILTER_ITEM_STOCK_MINIMO.equals(filtro)) {
                stream = stream.filter(filterMinimo.and(filterAgotados.negate()));
            } else if (FILTER_ITEM_STOCK_REGULAR.equals(filtro)) {
                stream = stream.filter(filterRegular);
            }
        }
        if (regTags.getSelected() > 0) {
            stream = stream.filter(filterTags);
        }

        populateTable(stream.collect(Collectors.toList()));
    }

    private void populateTable() {
        populateTable(app.getControl().getItemList(stFilterDisable, "name"));
    }

    private void populateTable(List<Item> itemList) {

        SwingWorker sw = new SwingWorker() {
            
            @Override
            protected Object doInBackground() throws Exception {
                model.setRowCount(0);
                localList = itemList;
                if (filtered) {
                    localList = listFiltered;
                }
                for (int i = 0; i < localList.size(); i++) {
                    Item item = localList.get(i);
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
            refreshItemsFiltered();
        } else if (AC_ADD_ITEM_TO_TABLE.equals(evt.getPropertyName())) {
            refreshItemsFiltered();
        } else if (PanelNewConciliacion.ACTION_SAVE_CONCILIACION.equals(evt.getPropertyName())) {
            refreshItemsFiltered();
        } else if (PanelAddItem.AC_DELETE_ITEM.equals(evt.getPropertyName())) {
            refreshItemsFiltered();
        } else if (PanelAddItem.AC_UPDATE_ITEM.equals(evt.getPropertyName())) {
            refreshItemsFiltered();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (AC_SHOW_ADD_ITEM.equals(e.getActionCommand())) {
            Permission perm = app.getControl().getPermissionByName("add-items-inventary");
            if (app.getControl().hasPermission(app.getUser(), perm)) {
                app.getGuiManager().showPanelAddItem(this, null);
            } else {
                GUIManager.showErrorMessage(this, "No tiene permisos para realizar esta accion", "Error de privilegios");
            }
        } else if (AC_SHOW_SNAPSHOT.equals(e.getActionCommand())) {
            Permission perm = app.getControl().getPermissionByName("show-snapshot-inventary");
            if (app.getControl().hasPermission(app.getUser(), perm)) {
                app.getGuiManager().showPanelSnapShot();
            } else {
                GUIManager.showErrorMessage(this, "No tiene permisos para realizar esta accion", "Error de privilegios");
            }
        } else if (AC_LOAD_ITEM.equals(e.getActionCommand())) {
            Permission perm = app.getControl().getPermissionByName("load-items-inventary");
            if (app.getControl().hasPermission(app.getUser(), perm)) {
                app.getGuiManager().showPanelSelItem(this);
            } else {
                GUIManager.showErrorMessage(this, "No tiene permisos para realizar esta accion", "Error de privilegios");
            }

        } else if (AC_REFRESH_ITEMS.equals(e.getActionCommand())) {
            refreshItemsFiltered();
        } else if (AC_ADD_CONCILIATION.equals(e.getActionCommand())) {
            Permission perm = app.getControl().getPermissionByName("conciliate-items-inventary");
            if (app.getControl().hasPermission(app.getUser(), perm)) {
                app.getGuiManager().showPanelConciliacion(true);
            } else {
                GUIManager.showErrorMessage(this, "No tiene permisos para realizar esta accion", "Error de privilegios");
            }

        } else if (AC_DOWNLOAD_ITEM.equals(e.getActionCommand())) {
            Permission perm = app.getControl().getPermissionByName("download-items-inventary");
            if (app.getControl().hasPermission(app.getUser(), perm)) {
                app.getGuiManager().showPanelDownItem(this);
            } else {
                GUIManager.showErrorMessage(this, "No tiene permisos para realizar esta accion", "Error de privilegios");
            }

        } else if (AC_CHANGE_ITEMS.equals(e.getActionCommand())) {
            filterSelected = regFilters.getSelectedItem();
            filtrarItems();
        } else if (AC_CHANGE_TAGS.equals(e.getActionCommand())) {
            tagSelected = regTags.getSelectedItem();
            filtrarItems();
        }

        if (e.getActionCommand().equals(AC_EXPORT_TO)) {
            Permission perm = app.getControl().getPermissionByName("export-items-inventary");
            if (app.getControl().hasPermission(app.getUser(), perm)) {
                exportToXLS();
            }else {
                GUIManager.showErrorMessage(this, "No tiene permisos para realizar esta accion", "Error de privilegios");
            }
        }

        if (e.getActionCommand().equals(AC_SHOW_DISABLE)) {

            stFilterDisable = btShowDisable.isSelected() ? "" : "enabled=1";
            ConfigDB config = app.getControl().getConfig(Configuration.SHOW_DISABLE_ITEMS);
            config.setValor(String.valueOf(!btShowDisable.isSelected()));
            app.getControl().updateConfig(config);
            refreshItemsFiltered();
        }

        if (e.getActionCommand().equals(AC_PRINT_LIST)) {
            Permission perm = app.getControl().getPermissionByName("print-items-inventary");
            if (app.getControl().hasPermission(app.getUser(), perm)) {    
                printList();
            }else {
                GUIManager.showErrorMessage(this, "No tiene permisos para realizar esta accion", "Error de privilegios");
            }
        }
    }

    private void printList() {
        ConfigDB config = app.getControl().getConfigLocal(Configuration.PRINTER_SELECTED);
        String propPrinter = config != null ? config.getValor() : "";
        if (propPrinter.isEmpty()) {
            GUIManager.showErrorMessage(null, "No ha seleccionado una impresora valida para imprimir", "Impresora no encontrada");
            return;
        }
        String printerName = propPrinter;
        String tag = regTags.getText().substring(5);
        app.getPrinterService().imprimirInventario(localList,tag.toUpperCase(), printerName);      
    }

    private void exportToXLS() {
         LOGGER.debug("Exportando...");
    
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

    private void refreshItemsFiltered() {
        if (regSearch.getText().isEmpty()) {
            regFilters.setText(loadFilters().toArray());
            regFilters.setSelected(filterSelected);
            regTags.setText(loadTags().toArray());
            regTags.setSelected(tagSelected);
            filtrarItems();
        } else {
            filtrar();
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

    public class MyPopupListener implements MouseListener {

        boolean seleccionar;

        public MyPopupListener() {
            this.seleccionar = true;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            isPopupTrigger(e);
        }

        @Override
        public void mousePressed(MouseEvent e) {
            isPopupTrigger(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            isPopupTrigger(e);
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            isPopupTrigger(e);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            isPopupTrigger(e);
        }

        public void isPopupTrigger(MouseEvent e) {
            if (e.isPopupTrigger()) {
                if (seleccionar) {
                    Object source = e.getSource();
                    if (source != null && (source instanceof JTable)) {
                        JTable table = (JTable) e.getSource();
                        int rowAtPoint = table.rowAtPoint(e.getPoint());
                        table.getSelectionModel().setSelectionInterval(rowAtPoint, rowAtPoint);
                        int row = table.getSelectedRow();
                        makePopup(row).show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            }

        }

    }
}
