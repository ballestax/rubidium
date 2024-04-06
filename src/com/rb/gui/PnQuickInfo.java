/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rb.gui;

import com.rb.Aplication;
import com.rb.domain.Invoice;
import com.rb.domain.Location;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.dz.PanelCapturaMod;

/**
 *
 * @author LuisR
 */
public class PnQuickInfo extends PanelCapturaMod implements ActionListener {

    private final Aplication app;
    private JPopupMenu popResultados;
    private Invoice productoSelect;

    /**
     * Creates new form PnQuickInfo
     */
    public PnQuickInfo(Aplication app) {
        this.app = app;
        initComponents();
        createComponenentes();
    }

    private void createComponenentes() {

        btBuscar.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                buscar();
            }
        });

        tfBusqueda.addActionListener(this);
        popResultados = new JPopupMenu();

        tfBusqueda.setForeground(Color.BLUE.darker());
        
        lbLogo.setIcon(new ImageIcon(app.getImgManager().getImagen("gui/img/LlantasAmerica.png", 80, 25)));

        btSalir.setText("Salir");
        btSalir.setActionCommand(Aplication.ACTION_CANCEL_PANEL);
        btSalir.addActionListener(this);

    }

    private void buscar() {
        try {
            if (tfBusqueda.getText() != null) {
                String nombre = tfBusqueda.getText();
                buscar(nombre);
            }
        } catch (Exception ex) {
            lbInfo.setText("<html><font color=red><p>Error consultando la base de datos.</p></font></html>");
            productoSelect = null;
        }

    }

    public boolean showingPopup() {
        return popResultados.isVisible();
    }

    public void buscar(String busqueda) {
//        setBuscando(true);
        String[] split = busqueda.split(" ");

        String ESCAPE = "\\\\";
        ESCAPE = " LIKE '%\\\\" + busqueda + "%' ESCAPE '\\\\' ";
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM productos WHERE nombre ");
        sql.append(ESCAPE);
        sql.append(" order by nombre");

        ArrayList<Invoice> productosList = app.getControl().getInvoiceslList(sql.toString(), "");
        int size = productosList.size();
        Iterator<Invoice> it = productosList.iterator();
        popResultados.removeAll();
        try {
            int c = 0;
            int LIM = 20;
            while (it.hasNext() && c < LIM) {
                Invoice prod = it.next();
                String str = prod.getFecha().toString();
                String codigo = prod.getFactura();
                String stPers = "<html><p>" + str.toUpperCase() + "</p></html>";
                stPers = stPers.replace(busqueda.toUpperCase(), "<font color=blue>" + busqueda.toUpperCase() + "</font>");

                JMenuItem item = new JMenuItem(stPers);
                item.setActionCommand("PRODUCTO:" + codigo);
                item.addActionListener(this);
                popResultados.add(item);
                c++;
            }
            if (c == 0) {
                JMenuItem item = new JMenuItem("No encontrado");
                item.setForeground(Color.red);
                tfBusqueda.setForeground(Color.red);
                popResultados.add(item);
            } else if (size > LIM) {
                int dif = size - LIM;
                JMenuItem item = new JMenuItem("<html><font color=orange><p>Sea mas especifico en la busqueda</p>"
                        + "<p>+" + dif + " registros encontrados.</p></font></html>");
                item.setForeground(Color.orange);
                tfBusqueda.setForeground(Color.orange);
                popResultados.add(item);
            }
            popResultados.show(tfBusqueda, 0, tfBusqueda.getHeight());

        } catch (Exception e) {
        }
    }
    
    private void buscarInvoice(String codigo) {
        Invoice producto = app.getControl().getInvoiceByCode(codigo);
        if (producto != null) {
            lbInfo.setText(getInfoInvoice(producto));
            productoSelect = producto;
        } else {
            lbInfo.setText("");
            productoSelect = null;
        }
    }

    private String getInfoInvoice(Invoice prod) {
        Map<Integer, String> maps = getLocationsMap();
        StringBuilder str = new StringBuilder();
//        double precio = app.getControl().getProductPrecio("producto='" + prod.getFactura()+ "'");
//        double prom = app.getControl().getProductPrecioPromedio("producto='" + prod.getFactura()+ "'");
        str.append("<html>");
        str.append("<table background-color=#453214><tr>");
        str.append("<td>Codigo:</td><td color=blue><font size=+1>").append(prod.getFactura()).append("</font></td></tr>");
        str.append("<tr><td>Invoice:</td><td color=blue><font size=+1>").append(prod.getFecha()).append("</font></td></tr>");
//        str.append("<tr><td>Precio:</td><td color=blue><font size=+1>").append(app.getDCFORM_P().format(precio)).append("</font></td></tr>");
//        str.append("<tr><td>Promedio:</td><td color=blue><font size=+1>").append(app.getDCFORM_P().format(prom)).append("</font></td></tr></table>");

//        ArrayList<Object[]> list = app.getControl().getCantidadInvoiceList("productos_inventario.producto='" + prod.getCodigo() + "'", "");
        String html = "<table><tr>";
//        for (int i = 0; i < list.size(); i++) {
//            Object[] get = list.get(i);
//            String color = "#C6ECBC";
//            html += "<td bgcolor=" + color + ">";
//            html += maps.get(get[0]) + ":</td><td bgcolor=" + color + "><font color=" + "red" + " size=+1>" + get[1] + " </font>";
//            html += "</td><td> </td>";
//
//        }
        html += "</tr></table></html>";
        str.append(html);
        return str.toString();
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

        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        tfBusqueda = new javax.swing.JTextField();
        btBuscar = new javax.swing.JButton();
        lbInfo = new javax.swing.JLabel();
        btSalir = new javax.swing.JButton();
        lbLogo = new javax.swing.JLabel();

        jButton1.setText("jButton1");

        setBackground(new java.awt.Color(166, 162, 220));

        jLabel1.setText("Buscar:");

        tfBusqueda.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        tfBusqueda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfBusquedaActionPerformed(evt);
            }
        });

        btBuscar.setText("Buscar");

        lbInfo.setBackground(new java.awt.Color(222, 241, 235));
        lbInfo.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        lbInfo.setOpaque(true);

        btSalir.setText("jButton2");

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
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfBusqueda, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btBuscar)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(lbLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btSalir)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfBusqueda, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbInfo, javax.swing.GroupLayout.DEFAULT_SIZE, 232, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btSalir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbLogo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void tfBusquedaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfBusquedaActionPerformed
        buscar();
    }//GEN-LAST:event_tfBusquedaActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btBuscar;
    private javax.swing.JButton btSalir;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel lbInfo;
    private javax.swing.JLabel lbLogo;
    private javax.swing.JTextField tfBusqueda;
    // End of variables declaration//GEN-END:variables

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().startsWith("PRODUCTO:")) {
            String codigo = e.getActionCommand().substring(9);
            buscarInvoice(codigo);
        } else if (Aplication.ACTION_CANCEL_PANEL.equals(e.getActionCommand())) {
            cancelPanel();
        }
    }
}
