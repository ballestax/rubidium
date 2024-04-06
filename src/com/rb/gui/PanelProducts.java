package com.rb.gui;

import com.rb.Aplication;
import com.rb.GUIManager;
import com.rb.MyConstants;
import com.rb.Utiles;
import com.rb.domain.Presentation;
import com.rb.domain.Product;
import static com.rb.gui.PanelTopSearch.AC_CLEAR_FIELD;
import com.rb.persistence.JDBC.JDBCProductDAO;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.text.Format;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import static javax.swing.BorderFactory.createLineBorder;
import javax.swing.BoxLayout;
import javax.swing.DefaultListSelectionModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import org.apache.log4j.Logger;
import org.dz.MyDefaultTableModel;
import org.dz.PanelCapturaMod;
import org.dz.ProgAction;
import org.dz.TextFormatter;

/**
 *
 * @author lrod
 */
public class PanelProducts extends PanelCapturaMod implements ActionListener, CaretListener, ListSelectionListener, TableModelListener {

    private final Aplication app;
    private MyDefaultTableModel model;
    private JPopupMenu popupTable;
    private MyPopupListener popupListenerTabla;
    private TableRowSorter<MyDefaultTableModel> lastSorter;
    private final JTextArea textArea;
    private ProgAction acNewCategory;
    public static final Logger LOGGER = Logger.getLogger(PanelProducts.class.getCanonicalName());
    private Product currentProduct;
    private JMenuItem itemEdit;
    private Product editingProduct;
    private boolean band;
    private int status;
    public static final int STATUS_NORMAL = 0;
    public static final int STATUS_EDITING = 1;

    public static final String AC_SET_VARPRICE = "AC_SET_VARPRICE";
    public static final String AC_ADD_PRESS = "AC_ADD_PRESS";
    public static final String AC_ENABLE_PRODUCT = "AC_ENABLE_PRODUCT";
    public static final String AC_SAVE_NEW_PRODUCT = "AC_SAVE_NEW_PRODUCT";
    public static final String AC_CANCEL_NEW_PRODUCT = "AC_CANCEL_NEW_PRODUCT";
    public static final String AC_CHANGE_NEW_CAT = "AC_CHANGE_NEW CAT";
    public static final String AC_NEW_PRODUCT = "AC_NEW_PRODUCT";
    public static final String AC_CHANGE_CATEGORY = "AC_CHANGE_CATEGORY";
    public static final String AC_REFRESH_LIST = "AC_REFRESH_LIST";
    public static final String AC_SAVE_EDIT = "AC_SAVE_EDIT";
    private Color BCBACK;

    /**
     * Creates new form PanelProducts
     *
     * @param app
     */
    public PanelProducts(Aplication app) {
        this.app = app;
        textArea = new JTextArea(5, 1);
        initComponents();
        createComponents();
        status = STATUS_NORMAL;
    }

    private void createComponents() {

        Font font = new Font("Sans", 1, 15);

        String[] cols = new String[]{"ID", "Nombre", "Categoria", "Precio", "Status"};
        model = new MyDefaultTableModel(cols, 0);

        ImageIcon icon = new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "add1.png", 20, 20));
        acNewCategory = new ProgAction("", icon, "Nueva categoria", 'c') {

            public void actionPerformed(ActionEvent e) {
                app.getGuiManager().showPanelNewCategory("Categorias", PanelProducts.this, getCategoriesList());
            }
        };

        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("Sans", 0, 15));

        JScrollPane scroll = new JScrollPane(textArea,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        regDesc.setComponent(scroll);

        regCat.setAction(acNewCategory);
        regCat.setFont(font);
        regCat.setActionCommand(AC_CHANGE_NEW_CAT);
        regCat.addActionListener(this);

        updateCategoriesList();

        regVarPrice.addActionListener(this);
        regVarPrice.setActionCommand(AC_SET_VARPRICE);

        regPrice.setDocument(TextFormatter.getDoubleLimiter());

        regName.setFont(font);
        regPrice.setFont(font);
        regCode.setFont(font);

//        regCat.setText(app.getControl().getCategoriesList().toArray());
        tbProducts.setModel(model);
        tbProducts.setRowHeight(24);
//        tbProducts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        DefaultListSelectionModel selectionModel = new DefaultListSelectionModel();
        selectionModel.addListSelectionListener(this);
        selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tbProducts.setSelectionModel(selectionModel);

        Font f = new Font("Sans", 0, 15);
        TablaCellRenderer tRenderer = new TablaCellRenderer(true, app.getDCFORM_P());

        int[] colW = new int[]{10, 100, 25, 20, 10};
        for (int i = 0; i < colW.length; i++) {
            tbProducts.getColumnModel().getColumn(i).setMinWidth(colW[i]);
            tbProducts.getColumnModel().getColumn(i).setPreferredWidth(colW[i]);
            tbProducts.getColumnModel().getColumn(i).setCellRenderer(new TablaCellRenderer(true, null));
        }
        tbProducts.getColumnModel().getColumn(3).setCellRenderer(tRenderer);
//        tbProducts.getColumnModel().getColumn(model.getColumnCount() - 1).setCellEditor(new RadioButtonEditor(tbProducts, this, AC_ENABLE_PRODUCT));
//        tbProducts.getColumnModel().getColumn(model.getColumnCount() - 1).setCellRenderer(new RadioButtonCellRenderer("Habilitado",true));

        itemEdit = new JMenuItem("Editar");
        itemEdit.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int row = tbProducts.getSelectedRow();
                long idProd = Long.parseLong(tbProducts.getValueAt(row, 0).toString());
                Product prod = app.getControl().getProductById(idProd);

                makeProductEditable(prod);
            }

        });

        makePopup(-1);

        popupListenerTabla = new MyPopupListener();

        tbProducts.addMouseListener(popupListenerTabla);

        lbID.setOpaque(true);
        lbID.setBorder(BorderFactory.createEmptyBorder(2, 7, 2, 7));
        lbID.setBackground(new Color(120, 144, 240));
        lbID.setFont(font);
        lbID1.setOpaque(true);
        lbID1.setBorder(BorderFactory.createEmptyBorder(2, 7, 2, 7));
        lbID1.setBackground(new Color(120, 144, 240));
