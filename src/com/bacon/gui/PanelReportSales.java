package com.bacon.gui;

import com.bacon.Aplication;
import com.bacon.MyConstants;
import com.bacon.domain.Invoice;
import com.bacon.domain.ProductoPed;
import com.bacon.gui.util.MyDatePickerImp;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import org.bx.gui.MyDefaultTableModel;
import org.dz.PanelCapturaMod;
import org.dz.TextFormatter;

/**
 *
 * @author lrod
 */
public class PanelReportSales extends PanelCapturaMod implements ActionListener {

    private final Aplication app;
    private FormatRenderer formatRenderer;
    private String[] colNamesProd;
    private float[] colWidthProd;
    private int[] colAlignProd;
    private String[] colNamesSal;
    private float[] colWidthSal;
    private int[] colAlignSal;
    private MyDefaultTableModel modelo;
    private MyDatePickerImp datePick1, datePick2;
    private Font DF_TABLE_FONT;
    private int numFilasDF;

    public static final String AC_SEL_TIPO_REGISTRO = "AC_SEL_TIPO_REGISTRO";
    public static final String AC_CHANGE_TIPO = "AC_CHANGE_TIPO";
    public static final String AC_SEL_PERIODO = "AC_SEL_PERIODO";
    public static final String AC_SEL_OPCION = "AC_SEL_OPCION";
    public static final String AC_DO_CONSULT = "AC_DO_CONSULT";
    public static final String AC_SEL_AÑOS = "AC_SEL_AÑOS";
    public static final String AC_SEL_MESES = "AC_SEL_MESES";

    private int filas;
    private int paginas;
    private int paginaActual;
    private int total;
    public static final String ACTION_LAST_PAGE = "ACTION_LAST_PAGE";
    public static final String ACTION_NEXT_PAGE = "ACTION_NEXT_PAGE";
    public static final String ACTION_PREV_PAGE = "ACTION_PREV_PAGE";
    public static final String ACTION_FIRST_PAGE = "ACTION_FIRST_PAGE";

    private String title;
    private String query;

    /**
     * Creates new form PanelReportSales
     */
    public PanelReportSales(Aplication app) {
        this.app = app;
        initComponents();
        createComponents();
    }

