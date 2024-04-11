package com.rb.gui;

import static javax.swing.BorderFactory.createLineBorder;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.math.BigDecimal;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.EventObject;
import java.util.List;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListSelectionModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bx.gui.MyDefaultTableModel;
import org.dz.PanelCapturaMod;
import org.dz.Utiles;
import org.jfree.util.PaintUtilities;

import com.rb.Aplication;
import com.rb.Configuration;
import com.rb.GUIManager;
import com.rb.MyConstants;
import com.rb.ProgAction;
import com.rb.domain.Cycle;
import com.rb.domain.Invoice;
import com.rb.domain.Permission;
import com.rb.domain.Product;
import com.rb.domain.ProductoPed;
import com.rb.domain.Table;
import com.rb.domain.Waiter;
import com.rb.gui.util.MyDatePickerImp;
import com.rb.gui.util.MyPopupListener;

/**
 *
 * @author lrod
 */
public class PanelListPedidos extends PanelCapturaMod implements ActionListener, ListSelectionListener {

    private final Aplication app;
    private MyDefaultTableModel model;
    private boolean filtroActivado;
    private TableRowSorter<MyDefaultTableModel> lastSorter;
    private String queryDate;
    private Color COLOR_BACKG;

    private final Logger log = LogManager.getLogger(PanelListPedidos.class.getCanonicalName());

    public static final String TODOS = " - TODOS - ";
    public static final String PERIODO_MES = "MES";
    public static final String PERIODO_SEMANA = "SEMANA";
    public static final String PERIODO_DIA = "DIA";
    public static final String PERIODO_OTRO_DIA = "OTRO DIA";
    public static final String PERIODO_HISTORICO = "HISTORICO";
    public static final String PERIODO_FECHA = "FECHA";
    public static final String PERIODO_CICLO = "CICLO";
    public static final String ST_FACTURADO = "FACTURADO";
    public static final String ST_ENTREGADO = "ENTREGADO";
    public static final String ST_DEVUELTO = "DEVUELTO";
    public static final String ST_DESPACHADO = "DESPACHADO";

    public static final String AC_SHOW_INVOICE = "AC_SHOW_INVOICE";

    private JPopupMenu popupTable;
    private MyPopupListener popupListenerTabla;
    private ArrayList<Product> productsList;
    private ArrayList<Waiter> waitersList;
    private SimpleDateFormat formFecha;
    private MyDatePickerImp datePick1;

    private ProgAction acSearchCycle;
    private JTextField tfCycle;
    private String selPeriodo;
    private String colSelection;
    private ProgAction actionSearch;
    private ImageIcon clearIcon;

//    private org.dz.MyDatePickerImp dpFinal;
//    private org.dz.MyDatePickerImp dpInicio;
    /**
     * Creates new form PanelListPedidos
     *
     * @param app
     */
    public PanelListPedidos(Aplication app) {
        this.app = app;
        initComponents();
        createComponents();
    }

