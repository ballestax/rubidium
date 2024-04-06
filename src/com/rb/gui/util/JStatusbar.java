/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rb.gui.util;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 *
 * @author CARLOS
 */
public class JStatusbar extends JPanel {

    public JStatusbar() {
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        int y = 0;
        g.setColor(new Color(156, 154, 140));
        g.drawLine(0, y, getWidth(), y);
        y++;
        g.setColor(new Color(196, 194, 183));
        g.drawLine(0, y, getWidth(), y);
        y++;
        g.setColor(new Color(218, 215, 201));
        g.drawLine(0, y, getWidth(), y);
        y++;
        g.setColor(new Color(233, 231, 217));
        g.drawLine(0, y, getWidth(), y);

        y = getHeight() - 3;
        g.setColor(new Color(233, 232, 218));
        g.drawLine(0, y, getWidth(), y);
        y++;
        g.setColor(new Color(233, 231, 216));
        g.drawLine(0, y, getWidth(), y);
        y = getHeight() - 1;
        g.setColor(new Color(221, 221, 220));
        g.drawLine(0, y, getWidth(), y);

    }

    public void addRightComponent(JComponent component, int dialogUnits) {
//        layout.appendColumn(new ColumnSpec("2dlu"));
//        layout.appendColumn(new ColumnSpec(dialogUnits + "dlu"));
//
//        layoutCoordinateX++;
//        contentPanel.add(
//                new SeparatorPanel(Color.GRAY, Color.WHITE),
//                new CellConstraints(layoutCoordinateX, layoutCoordinateY));
//        layoutCoordinateX++;
//        contentPanel.add(
//                component,
//                new CellConstraints(layoutCoordinateX, layoutCoordinateY));
    }
}
