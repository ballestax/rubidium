/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bacon.gui;

import com.bacon.Aplication;
import com.bacon.GUIManager;
import com.bacon.domain.Additional;
import com.bacon.domain.Client;
import com.bacon.domain.Invoice;
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
import java.net.URL;
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
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import org.apache.log4j.Logger;
import org.balx.ColorDg;
import org.bx.Imagenes;
import org.bx.Utiles;
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
    private ArrayList<ProductoPed> productos;
    public static final Logger logger = Logger.getLogger(PanelPedido.class.getCanonicalName());
    private JPopupMenu popupTabla;
    private MyPopupListener popupListenerTabla;
    private Color colorDelivery;
    private Color colorLocal;
    private ImageIcon icon;
    private int tipo;
    public static final int TIPO_LOCAL = 1;
    public static final int TIPO_DOMICILIO = 2;
    private int ajusteRegistros;

    public static final String ENTREGA_LOCAL = "LOCAL";
    public static final String ENTREGA_DOMICILIO = "DOMICILIO";
    public static final String ENTREGA_PARA_LLEVAR = "PARA LLEVAR";
    private Invoice invoice;

    /**
     * Creates new form PanelPedido
     *
     * @param app
     */
    public PanelPedido(Aplication app) {
        this.app = app;
        productos = new ArrayList<>();
        initComponents();
        createComponents();
    }

    private void createComponents() {

        Color color = new Color(184, 25, 2);
        Font font = new Font("Arial", 1, 18);

        colorDelivery = ColorDg.colorAleatorio().getColor1();
//        colorLocal = new Color(180,30,154);
        colorLocal = ColorDg.colorAleatorio().getColor2();

        DCFORM_P = (DecimalFormat) NumberFormat.getInstance();
        DCFORM_P.applyPattern("$ ###,###,###");

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
//        regCelular.setText("3006052119");

        regDireccion.setLabelText("Direccion");
//        regDireccion.setText("Calle 24 6-116");

        regDescuento.setLabelText("Des");
        regDescuento.setLabelFontSize(11);
        regSubtotal.setLabelText("Subtotal");
        regSubtotal.setEditable(false);
        regTotal.setLabelText("Total");
        regTotal.setEditable(false);

        tiempos = new String[]{"Pronto", "Especifica"};
        regTiempo.setText(tiempos);

        entregasLoc = new String[]{"Local"};
        entregasDom = new String[]{"Domicilio", "Para llevar"};

        regDomicilio.setActionCommand(AC_CHANGE_DOMICILIO);
        regDomicilio.addActionListener(this);
        regDomicilio.setSelected(0);

        lbEntregas.setHorizontalAlignment(SwingConstants.RIGHT);
        lbEntregas.setFont(font);
        lbEntregas.setText(DCFORM_P.format(2000));
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

        lbTiempos.setBorder(BorderFactory.createLineBorder(Color.darkGray, 1, true));
        lbDescuento1.setBorder(BorderFactory.createLineBorder(Color.darkGray, 1, true));
        lbEntregas.setBorder(BorderFactory.createLineBorder(Color.darkGray, 1, true));

        btConfirm.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "success.png", 10, 10)));
        btConfirm.setBackground(new Color(153, 255, 153));
        btConfirm.setMargin(new Insets(1, 1, 1, 1));
        btConfirm.setFont(new Font("Arial", 1, 11));
        btConfirm.setActionCommand(AC_CONFIRMAR_PEDIDO);
        btConfirm.addActionListener(this);
        btConfirm.setText("CONFIRMAR");

        btPrint.setBackground(new Color(153, 153, 255));
        btPrint.setMargin(new Insets(1, 1, 1, 1));
        btPrint.setFont(new Font("Arial", 1, 11));
        btPrint.setActionCommand(AC_PRINT_BILL);
        btPrint.addActionListener(this);
        btPrint.setText("IMPRIMIR");

        icon = new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "search.png", 16, 16));
