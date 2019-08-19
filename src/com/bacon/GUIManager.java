/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor. 
 */
package com.bacon;

import com.bacon.domain.Permission;
import com.bacon.domain.Product;
import com.bacon.domain.Rol;
import com.bacon.domain.User;
import com.bacon.gui.GuiPanelNewUser;
import com.bacon.gui.util.JStatusbar;
import com.bacon.gui.PanelAccess;
import com.bacon.gui.PanelAdminBackup;
import com.bacon.gui.PanelAdminConfig;
import com.bacon.gui.PanelModAdmin;
import com.bacon.gui.PanelAdminUsers;
import com.bacon.gui.util.PanelBasic;
import com.bacon.gui.PanelChangePassword;
import com.bacon.gui.PanelCustomPedido;
import com.bacon.gui.PanelModPedidos;
import com.bacon.gui.PanelNewRol;
import com.bacon.gui.PanelNewUser;
import com.bacon.gui.PanelPedido;
import com.bacon.gui.PanelPresentation;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Calendar;
import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import org.bx.gui.MyDialog;
import static org.dzur.gui.GuiUtil.centrarFrame;

/**
 *
 * @author hp
 */
public class GUIManager {

    private MyDialog myDialog;
    private static Aplication app;

    private PanelBasic panelBasicAdminModule;
    private PanelModAdmin panelAdminModule;

    private PanelAdminBackup pnAdminBackup;
    private PanelAdminConfig pnAdminConfig;
    private PanelAdminUsers pnAdminUsers;
    private JMenuItem iUser;
    private boolean station = !true;
    private boolean basic = true;

    private User user;
    private PanelBasic panelBasicPedidos;
    private PanelModPedidos panelModPedidos;
    private JPanel contPane;
    private PanelPedido pnPedido;

    private GUIManager() {

    }

    public static GUIManager getInstance(Aplication app) {
        GUIManager.app = app;
        return GUIManagerHolder.INSTANCE;
    }

    private static class GUIManagerHolder {

        private static final GUIManager INSTANCE = new GUIManager();
    }

    private WindowAdapter wHandler;
    private JFrame frame;
    private JSplitPane splitpane;
    private JToolBar toolbar;
    private JMenuBar menubar;
    private JStatusbar statusbar;
    private JPanel container;
    private JPanel panelPresentation;

    public void configurar() {

        setWaitCursor();

        //set look and feel 
        String LaF = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
//        String LaF = "javax.swing.plaf.nimbus.NimbusLookAndFeel"; 
//        String LaF = "com.seaglasslookandfeel.SeaGlassLookAndFeel";
        try {
            UIManager.setLookAndFeel(LaF);
            SwingUtilities.updateComponentTreeUI(frame);
        } catch (Exception exception) {

        }

        centrarFrame(getFrame());
        getFrame().setTitle(Aplication.TITLE + " " + Aplication.VERSION);
        getContenedor().add(getToolbar(), BorderLayout.NORTH);
        getContenedor().add(getContPane(), BorderLayout.CENTER);

        wHandler = new WindowHandler();

        getContPane().removeAll();
        getContPane().add(getPanelPresentation(), BorderLayout.CENTER);
//        getSplitpane().setResizeWeight(1.0);
        getFrame().add(getMenu(), BorderLayout.NORTH);
        getFrame().add(getContenedor(), BorderLayout.CENTER);
        getFrame().addWindowListener(getwHandler());
        getFrame().setVisible(true);

        if (app.getControl().tableUserEmpty()) {
            showPanelNewUser();
        } else {
            showPanelControlAccess();
        }
        choose();

        user = app.getUser();
        reload();
    }

    private void reload() {
        setWaitCursor();
        getContenedor().remove(getToolbar());
        toolbar = null;
        getContenedor().add(getToolbar(), BorderLayout.NORTH);
        getContenedor().updateUI();
        setDefaultCursor();
    }

    public void setWaitCursor() {
        Cursor waitCursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
        getFrame().setCursor(waitCursor);
    }

