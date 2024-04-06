/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rb.gui;

import com.rb.Aplication;
import com.rb.GUIManager;
import com.rb.PDFGenerator;
import com.rb.Utiles;
import com.rb.domain.Conciliacion;
import com.rb.domain.InventoryEvent;
import com.rb.domain.Item;
import com.rb.domain.Location;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import static javax.swing.BorderFactory.createLineBorder;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;

import org.apache.log4j.Logger;
import org.dz.MyDefaultTableModel;
import org.dz.MyListModel;
import org.dz.PanelCapturaMod;


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
    private int[] colsWidthInt;
    private int[] colsALign;
    private boolean valido;
    private String codigoProd;
    private String HTMLPDF;

    public enum Status {
        INICIAL, VENTA, ENTRADA, SALIDA, CONC
    };
    EnumMap<Status, Color> statusMap = new EnumMap<Status, Color>(Status.class);

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
        statusMap.put(Status.INICIAL, Color.black);
        statusMap.put(Status.VENTA, Color.blue);
        statusMap.put(Status.ENTRADA, Color.green.darker());
        statusMap.put(Status.SALIDA, Color.red);
        statusMap.put(Status.CONC, Color.magenta.darker());

        colsWidth = new float[]{1.5f, 2f, 3.0f, 1.0f};
        colsWidthInt = new int[]{20, 50, 200, 30};
        colsALign = new int[]{0, 0, 0, 2};
        String[] cols = {"Tipo", "Fecha", "Codigo", "Cantidad"};
        modeloTabla = new MyDefaultTableModel(cols, 0);
        tablaDetails.setModel(modeloTabla);
        tablaDetails.setRowHeight(24);
        FormatRenderer formatRenderer = new FormatRenderer(app.getDCFORM_P());

        TablaCellRenderer tRenderer = new TablaCellRenderer(true, app.getDCFORM_P());
        for (int i = 0; i < cols.length; i++) {
            tablaDetails.getColumnModel().getColumn(i).setMinWidth(colsWidthInt[i]);
            tablaDetails.getColumnModel().getColumn(i).setPreferredWidth(colsWidthInt[i]);
            tablaDetails.getColumnModel().getColumn(i).setCellRenderer(new TablaCellRenderer(true, null));
        }
        tablaDetails.getColumnModel().getColumn(3).setCellRenderer(tRenderer);
