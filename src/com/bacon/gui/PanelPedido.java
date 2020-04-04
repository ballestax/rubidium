/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bacon.gui;

import com.bacon.Aplication;
import com.bacon.Configuration;
import com.bacon.GUIManager;
import com.bacon.domain.Additional;
import com.bacon.domain.Client;
import com.bacon.domain.Cycle;
import com.bacon.domain.Invoice;
import com.bacon.domain.OtherProduct;
import com.bacon.domain.Presentation;
import com.bacon.domain.Product;
import com.bacon.domain.ProductoPed;
import com.bacon.domain.Table;
import com.bacon.domain.Waiter;
import com.bacon.gui.util.MyPopupListener;
import com.github.anastaciocintra.escpos.EscPos;
import com.github.anastaciocintra.escpos.EscPosConst;
import com.github.anastaciocintra.escpos.Style;
import com.github.anastaciocintra.escpos.image.BitImageWrapper;
import com.github.anastaciocintra.escpos.image.Bitonal;
import com.github.anastaciocintra.escpos.image.BitonalThreshold;
import com.github.anastaciocintra.escpos.image.EscPosImage;
import com.github.anastaciocintra.output.PrinterOutputStream;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import javax.print.PrintService;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.Border;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import org.apache.log4j.Logger;
import org.balx.ColorDg;
import org.balx.TextFormato;
import org.bx.Imagenes;
import org.bx.gui.MyDefaultTableModel;
import org.dz.PanelCapturaMod;
import org.dz.TextFormatter;

/**
 *
 * @author lrod
 */
public class PanelPedido extends PanelCapturaMod implements ActionListener, TableModelListener, PropertyChangeListener {

    private final Aplication app;
    private org.dzur.gui.MyListModel model;
    private MyDefaultTableModel modeloTb;
    private SpinnerNumberModel spModel;
    private DecimalFormat DCFORM_P;
    private BigDecimal totalFact;
    private String[] entregasLoc, entregasDom;
    private String[] tiempos;
    private ArrayList<ProductoPed> products;
    private ArrayList<OtherProduct> otherProducts;
    public static final Logger logger = Logger.getLogger(PanelPedido.class.getCanonicalName());
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

    /**
     * Creates new form PanelPedido
     *
     * @param app
     */
    public PanelPedido(Aplication app) {
        this.app = app;
        products = new ArrayList<>();
        otherProducts = new ArrayList<>();
        initComponents();
        createComponents();
    }

    private void createComponents() {

        Color color = new Color(184, 25, 2);
        Font font = new Font("Arial", 1, 18);
        Font font2 = new Font("Serif", 1, 15);

        colorDelivery = ColorDg.colorAleatorio().getColor1();
//        colorLocal = new Color(180,30,154);
        colorLocal = ColorDg.colorAleatorio().getColor2();

        DCFORM_P = (DecimalFormat) NumberFormat.getInstance();
        DCFORM_P.applyPattern("$ ###,###,###");

        linkMouseListener = new LinkMouseListener();

        lbTitle.setText("Pedido");

        btTogle1.setText("Local");
        btTogle1.setActionCommand(AC_SELECT_LOCAL);
        btTogle1.addActionListener(this);
        btTogle1.setSelected(true);
        btTogle1.setForeground(colorLocal);

        btTogle2.setText("Domicilio");
        btTogle2.setActionCommand(AC_SELECT_DELIVERY);
        btTogle2.addActionListener(this);
        btTogle2.setForeground(colorDelivery);

        btDelete.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "trash.png", 18, 18)));
        btDelete.setActionCommand(AC_DELETE_PEDIDO);
        btDelete.addActionListener(this);
        btDelete.setFocusPainted(false);

        regCelular.setLabelText("Celular:");
        regCelular.setFontCampo(font2);
//        regCelular.setText("3006052119");

        regDireccion.setLabelText("Direccion");
        regDireccion.setFontCampo(font2);
