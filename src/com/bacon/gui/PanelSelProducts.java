package com.bacon.gui;

import com.bacon.Aplication;
import com.bacon.Configuration;
import com.bacon.domain.Category;
import com.bacon.domain.ConfigDB;
import com.bacon.domain.Product;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.dz.Utiles;

/**
 *
 * @author lrod
 */
public class PanelSelProducts extends JPanel implements ActionListener, PropertyChangeListener {

    private ArrayList<Product> productsList;
    private Box panelTop;
    private PanelTopSearch panelTopSearch;
    private Aplication app;
    private PanelSelCategory panelSelCategory;
    private ArrayList<Category> categorys;
    private Box panelLeft;
    private PanelCategory panelCategory;
    private Comparator<Product> compAlpha;
    private Comparator<Product> compPrice;
    private PanelOrders pnOrders;

    public PanelSelProducts(Aplication app) {
        this.app = app;
        createComponents();
    }

    private void createComponents() {
        
        System.out.println("creating components");

        compAlpha = Comparator.comparing(product -> product.getName().toLowerCase());
        compPrice = Comparator.comparing(Product::getPrice)
                .thenComparing(product -> product.getName().toLowerCase());

        setLayout(new BorderLayout());

        panelTop = new Box(BoxLayout.Y_AXIS);

        panelTopSearch = new PanelTopSearch(app);
        panelTopSearch.addPropertyChangeListener(this);

        panelTop.add(panelTopSearch);

        panelSelCategory = app.getGuiManager().getPanelSelCategory();
        panelSelCategory.addPropertyChangeListener(this);
        panelTopSearch.addPropertyChangeListener(panelSelCategory);

        categorys = app.getControl().getCategoriesList();
        categorys.stream().forEach(category -> category.setColor(Utiles.colorAleatorio(200, 250)));
        ConfigDB config = app.getControl().getConfig(Configuration.MAX_CATEGORIES_LIST);
        int MAX = config != null ? (int) config.castValor() : 4;
        if (categorys.size() < MAX) {
            categorys = app.getControl().getAllCategoriesList();
        }
        Category CAT_TODOS = new Category("TODOS");
        CAT_TODOS.setColor(Color.LIGHT_GRAY.brighter());
        categorys.add(0, CAT_TODOS);

        panelSelCategory.setCategories(categorys);

        panelTop.add(panelSelCategory);

        add(panelTop, BorderLayout.NORTH);

        panelLeft = new Box(BoxLayout.Y_AXIS);
        JScrollPane scp = new JScrollPane(panelLeft);
        scp.getVerticalScrollBar().setUnitIncrement(30);
        add(scp, BorderLayout.CENTER);

        productsList = new ArrayList<>();
        loadAllProducts();
        
        pnOrders = app.getGuiManager().getPanelOrders();

        panelCategory = new PanelCategory(categorys, productsList, app, pnOrders);
//        panelCategory.addPropertyChangeListener(pnPedido);
//        panelCategory.addPropertyChangeListener(pnOrders);
        panelTopSearch.addPropertyChangeListener(panelCategory);
        PropertyChangeListener[] propertyListeners = panelCategory.getPropertyListeners();
        for (PropertyChangeListener propertyListener : propertyListeners) {
            System.out.println(propertyListener);
        }
        
        panelLeft.add(panelCategory);

    }

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
        }
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

}
