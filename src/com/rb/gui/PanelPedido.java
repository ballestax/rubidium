/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rb.gui;

import com.rb.Aplication;
import com.rb.Configuration;
import com.rb.GUIManager;
import com.rb.MyConstants;
import com.rb.domain.Client;
import com.rb.domain.ConfigDB;
import com.rb.domain.Cycle;
import com.rb.domain.Invoice;
import com.rb.domain.Item;
import com.rb.domain.OtherProduct;
import com.rb.domain.Presentation;
import com.rb.domain.Product;
import com.rb.domain.ProductoPed;
import com.rb.domain.Table;
import com.rb.domain.Waiter;
import com.rb.gui.util.MyPopupListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.Border;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dz.MyDefaultTableModel;

import org.dz.MyDialogEsc;
import org.dz.MyListModel;
import org.dz.PanelCapturaMod;
import org.dz.TextFormatter;
import org.dz.Utiles;
import org.jsoup.Jsoup;
import org.ocpsoft.prettytime.Duration;
import org.ocpsoft.prettytime.PrettyTime;

/**
 *
 * @author lrod
 */
public class PanelPedido extends PanelCapturaMod implements ActionListener, ChangeListener, TableModelListener, PropertyChangeListener {

    private final Aplication app;
    private MyListModel model;
    private MyDefaultTableModel modeloTb;
    private SpinnerNumberModel spModel;
    private SpinnerNumberModel spModelDel;
    private DecimalFormat DCFORM_P;
    private BigDecimal totalFact;
    private String[] entregasLoc, entregasDom;
    private String[] tiempos;
    private ArrayList<ProductoPed> products;
    private ArrayList<OtherProduct> otherProducts;
    private List<ProductoPed> oldProducts;
    private HashMap<Long, Object[]> checkInventory;
    private MultiValuedMap mapInventory;

    public static final Logger logger = LogManager.getLogger(PanelPedido.class.getCanonicalName());
    private JPopupMenu popupTabla;
    private MyPopupListener popupListenerTabla;
    private Color colorDelivery;
    private Color colorLocal;
    private ImageIcon icon;
    private int tipo;
    public static final int TIPO_LOCAL = 1;
    public static final int TIPO_DOMICILIO = 2;
    public static final int TIPO_PARA_LLEVAR = 3;
    private int ajusteRegistros;

    public static final String ENTREGA_LOCAL = "LOCAL";
    public static final String ENTREGA_DOMICILIO = "DOMICILIO";
    public static final String ENTREGA_PARA_LLEVAR = "PARA LLEVAR";

    public static final String[] TIPO_PEDIDO = {ENTREGA_LOCAL, ENTREGA_DOMICILIO, ENTREGA_PARA_LLEVAR};

    private Invoice invoice;

    private boolean block;
    private JMenuItem itemDelete;
    private LinkMouseListener linkMouseListener;
    private LabelFacturaMouseListener lbFacturaMouseListener;
    private boolean showDescuento;
    private ImageIcon iconOk;
    private ImageIcon iconWarning;
    private ImageIcon iconDefault;
    private ImageIcon iconTLGreen;
    private ImageIcon iconTLRed;
    private Client lastClient;
    private JMenuItem itemModify;

    /**
     * Creates new form PanelPedido
     *
     * @param app
     */
    public PanelPedido(Aplication app) {
        this.app = app;
        products = new ArrayList<>();
        otherProducts = new ArrayList<>();
        checkInventory = new HashMap<>();
        mapInventory = new ArrayListValuedHashMap<>();
        initComponents();
        createComponents();
    }

    private void createComponents() {

        Color color = new Color(184, 25, 2);
        Font font = new Font("Arial", 1, 18);
        Font font2 = new Font("Serif", 1, 15);

        colorDelivery = Utiles.colorAleatorio(100, 200).darker();
//        colorLocal = new Color(180,30,154);
        colorLocal = Utiles.colorAleatorio(100, 200).darker();

        DCFORM_P = (DecimalFormat) NumberFormat.getInstance();
        DCFORM_P.applyPattern("$ ###,###,###");

        linkMouseListener = new LinkMouseListener();
        lbFacturaMouseListener = new LabelFacturaMouseListener();

        showDescuento = false;

        ConfigDB config = app.getControl().getConfigLocal(Configuration.DOCUMENT_NAME);
        String docName = config != null && !config.getValor().isEmpty() ? config.getValor() : "Ticket";
        lbTitle.setText(docName);
        lbTitle.setToolTipText(getInfoCiclo(app.getControl().getLastCycle()));

        Font font1 = new Font("sans", 1, 11);

        btTogle1.setText("Local");
        btTogle1.setActionCommand(AC_SELECT_LOCAL);
        btTogle1.addActionListener(this);
        btTogle1.setSelected(true);
        btTogle1.setForeground(colorLocal);
        btTogle1.setMargin(new Insets(1, 1, 1, 1));
        btTogle1.setFont(font1);

        btTogle2.setText("Domicilio");
        btTogle2.setActionCommand(AC_SELECT_DELIVERY);
        btTogle2.addActionListener(this);
        btTogle2.setForeground(colorDelivery);
        btTogle2.setMargin(new Insets(1, 1, 1, 1));
        btTogle2.setMargin(null);
        btTogle2.setFont(font1);

        btDelete.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "trash.png", 18, 18)));
        btDelete.setActionCommand(AC_DELETE_PEDIDO);
        btDelete.addActionListener(this);
        btDelete.setFocusPainted(false);

//        btDelete1.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "shopping-cart-remove.png", 18, 18)));
//        btDelete1.setActionCommand(AC_CLEAR_PRODUCTS);
//        btDelete1.addActionListener(this);
//        btDelete1.setFocusPainted(false);
        regCelular.setLabelText("Celular:");
        regCelular.setFontCampo(font2);
        regCelular.setPopup(true);

        regDireccion.setLabelText("Direccion");
        regDireccion.setFontCampo(font2);
        regDireccion.setPopup(true);

        regDescuento.setLabelText("Des");
        regDescuento.setLabelFontSize(11);
        regSubtotal.setLabelText("Subtotal");
        regSubtotal.setEditable(false);
        regTotal.setLabelText("Total");
        regTotal.setEditable(false);

        tiempos = new String[]{"Pronto", "Especifica"};

        regService.setLabelText("Servicio %");
        regService.setFontCampo(font);
        regService.setTextAligment(SwingConstants.RIGHT);
        regService.setDocument(TextFormatter.getDoubleLimiter());
        regService.setText("0");
        regService.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
                calcularValores();
            }
        });

        spNumDom.setFont(font);

        tfService.setHorizontalAlignment(SwingConstants.RIGHT);
        tfService.setFont(font);
        tfService.setText("0");

        entregasLoc = new String[]{"Local"};
        entregasDom = new String[]{"Domicilio", "Para llevar"};

        regDomicilio.setActionCommand(AC_CHANGE_DOMICILIO);
        regDomicilio.addActionListener(this);
        regDomicilio.setSelected(0);

        lbEntregas.setHorizontalAlignment(SwingConstants.RIGHT);
        lbEntregas.setFont(font);

        config = app.getControl().getConfigLocal(Configuration.DELIVERY_VALUE);
        double valueDelivery = config != null ? (double) config.castValor() : 0;
        lbEntregas.setText(DCFORM_P.format(valueDelivery));
        lbDescuento1.setHorizontalAlignment(SwingConstants.RIGHT);
        lbDescuento1.setFont(font);
        lbDescuento1.setText("$0");

        regDescuento.setDocument(TextFormatter.getDoubleLimiter());
        regDescuento.setTextAligment(SwingConstants.CENTER);
        regDescuento.setText("0");

        regDescuento.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
            }

            @Override
            public void focusLost(FocusEvent e) {
                calcularValores();
            }
        });

        regSubtotal.setTextAligment(SwingConstants.RIGHT);
        regSubtotal.setForeground(color);
        regSubtotal.setFontCampo(font);
        regSubtotal.setText(DCFORM_P.format(0));

        regTotal.setTextAligment(SwingConstants.RIGHT);
        regTotal.setForeground(color);
        regTotal.setFontCampo(font);
        regTotal.setText(DCFORM_P.format(0));

        regService.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
            }

            @Override
            public void focusLost(FocusEvent e) {
                checkValuePorcService();
                calcularValores();
            }
        });

        tfService.setBorder(BorderFactory.createLineBorder(Color.darkGray, 1, true));
        lbDescuento1.setBorder(BorderFactory.createLineBorder(Color.darkGray, 1, true));
        lbEntregas.setBorder(BorderFactory.createLineBorder(Color.darkGray, 1, true));

        btConfirm.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "success.png", 10, 10)));
        btConfirm.setBackground(new Color(153, 255, 153));
        btConfirm.setMargin(new Insets(1, 1, 1, 1));
        btConfirm.setFont(new Font("Arial", 1, 11));
        btConfirm.setActionCommand(AC_CONFIRMAR_PEDIDO);
        btConfirm.addActionListener(this);
        btConfirm.setText("CONFIRMAR");

        ImageIcon iconPrint = new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "Printer-orange.png", 18, 18));
        ImageIcon iconOrder = new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "ordering.png", 18, 18));

        btOrder.setBackground(new Color(253, 153, 205));
        btOrder.setMargin(new Insets(1, 1, 1, 1));
        btOrder.setFont(new Font("Arial", 1, 10));
        btOrder.setActionCommand(AC_PRINT_ORDER);
        btOrder.addActionListener(this);
        btOrder.setIcon(iconOrder);
        btOrder.setToolTipText("Imprimir orden");

        btPrint.setBackground(new Color(153, 253, 255));
        btPrint.setMargin(new Insets(1, 1, 1, 1));
        btPrint.setFont(new Font("Arial", 1, 10));
        btPrint.setActionCommand(AC_PRINT_GUIDE);
        btPrint.addActionListener(this);
        btPrint.setIcon(iconPrint);
        btPrint.setText("Pedido");

        btPrint1.setBackground(new Color(153, 153, 255));
        btPrint1.setMargin(new Insets(1, 1, 1, 1));
        btPrint1.setFont(new Font("Arial", 1, 10));
        btPrint1.setActionCommand(AC_PRINT_BILL);
        btPrint1.addActionListener(this);
        btPrint1.setIcon(iconPrint);
        btPrint1.setText("Factura");

        chServ.setActionCommand(AC_CHECK_SERVICE);
        chServ.addActionListener(this);
        chServ.setSelected(false);

        chRecogido.setFont(new Font("sans", 1, 10));
        chRecogido.setText("Recogido");
        chRecogido.setActionCommand(AC_CHECK_RECOGIDO);
        chRecogido.addActionListener(this);
        chRecogido.setSelected(false);

        String isServ = app.getConfiguration().getProperty(Configuration.IS_SERVICE_DEF, "true");
        Boolean serv = Boolean.valueOf(isServ);
        regService.setEditable(!serv);
        tfService.setEditable(!serv);

        icon = new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "search.png", 16, 16));