//        lbID1.setFont(font);

        chEnableProd.setVisible(false);
        chEnableProd.setActionCommand(AC_ENABLE_PRODUCT);
        chEnableProd.addActionListener(this);

        regCode.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
            }

            @Override
            public void focusLost(FocusEvent e) {
                int existClave = app.getControl().existClave(JDBCProductDAO.TABLE_NAME, "code", "'" + regCode.getText() + "'");
                if (existClave > 0) {
                    regCode.setBorderToError();
                    regCode.setForeground(Color.red);
                } else {
                    regCode.setBorderToNormal();
                    regCode.setForeground(Color.black);
                }
            }
        });

        tbProducts.addMouseListener(popupListenerTabla);
        tbProducts.getModel().addTableModelListener(this);

        btBuscar.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "cancel.png", 18, 18)));
        btBuscar.setActionCommand(AC_CLEAR_FIELD);
        btBuscar.addActionListener(this);
//        regSearch.setFontCampo(font1);
        regSearch.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                filtrar(regSearch.getText().toUpperCase(), 1, MyConstants.FILTER_TEXT_INT_CONTAINS);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filtrar(regSearch.getText().toUpperCase(), 1, MyConstants.FILTER_TEXT_INT_CONTAINS);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filtrar(regSearch.getText().toUpperCase(), 1, MyConstants.FILTER_TEXT_INT_CONTAINS);
            }
        });

        btRefreshList.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "refresh.png", 18, 18)));
        btRefreshList.setActionCommand(AC_REFRESH_LIST);
        btRefreshList.addActionListener(this);

        btNewProduct.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "add1.png", 18, 18)));
        btNewProduct.setActionCommand(AC_NEW_PRODUCT);
        btNewProduct.addActionListener(this);

        btCancel.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "cancel.png", 18, 18)));
        btCancel.setActionCommand(AC_CANCEL_NEW_PRODUCT);
        btCancel.addActionListener(this);
        btCancel.setText("Cancelar");

        BCBACK = btSave.getBackground();
        btSave.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "success.png", 18, 18)));
        btSave.setActionCommand(AC_SAVE_NEW_PRODUCT);
        btSave.addActionListener(this);
        btSave.setText("Guardar");

        btAddPress.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "add-item.png", 18, 18)));
        btAddPress.setActionCommand(AC_ADD_PRESS);
        btAddPress.addActionListener(this);
        btAddPress.setText("Agregar");
        btAddPress.setVisible(false);

        btCancel.setVisible(false);
        btSave.setVisible(false);

        List<String> list = getCategoriesList().stream().map(cat -> cat.toUpperCase()).collect(Collectors.toList());
        list.add(0, "TODOS");
        regFilterCat.setText(list.toArray());
        regFilterCat.setActionCommand(AC_CHANGE_CATEGORY);
        regFilterCat.addActionListener(this);

//        pnCtrl.setLayout(new FlowLayout());
        pnCtrl.add(btRefreshList);

//        resetPanelNewProduct();
        editCampos(false);
        populateTable("");

        lbTitlePress.setOpaque(true);
        lbTitlePress.setBackground(org.dz.Utiles.colorAleatorio(150, 200));
        lbTitlePress.setText("Presentaciones");
        lbTitlePress.setVisible(false);

        panelContainPress.setLayout(new FlowLayout(FlowLayout.LEADING));

