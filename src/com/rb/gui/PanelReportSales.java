package com.rb.gui;

import com.rb.Aplication;
import com.rb.MyConstants;
import static com.rb.MyConstants.FILTER_NUM_INT_DIFFERENT;
import static com.rb.MyConstants.FILTER_NUM_INT_GREATER_EQUAL;
import static com.rb.MyConstants.FILTER_NUM_INT_LESS;
import static com.rb.MyConstants.FILTER_NUM_INT_LESS_EQUAL;
import com.rb.domain.Invoice;
import com.rb.gui.util.MyDatePickerImp;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.table.TableRowSorter;
import org.dz.PanelCapturaMod;
import org.dz.TextFormatter;
import java.util.Collections;
import java.util.List;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import org.dz.MyDefaultTableModel;

/**
 *
 * @author lrod
 */
public class PanelReportSales extends PanelCapturaMod implements ActionListener, ListSelectionListener {

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
    private Date start, end;
    private TableRowSorter<MyDefaultTableModel> lastSorter;

    public static final String AC_SEL_TIPO_REGISTRO = "AC_SEL_TIPO_REGISTRO";
    public static final String AC_SEL_CATEGORY = "AC_SEL_CATEGORY";
    public static final String AC_SEL_PRODUCT = "AC_SEL_PRODUCT";
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

    public static final String SEL_TODAS = "<- TODAS ->";

    private String title;
    private String query;

    private LabelInfo lbInfTotal, lbInfDelivery, lbInfService;
    private JLabel labelStatus;

