package com.bacon.gui;

import com.bacon.Aplication;
import com.bacon.Configuration;
import com.bacon.MyConstants;
import com.bacon.domain.Client;
import com.bacon.domain.Invoice;
import com.bacon.domain.ProductoPed;
import com.bacon.domain.Table;
import com.bacon.domain.Waiter;
import com.bacon.gui.util.MyPopupListener;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.math.BigDecimal;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.EventObject;
import java.util.List;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import static javax.swing.BorderFactory.createLineBorder;
import javax.swing.BoxLayout;
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
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import org.apache.log4j.Logger;
import org.balx.Utiles;
import org.bx.gui.MyDefaultTableModel;
import org.dz.PanelCapturaMod;

/**
 *
 * @author lrod
 */
public class PanelListPedidos extends PanelCapturaMod implements ActionListener {

    private final Aplication app;
    private MyDefaultTableModel model;
    private boolean filtroActivado;
    private TableRowSorter<MyDefaultTableModel> lastSorter;
    private String queryDate;
    private Color COLOR_BACKG;

    private Logger log = Logger.getLogger(PanelListPedidos.class.getCanonicalName());

    public static final String __TODOS__ = " - TODOS - ";
    public static final String PERIODO_MES = "MES";
    public static final String PERIODO_SEMANA = "SEMANA";
    public static final String PERIODO_DIA = "DIA";
    public static final String PERIODO_HISTORICO = "HISTORICO";
    public static final String PERIODO_FECHA = "FECHA";

    public static final String ST_FACTURADO = "FACTURADO";
    public static final String ST_ENTREGADO = "ENTREGADO";
    public static final String ST_DEVUELTO = "DEVUELTO";
    public static final String ST_DESPACHADO = "DESPACHADO";

    public static final String AC_SHOW_INVOICE = "AC_SHOW_INVOICE";

    private JPopupMenu popupTable;
    private MyPopupListener popupListenerTabla;
    private ArrayList<Client> clientList;
    private ArrayList<Waiter> waitersList;
    private SimpleDateFormat formFecha;

    public static final Logger logger = Logger.getLogger(PanelListPedidos.class.getCanonicalName());

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

        jLabel1.setText("Buscar");

        String[] colNames = {"Factura", "Fecha", "Estado", "Ciclo", "Tipo", "Cliente", "Mesa", "Mesero", "Valor", "Servicio", " Accion"};
        model = new MyDefaultTableModel(colNames, 0);
        tableList.setModel(model);
        tableList.setRowHeight(24);

        COLOR_BACKG = Utiles.colorAleatorio(125, 255).brighter();

        tableList.getTableHeader().setBackground(COLOR_BACKG);

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
                    inv.setStatus(Invoice.ST_ANULADA);
                    app.getControl().updateInvoice(inv);
                    List<ProductoPed> list = inv.getProducts();
                    app.getControl().restoreInventory(list);
                    loadPedidos();
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
                
                

            }
        });
        popupTable.add(item2);

        tableList.addMouseListener(popupListenerTabla);

        btBuscar.setText("");
        btBuscar.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "delete.png", 18, 18)));
        btBuscar.setActionCommand(ACTION_CLEAR_SEARCH);

        btBuscar.addActionListener(this);

        String[] TIPOS_VENTAS = {__TODOS__,
            MyConstants.PEDIDO_LOCAL.toUpperCase(),
            MyConstants.PEDIDO_DOMICILIO.toUpperCase(),
            MyConstants.PEDIDO_PARA_LLEVAR.toUpperCase()
        };

        regTipo.setText(TIPOS_VENTAS);
        regTipo.setActionCommand(ACTION_SEARCH);
        regTipo.addActionListener(this);
        regTipo.setBackground(COLOR_BACKG);

        clientList = app.getControl().getClientList("", "");
        Client CTODOS = new Client(0);
        CTODOS.setCellphone(__TODOS__);
        clientList.add(0, CTODOS);
        Client CLOCAL = new Client(1);
        CLOCAL.setCellphone("__LOCAL__");
        clientList.add(1, CLOCAL);
        regCliente.setActionCommand(ACTION_SEARCH);
        regCliente.addActionListener(this);
        regCliente.setText((clientList.toArray()));
        regCliente.setBackground(COLOR_BACKG);