    private void createComponents() {

        formFecha = new SimpleDateFormat("dd MMMM yyyy");

        datePick1.addPropertyChangeListener(this);

        colSelection = PaintUtilities.colorToString(tableList.getSelectionBackground().darker());

        regDate.setActionCommand(AC_CHANGE_DATE);
        regDate.addActionListener(this);

        String[] colNames = {"Factura", "Fecha", "Estado", "Ciclo", "Tipo", "Cliente", "Mesa", "Mesero", "Valor", "Servicio", " Accion"};
        model = new MyDefaultTableModel(colNames, 0);
        tableList.setModel(model);
        tableList.setRowHeight(24);

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
                    regDate.setBorderToError();
                    return;
                }
                regDate.setBorderToNormal();
                Cycle cycle = app.getControl().getCycle(Integer.valueOf(stCycle));
                if (cycle != null) {
                    makeTitleCycle(cycle);
                    filtradoSQL();
                } else {
                    makeTitleCycle(null);
                    clearTable();
                }
            }
        };

        COLOR_BACKG = Utiles.colorAleatorio(125, 255).brighter();

        tableList.getTableHeader().setBackground(COLOR_BACKG);
        tableList.getTableHeader().setReorderingAllowed(false);

        TablaCellRenderer tRenderer = new TablaCellRenderer(true, app.getDCFORM_P());

        int[] colW = new int[]{50, 50, 40, 15, 50, 60, 30, 50, 60, 60, 30};
        for (int i = 0; i < colW.length; i++) {
            tableList.getColumnModel().getColumn(i).setMinWidth(colW[i]);
            tableList.getColumnModel().getColumn(i).setPreferredWidth(colW[i]);
            tableList.getColumnModel().getColumn(i).setCellRenderer(new TablaCellRenderer(true, null));
        }

        tableList.getColumnModel().getColumn(8).setCellRenderer(tRenderer);
        tableList.getColumnModel().getColumn(9).setCellRenderer(tRenderer);
        tableList.getColumnModel().getColumn(model.getColumnCount() - 1).setCellEditor(new BotonEditor(tableList, this, "AC_MOD_USER"));
        tableList.getColumnModel().getColumn(model.getColumnCount() - 1).setCellRenderer(new ButtonCellRenderer("Ver"));

        popupTable = new JPopupMenu();
        popupListenerTabla = new MyPopupListener(popupTable, true);
        JMenuItem item1 = new JMenuItem("Anular");
        item1.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int r = tableList.getSelectedRow();
                String fact = tableList.getValueAt(r, 0).toString();
                Invoice inv = app.getControl().getInvoiceByCode(fact);
                StringBuilder msg = new StringBuilder();
                msg.append("<html>Esta seguro que desea anular la factura N° ");
                msg.append("<font color=blue>").append(inv.getFactura());
                msg.append(" </font> del ");
                msg.append("<font color=blue>").append(formFecha.format(inv.getFecha())).append("</font>");
                msg.append("<p>Por valor de: ").append("<font color=blue>").append(app.getDCFORM_P().format(inv.getValor())).append("</font></p></html>");
                msg.append("</html>");
                int opt = JOptionPane.showConfirmDialog(null, msg, "Advertencia", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                if (opt == JOptionPane.OK_OPTION) {
                    if (inv.getStatus() != Invoice.ST_ANULADA) {
                        inv.setStatus(Invoice.ST_ANULADA);
                        app.getControl().updateInvoice(inv);
                        List<ProductoPed> list = inv.getProducts();
                        app.getControl().restoreInventory(list);
                        loadPedidos();
                    } else {
                        GUIManager.showErrorMessage(PanelListPedidos.this, "La factura ya ha sido anulada", "Factura anulada");
                    }
                }

            }
        });
        popupTable.add(item1);

        JMenuItem item2 = new JMenuItem("Cargar");
        item2.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int r = tableList.getSelectedRow();
                String fact = tableList.getValueAt(r, 0).toString();
                Invoice inv = app.getControl().getInvoiceByCode(fact);

                pcs.firePropertyChange(AC_SHOW_INVOICE, inv, null);

                Permission perm = app.getControl().getPermissionByName("show-orders-module");
                app.getGuiManager().showBasicPanel(app.getGuiManager().getPanelBasicOrders(), perm);

            }
        });
        popupTable.add(item2);

        tableList.addMouseListener(popupListenerTabla);

        DefaultListSelectionModel selModel = new DefaultListSelectionModel();
        selModel.addListSelectionListener(this);

        tableList.setSelectionModel(selModel);

//        clearIcon = new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "delete.png", 18, 18));
//        actionSearch = new ProgAction("", clearIcon, "", 's') {
//            @Override
//            public void actionPerformed(ActionEvent ev) {
//                cleanSearch();
//            }
//        };
        String[] TIPOS_VENTAS = {TODOS,
            MyConstants.PEDIDO_LOCAL.toUpperCase(),
            MyConstants.PEDIDO_DOMICILIO.toUpperCase(),
            MyConstants.PEDIDO_PARA_LLEVAR.toUpperCase()
        };

        regTipo.setText(TIPOS_VENTAS);
        regTipo.setActionCommand(ACTION_SEARCH);
        regTipo.addActionListener(this);
        regTipo.setBackground(COLOR_BACKG);

        productsList = app.getControl().getProductsList("", "name");
        Product PTODOS = new Product(0);
        PTODOS.setName(TODOS);
        productsList.add(0, PTODOS);
        regProduct.setActionCommand(ACTION_SEARCH);
        regProduct.addActionListener(this);
        regProduct.setText((productsList.toArray()));
        regProduct.setBackground(COLOR_BACKG);

