/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bacon;

import com.bacon.domain.Permission;
import com.bacon.domain.Rol;
import com.bacon.domain.User;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.log4j.Logger;
import org.balx.Imagenes;

/**
 *
 * @author hp
 */
public final class Aplication implements ActionListener, PropertyChangeListener, ListSelectionListener {

    public static final String TITLE = "Bacon 57 Burger";
    public static final String VERSION = "2.1.2"; // 17/July/2021
    public static final String ACTION_CANCEL_PANEL = "acCancelPanel";
    public static final String ACTION_EXIT_APP = "acExitApp";
    public static final String ACTION_SHOW_PREFERENCES = "acShowPreferences";

    public static final String ACTION_SHOW_ADMIN = "acShowAdmin";
    public static final String ACTION_SHOW_ORDER = "acShowOrder";
    public static final String ACTION_SHOW_ORDER_LIST = "acShowOrderList";
    public static final String ACTION_SHOW_CASH = "acShowCash";
    public static final String ACTION_SHOW_REPORTS = "acShowReports";
    public static final String ACTION_SHOW_INVENTORY = "acShowInventory";
    public static final String ACTION_SHOW_PRODUCTS = "acShowProducts";

    public static final String ACTION_RETURN_TO_MENU = "acReturnToMenu";
    public static final String ACTION_CLOSE_SESION = "acCLoseSesion";
    public static final String ACTION_LOGGIN = "acLogIN";
    public static final String CONFIG_LASTUPDATE = "lastUpdate";

    public static final String PREFERENCES = "";
    public static final String DATABASE = "baconapp";
    public static final String WORK_FOLDER = "baconapp";
    public static final String DB_USER = "root";
    public static boolean INSTALL_DB = false;
    private static final boolean messaged = true;
    public static final String DEFAULT_EXPORT_DIR = "";

    //Correr la aplicacion con configuracion de servidor local
    private static boolean local = !true;

    public DateFormat DF = new SimpleDateFormat("dd-MM-yyyy");
    public DateFormat DF_FULL = new SimpleDateFormat("dd MMMM yyyy hh:mm");
    public DateFormat DF_FULL2 = new SimpleDateFormat("dd-MM-yyyy HH:mm");
    public DateFormat DF_FULL3 = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
    public DateFormat DF_SL = new SimpleDateFormat("dd MMMM yyyy");
    public DateFormat DF_SQL = new SimpleDateFormat("yyyy-MM-dd");
    public static final DateFormat DF_SQL_TS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public DateFormat DF_TIME = new SimpleDateFormat("HH:mm");

    private boolean tserver;
    private final Configuration configuration;
    private ImageManager imgManager;
    private GUIManager guiManager;
    private SQLManager sqlManager;
    private XLSManager xlsManager;
    private PrinterService printerService;
    private ProgAction acExitApp, acShowPreferences, acReturnToMenu,
            acShowAdmin, acCerrarSesion, acShowCash;
    private final Control control;
    private final ControlFilters ctrlFilters;
    private final BackupsCtrl ctrlBackup;
    private final Image imageBC;
    public static final int WC = 80;
    private final SimpleDateFormat formatoFecha;
    private User user;
    private static final Logger logger = Logger.getLogger(Aplication.class.getCanonicalName());
    private ScheduledExecutorService ses;
    private SimpleDateFormat sdfExport;
    private PropertyChangeSupport pcs;
    private String folderIcons = "gui/img/icons/";
    private ProgAction acShowOrders;
    public final DecimalFormat DCFORM_W;
    public final DecimalFormat DCFORM_P;
    private ProgAction acShowOrderList;
    private ProgAction acShowReports;
    private ProgAction acShowInventory;
    private ProgAction acShowProducts;