//        acSearch = new ProgAction("", icon, "Search client", 's', "AC_SEARCH_CLIENT");
        btSearch.setIcon(icon);
        btSearch.setActionCommand(AC_SEARCH_CLIENT);
        btSearch.addActionListener(this);

        String[] cols = {"Cant", "Producto", "Unidad", "Valor"};

        modeloTb = new MyDefaultTableModel(cols, 1);

        tbListado.setModel(modeloTb);
        tbListado.setRowHeight(60);
        tbListado.setFont(new Font("Tahoma", 0, 14));
        modeloTb.addTableModelListener(this);

        popupTabla = new JPopupMenu();
        popupListenerTabla = new MyPopupListener(popupTabla, true);
        JMenuItem item1 = new JMenuItem("Eliminar...");
        item1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int r = tbListado.getSelectedRow();
                ProductoPed pp = (ProductoPed) tbListado.getValueAt(r, 1);

                System.out.println("deleting:" + pp.getProduct().getName());
                modeloTb.removeRow(r);
                boolean del = productos.remove(pp);
                System.out.println("Rem:" + del);

            }
        });
        popupTabla.add(item1);

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

        regMesa.setText(tables.toArray());
        regMesera.setText(waiters.toArray());

        regTiempo.setEnabled(false);
        btPrint.setVisible(false);

        calcularValores();

        showLocal();

        lbFactura.setText(calculateProximoRegistro());
    }
    public static final String AC_PRINT_BILL = "AC_PRINT_BILL";
    public static final String AC_SEARCH_CLIENT = "AC_SEARCH_CLIENT";
    public static final String AC_SELECT_DELIVERY = "AC_SELECT_DELIVERY";
    public static final String AC_SELECT_LOCAL = "AC_SELECT_LOCAL";
    public static final String AC_DELETE_PEDIDO = "AC_DELETE_PEDIDO";
    public static final String AC_CHANGE_DOMICILIO = "AC_CHANGE_DOMICILIO";
    public static final String AC_CONFIRMAR_PEDIDO = "AC_CONFIRMAR_PEDIDO";

    @Override
    public void actionPerformed(ActionEvent e) {
        if (AC_CONFIRMAR_PEDIDO.equals(e.getActionCommand())) {
            calcularValores();
            try {
                verificarDatosFactura();
            } catch (Exception ex) {
            }

        } else if (AC_CHANGE_DOMICILIO.equals(e.getActionCommand())) {
            String dom = regDomicilio.getText();
            if (entregasDom[0].equals(dom)) {
                lbEntregas.setText(DCFORM_P.format(2000));
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

            invoice = null;
        } else if (AC_SELECT_DELIVERY.equals(e.getActionCommand())) {
            showDelivery();
        } else if (AC_SELECT_LOCAL.equals(e.getActionCommand())) {
            showLocal();
        } else if (AC_PRINT_BILL.equals(e.getActionCommand())) {
            if (invoice != null) {
                print(invoice);
            }
        } else if (AC_SEARCH_CLIENT.equals(e.getActionCommand())) {
            String cellphone = regCelular.getText();
            if (!cellphone.isEmpty()) {
                Client client = new Client(cellphone);
                app.getGuiManager().showClientCard(client);
            }

        }
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        switch (e.getType()) {
            case TableModelEvent.UPDATE:
                if (e.getColumn() == 0) {
                    tbListado.setValueAt(calculatePrecio(e.getLastRow()), e.getLastRow(), 3);
                    int cant = Integer.parseInt(tbListado.getValueAt(e.getLastRow(), 0).toString());
                    productos.get(e.getLastRow()).setCantidad(cant);
                }
                break;
            case TableModelEvent.INSERT:
                tbListado.setValueAt(calculatePrecio(e.getLastRow()), e.getLastRow(), 3);
                break;
            case TableModelEvent.DELETE:
                try {
                    productos.remove(e.getLastRow());
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
            System.out.println("received:" + prodPed.getProduct().getPrice());
            int cant = (int) ((Object[]) evt.getOldValue())[0];
            double price = (double) ((Object[]) evt.getOldValue())[1];
            if (prodPed.getPresentation() != null) {
                price = prodPed.getPresentation().getPrice();
            }
            addProductPed(prodPed, cant, price);
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
        Product producto = productPed.getProduct();
        if (productos.contains(productPed) && price == productPed.getPrecio()) {
            System.out.println("contains");
            try {
                int row = productos.indexOf(productPed);
                int cant = Integer.valueOf(modeloTb.getValueAt(row, 0).toString());
                modeloTb.setValueAt(cant + cantidad, row, 0);
                productPed.setCantidad(cantidad);
                productos.set(row, productPed);
            } catch (Exception e) {
            }

        } else {
            System.out.println("no contains");
            try {
                productPed.setCantidad(cantidad);
                productos.add(productPed);

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

    private void clearPedido() {
        productos.clear();
        modeloTb.setRowCount(0);
        regDomicilio.setSelected(0);
        regCelular.setText("");
        regDireccion.setText("");
        regDescuento.setText("0");

        calcularValores();
    }

    private void calcularValores() {
        double subtotal = calculateTotal();
        regSubtotal.setText(DCFORM_P.format(subtotal));
        double descuento = subtotal * calcularDescuento() / 100;
        lbDescuento1.setText(DCFORM_P.format(descuento > 0 ? descuento * -1 : descuento));
        double domicilio = 0;
        try {
            domicilio = DCFORM_P.parse(lbEntregas.getText()).doubleValue();
        } catch (Exception e) {
        }

        regTotal.setText(DCFORM_P.format(subtotal + domicilio - descuento));
    }

    private void verificarDatosFactura() throws ParseException {

        String mesa = "";
        String mesero = "";
        String celular = "";
        String direccion = "";

        if (productos.isEmpty()) {
            GUIManager.showErrorMessage(null, "No hay productos en la lista", "Pedido vacio");
            return;
        }
        String cliente;
        Waiter waitres = (Waiter) regMesera.getSelectedItem();
        Table table = (Table) regMesa.getSelectedItem();
        boolean validate = true;
        if (TIPO_LOCAL == tipo) {
            System.out.println("pedido local");
            if (waitres == null) {
                regMesera.setBorderToError();
                validate = false;
            }
            if (table == null) {
                regMesa.setBorderToError();
                validate = false;
            }
        } else {
            System.out.println("pedido domicilio");
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
            return;
        }

        celular = regCelular.getText();
        direccion = regDireccion.getText();

        verifyQuantitys();

        Invoice invoice = new Invoice();
        invoice.setFactura(calculateProximoRegistro());
        invoice.setCiclo(1L);
        invoice.setFecha(new Date());
        invoice.setIdCliente(1L);
        invoice.setDescuento(Double.parseDouble(regDescuento.getText()));

        if (waitres != null) {
            invoice.setIdWaitress(waitres.getId());
        }
        if (table != null) {
            invoice.setTable(table.getId());
        }

        System.out.println("invoice:" + invoice);

        String tipoEntrega = regDomicilio.getText().toUpperCase();
        System.out.println("tipoEntrega = " + tipoEntrega);
        switch (tipoEntrega) {
            case ENTREGA_DOMICILIO:
                invoice.setTipoEntrega(1);
                invoice.setValorDelivery(new BigDecimal(DCFORM_P.parse(lbEntregas.getText()).doubleValue()));
                break;
            case ENTREGA_LOCAL:
                invoice.setTipoEntrega(2);
                invoice.setValorDelivery(BigDecimal.ZERO);
                break;
            case ENTREGA_PARA_LLEVAR:
                invoice.setTipoEntrega(3);
                invoice.setValorDelivery(BigDecimal.ZERO);
                break;
        }

        invoice.setProducts(productos);

        invoice.setValor(totalFact);

        this.invoice = invoice;

        if (app.getControl().addInvoice(invoice)) {
            btPrint.setVisible(true);
            try {
                tbListado.getCellEditor().stopCellEditing();
            } catch (Exception e) {
            }

            regMesa.setEditable(false);
            regMesera.setEditable(false);
            regCelular.setEditable(false);
            regDireccion.setEditable(false);
            regDomicilio.setEditable(false);

            btTogle1.setEnabled(false);
            btTogle2.setEnabled(false);

            modeloTb.setColumnEditable(0, false);
            btDelete.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "new-file.png", 18, 18)));
        }
//        app.getGuiManager().reviewFacture(invoice);

    }

    private void verifyQuantitys() {
        for (int i = 0; i < productos.size(); i++) {
            ProductoPed pp = productos.get(i);
            int cant = (int) modeloTb.getValueAt(i, 0);
            if (pp.getCantidad() != cant) {
                productos.get(i).setCantidad(cant);
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

            if (invoice.getTipoEntrega() == TIPO_DOMICILIO) {
                escpos.writeLF(font2, "_________________________________________________");

                escpos.writeLF(String.format(formatInfo, "1", "Domicilio", "", app.DCFORM_P.format(invoice.getValorDelivery())));// app.DCFORM_P.format(invoice.getValorDelivery().doubleValue())));

            }
            escpos.writeLF(font2, "_________________________________________________");

            escpos.writeLF(String.format(formatInfo, "", "", "Total:", app.DCFORM_P.format(invoice.getValor())));

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
        regMesa.setVisible(false);
        regMesera.setVisible(false);
        regDomicilio.setText(entregasDom);
        regDomicilio.setSelected(0);

        regTiempo.setTint(colorDelivery);

        regTiempo.setTint(colorDelivery);
        regSubtotal.setTint(colorDelivery);
        regDescuento.setTint(colorDelivery);
        regTotal.setTint(colorDelivery);
        regDomicilio.setTint(colorDelivery);

        Border border = regTiempo.getBorder();

        lbTiempos.setBorder(border);
        lbEntregas.setBorder(border);
        lbDescuento1.setBorder(border);

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
        regMesa.setVisible(true);
        regMesera.setVisible(true);
        regDomicilio.setText(entregasLoc);
        regDomicilio.setSelected(0);

        regTiempo.setTint(colorLocal);
        regSubtotal.setTint(colorLocal);
        regDescuento.setTint(colorLocal);
        regTotal.setTint(colorLocal);
        regDomicilio.setTint(colorLocal);

        Border border = regTiempo.getBorder();

        lbTiempos.setBorder(border);
        lbEntregas.setBorder(border);
        lbDescuento1.setBorder(border);

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
        lbTiempos = new javax.swing.JLabel();
        regTiempo = new com.bacon.gui.util.Registro(BoxLayout.X_AXIS, "Tiempo",new String[1],60);
        regDomicilio = new com.bacon.gui.util.Registro(BoxLayout.X_AXIS, "Entrega",new String[1],60);
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(regDireccion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(regTiempo, javax.swing.GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE)
                                    .addComponent(regDomicilio, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(1, 1, 1)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(lbTiempos, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                                    .addComponent(lbEntregas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btPrint, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(regDescuento, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(regSubtotal, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbDescuento1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(regTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(12, 12, 12))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(regCelular, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addComponent(btSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(2, 2, 2)
                        .addComponent(lbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(regMesa, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(regMesera, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lbTitle)
                        .addGap(1, 1, 1)
                        .addComponent(lbFactura, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lbCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(regCelular, javax.swing.GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE)
                    .addComponent(btSearch, javax.swing.GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE)
                    .addComponent(lbStatus, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(regMesa, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(regMesera, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(regDireccion, javax.swing.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 87, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(regSubtotal, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(regTiempo, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbTiempos, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                    .addComponent(btPrint, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btSearch, regCelular, regDireccion});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {lbDescuento1, lbEntregas, lbTiempos, regDescuento, regDomicilio, regSubtotal, regTiempo, regTotal});

    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btConfirm;
    private javax.swing.JButton btDelete;
    private javax.swing.JButton btPrint;
    private javax.swing.JButton btSearch;
    private javax.swing.JToggleButton btTogle1;
    private javax.swing.JToggleButton btTogle2;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lbCliente;
    private javax.swing.JLabel lbDescuento1;
    private javax.swing.JLabel lbEntregas;
    private javax.swing.JLabel lbFactura;
    private javax.swing.JLabel lbStatus;
    private javax.swing.JLabel lbTiempos;
    private javax.swing.JLabel lbTitle;
    private com.bacon.gui.util.Registro regCelular;
    private com.bacon.gui.util.Registro regDescuento;
    private com.bacon.gui.util.Registro regDireccion;
    private com.bacon.gui.util.Registro regDomicilio;
    private com.bacon.gui.util.Registro regMesa;
    private com.bacon.gui.util.Registro regMesera;
    private com.bacon.gui.util.Registro regSubtotal;
    private com.bacon.gui.util.Registro regTiempo;
    private com.bacon.gui.util.Registro regTotal;
    private javax.swing.JTable tbListado;
    // End of variables declaration//GEN-END:variables
}
