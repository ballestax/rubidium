/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor. 
 */
package com.rb;

import com.rb.domain.Client;
import com.rb.domain.ConfigDB;
import com.rb.domain.Cycle;
import com.rb.domain.Invoice;
import com.rb.domain.Item;
import com.rb.domain.Order;
import com.rb.domain.Permission;
import com.rb.domain.Presentation;
import com.rb.domain.Product;
import com.rb.domain.ProductoPed;
import com.rb.domain.Rol;
import com.rb.domain.User;
import com.rb.gui.GuiPanelNewUser;
import com.rb.gui.GuiPanelSelProduct;
import com.rb.gui.util.JStatusbar;
import com.rb.gui.PanelAccess;
import com.rb.gui.PanelAddExtra;
import com.rb.gui.PanelAddItem;
import com.rb.gui.PanelAddProduct;
import com.rb.gui.PanelAdminBackup;
import com.rb.gui.PanelAdminConfig;
import com.rb.gui.PanelAdminTables;
import com.rb.gui.PanelModAdmin;
import com.rb.gui.PanelAdminUsers;
import com.rb.gui.PanelAdminWaiters;
import com.rb.gui.PanelCash;
import com.rb.gui.util.PanelBasic;
import com.rb.gui.PanelChangePassword;
import com.rb.gui.PanelClientCard;
import com.rb.gui.PanelConfigOthers;
import com.rb.gui.PanelConfigPrint;
import com.rb.gui.PanelConfigTicket;
import com.rb.gui.PanelConfirmPedido;
import com.rb.gui.PanelCustomPedido;
import com.rb.gui.PanelListPedidos;
import com.rb.gui.PanelModPedidos;
import com.rb.gui.PanelNewRol;
import com.rb.gui.PanelNewUser;
import com.rb.gui.PanelPedido;
import com.rb.gui.PanelDash;
import com.rb.gui.PanelDownItem;
import com.rb.gui.PanelInventory;
import com.rb.gui.PanelInvoicedOrder;
import com.rb.gui.PanelList;
import com.rb.gui.PanelNewConciliacion;
import com.rb.gui.PanelNewCycle;
import com.rb.gui.PanelNewLocation;
import com.rb.gui.PanelOrderList;
import com.rb.gui.PanelOrders;
import com.rb.gui.PanelOtherProduct;
import com.rb.gui.PanelPayInvoice;
import com.rb.gui.PanelPressProduct;
import com.rb.gui.PanelProducts;
import com.rb.gui.PanelQuickSearch;
import com.rb.gui.PanelReportSales;
import com.rb.gui.PanelSelCategory;
import com.rb.gui.PanelSelItem;
import com.rb.gui.PanelSelProducts;
import com.rb.gui.PanelSnapShot;
import com.rb.gui.PanelTakeOrders;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Insets;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
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
import javax.swing.JRootPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import static org.dz.GuiUtil.centrarFrame;
import org.dz.MyDialogEsc;

/**
 *
 * @author hp
 */
public class GUIManager {

    private MyDialogEsc myDialog;
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
    private PanelBasic panelBasicOrders;
    private PanelModPedidos panelModPedidos;
    private JPanel contPane;
    private PanelPedido pnPedido;
    private PanelBasic panelBasicSalesList;
    private PanelListPedidos panelSalesList;
    private PanelConfigPrint pnConfigPrint;
    private PanelBasic panelBasicCash;
    private PanelBasic panelBasicReports;
    private PanelCash panelCash;
    private GuiPanelSelProduct panelSelProduct;
    private PanelAddProduct panelAddProduct;
    private PanelConfigOthers pnConfigOthers;
    private PanelOtherProduct panelOtherProduct;
    private PanelSelCategory panelSelCategory;
    private PanelReportSales panelReportSales;
    private PanelBasic panelBasicInventory;
    private PanelInventory panelInventory;
    private PanelAddItem panelAddItem;
    private PanelList<Object> pnNewList;
    private PanelSelItem panelSelItem;
    private PanelNewConciliacion panelNewConciliacion;
    private PanelNewLocation pnNewLocation;
    private PanelDownItem panelDownItem;
    private PanelConfigTicket pnConfigTicket;
    private PanelAddExtra panelAddExtra;
    private PanelPayInvoice panelPayInvoice;
    private PanelBasic panelBasicProducts;
    private PanelProducts panelProducts;
    private PanelSnapShot panelSnapshot;
    private PanelOrders pnOrders;
    private PanelBasic panelBasicPOS;
    private PanelTakeOrders panelTakeOrders;
    private PanelBasic panelBasicOrdersList;
    private PanelOrderList panelOrderList;
    private PanelSelProducts panelSelProducts;
    private PanelInvoicedOrder panelInvoicedOrder;
    private PanelAdminWaiters pnAdminWaiters;
    private PanelAdminTables pnAdminTables;