    public Aplication() {
        Properties properties = new Properties();
        try {
            String logFile = Aplication.getDirTrabajo() + File.separator + "logging.log";
            properties.load(new FileInputStream("config/logging.properties"));
            properties.put("log4j.appender.file.File", logFile);
            org.apache.log4j.PropertyConfigurator.configure(properties);
        } catch (IOException ex) {
            System.err.println("No se encuentra el archivo de configuracion");
        }

        logger.debug("Iniciando la aplicacion..");

        pcs = new PropertyChangeSupport(this);

        tserver = false;
        configuration = Configuration.getInstancia();
        imgManager = ImageManager.getInstance();
        sqlManager = SQLManager.getInstance(this);
        guiManager = GUIManager.getInstance(this);
        xlsManager = XLSManager.getInstance(this);
        printerService = PrinterService.getInstance(this);

        configWorkFolder();
        Utiles.crearDirectorio(Paths.get(getDirPics(), ""));
        configuration.setPath(getDirTrabajo());
        configuration.load();

        DCFORM_W = (DecimalFormat) NumberFormat.getInstance();
        DCFORM_W.applyPattern("###############");

        DCFORM_P = (DecimalFormat) NumberFormat.getInstance();
        DCFORM_P.applyPattern("###,###,###.##");

        String path = Aplication.getDirTrabajo() + File.separator;

        String prop = configuration.getProperty(Configuration.DINST, "NULL");
        if ("NULL".equals(prop)) {
            configuration.setProperty(Configuration.DINST, new Date().toString());
        }
        int cus = configuration.getProperty(Configuration.CUS, 0);
        configuration.setProperty(Configuration.CUS, String.valueOf(++cus));

        initActions();
        formatoFecha = new SimpleDateFormat("dd-MM-yyyy");
        sdfExport = new SimpleDateFormat("yyyyMMddHHmmss");

        control = new Control(this);
        String propST = configuration.getProperty(Configuration.DATABASE_STATION, "false");

        boolean station = Boolean.parseBoolean(propST);
        if (!station) {
            INSTALL_DB = true;
            control.initDatabase();
            setupPermissionsFromLocal();
        } else {
            logger.debug("working as station");
            INSTALL_DB = false;
        }

        configuration.save();

        ctrlFilters = new ControlFilters();
//
        ctrlBackup = new BackupsCtrl(this);

        imageBC = createImage();

        initRoles();
    }

    public String getFolderIcons() {
        return folderIcons;
    }

    public static boolean isLocal() {
        return local;
    }

    private void initRoles() {

        boolean isEmpty = getControl().checkEmpty("roles");
        if (isEmpty) {
            Rol rolADMIN = new Rol("admin", "ADMIN", "Rol administrador");
            getControl().addRol(rolADMIN);
            Object[] data = getControl().getRolByName(rolADMIN.getName());
            rolADMIN.setId(Integer.parseInt(data[0].toString()));
            ArrayList<Permission> permissionList = getControl().getPermissionList();
            getControl().addPermissionRole(rolADMIN, permissionList);
        }
    }

    public void init() {
//        checkTime();
//        verifyLicTime();
        configDatabase();
        getGuiManager().configurar();

    }

    private boolean getTimeServer() {
        boolean verify = false;
        try {
            Install it = new Install();
            Date tServer = new TimeWaste().getTimeServer();
            if (tServer == null || tServer.after(it.getTWST())) {
                verify = false;
            } else {
                verify = true;
            }
        } catch (Exception e) {
            logger.debug(e.getMessage(), e);
        }
        return verify;
    }

    public synchronized void checkTime() {
        Thread hilo = new Thread(new Runnable() {

            @Override
            public void run() {
                logger.debug("Cheking server time");
                boolean tser = getTimeServer();
                tserver = tser;
                while (!tser) {
                    tser = getTimeServer();
                    tserver = tser;
                }
                logger.debug("Time server verified");
            }
        });
        hilo.start();
    }

    public DecimalFormat getDCFORM_P() {
        return DCFORM_P;
    }

    public DecimalFormat getDCFORM_W() {
        return DCFORM_W;
    }

    public void verifyLicTime() {

        Runnable runTask = new Runnable() {

            @Override
            public void run() {
                tserver = getTimeServer();
            }
        };

        ses = Executors.newScheduledThreadPool(2);
        ses.scheduleAtFixedRate(runTask, 1, 15, TimeUnit.SECONDS);

    }

    public void stopTask() {
        if (ses != null) {
            ses.shutdown();
        }
    }

