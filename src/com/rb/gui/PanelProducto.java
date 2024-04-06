/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rb.gui;

import com.rb.Aplication;
import com.rb.domain.Product;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

/**
 *
 * @author lrod
 */
public class PanelProducto extends JComponent implements ActionListener, MouseListener, PropertyChangeListener {

    private Product product;
    private PropertyChangeSupport pcs;
    private final Aplication app;

    public PanelProducto(Aplication app, Product product) {
        this.app = app;
        this.product = product;
        pcs = new PropertyChangeSupport(this);
        initComponents();
    }

    private void initComponents() {

        setLayout(new GridBagLayout());

        Font font = new Font("Tahoma", 1, 16);
        Font font2 = new Font("Serif", 2, 12);
        Font font3 = new Font("Sans", 1, 16);

        JLabel lbName = new JLabel(product.getName().toUpperCase());
        lbName.setForeground(Color.BLUE.darker().darker());
        lbName.setFont(font);
//        lbName.setBorder(BorderFactory.);

//        JTextArea ta = new JTextArea();
////        ta.setWrapStyleWord(true);
//        ta.setFont(font2);
//        ta.setEditable(false);
//        ta.setEnabled(false);
//        ta.setLineWrap(true);
//        ta.setText(product.getDescription());

        JLabel lbDescription = new JLabel();
        lbDescription.setText("<html><p>"+product.getDescription()+"</p></html>");
        lbDescription.setForeground(Color.darkGray);
        lbDescription.setFont(font2);

        NumberFormat NF = DecimalFormat.getCurrencyInstance();
        NF.setMaximumFractionDigits(0);
        JLabel lbPrice = new JLabel(NF.format(product.getPrice()));
        lbPrice.setFont(font3);
//        lbPrice.setOpaque(true);
        lbPrice.setForeground(Color.red.darker());

        JLabel lbImage = new JLabel();
        lbImage.setHorizontalAlignment(SwingConstants.CENTER);
//        lbImage.setBackground(Color.red);
        lbImage.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        lbImage.setPreferredSize(new Dimension(100, 100));
        lbImage.setMaximumSize(new Dimension(100, 100));

        JButton btAdd = new JButton();
        btAdd.setActionCommand("AC_ADD_QUICK");

        btAdd.setMargin(new Insets(2, 2, 2, 2));
        btAdd.setFocusPainted(false);
        btAdd.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "add1.png", 18, 18)));
        btAdd.addActionListener(this);

        ImageIcon icon = new ImageIcon(app.getImgManager().getImagen(product.getImage(), 100, 100));
        lbImage.setIcon(icon);

        add(lbName, new GridBagConstraints(0, 0, 4, 1, 0.1, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 1, 1));
        add(lbImage, new GridBagConstraints(5, 0, 3, 5, 0.1, 0.5, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 1, 1));
        add(lbDescription, new GridBagConstraints(0, 2, 4, 2, 0.0, 0.1, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 1, 1));
        add(lbPrice, new GridBagConstraints(0, 4, 4, 1, 0.1, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 1, 1));
        add(btAdd, new GridBagConstraints(8, 0, 1, 1, 0.0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 1, 1));
//        add(btAdd, new GridBagConstraints(8, 2, 1, 1, 0.0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 1, 1));

        setBorder(BorderFactory.createLineBorder(Color.orange.darker(), 1, true));

        addMouseListener(this);

    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }

}
