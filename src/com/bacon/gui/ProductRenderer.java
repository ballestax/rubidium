/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bacon.gui;

import com.bacon.domain.Presentation;
import com.bacon.domain.ProductoPed;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author LuisR
 */
public class ProductRenderer extends Box implements TableCellRenderer {

    private JLabel labelName, labelAdicion, labelNotas, labelEsp;

    /*
         *   Use the specified formatter to format the Object
     */
    public ProductRenderer(int axis) {
        super(axis);
        createComponent();
    }

    private void createComponent() {
        labelName = new JLabel();
        labelName.setOpaque(true);
        labelAdicion = new JLabel();
        labelAdicion.setOpaque(true);
        labelNotas = new JLabel();
        labelNotas.setOpaque(true);
        labelEsp = new JLabel();
        labelEsp.setOpaque(true);

        Font f1 = new Font("Serif", 0, 11);

        labelAdicion.setFont(f1);
        labelNotas.setFont(f1);
        labelEsp.setFont(f1);

        add(labelName);
        add(labelAdicion);
        
//        add(labelNotas);
//        add(labelEsp);

        setOpaque(true);

    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (value != null) {
            try {
                ProductoPed prodPed = (ProductoPed) value;

                Presentation presentation = prodPed.getPresentation();
                String stPres = "";
                if (presentation != null) {
                    stPres = " (" + presentation.getName() + ")";
                }
                String stExclusion = prodPed.getStExclusiones();

                labelName.setText(("<html><p>" + prodPed.getProduct().getName() + "</p><font size=2>" + stPres + "</font></html>").toUpperCase());
                labelAdicion.setText("<html>" + prodPed.getStAdicionales() + "</html>");
                labelNotas.setText(stExclusion.isEmpty() ? "" : "Sin: " + stExclusion);
                labelEsp.setText(prodPed.getEspecificaciones());

                StringBuilder stb = new StringBuilder();
                stb.append("<html>");
                stb.append("<font color=blue>").append(prodPed.getProduct().getName().toUpperCase()).append("</font>");
                stb.append("<p>").append(prodPed.getStAdicionales()).append("</p>");
                stb.append("<p>").append(prodPed.getStExclusiones()).append("</p>");
                stb.append("<p>").append(prodPed.getEspecificaciones()).append("</p></html>");

//                System.err.println(stb.toString());
                setToolTipText(stb.toString());
            } catch (Exception e) {
            }
        }

        if (isSelected) {
            setBackground(table.getSelectionBackground());
//            if (hasFocus) {
//                setBorder(BorderFactory.createLineBorder(Color.darkGray));
//            } else {
//                setBorder(createLineBorder(Color.lightGray));
//            }
        } else {
            setBackground(table.getBackground());
//            setForeground(Color.black);
            setBorder(UIManager.getBorder("Table.cellBorder"));
        }

        return this;
    }

    @Override
    public void setBackground(Color bg) {
        super.setBackground(bg); //To change body of generated methods, choose Tools | Templates.
        labelName.setBackground(bg);
        labelAdicion.setBackground(bg);
        labelNotas.setBackground(bg);
    }

}