    public boolean isTserver() {
        return tserver;
    }

    public void workOffline() {
        if (new Date().before(new Install().getTWST())) {
            this.tserver = true;
        }
    }

    protected final String getMap() {
        StringBuilder map = new StringBuilder();
        map.append("APP BASIC").append("\n");
        map.append("di:").append(getConfiguration().getProperty(configuration.DINST)).append("\n");
        map.append("os:").append(System.getProperty("os.name")).append("\n");
        map.append("usm:").append(System.getProperty("user.name")).append("\n");
        map.append("usl:").append(getUser()).append("\n");
        return map.toString();
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        switch (evt.getActionCommand()) {
            case Aplication.ACTION_CANCEL_PANEL:
                ((JButton) evt.getSource()).getRootPane().getParent().setVisible(false);
                break;
        }
    }

    public User getUser() {
        return user;
    }

    public DecimalFormat getCurrencyFormat() {
        DecimalFormat DF_CURRENCY = (DecimalFormat) NumberFormat.getInstance();
        DF_CURRENCY.applyPattern("$ ###,###,###");
        return DF_CURRENCY;
    }

    public void setUser(User user) {
        user.setPassword("");
        this.user = user;
        getGuiManager().reloadToolbar();
    }

    public SimpleDateFormat getSdfExport() {
        return sdfExport;
    }

    public static boolean isMessaged() {
        return messaged;
    }

    public void configDatabase() {
        logger.debug("setting the database");
        String driver = configuration.getProperty(Configuration.DATABASE_DRIVER);
        String prefijo = configuration.getProperty(Configuration.DATABASE_PREFIJO);
        String user = configuration.getProperty(Configuration.DATABASE_USER);
        String pass = configuration.getProperty(Configuration.DATABASE_PASSWORD);
        String url = configuration.getProperty(Configuration.DATABASE_URL);
        DBManager.getInstance().setupDatabase(driver, prefijo, url, user, pass);
    }

    private Image createImage() {
        Font f1 = new Font("Agency FB", 1, 16);
        Image centrarTexto = org.balx.Imagenes.centrarTexto(WC, WC, "Bacon 57 Burger", f1, Color.white, Color.blue);
        return Imagenes.imagenToGray(centrarTexto, "");
    }

    public Image getImageBC() {
        return imageBC;
    }

    private void setupPermissionsFromLocal() {
        if (getControl().getPermissionList().isEmpty()) {
            ArrayList<Permission> permissions = new ArrayList<>(Arrays.asList(MyConstants.PERMISSIONS));
            for (int i = 0; i < permissions.size(); i++) {
                try {
                    Permission permission = permissions.get(i);
                    control.addPermission(permission);
                } catch (Exception e) {
                    logger.debug("Error adding permission. ", e);
                    GUIManager.showErrorMessage(null, "Error agregando el permiso", "Error");
                }
            }
        }
    }

    public SimpleDateFormat getFormatoFecha() {
        return formatoFecha;
    }