//        tablaDetails.getColumnModel().getColumn(5).setCellRenderer(formatRenderer);
//        tablaDetails.getColumnModel().getColumn(6).setCellRenderer(formatRenderer);
    }
    public static final String AC_CLEAR_BUSQUEDA = "AC_CLEAR_BUSQUEDA";
    public static final String AC_SEL_CATEGORIA = "AC_SEL_CATEGORIA";

    public void showInfoProduct(Item item) {
        modeloTabla.setRowCount(0);
        if (item == null) {
            lbInfoProducto.setText("");
            return;
        }
        Map<Integer, String> locMap = getLocationsMap();

        double cant = 0;
        double costoIni = 0;
        double cantIni = 0;
        String locs = "";
        if (item != null) {
            cantIni = item.getInit();
            costoIni = item.getCost().doubleValue();
//            productInvList.get(0).getLocacion();
//            for (int i = 0; i < productInvList.size(); i++) {
//                cant += productInvList.get(i).getCantidad();
//                locs += locMap.get(productInvList.get(i).getLocacion());
//            }
        }

        Color colAl = org.dz.Utiles.colorAleatorio(200, 255);
        String color = Utiles.toHex(colAl);

        codigoProd = String.valueOf(item.getId());
        nombreProd = item.getName();
        String cat = "INVENTARIO";

//        ArrayList<Object[]> entradaByProductList = app.getControl().getEntradaByProductList(item.getCodigo());
        String queryDate = " AND (lastUpdatedTime between date_sub(now(),INTERVAL 5 DAY) and now())";
        ArrayList<InventoryEvent> eventInList = app.getControl().getInventoryRegisterList("idItem=" + item.getId(), "lastUpdatedTime DESC");
        ArrayList<Conciliacion> conciliacionList = app.getControl().getConciliacionList("idItem=" + item.getId() + "", "fecha DESC");
        ArrayList<Object[]> presentationsByItem = app.getControl().getPresentationsByItem(item.getId());

        Color colorLinks = presentationsByItem.size() > 0 ? Color.blue : Color.red;

        modeloTabla.setRowCount(0);
        modeloTabla.addRow(new Object[]{"INICIAL", "   ----------", "   ----------", cantIni, costoIni, cantIni * costoIni});
        double entradas = 0;
        double salidas = 0;

        for (int i = 0; i < presentationsByItem.size(); i++) {
            Object[] get = presentationsByItem.get(i);
            int idPres = Integer.parseInt(get[0].toString());
            int idProd = Integer.parseInt(get[1].toString());
//            System.out.println(idPres + "  - " + idProd);
            if (idPres == 0) {
                ArrayList<Object[]> outProd = app.getControl().getProductsOutInventoryList(idProd, item.getId(), item.getCreatedTime());
                for (int j = 0; j < outProd.size(); j++) {
                    Object[] data = outProd.get(j);
                    double quantity = Double.parseDouble(data[2].toString());
                    modeloTabla.addRow(new Object[]{"VENTA", app.DF_FULL2.format(item.getCreatedTime()), data[1].toString().toUpperCase(),
                        quantity});
                    salidas += quantity;
                }
            } else {
                ArrayList<Object[]> outPres = app.getControl().getPresentationsOutInventoryList(idPres, item.getId(), item.getCreatedTime());
                for (int j = 0; j < outPres.size(); j++) {
                    Object[] data = outPres.get(j);
                    double quantity = Double.parseDouble(data[3].toString());
                    String name = (data[1].toString() + " <" + data[2].toString() + ">").toUpperCase();
                    modeloTabla.addRow(new Object[]{"VENTA", app.DF_FULL2.format(item.getCreatedTime()), name,
                        quantity});
                    salidas += quantity;
                }
            }
            modeloTabla.setRowEditable(modeloTabla.getRowCount() - 1, false);
        }

        for (int i = 0; i < eventInList.size(); i++) {
            InventoryEvent event = eventInList.get(i);
            if (event.getEvent() == InventoryEvent.EVENT_IN) {
                entradas += event.getQuantity();
                String code = "E" + Utiles.getNumeroFormateado((int) event.getId(), 5);
                modeloTabla.addRow(new Object[]{"ENTRADA", app.DF_FULL2.format(event.getLastUpdate()), code, event.getQuantity(), 0, 0});
            } else {
                salidas += event.getQuantity();
                String code = "S" + Utiles.getNumeroFormateado((int) event.getId(), 5);
                modeloTabla.addRow(new Object[]{"SALIDA", app.DF_FULL2.format(event.getLastUpdate()), code, event.getQuantity(), 0, 0});
            }
            modeloTabla.setRowEditable(modeloTabla.getRowCount() - 1, false);
        }

//        for (int i = 0; i < eventInList.size(); i++) {
//            InventoryEvent event = eventInList.get(i);
//            entradas += event.getQuantity();
//            modeloTabla.addRow(new Object[]{"ENTRADA", event.getLastUpdate(), event.getId(), "---", event.getQuantity(), 0, 0});
//            modeloTabla.setRowEditable(modeloTabla.getRowCount() - 1, false);
//        }
        double conciliaciones = 0;
        for (int i = 0; i < conciliacionList.size(); i++) {
            Conciliacion conc = conciliacionList.get(i);
            double dif = conc.getConciliacion() - conc.getExistencias();
            conciliaciones += dif;
            modeloTabla.addRow(new Object[]{"CONC", app.DF_FULL2.format(conc.getFecha()), conc.getCodigo(), locMap.get(conc.getLocacion()), dif, 0, 0});
            modeloTabla.setRowEditable(modeloTabla.getRowCount() - 1, false);
        }

        cant = item.getInit() + entradas - salidas + conciliaciones;

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
        str.append("<td>Nombre:</td><td colspan=15 bgcolor=").append(color).append(">").append(item.getName().toUpperCase()).append("</td>");
        str.append("<td border:1px ").append(Utiles.toHex(colorLinks)).append(" solid rowspan=3 bgcolor=").append(color).append("><font color=").append(Utiles.toHex(colorLinks)).append(" size=+1>")
                .append(presentationsByItem.size())
                .append("</font><br><font color=").append(Utiles.toHex(colorLinks)).append("> enlaces.</font>").append("</td></tr>");
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

    public class TablaCellRenderer extends JLabel implements TableCellRenderer {

        boolean isBordered = true;
        private String tipo;
        private int status;

        private final Format formatter;
        private final Color ORANGE = new Color(244, 145, 0);

        public TablaCellRenderer(boolean isBordered, Format formatter) {
            super();
            this.isBordered = isBordered;
            this.formatter = formatter;
//            setFont(new Font("tahoma", 0, 14));
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            int r = table.convertRowIndexToModel(row);
            int col = 0;
            if (value != null) {
                if (formatter != null) {
                    try {
                        setHorizontalAlignment(SwingConstants.RIGHT);
                        value = formatter.format(value);
                    } catch (IllegalArgumentException e) {
                    }
                }
                setText(value.toString().toUpperCase());

                try {
                    tipo = modeloTabla.getValueAt(r, col).toString();
                } catch (Exception e) {
                }
            }
            if (isSelected) {
                setForeground(statusMap.get(Status.valueOf(tipo)));
                setBackground(tablaDetails.getSelectionBackground());
                if (hasFocus) {
                    setBorder(BorderFactory.createLineBorder(Color.darkGray));
                } else {
                    setBorder(createLineBorder(Color.lightGray));
                }
            } else {
                setBackground(tablaDetails.getBackground());
                setForeground(statusMap.get(Status.valueOf(tipo)));
//                setBackground(agotada || warning ? getForeground().brighter().brighter().brighter() : tableItems.getBackground());
                setBorder(UIManager.getBorder("Table.cellBorder"));
            }
            return this;
        }
    }

}
