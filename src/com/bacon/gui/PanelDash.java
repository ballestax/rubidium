/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bacon.gui;


import com.bacon.Aplication;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import org.balx.Imagenes;

/**
 *
 * @author hp
 */
public class PanelDash extends JPanel {

    private Aplication app;
    private final Image imagen;
    private final BufferedImage imgMas;
    private final int wl;
    private final int hl;

    public PanelDash(Aplication app) {
        this.app = app;
        setLayout(new GridBagLayout());
//        Font f1 = new Font("Tahoma", 1, 18);
//        Image centrarTexto = org.balx.Imagenes.centrarTexto(WC, WC, "DERO CAMPAÑA ELECTORAL", f1, Color.white, Color.blue);
//        BufferedImage textoImagen = ImagenesFx.textoImagen("DERO\nCAMPAÑA", WC, WC, Color.LIGHT_GRAY, Color.white, new Font("Tahoma", 1, 18));
//        imagen = centrarTexto;//ImagenesFx.rotarImagen(org.bx.Imagenes.toBuffereredImage(centrarTexto), -45);
        imagen = Imagenes.imagenToGray(app.getImageBC(), "");
        Image tmp = app.getImgManager().getImagen(app.getFolderIcons()+"logo.png");
        imgMas = org.bx.Imagenes.toBuffereredImage(tmp);
        tmp = org.dzur.ImagenesFx.escarlarImagen(imgMas, 80, 30, true);
        
        wl = imgMas.getWidth(this);
        hl = imgMas.getHeight(this);

//        add(newButton(app.getAction(Aplication.ACTION_NEW_LIDER), "img/lider_hl.png"), constrains(1, 1, 50));
////        add(newButton(app.getAction(Aplication.ACTION_NEW_LIDER), "img/lider_hl.png"), constrains(2, 1, 50));
////        add(newButton(app.getAction(Aplication.ACTION_SHOW_WITNESS), "img/witness_h.png"), constrains(3, 1, 50));
////        add(newButton(app.getAction(Aplication.ACTION_SHOW_JURY), "img/jury_hl.png"), constrains(4, 1, 50));
//        add(newButton(app.getAction(Aplication.ACTION_SHOW_REPORTS), "img/grafica_hl.png"), constrains(2, 1, 50));
//        add(newButton(app.getAction(Aplication.ACTION_SHOW_PUBLICITY), "img/warning.png"), constrains(3,1, 50));
//        add(newButton(app.getAction(Aplication.ACTION_SHOW_MAP), "img/map_marker.png"), constrains(4, 1, 50));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Rectangle bounds = getBounds();
        Graphics2D g2 = (Graphics2D) g;
        int WC = Aplication.WC;
        int cx = (bounds.width / WC) + 1;
        int cy = (bounds.height / WC) + 1;
        for (int i = 0; i < cx; i++) {
            for (int j = 0; j < cy; j++) {
                if (i % 2 == 0) {
                    if (j % 2 != 0) {
                        g2.drawImage(imagen, i * WC - 10, j * WC - 10, null);
                    }
                } else if (j % 2 == 0) {
                    g2.drawImage(imagen, i * WC - 10, j * WC - 10, null);
                }
            }
        }
        if (Aplication.isMessaged()) {
            int px = bounds.width / 2 - wl / 2;
            int py = bounds.height / 2 - hl / 2;

//          String logo = "";
//        BufferedImage logoImage = org.dzur.ImagenesFx.textoImagen(logo, wl, hl, Color.blue, new Color(255,255,255,50), new Font("Tahoma",1,42));
//            if(imgMas!=null)
////            g2.drawImage(imgMas, px, py, this);
        }
    } ///*/

    private JButton newButton(AbstractAction action, String imageSt) {
        final JButton bt1 = new JButton();
        bt1.setBorderPainted(false);
        bt1.setPreferredSize(new Dimension(50, 50));
        bt1.setFocusPainted(false);
        bt1.setContentAreaFilled(false);
        bt1.setFont(new Font("tahoma", 1, 16));
        bt1.setRolloverIcon(new ImageIcon(app.getImgManager().getImagen(imageSt, 100, 100)));
        bt1.setVerticalTextPosition(SwingConstants.BOTTOM);
        bt1.setHorizontalTextPosition(SwingConstants.CENTER);
        bt1.setAction(action);
        bt1.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e); //To change body of generated methods, choose Tools | Templates.
                bt1.setForeground(Color.red);
                bt1.setFont(new Font("tahoma", 1, 18));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e); //To change body of generated methods, choose Tools | Templates.
                bt1.setForeground(Color.black);
                bt1.setFont(new Font("tahoma", 1, 16));
            }

        });

        bt1.addFocusListener(new FocusAdapter() {

            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e); //To change body of generated methods, choose Tools | Templates.
                bt1.setForeground(Color.black);
                bt1.setFont(new Font("tahoma", 1, 16));
            }

        });
        return bt1;
    }

    private GridBagConstraints constrains(int f, int c, int sep) {
        GridBagConstraints constrains = new GridBagConstraints();
        constrains.gridx = f;
        constrains.gridy = c;
        constrains.weightx = 0.0;
        constrains.weighty = 0.0;
        constrains.insets = new Insets(sep, sep, sep, sep);
        constrains.ipadx = 100;
        constrains.ipady = 100;
        constrains.fill = GridBagConstraints.NONE;
        constrains.anchor = GridBagConstraints.CENTER;
        return constrains;
    }

}