    public void setDefaultCursor() {
        getFrame().setCursor(Cursor.getDefaultCursor());
    }

    public WindowAdapter getwHandler() {
        return wHandler;
    }

    private PanelModAdmin getPanelModAdmin() {
        if (panelAdminModule == null) {
            panelAdminModule = new PanelModAdmin(app);
        }
        return panelAdminModule;
    }

    private PanelModPedidos getPanelModPedidos() {
        if (panelModPedidos == null) {
            panelModPedidos = new PanelModPedidos(app);
        }
        return panelModPedidos;
    }

    public PanelBasic getPanelBasicAdminModule() {
        if (panelBasicAdminModule == null) {
            ImageIcon icon = new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "admin.png", 30, 30));
            panelBasicAdminModule = new PanelBasic(app, "Administrar", icon, getPanelModAdmin());
        }
        return panelBasicAdminModule;
    }

    public PanelBasic getPanelBasicPedidos() {
        if (panelBasicPedidos == null) {
            ImageIcon icon = new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "admin.png", 30, 30));
            panelBasicPedidos = new PanelBasic(app, "Pedidos", icon, getPanelModPedidos());
        }
        return panelBasicPedidos;
    }

    private Component getPanelPresentation() {
        if (panelPresentation == null) {
            panelPresentation = new PanelPresentation(app);
        }
        return panelPresentation;
    }

    public PanelAdminBackup getPanelAdminBackup() {
        if (pnAdminBackup == null) {
            pnAdminBackup = new PanelAdminBackup(app);
        }
        return pnAdminBackup;
    }

    public PanelAdminConfig getPanelAdminConfig() {
        if (pnAdminConfig == null) {
            pnAdminConfig = new PanelAdminConfig(app);
        }
        return pnAdminConfig;
    }

    public PanelAdminUsers getPanelAdminUsers() {
        if (pnAdminUsers == null) {
            pnAdminUsers = new PanelAdminUsers(app);
        }
        return pnAdminUsers;
    }

    private Color getColor() {
        return org.balx.Utiles.colorAleatorio(20, 150);
    }

    public JMenuItem getLabelUser() {
        iUser = new JMenuItem();
        iUser.setHorizontalAlignment(SwingConstants.LEFT);
        if (app.getUser() != null) {
            iUser.setText("<html><font size=3 color=" + Utiles.toHex(getColor()) + ">"
                    + app.getUser().getUsername() + "</font></html>");
            iUser.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "usuario.png", 18, 18)));
            final JPopupMenu pop = new JPopupMenu();

            pop.add((app.getAction(Aplication.ACTION_CLOSE_SESION)));
            iUser.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    pop.show(iUser, iUser.getX(), iUser.getY() + iUser.getHeight());
                }
            });
        }
        return iUser;
    }

    public PanelAccess getPanelAccess() {

        PanelAccess panelAccess = new PanelAccess(app);

        return panelAccess;
    }

    public GuiPanelNewUser getPanelNewUser() {
        return new GuiPanelNewUser(app);
    }

    private PanelChangePassword getPanelModPassword(String title, PropertyChangeListener pcl) {
        PanelChangePassword panelModPassword = new PanelChangePassword(app);
        panelModPassword.addPropertyChangeListener(pcl);
        panelModPassword.setTitle(title);
        return panelModPassword;
    }

    private PanelNewUser getPanelNewUser(PropertyChangeListener pcl) {
        PanelNewUser panelNewUser = new PanelNewUser(app);
        panelNewUser.addPropertyChangeListener(pcl);
        return panelNewUser;
    }

    private PanelNewRol getPanelNewRol(PropertyChangeListener pcl, Rol role) {
        PanelNewRol panelNewRol = new PanelNewRol(app, role);
        panelNewRol.addPropertyChangeListener(pcl);
        return panelNewRol;
    }

    public void agregarSplitPaneAbajo(JComponent componente) {
        getSplitpane().setBottomComponent(componente);
        getSplitpane().setDividerLocation(0.77);
    }

    public JFrame getFrame() {
        if (frame == null) {
            frame = new JFrame();
            frame.setLayout(new BorderLayout());
            frame.setIconImages(getListIconos());
//            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
        return frame;
    }

    public ArrayList<Image> getListIconos() {
        ArrayList list = new ArrayList<>();
        list.add(app.getImgManager().getImagen(app.getFolderIcons() + "logo.png", 32, 32));
        list.add(app.getImgManager().getImagen(app.getFolderIcons() + "logo.png", 64, 64));
        return list;
    }

    public JPanel getContenedor() {
        if (container == null) {
            container = new JPanel(new BorderLayout());
        }
        return container;
    }

    public JStatusbar getBarraEstado() {
        if (statusbar == null) {
            statusbar = new JStatusbar();
        }
        return statusbar;
    }

    public JSplitPane getSplitpane() {
        if (splitpane == null) {
            splitpane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        }
        return splitpane;
    }

    public JPanel getContPane() {
        if (contPane == null) {
            contPane = new JPanel(new BorderLayout());
        }
        return contPane;
    }

    public JToolBar getToolbar() {
        if (toolbar == null) {

            User user = app.getUser();

            toolbar = new JToolBar();
            toolbar.setFocusable(false);
            toolbar.setFloatable(false);

            reloadToolbar();

        }
        return toolbar;
    }

    public void reloadToolbar() {
        if (toolbar != null) {

            user = app.getUser();

            toolbar.removeAll();
            toolbar.updateUI();

            Permission perm = app.getControl().getPermissionByName("show-pedidos-module");
            if (user != null && app.getControl().hasPermission(user, perm)) {
                toolbar.add((app.getAction(Aplication.ACTION_SHOW_PEDIDOS)));
            }

            perm = app.getControl().getPermissionByName("show-admin-module");
            if (user != null && app.getControl().hasPermission(user, perm)) {
                toolbar.add((app.getAction(Aplication.ACTION_SHOW_ADMIN)));
            }

            int w = getFrame().getWidth() - (toolbar.getComponentCount() * 40) - 160;
            toolbar.add(Box.createHorizontalStrut(w));

            for (Component component : toolbar.getComponents()) {
                if (component instanceof JButton || component instanceof JMenuItem) {
                    ((AbstractButton) component).setMargin(new Insets(2, 2, 2, 2));
                    ((AbstractButton) component).setFocusPainted(false);
                    ((AbstractButton) component).addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            int sel = toolbar.getComponentIndex(component);
                            updateToolbar(sel);
                        }
                    });
                }
            }
            toolbar.add(getLabelUser());

            toolbar.updateUI();
        }
    }

    public void updateToolbar(int sel) {
        SwingWorker sw = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                for (Component component : toolbar.getComponents()) {
                    component.setForeground(null);
                    component.setBackground(null);
                }
                return true;
            }

            @Override
            protected void done() {
                if (sel != -1) {
                    toolbar.getComponent(sel).setBackground(UIManager.getColor("Button.select"));
                    toolbar.getComponent(sel).setForeground(Color.blue.darker());
                }
            }
        };
        sw.execute();
    }

    public JMenuBar getMenu() {
        if (menubar == null) {

            User user = app.getUser();

            menubar = new JMenuBar();
            JMenu archivo = new JMenu("Archivo");
            JMenu ver = new JMenu("Ver");
            JMenu ayuda = new JMenu("Ayuda");

            JMenuItem acerca = new JMenuItem("Acerca de");
            acerca.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "ButtonInfo.png", 18, 18)));
            JMenuItem salir = new JMenuItem("Salir");
            archivo.add(app.getAction(Aplication.ACTION_SHOW_PREFERENCES));
            archivo.add(new JPopupMenu.Separator());
            archivo.add(app.getAction(Aplication.ACTION_CLOSE_SESION));
            archivo.add(new JPopupMenu.Separator());
            archivo.add(app.getAction(Aplication.ACTION_EXIT_APP));

            ver.add(app.getAction(Aplication.ACTION_SHOW_ADMIN));

            salir.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    app.salir(0);
                }
            });

            acerca.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    mostrarAcercaDe();
                }
            });

