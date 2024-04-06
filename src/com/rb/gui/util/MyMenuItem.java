/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rb.gui.util;

import java.awt.Color;
import javax.swing.Action;
import javax.swing.JMenuItem;

/**
 *
 * @author ballestax
 */
public class MyMenuItem extends JMenuItem {

    private int orden;

    public MyMenuItem(Action a, Color bgColor) {
        super(a);
        setBackground(bgColor);
        updateUI();
    }

    public MyMenuItem(String text, Color bgColor) {
        super(text);
        setBackground(bgColor);
        updateUI();
    }

    public int getOrden() {
        return orden;
    }

    public void setOrden(int orden) {
        this.orden = orden;
    }

}
