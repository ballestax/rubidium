/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bacon.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author lrod
 */
public class SpinnerRenderer extends JSpinner implements TableCellRenderer {
    
    public SpinnerRenderer(Font f) {
        setOpaque(true);
        setFont(f);
        setBackground(Color.BLUE.darker());
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        setValue(value);
        return this;
    }
    
}
