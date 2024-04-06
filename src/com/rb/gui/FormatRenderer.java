/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rb.gui;

import java.awt.Color;
import java.text.Format;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author LuisR
 */
public class FormatRenderer extends DefaultTableCellRenderer {

    private final Format formatter;
    
    
    /*
         *   Use the specified formatter to format the Object
     */
    public FormatRenderer(Format formatter) {
        this.formatter = formatter;
        setHorizontalAlignment(SwingConstants.RIGHT);
        
    }

    @Override
    public void setValue(Object value) {
        //  Format the Object before setting its value in the renderer
        try {
            if (value != null) {
                value = formatter.format(value);
            }
        } catch (IllegalArgumentException e) {
        }

        super.setValue(value);
    }

}
