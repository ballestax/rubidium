package com.bacon.gui;

import com.bacon.domain.Waiter;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import org.dz.Utiles;

/**
 *
 * @author lrod
 */
public class WaiterListCellRenderer extends JLabel implements javax.swing.ListCellRenderer<Object> {

    public WaiterListCellRenderer() {
        setOpaque(true);
        setForeground(Utiles.colorAleatorio(0, 255));
        setBorder(BorderFactory.createEmptyBorder(3, 2, 3, 2));
        setFont(new Font("Sans", 1, 16));
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Object> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if (value != null && value instanceof Waiter) {
            Waiter waiter = (Waiter) value;

            setText(waiter.getName().toUpperCase());
            Color color = Color.BLACK;
            try {
                color = Color.decode(waiter.getColor());
            } catch (Exception e) {
            }

            setForeground(color);
            if (isSelected) {
                setBackground(list.getSelectionBackground());
            } else {
                setBackground(list.getBackground());
            }
        }
        return this;
    }
}