    private boolean showingInfo = false;
    private PanelQuickSearch panelQSearch;

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
    private PanelDash panelPresentation;
    

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

        UIDefaults defaults = UIManager.getLookAndFeelDefaults();
        if (defaults.get("Table.alternateRowColor") == null) {
            defaults.put("Table.alternateRowColor", new Color(240, 240, 240));
        }

        centrarFrame(getFrame());
        getFrame().setTitle(Aplication.TITLE + " " + Aplication.VERSION);
        ConfigDB config = app.getControl().getConfig("cf.showtoolbar");
        if (config != null && Boolean.getBoolean(config.getValor())) {
            getContenedor().add(getToolbar(), BorderLayout.NORTH);
            getFrame().add(getMenu(), BorderLayout.NORTH);
        }
        getContenedor().add(getContPane(), BorderLayout.CENTER);

        wHandler = new WindowHandler();

        getPanelPresentation().setVisible(false);

        getContPane().removeAll();
        getContPane().add(getPanelPresentation(), BorderLayout.CENTER);
//        getSplitpane().setResizeWeight(1.0);

//        config = app.getControl().getConfig("cf.showmenubar");
//        if (config != null && Boolean.getBoolean(config.getValor())) {
        getFrame().add(getMenu(), BorderLayout.NORTH);
//        }
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
        getPanelPresentation().setVisible(true);
//        ConfigDB config = app.getControl().getConfig("cf.showtoolbar");
//        if (config != null && Boolean.getBoolean(config.getValor())) {
        getToolbar().setVisible(true);
        getContenedor().add(getToolbar(), BorderLayout.NORTH);
//        }
        getContenedor().updateUI();
        getContPane().updateUI();
//        setToFullScreen();
        setDefaultCursor();
    }

    public void switchPanel() {
        getPanelModPedidos().switchPanel();
    }

    public void setToFullScreen() {
        GraphicsEnvironment lGrapEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice device = lGrapEnv.getDefaultScreenDevice();
        if (device.isFullScreenSupported()) {
            try {
//                getFrame().setUndecorated(true);
//                getFrame().setResizable(false);
                device.setFullScreenWindow(getFrame().getOwner());
            } catch (Exception e) {
                GUIManager.showErrorMessage(getFrame(), "Error al cambiar a modo fullscreen: " + e, "Error");
            } finally {
                device.setFullScreenWindow(null);
            }
        } else {
            GUIManager.showErrorMessage(getFrame(), "FullScreen mode no soportado", "Advertencia");
        }
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

    public PanelTakeOrders getPanelTakeOrders() {
        if (panelTakeOrders == null) {
            panelTakeOrders = new PanelTakeOrders(app);
        }
        return panelTakeOrders;
    }

    private PanelListPedidos getPanelSalesList() {
        if (panelSalesList == null) {
            panelSalesList = new PanelListPedidos(app);
            panelSalesList.addPropertyChangeListener(getPanelPedido());
        }
        return panelSalesList;
    }

    private PanelOrderList getPanelOrderslList() {
        if (panelOrderList == null) {
            panelOrderList = new PanelOrderList(app);

        }
        return panelOrderList;
    }

    private PanelCash getPanelCash(Cycle cycle) {
        if (panelCash == null) {
            panelCash = new PanelCash(app, cycle);
        }
        return panelCash;
    }

    private PanelQuickSearch getPanelQuickSearch() {
        if (panelQSearch == null) {
            panelQSearch = new PanelQuickSearch(app);
            
        }
        return panelQSearch;
    }

    private PanelProducts getPanelProducts() {
        if (panelProducts == null) {
            panelProducts = new PanelProducts(app);
        }
        return panelProducts;
    }

    private PanelReportSales getPanelReports() {
        if (panelReportSales == null) {
            panelReportSales = new PanelReportSales(app);
        }
        return panelReportSales;
    }

    private PanelInventory getPanelInventory() {
        if (panelInventory == null) {
            panelInventory = new PanelInventory(app);
        }
        return panelInventory;
    }

    public PanelSelProducts getPaneSelProducts() {
        if (panelSelProducts == null) {
            panelSelProducts = new PanelSelProducts(app);
        }
        return panelSelProducts;
    }

    public PanelBasic getPanelBasicAdminModule() {
        if (panelBasicAdminModule == null) {
            ImageIcon icon = new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "admin.png", 30, 30));
            panelBasicAdminModule = new PanelBasic(app, "Administrar", icon, getPanelModAdmin());
        }
        return panelBasicAdminModule;
    }

    public PanelBasic getPanelBasicOrders() {
        if (panelBasicOrders == null) {
            ImageIcon icon = new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "shop.png", 30, 30));
            panelBasicOrders = new PanelBasic(app, "Pedidos", icon, getPanelModPedidos());
        }
        return panelBasicOrders;
    }

    public PanelBasic getPanelBasicPOS() {
        if (panelBasicPOS == null) {
            ImageIcon icon = new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "monitor.png", 30, 30));
            panelBasicPOS = new PanelBasic(app, "POS", icon, getPanelTakeOrders());
        }
        return panelBasicPOS;
    }

    public PanelBasic getPanelBasicSalesList() {
        if (panelBasicSalesList == null) {
            ImageIcon icon = new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "sales-report.png", 30, 30));
            panelBasicSalesList = new PanelBasic(app, "Lista Ventas", icon, getPanelSalesList());
        }
        return panelBasicSalesList;
    }

    public PanelBasic getPanelBasicOrdersList() {
        if (panelBasicOrdersList == null) {
            ImageIcon icon = new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "ordering.png", 30, 30));
            panelBasicOrdersList = new PanelBasic(app, "Lista Pedidos", icon, getPanelOrderslList());
        }
        return panelBasicOrdersList;
    }

    public PanelBasic getPanelBasicCash() {
        if (panelBasicCash == null) {
            ImageIcon icon = new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "cash.png", 30, 30));
            panelBasicCash = new PanelBasic(app, "Caja", icon, getPanelCash(null));
        }
        return panelBasicCash;
    }

    public PanelBasic getPanelBasicProducts() {
        if (panelBasicProducts == null) {
            ImageIcon icon = new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "shopping-bag-purple.png", 30, 30));
            panelBasicProducts = new PanelBasic(app, "Productos", icon, getPanelProducts());
        }
        return panelBasicProducts;
    }

    public PanelBasic getPanelBasicReports() {
        if (panelBasicReports == null) {
            ImageIcon icon = new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "reports.png", 30, 30));
            panelBasicReports = new PanelBasic(app, "Reportes", icon, getPanelReports());
        }
        return panelBasicReports;
    }

    public PanelBasic getPanelBasicInventory() {
        if (panelBasicInventory == null) {
            ImageIcon icon = new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "Inventory-maintenance.png", 30, 30));
            panelBasicInventory = new PanelBasic(app, "Inventarios", icon, getPanelInventory());
        }
        return panelBasicInventory;
    }

    private PanelDash getPanelPresentation() {
        if (panelPresentation == null) {
            panelPresentation = new PanelDash(app);
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

    public PanelAdminWaiters getPanelAdminWaiters() {
        if (pnAdminWaiters == null) {
            pnAdminWaiters = new PanelAdminWaiters(app);
        }
        return pnAdminWaiters;
    }

    public PanelAdminTables getPanelAdminTables() {
        if (pnAdminTables == null) {
            pnAdminTables = new PanelAdminTables(app);
        }
        return pnAdminTables;
    }

    public GuiPanelSelProduct getPanelSelProduct() {
        if (panelSelProduct == null) {
            panelSelProduct = new GuiPanelSelProduct(app, null);
        }
        return panelSelProduct;
    }

    public PanelAddProduct getPanelAddProduct() {
        if (panelAddProduct == null) {
            panelAddProduct = new PanelAddProduct(app, null);
        }
        return panelAddProduct;
    }

    public PanelAddExtra getPanelAddExtra() {
        if (panelAddExtra == null) {
            panelAddExtra = new PanelAddExtra(app, null);
        }
        return panelAddExtra;
    }

    public PanelAddItem getPanelAddItem() {
        if (panelAddItem == null) {
            panelAddItem = new PanelAddItem(app);
        }
        return panelAddItem;
    }

    public PanelInvoicedOrder getPanelInvoicedOrder() {
        if (panelInvoicedOrder == null) {
            panelInvoicedOrder = new PanelInvoicedOrder(app);
        }
        return panelInvoicedOrder;
    }

    public PanelPayInvoice getPanelPayInvoice(Invoice inv) {
        if (panelPayInvoice == null) {
            panelPayInvoice = new PanelPayInvoice(app, inv);
        }
        return panelPayInvoice;
    }

    public PanelOtherProduct getPanelOtherProduct() {
        if (panelOtherProduct == null) {
            panelOtherProduct = new PanelOtherProduct(app, null);
        }
        return panelOtherProduct;
    }

    public PanelSelCategory getPanelSelCategory() {
        if (panelSelCategory == null) {
            panelSelCategory = new PanelSelCategory(app, null);
        }
        return panelSelCategory;
    }

    public PanelSnapShot getPanelSnapShot() {
        if (panelSnapshot == null) {
            panelSnapshot = new PanelSnapShot(app);
        }
        return panelSnapshot;
    }

    public PanelSelItem getPanelSelItem() {
        return getPanelSelItem(null);
    }

    public PanelSelItem getPanelSelItem(Item item) {
        if (panelSelItem == null) {
            panelSelItem = new PanelSelItem(app, null);
        }
        panelSelItem.reset();
        panelSelItem.setItem(item);
        return panelSelItem;
    }

    public PanelDownItem getPanelDownItem() {
        return getPanelDownItem(null);
    }

    public PanelDownItem getPanelDownItem(Item item) {
        if (panelDownItem == null) {
            panelDownItem = new PanelDownItem(app, null);
        }
        panelDownItem.reset();
        panelDownItem.setItem(item);
        return panelDownItem;
    }

    public PanelList getPanelNewList(String title, PropertyChangeListener listener, List lista) {
        pnNewList = new PanelList<>(app, title, listener, lista);
        return pnNewList;
    }

    public PanelNewConciliacion getPanelNewConciliacion(boolean reset) {
        if (panelNewConciliacion == null) {
            panelNewConciliacion = new PanelNewConciliacion(app);
        }
        if (reset) {
            panelNewConciliacion.reset();
        }
        return panelNewConciliacion;
    }

    public PanelNewLocation getPanelNewLocation() {
        if (pnNewLocation == null) {
            pnNewLocation = new PanelNewLocation(app);
        }
        return pnNewLocation;
    }

    private Color getColor() {
        return org.dz.Utiles.colorAleatorio(20, 150);
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

            KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
                @Override
                public boolean dispatchKeyEvent(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_F3) {
                        if (!showingInfo && app.getUser()!=null) {
                            showingInfo = true;
                            showPanelInfo();
                        }
                    }
                    return false;
                }
            });