    private void initActions() {
        acExitApp = new ProgAction("Salir",
                new ImageIcon(imgManager.getImagen(getFolderIcons() + "cancel.png", 32, 32)), "Salir de la aplicacion", 'x') {
            public void actionPerformed(ActionEvent e) {
                salir(0);
            }

        };
        acExitApp.setSmallIcon(new ImageIcon(imgManager.getImagen(getFolderIcons() + "cancel.png", 25, 25)));
        acExitApp.setLargeIcon(new ImageIcon(imgManager.getImagen(getFolderIcons() + "cancel.png", 32, 32)));

        acReturnToMenu = new ProgAction("Volver",
                null, "Volver al menu Principal", 'v') {
            public void actionPerformed(ActionEvent e) {
                getGuiManager().showMenuPrc();
            }
        };

        acShowAdmin = new ProgAction("Administrar",
                null, "Ver modulo de administracion", 'a') {
            public void actionPerformed(ActionEvent e) {
                Permission perm = getControl().getPermissionByName("show-admin-module");
                getGuiManager().showBasicPanel(getGuiManager().getPanelBasicAdminModule(), perm);
            }
        };
        acShowAdmin.setSmallIcon(new ImageIcon(imgManager.getImagen(getFolderIcons() + "admin.png", 25, 25)));
        acShowAdmin.setLargeIcon(new ImageIcon(imgManager.getImagen(getFolderIcons() + "admin.png", 32, 32)));

        acShowOrders = new ProgAction("Pedidos",
                null, "Ver modulo de pedidos", 'a') {
            public void actionPerformed(ActionEvent e) {
                Permission perm = getControl().getPermissionByName("show-pedidos-module");
                getGuiManager().showBasicPanel(getGuiManager().getPanelBasicPedidos(), perm);
            }
        };
        acShowOrders.setSmallIcon(new ImageIcon(imgManager.getImagen(getFolderIcons() + "shop.png", 25, 25)));
        acShowOrders.setLargeIcon(new ImageIcon(imgManager.getImagen(getFolderIcons() + "shop.png", 32, 32)));

        acShowOrderList = new ProgAction("Lista de pedidos",
                null, "Ver Lista de pedidos", 'a') {
            public void actionPerformed(ActionEvent e) {
                Permission perm = getControl().getPermissionByName("show-orderlist-module");
                getGuiManager().showBasicPanel(getGuiManager().getPanelBasicListPedidos(), perm);
            }
        };
        acShowOrderList.setSmallIcon(new ImageIcon(imgManager.getImagen(getFolderIcons() + "ordering.png", 25, 25)));
        acShowOrderList.setLargeIcon(new ImageIcon(imgManager.getImagen(getFolderIcons() + "ordering.png", 32, 32)));

        acShowCash = new ProgAction("Caja",
                null, "Ver modulo caja", 'c') {
            public void actionPerformed(ActionEvent e) {
                Permission perm = getControl().getPermissionByName("show-cash-module");
                getGuiManager().showBasicPanel(getGuiManager().getPanelBasicCash(), perm);
            }
        };
        acShowCash.setSmallIcon(new ImageIcon(imgManager.getImagen(getFolderIcons() + "cash.png", 25, 25)));
        acShowCash.setLargeIcon(new ImageIcon(imgManager.getImagen(getFolderIcons() + "cash.png", 32, 32)));

        acShowReports = new ProgAction("Reportes",
                null, "Ver modulo reportes", 'r') {
            public void actionPerformed(ActionEvent e) {
                Permission perm = getControl().getPermissionByName("show-reports-module");
                getGuiManager().showBasicPanel(getGuiManager().getPanelBasicReports(), perm);
            }
        };
        acShowReports.setSmallIcon(new ImageIcon(imgManager.getImagen(getFolderIcons() + "reports.png", 25, 25)));
        acShowReports.setLargeIcon(new ImageIcon(imgManager.getImagen(getFolderIcons() + "reports.png", 32, 32)));

        acShowInventory = new ProgAction("Inventario",
                null, "Ver modulo inventario", 'i') {
            public void actionPerformed(ActionEvent e) {
                Permission perm = getControl().getPermissionByName("show-inventory-module");
                getGuiManager().showBasicPanel(getGuiManager().getPanelBasicInventory(), perm);
            }
        };
        acShowInventory.setSmallIcon(new ImageIcon(imgManager.getImagen(getFolderIcons() + "Inventory-maintenance.png", 25, 25)));
        acShowInventory.setLargeIcon(new ImageIcon(imgManager.getImagen(getFolderIcons() + "Inventory-maintenance.png", 32, 32)));

        acShowProducts = new ProgAction("Products",
                null, "Ver modulo productos", 'p') {
            public void actionPerformed(ActionEvent e) {
                Permission perm = getControl().getPermissionByName("show-products-module");
                getGuiManager().showBasicPanel(getGuiManager().getPanelBasicProducts(), perm);
            }
        };
        acShowProducts.setSmallIcon(new ImageIcon(imgManager.getImagen(getFolderIcons() + "shopping-bag-purple.png", 25, 25)));
        acShowProducts.setLargeIcon(new ImageIcon(imgManager.getImagen(getFolderIcons() + "shopping-bag-purple.png", 32, 32)));

        acCerrarSesion = new ProgAction("Cerrar secion",
                null, "Cerrar la sesion del usuario actual", 'x') {
            public void actionPerformed(ActionEvent e) {
                getGuiManager().closeSesion();
            }
        };
        acCerrarSesion.setSmallIcon(new ImageIcon(imgManager.getImagen(getFolderIcons() + "system-log-out.png", 25, 25)));
        acCerrarSesion.setLargeIcon(new ImageIcon(imgManager.getImagen(getFolderIcons() + "system-log-out.png", 32, 32)));

    }

