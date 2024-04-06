/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rb.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.util.EventObject;
import javax.swing.DefaultCellEditor;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SwingUtilities;
import org.dz.TextFormatter;


/**
 *
 * @author lrod
 */
public class SpinnerEditor extends DefaultCellEditor {

    private JSpinner spinner;
    private JSpinner.DefaultEditor editor;
    private JTextField textField;
    private boolean valueSet;

    public SpinnerEditor(SpinnerModel model) {
        super(new JTextField());
        spinner = new JSpinner(model);
        editor = ((JSpinner.DefaultEditor) spinner.getEditor());

        textField = editor.getTextField();
        
        textField.setDocument(TextFormatter.getIntegerLimiter());
        textField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        if (valueSet) {
                            textField.setCaretPosition(1);
                        }
                    }
                });
            }

            @Override
            public void focusLost(FocusEvent e) {

            }
        });

        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopCellEditing();
            }
        });

    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        if (!valueSet) {
            spinner.setValue(value);
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                textField.requestFocus();
            }
        });

        return spinner;
    }

    @Override
    public boolean isCellEditable(EventObject anEvent) {
        if (anEvent instanceof KeyEvent) {
            KeyEvent ke = (KeyEvent) anEvent;
            textField.setText(String.valueOf(ke.getKeyChar()));
            valueSet = true;
        } else {
            valueSet = false;
        }
        return true;
    }

    @Override
    public Object getCellEditorValue() {
        return spinner.getValue();
    }

    @Override
    public boolean stopCellEditing() {
        try {
            editor.commitEdit();
            spinner.commitEdit();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Invalid value");
        }
        return super.stopCellEditing(); //To change body of generated methods, choose Tools | Templates.
    }

}
