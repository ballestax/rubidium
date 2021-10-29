package com.bacon.gui;

import com.bacon.Aplication;
import com.bacon.Configuration;
import com.bacon.domain.Category;
import com.bacon.domain.ConfigDB;
import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import org.dz.PanelCapturaMod;
import org.dz.Utiles;

/**
 *
 * @author lrod
 */
public class PanelSelCategory extends PanelCapturaMod implements ActionListener {

    private ArrayList<Category> categories;
    private final Aplication app;
    private Box boxContainer;
    private JPopupMenu popupExtraCategories;
    private JButton btExtras;

    public PanelSelCategory(Aplication app, ArrayList<Category> categories) {
        this.app = app;
        this.categories = categories;

        createComponents();
    }

    private void createComponents() {
        setLayout(new BorderLayout());
        Border marginBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
        setBorder(BorderFactory.createCompoundBorder(marginBorder,
                BorderFactory.createEtchedBorder(EtchedBorder.LOWERED)));

        boxContainer = new Box(BoxLayout.X_AXIS);

        loadCategories();
        add(boxContainer);
    }

    private void loadCategories() {
       
        boxContainer.removeAll();
        remove(boxContainer);
        
        ConfigDB config = app.getControl().getConfig(Configuration.MAX_CATEGORIES_LIST);
        int MAX = config != null ? (int) config.castValor() : 6;

        if (categories != null) {
            for (int i = 0; i < categories.size() && i <= MAX; i++) {
                JButton btCategory = new JButton();
                btCategory.setBackground(categories.get(i).getColor());
                btCategory.setMargin(new Insets(1, 1, 1, 1));
                String name = categories.get(i).getName().toUpperCase();
                btCategory.setText(name);
                btCategory.setActionCommand(SEL_CAT_ + name);
                btCategory.addActionListener(this);
                boxContainer.add(btCategory);
                boxContainer.add(Box.createHorizontalStrut(2));
            }
            if (categories.size() - 1 > MAX) {
                btExtras = new JButton();
                btExtras.setBackground(Utiles.colorAleatorio(125, 255));
                btExtras.setMargin(new Insets(1, 1, 1, 1));
                String name = "...";
                btExtras.setText(name);
                btExtras.setActionCommand(SHOW_EXTRA_CATEGORIES);
                btExtras.addActionListener(this);
                boxContainer.add(Box.createHorizontalGlue());
                boxContainer.add(btExtras);

                popupExtraCategories = new JPopupMenu();
                for (int i = MAX + 1; i < categories.size(); i++) {
                    JMenuItem item = new JMenuItem(categories.get(i).getName().toUpperCase());
                    item.addActionListener(this);
                    item.setActionCommand(SEL_CAT_ + categories.get(i).getName());
                    popupExtraCategories.add(item);
                }
            }
        }        
    }
    
    private static final String SHOW_EXTRA_CATEGORIES = "SHOW_EXTRA_CATEGORIES";
    public static final String SEL_CAT_ = "SEL_CAT_";

    public void setCategories(ArrayList<Category> categories) {
        this.categories = categories;
        reload();
    }

    public void reload() {
        removeAll();
        createComponents();
    }

    @Override
    public void reset() {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(SHOW_EXTRA_CATEGORIES)) {
            popupExtraCategories.show(btExtras, -popupExtraCategories.getWidth() + btExtras.getWidth(), btExtras.getHeight());
        } else {
            pcs.firePropertyChange(e.getActionCommand(), null, null);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (PanelTopSearch.AC_REFRESH_PRODUCTS.equals(evt.getPropertyName())) {
            reload();
            updateUI();
        }
    }

}