//        acSearch = new ProgAction("", icon, "Search client", 's', "AC_SEARCH_CLIENT");
        btSearch.setIcon(icon);
        btSearch.setActionCommand(AC_SEARCH_CLIENT);
        btSearch.addActionListener(this);

        btClear.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "cancel.png", 16, 16)));
        btClear.setActionCommand(AC_CLEAR_CLIENT);
        btClear.addActionListener(this);

//        btLastDelivery.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "cancel.png", 16, 16)));
        btLastDelivery.setActionCommand(AC_LAST_DELIVERY);
        btLastDelivery.addActionListener(this);
        btLastDelivery.setVisible(false);
        btLastDelivery.setFocusPainted(false);

        iconOk = new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "package-accept.png", 18, 18));
        iconWarning = new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "package-warning.png", 18, 18));
        iconDefault = new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "package-info.png", 18, 18));

        iconTLGreen = new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "trafficlight-green.png", 18, 18));
        iconTLRed = new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "trafficlight-red.png", 18, 18));

        btInventoryInfo.setIcon(iconDefault);
        btInventoryInfo.setActionCommand(AC_SHOW_INVENTORY);
        btInventoryInfo.addActionListener(this);

        String[] cols = {"Cant", "Producto", "Unidad", "Valor"};

        modeloTb = new MyDefaultTableModel(cols, 1);

        tbListado.setModel(modeloTb);

        tbListado.getTableHeader().setReorderingAllowed(false);

        boolean showExclusions = Boolean.parseBoolean(app.getConfiguration().getProperty(Configuration.SHOW_EXCLUSIONS));

        int height = 35; // + (showExclusions ? 15 : 0);
        tbListado.setRowHeight(height);
        tbListado.setFont(new Font("Tahoma", 0, 14));
        modeloTb.addTableModelListener(this);

        popupTabla = new JPopupMenu();
        popupListenerTabla = new MyPopupListener(popupTabla, true);
        itemDelete = new JMenuItem("Eliminar...");
        itemDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int r = tbListado.getSelectedRow();
                ProductoPed pp = (ProductoPed) tbListado.getValueAt(r, 1);
                modeloTb.removeRow(r);
                boolean del = products.remove(pp);
            }
        });
        popupTabla.add(itemDelete);

        itemModify = new JMenuItem("Modificar");
        itemModify.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int r = tbListado.getSelectedRow();
                ProductoPed pp = (ProductoPed) tbListado.getValueAt(r, 1);
                if (pp != null) {
                    app.getGuiManager().showCustomPedido(pp, PanelPedido.this);
                }
            }
        });
        popupTabla.add(itemModify);

        tbListado.addMouseListener(popupListenerTabla);

        tbListado.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 2) {
                    int rowAtPoint = tbListado.rowAtPoint(e.getPoint());
                    ProductoPed productoPed = (ProductoPed) tbListado.getValueAt(rowAtPoint, 1);
                    StringBuilder stb = new StringBuilder();
                    stb.append("<html>");
                    stb.append("<h1><font color=red>").append(productoPed.getProduct().getName().toUpperCase()).append("</font></h1>");
                    if (productoPed.hasPresentation()) {
                        stb.append("<h2").append("<font color=blue'>").append(productoPed.getPresentation().getName()).append("</font></h2>");
                    }
                    String[] stAdicionales3 = productoPed.getStAdicionales3();
                    for (int i = 0; i < stAdicionales3.length; i++) {
                        stb.append(stAdicionales3[i]).append("<br>");
                    }
                    if (productoPed.hasExcluisones()) {
                        stb.append("<p>").append(productoPed.getExclusiones()).append("</h1>");
                    }
                    stb.append("<h2><font color=orange>")
                            .append(productoPed.getCantidad())
                            .append("</font> x <font color=orange>")
                            .append(app.DCFORM_P.format(productoPed.getPrecio() + productoPed.getValueAdicionales()))
                            .append("</font></h2>");

                    JOptionPane.showMessageDialog(tbListado, stb.toString(),
                            StringUtils.capitalize(productoPed.getProduct().getName()),
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        Font fontTabla = new Font("Sans", 1, 16);

        FormatRenderer formatRenderer = new FormatRenderer(DCFORM_P);
        formatRenderer.setFont(fontTabla);
        formatRenderer.setForeground(color);
        ProductRenderer prodRenderer = new ProductRenderer(BoxLayout.Y_AXIS);

        int[] colW = new int[]{40, 220, 70, 80};
        for (int i = 0; i < colW.length; i++) {
            tbListado.getColumnModel().getColumn(i).setMinWidth(colW[i]);
            tbListado.getColumnModel().getColumn(i).setPreferredWidth(colW[i]);
        }

        spModel = new SpinnerNumberModel(1, 1, 100, 1);

        spModelDel = new SpinnerNumberModel(1, 1, 100, 1);

        spNumDom.setModel(spModelDel);
        spNumDom.addChangeListener(this);

        tbListado.getColumnModel().getColumn(0).setCellEditor(new SpinnerEditor(spModel));
        tbListado.getColumnModel().getColumn(0).setCellRenderer(new SpinnerRenderer(fontTabla));

        tbListado.getColumnModel().getColumn(1).setCellRenderer(prodRenderer);
        tbListado.getColumnModel().getColumn(2).setCellRenderer(formatRenderer);
        tbListado.getColumnModel().getColumn(3).setCellRenderer(formatRenderer);

        ArrayList<Object[]> data = new ArrayList<>();
        populateTabla(data);

        ArrayList<Table> tables = app.getControl().getTableslList("", "");

        ArrayList<Waiter> waiters = app.getControl().getWaitresslList("status=1", "name");
        waiters.add(0, new Waiter("-----", 0));
        regMesera.setText(waiters.toArray());
        regMesera.setFontCampo(new Font("Sans", 1, 16));
        ((JComboBox) regMesera.getComponent()).setRenderer(new WaiterListCellRenderer());
        regMesera.setActionCommand(AC_CHANGE_SELECTED);
        regMesera.addActionListener(this);

        lbIndicator.setBorder(BorderFactory.createEtchedBorder());
        lbIndicator.setOpaque(true);
        lbIndicator.setVisible(false);

        regCelular.setDocument(TextFormatter.getIntegerLimiter());
        regCelular.setActionCommand(AC_SEARCH_CLIENT);
        regCelular.addActionListener(this);

        regMesa.setFontCampo(new Font("Sans", 1, 16));
        regMesa.setText(tables.toArray());

//        regService.setEnabled(true);
        config = app.getControl().getConfigLocal(Configuration.PRINT_PREV_DELIVERY);
        btPrint.setVisible(config != null ? (Boolean.valueOf(config.getValor())) : false);
        btPrint.setVisible(false);
        btPrint1.setVisible(false);

        showLabelDescuento();

        containerPanels.setLayout(new BorderLayout());

        showDelivery();

        block = false;

        calcularValores();

        SwingWorker sw = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                publish(calculateProximoRegistro());
                return true;
            }

            @Override
            protected void process(List chunks) {
                String registro = "0";
                for (Object chunk : chunks) {
                    registro = (String) chunk;
                }
                lbFactura.setText("<html><font>" + registro + "</font></html>");
            }

        };
        sw.execute();

//        containerPanels.setBorder(bordeError);
        lbFactura.addMouseListener(lbFacturaMouseListener);