//        for (int i = 0; i < 3; i++) {
//            
//        }
    }

    public void filtrar(final String text, final int columna, final int tFilter) {
        RowFilter<Object, Object> filterText = new RowFilter<Object, Object>() {
            @Override
            public boolean include(RowFilter.Entry entry) {
                if (text.equals("")) {
                    return true;
                }
                if (MyConstants.FILTER_TEXT_INT_START == tFilter) {
                    return entry.getStringValue(columna).toLowerCase().startsWith(text.toLowerCase());
                } else if (tFilter == MyConstants.FILTER_TEXT_INT_CONTAINS) {
                    return entry.getStringValue(columna).toLowerCase().contains(text.toLowerCase());
                } else {
                    return entry.getStringValue(columna).equalsIgnoreCase(text);
                }
            }
        };

        TableRowSorter<MyDefaultTableModel> sorter = new TableRowSorter<>(model);
        sorter.setSortable(0, false);
        sorter.setSortable(3, false);
        sorter.setSortable(4, false);
//        sorter.setComparator(3, new COmpara);
        if (tFilter <= 3) {
            sorter.setRowFilter(filterText);
        }
        lastSorter = sorter;
        tbProducts.setRowSorter(sorter);
    }

    public void makeProductEditable(Product prod) {
        status = STATUS_EDITING;
        editingProduct = prod;
        regName.setText(prod.getName());
        regPrice.setText(app.getDCFORM_W().format(prod.getPrice()));
        regVarPrice.setSelected(prod.isVariablePrice());
        lbID1.setText("Editando: " + prod.getName().toUpperCase());

        editCampos(true);
        regName.addCaretListener(this);
        regPrice.addCaretListener(this);
        regDesc.addCaretListener(this);
        regCode.addCaretListener(this);

        tbProducts.setEnabled(false);
        btSave.setActionCommand(AC_SAVE_EDIT);
        btSave.setVisible(true);
        btSave.setEnabled(false);
        btCancel.setVisible(true);
        btAddPress.setVisible(false);
        lbTitlePress.setVisible(false);
        jScrollPane2.setVisible(false);

    }

    public void resetPanelNewProduct() {
        lbID.setBackground(new Color(120, 144, 220));
        regName.setText("");
        regCat.setSelected(0);
        regPrice.setText("");
        regDesc.setText("");
        regCode.setText("");
        lbID.setText("");
        regVarPrice.setSelected(false);
        editCampos(true);
        regCode.removeCaretListener(this);
        regName.removeCaretListener(this);
        regPrice.removeCaretListener(this);
        regDesc.removeCaretListener(this);

        status = STATUS_NORMAL;
    }

    private void showProduct(Product prod) {
        if (prod != null) {
            editCampos(false);
            regName.setText(prod.getName());
            regCat.setSelected(prod.getCategory().toUpperCase());
            regPrice.setText(app.DCFORM_W.format(prod.getPrice()));
            regDesc.setText(prod.getDescription());
            lbID.setText("ID: " + String.valueOf(prod.getId()));
            lbID1.setText(prod.isEnabled() ? "Habilitado" : "Deshabilitado");
            lbID.setBackground(prod.isEnabled() ? new Color(120, 144, 240) : new Color(2, 164, 184));
            lbID1.setBackground(prod.isEnabled() ? new Color(120, 144, 240) : new Color(2, 164, 184));
            chEnableProd.setBackground(prod.isEnabled() ? new Color(120, 144, 240) : new Color(2, 164, 184));
            chEnableProd.setVisible(true);
            chEnableProd.setSelected(prod.isEnabled());
            regVarPrice.setSelected(prod.isVariablePrice());
//            lbID1.setForeground(prod.isEnabled()?Color.BLACK:Color.RED);
            regCode.setText(prod.getCode());
            lbTitlePress.setVisible(true);
            loadPresentations(prod);
            btAddPress.setVisible(true);
            if (!jScrollPane2.isVisible()) {
                jScrollPane2.setVisible(true);
            }
            jScrollPane2.updateUI();
            currentProduct = prod;
        } else {
            resetPanelNewProduct();
            lbTitlePress.setVisible(false);
            btAddPress.setVisible(false);
            jScrollPane2.setVisible(false);
            lbID1.setText("");
            lbID1.setBackground(new Color(120, 144, 240));
            chEnableProd.setVisible(false);
//            lbID1.setForeground(Color.BLACK);
            currentProduct = null;

        }
    }

    public void loadPresentations(Product prod) {
        ArrayList<Presentation> presList = app.getControl().getAllPresentationsByProduct(prod.getId());
        lbTitlePress.setText("Presentaciones [ " + presList.size() + " ]");
        panelContainPress.removeAll();
        for (Presentation presentation : presList) {
            PanelPressProduct panelPressProduct = new PanelPressProduct(app, presentation);
            panelPressProduct.addPropertyChangeListener(this);
            panelContainPress.add(panelPressProduct);
        }
        panelContainPress.updateUI();
    }

    private void updateButtonSave() {
        if (band) {

            btSave.setToolTipText("Existen cambios sin guardar");
            btSave.setEnabled(true);
            btSave.setActionCommand(AC_SAVE_EDIT);

        } else {
            btSave.setBackground(BCBACK);
            btSave.setToolTipText("Todos los datos estan guardados");
            btSave.setEnabled(false);
//            btAdd.removeActionListener(this);
        }
    }

    private String autoGenCode() {
        int val = app.getControl().getMaxIDTabla(JDBCProductDAO.TABLE_NAME);
        String comp = Utiles.getNumeroFormateado(val + 1, 3);
        String code = comp;
        if (regCat.getSelected() > 0) {
            code = regCat.getText().trim().substring(0, 3) + comp;
        }
        return code.toUpperCase();
    }

    private JPopupMenu makePopup(int row) {
        boolean on = false;
        if (row >= 0) {
            long idProd = Long.parseLong(tbProducts.getValueAt(row, 0).toString());
            Product prod = app.getControl().getProductById(idProd);
            if (prod != null) {
                on = prod.isEnabled();
            }
        }
        String html = "<html>Habilitar <font color=" + (!on ? "green" : "red") + ">[" + (!on ? "ON" : "OFF") + "]</font><html>";
        JMenuItem itemEnable = new JMenuItem(html);
        itemEnable.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int r = tbProducts.getSelectedRow();
                long idProd = Long.parseLong(tbProducts.getValueAt(row, 0).toString());
                Product prod = app.getControl().getProductById(idProd);
                prod.setEnabled(!prod.isEnabled());
                app.getControl().updateProduct(prod);
                populateTable("");
                tbProducts.getSelectionModel().addSelectionInterval(r, r);
            }
        });

        JPopupMenu popupTable = new JPopupMenu();
        popupTable.add(itemEdit);
        popupTable.addSeparator();
        popupTable.add(itemEnable);

        return popupTable;
    }

    private void populateTable(String where) {
        populateTable(where, "name,price");
    }

    private void populateTable(String where, String order) {

        SwingWorker<Boolean, Product> sw = new SwingWorker<Boolean, Product>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                model.setRowCount(0);
                ArrayList<Product> productsList = app.getControl().getProductsList(where, order);
                for (Product product : productsList) {
                    publish(product);
                }
                return true;
            }

            @Override
            protected void process(List<Product> chunks) {
                for (Product prod : chunks) {
                    model.addRow(new Object[]{
                        prod.getId(),
                        prod.getName(),
                        prod.getCategory(),
                        prod.getPrice(),
                        prod.isEnabled() ? "Habilitado" : "Deshabilitado"
                    });
                    model.setRowEditable(model.getRowCount() - 1, false);
                }
            }

            @Override
            protected void done() {

            }
        };

        sw.execute();

    }

    public ArrayList<String> getCategoriesList() {
        ArrayList<String> categoriesList = app.getControl().getCategoriesList("", "name");
        return categoriesList;
    }

    private void updateCategoriesList() {
        List<String> list = getCategoriesList().stream().map(cat -> cat.toUpperCase()).collect(Collectors.toList());
        list.add(0, "");
        regCat.setText(list.toArray());
    }

    public Product parseProduct() {
        Product product = null;
        boolean valido = true;
        String name = regName.getText().trim();
        if (name.trim().isEmpty()) {
            regName.setBorder(bordeError);
            valido = false;
        } else {
            if (status == STATUS_EDITING && currentProduct != null) {
                if (!currentProduct.getName().equals(name)) {
                    int existClave = app.getControl().existClave("products", "name", "'" + name + "'");

                    if (existClave > 0) {
                        GUIManager.showErrorMessage(this, "<html><p>Ya exixte un producto registrado con este nombre:"
                                + "<p color=red size=+1>" + name.toUpperCase() + "</html>", "ADVERTENCIA");
                        regName.setForeground(Color.red);
                        valido = false;
                    }
                }

            } else {
                int existClave = app.getControl().existClave("products", "name", "'" + name + "'");

                if (existClave > 0) {
                    GUIManager.showErrorMessage(this, "<html><p>Ya exixte un producto registrado con este nombre:"
                            + "<p color=red size=+1>" + name.toUpperCase() + "</html>", "ADVERTENCIA");
                    regName.setForeground(Color.red);
                    valido = false;
                }
            }
        }
        String code = regCode.getText().trim();
        if (regCode.getText().trim().isEmpty()) {
            regCode.setBorder(bordeError);
            valido = false;
        } else {

            if (status == STATUS_EDITING && currentProduct != null) {
                if (!currentProduct.getCode().equals(code)) {
                    int existClave = app.getControl().existClave("products", "code", "'" + code + "'");
                    if (existClave > 0) {
                        GUIManager.showErrorMessage(this, "<html><p>Ya existe un producto registrado con este codigo:"
                                + "<p color=red size=+1>" + code + "</html>", "ADVERTENCIA");
                        regCode.setForeground(Color.red);
                        valido = false;
                    }
                }
            } else {
                int existClave = app.getControl().existClave("products", "code", "'" + code + "'");
                if (existClave > 0) {
                    GUIManager.showErrorMessage(this, "<html><p>Ya existe un producto registrado con este codigo:"
                            + "<p color=red size=+1>" + code + "</html>", "ADVERTENCIA");
                    regCode.setForeground(Color.red);
                    valido = false;
                }

            }
        }
        if (regCat.getSelected() < 1) {
            regCat.setBorder(bordeError);
            valido = false;
        }
        if (regPrice.getText().trim().isEmpty()) {
            regPrice.setBorder(bordeError);
            valido = false;
        }

        System.out.println(regDesc.getText());
        if (regDesc.getText().trim().isEmpty()) {
            regDesc.setBorder(bordeError);
            valido = false;
        }

        if (valido) {
            resetCampos();
            product = new Product();
            product.setName(regName.getText().trim().toLowerCase());
            product.setCode(regCode.getText().trim().toLowerCase());
            product.setDescription(regDesc.getText());
            product.setCategory(regCat.getSelectedItem().toString().toLowerCase());
            product.setPrice(Double.parseDouble(regPrice.getText()));
            product.setVariablePrice(regVarPrice.isSelected());
            product.setEnabled(true);
        }
        return product;
    }

    public void resetCampos() {
        regDesc.setBorderToNormal();
        regName.setBorderToNormal();
        regPrice.setBorderToNormal();
        regCat.setBorderToNormal();
        regCode.setBorderToNormal();
        lbID1.setText("");
    }

    public void editCampos(boolean enable) {
        regDesc.setEditable(enable);
        textArea.setEditable(enable);
        regName.setEditable(enable);
        regPrice.setEditable(enable);
        regCat.setEditable(enable);
        regCode.setEditable(enable);
        regVarPrice.setEditable(enable);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        lbID = new javax.swing.JLabel();
        regName = new org.dz.Registro(BoxLayout.X_AXIS, "Nombre","", 70);
        regCat = new org.dz.Registro(BoxLayout.X_AXIS, "Categoria", new String[1], 70);
        regPrice = new org.dz.Registro(BoxLayout.X_AXIS, "Precio", "",70);
        regDesc = new org.dz.Registro(BoxLayout.Y_AXIS, "Descripcion", textArea,70);
        regCode = new org.dz.Registro(BoxLayout.X_AXIS, "Codigo","", 55);
        regVarPrice = new org.dz.Registro(BoxLayout.X_AXIS, "Variable", false,70);
        btSave = new javax.swing.JButton();
        btCancel = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        panelContainPress = new javax.swing.JPanel();
        lbTitlePress = new javax.swing.JLabel();
        btAddPress = new javax.swing.JButton();
        lbID1 = new javax.swing.JLabel();
        chEnableProd = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        pnCtrl = new javax.swing.JPanel();
        btBuscar = new javax.swing.JButton();
        regSearch = new org.dz.Registro(BoxLayout.X_AXIS, "Buscar", "",60);
        btRefreshList = new javax.swing.JButton();
        regFilterCat = new org.dz.Registro(BoxLayout.X_AXIS, "Categoria", new String[]{}, 70);
        btNewProduct = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbProducts = new javax.swing.JTable();

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btSave.setText("jButton1");

        btCancel.setText("jButton2");

        javax.swing.GroupLayout panelContainPressLayout = new javax.swing.GroupLayout(panelContainPress);
        panelContainPress.setLayout(panelContainPressLayout);
        panelContainPressLayout.setHorizontalGroup(
            panelContainPressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panelContainPressLayout.setVerticalGroup(
            panelContainPressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 153, Short.MAX_VALUE)
        );

        jScrollPane2.setViewportView(panelContainPress);

        lbTitlePress.setText("jLabel1");

        btAddPress.setText("jButton2");

        lbID1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(regDesc, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(regCat, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(regName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(regPrice, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(regVarPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(lbTitlePress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btAddPress)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btCancel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btSave))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(lbID, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(0, 0, 0)
                        .addComponent(lbID1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(0, 0, 0)
                        .addComponent(chEnableProd)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(regCode, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane2))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(regCode, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
                    .addComponent(lbID, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
                    .addComponent(lbID1, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
                    .addComponent(chEnableProd, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(regName, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(regCat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(regPrice, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(regVarPrice, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(regDesc, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btSave)
                        .addComponent(btCancel)
                        .addComponent(btAddPress))
                    .addComponent(lbTitlePress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2)
                .addGap(28, 28, 28))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {regCat, regName, regPrice});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btCancel, btSave});

        jSplitPane1.setRightComponent(jPanel1);

        pnCtrl.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout pnCtrlLayout = new javax.swing.GroupLayout(pnCtrl);
        pnCtrl.setLayout(pnCtrlLayout);
        pnCtrlLayout.setHorizontalGroup(
            pnCtrlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnCtrlLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(regSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(btBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(regFilterCat, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btNewProduct, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btRefreshList, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pnCtrlLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btNewProduct, btRefreshList});

        pnCtrlLayout.setVerticalGroup(
            pnCtrlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnCtrlLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnCtrlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnCtrlLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(pnCtrlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(btBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(regSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btRefreshList)
                            .addComponent(regFilterCat, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(btNewProduct, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pnCtrlLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btNewProduct, btRefreshList, regFilterCat, regSearch});

        tbProducts.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(tbProducts);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addComponent(pnCtrl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(pnCtrl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                .addContainerGap())
        );

        jSplitPane1.setLeftComponent(jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1))
        );
    }// </editor-fold>//GEN-END:initComponents

    @Override
    public void actionPerformed(ActionEvent e) {

        if (AC_REFRESH_LIST.equals(e.getActionCommand())) {
            populateTable("");
        } else if (AC_CLEAR_FIELD.equals(e.getActionCommand())) {
            regSearch.setText("");
            regSearch.getComponent().requestFocus();
        } else if (AC_CHANGE_CATEGORY.equals(e.getActionCommand())) {
            String cat = regFilterCat.getText();
            if (!"TODOS".equals(cat)) {
                populateTable("category='" + cat.toLowerCase() + "'");
            } else {
                populateTable("");
            }
        } else if (AC_NEW_PRODUCT.equals(e.getActionCommand())) {
            tbProducts.getSelectionModel().clearSelection();
            tbProducts.setEnabled(false);
            resetPanelNewProduct();
            lbID.setText("NUEVO PRODUCTO");
            lbID.setBackground(new Color(150, 225, 145));
            lbID1.setBackground(new Color(150, 225, 145));
            regCode.setText(autoGenCode());
            btCancel.setVisible(true);
            btSave.setVisible(true);
            regName.requestFocus();
            btRefreshList.setEnabled(false);
            lbTitlePress.setVisible(false);
            jScrollPane2.setVisible(false);
        } else if (AC_CHANGE_NEW_CAT.equals(e.getActionCommand())) {
            if (status != STATUS_EDITING) {
                regCode.setText(autoGenCode());
            } else if (editingProduct != null) {
                String value = regCat.getSelectedItem().toString();
                if (regCat.getSelectedItem() != null && !editingProduct.getCategory().equalsIgnoreCase(value)) {
                    regCat.setBorder(bordeEdit);
                    regCat.setForeground(Color.blue);
                    band = true;
                } else {
                    regCat.setBorder(bordeNormal);
                    regCat.setForeground(Color.black);
                    band = false;
                }
                updateButtonSave();
            }
        } else if (AC_CANCEL_NEW_PRODUCT.equals(e.getActionCommand())) {
            tbProducts.setEnabled(true);
            resetPanelNewProduct();
            editCampos(false);
            btCancel.setVisible(false);
            btSave.setVisible(false);
            btSave.setActionCommand(AC_SAVE_NEW_PRODUCT);
            btRefreshList.setEnabled(true);
            lbTitlePress.setVisible(false);
            jScrollPane2.setVisible(false);
            panelContainPress.removeAll();
            btAddPress.setVisible(false);
            lbID.setBackground(new Color(120, 144, 240));
            lbID1.setBackground(new Color(120, 144, 240));
            if (tbProducts.getSelectedRow() != -1) {
                showProduct(currentProduct);
            }

        } else if (AC_SAVE_NEW_PRODUCT.equals(e.getActionCommand())) {
            Product product = parseProduct();
            if (product != null && app.getControl().addProduct(product)) {
                populateTable("");
                tbProducts.setEnabled(true);
                resetPanelNewProduct();
                btSave.setVisible(false);
                btCancel.setVisible(false);
            }
        } else if (AC_ENABLE_PRODUCT.equals(e.getActionCommand())) {
            int r = tbProducts.getSelectedRow();
            long idProd = currentProduct.getId();
            Product prod = app.getControl().getProductById(idProd);
            prod.setEnabled(!prod.isEnabled());
            currentProduct.setEnabled(!prod.isEnabled());
            app.getControl().updateProduct(prod);
            tbProducts.setValueAt(prod.isEnabled() ? "Habilitado" : "Deshabilitado", r, 4);
            showProduct(prod);

        } else if (AC_SAVE_EDIT.equals(e.getActionCommand())) {
            if (editingProduct != null) {
                long id = editingProduct.getId();
                Product product = parseProduct();
                if (product != null) {
                    product.setId(id);
                    if (app.getControl().updateProduct(product)) {
                        populateTable("");
                        tbProducts.setEnabled(true);
                        editCampos(false);
                        resetPanelNewProduct();
                        btSave.setVisible(false);
                        btCancel.setVisible(false);
                        editingProduct = null;
                        regPrice.setBorder(bordeNormal);
                        regPrice.setForeground(Color.black);
                        regDesc.setBorder(bordeNormal);
                        textArea.setForeground(Color.black);
                        regName.setBorder(bordeNormal);
                        regName.setForeground(Color.black);
                        regCode.setBorder(bordeNormal);
                        regCode.setForeground(Color.black);
                        regCat.setBorder(bordeNormal);
                        regCat.setForeground(Color.black);
                    }
                }
            }
        } else if (AC_ADD_PRESS.equals(e.getActionCommand())) {
            if (currentProduct != null) {
                app.getGuiManager().showPanelAddPress(this, currentProduct);
            }
        } else if (status == STATUS_EDITING && AC_SET_VARPRICE.equals(e.getActionCommand())) {
            if (editingProduct != null) {
                if (regVarPrice.isSelected() != editingProduct.isVariablePrice()) {
                    regVarPrice.setBorder(bordeEdit);
                    regVarPrice.setForeground(Color.blue);
                    band = true;
                } else {
                    regVarPrice.setBorder(bordeNormal);
                    regVarPrice.setForeground(Color.black);
                    band = false;
                }
                updateButtonSave();
            }
        }
    }

    @Override
    public void caretUpdate(CaretEvent e) {
        band = false;
        if (e.getSource().equals(regName.getComponent())) {
            if (editingProduct != null) {
                String value = regName.getText();
                try {
                    if (!editingProduct.getName().equalsIgnoreCase(value)) {
                        regName.setBorder(bordeEdit);
                        regName.setForeground(Color.blue);
                        band = true;
                    } else {
                        regName.setBorder(bordeNormal);
                        regName.setForeground(Color.black);
                    }
                } catch (Exception ex) {
                }
            }
        } else if (e.getSource().equals(regPrice.getComponent())) {
            if (editingProduct != null) {
                double value = Double.parseDouble(regPrice.getText());
                try {
                    if (editingProduct.getPrice() != value) {
                        regPrice.setBorder(bordeEdit);
                        regPrice.setForeground(Color.blue);
                        band = true;
                    } else {
                        regPrice.setBorder(bordeNormal);
                        regPrice.setForeground(Color.black);
                    }
                } catch (Exception ex) {
                }
            }
        } else if (e.getSource().equals(regDesc.getComponent())) {
            if (editingProduct != null) {
                String value = regDesc.getText();
                try {
                    if (!editingProduct.getDescription().equals(value)) {
                        regDesc.setBorder(bordeEdit);
                        textArea.setForeground(Color.blue);
                        band = true;
                    } else {
                        regDesc.setBorder(bordeNormal);
                        textArea.setForeground(Color.black);
                    }
                } catch (Exception ex) {
                }
            }
        } else if (e.getSource().equals(regCode.getComponent()) && status == STATUS_EDITING) {
            if (editingProduct != null) {
                String value = regCode.getText();
                try {
                    if (!editingProduct.getCode().equalsIgnoreCase(value)) {
                        regCode.setBorder(bordeEdit);
                        regCode.setForeground(Color.blue);
                        band = true;
                    } else {
                        regCode.setBorder(bordeNormal);
                        regCode.setForeground(Color.black);
                    }
                } catch (Exception ex) {
                }
            }
        }
        updateButtonSave();

    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

        if (PanelList.AC_SELECTED.equals(evt.getPropertyName())) {
            regCat.setText(evt.getNewValue().toString());
        } else if (PanelList.AC_ADD.equals(evt.getPropertyName())) {
            app.getControl().addCategory(evt.getNewValue().toString());
            updateCategoriesList();
        } else if (PanelList.AC_EDIT.equals(evt.getPropertyName())) {
            app.getControl().updateCategory(evt.getNewValue().toString(), evt.getOldValue().toString());
            updateCategoriesList();
        } else if (PanelList.AC_DELETE.equals(evt.getPropertyName())) {
            app.getControl().deleteUnit(evt.getNewValue().toString());
            updateCategoriesList();
        } else if (PanelPressProduct.AC_SAVE_PRESENTATION.equals(evt.getPropertyName())) {
            loadPresentations(currentProduct);
        } else if (PanelPressProduct.AC_EDIT_PRESENTATION.equals(evt.getPropertyName())) {
            loadPresentations(currentProduct);
        } else if (PanelPressProduct.AC_CHANGE_DEFAULT.equals(evt.getPropertyName())) {
            Component[] components = panelContainPress.getComponents();
            Presentation pres = (Presentation) evt.getNewValue();
            for (Component component : components) {
                PanelPressProduct pnPres = (PanelPressProduct) component;
                pnPres.changeToDefault(false);

                if (pnPres.getPresentation().getId() == pres.getId()) {
                    pnPres.changeToDefault(true);
                }
                pnPres.updateUI();
            }
            panelContainPress.updateUI();
        }
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        if (e.getType() == TableModelEvent.UPDATE) {
            try {
                tbProducts.updateUI();
            } catch (Exception ex) {
            }

        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
//        int row = e.getLastIndex();
        int row = tbProducts.getSelectedRow();
        if (row < 0) {
            showProduct(null);
        } else {
            try {
                String id = tbProducts.getValueAt(row, 0).toString();
                Product prod = app.getControl().getProductById(Long.valueOf(id));
                showProduct(prod);
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage());
            }
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btAddPress;
    private javax.swing.JButton btBuscar;
    private javax.swing.JButton btCancel;
    private javax.swing.JButton btNewProduct;
    private javax.swing.JButton btRefreshList;
    private javax.swing.JButton btSave;
    private javax.swing.JCheckBox chEnableProd;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JLabel lbID;
    private javax.swing.JLabel lbID1;
    private javax.swing.JLabel lbTitlePress;
    private javax.swing.JPanel panelContainPress;
    private javax.swing.JPanel pnCtrl;
    private org.dz.Registro regCat;
    private org.dz.Registro regCode;
    private org.dz.Registro regDesc;
    private org.dz.Registro regFilterCat;
    private org.dz.Registro regName;
    private org.dz.Registro regPrice;
    private org.dz.Registro regSearch;
    private org.dz.Registro regVarPrice;
    private javax.swing.JTable tbProducts;
    // End of variables declaration//GEN-END:variables

    public class TablaCellRenderer extends JLabel implements TableCellRenderer {

        boolean isBordered = true;
        private boolean disabled;
        private final Format formatter;

        public TablaCellRenderer(boolean isBordered, Format formatter) {
            super();
            this.isBordered = isBordered;
            this.formatter = formatter;
            disabled = false;
            setFont(new Font("tahoma", 0, 12));
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            int r = table.convertRowIndexToModel(row);
            if ("habilitado".equalsIgnoreCase(table.getModel().getValueAt(r, 4).toString())) {
                disabled = false;
            } else {
                disabled = true;
            }

            if (value != null) {
                if (formatter != null) {
                    try {
                        setHorizontalAlignment(SwingConstants.RIGHT);
                        value = formatter.format(value);
                    } catch (IllegalArgumentException e) {
                    }
                }
                setText(value.toString().toUpperCase());
            }
            if (isSelected) {
                setForeground(!disabled ? Color.black : Color.red);
                setBackground(tbProducts.getSelectionBackground());
                if (hasFocus) {
                    setBorder(BorderFactory.createLineBorder(Color.darkGray));
                } else {
                    setBorder(createLineBorder(Color.lightGray));
                }
            } else {
                setBackground(tbProducts.getBackground());
                setForeground(!disabled ? Color.black : Color.red);
                setBorder(UIManager.getBorder("Table.cellBorder"));
            }
            if (!table.isEnabled()) {
                setForeground(Color.gray);
            }
            return this;
        }
    }

    public class RadioButtonCellRenderer extends JRadioButton implements TableCellRenderer {

        public RadioButtonCellRenderer(String text, boolean selected) {
            setText(text);
            setSelected(selected);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

            if (isSelected) {
                setForeground(Color.black);
                setBackground(table.getSelectionBackground());
                if (hasFocus) {
                    setBorder(BorderFactory.createLineBorder(Color.darkGray));
                } else {
                    setBorder(createLineBorder(Color.lightGray));
                }
            } else {
                setBackground(table.getBackground());
                setForeground(Color.black);
                setBorder(UIManager.getBorder("Table.cellBorder"));
            }
            return this;
        }
    }

    public class RadioButtonEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {

        Boolean currentValue;
        JRadioButton button;
        protected static final String EDIT = "edit";
        private JTable tabla;
        private ActionListener acList;
        private String acCommand;

        public RadioButtonEditor(JTable tabla, ActionListener listener, String acCommand) {
            button = new JRadioButton();
            button.setBorderPainted(false);
            this.tabla = tabla;
            this.acList = listener;
            this.acCommand = acCommand;
            button.setActionCommand(acCommand);
            button.addActionListener(RadioButtonEditor.this);
        }

        @Override
        public boolean isCellEditable(EventObject e) {
            if (e instanceof MouseEvent) {
                return ((MouseEvent) e).getClickCount() >= 1;
            }
            return true;
        }

        @Override
        public Object getCellEditorValue() {
            return currentValue;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            currentValue = (Boolean) value;
            return button;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            final int c = tabla.getEditingColumn();
            final int f = tabla.getEditingRow();
            if (f != -1 && c != -1) {
                int row = tabla.convertRowIndexToModel(f);
                String id = model.getValueAt(row, 0).toString();
                button.setSelected(!button.isSelected());

            }
            try {
                fireEditingStopped();
            } catch (Exception ex) {
                System.err.println(ex.getMessage());
            }
        }
    }

    public class MyPopupListener implements MouseListener {

        boolean seleccionar;

        public MyPopupListener() {
            this.seleccionar = true;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            isPopupTrigger(e);
        }

        @Override
        public void mousePressed(MouseEvent e) {
            isPopupTrigger(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            isPopupTrigger(e);
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            isPopupTrigger(e);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            isPopupTrigger(e);
        }

        public void isPopupTrigger(MouseEvent e) {
            if (e.isPopupTrigger()) {
                if (seleccionar) {
                    Object source = e.getSource();
                    if (source != null && (source instanceof JTable)) {
                        JTable table = (JTable) e.getSource();
                        int rowAtPoint = table.rowAtPoint(e.getPoint());
                        table.getSelectionModel().setSelectionInterval(rowAtPoint, rowAtPoint);
                        int row = table.getSelectedRow();
                        makePopup(row).show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            }

        }

    }

}
