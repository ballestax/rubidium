/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rb.gui.util;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import org.dz.DatePicker;
import org.dz.MyDialogEsc;
import org.dz.Resources;

/**
 *
 * @author LuisR
 */
public class DatePickerImp extends JComponent implements ActionListener, CaretListener, KeyListener, PropertyChangeListener {

    private PropertyChangeSupport pcs;
    public static final String DATE_CHANGED = "DATE_CHANGED";
    public static final SimpleDateFormat formatDate = new SimpleDateFormat("dd-MM-yyyy");

    /**
     * Constructor por defecto
     */
    public DatePickerImp() {
        this(new Date(), false);
    }

    /**
     *
     * @param date
     * @param show
     */
    public DatePickerImp(Date date, boolean show) {
        this.date = date;
        pcs = new PropertyChangeSupport(this);
        initComponent(show);
    }

    private void initComponent(boolean show) {

        btPick = new JButton();
        btPick.setActionCommand("SHOW_DATEPICKER");
        btPick.addActionListener(this);
        btPick.setIcon(new ImageIcon(Resources.getImagen("img/calendar.png", DatePickerImp.this.getClass(), 16, 16)));
        btPick.setPreferredSize(new Dimension(16, 16));
        datePicker = new DatePicker(date);
        datePicker.addPropertyChangeListener(this);
        dateField = new JTextField();
        dateField.addCaretListener(this);
//        formatDate = new SimpleDateFormat("dd-MM-yyyy");
        if (show) {
            dateField.setText(formatDate.format(date));
        }
        setLayout(new BoxLayout(this, 0));
        add(dateField);
        add(btPick);
        /*addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == 3) {
                    DatePickerImp.this.setVisible(false);
                }
            }
        });
        addKeyListener(this);*/
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("SHOW_DATEPICKER")) {
            dialog = new MyDialogEsc();
            dialog.setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);
            dialog.setPreferredSize(new Dimension(200, 260));
            dialog.add(datePicker);
            dialog.setUndecorated(true);
            dialog.pack();
            Point loc = ((JComponent) e.getSource()).getLocationOnScreen();
            dialog.setLocation(loc.x - 180, loc.y + 20);
            dialog.addKeyListener(this);
            dialog.setVisible(true);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().startsWith("SEL_DIA_")) {
            Object value = evt.getNewValue();
            if (value != null && (value instanceof GregorianCalendar)) {
                GregorianCalendar fecSel = (GregorianCalendar) value;
                String stDate = formatDate.format(fecSel.getTime());
                dateField.setText(stDate);
                dialog.setVisible(false);
                setDate(fecSel.getTime());
                pcs.firePropertyChange(DATE_CHANGED, null, fecSel.getTime());
            }
        }
    }

    public SimpleDateFormat getFormatDate() {
        return formatDate;
    }

    public String getText() {
        return dateField.getText();
    }

    public void setText(String text) {
        dateField.setText(text);
        try {
            date = formatDate.parse(text);
        } catch (ParseException ex) {
            Logger.getLogger(DatePickerImp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setEditable(boolean editable) {
        java.awt.Color background = dateField.getBackground();
        dateField.setEditable(editable);
        btPick.setEnabled(editable);
        dateField.setBackground(background);
    }

    public void setTextEditable(boolean editable) {
        java.awt.Color background = dateField.getBackground();
        dateField.setEditable(editable);
        dateField.setBackground(background);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void keyPressed(KeyEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void keyReleased(KeyEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
        setText(formatDate.format(date));
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        if (listener != null) {
//            super.addPropertyChangeListener(listener);
            if (!containsListener(pcs, listener)) {
                pcs.addPropertyChangeListener(listener);
            }
        }
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        if (listener != null) {
//            super.removePropertyChangeListener(listener);
            pcs.removePropertyChangeListener(listener);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        dateField.setEnabled(enabled);
        btPick.setEnabled(enabled);
    }

    private boolean containsListener(PropertyChangeSupport pcs, PropertyChangeListener pcl) {
        ArrayList<PropertyChangeListener> listeners = new ArrayList<>(Arrays.asList(pcs.getPropertyChangeListeners()));
        return listeners.contains(pcl);
    }

    @Override
    public void caretUpdate(CaretEvent e) {
        if (e.getSource().equals(dateField)) {

            try {
                date = formatDate.parse(dateField.getText());
            } catch (Exception ex) {
                date = getDate();
            }
        }
    }

    public void showDate() {
        dateField.setText(formatDate.format(date));
    }

    private JButton btPick;
    private DatePicker datePicker;
    private JTextField dateField;
    private Date date;
    private MyDialogEsc dialog;

}