//        showAlertCycle();
    }
    private static final String AC_CHANGE_SELECTED = "AC_CHANGE_SELECTED";
    public static final String AC_CHECK_RECOGIDO = "AC_CHECK_RECOGIDO";

    public static final String AC_PRINT_ORDER = "AC_PRINT_ORDER";
    public static final String AC_PRINT_BILL = "AC_PRINT_BILL";
    public static final String AC_PRINT_GUIDE = "AC_PRINT_GUIDE";
    public static final String AC_SEARCH_CLIENT = "AC_SEARCH_CLIENT";
    public static final String AC_CLEAR_CLIENT = "AC_CLEAR_CLIENT";
    public static final String AC_LAST_DELIVERY = "AC_LAST_DELIVERY";
    public static final String AC_SELECT_DELIVERY = "AC_SELECT_DELIVERY";
    public static final String AC_SELECT_LOCAL = "AC_SELECT_LOCAL";
    public static final String AC_DELETE_PEDIDO = "AC_DELETE_PEDIDO";
    public static final String AC_CLEAR_PRODUCTS = "AC_CLEAR_PRODUCTS";
    public static final String AC_CHANGE_DOMICILIO = "AC_CHANGE_DOMICILIO";
    public static final String AC_CONFIRMAR_PEDIDO = "AC_CONFIRMAR_PEDIDO";
    public static final String AC_EDITAR_PEDIDO = "AC_EDITAR_PEDIDO";
    public static final String AC_CHECK_SERVICE = "AC_CHECK_SERVICE";
    public static final String AC_INVENTORY_INFO = "AC_INVENTORY_INFO";
    public static final String AC_SHOW_INVENTORY = "AC_SHOW_INVENTORY";
    public static final String AC_UPDATE_PEDIDO = "AC_UPDATE_PEDIDO";

    private void showLabelDescuento() {
        regDescuento.setVisible(showDescuento);
        lbDescuento1.setVisible(showDescuento);
    }

    public void showAlertCycle() {
        Cycle lastCycle = app.getControl().getLastCycle();
        PrettyTime pt = new PrettyTime(new Locale("es"));
        if (lastCycle != null) {
            Date init = lastCycle.getInit();
            List<Duration> presDur = pt.calculatePreciseDuration(init);
            String formatDuration = pt.formatDuration(presDur);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (AC_CONFIRMAR_PEDIDO.equals(e.getActionCommand())) {
            calcularValores();
            try {
                if (verificarDatosFactura()) {
                    lbFactura.removeMouseListener(lbFacturaMouseListener);
                    btConfirm.setEnabled(false);
                    popupTabla.remove(itemDelete);
                    block = true;
                    btClear.setEnabled(false);
                    btSearch.setEnabled(false);
                    spNumDom.setEnabled(false);
                    chRecogido.setEnabled(false);
                }
            } catch (Exception ex) {
                System.err.println(ex);
            }

        } else if (AC_CHANGE_DOMICILIO.equals(e.getActionCommand())) {
            String dom = regDomicilio.getText();
            if (entregasDom[0].equals(dom)) {  // DOMICILIO
                ConfigDB config = app.getControl().getConfigLocal(Configuration.DELIVERY_VALUE);
                double valueDelivery = config != null ? (double) config.castValor() : 0;
                if (spModelDel != null) {
                    spModelDel.setValue(1);
                }
                spNumDom.setVisible(true);
                lbEntregas.setText(DCFORM_P.format(valueDelivery));
                chRecogido.setSelected(false);
            } else {  // PARA LLEVAR
                spNumDom.setVisible(false);
                if (spModelDel != null) {
                    spModelDel.setValue(0);
                }
                lbEntregas.setText(DCFORM_P.format(0));
                chRecogido.setSelected(true);
            }
            calcularValores();
        } else if (AC_DELETE_PEDIDO.equals(e.getActionCommand())) {
            enablePedido(true);
            clearPedido();
            btDelete.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "trash.png", 18, 18)));
            regMesera.setEditable(true);
            regMesa.setEditable(true);
            regCelular.setEditable(true);
            regDireccion.setEditable(true);
            btTogle1.setEnabled(true);
            btTogle2.setEnabled(true);
            regDomicilio.setEditable(true);
            btPrint.setVisible(false);
            btPrint1.setVisible(false);
            btConfirm.setEnabled(true);
            lbStatus.setIcon(null);
            chServ.setEnabled(true);
            regDescuento.setEnabled(true);
            popupTabla.add(itemDelete);
            lbCliente.setText("");
            spModelDel.setValue(1);
            btInventoryInfo.setIcon(iconDefault);
            invoice = null;
            lastClient = null;
            btLastDelivery.setVisible(false);

            lbFactura.setText("<html><font>" + calculateProximoRegistro() + "</font></html>");
            lbFactura.addMouseListener(lbFacturaMouseListener);

            ConfigDB config = app.getControl().getConfigLocal(Configuration.PRINT_PREV_DELIVERY);
            if (config != null ? Boolean.valueOf(config.getValor()) : false) {
                btOrder.setVisible(true);
            }
            config = app.getControl().getConfigLocal(Configuration.DOCUMENT_NAME);
            String docName = config != null && !config.getValor().isEmpty() ? config.getValor() : "Ticket";
            lbTitle.setText(docName);

            lbTitle.setToolTipText(getInfoCiclo(app.getControl().getLastCycle()));

            block = false;
        } else if (AC_SELECT_DELIVERY.equals(e.getActionCommand())) {
            showDelivery();
        } else if (AC_SELECT_LOCAL.equals(e.getActionCommand())) {
            showLocal();
        } else if (AC_PRINT_BILL.equals(e.getActionCommand())) {
            if (invoice != null) {
                ConfigDB config = app.getControl().getConfigLocal(Configuration.PRINTER_SELECTED);
                String propPrinter = config != null ? config.getValor() : "";
                app.getPrinterService().imprimirFactura(invoice, propPrinter);
            }
        } else if (AC_PRINT_GUIDE.equals(e.getActionCommand())) {
            ConfigDB config = app.getControl().getConfig(Configuration.PRINTER_SELECTED);
            String propPrinter = config != null ? config.getValor() : "";
            if (invoice != null) {
                app.getPrinterService().imprimirGuide(invoice, propPrinter);
            }
        } else if (AC_PRINT_ORDER.equals(e.getActionCommand())) {
            ConfigDB config = app.getControl().getConfigLocal(Configuration.PRINTER_SELECTED);
            String propPrinter = config != null ? config.getValor() : "";
            if (invoice != null) {
                app.getPrinterService().imprimirPedido(invoice, propPrinter);
            } else {
                if (products.isEmpty()) {
                    GUIManager.showErrorMessage(null, "No hay productos en el pedido", "Advertencia");
                } else {
                    Invoice invoicePrev = getInvoice();
                    app.getPrinterService().imprimirPedido(invoicePrev, propPrinter);
                }
            }
        } else if (AC_SEARCH_CLIENT.equals(e.getActionCommand())) {
            String cellphone = regCelular.getText();
            if (!cellphone.isEmpty()) {
                int existClave = app.getControl().existClave("clients", "cellphone", cellphone);
                regDireccion.setText("");
                if (existClave > 0) {
                    Client client = app.getControl().getClient(cellphone);
                    Invoice lastInvoice = app.getControl().getLastDelivery(cellphone);
                    if (lastInvoice != null) {
                        Date fecha = lastInvoice.getFecha();
                        if ((new Date().getTime() - fecha.getTime()) <= 86400000) {
                            btLastDelivery.setIcon(iconTLRed);
                        } else {
                            btLastDelivery.setIcon(iconTLGreen);
                        }
                        btLastDelivery.setVisible(true);
                    }

                    lbCliente.setText("<html>" + client.getCellphone() + "<br><font size=-2>Guardado</font></html>");
                    lbCliente.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
                    lbCliente.addMouseListener(linkMouseListener);
                    lbStatus.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "user-green.png", 18, 18)));

                    //buscar ultimo pedido
                    this.lastClient = client;

                } else {
                    lbCliente.setText("");
                    Client client = new Client(cellphone);
                    app.getGuiManager().showClientCard(client);
                    regDireccion.getComponent().requestFocus();
                    lbStatus.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "user-red.png", 18, 18)));
                    lbCliente.removeMouseListener(linkMouseListener);

                }
            }

        } else if (AC_CHECK_SERVICE.equals(e.getActionCommand())) {

            int defServ = app.getConfiguration().getProperty(Configuration.DEF_SERVICE_PORC, 8);

            if (chServ.isSelected()) {
                regService.setText(String.valueOf(defServ));
            } else {
                regService.setText(String.valueOf(0));
            }

            calcularValores();

        } else if (AC_CHECK_RECOGIDO.equals(e.getActionCommand())) {
            if (chRecogido.isSelected()) {
                regDireccion.setText("RECOGIDO");
                spNumDom.setVisible(false);
                regDomicilio.setSelected(1);

            } else {
                regDireccion.setText("");
                regDomicilio.setSelected(0);
                spNumDom.setVisible(true);
            }

        } else if (AC_CLEAR_CLIENT.equals(e.getActionCommand())) {
            regCelular.setText("");
            regDireccion.setText("");
            lbCliente.setText("");
            regCelular.getComponent().requestFocus();
            lbStatus.setIcon(null);
            lbCliente.removeMouseListener(linkMouseListener);
            lastClient = null;
            btLastDelivery.setVisible(false);
        } else if (AC_SHOW_INVENTORY.equals(e.getActionCommand())) {
            HashMap<Integer, Double> inventory = checkInventory();
            if (!inventory.isEmpty()) {
                String htmlInv = htmlInfoInventory(inventory);
                MyDialogEsc dialog = new MyDialogEsc(app.getGuiManager().getFrame());
                dialog.add(new JLabel(htmlInv));
                dialog.pack();
                dialog.setLocationRelativeTo(null);
                dialog.setVisible(true);
            }
        } else if (AC_LAST_DELIVERY.equals(e.getActionCommand())) {

            StringBuilder htmlText = new StringBuilder("<html>");

            if (lastClient != null) {

                PrettyTime pt = new PrettyTime(new Locale("es"));

                Invoice lastInvoice = app.getControl().getLastDelivery(lastClient.getCellphone());

//                List<Duration> presDur = pt.calculatePreciseDuration(lastInvoice.getFecha());
                htmlText.append("Cliente: ").append("<font color=blue size=+1>").append(lastClient.getCellphone()).append("</font>");
                if (lastInvoice != null) {
                    htmlText.append("<br>");
                    htmlText.append("<p>Factura: <font color=blue>").append(lastInvoice.getFactura()).append("</font></p>");
                    htmlText.append("<p>Direccion: <font color=blue>").append(lastClient.getAddresses().get(0)).append("</font></p>");
                    htmlText.append("<p>Fecha: <font color=blue>").append(app.DF_FULL.format(lastInvoice.getFecha())).append("</font>       ");
                    htmlText.append("<font color=red>").append(pt.format(lastInvoice.getFecha())).append("</font></p>");

                    htmlText.append("<br><br>");
                    htmlText.append("<table  width=\"100%\" cellspacing=\"0\" border=\"1\">");
                    htmlText.append("<tr bgcolor=\"#A4C1FF\">");
                    htmlText.append("<td>Cantidad</td><td>Producto</td><td>V. Unit</td><td>V. Total</td></tr>");
                    for (ProductoPed product : lastInvoice.getProducts()) {
                        htmlText.append("<tr>");
                        htmlText.append("<td>").append(product.getCantidad())
                                .append("</td><td>").append(product.getProduct().getName().toUpperCase()).append("<br>")
                                .append(product.hasPresentation() ? "<font color=cyan>" + StringUtils.capitalize(product.getPresentation().getName()) + "</font>" : "");
                        if (product.hasAdditionals()) {
                            for (String string : product.getStAdicionales3()) {
                                htmlText.append("<font size=-1>" + string + "</font><br>");
                            }
                        }

                        htmlText.append("</td><td align=\"right\">").append(app.DCFORM_P.format(product.getPrecio() + product.getValueAdicionales()))
                                .append("</td><td align=\"right\">").append(app.DCFORM_P.format(product.getCantidad() * (product.getPrecio() + product.getValueAdicionales())))
                                .append("</td></tr>");
                    }
                    htmlText.append("</table>");

                    htmlText.append("<br><br>");
                    htmlText.append("<table width=\"100%\" border=\"1\"><tr>")
                            .append("<td>Domicilios<br><font size=+1 color=blue>")
                            .append(lastInvoice.getNumDeliverys()).append("</font></td>")
                            .append("<td>Valor Dom.<br><font size=+1 color=blue>")
                            .append(app.getCurrencyFormat().format(lastInvoice.getValorDelivery())).append("</font></td>")
                            .append("<td>Total<br><font size=+1 color=red>")
                            .append(app.getCurrencyFormat().format(lastInvoice.getValor())).append("</font></td>")
                            .append("</tr></table>");
                    htmlText.append("</html>");
                }
            }

            MyDialogEsc dial = new MyDialogEsc(app.getGuiManager().getFrame());

            dial.add(new JLabel(htmlText.toString()));
            dial.pack();
            dial.setLocationRelativeTo(null);
            dial.setVisible(true);
        } else if (AC_EDITAR_PEDIDO.equals(e.getActionCommand())) {

            SimpleDateFormat formFecha = new SimpleDateFormat("dd MMMM yyyy");

            Invoice inv = invoice;
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
                    app.getControl().restoreInventory(list, inv.getTipoEntrega());
                } else {
                    GUIManager.showErrorMessage(this, "La factura ya fue anulada, se cargara una nueva con los mismos datos", "Factura anulada");
                }
                lbFactura.setText(calculateProximoRegistro());

                enablePedido(true);

                block = false;

                btConfirm.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "update.png", 10, 10)));
                btConfirm.setBackground(new Color(153, 153, 255));
                btConfirm.setActionCommand(AC_CONFIRMAR_PEDIDO);
                btConfirm.setText("GUARDAR");

                lbCliente.setText("");

                btPrint.setVisible(false);
                btPrint1.setVisible(false);

            }

        } else if (AC_UPDATE_PEDIDO.equals(e.getActionCommand())) {
            Invoice invoice1 = getInvoice();
            app.getControl().updateInvoiceFull(invoice1, oldProducts);
            block = true;
        } else if (AC_CHANGE_SELECTED.equals(e.getActionCommand())) {
            Waiter waitres = (Waiter) regMesera.getSelectedItem();
            Color color = Color.BLACK;
            try {
                color = Color.decode(waitres.getColor());
            } catch (Exception ex) {
            }
            regMesera.setForeground(color);
            lbIndicator.setBackground(color);
            lbIndicator.setVisible(regMesera.getSelected() > 0);

        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        calcularValores();
    }

    private void enablePedido(boolean enable) {
        regMesera.setEnabled(enable);
        regMesa.setEnabled(enable);
        regCelular.setEditable(enable);
        regDireccion.setEditable(enable);
        tbListado.setEnabled(enable);
        spNumDom.setEnabled(enable);
        chRecogido.setEnabled(enable);
        chServ.setEnabled(enable);
        regDomicilio.setEnabled(enable);
        lbEntregas.setEnabled(enable);
        btTogle1.setEnabled(enable);
        btTogle2.setEnabled(enable);
        btInventoryInfo.setEnabled(enable);
        btSearch.setEnabled(enable);
        btClear.setEnabled(enable);
        if (enable) {
            popupTabla.add(itemDelete);
        } else {
            popupTabla.remove(itemDelete);
        }

    }

    private void calcularDelivery() {
        ConfigDB config = app.getControl().getConfigLocal(Configuration.DELIVERY_VALUE);
        double valueDelivery = config != null ? (double) config.castValor() : 0;
        int num = 0;
        if (tipo == TIPO_DOMICILIO) {
//            try {
            num = (Integer) spModelDel.getValue();
//            } catch (Exception e) {
//            }
        }
        double delivery = num * valueDelivery;
        lbEntregas.setText(DCFORM_P.format(delivery));
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        switch (e.getType()) {
            case TableModelEvent.UPDATE:
                if (e.getColumn() == 0) {
                    tbListado.setValueAt(calculatePrecio(e.getLastRow()), e.getLastRow(), 3);
                    int cant = Integer.parseInt(tbListado.getValueAt(e.getLastRow(), 0).toString());
                    ProductoPed prd = products.get(e.getLastRow());
                    prd.setCantidad(cant);

                    //update map inventory
                    SwingWorker sw = new SwingWorker() {
                        @Override
                        protected Object doInBackground() throws Exception {
                            HashMap<Integer, HashMap> mData = prd.getData();
                            Set<Integer> keys = mData.keySet();
                            for (Integer key : keys) {
                                HashMap data = mData.get(key);
                                double res = Double.valueOf(data.get("quantity").toString()) * cant;
                                MultiKey mKey = new MultiKey(data.get("id"), prd.hashCode());
                                mapInventory.remove(mKey);
                                mapInventory.put(mKey, res);
                            }
                            return true;
                        }
                    };
                    sw.execute();
//                    checkInventory();
                }
                break;
            case TableModelEvent.INSERT:
                tbListado.setValueAt(calculatePrecio(e.getLastRow()), e.getLastRow(), 3);
                break;
            case TableModelEvent.DELETE:
                try {
                    ProductoPed rem = products.remove(e.getLastRow());
                    HashMap<Integer, HashMap> mData = rem.getData();
                    Set<Integer> keys = mData.keySet();
                    for (Integer key : keys) {
                        HashMap data = mData.get(key);
                        MultiKey mKey = new MultiKey(data.get("id"), rem.hashCode());
                        Object remove = mapInventory.remove(mKey);
                    }
                    checkInventory();
                } catch (Exception ex) {
                }
                break;
            default:
                break;
        }

        calcularValores();

    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
//        logger.info(evt.getPropertyName()+":"+evt.getPropagationId());
        if (PanelProduct2.AC_ADD_QUICK.equals(evt.getPropertyName())) {
            Product prod = (Product) evt.getNewValue();
            Presentation pres = app.getControl().getPresentationsByDefault(prod.getId());
            addProduct(prod, prod.getPrice(), pres);
        } else if (PanelCustomPedido.AC_CUSTOM_ADD.equals(evt.getPropertyName())) {
            ProductoPed prodPed = (ProductoPed) evt.getNewValue();
            int cant = (int) ((Object[]) evt.getOldValue())[0];
            double price = (double) ((Object[]) evt.getOldValue())[1];
            if (prodPed.getPresentation() != null) {
                price = prodPed.getPresentation().getPrice();
            }
            addProductPed(prodPed, cant, price);
        } else if (PanelOtherProduct.AC_OTHER_PRODUCT.equals(evt.getPropertyName())) {
            OtherProduct oProd = (OtherProduct) evt.getOldValue();
            int cant = (int) evt.getNewValue();

//            Product prod = new Product();
//            prod.setName(oProd.getName());
//            prod.setPrice(oProd.getPrice());
//
//            ProductoPed pPed = new ProductoPed(prod);
//
//            addProductPed(pPed, cant, prod.getPrice(), true);
            addOtherProductPed(oProd, cant, oProd.getPrice());
        } else if (PanelClientCard.AC_SAVE_CLIENT.equals(evt.getPropertyName())) {
            Client client = (Client) evt.getNewValue();
            if (client != null) {
                lbCliente.setText("<html>" + client.getCellphone() + "<br><font size=-2>Guardado</font></html>");
                lbCliente.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
                lbCliente.addMouseListener(linkMouseListener);
                lbStatus.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "user-green.png", 18, 18)));
            }

        } else if (PanelListPedidos.AC_SHOW_INVOICE.equals(evt.getPropertyName())) {
            Invoice invoice = (Invoice) evt.getOldValue();
            loadInvoice(invoice);
        }

    }

    private void populateTabla(ArrayList<Object[]> list) {
        SwingWorker sw = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                modeloTb.setRowCount(0);
                for (int i = 0; i < list.size(); i++) {
                    try {
                        Object[] data = list.get(i);
                        int cant = Integer.parseInt(data[1].toString());
                        Product prd = (Product) data[0];

                        modeloTb.addRow(new Object[]{
                            cant,
                            prd.getName(),
                            prd.getPrice(),
                            prd.getPrice() * cant
                        });

                        modeloTb.setRowEditable(modeloTb.getRowCount() - 1, false);
                        modeloTb.setCellEditable(modeloTb.getRowCount() - 1, 0, true);
                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                    }
                }
                return true;
            }
        };
        sw.execute();
    }

    private boolean checkValuePorcService() {
        double calcularServicio = calcularServicio();
        double max = app.getConfiguration().getProperty(Configuration.MAX_SERVICE_PORC, 10);
        if (calcularServicio > max) {
            GUIManager.showErrorMessage(null, "El maximo porcentaje valido para el servicio voluntario es: " + max + " %", "Advertencia");
            regService.setText(String.valueOf(10));
            return false;
        }
        return true;
    }

    private void calcularService() {
        double pserv = calcularServicio();
        double total = calculateTotal();
        double service = total * pserv / 100;
        tfService.setText(DCFORM_P.format(service));
    }

    public void addProduct(Product producto, double precio, Presentation pres) {
        ProductoPed productoPed = new ProductoPed(producto);
        productoPed.setPresentation(pres);
        productoPed.setPrecio(precio);

        addProductPed(productoPed, 1, precio);
    }

    public void addProductPed(ProductoPed productPed, int cantidad, double price) {
        addProductPed(productPed, cantidad, price, false);
    }

    public void addProductPed(ProductoPed productPed, int cantidad, double price, boolean isOther) {
        if (block) {
            GUIManager.showErrorMessage(null, "El pedido esta cerrado no se puede agregar más productos", "Pedido cerrado");
            return;
        }

        Product producto = productPed.getProduct();

        if (productPed.hasPresentation()) {
            HashMap<Integer, HashMap> mapData = app.getControl().checkInventory(productPed.getPresentation().getId());
            productPed.setData(mapData);
            if (mapData != null && !mapData.isEmpty()) {
                Set<Integer> keys = mapData.keySet();
                for (Integer key : keys) {
                    HashMap data = mapData.get(key);
                    double res = Double.valueOf(data.get("quantity").toString()) * cantidad;
                    MultiKey mKey = new MultiKey(data.get("id"), productPed.hashCode());
                    mapInventory.put(mKey, res);
                }
            }
            checkInventory();
        } else {
            HashMap<Integer, HashMap> mapData = app.getControl().checkInventoryProduct(productPed.getProduct().getId());
            productPed.setData(mapData);
            if (mapData != null && !mapData.isEmpty()) {
                Set<Integer> keys = mapData.keySet();
                for (Integer key : keys) {
                    HashMap data = mapData.get(key);
                    double res = Double.valueOf(data.get("quantity").toString()) * cantidad;
                    MultiKey mKey = new MultiKey(data.get("id"), productPed.hashCode());
                    mapInventory.put(mKey, res);
                }
            }
            checkInventory();
        }

        if (products.contains(productPed) && price == productPed.getPrecio()) {
            try {
                int row = products.indexOf(productPed);
                int cant = Integer.valueOf(modeloTb.getValueAt(row, 0).toString());
                modeloTb.setValueAt(cant + cantidad, row, 0);
                productPed.setCantidad(cantidad);
                products.set(row, productPed);
            } catch (Exception e) {
            }
        } else {
            try {
                productPed.setCantidad(cantidad);
                products.add(productPed);
                double totalProd = (producto.isVariablePrice() || productPed.hasPresentation() ? price : producto.getPrice()) + productPed.getValueAdicionales();
                modeloTb.addRow(new Object[]{
                    cantidad,
                    productPed,
                    totalProd,
                    totalProd * cantidad
                });
                if (productPed.hasAdditionals()) {
                    int size = 11 * (int) Math.ceil(productPed.getAdicionales().size() / 2.0);
                    tbListado.setRowHeight(modeloTb.getRowCount() - 1, 35 + size);
                }

//                if (productPed.hasPresentation()) {
//                    checkProductInventory(productPed.getPresentation().getId(), productPed.getCantidad());
//                }
                modeloTb.setRowEditable(modeloTb.getRowCount() - 1, false);
                modeloTb.setCellEditable(modeloTb.getRowCount() - 1, 0, true);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }

//        checkAllInventory();
    }

    private HashMap<Integer, Double> checkInventory() {

        Set keySet = mapInventory.keySet();

        Iterator<MultiKey> it = keySet.iterator();

        HashMap<Integer, Double> invSimple = new HashMap<>();
        while (it.hasNext()) {
            MultiKey key = it.next();
            int id = (int) key.getKey(0);
            ArrayList<Double> vals = (ArrayList) mapInventory.get(key);
            double sum = 0;
            for (int i = 0; i < vals.size(); i++) {
                sum += vals.get(i);
            }
            if (invSimple.containsKey(id)) {
                Double val = invSimple.get(id);
                invSimple.put(id, val + sum);
            } else {
                invSimple.put(id, sum);
            }
        }
        return invSimple;

    }

    private String htmlInfoInventory(HashMap<Integer, Double> simpInv) {

        Set<Integer> keys = simpInv.keySet();

        StringBuilder stb = new StringBuilder();
        stb.append("<html><table border=1>");
        stb.append("<thead>");
        stb.append("<td>ITEM</td><td>INVENTARIO</td><td>CANTIDAD</td>");
        stb.append("</thead>");
        stb.append("<tbody>");
        for (Integer next : keys) {
            Item item = app.getControl().getItemWhere("id=" + next);
            Double cant = (item.isOnlyDelivery() && tipo == TIPO_LOCAL) ? 0 : simpInv.get(next);
            String color = (item.isOnlyDelivery() && tipo == TIPO_LOCAL) ? "#ff30a5" : (item.getQuantity() >= cant) ? "#00ff32" : "#ff4400";
            stb.append("<tr>")
                    .append("<td><font size=+1 color=").append(color).append(">").append(item.getName().toUpperCase()).append("</font></td>")
                    .append("<td><font size=+1> ").append(item.getQuantity()).append("</font></td>")
                    .append("<td><font size=+1 color=blue>").append(cant).append("</font></td>")
                    .append("</tr>");
        }
        stb.append("</tbody>");
        stb.append("</table></html>");

        return stb.toString();
    }

    private synchronized boolean checkAllInventory() {

        HashMap<Integer, Double> simpInv = checkInventory();
        Set<Integer> keys = simpInv.keySet();

        boolean band;

        for (Integer next : keys) {
            Item item = app.getControl().getItemWhere("id=" + next);
            Double cant = simpInv.get(next);
            band = item.getQuantity() < cant;
            if (tipo != TIPO_LOCAL && band) {
                return false;
            } else if (!item.isOnlyDelivery() && band) {
                return false;
            }
        }
        return true;
    }

    public void addOtherProductPed(OtherProduct otherProduct, int cantidad, double price) {

        if (block) {
            GUIManager.showErrorMessage(null, "El pedido esta cerrado no se puede agregar más productos", "Pedido cerrado");
            return;
        }

        Product prod = new Product(-1);
        prod.setName(otherProduct.getName());
        prod.setPrice(otherProduct.getPrice());

        ProductoPed pPed = new ProductoPed(prod);

        if (otherProducts.contains(otherProduct) && price == otherProduct.getPrice()) {
            try {
                int row = products.indexOf(otherProduct);
                int cant = Integer.valueOf(modeloTb.getValueAt(row, 0).toString());
                modeloTb.setValueAt(cant + cantidad, row, 0);
                pPed.setCantidad(cantidad);
                otherProducts.set(row, otherProduct);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }

        } else {
            try {
                pPed.setCantidad(cantidad);
                otherProducts.add(otherProduct);
                double totalProd = otherProduct.getPrice();
                modeloTb.addRow(new Object[]{
                    cantidad,
                    pPed,
                    totalProd,
                    totalProd * cantidad
                });
                modeloTb.setRowEditable(modeloTb.getRowCount() - 1, false);
                modeloTb.setCellEditable(modeloTb.getRowCount() - 1, 0, true);
            } catch (Exception ex) {
                System.err.println(ex.getMessage());
            }
        }
    }

    private void clearPedido() {

        products.clear();
        mapInventory.clear();
        modeloTb.setRowCount(0);
        regDomicilio.setSelected(0);
        regMesera.setSelected(0);
        regCelular.setText("");
        regDireccion.setText("");
        regDescuento.setText("0");
        chRecogido.setSelected(false);
        btConfirm.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "success.png", 10, 10)));
        btConfirm.setBackground(new Color(153, 255, 153));
        btConfirm.setActionCommand(AC_CONFIRMAR_PEDIDO);
        btConfirm.setText("CONFIRMAR");

        calcularValores();
    }

    private void calcularValores() {
        double subtotal = calculateTotal();
        regSubtotal.setText(DCFORM_P.format(subtotal));
        double servicio = 0;
        if (tipo == TIPO_LOCAL) {
            servicio = subtotal * calcularServicio() / 100;
        }
        double descuento = subtotal * calcularDescuento() / 100;
        lbDescuento1.setText(DCFORM_P.format(descuento > 0 ? descuento * -1 : descuento));
        tfService.setText(DCFORM_P.format(servicio));
        double domicilio = 0;
        calcularDelivery();
        try {
            domicilio = DCFORM_P.parse(lbEntregas.getText()).doubleValue();
        } catch (Exception e) {
        }
        regTotal.setText(DCFORM_P.format(subtotal + domicilio + servicio - descuento));

        SwingWorker sw = new SwingWorker() {

            @Override
            protected Object doInBackground() throws Exception {

                boolean inventoryOK = checkAllInventory();
                if (inventoryOK) {
                    btInventoryInfo.setIcon(iconOk);
                } else {
                    btInventoryInfo.setIcon(iconWarning);
                }
                return true;
            }
        };
        sw.execute();

    }

    private void loadInvoice(Invoice invoice) {

        block = false;

        oldProducts = invoice.getProducts();

        clearPedido();

        enablePedido(false);

        int delivery = invoice.getTipoEntrega();

        Long idClient = invoice.getIdCliente();
        Waiter waiter = app.getControl().getWaitressByID(invoice.getIdWaitress());

        lbFactura.setText("<html><font>" + invoice.getFactura() + "</font></html>");
        lbFactura.removeMouseListener(lbFacturaMouseListener);

        PrettyTime pt = new PrettyTime(new Locale("es"));
        String text = "<html><font size=-1 color=blue>" + app.DF_FULL3.format(invoice.getFecha()) + "</font><p><font color=red size=-2>" + pt.format(invoice.getFecha()) + "</font><html>";

        if (delivery == TIPO_LOCAL) {
            showLocal();
            regMesa.setText(String.valueOf(invoice.getTable()));
            regMesera.setSelected(waiter);

            chServ.setSelected(invoice.isService());
            regService.setText(String.valueOf(invoice.getPorcService()));

        } else {

            showDelivery();

            Client client = app.getControl().getClient(String.valueOf(idClient));
            if (client != null) {
                regCelular.setText(client.getCellphone());
                regDireccion.setText(client.getAddresses().get(0).toString());
            }

            spNumDom.setValue(invoice.getNumDeliverys());
            lbEntregas.setText(DCFORM_P.format(invoice.getValorDelivery().doubleValue()));

            if (delivery == TIPO_PARA_LLEVAR) {
                chRecogido.setSelected(true);
                regDomicilio.setSelected(1);
            }

            lbCliente.setText(text);

        }

        for (ProductoPed product : invoice.getProducts()) {
            addProductPed(product, product.getCantidad(), product.getPrecio());
        }

        btConfirm.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "cancel.png", 10, 10)));
        btConfirm.setBackground(new Color(255, 153, 153));
        btConfirm.setActionCommand(AC_EDITAR_PEDIDO);
        btConfirm.setText("ANULAR");
        btConfirm.setEnabled(true);

        btLastDelivery.setVisible(false);
        lbStatus.setVisible(false);

        tbListado.setEnabled(false);
        btPrint.setVisible(true);
        btPrint1.setVisible(true);

        block = true;

        this.invoice = invoice;
    }

    private boolean verificarDatosFactura() throws ParseException {
        String mesa = "";
        String mesero = "";
        String celular = "";
        String direccion = "";

        try {
            tbListado.getCellEditor().stopCellEditing();
        } catch (Exception e) {
        }

        //checkCiclo
        Cycle lastCycle = app.getControl().getLastCycle();
        if (lastCycle.getStatus() == Cycle.CLOSED) {
            GUIManager.showErrorMessage(null, "El ciclo: " + lastCycle.getId() + " esta cerrado\n"
                    + "Empiece un nuevo ciclo para facturar", "Ciclo cerrado");
            return false;
        }

        if (products.isEmpty() && otherProducts.isEmpty()) {
            GUIManager.showErrorMessage(null, "No hay productos en la lista", "Pedido vacio");
            return false;
        }

        if (!checkValuePorcService()) {
            return false;
        }

        if (!checkAllInventory()) {
            ConfigDB config = app.getControl().getConfigLocal(Configuration.INVOICE_OUT_STOCK);
            String property = config != null ? config.getValor() : "false";
            boolean permit = Boolean.valueOf(property);
            GUIManager.showErrorMessage(null, "Los productos exceden las existencias en inventario.\n"
                    + "Esta " + (permit ? "habilitado" : "deshabilitado") + " facturar sin existencias", "Producto agotado");
            if (!permit) {
                return false;
            }
        }

        Waiter waitres = (Waiter) regMesera.getSelectedItem();
        Table table = (Table) regMesa.getSelectedItem();
        boolean validate = true;
        if (TIPO_LOCAL == tipo) {
            if (waitres == null || regMesera.getSelected() < 1) {
                regMesera.setBorderToError();
                validate = false;
            }
            if (table == null) {
                regMesa.setBorderToError();
                validate = false;
            }
        } else {

            if (regCelular.getText().isEmpty()) {
                regCelular.setBorderToError();
                validate = false;
            }
            if (regDireccion.getText().isEmpty()) {
                regDireccion.setBorderToError();
                validate = false;
            }
        }

        if (!validate) {
            return false;
        }

        celular = regCelular.getText();
        direccion = regDireccion.getText();

        verifyQuantitys();

        Invoice invoice = new Invoice();
        invoice.setFactura(calculateProximoRegistro());

        Cycle cycle = app.getControl().getLastCycle();

        invoice.setCiclo(cycle != null ? cycle.getId() : 0);
        invoice.setFecha(new Date());

        invoice.setDescuento(Double.parseDouble(regDescuento.getText()));

        if (waitres != null) {
            invoice.setIdWaitress(waitres.getId());
        }
        if (table != null) {
            invoice.setTable(table.getId());
        }

        if (!celular.isEmpty()) {
            Client client = new Client(celular);
            client.addAddress(direccion);

            int existClave = app.getControl().existClave("clients", "cellphone", celular);

            if (existClave > 0) {
                app.getControl().updateClient(client);
            } else {
                app.getControl().addClient(client);
            }

            invoice.setIdCliente(Long.parseLong(celular));
        } else {
            invoice.setIdCliente(1L);
        }

        invoice.setService(false);
        invoice.setPorcService(0);

        invoice.setNumDeliverys(0);

        String tipoEntrega = regDomicilio.getText().toUpperCase();
        switch (tipoEntrega) {
            case ENTREGA_DOMICILIO:
                invoice.setTipoEntrega(TIPO_DOMICILIO);
                invoice.setNumDeliverys((Integer) spNumDom.getValue());
                invoice.setValorDelivery(new BigDecimal(DCFORM_P.parse(lbEntregas.getText()).doubleValue()));
                invoice.setIdWaitress(0);
                invoice.setTable(0);
                break;
            case ENTREGA_LOCAL:
                invoice.setTipoEntrega(TIPO_LOCAL);
                invoice.setValorDelivery(BigDecimal.ZERO);
                invoice.setIdCliente(1L);
                double calcularServicio = calcularServicio();
                if (calcularServicio > 0) {
                    invoice.setService(true);
                    invoice.setPorcService(calcularServicio);
                }
                break;
            case ENTREGA_PARA_LLEVAR:
                invoice.setTipoEntrega(TIPO_PARA_LLEVAR);
                invoice.setValorDelivery(BigDecimal.ZERO);
                invoice.setIdWaitress(0);
                invoice.setTable(0);
                break;
        }

        invoice.setProducts(products);

        invoice.setOtherProducts(otherProducts);

        invoice.isService();

        invoice.setValor(totalFact);

        //check factura number
//        int existClave = 0;
//        do {
//            existClave = app.getControl().existClave("invoices", "code", "'" + invoice.getFactura() + "'");
//            System.out.println("existClave = " + existClave);
//            if (existClave > 0) {
//                invoice.setFactura(calculateProximoRegistro());
//                System.out.println("FAC:"+invoice.getFactura());
//            }
//        } while (existClave <= 0);
        this.invoice = invoice;

        if (app.getControl().addInvoice(invoice)) {

            lbFactura.setText(invoice.getFactura());

            btPrint.setVisible(true);
            btPrint1.setVisible(true);

            regMesera.setEditable(false);
            regMesa.setEditable(false);
            regCelular.setEditable(false);
            regDireccion.setEditable(false);
            regDomicilio.setEditable(false);
            chServ.setEnabled(false);
            regDescuento.setEnabled(false);

            btTogle1.setEnabled(false);
            btTogle2.setEnabled(false);

            modeloTb.setColumnEditable(0, false);
            btDelete.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "new-file.png", 18, 18)));
        }
