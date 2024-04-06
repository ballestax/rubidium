/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rb.gui;

import com.rb.Aplication;
import com.rb.GUIManager;
import com.rb.Utiles;
import com.rb.domain.Conciliacion;
import com.rb.domain.Item;
import com.rb.domain.Location;
import com.rb.gui.util.DatePickerImp;
import com.rb.gui.util.MyDatePickerImp;
import com.rb.persistence.JDBC.JDBCDAOFactory;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import org.apache.log4j.Logger;
import org.dz.MyDefaultTableModel;
import org.dz.PanelCapturaMod;
import org.dz.TextFormatter;

/**
 *
 * @author LuisR
 */
public class PanelNewConciliacion extends PanelCapturaMod implements ActionListener, CaretListener {

    private final Aplication app;
    private JPopupMenu popResultados;
    private MyDefaultTableModel modelo;
    private Item itemSelect;
    private int ajusteRegistros;
    private String codigo;
    private MyDatePickerImp datePick1;

    private Logger logger = Logger.getLogger(PanelNewConciliacion.class.getCanonicalName());

    /**
     * Creates new form PnNewConciliacion
     *
     * @param app
     */
    public PanelNewConciliacion(Aplication app) {
        this.app = app;
        initComponents();
        createComponentes();
    }

    private void createComponentes() {

        datePick1.addPropertyChangeListener(this);

        btBuscar.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                buscar();
            }
        });

        tfBuscar.addActionListener(this);
        popResultados = new JPopupMenu();

        regFecha.addPropertyChangeListener(this);

//        modelo = new MyDefaultTableModel(new String[]{"Locacion", "Cantidad", "Real"}, 0);
//        tabla.setFont(new Font("tahoma", 0, 16));
//        tabla.setRowHeight(23);
//        tabla.setModel(modelo);
//        FormatRenderer formatRenderer = new FormatRenderer(app.DCFORM_P);
//        tabla.getColumnModel().getColumn(1).setCellRenderer(formatRenderer);
//        tabla.getColumnModel().getColumn(2).setCellRenderer(formatRenderer);
//        int[] colW = {100, 20, 20};
//        for (int i = 0; i < colW.length; i++) {
//            tabla.getColumnModel().getColumn(i).setMinWidth(colW[i]);
//            tabla.getColumnModel().getColumn(i).setPreferredWidth(colW[i]);
//        }
//
        cbLocacion.setModel(new DefaultComboBoxModel<>(app.getControl().getLocationList("", "name").toArray(new Location[0])));
        cbLocacion.setActionCommand(AC_SELECT_LOCATION);
        cbLocacion.addActionListener(this);
        cbLocacion.setSelectedIndex(-1);
        
        tfCantidadInv.setEditable(false);
        tfCantidadReal.setDocument(TextFormatter.getIntegerLimiter());
        tfCantidadReal.addCaretListener(this);

        Font f = new Font("Tahoma", 0, 14);
        tfCantidadInv.setFont(f);
        tfCantidadReal.setFont(f);

        btCancel.setText("Cancelar");
        btCancel.setActionCommand(Aplication.ACTION_CANCEL_PANEL);
        btCancel.addActionListener(this);

        btAceptar.setText("Guardar");
        btAceptar.setActionCommand(ACTION_SAVE_CONCILIACION);
        btAceptar.addActionListener(this);

