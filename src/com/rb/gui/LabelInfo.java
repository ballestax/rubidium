package com.rb.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

/**
 *
 * @author lrod
 */
public class LabelInfo extends Box {

    private String title;
    private Double quantity;

    private Color colorFont;
    private Color colorBackground;
    private Color colorBorder;

    private JLabel labelTitle;
    private JLabel labelQuantity;
    private final DecimalFormat DCFORM_P;

    public LabelInfo(String title, double quantity, Dimension dim) {
        super(BoxLayout.Y_AXIS);

        setPreferredSize(dim);
        setMinimumSize(dim);

        this.title = title;
        this.quantity = quantity;

        colorBackground = Color.pink;
        colorBorder = Color.RED;
        colorFont = Color.BLACK;

        DCFORM_P = (DecimalFormat) NumberFormat.getInstance();
        DCFORM_P.applyPattern("$ ###,###,###.##");

        setOpaque(true);
        setBackground(colorBackground);
        setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        createComponents();
    }

    public void setColor(Color color) {
        
        colorBackground = color.brighter().brighter();
        colorBorder = color.darker();
        
        refreshColor();
    }

    private void createComponents() {
        labelTitle = new JLabel(title);
        labelTitle.setOpaque(true);
        

        labelQuantity = new JLabel(DCFORM_P.format(quantity.doubleValue()));
        labelQuantity.setOpaque(true);

        labelQuantity.setHorizontalAlignment(SwingConstants.RIGHT);
        labelQuantity.setMinimumSize(new Dimension(150, 18));
        labelQuantity.setPreferredSize(new Dimension(150, 18));

        refreshColor();

//        labelQuantity.setBorder(BorderFactory.createLineBorder(Color.YELLOW));
        add(labelTitle);

        add(labelQuantity);

    }

    private void refreshColor() {
        setBackground(colorBackground);
        labelTitle.setBackground(colorBackground);
        labelQuantity.setBackground(colorBackground);
        labelTitle.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, colorBackground.darker()));

        setBorder(BorderFactory.createLineBorder(colorBorder, 1, true));
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
        labelQuantity.setText(DCFORM_P.format(quantity.doubleValue()));
    }

    public Double getQuantity() {
        return quantity;
    }
    
    

}
