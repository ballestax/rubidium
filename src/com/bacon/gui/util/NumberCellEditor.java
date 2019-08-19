/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bacon.gui.util;

import java.text.ParseException;
import java.awt.Component;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Locale;
import javax.swing.DefaultCellEditor;
import javax.swing.JFormattedTextField;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.text.NumberFormatter;

/**
 *
 * @author LuisR
 */
public class NumberCellEditor extends DefaultCellEditor {

    private int decimales;
    
    public NumberCellEditor() {
        super(new JFormattedTextField());
        decimales = 2;
    }
    
    public NumberCellEditor(int decimales) {
        super(new JFormattedTextField());        
        this.decimales = decimales;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        JFormattedTextField editor = (JFormattedTextField) super.getTableCellEditorComponent(table, value, isSelected, row, column);

        if (value instanceof Number) {
            Locale myLocale = Locale.getDefault();

            NumberFormat numberFormatB = NumberFormat.getInstance(myLocale);
            numberFormatB.setMaximumFractionDigits(decimales);
            numberFormatB.setMinimumFractionDigits(decimales);
            numberFormatB.setMinimumIntegerDigits(1);

            editor.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(
                    new NumberFormatter(numberFormatB)));

            editor.setHorizontalAlignment(SwingConstants.RIGHT);
//            editor.setDocument(org.bx.TextFormatter.getDoubleLimiter());
            editor.setValue(value);
        }
        return editor;
    }

    @Override
    public boolean stopCellEditing() {
        try {
            // try to get the value
            this.getCellEditorValue();
            return super.stopCellEditing();
        } catch (Exception ex) {
            return false;
        }

    }

    @Override
    public Object getCellEditorValue() {
        // get content of textField
        String str = (String) super.getCellEditorValue();
        if (str == null) {
            return null;
        }

        if (str.length() == 0) {
            return null;
        }

        // try to parse a number
        try {
            ParsePosition pos = new ParsePosition(0);
            Number n = NumberFormat.getInstance().parse(str, pos);
            if (pos.getIndex() != str.length()) {
                throw new ParseException("parsing incomplete", pos.getIndex());
            }

            // return an instance of column class
            return new Float(n.floatValue());

        } catch (ParseException pex) {
            throw new RuntimeException(pex);
        }
    }
}
