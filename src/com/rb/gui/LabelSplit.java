/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rb.gui;

import com.rb.ImageManager;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JLabel;
import org.apache.log4j.Logger;

/**
 *
 * @author lrod
 */
public class LabelSplit extends JLabel {

    private String textShow;
    private String text = "";
    private boolean ready = false;

    private static final Logger logger = Logger.getLogger(LabelSplit.class.getCanonicalName());

    public LabelSplit(String text) {
        super(text);
        logger.debug("creando splitlabel. Text:" + text);
        setupText();
        textShow = text;

    }

    private void setupText() {
        String[] split = ImageManager.partirCadena(getText(), getFont(), 150, 12);
        StringBuilder stb = new StringBuilder("<html>");
        for (String string : split) {
            stb.append("<p>").append(string).append("</p>");
        }
        stb.append("</html>");
        setToolTipText(stb.toString());

        calculateSplit();

    }

    @Override
    public void setText(String text) {
        super.setText(text); //To change body of generated methods, choose Tools | Templates.
        textShow = text;

    }

    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag); //To change body of generated methods, choose Tools | Templates.
        if (aFlag) {
            calculateSplit();
        }
    }

    private void calculateSplit() {
        if (text == null || text.isEmpty()) {
            text = getText();
        }
        logger.debug("calculate split:" + text);

        int offset = 10;
        int width = getWidth();
        String tmpText = text;
        boolean band = true;

        do {

            double textSize = getTextSize(tmpText);
            if (textSize > (width - offset) && tmpText.length() > 1) {
                tmpText = tmpText.substring(0, tmpText.length() - 1);
            } else {
                band = false;
                ready = true;
            }

        } while (band);
        setText(tmpText + "...");

    }

    private double getTextSize(String text) {
        double width = 1;
        try {
            Graphics2D g = (Graphics2D) getGraphics();
            java.awt.font.FontRenderContext frc = g.getFontRenderContext();
            Rectangle2D bounds = getFont().getStringBounds(text, frc);
            width = bounds.getWidth();
        } catch (Exception e) {
        }
        return width;
    }

}