//        String[] ESTADOS = {__TODOS__, ST_ENTREGADO, ST_DEVUELTO, ST_DESPACHADO, ST_FACTURADO};
        waitersList = app.getControl().getWaitresslList("", "name");
        Waiter WTODOS = new Waiter();
        WTODOS.setName(TODOS);
        waitersList.add(0, WTODOS);
        regMesero.setLabelText("Mesero");
        regMesero.setText(waitersList.toArray());
        regMesero.setActionCommand(ACTION_SEARCH);
        regMesero.addActionListener(this);
        regMesero.setBackground(COLOR_BACKG);

        String[] PERIODOS = {PERIODO_DIA, PERIODO_OTRO_DIA, PERIODO_SEMANA, PERIODO_MES, PERIODO_HISTORICO, PERIODO_CICLO};
        regPeriodo.setText(PERIODOS);
        regPeriodo.setActionCommand(ACTION_SEL_PERIODO);
        regPeriodo.addActionListener(this);
        regPeriodo.setBackground(COLOR_BACKG);

        lbPeriodo.setOpaque(true);
        lbPeriodo.setBackground(COLOR_BACKG);
        lbPeriodo.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        lbPeriodo1.setOpaque(true);
        lbPeriodo1.setBackground(COLOR_BACKG);
        lbPeriodo1.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        lbPeriodo1.setVisible(false);

        btFilters.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "view-filter.png", 18, 18)));
        btFilters.setActionCommand(ACTION_ACTIVATE_FILTER);
        btFilters.addActionListener(this);
        btFilters.setToolTipText("Activar Filtros");

        btConfig.setText("");
        btConfig.setToolTipText("Opciones de visualización");
        btConfig.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "layer-visible-on.png", 18, 18)));
        btConfig.setActionCommand(ACTION_SHOW_CONFIG);
        btConfig.addActionListener(this);
        btConfig.setEnabled(false);

        btUpdate.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "update.png", 18, 18)));
        btUpdate.setActionCommand(ACTION_UPDATE_LIST);
        btUpdate.addActionListener(this);

        ImageIcon searchIcon = new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "search.png", 16, 16));
        regSearch.setLabelIcon(searchIcon);
        regSearch.setLabelHorizontalAlignment(SwingConstants.RIGHT);
        regSearch.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                filtrar(regSearch.getText().toUpperCase(), -1, MyConstants.FILTER_TEXT_INT_CONTAINS);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filtrar(regSearch.getText().toUpperCase(), -1, MyConstants.FILTER_TEXT_INT_CONTAINS);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filtrar(regSearch.getText().toUpperCase(), -1, MyConstants.FILTER_TEXT_INT_CONTAINS);
            }
        });