//        app.getGuiManager().reviewFacture(invoice);

        return true;

    }

    private String getInfoCiclo(Cycle ciclo) {
        boolean status = true;
        PrettyTime pt = new PrettyTime(new Locale("es"));
        String idCycle = "-";
        String cycleInit = "-";
        String cycleDur = "-";

        if (ciclo != null) {
            status = ciclo.getStatus() == Cycle.CLOSED;
            List<Duration> presDur = pt.calculatePreciseDuration(ciclo.getInit());
            cycleInit = app.DF_FULL.format(ciclo.getInit());
            cycleDur = pt.formatDuration(presDur);
        }

        return "<html><font size=+1 color=" + (status ? "RED" : "#6e0056") + ">Ciclo: " + idCycle + ""
                + "<p>Inicio: " + cycleInit + " "
                + "<p>" + cycleDur
                + "<p>Estado:" + (status ? "Cerrado" : "Abierto") + "</font></html>";
    }

    public Invoice getInvoice() {
        String mesa = "";
        String mesero = "";
        String celular = "";
        String direccion = "";

        Waiter waitres = (Waiter) regMesera.getSelectedItem();
        Table table = (Table) regMesa.getSelectedItem();
        boolean validate = true;
        if (TIPO_LOCAL == tipo) {
            if (waitres == null || regMesera.getSelected() < 1) {
                regMesera.setBorderToError();
                validate = false;
            }
            if (table == null) {
                regMesa.setBorderToError();
                validate = false;
            }
        } else {
            if (regCelular.getText().isEmpty()) {
                regCelular.setBorderToError();
                validate = false;
            }
            if (regDireccion.getText().isEmpty()) {
                regDireccion.setBorderToError();
                validate = false;
            }
        }

        if (!validate) {
//            return false;
        }

        celular = regCelular.getText();
        direccion = regDireccion.getText();

        verifyQuantitys();

        Invoice invoice = new Invoice();
        invoice.setFactura(calculateProximoRegistro());

        Cycle cycle = app.getControl().getLastCycle();

        invoice.setCiclo(cycle != null ? cycle.getId() : 0);
        invoice.setFecha(new Date());

        invoice.setDescuento(Double.parseDouble(regDescuento.getText()));

        if (waitres != null) {
            invoice.setIdWaitress(waitres.getId());
        }
        if (table != null) {
            invoice.setTable(table.getId());
        }

        if (!celular.isEmpty()) {
            Client client = new Client(celular);
            client.addAddress(direccion);

            int existClave = app.getControl().existClave("clients", "cellphone", celular);

            if (existClave > 0) {
                app.getControl().updateClient(client);
            } else {
                app.getControl().addClient(client);
            }

            invoice.setIdCliente(Long.parseLong(celular));
        } else {
            invoice.setIdCliente(1L);
        }

        invoice.setService(false);
        invoice.setPorcService(0);

        invoice.setNumDeliverys(0);

        String tipoEntrega = regDomicilio.getText().toUpperCase();
        switch (tipoEntrega) {
            case ENTREGA_DOMICILIO:
                invoice.setTipoEntrega(TIPO_DOMICILIO);

                try {
                    invoice.setNumDeliverys((Integer) spModelDel.getValue());
                    invoice.setValorDelivery(new BigDecimal(DCFORM_P.parse(lbEntregas.getText()).doubleValue()));
                } catch (ParseException ex) {
                    invoice.setValorDelivery(BigDecimal.ZERO);
                }

                invoice.setIdWaitress(0);
                invoice.setTable(0);
                break;
            case ENTREGA_LOCAL:
                invoice.setTipoEntrega(TIPO_LOCAL);
                invoice.setValorDelivery(BigDecimal.ZERO);
                invoice.setIdCliente(1L);
                double calcularServicio = calcularServicio();
                if (calcularServicio > 0) {
                    invoice.setService(true);
                    invoice.setPorcService(calcularServicio);
                }
                break;
            case ENTREGA_PARA_LLEVAR:
                invoice.setTipoEntrega(TIPO_PARA_LLEVAR);
                invoice.setValorDelivery(BigDecimal.ZERO);
                invoice.setIdWaitress(0);
                invoice.setTable(0);
                break;
        }

        for (int i = 0; i < products.size(); i++) {
            ProductoPed get = products.get(i);
        }

        invoice.setProducts(products);

        invoice.isService();

        invoice.setValor(totalFact);
        return invoice;
    }

    private void verifyQuantitys() {
        for (int i = 0; i < products.size(); i++) {
            ProductoPed pp = products.get(i);
            int cant = (int) modeloTb.getValueAt(i, 0);
            if (pp.getCantidad() != cant) {
                products.get(i).setCantidad(cant);
            }
        }
    }

    private String calculateProximoRegistro() {
        ConfigDB config = app.getControl().getConfigLocal(Configuration.PREFIX_INVOICES);
        String prefijo = config != null ? config.getValor() : "";

        //get el numero de ceros a la izquierda para formatear el numero
        int ceros = 0;
        try {
            ceros = Integer.parseInt(app.getConfiguration().getProperty("cf.zeros", "0"));
        } catch (NumberFormatException e) {
        }
//        int rows = app.getControl().contarRows("select id from invoices");
        Object maxValue = app.getControl().getMaxValue("invoices", "code");

        Integer value = 0;
        try {
            value = Integer.parseInt(StringUtils.getDigits(maxValue.toString()));
        } catch (Exception e) {
        }

        String codigo = prefijo + com.rb.Utiles.getNumeroFormateado(value + ajusteRegistros + 1, ceros);
        int existClave = app.getControl().existClave("invoices", "code", "'" + codigo + "'");

        while (existClave >= 1) {
            //Comprobar si se esta creando una clave repetida por eliminacion de registros
            //Si esta repetida ajustar el valor y guardar el ajuste para la proxima insercion
            ajusteRegistros++;
            codigo = prefijo + com.rb.Utiles.getNumeroFormateado(value + ajusteRegistros + 1, ceros);
            existClave = app.getControl().existClave("invoices", "code", "'" + codigo + "'");
        }
        return codigo;
    }

    private String getConsecutivoFactura() {

        String consFactura = "000";

        ConfigDB cfFactura = app.getControl().getConfigLocal(MyConstants.CF_FACTURA_ACTUAL);
        if (cfFactura != null) {

            Integer consecutivo = (Integer) cfFactura.castValor();

            consFactura = String.valueOf(consecutivo + 1);
        }
        return consFactura;
    }

    private double calcularDescuento() {
        double desc = 0;
        if (!showDescuento) {
            return desc;
        }
        try {
            Double value = Double.parseDouble(regDescuento.getText());
            desc = value;
        } catch (NumberFormatException e) {
            System.err.println("ex.parse number Discount: " + e.getMessage());
        }
        return desc;
    }

    private double calcularServicio() {
        double serv = 0;
        try {
            Double value = Double.parseDouble(regService.getText());
            serv = value;
        } catch (NumberFormatException e) {
            System.err.println("ex.parse number Service: " + e.getMessage());
        }
        return serv;
    }

    private double calculateTotal() {
        int ROWS = tbListado.getRowCount();
        double total = 0;
        for (int i = 0; i < ROWS; i++) {
            double valorProductos = 0;
            try {
                Double value = Double.parseDouble(tbListado.getValueAt(i, 3).toString());
                valorProductos = value;
            } catch (Exception e) {
                System.err.println("ex.parse number Total: " + e.getMessage());
            }
            total += valorProductos;
        }
        totalFact = new BigDecimal(total);
        return total;
    }

    private double calculatePrecio(int row) {
        double total = 0;
        try {
            Double cant = Double.parseDouble(modeloTb.getValueAt(row, 0).toString());
            Double value = Double.parseDouble(modeloTb.getValueAt(row, 2).toString());
            total = cant * value;
        } catch (NumberFormatException e) {
            System.err.println("ex.parse number Price: " + e.getMessage());
        }
        return total;
    }

    private void showDelivery() {
        tipo = TIPO_DOMICILIO;

        btTogle2.setSelected(true);

        lbTitle.setForeground(colorDelivery.darker());
        //this.setBackground(colorDelivery.brighter());
        regCelular.setTint(colorDelivery);
        regCelular.setBordeNormal(regCelular.getBorder());
        regDireccion.setTint(colorDelivery);
        regDireccion.setBordeNormal(regDireccion.getBorder());

        jScrollPane2.setBorder(BorderFactory.createLineBorder(colorDelivery, 1, true));
        tbListado.getTableHeader().setBackground(colorDelivery.brighter());

        spNumDom.setVisible(true);
        regDomicilio.setVisible(true);
        lbEntregas.setVisible(true);
        regCelular.setVisible(true);
        regDireccion.setVisible(true);
        lbCliente.setVisible(true);
        lbStatus.setVisible(true);
        btSearch.setVisible(true);
        btClear.setVisible(true);
//        btLastDelivery.setVisible(true);
        regMesera.setVisible(false);
        lbIndicator.setVisible(false);
        regMesa.setVisible(false);
        regService.setVisible(false);
        tfService.setVisible(false);
        regDomicilio.setText(entregasDom);
        regDomicilio.setSelected(0);
        chServ.setVisible(false);
        chRecogido.setVisible(true);

        regService.setTint(colorDelivery);

        regService.setTint(colorDelivery);
        regSubtotal.setTint(colorDelivery);
        regDescuento.setTint(colorDelivery);
        regTotal.setTint(colorDelivery);
        regDomicilio.setTint(colorDelivery);

        Border border = regService.getBorder();

        tfService.setBorder(border);
        lbEntregas.setBorder(border);
        lbDescuento1.setBorder(border);
        chServ.setBorder(border);

        containerPanels.removeAll();
        containerPanels.add(pnContDelivery);
        containerPanels.updateUI();

    }

    private void showLocal() {
        tipo = TIPO_LOCAL;

        btTogle1.setSelected(true);

        lbTitle.setForeground(colorLocal.darker());
//        this.setBackground(colorLocal.brighter());
        regMesera.setTint(colorLocal);
        lbIndicator.setVisible(regMesera.getSelected() > 0);
        regMesa.setTint(colorLocal);

        jScrollPane2.setBorder(BorderFactory.createLineBorder(colorLocal, 1, true));
        tbListado.getTableHeader().setBackground(colorLocal.brighter());

        spNumDom.setVisible(false);
        regDomicilio.setVisible(false);
        lbEntregas.setVisible(false);
        regCelular.setVisible(false);
        regDireccion.setVisible(false);
        lbCliente.setVisible(false);
        lbStatus.setVisible(false);
        btSearch.setVisible(false);
        btLastDelivery.setVisible(false);
        btClear.setVisible(false);
        regMesera.setVisible(true);
        regMesa.setVisible(true);
        regService.setVisible(true);
        tfService.setVisible(true);
        chServ.setVisible(true);
        chRecogido.setVisible(false);

        regDomicilio.setText(entregasLoc);
        regDomicilio.setSelected(0);

        regService.setTint(colorLocal);
        regSubtotal.setTint(colorLocal);
        regDescuento.setTint(colorLocal);
        regTotal.setTint(colorLocal);
        regDomicilio.setTint(colorLocal);

        Border border = regService.getBorder();

        tfService.setBorder(border);
        lbEntregas.setBorder(border);
        lbDescuento1.setBorder(border);
        chServ.setBorder(border);

        containerPanels.removeAll();
        containerPanels.add(pnContService);
        containerPanels.updateUI();

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        pnContDelivery = new javax.swing.JPanel();
        lbEntregas = new javax.swing.JLabel();
        spNumDom = new javax.swing.JSpinner();
        regDomicilio = new com.rb.gui.util.Registro(BoxLayout.X_AXIS, "Entrega",new String[1],60);
        pnContService = new javax.swing.JPanel();
        regService = new com.rb.gui.util.Registro(BoxLayout.X_AXIS, "Servicio","",70);
        tfService = new javax.swing.JTextField();
        chServ = new javax.swing.JCheckBox();
        lbTitle = new javax.swing.JLabel();
        regCelular = new com.rb.gui.util.Registro(BoxLayout.X_AXIS,"","", 70);
        regDireccion = new com.rb.gui.util.Registro(BoxLayout.X_AXIS,"","",70);
        btConfirm = new javax.swing.JButton();
        btDelete = new javax.swing.JButton();
        regDescuento = new com.rb.gui.util.Registro(BoxLayout.X_AXIS,"","");
        regTotal = new com.rb.gui.util.Registro(BoxLayout.X_AXIS,"","",60);
        regSubtotal = new com.rb.gui.util.Registro(BoxLayout.X_AXIS, "","",60);
        lbDescuento1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tbListado = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        btTogle2 = new javax.swing.JToggleButton();
        btTogle1 = new javax.swing.JToggleButton();
        regMesera = new com.rb.gui.util.Registro(BoxLayout.X_AXIS, "Mesero",new String[1], 70);
        regMesa = new com.rb.gui.util.Registro(BoxLayout.X_AXIS, "Mesa",new String[1],70);
        btSearch = new javax.swing.JButton();
        lbCliente = new javax.swing.JLabel();
        lbStatus = new javax.swing.JLabel();
        lbFactura = new javax.swing.JLabel();
        btPrint = new javax.swing.JButton();
        btPrint1 = new javax.swing.JButton();
        btClear = new javax.swing.JButton();
        chRecogido = new javax.swing.JCheckBox();
        btInventoryInfo = new javax.swing.JButton();
        btLastDelivery = new javax.swing.JButton();
        lbIndicator = new javax.swing.JLabel();
        containerPanels = new javax.swing.JPanel();
        btOrder = new javax.swing.JButton();

        lbEntregas.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(233, 235, 4)));
        lbEntregas.setMinimumSize(new java.awt.Dimension(80, 31));
        lbEntregas.setPreferredSize(new java.awt.Dimension(100, 31));

        javax.swing.GroupLayout pnContDeliveryLayout = new javax.swing.GroupLayout(pnContDelivery);
        pnContDelivery.setLayout(pnContDeliveryLayout);
        pnContDeliveryLayout.setHorizontalGroup(
            pnContDeliveryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnContDeliveryLayout.createSequentialGroup()
                .addComponent(regDomicilio, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spNumDom, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(lbEntregas, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2))
        );
        pnContDeliveryLayout.setVerticalGroup(
            pnContDeliveryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnContDeliveryLayout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addGroup(pnContDeliveryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(regDomicilio, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbEntregas, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spNumDom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(1, 1, 1))
        );

        pnContDeliveryLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {lbEntregas, regDomicilio, spNumDom});

        chServ.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        chServ.setOpaque(true);

        javax.swing.GroupLayout pnContServiceLayout = new javax.swing.GroupLayout(pnContService);
        pnContService.setLayout(pnContServiceLayout);
        pnContServiceLayout.setHorizontalGroup(
            pnContServiceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnContServiceLayout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(chServ, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(regService, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tfService, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2))
        );
        pnContServiceLayout.setVerticalGroup(
            pnContServiceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnContServiceLayout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addGroup(pnContServiceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(tfService, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(regService, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chServ, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(1, 1, 1))
        );

        pnContServiceLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {chServ, regService, tfService});

        lbTitle.setFont(new java.awt.Font("Ubuntu", 1, 14)); // NOI18N
        lbTitle.setText("jLabel1");
        lbTitle.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEtchedBorder(), javax.swing.BorderFactory.createEmptyBorder(1, 5, 1, 5)));

        regTotal.setMinimumSize(new java.awt.Dimension(160, 31));
        regTotal.setPreferredSize(new java.awt.Dimension(160, 31));

        regSubtotal.setMinimumSize(new java.awt.Dimension(160, 31));
        regSubtotal.setPreferredSize(new java.awt.Dimension(160, 31));

        tbListado.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane2.setViewportView(tbListado);

        buttonGroup1.add(btTogle2);

        buttonGroup1.add(btTogle1);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btTogle1, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btTogle2, javax.swing.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btTogle1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btTogle2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        lbCliente.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.lightGray));

        lbFactura.setFont(new java.awt.Font("Ubuntu", 1, 18)); // NOI18N
        lbFactura.setForeground(new java.awt.Color(1, 41, 103));
        lbFactura.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbFactura.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEtchedBorder(), javax.swing.BorderFactory.createEmptyBorder(1, 5, 1, 5)));

        chRecogido.setFont(new java.awt.Font("Ubuntu", 0, 14)); // NOI18N

        javax.swing.GroupLayout containerPanelsLayout = new javax.swing.GroupLayout(containerPanels);
        containerPanels.setLayout(containerPanelsLayout);
        containerPanelsLayout.setHorizontalGroup(
            containerPanelsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        containerPanelsLayout.setVerticalGroup(
            containerPanelsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 33, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbIndicator, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(regDireccion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chRecogido, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(lbTitle)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbFactura, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btInventoryInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(regCelular, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btClear, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(lbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(1, 1, 1)
                        .addComponent(btLastDelivery, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(regMesera, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(6, 6, 6)
                        .addComponent(regMesa, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(26, 26, 26))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(3, 3, 3)
                                .addComponent(btOrder, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(3, 3, 3)
                                .addComponent(btPrint, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(3, 3, 3)
                                .addComponent(btPrint1, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(containerPanels, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(regDescuento, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(6, 6, 6)
                                .addComponent(lbDescuento1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(regTotal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(regSubtotal, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btDelete, btInventoryInfo});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btPrint, btPrint1});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btDelete, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbFactura, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btInventoryInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(lbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btLastDelivery, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btClear, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(regCelular, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE))
                    .addComponent(btSearch, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE))
                .addGap(7, 7, 7)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(regMesera, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(regMesa, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(regDireccion, javax.swing.GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE)
                    .addComponent(chRecogido, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(2, 2, 2)
                .addComponent(lbIndicator, javax.swing.GroupLayout.PREFERRED_SIZE, 5, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 68, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(regSubtotal, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(containerPanels, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btPrint1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(regTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 30, Short.MAX_VALUE)
                    .addComponent(btPrint, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btOrder, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(regDescuento, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                    .addComponent(lbDescuento1, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btSearch, regCelular, regDireccion});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btConfirm, btPrint, btPrint1, containerPanels, lbDescuento1, regDescuento, regSubtotal, regTotal});

    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btClear;
    private javax.swing.JButton btConfirm;
    private javax.swing.JButton btDelete;
    private javax.swing.JButton btInventoryInfo;
    private javax.swing.JButton btLastDelivery;
    private javax.swing.JButton btOrder;
    private javax.swing.JButton btPrint;
    private javax.swing.JButton btPrint1;
    private javax.swing.JButton btSearch;
    private javax.swing.JToggleButton btTogle1;
    private javax.swing.JToggleButton btTogle2;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox chRecogido;
    private javax.swing.JCheckBox chServ;
    private javax.swing.JPanel containerPanels;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lbCliente;
    private javax.swing.JLabel lbDescuento1;
    private javax.swing.JLabel lbEntregas;
    private javax.swing.JLabel lbFactura;
    private javax.swing.JLabel lbIndicator;
    private javax.swing.JLabel lbStatus;
    private javax.swing.JLabel lbTitle;
    private javax.swing.JPanel pnContDelivery;
    private javax.swing.JPanel pnContService;
    private com.rb.gui.util.Registro regCelular;
    private com.rb.gui.util.Registro regDescuento;
    private com.rb.gui.util.Registro regDireccion;
    private com.rb.gui.util.Registro regDomicilio;
    private com.rb.gui.util.Registro regMesa;
    private com.rb.gui.util.Registro regMesera;
    private com.rb.gui.util.Registro regService;
    private com.rb.gui.util.Registro regSubtotal;
    private com.rb.gui.util.Registro regTotal;
    private javax.swing.JSpinner spNumDom;
    private javax.swing.JTable tbListado;
    private javax.swing.JTextField tfService;
    // End of variables declaration//GEN-END:variables

    private class LinkMouseListener extends MouseAdapter {

        @Override
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            String cellphone = regCelular.getText();
            if (!cellphone.isEmpty()) {
                Client client = app.getControl().getClient(cellphone);
                regDireccion.setText(client.getAddresses().get(0).toString());
                ((JTextField) regDireccion.getComponent()).setCaretPosition(0);
            }
        }
    }

    private class LabelFacturaMouseListener extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) {
                String fact = lbFactura.getText();
                String text = Jsoup.parse(fact).text();
                regCelular.setText(text);
            }
        }
    }

}