    public AbstractAction getAction(String action) {
        switch (action) {
            case ACTION_SHOW_PREFERENCES:
                return acShowPreferences;
            case ACTION_EXIT_APP:
                return acExitApp;
            case ACTION_RETURN_TO_MENU:
                return acReturnToMenu;

            case ACTION_CLOSE_SESION:
                return acCerrarSesion;

            case ACTION_SHOW_ADMIN:
                return acShowAdmin;

            case ACTION_SHOW_ORDER:
                return acShowOrders;

            case ACTION_SHOW_ORDER_LIST:
                return acShowOrderList;

            case ACTION_SHOW_CASH:
                return acShowCash;

            case ACTION_SHOW_REPORTS:
                return acShowReports;

            case ACTION_SHOW_PRODUCTS:
                return acShowProducts;

            case ACTION_SHOW_INVENTORY:
                return acShowInventory;

            default:
                return null;
        }
    }

    public Control getControl() {
        return control;
    }

    public BackupsCtrl getCtrlBackup() {
        return ctrlBackup;
    }

    public ControlFilters getCtrlFilters() {
        return ctrlFilters;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public ImageManager getImgManager() {
        return imgManager;
    }

    public GUIManager getGuiManager() {
        return guiManager;
    }

    public SQLManager getSqlManager() {
        return sqlManager;
    }

    public XLSManager getXlsManager() {
        return xlsManager;
    }

    public PrinterService getPrinterService() {
        return printerService;
    }

    public String getUserHome() {
        return System.getProperty("user.home");
    }

    public String getRunDir() {
        return System.getProperty("user.dir");
    }

    public String getDirDocuments() {
        Path dir = Paths.get(System.getProperty("user.home"), "");
        return dir.toString() + File.separator + "Documents";
    }

    public static String getDirTrabajo() {
        String SUF = ".";
        if (System.getProperty("os.name").toUpperCase().contains("XP")) {
            SUF = "";
        }
        return System.getProperty("user.home") + File.separator + SUF + WORK_FOLDER;
    }

    public static String getDirPics() {
        return getDirTrabajo() + File.separator + "pics";
    }

    public void salir(int i) {
        logger.debug("Closing aplication..");
        guiManager.getFrame().setVisible(false);
        guiManager.getFrame().dispose();
        configuration.savePreferences();
        configuration.save();
        System.exit(i);
    }

    private boolean configWorkFolder() {
        logger.debug("Setting work folder");
        Path path;
        boolean creado = false;
        path = Paths.get(getDirTrabajo(), "");
        if (System.getProperty("os.name").toUpperCase().contains("XP")) {
            logger.debug("Setting for Windows XP system");
            path = Paths.get(getUserHome(), "");
        }
        if (!Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
            try {
                Files.createDirectory(path);
                path = path.toAbsolutePath();
                logger.debug("\n" + path + " directorio creado.");
                return true;
            } catch (NoSuchFileException e) {
                creado = false;
                logger.debug("\nDirectory creation failed:\n" + e);
            } catch (FileAlreadyExistsException e) {
                creado = false;
                System.err.println("\nDirectory creation failed:\n" + e);
            } catch (IOException e) {
                creado = false;
                logger.debug("\nDirectory creation failed:\n" + e);
            }
        } else {
            logger.debug("Directory already exist");
        }
        return creado;
    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {

    }

    protected void addPropertyChangeListener(PropertyChangeListener pcl) {
        pcs.addPropertyChangeListener(pcl);
    }

    protected void removePropertyChangeListener(PropertyChangeListener pcl) {
        pcs.removePropertyChangeListener(pcl);
    }

}