//        String[] ESTADOS = {__TODOS__, ST_ENTREGADO, ST_DEVUELTO, ST_DESPACHADO, ST_FACTURADO};
        waitersList = app.getControl().getWaiterslList("", "");
        Waiter WTODOS = new Waiter();
        WTODOS.setName(__TODOS__);
        waitersList.add(0, WTODOS);
        regMesero.setLabelText("Mesero");
        regMesero.setText(waitersList.toArray());
        regMesero.setActionCommand(ACTION_SEARCH);
        regMesero.addActionListener(this);
        regMesero.setBackground(COLOR_BACKG);

        String[] PERIODOS = {PERIODO_DIA, PERIODO_SEMANA, PERIODO_MES, PERIODO_HISTORICO};
        regPeriodo.setText(PERIODOS);
        regPeriodo.setActionCommand(ACTION_SEL_PERIODO);
        regPeriodo.addActionListener(this);
        regPeriodo.setBackground(COLOR_BACKG);

        lbPeriodo.setOpaque(true);
        lbPeriodo.setBackground(COLOR_BACKG);
        lbPeriodo.setBorder(BorderFactory.createLineBorder(Color.BLACK));

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

        tfBuscar.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                filtrar(tfBuscar.getText().toUpperCase(), -1, MyConstants.FILTER_TEXT_INT_CONTAINS);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filtrar(tfBuscar.getText().toUpperCase(), -1, MyConstants.FILTER_TEXT_INT_CONTAINS);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filtrar(tfBuscar.getText().toUpperCase(), -1, MyConstants.FILTER_TEXT_INT_CONTAINS);
            }
        }
        );

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
        queryDate = "";
        filtroActivado = false;
        activarFiltros(filtroActivado);
        updateConfig();

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
        jLabel1 = new javax.swing.JLabel();
        tfBuscar = new javax.swing.JTextField();
        btBuscar = new javax.swing.JButton();
        regTipo = new com.bacon.gui.util.Registro(BoxLayout.Y_AXIS, "Tipo", new String[0]);
        regPeriodo = new com.bacon.gui.util.Registro(BoxLayout.Y_AXIS, "Periodo", new Object[0]);
        lbPeriodo = new javax.swing.JLabel();
        regMesero = new com.bacon.gui.util.Registro(BoxLayout.Y_AXIS, "Estado", new String[0]);
        regCliente = new com.bacon.gui.util.Registro(BoxLayout.Y_AXIS, "Cliente", new String[0]);
        btConfig = new javax.swing.JButton();
        btUpdate = new javax.swing.JButton();
        btFilters = new javax.swing.JToggleButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableList = new javax.swing.JTable();
        lbStatus = new javax.swing.JLabel();

        pnFilters.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setText("jLabel1");

        tfBuscar.setFont(new java.awt.Font("Ubuntu", 0, 18)); // NOI18N

        lbPeriodo.setText("jLabel2");

        javax.swing.GroupLayout pnFiltersLayout = new javax.swing.GroupLayout(pnFilters);
        pnFilters.setLayout(pnFiltersLayout);
        pnFiltersLayout.setHorizontalGroup(
            pnFiltersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnFiltersLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tfBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(btBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btFilters, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(regTipo, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(9, 9, 9)
                .addComponent(regMesero, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(regCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(regPeriodo, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbPeriodo, javax.swing.GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE)
                .addGap(34, 34, 34)
                .addComponent(btUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btConfig, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        pnFiltersLayout.setVerticalGroup(
            pnFiltersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnFiltersLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(pnFiltersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(regTipo, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(regMesero, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(regCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(regPeriodo, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbPeriodo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1)
                    .addComponent(btConfig, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btFilters, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(26, Short.MAX_VALUE))
        );

        pnFiltersLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btBuscar, regCliente, regMesero, regPeriodo, regTipo, tfBuscar});

        tableList.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(tableList);

        lbStatus.setBackground(new java.awt.Color(147, 153, 165));
        lbStatus.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        lbStatus.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        lbStatus.setOpaque(true);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnFilters, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnFilters, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void loadPedidos() {
        log.debug("On funcion loadPedidos");
        populateTabla(queryDate);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (ACTION_SEARCH.equals(e.getActionCommand())) {
            filtradoSQL();
        } else if (ACTION_UPDATE_LIST.equals(e.getActionCommand())) {
            btFilters.setSelected(false);
            updateCombos();
            activarFiltros(false);
            log.debug("Call from updatin list filtro");
            loadPedidos();
        } else if (ACTION_CLEAR_SEARCH.equals(e.getActionCommand())) {
            tfBuscar.setText("");
        } else if (ACTION_ACTIVATE_FILTER.equals(e.getActionCommand())) {
            boolean selected = btFilters.isSelected();
            activarFiltros(selected);
            filtroActivado = selected;
            filtradoSQL();
        } else if (ACTION_SEL_PERIODO.equals(e.getActionCommand())) {
            String selPeriodo = regPeriodo.getText();
            saveConfig(selPeriodo);
            Calendar cal = Calendar.getInstance();
            if (null != selPeriodo) {
                switch (selPeriodo) {
                    case PERIODO_DIA: {
                        String date = new SimpleDateFormat("dd MMMM yyyy (EEE)").format(cal.getTime());
                        String today = app.DF_SQL.format(cal.getTime());
                        cal.add(Calendar.DATE, 1);
                        String added = app.DF_SQL.format(cal.getTime());
                        queryDate = "sale_date >='" + today + "' AND sale_date<'" + added + "'";
                        lbPeriodo.setText("<html><p color=gray>Hoy:<p color=blue size=+1>" + date.toUpperCase() + "<html>");
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

    public void filtradoSQL() {
        int tipo = regTipo.getSelected();
        int selCliente = regCliente.getSelected();
        long cliente = selCliente > 1 ? Long.parseLong(regCliente.getText()) : selCliente;

        int mesero = regMesero.getSelected();

        buscarFacturas(mesero, cliente, tipo);
    }

    private void activarFiltros(boolean activar) {
        regCliente.setEnabled(activar);
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

    private void buscarFacturas(int idMesero, long idClient, int idTipo) {
//        String date = "( fecha BETWEEN '" + app.DF.format(dIni) + "' AND '" + app.DF.format(dFin) + "')";

        String query = "";
        if (filtroActivado) {
            String tipo = idTipo == 0 ? "" : "deliveryType=" + idTipo + "";
            String mesero = idMesero == 0 ? "" : !tipo.isEmpty() ? " AND idMesero=" + idMesero + "" : "idMesero=" + idMesero;
            String cliente = (idClient < 1) ? "" : !mesero.isEmpty() ? " AND " + "idClient=" + idClient : "idClient=" + idClient;
            query = tipo + mesero + cliente;
        }

        query = !queryDate.isEmpty() ? (!query.isEmpty() ? (queryDate + " AND " + query) : queryDate) : !query.isEmpty() ? query : "";

        populateTabla(query);
    }

    private void updateCombos() {
//        String text = cbProveedor.getText();
//        proveedorList = app.getControl().getProveedorList("", "");
//        Proveedor TODOS = new Proveedor(0L);
//        TODOS.setRazonSocial(__TODOS__);
//        proveedorList.add(0, TODOS);
//        cbProveedor.setText(proveedorList.toArray());
//        try {
//            cbProveedor.setText(text);
//        } catch (Exception e) {
//        }
    }

    private void populateTabla(String query) {

        SwingWorker sw = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                model.setRowCount(0);

                ArrayList<Invoice> invoiceslList = app.getControl().getInvoiceslList(query, "code DESC, sale_date DESC");
                BigDecimal total = new BigDecimal(0);
                double servicio = 0;
                int totalProducts = 0;
                int anuladas = 0;
                for (int i = 0; i < invoiceslList.size(); i++) {
                    Invoice invoice = invoiceslList.get(i);
                    Waiter waiter = app.getControl().getWaitersByID(invoice.getIdWaitress());
                    Table table = app.getControl().getTableByID(invoice.getTable());
                    if (invoice.getStatus() != Invoice.ST_ANULADA) {
                        total = total.add(invoice.getValor());
                        totalProducts += invoice.getProducts().size();
                        servicio += invoice.getValueService();
                    } else {
                        anuladas++;
                    }

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

                }

                lbStatus.setText("<html><font color=blue>" + (invoiceslList.size() - anuladas)
                        + "</font> pedidos - <font color=blue>" + totalProducts
                        + "</font> productos. "
                        + "Servicio: <font color=green>" + app.DCFORM_P.format(servicio)
                        + "</font> - Total: <font color=blue>" + app.DCFORM_P.format(total.doubleValue()) + "</font></html>");

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
    private javax.swing.JButton btBuscar;
    private javax.swing.JButton btConfig;
    private javax.swing.JToggleButton btFilters;
    private javax.swing.JButton btUpdate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbPeriodo;
    private javax.swing.JLabel lbStatus;
    private javax.swing.JPanel pnFilters;
    private com.bacon.gui.util.Registro regCliente;
    private com.bacon.gui.util.Registro regMesero;
    private com.bacon.gui.util.Registro regPeriodo;
    private com.bacon.gui.util.Registro regTipo;
    private javax.swing.JTable tableList;
    private javax.swing.JTextField tfBuscar;
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
