package com.bacon.gui;

import com.bacon.Aplication;
import com.bacon.MyConstants;
import com.bacon.domain.Invoice;
import com.bacon.domain.ProductoPed;
import com.bacon.gui.util.DatePickerImp;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.swing.SwingConstants;
import javax.xml.stream.Location;
import org.bx.gui.MyDefaultTableModel;
import org.dz.PanelCapturaMod;

/**
 *
 * @author lrod
 */
public class PanelReportSales extends PanelCapturaMod implements ActionListener{

    private final Aplication app;
    private FormatRenderer formatRenderer;
    private String[] colNamesProd;
    private float[] colWidthProd;
    private int[] colAlignProd;
    private String[] colNamesSal;
    private float[] colWidthSal;
    private int[] colAlignSal;
    private MyDefaultTableModel modelo;
    private DatePickerImp datePick1, datePick2;
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
    }
    
    private void createComponents() {
        formatRenderer = new FormatRenderer(app.getDCFORM_P());

        colNamesProd = new String[]{"N°", "Producto", "Factura", "", "Locación", "Fecha", "Cantidad", "Valor"};
        colWidthProd = new float[]{0.8f, 3.2f, 1.4f, 3, 2, 1.5f, 1.4f, 1.7f};
        colAlignProd = new int[]{0, 0, 0, 0, 0, 1, 2, 2};

//        colNamesMov = new String[]{"N°", "Fecha", "Factura", "Cliente", "Tipo", " Valor", "Abono", "Vencimiento"};
//        colWidthMov = new float[]{0.8f, 1.5f, 1.5f, 3, 1.5f, 2, 2, 2};
//        colAlignMov = new int[]{0, 0, 0, 0, 0, 2, 2, 1};

        colNamesSal = new String[]{"N°", "Fecha", "Tipo registro", "Factura", "Cliente", "Tipo", " Valor", "Abono", "Vencimiento"};
        colWidthSal = new float[]{0.8f, 1.6f, 2.3f, 1.4f, 3, 1.5f, 1.8f, 1.75f, 1.75f};
        colAlignSal = new int[]{0, 0, 0, 0, 0, 0, 2, 2, 1};

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
        regDate1.setVisible(true);
        regDate1.setEnabled(false);
        regDate1.setVisible(false);
        query = "fecha ='" + app.DF_SQL.format(datePick1.getDate()) + "'";
    }

    public void opcionOtroDia() {
        System.out.println("Otrodia:" + datePick1.getDate());
        regDate2.setVisible(true);
        regDate2.setEnabled(true);
        regDate2.setVisible(false);
        query = "fecha ='" + app.DF_SQL.format(datePick1.getDate()) + "'";
    }

    public void opcionEstaSemana() {
        regDate1.setVisible(true);
        regDate1.setEnabled(false);
        regDate2.setVisible(true);
        regDate2.setEnabled(false);

        Calendar cal = Calendar.getInstance();
        int dayOfTheWeek = cal.get(Calendar.DAY_OF_WEEK);
        cal.add(Calendar.DAY_OF_WEEK, Calendar.SUNDAY - dayOfTheWeek);
        datePick1.setDate(cal.getTime());
        cal.add(Calendar.DAY_OF_WEEK, +7);
        datePick2.setDate(cal.getTime());

        query = "fecha>'" + app.DF_SQL.format(datePick1.getDate()) + "' AND fecha<'" + app.DF_SQL.format(datePick2.getDate()) + "'";
    }

    public void opcionSemanaPasada() {
        regDate1.setVisible(true);
        regDate1.setEnabled(false);
        regDate2.setVisible(true);
        regDate2.setEnabled(false);

        Calendar cal = Calendar.getInstance();
        int dayOfTheWeek = cal.get(Calendar.DAY_OF_WEEK);
        cal.add(Calendar.DAY_OF_WEEK, Calendar.SUNDAY - dayOfTheWeek);
        datePick2.setDate(cal.getTime());
        cal.add(Calendar.DAY_OF_WEEK, -7);
        datePick1.setDate(cal.getTime());

        query = "fecha>'" + app.DF_SQL.format(datePick1.getDate()) + "' AND fecha<'" + app.DF_SQL.format(datePick2.getDate()) + "'";
    }

    private void opcionMeses(int mes) {
        regDate1.setVisible(true);
        regDate1.setEnabled(false);
        regDate2.setVisible(true);
        regDate2.setEnabled(false);
        regSelection.setSelected(mes);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, mes);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        datePick1.setDate(cal.getTime());
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        datePick2.setDate(cal.getTime());

        query = "fecha>'" + app.DF_SQL.format(datePick1.getDate()) + "' AND fecha<'" + app.DF_SQL.format(datePick2.getDate()) + "'";
    }

    private void opcionAños(int año) {
        regDate1.setVisible(true);
        regDate1.setEnabled(false);
        regDate2.setVisible(true);
        regDate2.setEnabled(false);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, año);
        cal.set(Calendar.DAY_OF_YEAR, cal.getActualMinimum(Calendar.DAY_OF_YEAR));
        datePick1.setDate(cal.getTime());
        cal.set(Calendar.DAY_OF_YEAR, cal.getActualMaximum(Calendar.DAY_OF_YEAR));
        datePick2.setDate(cal.getTime());

        query = "fecha>'" + app.DF_SQL.format(datePick1.getDate()) + "' AND fecha<'" + app.DF_SQL.format(datePick2.getDate()) + "'";
    }

    private void opcionRango() {
        regDate1.setVisible(true);
        regDate1.setEnabled(true);
        regDate2.setVisible(true);
        regDate2.setEnabled(true);

        Calendar cal = Calendar.getInstance();
        datePick2.setDate(cal.getTime());
        cal.add(Calendar.DATE, -15);
        datePick1.setDate(cal.getTime());
        if (datePick1.getDate().compareTo(datePick2.getDate()) > 0) {
//            GUIManager.showErrorMessage("La fecha incicial es mayor ", cal, query);
        }

        query = "fecha>'" + app.DF_SQL.format(datePick1.getDate()) + "' AND fecha<'" + app.DF_SQL.format(datePick2.getDate()) + "'";
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
        String comp = app.DF_SL.format(datePick1.getDate()) + (query.contains(" AND ") ? " a " + app.DF_SL.format(datePick2.getDate()) : "");
        title = "REPORTE DE " + TIPO + " :  " + comp;
        lbTitle.setText("<html><font color=#D52715 size=+1>REPORTE DE " + TIPO + "</font>:<font size=+1> " + comp + "</font></html>");
        if ("RANGO".equals(regPeriodo.getText())) {
            query = "fecha>'" + app.DF_SQL.format(datePick1.getDate()) + "' AND fecha<'" + app.DF_SQL.format(datePick2.getDate()) + "'";
        } else if ("OTRO DIA".equals(regSelection.getText())) {
            query = "fecha ='" + app.DF_SQL.format(datePick1.getDate()) + "'";
        }
        if (MyConstants.TIPO_REPORTE[0].equals(TIPO)) {
            //filtrar tipos de registros
            int sel = regTipoReg.getSelected();
            String locQuery = query;
            if (sel > 0) {
                locQuery = query.isEmpty() ? "tipoRegistro=" + sel : query + " AND tipoRegistro=" + sel;
            }
            ArrayList<Invoice> salidaList = app.getControl().getInvoiceslList(locQuery, "fecha");
            setupTable(colNamesSal, colAlignSal, colWidthSal);
            populateTabla(salidaList, TIPO);
        } else if (MyConstants.TIPO_REPORTE[1].equals(TIPO)) {
            ArrayList productSalidaList = app.getControl().getInvoiceByProductListWhere(query, "fecha");
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

    private void populateTabla(ArrayList list, String tipo) {
        modelo.setRowCount(0);
        if (tipo.equals(MyConstants.TIPO_REPORTE[0]) || tipo.equals(MyConstants.TIPO_REPORTE[2])) {  // VENTAS Y COMPRAS
//            modelo.setColumnIdentifiers(colNamesMov);
//            tabla.getColumnModel().getColumn(5).setCellRenderer(formatRenderer);
//            tabla.getColumnModel().getColumn(6).setCellRenderer(formatRenderer);
            for (int i = 0; i < list.size(); i++) {
                Invoice inv = (Invoice) list.get(i);
                double total = 0;
                if (tipo.equals(MyConstants.TIPO_REPORTE[0])) {  //VENTAS
                    List<ProductoPed> products = inv.getProducts();
                    for (int j = 0; j < products.size(); j++) {
                        ProductoPed get = products.get(j);
                        total += get.getPrecio() * get.getCantidad();
                    }
                    Invoice invoice = (Invoice) list.get(i);
//                    modelo.setColumnIdentifiers(colNamesSal);
//                    tabla.getColumnModel().getColumn(6).setCellRenderer(formatRenderer);
//                    tabla.getColumnModel().getColumn(7).setCellRenderer(formatRenderer);
//                    modelo.addRow(new Object[]{modelo.getRowCount() + 1, invoice.getFecha(),
//                        invoice.getTipoRegistro() == Salida.FACTURA_DE_VENTA ? MyConstants.SALIDA_FACTURA : MyConstants.SALIDA_NORMAL,
//                        invoice.getFactura(), invoice.getPersona(),
//                        (invoice.getTipo() == 1 ? MyConstants.ST_CONTADO : MyConstants.ST_CREDITO).toUpperCase(),
//                        total,
//                        invoice.getTipo() == 1 ? "-" : invoice.getAbono(),
//                        invoice.getTipo() == 1 ? "-" : invoice.getFechaVencimiento()
//                    });

                } else if (tipo.equals(MyConstants.TIPO_REPORTE[2])) { //COMPRAS
//                    ArrayList<ProductoEntrada> productos = ((Entrada) inv).getProductos();
//                    for (int j = 0; j < productos.size(); j++) {
//                        ProductoEntrada get = productos.get(j);
//                        total += get.getPrecio().doubleValue() * get.getCantidad();
//                    }
//                    modelo.addRow(new Object[]{modelo.getRowCount() + 1, inv.getFecha(),
//                        inv.getFactura(), inv.getPersona(),
//                        (inv.getTipo() == 1 ? MyConstants.ST_CONTADO : MyConstants.ST_CREDITO).toUpperCase(),
//                        total,
//                        inv.getTipo() == 1 ? "-" : inv.getAbono(),
//                        inv.getTipo() == 1 ? "-" : inv.getFechaVencimiento()
//                    });
                }

//                modelo.addRow(new Object[]{modelo.getRowCount() + 1, mov.getFecha(),
//                    mov.getFactura(), mov.getPersona(),
//                    (mov.getTipo() == 1 ? MyConstants.ST_CONTADO : MyConstants.ST_CREDITO).toUpperCase(),
//                    total,
//                    mov.getTipo() == 1 ? "-" : mov.getAbono(),
//                    mov.getTipo() == 1 ? "-" : mov.getFechaVencimiento()
//                });
                modelo.setRowEditable(modelo.getRowCount() - 1, false);
//            modelo.setCellEditable(modelo.getRowCount() - 1, modelo.getColumnCount() - 1, true);
            }
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
        regSelection = new org.dz.Registro();
        regPeriodo = new org.dz.Registro();
        regDate1 = new org.dz.Registro();
        regDate2 = new org.dz.Registro();
        regTipo = new org.dz.Registro();
        btConsult = new javax.swing.JButton();
        regTipoReg = new org.dz.Registro();
        lbTitle = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableReport = new javax.swing.JTable();

        pnFilters.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btConsult.setText("jButton1");

        javax.swing.GroupLayout pnFiltersLayout = new javax.swing.GroupLayout(pnFilters);
        pnFilters.setLayout(pnFiltersLayout);
        pnFiltersLayout.setHorizontalGroup(
            pnFiltersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnFiltersLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(regSelection, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(regPeriodo, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(regDate1, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(regDate2, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(regTipo, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(regTipoReg, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 86, Short.MAX_VALUE)
                .addComponent(btConsult)
                .addContainerGap())
        );
        pnFiltersLayout.setVerticalGroup(
            pnFiltersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnFiltersLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnFiltersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(regTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(regDate2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(regPeriodo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(regDate1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btConsult)
                    .addComponent(regTipoReg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnFiltersLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(regSelection, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnFiltersLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btConsult, regDate1, regDate2, regPeriodo, regSelection, regTipo, regTipoReg});

        tableReport.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(tableReport);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnFilters, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1))
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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btConsult;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbTitle;
    private javax.swing.JPanel pnFilters;
    private org.dz.Registro regDate1;
    private org.dz.Registro regDate2;
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
