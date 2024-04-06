/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rb.gui.util;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JToggleButton;

/**
 *
 * @author ballestax
 */
public class MyButtonGroup extends JComponent {

    private static final String ON = "On";
    private static final String OFF = "Off";
    private final JToggleButton bOn = new JToggleButton(ON);
    private final JToggleButton bOff = new JToggleButton(OFF);
    private final ButtonHandler handler = new ButtonHandler();

    public MyButtonGroup() {
        this.setLayout(new FlowLayout());
        ButtonGroup bg = new ButtonGroup();
        this.add(bOn);
        bg.add(bOn);
        bOn.setSelected(true);
        bOn.addActionListener(handler);
        this.add(bOff);
        bg.add(bOff);
        bOff.addActionListener(handler);
    }

    public void addActionListener(ActionListener listener) {
        bOn.addActionListener(listener);
        bOff.addActionListener(listener);
    }

    private class ButtonHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String cmd = e.getActionCommand();
            if (ON.equals(cmd)) {
            } else {
                
            }
        }
    }

}
