/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rb.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.text.Format;
import javax.swing.BorderFactory;
import static javax.swing.BorderFactory.createLineBorder;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author LuisR
 */
public class TablaCellRenderer extends DefaultTableCellRenderer {

    boolean isBordered = true;
    private boolean agotado;
    private Format format;

    public TablaCellRenderer(boolean isBordered) {
        super();
        this.isBordered = isBordered;
        agotado = false;
        setOpaque(true);
        setHorizontalAlignment(SwingConstants.LEFT);
        format = null;
    }

    public TablaCellRenderer(boolean isBordered, Font f) {
        super();
        this.isBordered = isBordered;
        agotado = false;
        setFont(f);
        setOpaque(true);
        setHorizontalAlignment(SwingConstants.LEFT);
        format = null;
    }

    public TablaCellRenderer(boolean isBordered, int align, Format format) {
        super();
        this.isBordered = isBordered;
//        setFont(f != null ? f : new Font("Tahoma", 0, 12));
        setOpaque(true);
        setHorizontalAlignment(align);
        this.format = format;
    }

    public TablaCellRenderer(boolean isBordered, Font f, int align, Format format) {
        super();
        this.isBordered = isBordered;
        setFont(f != null ? f : new Font("Tahoma", 0, 12));
        setOpaque(true);
        setHorizontalAlignment(align);
        this.format = format;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        int r = table.convertRowIndexToModel(row);
        if (value != null) {
            setText(value.toString().toUpperCase());
            if (format != null) {
                try {
                    setText(format.format(value));
                } catch (Exception e) {

                }
            }
        }
        if (isSelected) {
            setForeground(Color.black);
            setBackground(table.getSelectionBackground());
            if (hasFocus) {
                setBorder(BorderFactory.createLineBorder(Color.darkGray));
            } else {
                setBorder(createLineBorder(Color.lightGray));
            }
        } else {
            setBackground(table.getBackground());
            setForeground(Color.black);
            setBorder(UIManager.getBorder("Table.cellBorder"));
        }
        return this;
    }
}