//            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
        return frame;
    }

    public ArrayList<Image> getListIconos() {
        ArrayList list = new ArrayList<>();
        list.add(app.getImgManager().getImagen(app.getFolderIcons() + "logo-2.png", 32, 32));
        list.add(app.getImgManager().getImagen(app.getFolderIcons() + "logo-2.png", 64, 64));
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

            Permission perm = app.getControl().getPermissionByName("show-orders-module");
            if (user != null && app.getControl().hasPermission(user, perm)) {
                toolbar.add((app.getAction(Aplication.ACTION_SHOW_ORDER)));
            }

            perm = app.getControl().getPermissionByName("show-saleslist-module");
            if (user != null && app.getControl().hasPermission(user, perm)) {
                toolbar.add((app.getAction(Aplication.ACTION_SHOW_SALES_LIST)));
            }

            perm = app.getControl().getPermissionByName("show-orderlist-module");
            if (user != null && app.getControl().hasPermission(user, perm)) {
                toolbar.add((app.getAction(Aplication.ACTION_SHOW_ORDER_LIST)));
            }

            perm = app.getControl().getPermissionByName("show-cash-module");
            if (user != null && app.getControl().hasPermission(user, perm)) {
                toolbar.add((app.getAction(Aplication.ACTION_SHOW_CASH)));
            }

            perm = app.getControl().getPermissionByName("show-reports-module");
            if (user != null && app.getControl().hasPermission(user, perm)) {
                toolbar.add((app.getAction(Aplication.ACTION_SHOW_REPORTS)));
            }

            perm = app.getControl().getPermissionByName("show-inventory-module");
            if (user != null && perm != null && app.getControl().hasPermission(user, perm)) {
                toolbar.add((app.getAction(Aplication.ACTION_SHOW_INVENTORY)));
            }

            perm = app.getControl().getPermissionByName("show-products-module");
            if (user != null && app.getControl().hasPermission(user, perm)) {
                toolbar.add((app.getAction(Aplication.ACTION_SHOW_PRODUCTS)));
            }

            perm = app.getControl().getPermissionByName("show-pos-module");
            if (user != null && app.getControl().hasPermission(user, perm)) {
                toolbar.add((app.getAction(Aplication.ACTION_SHOW_POS)));
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
            acerca.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "information.png", 18, 18)));
            JMenuItem salir = new JMenuItem("Salir");
            archivo.add(app.getAction(Aplication.ACTION_SHOW_PREFERENCES));
            archivo.add(new JPopupMenu.Separator());
            archivo.add(app.getAction(Aplication.ACTION_CLOSE_SESION));
            archivo.add(new JPopupMenu.Separator());
            archivo.add(app.getAction(Aplication.ACTION_EXIT_APP));

           // ver.add(app.getAction(Aplication.ACTION_SHOW_ADMIN));

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
            //menubar.add(ver);

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

    public void showPanelInfo() {
        setWaitCursor();

        JDialog dialog = new MyDialogEsc(getFrame()) {
            @Override
            public void setVisible(boolean b) {
                super.setVisible(b); //To change body of generated methods, choose Tools | Templates.
                if (!b) {
                    showingInfo = false;
                }
            }
        };
        dialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setModal(true);
        int w = 800;
        int h = 500;
        dialog.setPreferredSize(new Dimension(w, h));

        getPanelQuickSearch().reset();
//        dialog.setResizable(false);
        dialog.add(getPanelQuickSearch());
        dialog.setTitle("Buscar ticket.");
        dialog.pack();
        dialog.setLocationRelativeTo(getFrame());
        setDefaultCursor();
        dialog.setVisible(true);
    }

    private void mostrarAcercaDe() {
        String msg = Aplication.TITLE + " " + Aplication.VERSION;
        JLabel about = new JLabel(getCopyright());
        about.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                if (e.getClickCount() >= 4) {
                    String msg = "Hello fucking world!!";
                    JOptionPane.showMessageDialog(null, msg, "13171x", JOptionPane.PLAIN_MESSAGE);
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
        dialog.setPreferredSize(null);
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
        dialog.setPreferredSize(null);
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
        getContPane().add(getPanelPresentation(), BorderLayout.CENTER);
        reloadToolbar();
        getContenedor().updateUI();
        setDefaultCursor();
    }

    public final void closeSesion() {
        iUser.setText("<html>Usuario:<html>");
        showMenuPrc();
        showPanelControlAccess();
        iUser.setText("<html>Usuario:<font size=3 color=" + Utiles.toHex(getColor()) + ">"
                + app.getUser().getUsername() + "</font></html>");
//        getPanelPresentation().setVisible(false);
//        getToolbar().setVisible(false);
//        getContPane().updateUI();
    }

    private void showPanelNewUser() {
        setWaitCursor();
        final JDialog dialog = new MyDialogEsc();
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
        final JDialog dialog = new JDialog();
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

    public MyDialogEsc getDialog(boolean limpiar) {
        if (myDialog == null) {
            myDialog = new MyDialogEsc(frame);
        }
        if (limpiar) {
            myDialog.getContentPane().removeAll();
        }
        return myDialog;
    }

    public MyDialogEsc getDialog(JDialog parent) {
        MyDialogEsc dialog = new MyDialogEsc(parent);
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

    public PanelOrders getPanelOrders() {
        if (pnOrders == null) {
            pnOrders = new PanelOrders(app);
        }
        return pnOrders;
    }

    public void showCustomPedido(Product product, PropertyChangeListener pcl) {
        setWaitCursor();
        JDialog dialog = getDialog(true);
        dialog.setPreferredSize(null);
        PanelCustomPedido pnCustomPed = new PanelCustomPedido(app, product);
        pnCustomPed.addPropertyChangeListener(getPanelPedido());
        pnCustomPed.addPropertyChangeListener(getPanelOrders());
        dialog.add(pnCustomPed);
        dialog.setResizable(false);
        dialog.setTitle(product.getName());
        dialog.pack();
        dialog.setLocationRelativeTo(getFrame());
        setDefaultCursor();
        dialog.setVisible(true);
    }

    public void showCustomPedido(ProductoPed product, PropertyChangeListener pcl) {
        showCustomPedido(product, pcl, -1);
    }

    public void showCustomPedido(ProductoPed product, PropertyChangeListener pcl, int row) {
        setWaitCursor();
        JDialog dialog = getDialog(true);
        dialog.setPreferredSize(null);
        PanelCustomPedido pnCustomPed = new PanelCustomPedido(app, product.getProduct());
        pnCustomPed.addPropertyChangeListener(getPanelPedido());
        pnCustomPed.addPropertyChangeListener(getPanelOrders());
        pnCustomPed.showProductPed(product, row);
        dialog.add(pnCustomPed);
        dialog.setResizable(false);
        dialog.setTitle(product.getProduct().getName().toUpperCase());
        dialog.pack();
        dialog.setLocationRelativeTo(getFrame());
        setDefaultCursor();
        dialog.setVisible(true);
    }

    public void reviewFacture(Invoice invoice) {
        PanelConfirmPedido confirmPedido = new PanelConfirmPedido(app, invoice);

        setWaitCursor();
        JDialog dialog = getDialog(true);
        dialog.setPreferredSize(null);
        dialog.add(confirmPedido);
//        dialog.setResizable(false);
        dialog.setTitle("Revisar pedido");
        dialog.pack();
        dialog.setLocationRelativeTo(getFrame());
        setDefaultCursor();
        dialog.setVisible(true);

    }

    public void showClientCard(Client client) {
        setWaitCursor();
        JDialog dialog = getDialog(true);
//        dialog.setUndecorated(true);
//        int w = 360;
//        int h = 200;
        dialog.setPreferredSize(null);
        PanelClientCard clientCard = new PanelClientCard(app, client);
        clientCard.addPropertyChangeListener(getPanelPedido());
        dialog.add(clientCard);
//        dialog.setResizable(false);
//        dialog.setTitle();
        dialog.pack();
        dialog.setLocationRelativeTo(getFrame());
        setDefaultCursor();
        dialog.setVisible(true);
    }

    public void showInventoryCard() {
        setWaitCursor();
        JDialog dialog = getDialog(true);
        dialog.setPreferredSize(null);
//        dialog.setUndecorated(true);
//        int w = 360;
//        int h = 200;
//        dialog.setPreferredSize(new Dimension(w, h));
//        PanelClientCard clientCard = new PanelClientCard(app, client);
//        clientCard.addPropertyChangeListener(getPanelPedido());
//        dialog.add(clientCard);
//        dialog.setResizable(false);
//        dialog.setTitle();
        dialog.pack();
        dialog.setLocationRelativeTo(getFrame());
        setDefaultCursor();
        dialog.setVisible(true);
    }

    public void showPanelPayInvoice(Invoice invoice) {
        setWaitCursor();
        JDialog dialog = getDialog(true);

        dialog.setPreferredSize(null);
        PanelPayInvoice pPayInvoice = new PanelPayInvoice(app, invoice);
//        ppayInvoice.addPropertyChangeListener(getPanelPedido());
        dialog.add(pPayInvoice);
//        dialog.setResizable(false);
//        dialog.setTitle();
        dialog.pack();
        dialog.setLocationRelativeTo(getFrame());
        setDefaultCursor();
        dialog.setVisible(true);
    }

    public void showPanelNewCycle(PropertyChangeListener listener) {
        setWaitCursor();
        JDialog dialog = getDialog(true);
        dialog.setPreferredSize(null);
        PanelNewCycle panelNewCycle = new PanelNewCycle(app);
        panelNewCycle.addPropertyChangeListener(listener);
        dialog.add(panelNewCycle);
        dialog.setTitle("Nuevo ciclo de caja");
        dialog.pack();
        dialog.setLocationRelativeTo(getFrame());
        setDefaultCursor();
        dialog.setVisible(true);
    }

    public void showPanelSelProduct(PropertyChangeListener listener) {
        setWaitCursor();
        JDialog dialog = new MyDialogEsc();
        dialog.setModal(true);
        int w = 700;
        int h = 500;
        dialog.setPreferredSize(new Dimension(w, h));

        if (!getPanelSelProduct().containsListener(listener)) {
            getPanelSelProduct().addPropertyChangeListener(listener);
        }
        getPanelSelProduct().reset();
        dialog.setResizable(false);
        dialog.add(getPanelSelProduct());
        dialog.setTitle("Agregar producto.");
        dialog.pack();
        dialog.setLocationRelativeTo(getFrame());
        setDefaultCursor();
        dialog.setVisible(true);
    }

    public void showPanelAddProduct(PropertyChangeListener listener) {
        setWaitCursor();
        JDialog dialog = new MyDialogEsc();
        dialog.setModal(true);
        int w = 700;
        int h = 400;
        dialog.setPreferredSize(new Dimension(w, h));

        if (!getPanelAddProduct().containsListener(listener)) {
            getPanelAddProduct().addPropertyChangeListener(listener);
        }
        getPanelAddProduct().reset();
        dialog.setResizable(false);
        dialog.add(getPanelAddProduct());
        dialog.setTitle("Agregar producto.");
        dialog.pack();
        dialog.setLocationRelativeTo(getFrame());
        setDefaultCursor();
        dialog.setVisible(true);
    }

    public void showPanelSnapShot() {
        setWaitCursor();
        JDialog dialog = new MyDialogEsc();
        dialog.setModal(true);
        int w = 800;
        int h = 600;
        dialog.setPreferredSize(new Dimension(w, h));

//        if (!getPanelAddProduct().containsListener(listener)) {
//            getPanelAddProduct().addPropertyChangeListener(listener);
//        }
        getPanelSnapShot().reset();
//        dialog.setResizable(false);
        dialog.add(getPanelSnapShot());
        dialog.setTitle("Snapshot.");
        dialog.pack();
        dialog.setLocationRelativeTo(getFrame());
        setDefaultCursor();
        dialog.setVisible(true);
    }

    public void showPanelAddExtra(PropertyChangeListener listener) {
        setWaitCursor();
        JDialog dialog = new MyDialogEsc();
        dialog.setModal(true);
        int w = 350;
        int h = 400;
        dialog.setPreferredSize(new Dimension(w, h));

        if (!getPanelAddExtra().containsListener(listener)) {
            getPanelAddExtra().addPropertyChangeListener(listener);
        }
        getPanelAddExtra().reset();
        dialog.setResizable(false);
        dialog.add(getPanelAddExtra());
        dialog.setTitle("Agregar entrada/salida.");
        dialog.pack();
        dialog.setLocationRelativeTo(getFrame());
        setDefaultCursor();
        dialog.setVisible(true);
    }

    public void showPanelAddOtherProduct(PropertyChangeListener listener) {
        setWaitCursor();
        JDialog dialog = new MyDialogEsc();
        dialog.setModal(true);

        dialog.pack();

        if (!getPanelOtherProduct().containsListener(listener)) {
            getPanelOtherProduct().addPropertyChangeListener(listener);
        }
        getPanelOtherProduct().reset();
        dialog.setResizable(false);
        dialog.add(getPanelOtherProduct());
        dialog.setTitle("Agregar otro producto.");
        dialog.pack();
        dialog.setLocationRelativeTo(getFrame());
        setDefaultCursor();
        dialog.setVisible(true);
    }

    public void showPanelAddItem(PropertyChangeListener listener, Item item) {
        setWaitCursor();
        JDialog dialog = new MyDialogEsc();
        dialog.setModal(true);
        int w = 1000;
        int h = 600;
        dialog.setPreferredSize(new Dimension(w, h));

        if (!getPanelAddItem().containsListener(listener)) {
            getPanelAddItem().addPropertyChangeListener(listener);
        }

        getPanelAddItem().reset();
        String title = "Agregar item al inventario.";
        getPanelAddItem().modoNuevoItem();
        if (item != null) {
            getPanelAddItem().setItem(item);
            getPanelAddItem().modoActualizarItem();
            title = "Actualizar item.";
        }
//        dialog.setResizable(false);
        dialog.add(getPanelAddItem());
        dialog.setTitle(title);
        dialog.pack();
        dialog.setLocationRelativeTo(getFrame());
        setDefaultCursor();
        dialog.setVisible(true);
    }

    public void showPanelInvoicedOrder(PropertyChangeListener listener, Order order) {
        setWaitCursor();
        JDialog dialog = new MyDialogEsc();
        dialog.setModal(true);
        int w = 800;
        int h = 600;
        dialog.setPreferredSize(new Dimension(w, h));

        if (!getPanelInvoicedOrder().containsListener(listener)) {
            getPanelInvoicedOrder().addPropertyChangeListener(listener);
        }

        getPanelInvoicedOrder().reset();
        String title = "Facturar orden.";

        if (order != null) {
            getPanelInvoicedOrder().setOrder(order);
        }
//        dialog.setResizable(false);
        dialog.add(getPanelInvoicedOrder());
        dialog.setTitle(title);
        dialog.pack();
        dialog.setLocationRelativeTo(getFrame());
        setDefaultCursor();
        dialog.setVisible(true);
    }

    public void showPanelSelItem(PropertyChangeListener listener) {
        showPanelSelItem(null, listener);
    }

    public void showPanelSelItem(Item item, PropertyChangeListener listener) {
        setWaitCursor();
        JDialog dialog = new MyDialogEsc();
        dialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        if (!getPanelSelItem().containsListener(listener)) {
            getPanelSelItem().addPropertyChangeListener(listener);
        }
        dialog.add(getPanelSelItem(item));
        dialog.setTitle("Agregar item.");
        dialog.pack();
        dialog.setLocationRelativeTo(getFrame());
        setDefaultCursor();
        dialog.setVisible(true);
    }

    public void showPanelDownItem(PropertyChangeListener listener) {
        showPanelDownItem(null, listener);
    }

    public void showPanelDownItem(Item item, PropertyChangeListener listener) {
        setWaitCursor();
        JDialog dialog = new MyDialogEsc();
        dialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        if (!getPanelDownItem().containsListener(listener)) {
            getPanelDownItem().addPropertyChangeListener(listener);
        }
        dialog.add(getPanelDownItem(item));
        dialog.setTitle("Descargar item.");
        dialog.pack();
        dialog.setLocationRelativeTo(getFrame());
        setDefaultCursor();
        dialog.setVisible(true);
    }

    public void showPanelNewUnit(String title, PropertyChangeListener listener, ArrayList lista) {
        setWaitCursor();
        JDialog dialog = new MyDialogEsc();
        dialog.setModal(true);
        dialog.setIconImage(app.getImgManager().getImagen("gui/img/Inventory-maintenance.png", 18, 18));
        dialog.add(getPanelNewList(title, listener, lista));
        dialog.setTitle("Unidades de medida.");
        dialog.pack();
        dialog.setLocationRelativeTo(getFrame());
        setDefaultCursor();
        dialog.setVisible(true);
    }

//    public void showPanelNewLocation() {
//        setWaitCursor();
//        JDialog dialog = new MyDialog();
//        dialog.setModal(true);
//        dialog.setIconImage(app.getImgManager().getImagen("gui/img/Inventory-maintenance.png", 18, 18));        
////        if (!getPanelNewLocation().containsListener(getPanelNewProduct())) {
////            getPanelNewLocation().addPropertyChangeListener(getPanelNewProduct());
////        }
//        dialog.add(getPanelNewLocation());
//        dialog.setTitle("Nueva Locacion.");
//        dialog.pack();
//        dialog.setLocationRelativeTo(getFrame());
//        setDefaultCursor();
//        dialog.setVisible(true);
//    }
    public void showPanelConciliacion(boolean reset) {
        setWaitCursor();
        JDialog dialog = new MyDialogEsc();
        dialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        if (!getPanelNewConciliacion(false).containsListener(getPanelInventory())) {
            getPanelNewConciliacion(false).addPropertyChangeListener(getPanelInventory());
        }
        dialog.add(getPanelNewConciliacion(reset));
        dialog.setTitle("Agregar conciliacion.");
        dialog.pack();
        dialog.setLocationRelativeTo(getFrame());
        setDefaultCursor();
        dialog.setVisible(true);
    }

    public void showPanelNewLocation(PropertyChangeListener listener) {
        setWaitCursor();
        JDialog dialog = new MyDialogEsc();
        dialog.setModal(true);
        if (!getPanelSelItem().containsListener(listener)) {
            getPanelSelItem().addPropertyChangeListener(listener);
        }
        dialog.add(getPanelNewLocation());
        dialog.setTitle("Nueva Locacion.");
        dialog.pack();
        dialog.setLocationRelativeTo(getFrame());
        setDefaultCursor();
        dialog.setVisible(true);
    }

    public PanelConfigPrint getPanelConfigPrint() {
        if (pnConfigPrint == null) {
            pnConfigPrint = new PanelConfigPrint(app);
        }
        return pnConfigPrint;
    }

    public PanelConfigOthers getPanelConfigOthers() {
        if (pnConfigOthers == null) {
            pnConfigOthers = new PanelConfigOthers(app);
        }
        return pnConfigOthers;
    }

    public PanelConfigTicket getPanelConfigTicket() {
        if (pnConfigTicket == null) {
            pnConfigTicket = new PanelConfigTicket(app);
        }
        return pnConfigTicket;
    }

    public void showPanelNewCategory(String title, PropertyChangeListener listener, List lista) {
        setWaitCursor();
        JDialog dialog = new MyDialogEsc();
        dialog.setModal(true);
        dialog.setIconImage(app.getImgManager().getImagen("gui/img/Inventory-maintenance.png", 18, 18));
        dialog.add(getPanelNewList(title, listener, lista));
        dialog.setTitle("Categorias.");
        dialog.pack();
        dialog.setLocationRelativeTo(getFrame());
        setDefaultCursor();
        dialog.setVisible(true);
    }

    public void showPanelAddPress(PropertyChangeListener listener, Product product) {
        setWaitCursor();
        JDialog dialog = new MyDialogEsc();
        dialog.setModal(true);
        int w = 400;
        int h = 400;
        dialog.setPreferredSize(new Dimension(w, h));

        PanelPressProduct panelPres = new PanelPressProduct(app, product);
        panelPres.addPropertyChangeListener(listener);
        panelPres.showNewPresentacionMode();

        String title = "Agregar presentacion.";

//        dialog.setResizable(false);
        dialog.add(panelPres);
        dialog.setTitle(title);
        dialog.pack();
        dialog.setLocationRelativeTo(getFrame());
        setDefaultCursor();
        dialog.setVisible(true);
    }

    public void showPanelEditPress(PropertyChangeListener listener, Presentation presentation) {
        setWaitCursor();
        JDialog dialog = new MyDialogEsc();
        dialog.setModal(true);
        int w = 400;
        int h = 300;
        dialog.setPreferredSize(new Dimension(w, h));

        PanelPressProduct panelPres = new PanelPressProduct(app, presentation);
        panelPres.addPropertyChangeListener(getPanelProducts());
        panelPres.showEditPresentacionMode();

        String title = "Editar presentacion.";

//        dialog.setResizable(false);
        dialog.add(panelPres);
        dialog.setTitle(title);
        dialog.pack();
        dialog.setLocationRelativeTo(getFrame());
        setDefaultCursor();
        dialog.setVisible(true);
    }

}
