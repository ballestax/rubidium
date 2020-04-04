package com.bacon.gui;

import com.bacon.Aplication;
import com.bacon.Configuration;
import com.bacon.domain.Category;
import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import org.dz.PanelCaptura;
import org.dz.Utiles;

/**
 *
 * @author lrod
 */
public class PanelSelCategory extends PanelCaptura implements ActionListener {

    private ArrayList<Category> categories;
    private final Aplication app;
    private Box boxContainer;

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

        int MAX = app.getConfiguration().getProperty(Configuration.MAX_CATEGORIES_LIST, 5);

        if (categories != null) {
            for (int i = 0; i < categories.size() && i <= MAX; i++) {
                JButton btCategory = new JButton();
//                btCategory.setBorderPainted(false);
                btCategory.setBackground(Utiles.colorAleatorio(125, 255));
                btCategory.setMargin(new Insets(1, 1, 1, 1));
                String name = categories.get(i).getName().toUpperCase();
                btCategory.setText(name);
                btCategory.setActionCommand(SEL_CAT_ + name);
                btCategory.addActionListener(this);
                boxContainer.add(btCategory);
                boxContainer.add(Box.createHorizontalStrut(10));
            }
        }

        boxContainer.add(Box.createHorizontalGlue());

//        if (categories != null && !categories.isEmpty()) {
//            JComboBox<Category> moreCategories = new JComboBox<>();
//            moreCategories.setPreferredSize(new Dimension(120, 30));
//            moreCategories.setModel(new DefaultComboBoxModel<>(categories.toArray(new Category[0])));
////            boxContainer.add(moreCategories);
//        }
        add(boxContainer);

    }
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
        pcs.firePropertyChange(e.getActionCommand(), null, null);
    }

}
