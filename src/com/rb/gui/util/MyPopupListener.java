/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rb.gui.util;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

/**
 *
 * @author ballestax
 */
public class MyPopupListener implements MouseListener {

    boolean seleccionar;
    JPopupMenu popup;

    public MyPopupListener(JPopupMenu popup) {
        this.popup = popup;
        this.seleccionar = seleccionar;
    }

    public MyPopupListener(JPopupMenu popup, boolean seleccionar) {
        this.popup = popup;
        this.seleccionar = seleccionar;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        isPopupTrigger(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        isPopupTrigger(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        isPopupTrigger(e);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        isPopupTrigger(e);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        isPopupTrigger(e);
    }

    public void isPopupTrigger(MouseEvent e) {
        if (e.isPopupTrigger()) {
            if (seleccionar) {
                Object source = e.getSource();
                if (source != null && (source instanceof JTable)) {
                    JTable table = (JTable) e.getSource();
                    int rowAtPoint = table.rowAtPoint(e.getPoint());
                    table.getSelectionModel().setSelectionInterval(rowAtPoint, rowAtPoint);
                }
            }
            popup.show(e.getComponent(), e.getX(), e.getY());
        }
    }
}
