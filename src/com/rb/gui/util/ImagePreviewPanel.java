/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rb.gui.util;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JPanel;
import javax.swing.ImageIcon;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JFileChooser;
import java.io.File;

/**
 *
 * @author ballestax
 */
public class ImagePreviewPanel extends JPanel implements PropertyChangeListener {

    private int width, height;
    private ImageIcon icon;
    private Image image;
    private static final int SZ_IMAGE_DF = 155;
    private Color colorBG;

    public ImagePreviewPanel() {
        setPreferredSize(new Dimension(SZ_IMAGE_DF, -1));
        colorBG = getBackground();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();
        if (propertyName.equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)) {
            File selection = (File) evt.getNewValue();
            String name;
            if (selection == null) {
                return;
            } else {
                name = selection.getAbsolutePath();
            }

            if ((name != null)
                    && name.toLowerCase().endsWith(".jpg")
                    || name.toLowerCase().endsWith(".png")
                    || name.toLowerCase().endsWith(".jpeg")
                    || name.toLowerCase().endsWith(".gif")) {
                icon = new ImageIcon(name);
                image = icon.getImage();
                scaleImage();
                repaint();
            }
        }
    }

    private void scaleImage() {
        width = image.getWidth(this);
        height = image.getHeight(this);
        double ratio = 1.0;

        if (width >= height) {
            ratio = (double) (SZ_IMAGE_DF - 5) / width;
            width = SZ_IMAGE_DF - 5;
            height = (int) (height * ratio);
        } else {
            if (getHeight() > 150) {
                ratio = (double) (SZ_IMAGE_DF - 5) / height;
                height = SZ_IMAGE_DF - 5;
                width = (int) (width * ratio);
            } else {
                ratio = (double) getHeight() / height;
                height = getHeight();
                width = (int) (width * ratio);
            }
        }
        image = image.getScaledInstance(width, height, Image.SCALE_DEFAULT);
    }

    public void paintComponent(Graphics g) {
        g.setColor(colorBG);
        g.fillRect(0, 0, SZ_IMAGE_DF, getHeight());
        g.drawImage(image, getWidth() / 2 - width / 2 + 5, getHeight() / 2 - height / 2, this);
    }

}