    private void createComponents() {
        formatRenderer = new FormatRenderer(app.getDCFORM_P());

        numFilasDF = 30;

        datePick1.addPropertyChangeListener(this);
        datePick2.addPropertyChangeListener(this);
        
        regDateP1.setActionCommand("AC_CHANGE_DATE");
        regDateP1.addActionListener(this);

        regDateP2.setActionCommand("AC_CHANGE_DATE");
        regDateP2.addActionListener(this);

//        datePick1 = new MyDatePickerImp(new Date(), true);
//        datePick2 = new MyDatePickerImp(new Date(), true);
        colNamesProd = new String[]{"N°", "Producto", "Factura", "", "Locación", "Fecha", "Cantidad", "Valor"};
        colWidthProd = new float[]{0.8f, 3.2f, 1.4f, 3, 2, 1.5f, 1.4f, 1.7f};
        colAlignProd = new int[]{0, 0, 0, 0, 0, 1, 2, 2};

//        colNamesMov = new String[]{"N°", "Fecha", "Factura", "Cliente", "Tipo", " Valor", "Abono", "Vencimiento"};
//        colWidthMov = new float[]{0.8f, 1.5f, 1.5f, 3, 1.5f, 2, 2, 2};
//        colAlignMov = new int[]{0, 0, 0, 0, 0, 2, 2, 1};
        colNamesSal = new String[]{"N°", "Factura", "Fecha", "Estado", "Ciclo", "Tipo", " Cliente", "Valor", "Domicilio", "Servicio"};
        colWidthSal = new float[]{0.3f, 1.6f, 2.3f, 1.4f, 3, 1.5f, 1.8f, 1.75f, 1.75f, 1.75f};
        colAlignSal = new int[]{0, 0, 0, 0, 0, 0, 2, 2, 2, 2};

        modelo = new MyDefaultTableModel(colNamesSal, 0);
        tableReport.setModel(modelo);
        tableReport.setRowHeight(22);
//        tableReport.getColumnModel().getColumn(4).setCellRenderer(formatRenderer);

        DF_TABLE_FONT = new Font("Tahoma", 0, 14);

        regTipo.setText(MyConstants.TIPO_REPORTE);
        regTipo.setFontCampo(new Font("Tahoma", 0, 15));
        regTipo.setForeground(Color.blue.darker());
        regTipo.setActionCommand(AC_CHANGE_TIPO);
        regTipo.addActionListener(this);

        btConsult.setText("Consultar");
        btConsult.setActionCommand(AC_DO_CONSULT);
        btConsult.addActionListener(this);

        regPeriodo.setText(MyConstants.PERIODOS);
        regPeriodo.setActionCommand(AC_SEL_PERIODO);
        regPeriodo.addActionListener(this);
        regPeriodo.setFontCampo(new Font("Tahoma", 0, 15));

        regSelection.setActionCommand(AC_SEL_OPCION);
        regSelection.addActionListener(this);
        regSelection.setText(new String[]{"HOY", "OTRO DIA"});
        regSelection.setFontCampo(new Font("Tahoma", 0, 15));

        regTipoReg.setActionCommand(AC_SEL_TIPO_REGISTRO);
        regTipoReg.addActionListener(this);
        regTipoReg.setText(new String[]{"TODOS", "FACTURA VENTA"});
        regTipoReg.setFontCampo(new Font("Tahoma", 0, 15));

        //NAV
        btUpdate.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "update.png", 12, 12)));
        btUpdate.setToolTipText("Actualizar");
        btFirstPage.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "first_page.png", 12, 12)));
        btFirstPage.setToolTipText("Primera pagina");
        btPrevPage.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "previus_page.png", 12, 12)));
        btPrevPage.setToolTipText("Pagina anterior");
        btNextPage.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "next_page.png", 12, 12)));
        btNextPage.setToolTipText("Pagina siguiente");
        btLastPage.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "last_page.png", 12, 12)));
        btLastPage.setToolTipText("Ultima pagina");

        btFirstPage.setActionCommand(ACTION_FIRST_PAGE);
        btFirstPage.addActionListener(this);
        btPrevPage.setActionCommand(ACTION_PREV_PAGE);
        btPrevPage.addActionListener(this);
        btNextPage.setActionCommand(ACTION_NEXT_PAGE);
        btNextPage.addActionListener(this);
        btLastPage.setActionCommand(ACTION_LAST_PAGE);
        btLastPage.addActionListener(this);

        labelPaginas.setHorizontalAlignment(SwingConstants.CENTER);
        labelPaginas.setText("<html><font color='blue'> . . . </font></html>");
//        labelPaginas.setVisible(false);
        tfFilas.setText("" + numFilasDF);
        tfFilas.setDocument(TextFormatter.getIntegerLimiter());

        paginaActual = 1;