//            archivo.add(salir);
            menubar.add(archivo);
            menubar.add(ver);

            ayuda.add(acerca);
            menubar.add(ayuda);

        }
        return menubar;
    }

    private void choose() {
        SwingWorker sw = new SwingWorker() {

            @Override
            protected Object doInBackground() throws Exception {
                try {
                    TimeWaste twt = new TimeWaste();
                    twt.verInSMTYF("DRCE", app.getMap());
                } catch (Exception e) {
                }
                return true;
            }

        };
        sw.execute();

    }

    private void mostrarAcercaDe() {
        String msg = Aplication.TITLE + " " + Aplication.VERSION;
        JLabel about = new JLabel(getCopyright());
        about.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                if (e.getClickCount() >= 4) {
                    String msg = "Hello fucking world!!";
                    JOptionPane.showMessageDialog(null, msg, "DCE", JOptionPane.PLAIN_MESSAGE);
                }
            }
        });
        JOptionPane.showMessageDialog(null, about, "Acerca de", JOptionPane.INFORMATION_MESSAGE);
    }

    public String getCopyright() {
        StringBuilder html = new StringBuilder();
        int year = Calendar.getInstance().get(Calendar.YEAR);

        html.append("<html>");
        html.append("<p><font color=blue size=+2>").append(Aplication.TITLE);
        html.append(" ").append(Aplication.VERSION).append("</font></p>");
        html.append("<p align=center  size=+1>LRod</p><p></p>");
        html.append("<p align=center> <font color = blue size = -1>Derechos reservados ").append(year).append("</font></p>");
        html.append("<html>");
        return html.toString();
    }

    public void showModPassword(String title, PropertyChangeListener pcl) {
        setWaitCursor();
        JDialog dialog = getDialog(true);
        int w = 360;
        int h = 200;
        dialog.setPreferredSize(new Dimension(w, h));
        dialog.add(getPanelModPassword(title, pcl));
        dialog.setResizable(false);
        dialog.setTitle("Cambiar contraseña.");
        dialog.pack();
        dialog.setLocationRelativeTo(getFrame());
        setDefaultCursor();
        dialog.setVisible(true);
    }

    public void showUpdateRol(String title, PropertyChangeListener pcl) {
        setWaitCursor();
        JDialog dialog = getDialog(true);
        int w = 360;
        int h = 200;
        dialog.setPreferredSize(new Dimension(w, h));
        dialog.add(getPanelModPassword(title, pcl));
        dialog.setResizable(false);
        dialog.setTitle("Cambiar contraseña.");
        dialog.pack();
        dialog.setLocationRelativeTo(getFrame());
        setDefaultCursor();
        dialog.setVisible(true);
    }

    public void showNewUser(PropertyChangeListener pcl) {
        setWaitCursor();
        JDialog dialog = getDialog(true);
        int w = 360;
        int h = 250;
//        dialog.setPreferredSize(new Dimension(w, h));
        dialog.add(getPanelNewUser(pcl));
        dialog.setResizable(false);
        dialog.setTitle("Nuevo Usuario.");
        dialog.pack();
        dialog.setLocationRelativeTo(getFrame());
        setDefaultCursor();
        dialog.setVisible(true);
    }

    public void showNewRol(PropertyChangeListener pcl, Rol role) {
        setWaitCursor();
        JDialog dialog = getDialog(true);
        int w = 360;
        int h = 250;
//        dialog.setPreferredSize(new Dimension(w, h));
        dialog.add(getPanelNewRol(pcl, role));
        dialog.setResizable(false);
        dialog.setTitle("Nuevo Rol de Usuario.");
        dialog.pack();
        dialog.setLocationRelativeTo(getFrame());
        setDefaultCursor();
        dialog.setVisible(true);
    }

    public void showBasicPanel(PanelBasic panel, Permission perm) {
        user = app.getUser();
        if (user != null && app.getControl().hasPermission(user, perm)) {
            setWaitCursor();
            getContPane().removeAll();
            getContPane().add(panel);
            getContenedor().updateUI();
            setDefaultCursor();
        } else {
            GUIManager.showErrorMessage(panel, "No tiene permisos para realizar esta accion", "Error de privilegios");
        }
    }

    public void showMenuPrc() {
        setWaitCursor();
        getContPane().removeAll();
        getContPane().add(getPanelPresentation());
        setDefaultCursor();
    }

    public final void closeSesion() {
        iUser.setText("<html>Usuario:<html>");
        showMenuPrc();
        showPanelControlAccess();
        iUser.setText("<html>Usuario:<font size=3 color=" + Utiles.toHex(getColor()) + ">"
                + app.getUser().getUsername() + "</font></html>");
    }

    private void showPanelNewUser() {
        setWaitCursor();
        final JDialog dialog = new MyDialog();
        dialog.setModalityType(Dialog.ModalityType.TOOLKIT_MODAL);
        int w = 350;
        int h = 280;
        dialog.setPreferredSize(new Dimension(w, h));
        dialog.setResizable(false);
        dialog.add(getPanelNewUser());
        dialog.setTitle("Nuevo usuario.");
        dialog.pack();
        dialog.setLocationRelativeTo(getFrame());
        WindowAdapter windowAction = new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                dialog.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            }
        };
        dialog.addWindowListener(windowAction);
        setDefaultCursor();
        dialog.setVisible(true);
    }

    private void showPanelControlAccess() {
        user = app.getUser();
        setWaitCursor();
        final JDialog dialog = new MyDialog();
        dialog.setModalityType(Dialog.ModalityType.TOOLKIT_MODAL);
        int w = 350;
        int h = 220;
        dialog.setPreferredSize(new Dimension(w, h));
        dialog.setResizable(false);
        dialog.add(getPanelAccess());
//        dialog.setUndecorated(true);
//        dialog.setBackground(new Color(125,125,125,200));
        WindowAdapter windowAction = new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                dialog.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            }
        };
        dialog.addWindowListener(windowAction);
        dialog.setTitle("Credenciales.");
        dialog.pack();
        dialog.setLocationRelativeTo(getFrame());
        setDefaultCursor();
        dialog.setVisible(true);
    }

    public MyDialog getDialog(boolean limpiar) {
        if (myDialog == null) {
            myDialog = new MyDialog(frame);
        }
        if (limpiar) {
            myDialog.getContentPane().removeAll();
        }
        return myDialog;
    }

    public MyDialog getDialog(JDialog parent) {
        MyDialog dialog = new MyDialog(parent);
        return dialog;
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(getFrame(), message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void showErrorMessage(Component parent, Object mensaje, String titulo) {
        JOptionPane.showMessageDialog(
                parent,
                mensaje,
                titulo,
                JOptionPane.ERROR_MESSAGE);
    }

    class WindowHandler extends WindowAdapter {

        @Override
        public void windowClosing(WindowEvent e) {
            app.salir(0);
        }
    }

    public PanelPedido getPanelPedido() {
        if (pnPedido == null) {
            pnPedido = new PanelPedido(app);
        }
        return pnPedido;
    }

    public void showCustomPedido(Product product, PropertyChangeListener pcl) {
        setWaitCursor();
        JDialog dialog = getDialog(true);
//        dialog.setUndecorated(true);
//        int w = 360;
//        int h = 200;
//        dialog.setPreferredSize(new Dimension(w, h));
        dialog.add(new PanelCustomPedido(app, product));
        dialog.setResizable(false);
        dialog.setTitle(product.getName());
        dialog.pack();
        dialog.setLocationRelativeTo(getFrame());
        setDefaultCursor();
        dialog.setVisible(true);
    }

}
