/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bacon.gui;


import com.bacon.Aplication;
import com.bacon.domain.User;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 *
 * @author Ballestax
 */
public class PanelAccess extends JPanel{

    private JTextField usuario;
    private JPasswordField contraseña;
    private JLabel mensajes;
    private String nomBtns[] = {"Aceptar", "Cancelar"};
    private JButton[] btns = new JButton[nomBtns.length];
    private Font fnt = new Font("arial", 1, 15);
    private Font fnt2 = new Font("arial", 0, 20);
    private Aplication app;
    private JLabel labelImg;
    private com.bacon.gui.util.Registro regHost;
    private ActionListener listener;
    private JTextField host;
    private org.bx.Registro regUser;
    private org.bx.Registro regPassword;
    private PropertyChangeSupport pcs;

    public PanelAccess(Aplication app) {
        this(app, null);
    }

    public PanelAccess(Aplication app, ActionListener listener) {
        this.app = app;
        this.listener = listener;
        this.pcs = new PropertyChangeSupport(this);
        setSize(400, 300);
        crearComponentes();
        setLayout(new GridBagLayout());

//        JLabel lbMsg = new JLabel("<html><font color=blue>Digite su usuario y contraseña para ingresar al sistema</font></html>");

        add(labelImg, new GridBagConstraints(0, 1, 2, 2, 0.1, 0.1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 40, 40));
        add(regUser, new GridBagConstraints(3, 1, 2, 1, 0.1, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(15, 2, 2, 7), 100, 16));
        add(regPassword, new GridBagConstraints(3, 2, 2, 1, 0.1, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 7), 100, 16));

//        add(regHost, new GridBagConstraints(3, 3, 2, 1, 0.1, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 7), 100, 16));
//        Box box = new Box(BoxLayout.X_AXIS);
//        box.add(Box.createHorizontalStrut(15));
//        box.add(btns[0]);
//        box.add(Box.createHorizontalStrut(5));
//        box.add(btns[1]);
//        box.add(Box.createHorizontalStrut(15));
//        add(box, new GridBagConstraints(1, 4, 6, 1, 1.0, 1.0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(7, 2, 2, 7), 0, 0));
        add(btns[1], new GridBagConstraints(0, 5, 2, 1, 0.1, 0.0, GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL, new Insets(7, 15, 7, 7), 0, 0));
        add(btns[0], new GridBagConstraints(3, 5, 2, 1, 0.1, 0.0, GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL, new Insets(7, 5, 7, 15), 0, 0));

        setBackground(new Color(0, 0, 0, 0));
    
    }

    private void crearComponentes() {
        usuario = new JTextField();
        usuario.setSize(170, 21);
        usuario.setFont(fnt);
        regUser = new org.bx.Registro(BoxLayout.Y_AXIS, "Usuario:", usuario);

        contraseña = new JPasswordField();
        contraseña.setFont(fnt2);
        contraseña.setSize(170, 21);
        contraseña.setLocation(85, 90);
//        contraseña.setEchoChar('*');
        regPassword = new org.bx.Registro(BoxLayout.Y_AXIS, "Contraseña:", contraseña);
        contraseña.addKeyListener(new KeyListener() {

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    procesarEvento(e);
                }
            }

            @Override
            public void keyTyped(KeyEvent e) {
            }
        });

        host = new JTextField();
        host.setSize(170, 21);
        host.setFont(fnt);
        regHost = new com.bacon.gui.util.Registro(BoxLayout.Y_AXIS, "Host:", host);

        labelImg = new JLabel(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "security.png", 90, 90)));

        mensajes = new JLabel("MENSAJES");
        mensajes.setSize(200, 50);
        mensajes.setLocation(50, 130);
        mensajes.setFont(fnt);
        mensajes.setBackground(Color.orange);

        for (int i = 0; i < btns.length; i++) {
            btns[i] = new JButton(nomBtns[i]);
            btns[i].setFont(fnt);
            btns[i].setSize(100, 30);
            btns[i].setLocation(40 + (i * 120), 190);
            btns[i].addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    procesarEvento(e);
                }
            });
        }
    }

    private void procesarEvento(ActionEvent e) {
        if (e.getSource().equals(btns[0])) {
            if (comprobarUsuario(usuario.getText(), contraseña.getPassword())) {
                getRootPane().getParent().setVisible(false);                
                if (listener != null) {                
                    listener.actionPerformed(new ActionEvent(this, 1010, "AC_CONTINUE"));
                    
                }
            } else {
                String msg = "Datos incorrectos";
                JOptionPane.showMessageDialog(this.getParent(), msg, "Error", JOptionPane.ERROR_MESSAGE);
                contraseña.requestFocus();
                contraseña.selectAll();
            }
        }
        if (e.getSource().equals(btns[1])) {
            if (listener == null) {
                System.exit(0);
            } else {
                listener.actionPerformed(new ActionEvent(this, 1010, "AC_CANCELAR"));
            }
        }
    }

    private void procesarEvento(KeyEvent e) {
        if (comprobarUsuario(usuario.getText(), contraseña.getPassword())) {
            getRootPane().getParent().setVisible(false);
            
        } else {
            String msg = "Datos incorrectos";
            JOptionPane.showMessageDialog(this.getParent(), msg, "Error", JOptionPane.ERROR_MESSAGE);
            contraseña.requestFocus();
            contraseña.selectAll();
        }
    }

    private boolean comprobarUsuario(String user, char[] contra) {
        if (user.length() == 0 || user == null) {
            return false;
        }
        User user_ = app.getControl().verifyUser(user, contra);
        if (user_ != null) {
            app.setUser(user_);
            pcs.firePropertyChange(Aplication.ACTION_LOGGIN, null, user_);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        if(pcs !=null &&listener!=null)
        pcs.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        if(pcs !=null &&listener!=null)
        pcs.removePropertyChangeListener(listener);
    }
        
}
