/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bacon.gui.util;

import java.awt.Color;
import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author ballestax
 */
public class TableSelectCellRenderer extends JLabel implements TableCellRenderer {

    boolean isBordered = true;
    JTable table;
    private static Color COLOR_CHECK = new Color(100, 220, 159);

    public TableSelectCellRenderer(boolean isBordered) {
        this.isBordered = isBordered;        
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (column != 0) {
            if ((Boolean) table.getValueAt(row, 0) == true) {
                setBackground(COLOR_CHECK);
//                    setForeground(EST_COLOR[estados[row]]);
                if (hasFocus) {
                    setBorder(BorderFactory.createLineBorder(Color.red));
                } else {
                    setBorder(BorderFactory.createLineBorder(Color.ORANGE));
                }
            } else {
                if (isSelected) {
//                        setForeground(EST_COLOR[estados[row]]);
                    setBackground(table.getSelectionBackground());
                    if (hasFocus) {
                        setBorder(BorderFactory.createLineBorder(Color.darkGray));
                    } else {
                        setBorder(BorderFactory.createLineBorder(Color.lightGray));
                    }
                } else {
//                    setBackground(table.getBackground());
                    setBackground(row % 2 == 0 ? table.getBackground() : UIManager.getColor("Table.alternateRowColor"));
//                        setForeground(EST_COLOR[estados[row]]);
                    setBorder(UIManager.getBorder("Table.cellBorder"));
                }
            }
        }
        if (column == 0) {
            JCheckBox component = (JCheckBox) table.getDefaultRenderer(Boolean.class).
                    getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if ((Boolean) table.getValueAt(row, 0) == true) {
                component.setBackground(COLOR_CHECK);
                component.setForeground(Color.white);
                if (isSelected) {
                    if (hasFocus) {
                        component.setBorder(BorderFactory.createLineBorder(Color.red));
                    } else {
                        component.setBorder(BorderFactory.createLineBorder(Color.ORANGE));
                    }
                } else {
                    component.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.ORANGE));
                }
            } else {
                if (isSelected) {
                    component.setForeground(table.getSelectionForeground());
                    component.setBackground(table.getSelectionBackground());
                    if (hasFocus) {
                        component.setBorder(BorderFactory.createLineBorder(Color.darkGray));
                    } else {
                        component.setBorder(BorderFactory.createLineBorder(Color.lightGray));
                    }
                } else {
                    component.setBackground(row % 2 == 0 ? table.getBackground() : UIManager.getColor("Table.alternateRowColor"));
                    component.setForeground(table.getForeground());
                    component.setBorder(UIManager.getBorder("Table.cellBorder"));
                }
            }
            return component;

        } else {

            if (value != null) {
                setText(value.toString());
            } else {
                setText("");
            }
            return this;

        }
    }

    public static Color getCOLOR_CHECK() {
        return COLOR_CHECK;
    }

}
