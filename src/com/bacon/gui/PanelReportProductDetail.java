/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bacon.gui;


import com.bacon.Aplication;
import com.bacon.GUIManager;
import com.bacon.PDFGenerator;
import com.bacon.Utiles;
import com.bacon.domain.Conciliacion;
import com.bacon.domain.Item;
import com.bacon.domain.Location;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.bx.gui.MyDefaultTableModel;
import org.dz.PanelCapturaMod;
import org.dzur.gui.MyListModel;

/**
 *
 * @author LuisR
 */
public class PanelReportProductDetail extends PanelCapturaMod implements ActionListener {

    private static final Logger logger = Logger.getLogger(PanelReportProductDetail.class.getCanonicalName());

    private final Aplication app;
    private MyDefaultTableModel modeloTabla;
    private MyListModel modeloLista;
    private String nombreProd;
    private float[] colsWidth;
    private int[] colsALign;
    private boolean valido;
    private String codigoProd;
    private String HTMLPDF;

    /**
     * Creates new form PnProductDetail
     *
     * @param app
     */
    public PanelReportProductDetail(Aplication app) {
        this.app = app;
        initComponents();
        createComponent();
    }

    private void createComponent() {


        colsWidth = new float[]{1.4f, 1.4f, 1.4f, 3.0f, 2.5f, 1.2f, 1.5f, 1.5f};
        colsALign = new int[]{0, 0, 0, 0, 0, 2, 2, 2};
        String[] cols = {"Tipo", "Fecha", "Factura", "Proveedor/Cliente", "Locacion", "Cantidad", "Valor", "Total"};
        modeloTabla = new MyDefaultTableModel(cols, 0);
        tablaDetails.setModel(modeloTabla);
        tablaDetails.setRowHeight(24);
        FormatRenderer formatRenderer = new FormatRenderer(app.getDCFORM_P());
        tablaDetails.getColumnModel().getColumn(5).setCellRenderer(formatRenderer);
        tablaDetails.getColumnModel().getColumn(6).setCellRenderer(formatRenderer);
        tablaDetails.getColumnModel().getColumn(7).setCellRenderer(formatRenderer);
    }
    public static final String AC_CLEAR_BUSQUEDA = "AC_CLEAR_BUSQUEDA";
    public static final String AC_SEL_CATEGORIA = "AC_SEL_CATEGORIA";

