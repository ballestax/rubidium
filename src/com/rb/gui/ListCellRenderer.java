/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rb.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.Arrays;
import javax.swing.BorderFactory;
import static javax.swing.BorderFactory.createLineBorder;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JSpinner;
import javax.swing.UIManager;

/**
 *
 * @author lrod
 */
public class ListCellRenderer extends Box implements javax.swing.ListCellRenderer {

    private JSpinner spCantidad;
    private JLabel lbNombre;
    private JLabel lbAdicion;
    private JLabel lbPrecio;
    private JLabel lbNotas;
    private PanelListItem pi;

    public ListCellRenderer(int axis) {
        super(axis);
        setFont(new Font("Courier", 0, 9));
        setOpaque(true);
        createComponent();
    }

    private void createComponent() {

        pi = new PanelListItem();
        add(pi);

        /*
        spCantidad = new JSpinner();
        add(spCantidad);

        Box box = new Box(BoxLayout.Y_AXIS);
        lbNombre = new JLabel();
        box.add(lbNombre);

        lbAdicion = new JLabel();
        box.add(lbAdicion);

        lbNotas = new JLabel();
        box.add(lbNotas);
        add(box);

        lbPrecio = new JLabel();
        add(lbPrecio);*/
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

                
        if (value != null) {
            String[] data = (String[]) value;
//            System.out.println(Arrays.toString(data));
            pi.setCantidad(Integer.valueOf(data[0]));
            pi.setNombre(data[1]);
            pi.setAdicion(data[2]);
            pi.setNotas(data[3]);
            
            pi.setPrecio(data[4]);

//            spCantidad.setValue(Integer.valueOf(data[0]));
//            lbNombre.setText(data[1]);
//            lbAdicion.setText(data[2]);
//            lbNotas.setText(data[3]);
//            lbPrecio.setText(data[4]);
        }

        if (isSelected) {
            setForeground(Color.black);
            setBackground(list.getSelectionBackground());
            if (cellHasFocus) {
                setBorder(BorderFactory.createLineBorder(Color.darkGray));
            } else {
                setBorder(createLineBorder(Color.lightGray));
            }
        } else {
            setBackground(list.getBackground());
            setForeground(Color.black);
//            setBorder(UIManager.getBorder("Table.cellBorder"));
            setBorder(BorderFactory.createLineBorder(Color.orange.darker()));
        }
        return this;
    }
}
