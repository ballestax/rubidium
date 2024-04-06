/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor. 
 */

package com.rb.gui;


import com.rb.Aplication;
import com.rb.domain.Rol;
import com.rb.domain.User;
import com.rb.persistence.JDBC.JDBCUserDAO;
import com.rb.persistence.dao.DAOException;
import com.rb.persistence.dao.DAOFactory;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import org.dz.PanelCaptura;
import org.dz.Registro;


/**
 *
 * @author EL_FUETE
 */
public class GuiPanelNewUser extends PanelCaptura{
    private Registro regNombre;
    private Registro regPass;
    private Registro regConfirmPass;
    private Aplication app;
    private JLabel lbImage;
    private String nomBtns[] = {"Aceptar", "Cancelar"};
    private JButton[] btns = new JButton[nomBtns.length];
    private Font fnt = new Font("arial", 1, 15);
    private Font fnt2 = new Font("arial", 0, 18);
    private JPasswordField pfPass;
    private JPasswordField pfPassConfirm;
    private JLabel labelMessage;

    public GuiPanelNewUser(Aplication app) {
        this.app = app;
        initComponents();
    }

    private void initComponents() {

        setLayout(new GridBagLayout());

        pfPass = new JPasswordField();
        pfPassConfirm = new JPasswordField();
        regNombre = new Registro(BoxLayout.Y_AXIS, "Usuario:", "");
        regPass = new Registro(BoxLayout.Y_AXIS, "Contraseña:", pfPass);
        regConfirmPass = new Registro(BoxLayout.Y_AXIS, "Verificar Contraseña:", pfPassConfirm);
        lbImage = new JLabel(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons()+"security.png", 60, 60)));
        for (int i = 0; i < nomBtns.length; i++) {
            btns[i] = new JButton(nomBtns[i]);
            btns[i].setFont(fnt);
//            btns[i].setSize(100, 30);
//            btns[i].setLocation(40 + (i * 120), 190);
            btns[i].addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    procesarEvento(e);
                }
            });

        }

        labelMessage = new JLabel();
        labelMessage.setText("");
        labelMessage.setForeground(Color.red);

         add(lbImage, new GridBagConstraints(0, 1, 2, 4, 0.1, 0.1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 40, 40));
        add(regNombre, new GridBagConstraints(3, 1, 2, 1, 0.1, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(15, 2, 2, 7), 100, 16));
        add(regPass, new GridBagConstraints(3, 2, 2, 1, 0.1, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 7), 100, 16));
        add(regConfirmPass, new GridBagConstraints(3, 3, 2, 1, 0.1, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 7), 100, 16));
        add(labelMessage, new GridBagConstraints(1, 5, 5, 1, 1, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 7), 100, 16));

        add(btns[1], new GridBagConstraints(0, 6, 2, 1, 0.1, 0.0, GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL, new Insets(7, 2, 2, 7), 0, 0));
        add(btns[0], new GridBagConstraints(3, 6, 2, 1, 0.1, 0.0, GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL, new Insets(7, 2, 2, 7), 0, 0));
    }

    private void procesarEvento(ActionEvent e) {
        if (e.getSource().equals(btns[0])) {
            JDBCUserDAO userDAO;
            boolean valido = true;
            if (!comprobarPass()) {
                valido = false;
                showMessage("Las contraseñas no coinciden");
                regPass.setBorderToError();
                regConfirmPass.setBorderToError();
            }
            if (regNombre.getText().isEmpty() || regNombre.getText().length() < 4) {
                valido = false;
                showMessage("Nombre demasiado corto (minimo 4 caracteres).");
                regNombre.setBorderToError();
            }
            if (pfPass.getPassword().length < 4) {
                valido = false;
                showMessage("Contraseña muy corto (minimo 4 caracteres).");
                regPass.setBorderToError();
                regConfirmPass.setBorderToError();
            }
            try {
                if (valido) {
                    userDAO = (JDBCUserDAO) DAOFactory.getInstance().getUserDAO();
                    userDAO.init();
                    userDAO.addUser(regNombre.getText(), String.valueOf(pfPass.getPassword()), User.AccessLevel.ADMIN);
                    
                    int maxIDTabla = app.getControl().getMaxIDTabla("users");
                    User user = new User(regNombre.getText());
                    user.setId(maxIDTabla);
                    app.setUser(user); // Pasar el usuario al app
                    Rol rol = app.getControl().getRol("admin");
                    app.getControl().addRoleUser(user, rol);
                    getRootPane().getParent().setVisible(false);
                }
            } catch (DAOException ex) {
                Logger.getLogger(GuiPanelNewUser.class.getName()).log(Level.SEVERE, null, ex);
            } catch (RemoteException ex) {
                Logger.getLogger(GuiPanelNewUser.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        if (e.getSource().equals(btns[1])) {
            System.exit(0);
        }
    }

    public boolean comprobarPass() {
        char[] password = pfPass.getPassword();
        char[] password1 = pfPassConfirm.getPassword();
        // checks for same array reference
        if (password == password1) {
            return true;
        }
        // checks for null arrays
        if (password == null || password1 == null) {
            return false;
        }
        int length = password.length;
        // arrays should be of equal length
        if (password1.length != length) {
            return false;
        }
        // compare array values
        for (int i = 0; i < length; i++) {
            if (password[i] != password1[i]) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void reset() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void showMessage(String message) {
        labelMessage.setText(message);
    }
    
}