//        tabla.setTableHeader(null);
    }
    public static final String AC_SELECT_LOCATION = "AC_SELECT_LOCATION";
    public static final String ACTION_SAVE_CONCILIACION = "ACTION_SAVE_CONCILIACION";

    public Map<Integer, String> getLocationsMap() {
        ArrayList<Location> locations = app.getControl().getLocationList("", "");
        Map<Integer, String> mapLocations = new HashMap<>();
        for (int i = 0; i < locations.size(); i++) {
            Location get = locations.get(i);
            mapLocations.put(get.getId(), get.getName());
        }
        return mapLocations;
    }
    
    private void buscar() {
        try {
            if (tfBuscar.getText() != null) {
                String nombre = tfBuscar.getText();
                buscar(nombre);
            }
        } catch (Exception ex) {
            lbInfo.setText("<html><font color=red><p>Error consultando la base de datos.</p></font></html>");
            itemSelect = null;
        }

    }

    public void buscar(String busqueda) {
//        setBuscando(true);
        String[] split = busqueda.split(" ");

        String ESCAPE = "\\\\";
//        ESCAPE = " LIKE '%\\\\" + busqueda + "%' ESCAPE '\\\\' ";
        ESCAPE = " LIKE '%" + busqueda + "%'";

        StringBuilder sql = new StringBuilder();
//        sql.append("SELECT * FROM inventory WHERE name ");
        sql.append("name");
        sql.append(ESCAPE);
        String order = "name";

        ArrayList<Item> itemList = app.getControl().getItemList(sql.toString(), order);
        int size = itemList.size();
        Iterator<Item> it = itemList.iterator();
        popResultados.removeAll();
        try {
            int c = 0;
            int LIM = 20;
            while (it.hasNext() && c < LIM) {
                Item item = it.next();
                String name = item.getName();
                long idItem = item.getId();
                String stPers = "<html><p>" + name.toUpperCase() + "</p></html>";
                stPers = stPers.replace(busqueda.toUpperCase(), "<font color=blue>" + busqueda.toUpperCase() + "</font>");

                JMenuItem menuItem = new JMenuItem(stPers);
                menuItem.setActionCommand("ITEM:" + item.getId());
                menuItem.addActionListener(this);
                popResultados.add(menuItem);
                c++;
            }
            if (c == 0) {
                JMenuItem item = new JMenuItem("No encontrado");
                item.setForeground(Color.red);
                tfBuscar.setForeground(Color.red);
                popResultados.add(item);
            } else if (size > LIM) {
                int dif = size - LIM;
                JMenuItem item = new JMenuItem("<html><font color=orange><p>Sea mas especificoe en la busqueda</p>"
                        + "<p>+" + dif + " registros encontrados.</p></font></html>");
                item.setForeground(Color.orange);
                tfBuscar.setForeground(Color.orange);
                popResultados.add(item);
            }
            popResultados.show(tfBuscar, 0, tfBuscar.getHeight());

        } catch (Exception e) {
        }
    }

    private void buscarItem(long idItem) {
        Item item = app.getControl().getItemWhere("id=" + idItem);
        if (item != null) {
            lbInfo.setText(getInfoProducto(item));
            itemSelect = item;
            resetDatos();
        } else {
            lbInfo.setText("");
            itemSelect = null;
        }
    }

    private void guardarConciliacion() {
        Conciliacion conciliacion = getConciliacion();
        if (conciliacion != null) {
            try {
                JDBCDAOFactory.getInstance().getConciliacionDAO().addConciliacion(conciliacion);
                reset();
                cancelPanel();
                pcs.firePropertyChange(ACTION_SAVE_CONCILIACION, null, conciliacion);
            } catch (Exception e) {
                GUIManager.showErrorMessage(this, "Error guardando la tranferencia. " + e.getMessage(), "Error");
                logger.debug(e.getMessage(), e);
            }
        }
    }

    private Conciliacion getConciliacion() {
        Conciliacion conc = null;
        boolean valido = true;
        if (datePick1.getText().trim().isEmpty()) {
            regFecha.setBorder(bordeError);
            valido = false;
        }
        if (cbLocacion.getSelectedIndex() < 0) {
            cbLocacion.setBorder(bordeError);
            valido = false;
        }
        if (tfCantidadReal.getText().trim().isEmpty()) {
            tfCantidadReal.setBorder(bordeError);
            valido = false;
        }
        if (itemSelect == null) {
            GUIManager.showErrorMessage(null, "No ha seleccionado un producto", "Error");
            valido = false;
        }
        try {
            double inv = Double.parseDouble(tfCantidadInv.getText());
            double real = Double.parseDouble(tfCantidadReal.getText());
            if (inv == real) {
                GUIManager.showErrorMessage(null, "El valor en inventario y el real es el mismo", "Error");
                valido = false;
            }
        } catch (Exception e) {
        }

        if (valido) {
            conc = new Conciliacion();
            conc.setCodigo(codigo);
            conc.setFecha(datePick1.getDate());
            int idOrig = 1; //((Location) cbLocacion.getSelectedItem()).getId();
            conc.setLocacion(idOrig);
            conc.setNota(taNota.getText());
            conc.setIdItem(itemSelect.getId());
            conc.setExistencias(Double.parseDouble(tfCantidadInv.getText()));
            conc.setConciliacion(Double.parseDouble(tfCantidadReal.getText()));
            conc.setUser(app.getUser().getUsername());
        }
        return conc;
    }

    private String getInfoProducto(Item item) {
//        Map<Integer, String> maps = getLocationsMap();
        StringBuilder str = new StringBuilder();
        str.append("<html>");
        str.append("<table><tr>");
        str.append("<td>Codigo:</td><td color=blue font-size=16>").append(item.getId()).append("</td></tr>");
        str.append("<tr><td>Producto:</td><td color=blue font-size=16>").append(item.getName()).append("</td></tr></table>");

        ArrayList<Location> list = app.getControl().getLocationList("", "");
//        ArrayList<Object[]> list = app.getControl().getCantidadProductoList("productos_inventario.producto='" + prod.getCodigo() + "'", "");
//        Object[] loc1 = new Object[]{"Locacion", item.getQuantity()};
        String html = "<table><tr>";
        for (int i = 0; i < list.size(); i++) {
            Location loc1 = list.get(i);
            double quantity = item.getQuantity();
//            Object[] get = list.get(i);
            String color = "#C6ECBC";
            html += "<td bgcolor=" + color + ">";
            html += loc1.getName() + ":</td><td bgcolor=" + color + "><font color=" + "red" + " size=+1>" + quantity + " </font>";
            html += "</td><td> </td>";

        }
        html += "</tr></table></html>";
        str.append(html);
        return str.toString();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        tfBuscar = new javax.swing.JTextField();
        btBuscar = new javax.swing.JButton();
        lbInfo = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        taNota = new javax.swing.JTextArea();
        btCancel = new javax.swing.JButton();
        btAceptar = new javax.swing.JButton();
        lbInfoConciliacion = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        tfCantidadInv = new javax.swing.JTextField();
        cbLocacion = new javax.swing.JComboBox<>();
        tfCantidadReal = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        datePick1 = new MyDatePickerImp(new Date(), true);
        regFecha = new org.dz.Registro(BoxLayout.X_AXIS, "", datePick1);

        jLabel1.setText("Producto:");

        tfBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfBuscarActionPerformed(evt);
            }
        });

        btBuscar.setText("Buscar");

        lbInfo.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel3.setText("Fecha:");

        taNota.setColumns(20);
        taNota.setRows(5);
        taNota.setBorder(javax.swing.BorderFactory.createTitledBorder("Nota"));
        jScrollPane1.setViewportView(taNota);

        btCancel.setText("jButton1");

        btAceptar.setText("jButton2");

        lbInfoConciliacion.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel4.setText("Locacion:");

        jLabel5.setText("Inventario:");

        jLabel6.setText("Real:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tfBuscar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btAceptar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btCancel))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cbLocacion, 0, 140, Short.MAX_VALUE)
                            .addComponent(tfCantidadInv)
                            .addComponent(tfCantidadReal)
                            .addComponent(regFecha, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addComponent(lbInfoConciliacion, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel3, jLabel4, jLabel5, jLabel6});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(tfBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jLabel3)
                            .addComponent(regFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jLabel4)
                            .addComponent(cbLocacion, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jLabel5)
                            .addComponent(tfCantidadInv, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tfCantidadReal, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6))
                        .addGap(0, 11, Short.MAX_VALUE))
                    .addComponent(lbInfoConciliacion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btAceptar)
                    .addComponent(btCancel))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {cbLocacion, regFecha});

    }// </editor-fold>//GEN-END:initComponents

    private void tfBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfBuscarActionPerformed
        buscar();
    }//GEN-LAST:event_tfBuscarActionPerformed

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (DatePickerImp.DATE_CHANGED.equals(evt.getPropertyName())) {
            showInfoConciliacion();
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btAceptar;
    private javax.swing.JButton btBuscar;
    private javax.swing.JButton btCancel;
    private javax.swing.JComboBox<Location> cbLocacion;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbInfo;
    private javax.swing.JLabel lbInfoConciliacion;
    private org.dz.Registro regFecha;
    private javax.swing.JTextArea taNota;
    private javax.swing.JTextField tfBuscar;
    private javax.swing.JTextField tfCantidadInv;
    private javax.swing.JTextField tfCantidadReal;
    // End of variables declaration//GEN-END:variables

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().startsWith("ITEM:")) {
            long codigo = Long.parseLong(e.getActionCommand().substring(5));
            buscarItem(codigo);
        } else if (Aplication.ACTION_CANCEL_PANEL.equals(e.getActionCommand())) {
            cancelPanel();
        } else if (ACTION_SAVE_CONCILIACION.equals(e.getActionCommand())) {
            guardarConciliacion();
        }
        else if (AC_SELECT_LOCATION.equals(e.getActionCommand())) {
            if (itemSelect != null && cbLocacion.getSelectedIndex() >= 0) {
                Location loc = (Location) cbLocacion.getSelectedItem();
                Item item = app.getControl().getItemWhere("id="+itemSelect.getId());
//                ProductoInventario prod = app.getControl().getProductoInventario(itemSelect.getCodigo(), loc.getId());
                if (item != null) {
                    tfCantidadInv.setText(String.valueOf(item.getQuantity()));
                    showInfoConciliacion();
                } else {
                    tfCantidadInv.setText(String.valueOf(0));
                    showInfoConciliacion();
                }
            }
        }
    }

    @Override
    public void caretUpdate(CaretEvent e) {
        if (e.getSource().equals(tfCantidadReal)) {
            showInfoConciliacion();
        }
    }

    private void showInfoConciliacion() {
        String code = "C" + generarCodigo();
        codigo = code;

        double inv = 0;  //Double.parseDouble(tfCantidadInv.getText());
        double real = 0;
        try {
            inv = Double.parseDouble(tfCantidadInv.getText());
            real = Double.parseDouble(tfCantidadReal.getText());
        } catch (Exception e) {
        }
        double diff = inv - real;
        StringBuilder str = new StringBuilder();
        str.append("<html>");
        str.append("<table font-size=14>");
        str.append("<tr><td>Conciliacion:</td><td color=blue>").append(code).append("</td></tr>");
        str.append("<tr><td>Fecha:</td><td color=blue>").append(app.getFormatoFecha().format(datePick1.getDate())).append("</td></tr>");
        str.append("<tr><td>Locacion:</td><td color=blue>").append("Locacion").append("</td></tr>");
        str.append("<tr><td>Inventario:</td><td color=blue>").append(tfCantidadInv.getText()).append("</td></tr>");
        str.append("<tr><td>Real:</td><td color=blue>").append(tfCantidadReal.getText()).append("</td></tr>");
        str.append("<tr><td>Diferencia:</td><td color=").append(diff == 0 ? "color=blue>" : diff > 0 ? "red>" : "orange>").append(diff).append("</td></tr>");
        str.append("</table>");
        str.append("</html>");
        lbInfoConciliacion.setText(str.toString());
    }

    private void resetDatos() {
        cbLocacion.setSelectedIndex(-1);
        tfCantidadInv.setText("");
        tfCantidadReal.setText("");
    }

    @Override
    public void reset() {
//        modelo.setRowCount(0);
        resetDatos();
        cbLocacion.setBorder(bordeNormal);
        tfCantidadReal.setBorder(bordeNormal);
        regFecha.setBorder(bordeNormal);
        itemSelect = null;
        tfBuscar.setText("");
        datePick1.setDate(new Date());
        lbInfo.setText("");
        lbInfoConciliacion.setText("");
    }

    private String generarCodigo() {
        int rows = app.getControl().contarRows("select id from conciliaciones");
        String codigo = Utiles.getNumeroFormateado(rows + ajusteRegistros + 1, 5);
        int existClave = app.getControl().existClave("conciliaciones", "codigo", "'" + codigo + "'");
        while (existClave >= 1) {
            //Comprobar si se esta creando una clave repetida por eliminacion de registros
            //Si esta repetida ajustar el valor y guardar el ajuste para la proxima insercion
            ajusteRegistros++;
            codigo = Utiles.getNumeroFormateado(rows + ajusteRegistros + 1, 5);
            existClave = app.getControl().existClave("conciliaciones", "codigo", "'" + codigo + "'");
        }
        return codigo;
    }

}
