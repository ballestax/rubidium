/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bacon.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.text.Format;
import javax.swing.BorderFactory;
import static javax.swing.BorderFactory.createLineBorder;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author LuisR
 */
public class ProductRenderer extends Box implements TableCellRenderer {
    
    private JLabel labelName, labelAdicion, labelNotas;

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
        
        Font f1 = new Font("Serif", 0, 11);
        
        labelAdicion.setFont(f1);
        labelNotas.setFont(f1);
        
        add(labelName);
        add(labelAdicion);
        add(labelNotas);
        
        setOpaque(true);
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (value != null) {
            try {
                String[] data = (String[]) value;
                labelName.setText(data[0]);
                labelAdicion.setText(data[1]);
                labelNotas.setText(data[2]);
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
