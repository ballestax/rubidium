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
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import org.bx.gui.MyDefaultTableModel;
import org.dz.PanelCapturaMod;

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

    public static final String AC_SEL_TIPO_REGISTRO = "AC_SEL_TIPO_REGISTRO";
    public static final String AC_CHANGE_TIPO = "AC_CHANGE_TIPO";
    public static final String AC_SEL_PERIODO = "AC_SEL_PERIODO";
    public static final String AC_SEL_OPCION = "AC_SEL_OPCION";
    public static final String AC_DO_CONSULT = "AC_DO_CONSULT";
    public static final String AC_SEL_AÑOS = "AC_SEL_AÑOS";
    public static final String AC_SEL_MESES = "AC_SEL_MESES";
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

//        datePick1 = new MyDatePickerImp(new Date(), true);
//        datePick2 = new MyDatePickerImp(new Date(), true);
        colNamesProd = new String[]{"N°", "Producto", "Factura", "", "Locación", "Fecha", "Cantidad", "Valor"};
        colWidthProd = new float[]{0.8f, 3.2f, 1.4f, 3, 2, 1.5f, 1.4f, 1.7f};
        colAlignProd = new int[]{0, 0, 0, 0, 0, 1, 2, 2};

//        colNamesMov = new String[]{"N°", "Fecha", "Factura", "Cliente", "Tipo", " Valor", "Abono", "Vencimiento"};
//        colWidthMov = new float[]{0.8f, 1.5f, 1.5f, 3, 1.5f, 2, 2, 2};
//        colAlignMov = new int[]{0, 0, 0, 0, 0, 2, 2, 1};
        colNamesSal = new String[]{"N°", "Factura", "Fecha", "Estado", "Cliente", "Tipo", " Cliente", "Valor", "Domicilio", "Servicio"};
        colWidthSal = new float[]{0.3f, 1.6f, 2.3f, 1.4f, 3, 1.5f, 1.8f, 1.75f, 1.75f, 1.75f};
        colAlignSal = new int[]{0, 0, 0, 0, 0, 0, 2, 2, 2, 2};

        modelo = new MyDefaultTableModel(colNamesSal, 0);
        tableReport.setModel(modelo);
        tableReport.setRowHeight(22);
        tableReport.getColumnModel().getColumn(4).setCellRenderer(formatRenderer);

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
        System.out.println("Otrodia:" + datePick1.getDate());
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
        modelo.setColumnIdentifiers(colNames);
        for (int i = 0; i < colNames.length; i++) {
//            tabla.getColumnModel().getColumn(i).setHeaderValue(colNames[i]);
            tableReport.getColumnModel().getColumn(i).setCellRenderer(new TablaCellRenderer(true, DF_TABLE_FONT,
                    colAlign[i] == 0 ? SwingConstants.LEFT : colAlign[i] == 2 ? SwingConstants.RIGHT : SwingConstants.CENTER,
                    colAlign[i] == 2 ? app.getDCFORM_P() : null
            ));
            int widthCol = (int) (colWidth[i] / SUM * W);
            tableReport.getColumnModel().getColumn(i).setMinWidth(widthCol);
            tableReport.getColumnModel().getColumn(i).setPreferredWidth(widthCol);
        }
    }

    private void doConsult(String TIPO) {
        makeTitle();
        if ("RANGO".equals(regPeriodo.getText())) {
            query = "(sale_date between '" + app.DF_SQL.format(datePick1.getDate()) + " 00:00:00' and '" + app.DF_SQL.format(datePick2.getDate()) + " 23:59:59')";
        } else if ("OTRO DIA".equals(regSelection.getText())) {
            query = "(sale_date between '" + app.DF_SQL.format(datePick1.getDate()) + " 00:00:00' and '" + app.DF_SQL.format(datePick1.getDate()) + " 23:59:59')";
        }
        System.out.println(">>" + query);
        if (MyConstants.TIPO_REPORTE[0].equals(TIPO)) {
            //filtrar tipos de registros
            int sel = regTipoReg.getSelected();
            String locQuery = query;
            if (sel > 0) {
//                locQuery = query.isEmpty() ? "tipoRegistro=" + sel : query + " AND tipoRegistro=" + sel;
            }
            System.out.println("locquery>>" + locQuery);
            ArrayList<Invoice> salidaList = app.getControl().getInvoiceslList(locQuery, "sale_date");
            System.out.println(">>" + salidaList.size());
            setupTable(colNamesSal, colAlignSal, colWidthSal);
            populateTabla(salidaList, TIPO);
        } else if (MyConstants.TIPO_REPORTE[1].equals(TIPO)) {
            ArrayList productSalidaList = app.getControl().getInvoiceByProductListWhere(query, "sale_date");
            setupTable(colNamesProd, colAlignProd, colWidthProd);
            populateTabla(productSalidaList, TIPO);
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

    }

    public void makeTitle() {
        String tipo = regTipo.getText();
        String comp = app.DF_SL.format(datePick1.getDate()) + (query.contains(" AND ") ? " a " + app.DF_SL.format(datePick2.getDate()) : "");
        title = "REPORTE DE " + tipo + " :  " + comp;
        lbTitle.setText("<html><font color=#D52715 size=+1>REPORTE DE " + tipo + "</font>:<font size=+1> " + comp + "</font></html>");
    }

    private void populateTabla(ArrayList list, String tipo) {
        modelo.setRowCount(0);
        if (tipo.equals(MyConstants.TIPO_REPORTE[0]) || tipo.equals(MyConstants.TIPO_REPORTE[2])) {  // VENTAS Y COMPRAS
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
                            modelo.setColumnIdentifiers(colNamesSal);
                            tableReport.getColumnModel().getColumn(7).setCellRenderer(formatRenderer);
                            tableReport.getColumnModel().getColumn(8).setCellRenderer(formatRenderer);
                            tableReport.getColumnModel().getColumn(9).setCellRenderer(formatRenderer);
                            modelo.addRow(new Object[]{
                                modelo.getRowCount() + 1,
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
        } else if (tipo.equals(MyConstants.TIPO_REPORTE[3])) {
            colNamesProd[3] = "Proveedor";
            tableReport.getColumnModel().getColumn(5).setHeaderValue(colNamesProd[5]);
//            modelo.setColumnIdentifiers(colNamesProd);
//            tabla.getColumnModel().getColumn(3).setCellRenderer(formatRenderer);
//            tabla.getColumnModel().getColumn(4).setCellRenderer(formatRenderer);
            for (int i = 0; i < list.size(); i++) {
                Object[] row = (Object[]) list.get(i);
//                Producto prod = app.getControl().getProducto(row[7].toString());
//                Proveedor proveedor = app.getControl().getProveedor(Long.parseLong(row[5].toString()));
//                Location location = app.getControl().getLocation(Integer.parseInt(row[6].toString()));

//                modelo.addRow(new Object[]{modelo.getRowCount() + 1,
//                    prod.getNombre(), row[1],
//                    proveedor.getRazonSocial(), location.getNombre(), row[4], row[2], row[3]
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

        tableReport.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(tableReport);

        pnLabels.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnLabels.setLayout(new javax.swing.BoxLayout(pnLabels, javax.swing.BoxLayout.LINE_AXIS));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(pnLabels, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnFilters, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbTitle, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnFilters, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 211, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnLabels, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btConsult;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbTitle;
    private javax.swing.JPanel pnFilters;
    private javax.swing.JPanel pnLabels;
    private com.bacon.gui.util.Registro regDateP1;
    private com.bacon.gui.util.Registro regDateP2;
    private org.dz.Registro regPeriodo;
    private org.dz.Registro regSelection;
    private org.dz.Registro regTipo;
    private org.dz.Registro regTipoReg;
    private javax.swing.JTable tableReport;
    // End of variables declaration//GEN-END:variables

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (AC_DO_CONSULT.equals(e.getActionCommand())) {
            String tipo = regTipo.getText();
            doConsult(tipo);
        } else if (AC_CHANGE_TIPO.equals(e.getActionCommand())) {
            changeTipo();

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
        }
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