//        dpFinal = new MyDatePickerImp();
//        dpFinal.setDate(new Date());
//        dpFinal.setTextEditable(false);
//        dpFinal.addPropertyChangeListener(this);
//        dpFinal.setVisible(false);
//
//        dpInicio = new MyDatePickerImp();
//        int numDias = 30;
//        Calendar cal = Calendar.getInstance();
//        cal.add(Calendar.DAY_OF_MONTH, -numDias);
//        dpInicio.setDate(cal.getTime());
//        dpInicio.setTextEditable(false);
//        dpInicio.addPropertyChangeListener(this);
//        dpInicio.setVisible(false);
//        tablaInventario.setFont(Aplication.DEFAULT_FONT_TF.deriveFont(13));
        regDate.setVisible(false);

        pnFilters.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                changeLayoutScreen();
            }

            @Override
            public void componentMoved(ComponentEvent e) {

            }

            @Override
            public void componentShown(ComponentEvent e) {

            }

            @Override
            public void componentHidden(ComponentEvent e) {

            }
        });

        lbStatus.setBackground(new Color(252, 220, 224));
        lbStatus1.setBackground(new Color(252, 220, 224));

        queryDate = "";
        filtroActivado = false;
        activarFiltros(filtroActivado);

        changeLayoutScreen();
        updateConfig();

    }
    private static final String AC_SEARCH_CYCLE = "AC_SEARCH_CYCLE";

    private static final String AC_CHANGE_DATE = "AC_CHANGE_DATE";

    public void changeLayoutScreen() {
        int LIM = 1110;
        if (pnFilters.getWidth() < LIM) {
            lbPeriodo.setVisible(false);
            lbPeriodo1.setVisible(true);
        } else {
            lbPeriodo.setVisible(true);
            lbPeriodo1.setVisible(false);
        }
    }

    public void updateConfig() {
        String periodoDF = app.getConfiguration().getProperty(Configuration.PN_ENTRADA_PERIODO, PERIODO_DIA);
        regPeriodo.setText(periodoDF);
    }

    public static final String ACTION_ACTIVATE_FILTER = "ACTION_ACTIVATE_FILTER";
    public static final String ACTION_UPDATE_LIST = "ACTION_UPDATE_LIST";
    public static final String ACTION_SHOW_CONFIG = "ACTION_SHOW_CONFIG";
    public static final String ACTION_SEL_ESTADO = "ACTION_SEL_ESTADO";
    public static final String ACTION_SEL_WAITERS = "ACTION_SEL_WAITERS";
    public static final String ACTION_SEL_PERIODO = "ACTION_SEL_PERIODO";
    public static final String ACTION_SEARCH = "ACTION_SEARCH";
    public static final String ACTION_CLEAR_SEARCH = "ACTION_CLEAR_SEARCH";

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnFilters = new javax.swing.JPanel();
        regTipo = new com.rb.gui.util.Registro(BoxLayout.Y_AXIS, "Tipo", new String[0]);
        regPeriodo = new com.rb.gui.util.Registro(BoxLayout.Y_AXIS, "Periodo", new Object[0]);
        lbPeriodo = new javax.swing.JLabel();
        regMesero = new com.rb.gui.util.Registro(BoxLayout.Y_AXIS, "Estado", new String[0]);
        regProduct = new com.rb.gui.util.Registro(BoxLayout.Y_AXIS, "Producto", new String[0]);
        btConfig = new javax.swing.JButton();
        btUpdate = new javax.swing.JButton();
        btFilters = new javax.swing.JToggleButton();
        datePick1 = new MyDatePickerImp(new Date(), true);
        regDate = new com.rb.gui.util.Registro(BoxLayout.Y_AXIS, "Fecha", datePick1);
        clearIcon = new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "delete.png", 18, 18));
        actionSearch = new ProgAction("", clearIcon, "", 's') {
            @Override
            public void actionPerformed(ActionEvent ev) {
                cleanSearch();
            }
        };
        regSearch = new com.rb.gui.util.Registro(BoxLayout.X_AXIS, "", "", 80, actionSearch);
        lbPeriodo1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableList = new javax.swing.JTable();
        lbStatus = new javax.swing.JLabel();
        lbStatus1 = new javax.swing.JLabel();

        pnFilters.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lbPeriodo.setText("jLabel2");

        lbPeriodo1.setText("jLabel2");

        javax.swing.GroupLayout pnFiltersLayout = new javax.swing.GroupLayout(pnFilters);
        pnFilters.setLayout(pnFiltersLayout);
        pnFiltersLayout.setHorizontalGroup(
            pnFiltersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnFiltersLayout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(regSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btFilters, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addGroup(pnFiltersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(pnFiltersLayout.createSequentialGroup()
                        .addComponent(regTipo, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(regMesero, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(regProduct, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(regPeriodo, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(regDate, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lbPeriodo1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbPeriodo, javax.swing.GroupLayout.DEFAULT_SIZE, 319, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btConfig, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        pnFiltersLayout.setVerticalGroup(
            pnFiltersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnFiltersLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(pnFiltersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(regSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btFilters, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(regMesero, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(regProduct, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(regPeriodo, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(regDate, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbPeriodo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btConfig, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(regTipo, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbPeriodo1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pnFiltersLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {regDate, regMesero, regPeriodo, regProduct});

        tableList.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(tableList);

        lbStatus.setBackground(new java.awt.Color(147, 153, 165));
        lbStatus.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbStatus.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 0, new java.awt.Color(0, 0, 0)), javax.swing.BorderFactory.createEmptyBorder(1, 5, 1, 1)));
        lbStatus.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        lbStatus.setOpaque(true);

        lbStatus1.setBackground(new java.awt.Color(147, 153, 165));
        lbStatus1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbStatus1.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 1, 1, new java.awt.Color(0, 0, 0)), javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 5)));
        lbStatus1.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        lbStatus1.setOpaque(true);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(lbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 512, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(lbStatus1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(pnFilters, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1319, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnFilters, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE)
                .addGap(3, 3, 3)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbStatus1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void loadPedidos() {
        log.debug("On funcion loadPedidos");
        populateTabla(queryDate);
    }

    private void clearTable() {
        model.setRowCount(0);
    }

    private void cleanSearch() {
        regSearch.setText("");
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        int[] selectedRows = tableList.getSelectedRows();
        double total = 0, service = 0;

        for (int selectedRow : selectedRows) {
            String status = tableList.getValueAt(selectedRow, 2).toString();  //STATUS
//            if (!Invoice.STATUSES[Invoice.ST_ANULADA].equalsIgnoreCase(status)) {
            double valInvoice = Double.parseDouble(tableList.getValueAt(selectedRow, 8).toString());
            double servInvoice = Double.parseDouble(tableList.getValueAt(selectedRow, 9).toString());

            total += valInvoice;
            service += servInvoice;
//            }
        }
        makeStatusLabelSelecteds(selectedRows.length, total, service);
    }

    private void makeStatusLabelSelecteds(int rows, double tot, double serv) {

        if (rows > 0) {
            lbStatus.setText("<html>  <font color=" + colSelection + ">Selección</font> [<font color=blue>" + (rows)
                    + "</font> pedidos]  "
                    + "Servicio: <font color=green>" + app.DCFORM_P.format(serv)
                    + "</font> - Total: <font color=blue>" + app.DCFORM_P.format(tot) + "</font></html>");
        } else {
            lbStatus.setText("");
        }

    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (MyDatePickerImp.DATE_CHANGED.equals(evt.getPropertyName())) {
            makeQueryOtherDay(Calendar.getInstance());
            filtradoSQL();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (ACTION_SEARCH.equals(e.getActionCommand())) {
            filtradoSQL();
        } else if (AC_SEARCH_CYCLE.equals(e.getActionCommand())) {
            acSearchCycle.actionPerformed(new ActionEvent(tfCycle, tfCycle.hashCode(), AC_SEARCH_CYCLE));
        } else if (ACTION_UPDATE_LIST.equals(e.getActionCommand())) {
            btFilters.setSelected(false);
            updateCombos();
            activarFiltros(false);
            log.debug("Call from updatin list filtro");
            if (PERIODO_CICLO.equals(selPeriodo)) {
                filtradoSQL();
            }
        } else if (ACTION_CLEAR_SEARCH.equals(e.getActionCommand())) {
            regSearch.setText("");
        } else if (ACTION_ACTIVATE_FILTER.equals(e.getActionCommand())) {
            boolean selected = btFilters.isSelected();
            activarFiltros(selected);
            filtroActivado = selected;
            filtradoSQL();
        } else if (AC_CHANGE_DATE.equals(e.getActionCommand())) {
            makeQueryOtherDay(Calendar.getInstance());
            filtradoSQL();
        } else if (ACTION_SEL_PERIODO.equals(e.getActionCommand())) {
            selPeriodo = regPeriodo.getText();
            saveConfig(selPeriodo);
            Calendar cal = Calendar.getInstance();
            regDate.setVisible(false);
            if (null != selPeriodo) {
                switch (selPeriodo) {
                    case PERIODO_DIA: {
                        String date = new SimpleDateFormat("dd MMMM yyyy (EEE)").format(cal.getTime());
                        String today = app.DF_SQL.format(cal.getTime());
                        cal.add(Calendar.DATE, 1);
                        String added = app.DF_SQL.format(cal.getTime());
                        queryDate = "sale_date >='" + today + "' AND sale_date<'" + added + "'";
                        lbPeriodo.setText("<html><p color=gray>Hoy:<p color=blue size=+1>" + date.toUpperCase() + "<html>");
                        lbPeriodo1.setText("<html><p color=gray>Hoy:  <span color=blue size=+1>" + date.toUpperCase() + "<html>");
                        break;
                    }

                    case PERIODO_OTRO_DIA: {
                        regDate.setVisible(true);
                        regDate.setLabelText("Fecha");
                        regDate.setAction(null);
                        regDate.setComponent(datePick1);
                        makeQueryOtherDay(cal);
                        break;
                    }

                    case PERIODO_CICLO: {
                        regDate.setVisible(true);
                        regDate.setComponent(tfCycle);
                        regDate.setLabelText("Ciclo");
                        regDate.setAction(acSearchCycle);
                        if (tfCycle.getText().trim().isEmpty()) {
                            Cycle lastCycle = app.getControl().getLastCycle();
                            tfCycle.setText(String.valueOf(lastCycle.getId()));
                            makeTitleCycle(lastCycle);
                        }
                        break;
                    }

                    case PERIODO_SEMANA: {
                        int dayOfTheWeek = cal.get(Calendar.DAY_OF_WEEK);
                        cal.add(Calendar.DAY_OF_WEEK, Calendar.SUNDAY - dayOfTheWeek);
                        String date1 = app.DF.format(cal.getTime());
                        String iniQuery = app.DF_SQL.format(cal.getTime());
                        cal.add(Calendar.DAY_OF_WEEK, +7);
                        String finQuery = app.DF_SQL.format(cal.getTime());
                        String date2 = app.DF.format(cal.getTime());
                        queryDate = "sale_date>'" + iniQuery + "' AND sale_date<='" + finQuery + "'";
                        lbPeriodo.setText("<html><p color=gray>Semana:<p color=blue size=+1>" + date1 + " al " + date2 + "<html>");
                        lbPeriodo1.setText("<html><p color=gray>Semana:  <span color=blue size=+1>" + date1 + " al " + date2 + "<html>");
                        break;
                    }
                    case PERIODO_MES: {
                        String date = new SimpleDateFormat("MMMM").format(cal.getTime());
                        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
                        String iniQuery = app.DF_SQL.format(cal.getTime());
                        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
                        String finQuery = app.DF_SQL.format(cal.getTime());
                        queryDate = "sale_date>'" + (iniQuery) + "' AND sale_date<='" + finQuery + "'";
                        lbPeriodo.setText("<html><p color=gray>Mes:<p color=blue size=+1>" + date.toUpperCase() + "<html>");
                        lbPeriodo1.setText("<html><p color=gray>Mes:  <span color=blue size=+1>" + date.toUpperCase() + "<html>");
                        break;
                    }
                    case PERIODO_HISTORICO: {
                        queryDate = "";
                        Date fecha = new Date();
                        int rows = app.getControl().contarRows("select id from invoices");
                        if (rows > 0) {
                            //Demasiado tiempo consultando listado para obtener un solo dato
                            fecha = app.getControl().getPrimerRegistro("invoices", "sale_date");
                        }
                        String date1 = app.DF.format(fecha);
                        String date2 = app.DF.format(cal.getTime());
                        lbPeriodo.setText("<html><p color=gray>Historico:<p color=blue size=+1> Desde " + date1 + " hasta " + date2 + "<html>");
                        lbPeriodo1.setText("<html><p color=gray>Historico:  <span color=blue size=+1> Desde " + date1 + " hasta " + date2 + "<html>");
                        break;
                    }
                    default:
                        queryDate = "";
                        break;
                }
            }
            filtradoSQL();
        }

    }

    private void makeTitleCycle(Cycle cycle) {
        if (cycle != null) {
            String date1 = app.DF_FULL2.format(cycle.getInit());
            String date2 = "---";
            if (cycle.getEnd() != null) {
                date2 = app.DF_FULL2.format(cycle.getEnd());
            }
            lbPeriodo.setText("<html><p color=gray>Ciclo: [<span color=black>" + cycle.getId() + "<span>]<p color=blue size=+1>" + date1 + " / " + date2 + "<html>");
            lbPeriodo1.setText("<html><p color=gray>Ciclo: [<span color=black>" + cycle.getId() + "<span>]<span color=blue size=+1>" + date1 + " / " + date2 + "<html>");
        } else {
            String ciclo = tfCycle.getText();
            lbPeriodo.setText("<html><p color=gray>Ciclo: [<span color=black>" + ciclo + "<span>]<p color=blue size=+1>No existe<html>");
            lbPeriodo1.setText("<html><p color=gray>Ciclo: [<span color=black>" + ciclo + "<span>]<span color=blue size=+1>No existe<html>");
        }
    }

    private void makeQueryOtherDay(Calendar cal) {
        String fecha = regDate.getText();
        Date date = new Date();
        try {
            date = MyDatePickerImp.formatDate.parse(fecha);
        } catch (ParseException ex) {
            LogManager.getLogger(PanelListPedidos.class.getName()).log(Level.ERROR, ex.getMessage(), ex);
        }
        cal.setTime(date);

        String stDate = new SimpleDateFormat("dd MMMM yyyy (EEE)").format(cal.getTime());

        String today = app.DF_SQL.format(cal.getTime());
        cal.add(Calendar.DATE, 1);
        String added = app.DF_SQL.format(cal.getTime());
        queryDate = "sale_date >='" + today + "' AND sale_date<'" + added + "'";

        lbPeriodo.setText("<html><p color=gray>Dia:<p color=blue size=+1>" + stDate.toUpperCase() + "<html>");
        lbPeriodo1.setText("<html><p color=gray>Dia:  <span color=blue size=+1>" + stDate.toUpperCase() + "<html>");
    }

    public void filtradoSQL() {
        int tipo = regTipo.getSelected();
        long product = ((Product) regProduct.getSelectedItem()).getId();
        Waiter mesero = (Waiter) regMesero.getSelectedItem();
        if (PERIODO_CICLO.equals(selPeriodo)) {
            int ciclo = Integer.parseInt(regDate.getText());
            buscarFacturasByCycle(mesero.getId(), product, tipo, ciclo);
        } else {
            buscarFacturas(mesero.getId(), product, tipo);
        }
    }

    private void activarFiltros(boolean activar) {
        regProduct.setEnabled(activar);
        regTipo.setEnabled(activar);
        regMesero.setEnabled(activar);
    }

    protected void saveConfig(String selPeriodo) {
        SwingWorker sw = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                app.getConfiguration().setProperty(Configuration.PN_ENTRADA_PERIODO, selPeriodo);
                app.getConfiguration().save();
                return true;
            }
        };
        sw.execute();
    }

    private void buscarFacturas(int idMesero, long idProd, int idTipo) {
//        String date = "( fecha BETWEEN '" + app.DF.format(dIni) + "' AND '" + app.DF.format(dFin) + "')";
        String query = "";
        if (filtroActivado) {
            String tipo = idTipo == 0 ? "" : "i.deliveryType=" + idTipo + " ";
            String mesero = idMesero == 0 ? "" : !tipo.isEmpty() ? " AND i.idMesero=" + idMesero + "" : "i.idMesero=" + idMesero;
            String prod = (idProd < 1) ? "" : !mesero.isEmpty() ? " AND " + "p.id=" + idProd : (!tipo.isEmpty() ? "AND p.id=" : " p.id=") + idProd;
            query = tipo + mesero + prod;
        }
        query = !queryDate.isEmpty() ? (!query.isEmpty() ? (queryDate + " AND " + query) : queryDate) : !query.isEmpty() ? query : "";
        populateTabla(query);
    }

    private void buscarFacturasByCycle(int idMesero, long idProd, int idTipo, int cycle) {
//        String date = "( fecha BETWEEN '" + app.DF.format(dIni) + "' AND '" + app.DF.format(dFin) + "')";
        String query = "";
        if (filtroActivado) {
            String tipo = idTipo == 0 ? "" : "i.deliveryType=" + idTipo + " ";
            String mesero = idMesero == 0 ? "" : !tipo.isEmpty() ? " AND i.idMesero=" + idMesero + "" : "i.idMesero=" + idMesero;
            String prod = (idProd < 1) ? "" : !mesero.isEmpty() ? " AND " + "p.id=" + idProd : (!tipo.isEmpty() ? "AND p.id=" : " p.id=") + idProd;
            query = tipo + mesero + prod;
        }
        String queryCycle = "i.ciclo=" + cycle;
        query = !query.isEmpty() ? (query + " AND " + queryCycle) : queryCycle;
        populateTabla(query);
    }

    private void updateCombos() {

        regTipo.setSelected(0);

        productsList = app.getControl().getProductsList("", "name");
        Product PTODOS = new Product(0);
        PTODOS.setName(TODOS);
        productsList.add(0, PTODOS);
        regProduct.setText((productsList.toArray()));

        waitersList = app.getControl().getWaitresslList("", "name");
        Waiter WTODOS = new Waiter();
        WTODOS.setName(TODOS);
        waitersList.add(0, WTODOS);
        regMesero.setText(waitersList.toArray());
    }

    private void populateTabla(String query) {

        SwingWorker sw = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                model.setRowCount(0);

                ArrayList<Invoice> invoiceslList = app.getControl().getInvoicesLiteWhitProduct(query);
                BigDecimal total = new BigDecimal(0);
                double servicio = 0;
                int totalProducts = 0;
                int anuladas = 0;
                for (int i = 0; i < invoiceslList.size(); i++) {
                    Invoice invoice = invoiceslList.get(i);
                    Waiter waiter = app.getControl().getWaitressByID(invoice.getIdWaitress());
                    Table table = app.getControl().getTableByID(invoice.getTable());
                    if (invoice.getStatus() != Invoice.ST_ANULADA) {
                        total = total.add(invoice.getValor());
                        totalProducts += invoice.getNumItems();
                        servicio += invoice.getValueService();
                    } else {
                        anuladas++;
                    }
                    try {
                        model.addRow(new Object[]{
                            invoice.getFactura(),
                            app.DF_FULL2.format(invoice.getFecha()),
                            Invoice.STATUSES[invoice.getStatus()],
                            invoice.getCiclo(),
                            invoice.getTipoEntrega() + ":" + MyConstants.TIPO_PEDIDO[invoice.getTipoEntrega() - 1],
                            invoice.getIdCliente() == 1 ? "LOCAL" : invoice.getIdCliente(),
                            table != null ? table.getName() : "-",
                            waiter != null ? waiter.getName() : "-",
                            invoice.getValor(),
                            invoice.getValor().doubleValue() * invoice.getPorcService() / 100.0
                        });

                        model.setRowEditable(model.getRowCount() - 1, false);
                        model.setCellEditable(model.getRowCount() - 1, model.getColumnCount() - 1, true);

                    } catch (Exception e) {

                    }

                }

                lbStatus1.setText("<html>[ <font color=blue>" + (invoiceslList.size() - anuladas)
                        + "</font> pedidos - <font color=blue>" + totalProducts
                        + "</font> productos ]  "
                        + "Servicio: <font color=green>" + app.DCFORM_P.format(servicio)
                        + "</font> - Total: <font color=blue>" + app.DCFORM_P.format(total.doubleValue()) + "</font>  </html>");

                return true;
            }

            @Override
            protected void done() {
                app.getGuiManager().setDefaultCursor();
            }

        };
        app.getGuiManager().setWaitCursor();
        sw.execute();

    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btConfig;
    private javax.swing.JToggleButton btFilters;
    private javax.swing.JButton btUpdate;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbPeriodo;
    private javax.swing.JLabel lbPeriodo1;
    private javax.swing.JLabel lbStatus;
    private javax.swing.JLabel lbStatus1;
    private javax.swing.JPanel pnFilters;
    private com.rb.gui.util.Registro regDate;
    private com.rb.gui.util.Registro regMesero;
    private com.rb.gui.util.Registro regPeriodo;
    private com.rb.gui.util.Registro regProduct;
    private com.rb.gui.util.Registro regSearch;
    private com.rb.gui.util.Registro regTipo;
    private javax.swing.JTable tableList;
    // End of variables declaration//GEN-END:variables

    public class BotonEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {

        private JTextField campo;
        Boolean currentValue;
        JButton button;
        protected static final String EDIT = "edit";
        private JTable tabla;
        private ActionListener acList;
        private String acCommand;

        public BotonEditor(JTable tabla, ActionListener listener, String acCommand) {
            button = new JButton();
            button.setBorderPainted(false);
            this.tabla = tabla;
            this.acList = listener;
            this.acCommand = acCommand;
            button.setActionCommand(acCommand);
            button.addActionListener(BotonEditor.this);

        }

        @Override
        public boolean isCellEditable(EventObject e) {
            if (e instanceof MouseEvent) {
                return ((MouseEvent) e).getClickCount() >= 1;
            }
            return true;
        }

        @Override
        public Object getCellEditorValue() {
            return currentValue;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            currentValue = (Boolean) value;
            return button;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            final int c = tabla.getEditingColumn();
            final int f = tabla.getEditingRow();
            if (f != -1 && c != -1) {
                int row = tabla.convertRowIndexToModel(f);
                String code = model.getValueAt(row, 0).toString();
                Invoice invoice = app.getControl().getInvoiceByCode(code);
                app.getGuiManager().reviewFacture(invoice);

            }
            try {
                fireEditingStopped();
            } catch (Exception ex) {
                System.err.println(ex.getMessage());
            }
        }
    }

    public class ButtonCellRenderer extends JButton implements TableCellRenderer {

        public ButtonCellRenderer(String text) {
            setText(text);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

            if (isSelected) {
                setForeground(Color.black);
                setBackground(table.getSelectionBackground());
                if (hasFocus) {
                    setBorder(BorderFactory.createLineBorder(Color.darkGray));
                } else {
                    setBorder(createLineBorder(Color.lightGray));
                }
            } else {
                setBackground(table.getBackground());
                setForeground(Color.black);
                setBorder(UIManager.getBorder("Table.cellBorder"));
            }
            return this;
        }
    }

    public void filtrar(final String text, final int columna, final int tFilter) {
        RowFilter<Object, Object> filterText = new RowFilter<Object, Object>() {
            @Override
            public boolean include(RowFilter.Entry entry) {
                if (text.equals("")) {
                    return true;
                }
                if (columna == -1) {
                    boolean v;
                    if (tFilter == MyConstants.FILTER_TEXT_INT_START) {
                        for (int i = entry.getValueCount() - 1; i >= 0; i--) {
                            if (entry.getStringValue(i).startsWith(text.toUpperCase())) {
                                return true;
                            }
                        }
                        return false;
                    } else if (tFilter == MyConstants.FILTER_TEXT_INT_CONTAINS) {
                        for (int i = entry.getValueCount() - 1; i >= 0; i--) {
                            if (entry.getStringValue(i).contains(text.toUpperCase())) {
                                return true;
                            }
                        }
                        return false;
                    } else {
                        for (int i = entry.getValueCount() - 1; i >= 0; i--) {
                            if (entry.getStringValue(i).equals(text.toUpperCase())) {
                                return true;
                            }
                        }
                        return false;
                    }
                } else if (tFilter == MyConstants.FILTER_TEXT_INT_START) {
                    return entry.getStringValue(columna).startsWith(text.toUpperCase());
                } else if (tFilter == MyConstants.FILTER_TEXT_INT_CONTAINS) {
                    return entry.getStringValue(columna).contains(text.toUpperCase());
                } else {
                    return entry.getStringValue(columna).equals(text.toUpperCase());
                }
            }
        };

        RowFilter<Object, Object> filterNum;
        filterNum = new RowFilter<Object, Object>() {
            @Override
            public boolean include(RowFilter.Entry entry) {
                int value;
                try {
                    value = Integer.parseInt(text);
                } catch (NumberFormatException e) {
                    return true;
                }
                if (tFilter == MyConstants.FILTER_NUM_INT_GREATER) {
                    return Integer.parseInt(entry.getStringValue(columna)) > value;
                } else if (tFilter == MyConstants.FILTER_NUM_INT_GREATER_EQUAL) {
                    return Integer.parseInt(entry.getStringValue(columna)) >= value;
                } else if (tFilter == MyConstants.FILTER_NUM_INT_LESS) {
                    return Integer.parseInt(entry.getStringValue(columna)) < value;
                } else if (tFilter == MyConstants.FILTER_NUM_INT_LESS_EQUAL) {
                    return Integer.parseInt(entry.getStringValue(columna)) <= value;
                } else if (tFilter == MyConstants.FILTER_NUM_INT_DIFFERENT) {
                    return Integer.parseInt(entry.getStringValue(columna)) != value;
                } else {
                    return Integer.parseInt(entry.getStringValue(columna)) == value;
                }
            }
        };
        TableRowSorter<MyDefaultTableModel> sorter;
        sorter = new TableRowSorter<>(model);
        if (tFilter <= 3) {
            sorter.setRowFilter(filterText);
        } else {
            sorter.setRowFilter(filterNum);
        }
        lastSorter = sorter;
        tableList.setRowSorter(sorter);

    }

    public class TablaCellRenderer extends JLabel implements TableCellRenderer {

        boolean isBordered = true;
        private boolean anulada;
        private final Format formatter;

        public TablaCellRenderer(boolean isBordered, Format formatter) {
            super();
            this.isBordered = isBordered;
            this.formatter = formatter;
            anulada = false;
            setFont(new Font("tahoma", 0, 12));
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            int r = table.convertRowIndexToModel(row);
            if ("ANULADA".equals(table.getModel().getValueAt(r, 2).toString())) {
                anulada = true;
            } else {
                anulada = false;
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
                setForeground(!anulada ? Color.black : Color.red);
                setBackground(tableList.getSelectionBackground());
                if (hasFocus) {
                    setBorder(BorderFactory.createLineBorder(Color.darkGray));
                } else {
                    setBorder(createLineBorder(Color.lightGray));
                }
            } else {
                setBackground(tableList.getBackground());
                setForeground(!anulada ? Color.black : Color.red);
                setBorder(UIManager.getBorder("Table.cellBorder"));
            }
            return this;
        }
    }

}
