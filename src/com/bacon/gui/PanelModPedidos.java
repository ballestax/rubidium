/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bacon.gui;

import com.bacon.domain.Category;
import com.bacon.Aplication;
import com.bacon.Configuration;
import com.bacon.domain.ConfigDB;
import com.bacon.domain.Product;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.dz.PanelCapturaMod;
import org.dz.Utiles;


/**
 *
 * @author lrod
 */
public class PanelModPedidos extends PanelCapturaMod
        implements ActionListener {
    
    private final Aplication app;
    private JPanel pnCont;
    private Box panelLeft;
    private Box panelTop;
    private PanelPedido pnPedido;
    private ArrayList<Category> categorys;
    private ArrayList<Product> productsList;
    private PanelTopSearch panelTopSearch;
    private PanelCategory panelCategory;
    private PanelSelCategory panelSelCategory;
    private Comparator<Product> compAlpha;
    private Comparator<Product> compPrice;

    /**
     * Creates new form PanelPedidos
     *
     * @param app
     */
    public PanelModPedidos(Aplication app) {
        this.app = app;
        initComponents();
        createComponents();
    }
    
    private void createComponents() {
        
        categorys = new ArrayList<>();
        categorys.add(new Category("Productos"));
        pnCont = new JPanel(new BorderLayout());
        
        compAlpha = Comparator.comparing(product -> product.getName().toLowerCase());
        compPrice = Comparator.comparing(Product::getPrice)
                .thenComparing(product -> product.getName().toLowerCase());
        
        panelTop = new Box(BoxLayout.Y_AXIS);
        
        panelTopSearch = new PanelTopSearch(app);
        panelTopSearch.addPropertyChangeListener(this);
        
        panelTop.add(panelTopSearch);
        
        panelSelCategory = app.getGuiManager().getPanelSelCategory();
        panelSelCategory.addPropertyChangeListener(this);
        panelTopSearch.addPropertyChangeListener(panelSelCategory);
        
        categorys = app.getControl().getCategoriesList();
        categorys.stream().forEach(category -> category.setColor(Utiles.colorAleatorio(200, 250)));
        ConfigDB config = app.getControl().getConfigLocal(Configuration.MAX_CATEGORIES_LIST);
        int MAX = config != null ? (int) config.castValor() : 4;
        if (categorys.size() < MAX) {
            categorys = app.getControl().getAllCategoriesList();
        }
        Category CAT_TODOS = new Category("TODOS");
        CAT_TODOS.setColor(Color.LIGHT_GRAY.brighter());
        categorys.add(0, CAT_TODOS);
        
        panelSelCategory.setCategories(categorys);
        
        panelTop.add(panelSelCategory);
        
        pnCont.add(panelTop, BorderLayout.NORTH);
        
        panelLeft = new Box(BoxLayout.Y_AXIS);
        JScrollPane scp = new JScrollPane(panelLeft);
        scp.getVerticalScrollBar().setUnitIncrement(30);
        pnCont.add(scp, BorderLayout.CENTER);
        splitPane.setLeftComponent(pnCont);
        
        pnPedido = app.getGuiManager().getPanelPedido();
        
        splitPane.setRightComponent(pnPedido);
        splitPane.setOneTouchExpandable(true);
        
        productsList = new ArrayList<>();
        loadAllProducts();
        
        panelCategory = new PanelCategory(categorys, productsList, app);
        panelCategory.addPropertyChangeListener(pnPedido);
        panelTopSearch.addPropertyChangeListener(panelCategory);
        
        panelLeft.add(panelCategory);
    }
    
    private void loadAllProducts() {
        String order = app.getConfiguration().getProperty(Configuration.PROD_ORDER, "----");
        String orderBy = "";
        if (PanelCategory.ORDEN_ALPHA.equalsIgnoreCase(order)) {
            orderBy = "name";
        } else if (PanelCategory.ORDEN_PRICE.equalsIgnoreCase(order)) {
            orderBy = "price, name";
        }
        
        productsList = app.getControl().getProductsList("enabled=1", orderBy);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        splitPane = new javax.swing.JSplitPane();

        setLayout(new java.awt.BorderLayout());
        add(splitPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    @Override
    
    public void actionPerformed(ActionEvent e) {
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (PanelTopSearch.AC_FILTER_PRODUCTS.equals(evt.getPropertyName())) {
            ArrayList<Product> productsList = (ArrayList<Product>) evt.getNewValue();
            
            if (productsList == null) {
//                panelLeft.removeAll();
                productsList = this.productsList;
                panelCategory.setProducts(productsList);
//                loadAllProducts();
            } else {
//                panelLeft.removeAll();
                panelCategory.setProducts(productsList);
                panelLeft.updateUI();
            }
            this.updateUI();
        } else if (evt.getPropertyName().startsWith(PanelSelCategory.SEL_CAT_)) {
            String cat = evt.getPropertyName().substring(8).toLowerCase();
            
            Comparator<Product> comparator = Comparator.<Product>naturalOrder();
            if (PanelCategory.ORDEN_ALPHA.equalsIgnoreCase(panelCategory.getSelectedSort())) {
                comparator = compAlpha;
            } else if (PanelCategory.ORDEN_PRICE.equalsIgnoreCase(panelCategory.getSelectedSort())) {
                comparator = compPrice;
            }
            
            List<Product> list = this.productsList;
            
            if (!"TODOS".equalsIgnoreCase(cat) && !"...".equalsIgnoreCase(cat)) {
                list = productsList.stream().filter(p -> p.getCategory().equalsIgnoreCase(cat)).sorted(comparator).collect(Collectors.toList());
            } else {
                list = productsList.stream().sorted(comparator).collect(Collectors.toList());
            }
            panelCategory.setProducts(list);
            panelLeft.updateUI();
        }else if(evt.getPropertyName().equals(PanelTopSearch.AC_REFRESH_PRODUCTS)){
            System.out.println("Refrescando productos");
            loadAllProducts();
        }
        
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSplitPane splitPane;
    // End of variables declaration//GEN-END:variables
}