    /**
     * Creates new form PanelReportSales
     *
     * @param app
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
        colNamesProd = new String[]{"idprod", "Categoria", "Producto", "Presentacion", "Cantidad", "Valor"};
        colWidthProd = new float[]{0.5f, 0.5f, 2f, 2f, 1.8f, 2f};
        colAlignProd = new int[]{0, 0, 0, 0, 2, 2};

//        colNamesMov = new String[]{"N°", "Fecha", "Factura", "Cliente", "Tipo", " Valor", "Abono", "Vencimiento"};
//        colWidthMov = new float[]{0.8f, 1.5f, 1.5f, 3, 1.5f, 2, 2, 2};
//        colAlignMov = new int[]{0, 0, 0, 0, 0, 2, 2, 1};
        colNamesSal = new String[]{"N°", "Factura", "Fecha", "Estado", "Ciclo", "Tipo", " Cliente", "Valor", "Domicilio", "Servicio"};
        colWidthSal = new float[]{0.3f, 1.6f, 2.3f, 1.4f, 3, 1.5f, 1.8f, 1.75f, 1.75f, 1.75f};
        colAlignSal = new int[]{0, 0, 0, 0, 0, 0, 2, 2, 2, 2};

        modelo = new MyDefaultTableModel(colNamesSal, 0);
        tableReport.setModel(modelo);
        tableReport.setRowHeight(24);
        tableReport.getTableHeader().setReorderingAllowed(false);

        DefaultListSelectionModel selModel = new DefaultListSelectionModel();
        selModel.addListSelectionListener(this);

        tableReport.setSelectionModel(selModel);

        modelo.addTableModelListener(new TableModelListener() {

            @Override
            public void tableChanged(TableModelEvent e) {
//                sumColumn(7, lbInfTotal);
//                sumColumn(8, lbInfService);
//                sumColumn(9, lbInfDelivery);
            }
        });
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

        regCategory.setLabelText("Categoria");
        regCategory.setActionCommand(AC_SEL_CATEGORY);
        regCategory.addActionListener(this);
        regCategory.setText(new String[]{});
        regCategory.setFontCampo(new Font("Tahoma", 0, 14));
        regCategory.setVisible(false);

        regProduct.setLabelText("Producto");
        regProduct.setActionCommand(AC_SEL_PRODUCT);
        regProduct.addActionListener(this);
        regProduct.setText(new String[]{});
        regProduct.setFontCampo(new Font("Tahoma", 0, 14));
        regProduct.setVisible(false);

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

        regTipoReg.setVisible(false);

        Dimension dim = new Dimension(120, 35);
        lbInfTotal = new LabelInfo("Total", 0, dim);

        lbInfService = new LabelInfo("Servicio", 0, dim);
        lbInfService.setColor(Color.cyan);

        lbInfDelivery = new LabelInfo("Domicilio", 0, dim);
        lbInfDelivery.setColor(Color.YELLOW);

        pnBottonBar.setLayout(new BorderLayout());

        JPanel pnLabels = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnLabels.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        pnLabels.add(lbInfTotal);
        pnLabels.add(lbInfService);
        pnLabels.add(lbInfDelivery);
        labelStatus = new JLabel("");
        pnBottonBar.add(pnLabels, BorderLayout.EAST);
        pnBottonBar.add(labelStatus, BorderLayout.WEST);

        changeTipo();
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
        Calendar cal = Calendar.getInstance();
        datePick1.setDate(new Date());
        regDateP1.setVisible(true);
        regDateP2.setEnabled(false);
        regDateP2.setVisible(false);
        datePick1.setEnabled(false);

        setEndTime(cal);

        query = "(sale_date between '" + app.DF_SQL.format(datePick1.getDate()) + " 00:00:00' and '" + app.DF_SQL.format(datePick1.getDate()) + " 23:59:59')";
        makeTitle();
    }

    public void opcionOtroDia() {
        regDateP1.setVisible(true);
        regDateP1.setEnabled(true);
        regDateP2.setVisible(false);
        datePick1.setEnabled(true);
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

        setEndTime(cal);

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

        setEndTime(cal);

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

        setEndTime(cal);

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

        setEndTime(cal);

        query = "(sale_date between '" + app.DF_SQL.format(datePick1.getDate()) + " 00:00:00' and '" + app.DF_SQL.format(datePick2.getDate()) + " 23:59:59')";
    }

    public void setEndTime(Calendar cal) {
        cal.setTime(datePick2.getDate());
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        end = cal.getTime();
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

        setEndTime(cal);

        query = "(sale_date between '" + app.DF_SQL.format(datePick1.getDate()) + " 00:00:00' and '" + app.DF_SQL.format(datePick2.getDate()) + " 23:59:59')";
    }

    private void changeTipo() {
        String tipo = regTipo.getText();
        if (MyConstants.TIPO_REPORTE[1].equals(tipo)) {
            regCategory.setVisible(false);
            regProduct.setVisible(false);
            lbInfDelivery.setVisible(true);
            lbInfService.setVisible(true);

        } else if (MyConstants.TIPO_REPORTE[0].equals(tipo)) {
            regCategory.setVisible(true);
            regProduct.setVisible(true);
            lbInfDelivery.setVisible(false);
            lbInfService.setVisible(false);
        }
        makeTitle();
    }

    private void setupTable(String[] colNames, int[] colAlign, float[] colWidth) {
        int W = tableReport.getWidth();
        float SUM = com.rb.Utiles.sumarArray(colWidth);
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
        Calendar cal = Calendar.getInstance();

        cal.setTime(datePick1.getDate());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        start = cal.getTime();
        if ("RANGO".equals(regPeriodo.getText())) {
            setEndTime(cal);
            query = "(sale_date between '" + app.DF_SQL.format(datePick1.getDate()) + " 00:00:00' and '" + app.DF_SQL.format(datePick2.getDate()) + " 23:59:59')";
        } else if ("OTRO DIA".equals(regSelection.getText())) {
            cal.add(Calendar.SECOND, 86399); // Agregar un dia menos un segundo a la fecha
            end = cal.getTime();
            query = "(sale_date between '" + app.DF_SQL.format(datePick1.getDate()) + " 00:00:00' and '" + app.DF_SQL.format(datePick1.getDate()) + " 23:59:59')";
        }

        enableNav(true);

        setupNavegador();

        if (MyConstants.TIPO_REPORTE[1].equals(TIPO)) {
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
        } else if (MyConstants.TIPO_REPORTE[0].equals(TIPO)) {
            enableNav(false);
            ArrayList productSalesList = app.getControl().getProductsSales(start, end);
            setupTable(colNamesProd, colAlignProd, colWidthProd);

            populateTabla(productSalesList, TIPO, 1);
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
        tableReport.setRowSorter(null);
        lbInfTotal.setQuantity(0.0);
        lbInfDelivery.setQuantity(0.0);
        lbInfService.setQuantity(0.0);
        if (tipo.equals(MyConstants.TIPO_REPORTE[1])) {  // VENTAS Y COMPRAS
            modelo.setColumnIdentifiers(colNamesSal);

            //clear registers
            regCategory.setText(new String[]{});
            regProduct.setText(new String[]{});

            SwingWorker sw = new SwingWorker<Object, Object[]>() {
                @Override
                protected Object doInBackground() throws Exception {
                    for (int i = 0; i < list.size(); i++) {
                        Invoice inv = (Invoice) list.get(i);
                        double total = 0;

                        Invoice invoice = (Invoice) list.get(i);

                        tableReport.getColumnModel().getColumn(7).setCellRenderer(formatRenderer);
                        tableReport.getColumnModel().getColumn(8).setCellRenderer(formatRenderer);
                        tableReport.getColumnModel().getColumn(9).setCellRenderer(formatRenderer);
                        if (invoice.getStatus() != 1) {
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
                            publish(new Object[]{invoice.getValor().doubleValue(),
                                invoice.getValorDelivery().doubleValue(),
                                invoice.getValueService(),
                            });
                        }

                        modelo.setRowEditable(modelo.getRowCount() - 1, false);
                    }
                    return true;
                }

                @Override
                protected void process(List<Object[]> chunks) {
                    for (Object chunk : chunks) {
                        Object[] data = (Object[]) chunk;

                        lbInfTotal.setQuantity(lbInfTotal.getQuantity() + (Double.parseDouble(data[0].toString())));
                        lbInfDelivery.setQuantity(lbInfDelivery.getQuantity() + (Double.parseDouble(data[1].toString())));
                        lbInfService.setQuantity(lbInfService.getQuantity() + (Double.parseDouble(data[2].toString())));
                    };
                }               
                

                @Override
                protected void done() {
                    app.getGuiManager().setDefaultCursor();
                }
            };
            app.getGuiManager().setWaitCursor();
            tableReport.updateUI();
            sw.execute();

        } else if (tipo.equals(MyConstants.TIPO_REPORTE[0])) {
            modelo.setColumnIdentifiers(colNamesProd);
            HashSet catSet = new HashSet<>();
            HashSet prodSet = new HashSet<>();
            catSet.add(SEL_TODAS);
            prodSet.add(SEL_TODAS);

            SwingWorker sw1 = new SwingWorker() {
                double total = 0;

                @Override
                protected Object doInBackground() throws Exception {
                    tableReport.getColumnModel().getColumn(4).setCellRenderer(formatRenderer);
                    tableReport.getColumnModel().getColumn(5).setCellRenderer(formatRenderer);
                    for (int i = 0; i < list.size(); i++) {
                        Object[] row = (Object[]) list.get(i);
                        catSet.add(row[1]);
                        prodSet.add(row[2]);
                        total += Double.parseDouble(row[5].toString());
                        modelo.addRow(row);
                        modelo.setRowEditable(modelo.getRowCount() - 1, false);
                    }
                    return true;
                }

                @Override
                protected void done() {
                    app.getGuiManager().setDefaultCursor();
                    ArrayList sortedList = new ArrayList(catSet);
                    Collections.sort(sortedList);
                    regCategory.setText(sortedList.toArray());
                    sortedList = new ArrayList(prodSet);
                    Collections.sort(sortedList);
                    regProduct.setText(sortedList.toArray());
                    lbInfTotal.setQuantity(total);

                }
            };
            app.getGuiManager().setWaitCursor();
            tableReport.updateUI();
            sw1.execute();
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
        regDateP1 = new com.rb.gui.util.Registro(BoxLayout.Y_AXIS, "Fecha inicio", datePick1);
        datePick2 = new MyDatePickerImp(new Date(), true);
        regDateP2 = new com.rb.gui.util.Registro(BoxLayout.Y_AXIS,"Fecha final", datePick2);
        regCategory = new org.dz.Registro(BoxLayout.Y_AXIS, "TipoReg", new String[1]);
        regProduct = new org.dz.Registro(BoxLayout.Y_AXIS, "TipoReg", new String[1]);
        lbTitle = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableReport = new javax.swing.JTable();
        pnBottonBar = new javax.swing.JPanel();
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

        btConsult.setFont(new java.awt.Font("Ubuntu", 0, 12)); // NOI18N

        javax.swing.GroupLayout pnFiltersLayout = new javax.swing.GroupLayout(pnFilters);
        pnFilters.setLayout(pnFiltersLayout);
        pnFiltersLayout.setHorizontalGroup(
            pnFiltersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnFiltersLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(regTipo, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(regPeriodo, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(regSelection, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(regDateP1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(regDateP2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(regTipoReg, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(regCategory, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(regProduct, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 134, Short.MAX_VALUE)
                .addComponent(btConsult, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
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
                            .addComponent(regSelection, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(regCategory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(regProduct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.CENTER, pnFiltersLayout.createSequentialGroup()
                        .addComponent(btConsult)
                        .addGap(12, 12, 12))))
        );

        pnFiltersLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btConsult, regCategory, regDateP1, regDateP2, regPeriodo, regProduct, regSelection, regTipo, regTipoReg});

        lbTitle.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        tableReport.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(tableReport);

        pnBottonBar.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnBottonBar.setLayout(new javax.swing.BoxLayout(pnBottonBar, javax.swing.BoxLayout.LINE_AXIS));

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
            .addGroup(layout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnBottonBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnFilters, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(lbTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelNav, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(3, 3, 3))
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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnBottonBar, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
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
    private javax.swing.JPanel pnBottonBar;
    private javax.swing.JPanel pnFilters;
    private org.dz.Registro regCategory;
    private com.rb.gui.util.Registro regDateP1;
    private com.rb.gui.util.Registro regDateP2;
    private org.dz.Registro regPeriodo;
    private org.dz.Registro regProduct;
    private org.dz.Registro regSelection;
    private org.dz.Registro regTipo;
    private org.dz.Registro regTipoReg;
    private javax.swing.JTable tableReport;
    private javax.swing.JTextField tfBuscar;
    private javax.swing.JTextField tfFilas;
    // End of variables declaration//GEN-END:variables

    @Override
    public void valueChanged(ListSelectionEvent e) {
        int[] selectedRows = tableReport.getSelectedRows();
        double val = 0, dom = 0, serv = 0;

        String stTipo = regTipo.getText();
        int tipoReporte = 0;
        if (MyConstants.TIPO_REPORTE[1].equals(stTipo)) {
            tipoReporte = 0;
            for (int selectedRow : selectedRows) {
                double _val = Double.parseDouble(tableReport.getValueAt(selectedRow, 7).toString());
                double _dom = Double.parseDouble(tableReport.getValueAt(selectedRow, 8).toString());
                double _serv = Double.parseDouble(tableReport.getValueAt(selectedRow, 9).toString());
                val += _val;
                dom += _dom;
                serv += _serv;
            }
        } else if (MyConstants.TIPO_REPORTE[0].equals(stTipo)) {
            tipoReporte = 1;
            for (int selectedRow : selectedRows) {
                double _val = Double.parseDouble(tableReport.getValueAt(selectedRow, 4).toString());
                double _dom = Double.parseDouble(tableReport.getValueAt(selectedRow, 5).toString());
                val += _val;
                dom += _dom;
            }
        }
        makeStatusLabelSelecteds(tipoReporte, selectedRows.length, val, dom, serv);
    }

    private void makeStatusLabelSelecteds(int tipo, int rows, double val, double dom, double serv) {
        String colSelection = "#34f";
        if (tipo == 0 && rows > 0) {
            labelStatus.setText("<html>  <font color=" + colSelection + ">Selección</font> [<font color=blue>" + (rows)
                    + "</font> pedidos]  "
                    + "Total: <font color=green>" + app.DCFORM_P.format(val)
                    + "</font> - Servicio: <font color=blue>" + app.DCFORM_P.format(serv) + "</font>"
                    + " - Domicilios: <font color=blue>" + app.DCFORM_P.format(dom
                    ) + "</font></html>");
        } else if (tipo == 1 && rows > 0) {
            labelStatus.setText("<html>  <font color=" + colSelection + ">Selección</font> [<font color=blue>" + (rows)
                    + "</font> productos]  "
                    + "Total: <font color=green>" + app.DCFORM_P.format(val)
                    + "</font> - Cantidad: <font color=blue>" + app.DCFORM_P.format(dom)
                    + "</font></html>");
        } else {
            labelStatus.setText("");
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (MyDatePickerImp.DATE_CHANGED.equals(evt.getPropertyName())) {
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
        } else if (AC_SEL_CATEGORY.equals(e.getActionCommand())) {
            String cat = regCategory.getText();
            filtrar(cat, 1, MyConstants.FILTER_TEXT_INT_EQUALS);
            HashSet<String> prodSet = new HashSet<>();
            for (int row = 0; row < tableReport.getRowCount(); row++) {
                String prod = tableReport.getValueAt(row, 2).toString();
                prodSet.add(prod);
            }
            prodSet.add(SEL_TODAS);
            ArrayList sortedList = new ArrayList(prodSet);
            Collections.sort(sortedList);
            regProduct.setText(sortedList.toArray());

        } else if (AC_SEL_PRODUCT.equals(e.getActionCommand())) {
            String prod = regProduct.getText();
            filtrar(prod, 2, MyConstants.FILTER_TEXT_INT_EQUALS);
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

    public void enableNav(boolean enabled) {
        btUpdate.setEnabled(enabled);
        btFirstPage.setEnabled(enabled);
        btLastPage.setEnabled(enabled);
        btNextPage.setEnabled(enabled);
        btPrevPage.setEnabled(enabled);
        tfBuscar.setEnabled(enabled);
        tfFilas.setEnabled(enabled);
        lbFilas.setEnabled(enabled);
        lbBuscar.setEnabled(enabled);
        String color = enabled ? "black" : "gray";
        labelPaginas.setText("<html><font color='" + color + "'> . . . </font></html>");

    }

    private void sumColumn(int col, LabelInfo labInfo) {

        SwingWorker sw1 = new SwingWorker() {
            int rows = tableReport.getRowCount();
            double sum = 0, val = 0;

            @Override
            protected Object doInBackground() throws Exception {

                for (int r = 0; r < rows; r++) {
                    try {
                        val = Double.parseDouble(tableReport.getValueAt(r, col).toString());
                    } catch (NumberFormatException e) {
                        val = 0;
                    }
                    sum += val;

                }
                return true;
            }

            @Override
            protected void done() {
                labInfo.setQuantity(sum);
            }
        };
        sw1.execute();

    }

    public void filtrar(final String text, final int columna, final int tFilter) {

        if (SEL_TODAS.equals(text)) {
            tableReport.setRowSorter(null);
            if (columna == 2) {
                filtrar(regCategory.getText(), 1, MyConstants.FILTER_TEXT_INT_EQUALS);
            }
            return;
        }

        RowFilter<Object, Object> filterText = new RowFilter<Object, Object>() {
            @Override
            public boolean include(RowFilter.Entry entry) {
                if (text.equals("")) {
                    return true;
                }
                if (MyConstants.FILTER_TEXT_INT_START == tFilter) {
                    return entry.getStringValue(columna).startsWith(text);
                } else if (tFilter == MyConstants.FILTER_TEXT_INT_CONTAINS) {
                    return entry.getStringValue(columna).contains(text);
                } else {
                    return entry.getStringValue(columna).equals(text);
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
                } else if (tFilter == FILTER_NUM_INT_GREATER_EQUAL) {
                    return Integer.parseInt(entry.getStringValue(columna)) >= value;
                } else if (tFilter == FILTER_NUM_INT_LESS) {
                    return Integer.parseInt(entry.getStringValue(columna)) < value;
                } else if (tFilter == FILTER_NUM_INT_LESS_EQUAL) {
                    return Integer.parseInt(entry.getStringValue(columna)) <= value;
                } else if (tFilter == FILTER_NUM_INT_DIFFERENT) {
                    return Integer.parseInt(entry.getStringValue(columna)) != value;
                } else {
                    return Integer.parseInt(entry.getStringValue(columna)) == value;
                }
            }
        };

        TableRowSorter<MyDefaultTableModel> sorter = new TableRowSorter<>(modelo);
//        sorter.setComparator(3, new COmpara);
        if (tFilter <= 3) {
            sorter.setRowFilter(filterText);
        } else {
            sorter.setRowFilter(filterNum);
        }
        lastSorter = sorter;
//        sorter.addRowSorterListener(new RowSorterListener(){            
//            public void sorterChanged(RowSorterEvent e){
//                System.out.println(e);
//            }
//        });

        tableReport.setRowSorter(sorter);

        sumColumn(5, lbInfTotal);
    }

}
