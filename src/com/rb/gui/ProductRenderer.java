/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rb.gui;

import com.rb.Aplication;
import com.rb.domain.AdditionalPed;
import com.rb.domain.Ingredient;
import com.rb.domain.Presentation;
import com.rb.domain.ProductoPed;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.text.AttributedString;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;
import org.dz.Resources;

/**
 *
 * @author LuisR
 */
public class ProductRenderer extends Box implements TableCellRenderer {

    private JLabel labelName, labelEsp;
    private JLabel labelAdicion, labelIngredients;
    private ProductoPed prodPed;
    private Object oldValue;
    private Font f1, fTitle, fTitleAvoid;
    private Color alterRowColor;
    private Box boxTop;
    private JLabel lbIconSended, lbIconEntry;
    private ImageIcon iconSend1;
    private ImageIcon iconSend2;
    private ImageIcon iconSend3;
    private ImageIcon iconPlus;
    private ImageIcon iconMinus;
    private ImageIcon iconCancel;
    private boolean iconPainted = true;
    private Color selectionBackgroundColor = null;
    private boolean marked = false;

    /*
         *   Use the specified formatter to format the Object
     */
    public ProductRenderer(int axis) {
        super(axis);
//        super();
//        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        this.setAlignmentX(LEFT_ALIGNMENT);
        alterRowColor = UIManager.getColor("Table.alternateRowColor");

        createComponent();
    }

    private void createComponent() {
        oldValue = null;

        fTitleAvoid = new Font("Sans", Font.CENTER_BASELINE, 11);
        fTitle = new Font("Sans", Font.BOLD, 11);

        labelName = new JLabel();
        labelName.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));
        labelName.setOpaque(true);
        labelName.setAlignmentX(0);
        labelName.setFont(fTitleAvoid);

        boxTop = new Box(BoxLayout.X_AXIS);

        iconSend1 = new ImageIcon(Resources.getImagen("gui/img/icons/" + "right_green.png", Aplication.class, 20, 20));
        iconSend2 = new ImageIcon(Resources.getImagen("gui/img/icons/" + "right_orange.png", Aplication.class, 20, 20));
        iconSend3 = new ImageIcon(Resources.getImagen("gui/img/icons/" + "right_red.png", Aplication.class, 20, 20));
        iconPlus = new ImageIcon(Resources.getImagen("gui/img/icons/" + "plus.png", Aplication.class, 20, 20));
        iconMinus = new ImageIcon(Resources.getImagen("gui/img/icons/" + "minus.png", Aplication.class, 20, 20));
        iconCancel = new ImageIcon(Resources.getImagen("gui/img/icons/" + "cancel.png", Aplication.class, 20, 20));
//        ImageIcon iconEntry = new ImageIcon(Resources.getImagen("gui/img/icons/" + "clock.png", Aplication.class, 15, 15));
        lbIconSended = new JLabel();
        lbIconSended.setIcon(null);
//        lbIconEntry = new JLabel();
//        lbIconEntry.setIcon(iconEntry);
//        labelName.setPreferredSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
        labelAdicion = new JLabel();
        labelAdicion.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.lightGray));
//        labelAdicion.setEditable(false);
        labelAdicion.setOpaque(true);
        labelAdicion.setAlignmentX(0);
        labelIngredients = new JLabel();
//        labelIngredients.setWrapStyleWord(true);
//        labelIngredients.setLineWrap(true);
        labelIngredients.setOpaque(true);
        labelIngredients.setAlignmentX(0);
        labelIngredients.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.lightGray));
        labelEsp = new JLabel();
        labelEsp.setOpaque(true);
        labelEsp.setAlignmentX(0);

        f1 = new Font("Serif", 0, 11);

        labelAdicion.setFont(f1);
        labelIngredients.setFont(f1);
        labelEsp.setFont(f1);

//        lbIconSended.setVisible(false);
        boxTop.setAlignmentX(0);
        boxTop.add(labelName);
        boxTop.add(lbIconSended);
//        boxTop.add(lbIconEntry);
        add(boxTop);
//        add(labelName);
        add(labelAdicion);
        add(labelIngredients);