//        setupNavegador();

        opcionHoy();

    }

    public String[] getAños() {
        String[] años = new String[5];
        Calendar cal = Calendar.getInstance();
        int cYear = cal.get(Calendar.YEAR);
        for (int i = 0; i < años.length; i++) {
            años[i] = String.valueOf(cYear--);
        }
        return años;
    }

    public void opcionHoy() {
        datePick1.setDate(new Date());
        regDateP1.setVisible(true);
        regDateP2.setEnabled(false);
        regDateP2.setVisible(false);
        query = "(sale_date between '" + app.DF_SQL.format(datePick1.getDate()) + " 00:00:00' and '" + app.DF_SQL.format(datePick1.getDate()) + " 23:59:59')";
        makeTitle();
    }

    public void opcionOtroDia() {
        regDateP1.setVisible(true);
        regDateP1.setEnabled(true);
        regDateP2.setVisible(false);
        query = "(sale_date between '" + app.DF_SQL.format(datePick1.getDate()) + " 00:00:00' and '" + app.DF_SQL.format(datePick1.getDate()) + " 23:59:59')";
    }

    public void opcionEstaSemana() {
        regDateP1.setVisible(true);
        regDateP1.setEnabled(false);
        regDateP2.setVisible(true);
        regDateP2.setEnabled(false);

        Calendar cal = Calendar.getInstance();
        int dayOfTheWeek = cal.get(Calendar.DAY_OF_WEEK);
        cal.add(Calendar.DAY_OF_WEEK, Calendar.SUNDAY - dayOfTheWeek);
        datePick1.setDate(cal.getTime());
        cal.add(Calendar.DAY_OF_WEEK, +7);
        datePick2.setDate(cal.getTime());

        query = "(sale_date between '" + app.DF_SQL.format(datePick1.getDate()) + " 00:00:00' and '" + app.DF_SQL.format(datePick2.getDate()) + " 23:59:59')";
    }

    public void opcionSemanaPasada() {
        regDateP1.setVisible(true);
        regDateP1.setEnabled(false);
        regDateP2.setVisible(true);
        regDateP2.setEnabled(false);

        Calendar cal = Calendar.getInstance();
        int dayOfTheWeek = cal.get(Calendar.DAY_OF_WEEK);
        cal.add(Calendar.DAY_OF_WEEK, Calendar.SUNDAY - dayOfTheWeek);
        datePick2.setDate(cal.getTime());
        cal.add(Calendar.DAY_OF_WEEK, -7);
        datePick1.setDate(cal.getTime());

        query = "(sale_date between '" + app.DF_SQL.format(datePick1.getDate()) + " 00:00:00' and '" + app.DF_SQL.format(datePick2.getDate()) + " 23:59:59')";
    }

    private void opcionMeses(int mes) {
        regDateP1.setVisible(true);
        regDateP1.setEnabled(false);
        regDateP2.setVisible(true);
        regDateP2.setEnabled(false);
        regSelection.setSelected(mes);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, mes);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        datePick1.setDate(cal.getTime());
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        datePick2.setDate(cal.getTime());

        query = "(sale_date between '" + app.DF_SQL.format(datePick1.getDate()) + " 00:00:00' and '" + app.DF_SQL.format(datePick2.getDate()) + " 23:59:59')";
    }

    private void opcionAños(int año) {
        regDateP1.setVisible(true);
        regDateP1.setEnabled(false);
        regDateP2.setVisible(true);
        regDateP2.setEnabled(false);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, año);
        cal.set(Calendar.DAY_OF_YEAR, cal.getActualMinimum(Calendar.DAY_OF_YEAR));
        datePick1.setDate(cal.getTime());
        cal.set(Calendar.DAY_OF_YEAR, cal.getActualMaximum(Calendar.DAY_OF_YEAR));
        datePick2.setDate(cal.getTime());

        query = "(sale_date between '" + app.DF_SQL.format(datePick1.getDate()) + " 00:00:00' and '" + app.DF_SQL.format(datePick2.getDate()) + " 23:59:59')";
    }

    private void opcionRango() {
        regDateP1.setVisible(true);
        regDateP1.setEnabled(true);
        regDateP2.setVisible(true);
        regDateP2.setEnabled(true);

        Calendar cal = Calendar.getInstance();
        datePick2.setDate(cal.getTime());
        cal.add(Calendar.DATE, -15);
        datePick1.setDate(cal.getTime());
        if (datePick1.getDate().compareTo(datePick2.getDate()) > 0) {
//            GUIManager.showErrorMessage("La fecha incicial es mayor ", cal, query);
        }
        query = "(sale_date between '" + app.DF_SQL.format(datePick1.getDate()) + " 00:00:00' and '" + app.DF_SQL.format(datePick2.getDate()) + " 23:59:59')";
    }

    private void changeTipo() {
        String tipo = regTipo.getText();
        if (MyConstants.TIPO_REPORTE[0].equals(tipo)) {
            regTipoReg.setVisible(true);
        } else if (MyConstants.TIPO_REPORTE[1].equals(tipo)) {
            regTipoReg.setVisible(false);
        } else if (MyConstants.TIPO_REPORTE[2].equals(tipo)) {
            regTipoReg.setVisible(false);
        } else if (MyConstants.TIPO_REPORTE[3].equals(tipo)) {
            regTipoReg.setVisible(false);
        }
        makeTitle();
    }

    private void setupTable(String[] colNames, int[] colAlign, float[] colWidth) {
        int W = tableReport.getWidth();
        float SUM = com.bacon.Utiles.sumarArray(colWidth);
//        modelo.setColumnIdentifiers(colNames);
//        for (int i = 0; i < colNames.length; i++) {
////            tabla.getColumnModel().getColumn(i).setHeaderValue(colNames[i]);
//            tableReport.getColumnModel().getColumn(i).setCellRenderer(new TablaCellRenderer(true, DF_TABLE_FONT,
//                    colAlign[i] == 0 ? SwingConstants.LEFT : colAlign[i] == 2 ? SwingConstants.RIGHT : SwingConstants.CENTER,
//                    colAlign[i] == 2 ? app.getDCFORM_P() : null
//            ));
//            int widthCol = (int) (colWidth[i] / SUM * W);
//            tableReport.getColumnModel().getColumn(i).setMinWidth(widthCol);
//            tableReport.getColumnModel().getColumn(i).setPreferredWidth(widthCol);
//        }
    }

    private void loadData() {
        String TIPO = regTipo.getText();
        doConsult(TIPO);
    }

    private void doConsult(String TIPO) {
        makeTitle();
        if ("RANGO".equals(regPeriodo.getText())) {
            query = "(sale_date between '" + app.DF_SQL.format(datePick1.getDate()) + " 00:00:00' and '" + app.DF_SQL.format(datePick2.getDate()) + " 23:59:59')";
        } else if ("OTRO DIA".equals(regSelection.getText())) {
            query = "(sale_date between '" + app.DF_SQL.format(datePick1.getDate()) + " 00:00:00' and '" + app.DF_SQL.format(datePick1.getDate()) + " 23:59:59')";
        }

        setupNavegador();

        if (MyConstants.TIPO_REPORTE[0].equals(TIPO)) {
            //filtrar tipos de registros
            int sel = regTipoReg.getSelected();
            String locQuery = query;
            if (sel > 0) {
//                locQuery = query.isEmpty() ? "tipoRegistro=" + sel : query + " AND tipoRegistro=" + sel;
            }
            int ini = (paginaActual - 1) * filas;
            int fin = ini + filas;
            ArrayList<Invoice> salidaList = app.getControl().getInvoicesLitelList(locQuery, "sale_date", ini, fin);
            setupTable(colNamesSal, colAlignSal, colWidthSal);
            populateTabla(salidaList, TIPO, ini + 1);
        } else if (MyConstants.TIPO_REPORTE[1].equals(TIPO)) {
            ArrayList productSalidaList = app.getControl().getInvoiceByProductListWhere(query, "sale_date");
            setupTable(colNamesProd, colAlignProd, colWidthProd);
            populateTabla(productSalidaList, TIPO, 1);
        }
        /* } else if (MyConstants.TIPO_REPORTE[2].equals(TIPO)) {
            ArrayList<Entrada> entradaList = app.getControl().getEntradaList(query, "fecha");
            setupTable(colNamesMov, colAlignMov, colWidthMov);
            populateTabla(entradaList, TIPO);
        } else if (MyConstants.TIPO_REPORTE[3].equals(TIPO)) {
            ArrayList productEntradaList = app.getControl().getEntradaByProductListWhere(query, "fecha");
            setupTable(colNamesProd, colAlignProd, colWidthProd);
            populateTabla(productEntradaList, TIPO);
        }*/
//        System.out.println("query:: = " + query);
    }

    public void makeTitle() {
        String tipo = regTipo.getText();
        int sel = regPeriodo.getSelected();
        String comp = "<html><font color=blue>" + app.DF_SL.format(datePick1.getDate()) + (sel > 0 ? "</font> a <font color=blue>" + app.DF_SL.format(datePick2.getDate()) + "</font>" : "");
        title = "REPORTE DE " + tipo + " :  " + comp;
        lbTitle.setText("<html><font color=#D52715 size=+1>REPORTE DE " + tipo + "</font>:<font size=+1> " + comp + "</font></html>");
    }

    private void populateTabla(ArrayList list, String tipo, int ini) {
        modelo.setRowCount(0);
        if (tipo.equals(MyConstants.TIPO_REPORTE[0])) {  // VENTAS Y COMPRAS
            SwingWorker sw = new SwingWorker() {
                @Override
                protected Object doInBackground() throws Exception {
                    for (int i = 0; i < list.size(); i++) {
                        Invoice inv = (Invoice) list.get(i);
                        double total = 0;
                        if (tipo.equals(MyConstants.TIPO_REPORTE[0])) {  //VENTAS
//                            List<ProductoPed> products = inv.getProducts();
//                            for (int j = 0; j < products.size(); j++) {
//                                ProductoPed get = products.get(j);
//                                total += get.getPrecio() * get.getCantidad();
//                            }
                            Invoice invoice = (Invoice) list.get(i);
//                            modelo.setColumnIdentifiers(colNamesSal);
                            tableReport.getColumnModel().getColumn(7).setCellRenderer(formatRenderer);
                            tableReport.getColumnModel().getColumn(8).setCellRenderer(formatRenderer);
                            tableReport.getColumnModel().getColumn(9).setCellRenderer(formatRenderer);
                            modelo.addRow(new Object[]{
                                ini + i,
                                invoice.getFactura(),
                                app.DF_FULL2.format(invoice.getFecha()),
                                Invoice.STATUSES[invoice.getStatus()],
                                invoice.getCiclo(),
                                MyConstants.TIPO_PEDIDO[invoice.getTipoEntrega() - 1],
                                invoice.getIdCliente() == 1 ? "LOCAL" : invoice.getIdCliente(),
                                invoice.getValor(),
                                invoice.getValorDelivery(),
                                invoice.getValor().doubleValue() * invoice.getPorcService() / 100.0
                            });

                        } else if (tipo.equals(MyConstants.TIPO_REPORTE[2])) { //COMPRAS

                        }
                        modelo.setRowEditable(modelo.getRowCount() - 1, false);
                    }
                    return true;
                }

                @Override
                protected void done() {
                    app.getGuiManager().setDefaultCursor();
                }
            };
            app.getGuiManager().setWaitCursor();
            tableReport.updateUI();
            sw.execute();

        } else if (tipo.equals(MyConstants.TIPO_REPORTE[1])) {

            colNamesProd[3] = "Cliente";
            tableReport.getColumnModel().getColumn(5).setHeaderValue(colNamesProd[5]);
//            modelo.setColumnIdentifiers(colNamesProd);
//            tabla.getColumnModel().getColumn(3).setCellRenderer(formatRenderer);
//            tabla.getColumnModel().getColumn(4).setCellRenderer(formatRenderer);
            for (int i = 0; i < list.size(); i++) {
                Object[] row = (Object[]) list.get(i);
//                ProductoPed prod = app.getControl().getProducto(row[7].toString());
//                Cliente cliente = app.getControl().getCliente(Long.parseLong(row[5].toString()));
//                Location location = app.getControl().getLocation(Integer.parseInt(row[6].toString()));

//                modelo.addRow(new Object[]{modelo.getRowCount() + 1,
////                    prod.getNombre(), row[1],
//                    cliente.getRazonSocial(), location.getNombre(), row[4], row[2], row[3]
//                });
//                modelo.setRowEditable(modelo.getRowCount() - 1, false);
            }
        }
        //Limpiar la query no se acumele la misma consulta
//        query = "";  // error limpia el periodo seleccionado

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnFilters = new javax.swing.JPanel();
        regSelection = new org.dz.Registro(BoxLayout.Y_AXIS, "Seleccion", new String[1]);
        regPeriodo = new org.dz.Registro(BoxLayout.Y_AXIS, "Periodo", new String[1]);
        regTipo = new org.dz.Registro(BoxLayout.Y_AXIS, "Tipo", new String[1]);
        btConsult = new javax.swing.JButton();
        regTipoReg = new org.dz.Registro(BoxLayout.Y_AXIS, "TipoReg", new String[1]);
        datePick1 = new MyDatePickerImp(new Date(), true);
        regDateP1 = new com.bacon.gui.util.Registro(BoxLayout.Y_AXIS, "Fecha inicio", datePick1);
        datePick2 = new MyDatePickerImp(new Date(), true);
        regDateP2 = new com.bacon.gui.util.Registro(BoxLayout.Y_AXIS,"Fecha final", datePick2);
        lbTitle = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableReport = new javax.swing.JTable();
        pnLabels = new javax.swing.JPanel();
        panelNav = new javax.swing.JPanel();
        btUpdate = new javax.swing.JButton();
        btFirstPage = new javax.swing.JButton();
        btPrevPage = new javax.swing.JButton();
        btLastPage = new javax.swing.JButton();
        btNextPage = new javax.swing.JButton();
        lbBuscar = new javax.swing.JLabel();
        tfBuscar = new javax.swing.JTextField();
        labelPaginas = new javax.swing.JLabel();
        lbFilas = new javax.swing.JLabel();
        tfFilas = new javax.swing.JTextField();

        pnFilters.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btConsult.setText("jButton1");

        javax.swing.GroupLayout pnFiltersLayout = new javax.swing.GroupLayout(pnFilters);
        pnFilters.setLayout(pnFiltersLayout);
        pnFiltersLayout.setHorizontalGroup(
            pnFiltersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnFiltersLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(regTipo, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(regPeriodo, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(regSelection, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(regDateP1, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(regDateP2, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(regTipoReg, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 274, Short.MAX_VALUE)
                .addComponent(btConsult)
                .addContainerGap())
        );

        pnFiltersLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {regDateP1, regDateP2, regPeriodo, regSelection, regTipo, regTipoReg});

        pnFiltersLayout.setVerticalGroup(
            pnFiltersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnFiltersLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnFiltersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnFiltersLayout.createSequentialGroup()
                        .addGroup(pnFiltersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(regTipoReg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(regTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(regDateP2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(regDateP1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(regPeriodo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(regSelection, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.CENTER, pnFiltersLayout.createSequentialGroup()
                        .addComponent(btConsult)
                        .addGap(12, 12, 12))))
        );

        pnFiltersLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btConsult, regDateP1, regDateP2, regPeriodo, regSelection, regTipo, regTipoReg});

        lbTitle.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        tableReport.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(tableReport);

        pnLabels.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnLabels.setLayout(new javax.swing.BoxLayout(pnLabels, javax.swing.BoxLayout.LINE_AXIS));

        panelNav.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btUpdateActionPerformed(evt);
            }
        });

        lbBuscar.setFont(new java.awt.Font("DejaVu Sans", 0, 11)); // NOI18N
        lbBuscar.setText("Buscar:");

        labelPaginas.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelPaginas.setText("Pagina: ");

        lbFilas.setFont(new java.awt.Font("DejaVu Sans", 0, 11)); // NOI18N
        lbFilas.setText("Filas:");

        tfFilas.setMinimumSize(new java.awt.Dimension(30, 35));
        tfFilas.setPreferredSize(new java.awt.Dimension(30, 35));
        tfFilas.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tfFilasFocusLost(evt);
            }
        });
        tfFilas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfFilasActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelNavLayout = new javax.swing.GroupLayout(panelNav);
        panelNav.setLayout(panelNavLayout);
        panelNavLayout.setHorizontalGroup(
            panelNavLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelNavLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(btUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btFirstPage, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btPrevPage, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btNextPage, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btLastPage, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelPaginas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbFilas)
                .addGap(3, 3, 3)
                .addComponent(tfFilas, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbBuscar)
                .addGap(8, 8, 8)
                .addComponent(tfBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        panelNavLayout.setVerticalGroup(
            panelNavLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelNavLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(panelNavLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btLastPage, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelPaginas)
                    .addComponent(btUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btFirstPage, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btPrevPage, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btNextPage, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbFilas)
                    .addComponent(tfFilas, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbBuscar)
                    .addComponent(tfBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6))
        );

        panelNavLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {tfBuscar, tfFilas});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(pnLabels, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnFilters, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lbTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelNav, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnFilters, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(panelNav, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnLabels, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {lbTitle, panelNav});

    }// </editor-fold>//GEN-END:initComponents

    private void btUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btUpdateActionPerformed
        setupNavegador();
        loadData();
    }//GEN-LAST:event_btUpdateActionPerformed

    private void tfFilasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfFilasActionPerformed
        setupNavegador();
        loadData();
    }//GEN-LAST:event_tfFilasActionPerformed

    private void tfFilasFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tfFilasFocusLost
        setupNavegador();
        loadData();
    }//GEN-LAST:event_tfFilasFocusLost


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btConsult;
    private javax.swing.JButton btFirstPage;
    private javax.swing.JButton btLastPage;
    private javax.swing.JButton btNextPage;
    private javax.swing.JButton btPrevPage;
    private javax.swing.JButton btUpdate;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel labelPaginas;
    private javax.swing.JLabel lbBuscar;
    private javax.swing.JLabel lbFilas;
    private javax.swing.JLabel lbTitle;
    private javax.swing.JPanel panelNav;
    private javax.swing.JPanel pnFilters;
    private javax.swing.JPanel pnLabels;
    private com.bacon.gui.util.Registro regDateP1;
    private com.bacon.gui.util.Registro regDateP2;
    private org.dz.Registro regPeriodo;
    private org.dz.Registro regSelection;
    private org.dz.Registro regTipo;
    private org.dz.Registro regTipoReg;
    private javax.swing.JTable tableReport;
    private javax.swing.JTextField tfBuscar;
    private javax.swing.JTextField tfFilas;
    // End of variables declaration//GEN-END:variables

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(MyDatePickerImp.DATE_CHANGED.equals(evt.getPropertyName())){
            makeTitle();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (AC_DO_CONSULT.equals(e.getActionCommand())) {
            String tipo = regTipo.getText();
            doConsult(tipo);
        } else if (AC_CHANGE_TIPO.equals(e.getActionCommand())) {
            changeTipo();
        } else if ("AC_CHANGE_DATE".equals(e.getActionCommand())) {
            makeTitle();
        } else if (AC_SEL_PERIODO.equals(e.getActionCommand())) {
            if (MyConstants.PERIODOS[0].equals(regPeriodo.getText())) {  //DIA
                regSelection.setVisible(true);
                regSelection.setText(new String[]{"HOY", "OTRO DIA"});
                regSelection.setActionCommand(AC_SEL_OPCION);
                opcionHoy();
            } else if (MyConstants.PERIODOS[1].equals(regPeriodo.getText())) {  //SEMANA
                regSelection.setVisible(true);
                regSelection.setText(new String[]{"ESTA SEMANA", "SEMANA PASADA"});
                regSelection.setActionCommand(AC_SEL_OPCION);
                opcionEstaSemana();
            } else if (MyConstants.PERIODOS[2].equals(regPeriodo.getText())) {  //MES
                regSelection.setVisible(true);
                regSelection.setText(MyConstants.MONTHS);
                regSelection.setMaxRowCount(12);
                regSelection.setActionCommand(AC_SEL_MESES);
                int mes = Calendar.getInstance().get(Calendar.MONTH);
                opcionMeses(mes);
            } else if (MyConstants.PERIODOS[3].equals(regPeriodo.getText())) {  //AÑOS
                regSelection.setVisible(true);
                regSelection.setText(getAños());
                regSelection.setActionCommand(AC_SEL_AÑOS);
                opcionAños(Integer.parseInt(regSelection.getText()));
            } else if (MyConstants.PERIODOS[4].equals(regPeriodo.getText())) {  //RANGO
                regSelection.setVisible(false);
                opcionRango();
            }
            makeTitle();
        } else if (AC_SEL_OPCION.equals(e.getActionCommand())) {
            if ("HOY".equals(regSelection.getText())) {  //HOY                
                opcionHoy();
            } else if ("OTRO DIA".equals(regSelection.getText())) {  //MES
                opcionOtroDia();
            } else if ("ESTA SEMANA".equals(regSelection.getText())) {  //MES
                opcionEstaSemana();
            } else if ("SEMANA PASADA".equals(regSelection.getText())) {  //MES
                opcionSemanaPasada();
            }
            makeTitle();
        } else if (AC_SEL_MESES.equals(e.getActionCommand())) {
            opcionMeses(regSelection.getSelected());
            makeTitle();
        } else if (AC_SEL_AÑOS.equals(e.getActionCommand())) {
            opcionAños(Integer.parseInt(regSelection.getText()));
            makeTitle();
        } else if (e.getActionCommand().equals(ACTION_NEXT_PAGE)) {
            paginaActual++;
//            setupNavegador();
            loadData();
        } else if (e.getActionCommand().equals(ACTION_PREV_PAGE)) {
            paginaActual--;
//            setupNavegador();
            loadData();
        } else if (e.getActionCommand().equals(ACTION_FIRST_PAGE)) {
            paginaActual = 1;
//            setupNavegador();
            loadData();
        } else if (e.getActionCommand().equals(ACTION_LAST_PAGE)) {
            paginaActual = paginas;
//            setupNavegador();
            loadData();
        }
    }

    private void setupNavegador() {
        total = 0;
//        if (cbCategory.getSelectedItem().equals(MyConstants.CATEGORY[1])) {  //POTENCIAL

        total = app.getControl().countInvoices(query);
        try {
            filas = tfFilas.getText() != null ? Integer.parseInt(tfFilas.getText()) : numFilasDF;
        } catch (Exception e) {
            filas = 0;
        }
        filas = filas <= 0 ? numFilasDF : filas;
        tfFilas.setText(String.valueOf(filas));
        if (total > filas) {
            double d = Double.valueOf(total) / Double.valueOf(filas);
            paginas = (int) Math.ceil(d);
        } else {
            paginas = 1;
        }
        if (paginaActual > paginas) {
            paginaActual = paginas;
        }
//        paginaActual = paginaActual;
        if (paginas == 1) {
            btFirstPage.setEnabled(false);
            btPrevPage.setEnabled(false);
            btLastPage.setEnabled(false);
            btNextPage.setEnabled(false);
        } else if (paginaActual == 1) {
            btFirstPage.setEnabled(false);
            btPrevPage.setEnabled(false);
            btLastPage.setEnabled(true);
            btNextPage.setEnabled(true);
        } else if (paginaActual == paginas) {
            btFirstPage.setEnabled(true);
            btPrevPage.setEnabled(true);
            btLastPage.setEnabled(false);
            btNextPage.setEnabled(false);
        } else {
            btFirstPage.setEnabled(true);
            btPrevPage.setEnabled(true);
            btLastPage.setEnabled(true);
            btNextPage.setEnabled(true);
        }
        labelPaginas.setText("<html><font size=-2>Pags: </font><font color='blue' size=-2>" + paginaActual + " de " + paginas + "</font>"
                + "<br><p><font size=-2>< " + total + " Reg. </font></p></html>");

        labelPaginas.repaint();
        btFirstPage.repaint();
        btLastPage.repaint();
        btNextPage.repaint();
        btPrevPage.repaint();
        panelNav.updateUI();
    }

    public Box getLabel(String title, String value) {
        Box box = new Box(BoxLayout.Y_AXIS);
        JLabel lbTitle = new JLabel(title);
        lbTitle.setBorder(BorderFactory.createLineBorder(Color.GREEN));
        box.add(lbTitle);
        JLabel lbValue = new JLabel(title);
        lbValue.setBorder(BorderFactory.createLineBorder(Color.RED));
        box.add(lbValue);

        return box;
    }

}