    public void showInfoProduct(Item item) {

        if (item == null) {
            lbInfoProducto.setText("");
            modeloTabla.setRowCount(0);
            return;
        }
        Map<Integer, String> locMap = getLocationsMap();
        
        double cant = 0;
        double costoIni = 0;
        double cantIni = 0;
        String locs = "";
        if (item!=null) {
            cantIni = item.getInit();
            costoIni = item.getCost().doubleValue();
//            productInvList.get(0).getLocacion();
//            for (int i = 0; i < productInvList.size(); i++) {
//                cant += productInvList.get(i).getCantidad();
//                locs += locMap.get(productInvList.get(i).getLocacion());
//            }
        }

        Color colAl = org.dzur.Util.colorAleatorio(200, 255);
        String color = Utiles.toHex(colAl);

        codigoProd = String.valueOf(item.getId());
        nombreProd = item.getName();
        String cat = "INVENTARIO";

//        ArrayList<Object[]> entradaByProductList = app.getControl().getEntradaByProductList(item.getCodigo());
//        ArrayList<Object[]> salidaByProductList = app.getControl().getSalidaByProductList(item.getCodigo());
        ArrayList<Conciliacion> conciliacionList = app.getControl().getConciliacionList("idItem=" + item.getId()+ "", "fecha");
        modeloTabla.setRowCount(0);
        modeloTabla.addRow(new Object[]{"INICIAL", "   ----------", "   ----------", "   ----------", "   ----------", cantIni, costoIni, cantIni * costoIni});
        double entradas = 0;
//        for (int i = 0; i < entradaByProductList.size(); i++) {
//            Object[] row = entradaByProductList.get(i);
//            Proveedor prov = app.getControl().getProveedor(Long.parseLong(row[5].toString()));
//            double total = Double.parseDouble(row[2].toString()) * Double.parseDouble(row[3].toString());
//            entradas += Double.parseDouble(row[2].toString());
//            modeloTabla.addRow(new Object[]{"ENTRADA", app.DF.format((Date) row[4]), row[1], prov.getRazonSocial(), locMap.get(row[6]), row[2], row[3], total});
//            modeloTabla.setRowEditable(modeloTabla.getRowCount() - 1, false);
//        }
        double salidas = 0;
//        for (int i = 0; i < salidaByProductList.size(); i++) {
//            Object[] row = salidaByProductList.get(i);
//            Cliente clie = app.getControl().getCliente(Long.parseLong(row[5].toString()));
//            double total = Double.parseDouble(row[2].toString()) * Double.parseDouble(row[3].toString());
//            salidas += Double.parseDouble(row[2].toString());
//            modeloTabla.addRow(new Object[]{"SALIDA", app.DF.format((Date) row[4]), row[1], clie.getRazonSocial(), locMap.get(row[6]), row[2], row[3], total});
//            modeloTabla.setRowEditable(modeloTabla.getRowCount() - 1, false);
//        }
        double conciliaciones = 0;
        for (int i = 0; i < conciliacionList.size(); i++) {
            Conciliacion conc = conciliacionList.get(i);
            double dif = conc.getConciliacion()-conc.getExistencias();
            conciliaciones += dif;
            modeloTabla.addRow(new Object[]{"CONC.", app.DF.format(conc.getFecha()), conc.getCodigo(), "---", locMap.get(conc.getLocacion()), dif, 0, 0});
            modeloTabla.setRowEditable(modeloTabla.getRowCount() - 1, false);
        }

        StringBuilder strHtml = new StringBuilder();
        strHtml.append("<html><table width=\"100%\" border=\"1\" cellspacing=\"0\"><tr>");
        strHtml.append("<td class=\"label\">Nombre:</td><td colspan=\"13\" >").append(item.getName().toUpperCase()).append("</td></tr>");
        strHtml.append("<tr><td class=\"label\">Codigo:</td><td >").append(item.getId()).append("</td>");
        strHtml.append("<td class=\"label\">Categoria:</td><td >").append(cat).append("</td></tr>");
        strHtml.append("<tr><td class=\"label\" >Inicial:</td><td padding=5px >").append(app.DCFORM_P.format(item.getInit())).append("</td>");
        strHtml.append("<td class=\"label\" >Entradas:</td><td padding=5px >").append(app.DCFORM_P.format(entradas)).append("</td>");
        strHtml.append("<td class=\"label\" >Salidas:</td><td padding=5px >").append(app.DCFORM_P.format(salidas)).append("</td>");
        strHtml.append("<td class=\"label\" >Conc:</td><td padding=5px >").append(app.DCFORM_P.format(conciliaciones)).append("</td>");
        strHtml.append("<td class=\"label\" >Total:</td><td padding=5px >").append(app.DCFORM_P.format(cant)).append("</td>");
        strHtml.append("</tr></table></html>");
        HTMLPDF = strHtml.toString();

        StringBuilder str = new StringBuilder();
        str.append("<html><table><tr>");
        str.append("<td>Nombre:</td><td colspan=15 bgcolor=").append(color).append(">").append(item.getName().toUpperCase()).append("</td></tr>");
        str.append("<tr><td>Codigo:</td><td colspan=4 bgcolor=").append(color).append(">").append(item.getId()).append("</td><td>  </td>");
        str.append("<td>Categoria:</td><td colspan=4 bgcolor=").append(color).append(">").append(cat).append("</td><td>  </td></tr>");
        str.append("<tr><td bgcolor=#cec5d3>Inicial:</td><td bgcolor=").append(color).append(">").append(item.getInit()).append("</td><td>  </td>");
        str.append("<td bgcolor=#cec5d3>Entradas:</td><td bgcolor=").append(color).append(">").append(app.DCFORM_P.format(entradas)).append("</td><td>  </td>");
        str.append("<td bgcolor=#cec5d3>Salidas:</td><td bgcolor=").append(color).append(">").append(app.DCFORM_P.format(salidas)).append("</td><td>  </td>");
        str.append("<td bgcolor=#cec5d3>Conc:</td><td bgcolor=").append(color).append(">").append(app.DCFORM_P.format(conciliaciones)).append("</td><td>  </td>");
        str.append("<td bgcolor=#cec5d3>Total:</td><td bgcolor=").append(color).append(">").append(cant).append("</td><td>  </td>");

        str.append("</tr></table></html>");
        lbInfoProducto.setText(str.toString());

    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        
    }

    
    public Map<Integer, String> getLocationsMap() {
        ArrayList<Location> locations = app.getControl().getLocationList("", "");
        Map<Integer, String> mapLocations = new HashMap<>();
        for (int i = 0; i < locations.size(); i++) {
            Location get = locations.get(i);
            mapLocations.put(get.getId(), get.getName());
        }
        return mapLocations;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lbInfoProducto = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaDetails = new javax.swing.JTable();

        lbInfoProducto.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        tablaDetails.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(tablaDetails);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addComponent(lbInfoProducto, javax.swing.GroupLayout.DEFAULT_SIZE, 452, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 448, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbInfoProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 242, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbInfoProducto;
    private javax.swing.JTable tablaDetails;
    // End of variables declaration//GEN-END:variables

    

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("ACTION_BUTTON".equals(evt.getPropertyName())) {
            Path p = null;
            try {
                if (valido) {
                    Date date = new Date();
                    DateFormat DF = new SimpleDateFormat("ddMMyyyy_hhmm");
                    p = Paths.get(app.getDirDocuments(), "reporte_prod_" + codigoProd + "_" + DF.format(date) + ".pdf");
//                    System.err.println(p.toString());
                    PDFGenerator pdf = new PDFGenerator(app);
                    pdf.createDocument(p.toString(), modeloTabla, "Reporte Producto: " + nombreProd, HTMLPDF, colsWidth, colsALign);
                }
            } catch (Exception e) {
                logger.debug(e.getMessage(), e);
            }
            try {
                Desktop.getDesktop().open(p.toFile());
            } catch (Exception e) {
                GUIManager.showErrorMessage(null, "Error: " + e.getMessage(), "Error");
            }
        }
    }

}