//        add(labelEsp);

        labelAdicion.setVisible(false);
        labelIngredients.setVisible(false);
        labelEsp.setVisible(false);
        setOpaque(true);

    }

    public void setIconPainted(boolean iconPainted) {
        this.iconPainted = iconPainted;
    }

    public void setSelectionBackgroundColor(Color selectionBacgroungColor) {
        this.selectionBackgroundColor = selectionBacgroungColor;
    }

    public void setMarked(boolean marked) {
        this.marked = marked;
    }

    private void createTooltip() {
        if (prodPed != null) {
            StringBuilder stb = new StringBuilder();
            stb.append("<html>");
            stb.append("<font color=blue>").append(prodPed.getProduct().getName().toUpperCase()).append("</font>");
            stb.append("<font color=#bb2345>[").append(prodPed.getPrecio()).append("]</font>");
            if (prodPed.hasPresentation()) {
                stb.append("<p>").append(prodPed.getPresentation().getName()).append("</p>");
            }
            if (prodPed.hasAdditionals()) {
                ArrayList<AdditionalPed> adicionales = prodPed.getAdicionales();
                stb.append("Adiciones:");
                stb.append("<ul>");
                for (AdditionalPed adicional : adicionales) {
                    stb.append("<li>").append(adicional.getAdditional().getName())
                            .append("(<font color=blue>x")
                            .append(adicional.getCantidad())
                            .append("(</font>)")
                            .append("</li>");
                }
                stb.append("</ul>");

            }
            if (prodPed.hasExcluisones()) {
                stb.append("<p><font color=red> Sin:").append(prodPed.getStExclusiones()).append("</font></p>");
            }
//                stb.append("<p>").append(prodPed.getEspecificaciones()).append("</p>)");
            stb.append("</html>");

//                System.err.println(stb.toString());
            setToolTipText(stb.toString());
        }
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (value != null) {
            try {
                prodPed = (ProductoPed) value;

                labelName.setFont(fTitle);

                int width = table.getColumnModel().getColumn(column).getWidth();
                Presentation presentation = prodPed.getPresentation();
                String stPres = "";
                if (presentation != null) {
                    stPres = " (" + presentation.getName() + ")";
                }
                String termino = prodPed.getTermino();
                String stTerm = "";
                if (termino != null && !termino.trim().isEmpty()) {
                    stTerm = " [" + prodPed.getTermino() + "]";
                }

                boolean entry = prodPed.isEntry();
                String stEntry = "";
                if (entry) {
                    stEntry = " [" + "ENTRADA" + "]";
                }

                boolean delivery = prodPed.isDelivery();
                String stDelivery = "";
                if (delivery) {
                    stDelivery = " [" + "LLEVAR" + "]";
                }

                switch (prodPed.getStatus()) {
                    case ProductoPed.ST_SENDED:
                    case ProductoPed.ST_SENDED_MOD:
                        lbIconSended.setIcon(iconSend1);
                        break;
                    case ProductoPed.ST_MOD_ADD_CANT:
                    case ProductoPed.ST_NEW_ADD:
                        lbIconSended.setIcon(iconPlus);
                        break;
                    case ProductoPed.ST_MOD_MIN_CANT:
                        lbIconSended.setIcon(iconMinus);
                        break;
                    case ProductoPed.ST_AVOIDED:
                        lbIconSended.setIcon(iconCancel);
                        labelName.setFont(fTitleAvoid);
                        labelName.setForeground(Color.MAGENTA);
                        break;
                    default:
                        lbIconSended.setIcon(null);
                }

                lbIconSended.setVisible(iconPainted);

                //                String stExclusion = prodPed.getStExclusiones();
                String color = prodPed.getStatus() == ProductoPed.ST_MOD_ADD_CANT ? "#ff2345" : "#000";
                if (prodPed.getStatus() == ProductoPed.ST_NEW_ADD) {
                    color = "#c54e00";
                }
                labelName.setText(("<html><p><font color=" + color + ">" + prodPed.getProduct().getName()
                        + "</font></p><font size=2 color=blue>"
                        + stPres
                        + stTerm
                        + "<font color=#fe3917>" + stEntry + "</font>"
                        + "<font color=#feaa17>" + stDelivery + "</font>"
                        + "</font></html>").toUpperCase());

                int height = 0;
                if (prodPed.hasAdditionals()) {
                    labelAdicion.setVisible(true);
                    String stAdic = "<html><p>";
                    for (int i = 0; i < prodPed.getAdicionales().size(); i++) {
                        AdditionalPed adicional = prodPed.getAdicionales().get(i);
                        stAdic += "<font color=blue>" + adicional.toString() + "</font>";
                        stAdic += ((i != prodPed.getAdicionales().size() - 1 ? "<br>" : ""));
                        height += 7;
                    }
                    stAdic += "</p></html>";

                    labelAdicion.setText(stAdic);
                    labelAdicion.setPreferredSize(new Dimension(width, height));
                } else {
                    labelAdicion.setVisible(false);
                }
                int height2 = 0;
                if (prodPed.hasExcluisones()) {
                    labelIngredients.setVisible(true);
                    String stExc = "<html><p style=width:" + width + "px;>";
                    for (int i = 0; i < prodPed.getExclusiones().size(); i++) {
                        Ingredient ingred = prodPed.getExclusiones().get(i);
                        stExc += "<font color=red>s/" + ingred.getName() + "</font>";
                        stExc += ((i != prodPed.getExclusiones().size() - 1 ? "<br>" : ""));
                        height2 += 7;
                    }
                    stExc += "</p></html>";
                    labelIngredients.setText(stExc);
                    labelIngredients.setPreferredSize(new Dimension(width, height2));
                } else {
                    labelIngredients.setVisible(false);
                }
//                
//                setSize(table.getColumnModel().getColumn(column).getWidth(),
//            Short.MAX_VALUE);

                table.setRowHeight(row, getPreferredSize().height + 10 + height + height2);

                labelEsp.setText(prodPed.getEspecificaciones());

            } catch (Exception e) {
            }
        }

        if (isSelected) {
            setBackground(table.getSelectionBackground());
//            if (hasFocus) {
//                setBorder(BorderFactory.createLineBorder(Color.darkGray));
//            } else {
//                setBorder(createLineBorder(Color.lightGray));
//            }
        } else {
            setBackground(row % 2 == 0 ? table.getBackground() : UIManager.getColor("Table.alternateRowColor"));
//            setForeground(Color.black);
            setBorder(UIManager.getBorder("Table.cellBorder"));
        }

        boolean mark = false;
        try {
            if (table.getValueAt(row, 0) instanceof Boolean) {
                mark = Boolean.valueOf(table.getValueAt(row, 0).toString());
            }
        } catch (Exception e) {
        }

        if (marked && mark) {
            if (selectionBackgroundColor != null) {
                setBackground(selectionBackgroundColor);
            }
        }

        return this;
    }

    @Override
    public void setBackground(Color bg) {
        super.setBackground(bg); //To change body of generated methods, choose Tools | Templates.
        labelName.setBackground(bg);
        labelAdicion.setBackground(bg);
        labelIngredients.setBackground(bg);
    }

}