//        regDireccion.setText("Calle 24 6-116");

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

        int valueDelivery = app.getConfiguration().getProperty(Configuration.DELIVERY_VALUE, 2000);
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

        btPrint.setBackground(new Color(253, 153, 155));
        btPrint.setMargin(new Insets(1, 1, 1, 1));
        btPrint.setFont(new Font("Arial", 1, 10));
        btPrint.setActionCommand(AC_PRINT_ORDER);
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

        String[] cols = {"Cant", "Producto", "Unidad", "Valor"};

        modeloTb = new MyDefaultTableModel(cols, 1);

        tbListado.setModel(modeloTb);
        tbListado.setRowHeight(60);
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

        tbListado.addMouseListener(popupListenerTabla);

        tbListado.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 2) {
                    int rowAtPoint = tbListado.rowAtPoint(e.getPoint());
                    JOptionPane.showMessageDialog(tbListado, "Info");
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

        tbListado.getColumnModel().getColumn(0).setCellEditor(new SpinnerEditor(spModel));
        tbListado.getColumnModel().getColumn(0).setCellRenderer(new SpinnerRenderer(fontTabla));

        tbListado.getColumnModel().getColumn(1).setCellRenderer(prodRenderer);
        tbListado.getColumnModel().getColumn(2).setCellRenderer(formatRenderer);
        tbListado.getColumnModel().getColumn(3).setCellRenderer(formatRenderer);

        ArrayList<Object[]> data = new ArrayList<>();

//        data.add(new Object[]{1, new String[]{"Doble carne", "+Queso americano", "Sin verduras"},
//            17000, 18000});
//        data.add(new Object[]{2, new String[]{"Rib 57", "", ""},
//            18000, 36000});
//        data.add(new Object[]{1, new String[]{"Chori", "", ""},
//            15000, 15000});
        populateTabla(data);

        ArrayList<Table> tables = app.getControl().getTableslList("", "");

        ArrayList<Waiter> waiters = app.getControl().getWaiterslList("", "");

        org.balx.TextFormato tForm = new TextFormato();
        regCelular.setDocument(tForm.getLimitadorNumeros());
        regCelular.setActionCommand(AC_SEARCH_CLIENT);
        regCelular.addActionListener(this);

        regMesa.setText(tables.toArray());
        regMesera.setText(waiters.toArray());

//        regService.setEnabled(true);
        btPrint.setVisible((Boolean.valueOf(app.getConfiguration().getProperty(Configuration.PRINT_PREV_DELIVERY))));
//        btPrint.setVisible(false);
        btPrint1.setVisible(false);

        calcularValores();

        showLocal();

        block = false;

        lbFactura.setText(calculateProximoRegistro());
    }
    public static final String AC_CHECK_RECOGIDO = "AC_CHECK_RECOGIDO";

    public static final String AC_PRINT_ORDER = "AC_PRINT_ORDER";
    public static final String AC_PRINT_BILL = "AC_PRINT_BILL";
    public static final String AC_SEARCH_CLIENT = "AC_SEARCH_CLIENT";
    public static final String AC_CLEAR_CLIENT = "AC_CLEAR_CLIENT";
    public static final String AC_SELECT_DELIVERY = "AC_SELECT_DELIVERY";
    public static final String AC_SELECT_LOCAL = "AC_SELECT_LOCAL";
    public static final String AC_DELETE_PEDIDO = "AC_DELETE_PEDIDO";
    public static final String AC_CHANGE_DOMICILIO = "AC_CHANGE_DOMICILIO";
    public static final String AC_CONFIRMAR_PEDIDO = "AC_CONFIRMAR_PEDIDO";
    public static final String AC_CHECK_SERVICE = "AC_CHECK_SERVICE";

    @Override
    public void actionPerformed(ActionEvent e) {
        if (AC_CONFIRMAR_PEDIDO.equals(e.getActionCommand())) {
            calcularValores();
            try {
                if (verificarDatosFactura()) {
                    btConfirm.setEnabled(false);
                    popupTabla.remove(itemDelete);
                    block = true;
                }
            } catch (Exception ex) {
            }

        } else if (AC_CHANGE_DOMICILIO.equals(e.getActionCommand())) {
            String dom = regDomicilio.getText();
            if (entregasDom[0].equals(dom)) {
                int valueDelivery = app.getConfiguration().getProperty(Configuration.DELIVERY_VALUE, 2000);
                lbEntregas.setText(DCFORM_P.format(valueDelivery));
            } else {
                lbEntregas.setText(DCFORM_P.format(0));
            }
            calcularValores();
        } else if (AC_DELETE_PEDIDO.equals(e.getActionCommand())) {
            clearPedido();
            btDelete.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "trash.png", 18, 18)));
            regMesa.setEditable(true);
            regMesera.setEditable(true);
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
            invoice = null;
            lbFactura.setText(calculateProximoRegistro());

            if (Boolean.valueOf(app.getConfiguration().getProperty(Configuration.PRINT_PREV_DELIVERY))) {
                btPrint.setVisible(true);
            }

            block = false;
        } else if (AC_SELECT_DELIVERY.equals(e.getActionCommand())) {
            showDelivery();
        } else if (AC_SELECT_LOCAL.equals(e.getActionCommand())) {
            showLocal();
        } else if (AC_PRINT_BILL.equals(e.getActionCommand())) {
            if (invoice != null) {
                String propPrinter = app.getConfiguration().getProperty(Configuration.PRINTER_SELECTED);
                app.getPrinterService().imprimirFactura(invoice, propPrinter);
            }
        } else if (AC_PRINT_ORDER.equals(e.getActionCommand())) {
            String propPrinter = app.getConfiguration().getProperty(Configuration.PRINTER_SELECTED);
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
                    lbCliente.setText("<html>" + client.getCellphone() + "<br><font size=-2>Guardado</font></html>");
                    lbCliente.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
                    lbCliente.addMouseListener(linkMouseListener);
                    lbStatus.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "user-green.png", 18, 18)));
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
                regDomicilio.setSelected(1);
            } else {
                regDireccion.setText("");
                regDomicilio.setSelected(0);
            }

        } else if (AC_CLEAR_CLIENT.equals(e.getActionCommand())) {
            regCelular.setText("");
            regDireccion.setText("");
            lbCliente.setText("");
            regCelular.getComponent().requestFocus();
            lbStatus.setIcon(null);
            lbCliente.removeMouseListener(linkMouseListener);
        }
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        switch (e.getType()) {
            case TableModelEvent.UPDATE:
                if (e.getColumn() == 0) {
                    tbListado.setValueAt(calculatePrecio(e.getLastRow()), e.getLastRow(), 3);
                    int cant = Integer.parseInt(tbListado.getValueAt(e.getLastRow(), 0).toString());
                    products.get(e.getLastRow()).setCantidad(cant);

                }
                break;
            case TableModelEvent.INSERT:
                tbListado.setValueAt(calculatePrecio(e.getLastRow()), e.getLastRow(), 3);
                break;
            case TableModelEvent.DELETE:
                try {
                    products.remove(e.getLastRow());
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
        logger.debug("last:" + evt.getPropertyName() + ":" + evt.getPropagationId());
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
        /*if (productos.contains(productoPed)) {
            int row = productos.indexOf(productoPed);
            int cant = Integer.valueOf(modeloTb.getValueAt(row, 0).toString());
            modeloTb.setValueAt(cant + 1, row, 0);
            productoPed.setCantidad(cant + 1);
            productos.set(row, productoPed);
        } else {
            try {
                productoPed.setCantidad(1);
                productos.add(productoPed);
                modeloTb.addRow(new Object[]{
                    1,
                    productoPed,
                    producto.getPrice(),
                    producto.getPrice()
                });

                modeloTb.setRowEditable(modeloTb.getRowCount() - 1, false);
                modeloTb.setCellEditable(modeloTb.getRowCount() - 1, 0, true);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }*/
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
                modeloTb.setRowEditable(modeloTb.getRowCount() - 1, false);
                modeloTb.setCellEditable(modeloTb.getRowCount() - 1, 0, true);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
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
                System.out.println(ex.getMessage());
            }
        }
    }

    private void clearPedido() {
        products.clear();
        modeloTb.setRowCount(0);
        regDomicilio.setSelected(0);
        regCelular.setText("");
        regDireccion.setText("");
        regDescuento.setText("0");
        chRecogido.setSelected(false);
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
        try {
            domicilio = DCFORM_P.parse(lbEntregas.getText()).doubleValue();
        } catch (Exception e) {
        }
        regTotal.setText(DCFORM_P.format(subtotal + domicilio + servicio - descuento));
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

        if (products.isEmpty() && otherProducts.isEmpty()) {
            GUIManager.showErrorMessage(null, "No hay productos en la lista", "Pedido vacio");
            return false;
        }

        if (!checkValuePorcService()) {
            return false;
        }

        Waiter waitres = (Waiter) regMesera.getSelectedItem();
        Table table = (Table) regMesa.getSelectedItem();
        boolean validate = true;
        if (TIPO_LOCAL == tipo) {
            if (waitres == null) {
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

        String tipoEntrega = regDomicilio.getText().toUpperCase();
        switch (tipoEntrega) {
            case ENTREGA_DOMICILIO:
                invoice.setTipoEntrega(TIPO_DOMICILIO);
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
                break;
        }

        invoice.setProducts(products);

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

            regMesa.setEditable(false);
            regMesera.setEditable(false);
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

    public Invoice getInvoice() {
        String mesa = "";
        String mesero = "";
        String celular = "";
        String direccion = "";

        Waiter waitres = (Waiter) regMesera.getSelectedItem();
        Table table = (Table) regMesa.getSelectedItem();
        boolean validate = true;
        if (TIPO_LOCAL == tipo) {
            if (waitres == null) {
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

        String tipoEntrega = regDomicilio.getText().toUpperCase();
        switch (tipoEntrega) {
            case ENTREGA_DOMICILIO:
                invoice.setTipoEntrega(TIPO_DOMICILIO);
                 {
                    try {
                        invoice.setValorDelivery(new BigDecimal(DCFORM_P.parse(lbEntregas.getText()).doubleValue()));
                    } catch (ParseException ex) {
                        invoice.setValorDelivery(BigDecimal.ZERO);
                    }
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
        int rows = app.getControl().contarRows("select id from invoices");
        String codigo = "F" + com.bacon.Utiles.getNumeroFormateado(rows + ajusteRegistros + 1, 6);
        int existClave = app.getControl().existClave("invoices", "code", "'" + codigo + "'");
        while (existClave >= 1) {
            //Comprobar si se esta creando una clave repetida por eliminacion de registros
            //Si esta repetida ajustar el valor y guardar el ajuste para la proxima insercion
            ajusteRegistros++;
            codigo = "F" + com.bacon.Utiles.getNumeroFormateado(rows + ajusteRegistros + 1, 6);
            existClave = app.getControl().existClave("invoices", "code", "'" + codigo + "'");
        }
        return codigo;
    }

    private double calcularDescuento() {
        double desc = 0;
        try {
            Double value = Double.parseDouble(regDescuento.getText());
            desc = value;
        } catch (NumberFormatException e) {
            System.err.println("ex.parse number: " + e.getMessage());
        }
        return desc;
    }

    private double calcularServicio() {
        double serv = 0;
        try {
            Double value = Double.parseDouble(regService.getText());
            serv = value;
        } catch (NumberFormatException e) {
            System.err.println("ex.parse number: " + e.getMessage());
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
                System.err.println("ex.parse number: " + e.getMessage());
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
            System.err.println("ex.parse number: " + e.getMessage());
        }
        return total;
    }

    private void print(Invoice invoice) {

        String propPrinter = app.getConfiguration().getProperty(Configuration.PRINTER_SELECTED);

        if (propPrinter.isEmpty()) {
            GUIManager.showErrorMessage(null, "No ha seleccionado una impresora valida para imprimir", "Impresora no encontrada");
            return;
        }

        String printerName = "POS-STAR";

        PrintService printService = PrinterOutputStream.getPrintServiceByName(printerName);
        EscPos escpos;
        try {
            Bitonal algorithm = new BitonalThreshold(127);
            // creating the EscPosImage, need buffered image and algorithm.
//            URL githubURL = getURL("logo1.png");
//            System.out.println("githubURL = " + githubURL);
//            BufferedImage imagen = ImageIO.read(githubURL);
            Image imagen = app.getImgManager().getImagen("gui/img/" + "logo2.png", 150, 150);
            BufferedImage buffImagen = Imagenes.toBuffereredImage(imagen);
            EscPosImage escposImage = new EscPosImage(buffImagen, algorithm);

            // this wrapper uses esc/pos sequence: "ESC '*'"
            BitImageWrapper imageWrapper = new BitImageWrapper();

            Style font2 = new Style().setFontSize(Style.FontSize._1, Style.FontSize._1).setJustification(EscPosConst.Justification.Center);
            Style font3 = new Style().setFontSize(Style.FontSize._1, Style.FontSize._1);
            Style font4 = new Style().setFontSize(Style.FontSize._1, Style.FontSize._1).setJustification(EscPosConst.Justification.Right);

            escpos = new EscPos(new PrinterOutputStream(printService));
            imageWrapper.setJustification(EscPosConst.Justification.Center);
            escpos.write(imageWrapper, escposImage);
            escpos.feed(1);
            escpos.writeLF(new Style().setFontSize(Style.FontSize._3, Style.FontSize._3).setJustification(EscPosConst.Justification.Center),
                    "Bacon 57 Burger");
            escpos.writeLF(font2, "NIT. 1129518949");
            escpos.writeLF(font2, "Calle 18 # 5-59");
            escpos.writeLF(font2, "321 5944870");
            escpos.feed(1);
            if (invoice.getTipoEntrega() == 1) {
                escpos.writeLF(font3, "Cliente:");
                escpos.writeLF(font3, "Direccion:");
            } else {
                escpos.writeLF(font3, "Mesa:");
                escpos.writeLF(font3, "Mesero:");
            }
            escpos.feed(1);
            escpos.writeLF(font3, String.format("Tiquete N°: %1s %25.25s", invoice.getFactura(), app.DF_FULL.format(invoice.getFecha())));
            escpos.feed(1);

            String column1Format = "%3.3s";  // fixed size 3 characters, left aligned
            String column2Format = "%-26.26s";  // fixed size 8 characters, left aligned
            String column3Format = "%7.7s";   // fixed size 6 characters, right aligned
            String column4Format = "%8.8s";   // fixed size 6 characters, right aligned
            String formatInfo = column1Format + " " + column2Format + " " + column3Format + " " + column4Format;

            escpos.writeLF(font2, "===============================================");
            List<ProductoPed> products = invoice.getProducts();
            for (int i = 0; i < products.size(); i++) {
                ProductoPed product = products.get(i);
                Presentation presentation = product.getPresentation();
                String stPres = "";
                if (presentation != null) {
                    stPres = " (" + presentation.getName() + ")";
                }
                double priceFinal = product.getProduct().getPrice() + product.getValueAdicionales();
                escpos.writeLF(String.format(formatInfo, product.getCantidad(), (product.getProduct().getName() + stPres).toUpperCase(),
                        app.DCFORM_P.format(priceFinal), app.DCFORM_P.format(product.getCantidad() * priceFinal)));

                for (int j = 0; j < product.getAdicionales().size(); j++) {
                    Additional adic = product.getAdicionales().get(j).getAdditional();
                    int cant = product.getAdicionales().get(j).getCantidad();
                    StringBuilder stb = new StringBuilder();
                    stb.append("+").append(adic.getName()).append("(x").append(cant).append(")");
                    escpos.writeLF("    " + stb.toString());
                }
            }

            BigDecimal total = invoice.getValor();

            if (invoice.getTipoEntrega() == TIPO_DOMICILIO) {
                escpos.writeLF(font2, "_________________________________________________");

                escpos.writeLF(String.format(formatInfo, "1", "Domicilio", "", app.DCFORM_P.format(invoice.getValorDelivery())));// app.DCFORM_P.format(invoice.getValorDelivery().doubleValue())));

                total = total.add(invoice.getValorDelivery());
            }
            escpos.writeLF(font2, "_________________________________________________");

            escpos.writeLF(String.format(formatInfo, "", "", "Total:", app.DCFORM_P.format(total)));

            escpos.writeLF(font2, "================================================");

            escpos.feed(1);

            escpos.writeLF(font2, "Gracias por su compra");
            escpos.feed(5);

            escpos.cut(EscPos.CutMode.FULL);

            escpos.close();

        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(PanelPedido.class.getName()).log(Level.ALL.SEVERE, null, ex);
        }

    }

    private void showDelivery() {
        tipo = TIPO_DOMICILIO;

        lbTitle.setForeground(colorDelivery.darker());
        //this.setBackground(colorDelivery.brighter());
        regCelular.setTint(colorDelivery);
        regCelular.setBordeNormal(regCelular.getBorder());
        regDireccion.setTint(colorDelivery);
        regDireccion.setBordeNormal(regDireccion.getBorder());

        jScrollPane2.setBorder(BorderFactory.createLineBorder(colorDelivery, 1, true));
        tbListado.getTableHeader().setBackground(colorDelivery.brighter());

        regCelular.setVisible(true);
        regDireccion.setVisible(true);
        lbCliente.setVisible(true);
        lbStatus.setVisible(true);
        btSearch.setVisible(true);
        btClear.setVisible(true);
        regMesa.setVisible(false);
        regMesera.setVisible(false);
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

    }

    private void showLocal() {
        tipo = TIPO_LOCAL;

        lbTitle.setForeground(colorLocal.darker());
//        this.setBackground(colorLocal.brighter());      
        regMesa.setTint(colorLocal);
        regMesera.setTint(colorLocal);

        jScrollPane2.setBorder(BorderFactory.createLineBorder(colorLocal, 1, true));
        tbListado.getTableHeader().setBackground(colorLocal.brighter());

        regCelular.setVisible(false);
        regDireccion.setVisible(false);
        lbCliente.setVisible(false);
        lbStatus.setVisible(false);
        btSearch.setVisible(false);
        btClear.setVisible(false);
        regMesa.setVisible(true);
        regMesera.setVisible(true);
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
        lbTitle = new javax.swing.JLabel();
        regCelular = new com.bacon.gui.util.Registro(BoxLayout.X_AXIS,"","", 70);
        regDireccion = new com.bacon.gui.util.Registro(BoxLayout.X_AXIS,"","",70);
        btConfirm = new javax.swing.JButton();
        btDelete = new javax.swing.JButton();
        regDescuento = new com.bacon.gui.util.Registro(BoxLayout.X_AXIS,"","");
        regTotal = new com.bacon.gui.util.Registro(BoxLayout.X_AXIS,"","",60);
        regSubtotal = new com.bacon.gui.util.Registro(BoxLayout.X_AXIS, "","",60);
        regService = new com.bacon.gui.util.Registro(BoxLayout.X_AXIS, "Servicio","",70);
        regDomicilio = new com.bacon.gui.util.Registro(BoxLayout.X_AXIS, "Entrega",new String[1],90);
        lbDescuento1 = new javax.swing.JLabel();
        lbEntregas = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tbListado = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        btTogle2 = new javax.swing.JToggleButton();
        btTogle1 = new javax.swing.JToggleButton();
        regMesa = new com.bacon.gui.util.Registro(BoxLayout.X_AXIS, "Mesa",new String[1], 70);
        regMesera = new com.bacon.gui.util.Registro(BoxLayout.X_AXIS, "Mesero",new String[1],70);
        btSearch = new javax.swing.JButton();
        lbCliente = new javax.swing.JLabel();
        lbStatus = new javax.swing.JLabel();
        lbFactura = new javax.swing.JLabel();
        btPrint = new javax.swing.JButton();
        tfService = new javax.swing.JTextField();
        chServ = new javax.swing.JCheckBox();
        btPrint1 = new javax.swing.JButton();
        btClear = new javax.swing.JButton();
        chRecogido = new javax.swing.JCheckBox();

        lbTitle.setFont(new java.awt.Font("Ubuntu", 1, 18)); // NOI18N
        lbTitle.setText("jLabel1");
        lbTitle.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEtchedBorder(), javax.swing.BorderFactory.createEmptyBorder(1, 5, 1, 5)));

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
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addComponent(btTogle1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(btTogle2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btTogle1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btTogle2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        lbFactura.setFont(new java.awt.Font("Ubuntu", 1, 18)); // NOI18N
        lbFactura.setForeground(new java.awt.Color(1, 41, 103));
        lbFactura.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbFactura.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEtchedBorder(), javax.swing.BorderFactory.createEmptyBorder(1, 5, 1, 5)));

        chServ.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        chServ.setOpaque(true);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btPrint, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btPrint1, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(chServ, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, 0)
                                        .addComponent(regService, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addComponent(regDomicilio, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(1, 1, 1)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(lbEntregas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(tfService, javax.swing.GroupLayout.DEFAULT_SIZE, 117, Short.MAX_VALUE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(regDescuento, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0)
                                .addComponent(lbDescuento1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(regSubtotal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(regTotal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(jScrollPane2)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(regDireccion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chRecogido, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(lbTitle)
                        .addGap(1, 1, 1)
                        .addComponent(lbFactura, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(55, 55, 55)
                        .addComponent(btDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(regCelular, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addComponent(btSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btClear, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(lbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(regMesa, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(regMesera, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btDelete, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbFactura, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(lbCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(regCelular, javax.swing.GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE)
                        .addComponent(btSearch, javax.swing.GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE)
                        .addComponent(lbStatus, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(btClear, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(regMesa, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(regMesera, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(regDireccion, javax.swing.GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE)
                    .addComponent(chRecogido, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 87, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(tfService, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(regService, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(regSubtotal, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chServ, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(regDomicilio, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(regDescuento, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                    .addComponent(lbDescuento1, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbEntregas, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(regTotal, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                    .addComponent(btConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btPrint, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btPrint1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btSearch, regCelular, regDireccion});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {lbDescuento1, lbEntregas, regDescuento, regDomicilio, regService, regSubtotal, regTotal, tfService});

    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btClear;
    private javax.swing.JButton btConfirm;
    private javax.swing.JButton btDelete;
    private javax.swing.JButton btPrint;
    private javax.swing.JButton btPrint1;
    private javax.swing.JButton btSearch;
    private javax.swing.JToggleButton btTogle1;
    private javax.swing.JToggleButton btTogle2;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox chRecogido;
    private javax.swing.JCheckBox chServ;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lbCliente;
    private javax.swing.JLabel lbDescuento1;
    private javax.swing.JLabel lbEntregas;
    private javax.swing.JLabel lbFactura;
    private javax.swing.JLabel lbStatus;
    private javax.swing.JLabel lbTitle;
    private com.bacon.gui.util.Registro regCelular;
    private com.bacon.gui.util.Registro regDescuento;
    private com.bacon.gui.util.Registro regDireccion;
    private com.bacon.gui.util.Registro regDomicilio;
    private com.bacon.gui.util.Registro regMesa;
    private com.bacon.gui.util.Registro regMesera;
    private com.bacon.gui.util.Registro regService;
    private com.bacon.gui.util.Registro regSubtotal;
    private com.bacon.gui.util.Registro regTotal;
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
            }
        }

    }

}
