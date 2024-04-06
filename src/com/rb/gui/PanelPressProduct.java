package com.rb.gui;

import com.rb.Aplication;
import com.rb.GUIManager;
import com.rb.domain.Presentation;
import com.rb.domain.Product;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import org.dz.PanelCapturaMod;

/**
 *
 * @author lrod
 */
public class PanelPressProduct extends PanelCapturaMod implements ActionListener, CaretListener {

    private final Aplication app;
    private Presentation presentation;
    private Color color1;
    private Color color2;
    private Color color3;
    private Font fontLabel;
    private Product prod;
    private JPopupMenu popup;

    private int status;
    public static final int STATUS_NORMAL = 0;
    public static final int STATUS_EDITING = 1;
    private boolean band;
    private Color BCBACK;
    private Presentation editingPres;

    /**
     * Creates new form PanelPressProduct
     *
     * @param app
     * @param pres
     */
    public PanelPressProduct(Aplication app, Presentation pres) {
        this.app = app;
        this.presentation = pres;
        initComponents();
        createComponents();
    }

    public PanelPressProduct(Aplication app, Product prod) {
        this.app = app;
        this.prod = prod;
        initComponents();
        createComponents();
    }

    private void createComponents() {
        status = STATUS_NORMAL;
        lbTitle.setOpaque(true);
        color1 = new Color(100, 200, 160);
        color2 = new Color(100, 190, 180);
        color3 = new Color(255, 120, 130);
        lbTitle.setBackground(color1);
        lbTitle.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 5));
        Font font = new Font("Sans", 1, 14);
        fontLabel = new Font("arial", 0, 11);
        lbTitle.setFont(font);
        regName.setFont(font);
        regName.setLabelFont(fontLabel);
        regPrice.setFont(font);
        regPrice.setLabelFont(fontLabel);

        btCancel.setActionCommand(AC_CANCEL_PANEL);
        btCancel.addActionListener(this);
        btCancel.setVisible(false);

        btSave.setActionCommand(AC_SAVE_PRESENTATION);
        btSave.addActionListener(this);
        btSave.setVisible(false);
        
        BCBACK = btSave.getBackground();

        setBorder(BorderFactory.createLineBorder(color1.darker(), 2, true));
        setPresentation(presentation);
        if (presentation != null) {
            if (presentation.isDefault()) {
                chEnable.setVisible(true);
                chEnable.setBackground(color3);
                setBorder(BorderFactory.createLineBorder(color3, 2, true));
                lbTitle.setBackground(color3);
                chEnable.setBackground(color3);
                rbDefault.setEnabled(false);
            } else {
                setBorder(BorderFactory.createLineBorder(color1.darker(), 2, true));
                lbTitle.setBackground(color1);
                chEnable.setBackground(color1);
            }
            if (!presentation.isEnabled()) {
                changeToEnabled(false);
            }

        }

        if (prod != null) {
            lbTitle.setText(prod.getName().toUpperCase());
        }

        chEnable.setToolTipText("Habilitar");
        chEnable.setActionCommand(AC_ENABLE_PRESS);
        chEnable.addActionListener(this);

        rbDefault.setActionCommand(AC_CHANGE_DEFAULT);
        rbDefault.addActionListener(this);
        rbDefault.setText("Default");

        btEdit.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "edit.png", 16, 16)));
        btEdit.setText("");
        btEdit.setActionCommand(AC_EDIT_PRESENTATION);
        btEdit.addActionListener(this);

    }
    public static final String AC_EDIT_PRESENTATION = "AC_EDIT_PRESENTATION";
    public static final String AC_SAVE_PRESENTATION = "AC_SAVE_PRESENTATION";
    public static final String AC_CANCEL_PANEL = "AC_CANCEL_PANEL";
    public static final String AC_CHANGE_DEFAULT = "AC_CHANGE_DEFAULT";
    public static final String AC_ENABLE_PRESS = "AC_ENABLE_PRESS";

    public void setPresentation(Presentation pres) {
        this.presentation = pres;
        if (pres != null) {
            lbTitle.setText(String.valueOf(pres.getId()));
            regName.setText(pres.getName().toUpperCase());
            regPrice.setText(app.getDCFORM_W().format(pres.getPrice()));
            chEnable.setSelected(pres.isEnabled());
            rbDefault.setSelected(pres.isDefault());
            regName.setEditable(false);
            regPrice.setEditable(false);
            StringBuilder stb = new StringBuilder();
            stb.append("<html><h3 size=+1 color=#1144ff>")
                    .append(pres.getName().toUpperCase())
                    .append("<h4 color=red>")
                    .append(app.getDCFORM_W().format(pres.getPrice()))
                    .append("<p color=#aa2277>")
                    .append(pres.isEnabled() ? "<strong>Habilitado<strong>" : "Deshabilitado")
                    .append("</html>");
            setToolTipText(stb.toString());
            regName.setToolTipTextfull(stb.toString());
            regPrice.setToolTipTextfull(stb.toString());
        }
    }

    private Presentation parsePresentation() {
        Presentation pres = null;
        boolean valido = true;
        String name = regName.getText().trim();
        if (name.trim().isEmpty()) {
            regName.setBorder(bordeError);
            valido = false;
        } else {
            if (status == STATUS_EDITING) {
//                int existClave = app.getControl().existClaveMult("presentation_product", "name", "name='" + name + "' AND product_id=" + presentation.getIDProd());
//
//                if (existClave > 0) {
//                    GUIManager.showErrorMessage(this, "<html><p>Ya existe una presentacion registrada con este nombre:"
//                            + "<p color=red size=+1>" + name.toUpperCase() + "</html>", "ADVERTENCIA");
//                    regName.setForeground(Color.red);
//                    valido = false;
//                }
            } else {
                int existClave = app.getControl().existClaveMult("presentation_product", "name", "name='" + name + "' AND product_id=" + prod.getId());

                if (existClave > 0) {
                    GUIManager.showErrorMessage(this, "<html><p>Ya existe una presentacion registrada con este nombre:"
                            + "<p color=red size=+1>" + name.toUpperCase() + "</html>", "ADVERTENCIA");
                    regName.setForeground(Color.red);
                    valido = false;
                }
            }
        }
        if (regPrice.getText().trim().isEmpty()) {
            regPrice.setBorder(bordeError);
            valido = false;
        }

        if (valido) {
            pres = new Presentation();

            pres.setName(regName.getText().trim().toLowerCase());
            pres.setPrice(Double.parseDouble(regPrice.getText()));
            pres.setSerie(1);
            pres.setDefault(rbDefault.isSelected());
            pres.setEnabled(true);
            pres.setIDProd(status == STATUS_EDITING ? presentation.getIDProd() : prod.getId());
            if (status == STATUS_EDITING) {
                pres.setId(presentation.getId());
                pres.setEnabled(presentation.isEnabled());
            }
        }
        return pres;
    }

    public void showNewPresentacionMode() {
        status = STATUS_NORMAL;
        regName.setOrientation(BoxLayout.X_AXIS, 95);
        regName.setLabelFont(new Font("arial", 1, 12));
        regName.removeCaretListener(this);
        regPrice.setOrientation(BoxLayout.X_AXIS, 95);
        regPrice.setLabelFont(new Font("arial", 1, 12));
        regPrice.removeCaretListener(this);
        btCancel.setVisible(true);
        btSave.setVisible(true);
        chEnable.setVisible(false);
        btCancel.setText("Cancelar");
        btSave.setText("Guardar");
        btEdit.setVisible(false);
        updateUI();
    }

    public void showEditPresentacionMode() {
        status = STATUS_EDITING;
        editingPres = presentation;
        regName.setOrientation(BoxLayout.X_AXIS, 95);
        regName.setLabelFont(new Font("arial", 1, 12));
        regName.addCaretListener(this);
        regPrice.setOrientation(BoxLayout.X_AXIS, 95);
        regPrice.setLabelFont(new Font("arial", 1, 12));
        regPrice.addCaretListener(this);
        btCancel.setVisible(true);
        btSave.setVisible(true);
        btSave.setEnabled(false);
        btSave.setActionCommand(AC_UPDATE_PRESENTATION);
        chEnable.setVisible(false);
        btCancel.setText("Cancelar");
        btSave.setText("Guardar");
        btEdit.setVisible(false);
        regName.setEditable(true);
        regName.setEnabled(true);
        regPrice.setEditable(true);
        regPrice.setEnabled(true);
        updateUI();
    }
    public static final String AC_UPDATE_PRESENTATION = "AC_UPDATE_PRESENTATION";

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lbTitle = new javax.swing.JLabel();
        regName = new org.dz.Registro(BoxLayout.Y_AXIS,"Presentacion","");
        regPrice = new org.dz.Registro(BoxLayout.Y_AXIS,"Precio","");
        chEnable = new javax.swing.JCheckBox();
        rbDefault = new javax.swing.JRadioButton();
        btSave = new javax.swing.JButton();
        btCancel = new javax.swing.JButton();
        btEdit = new javax.swing.JButton();

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(lbTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(chEnable, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(regName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(rbDefault)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btCancel, javax.swing.GroupLayout.DEFAULT_SIZE, 62, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btSave, javax.swing.GroupLayout.DEFAULT_SIZE, 62, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(regPrice, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(6, 6, 6))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lbTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chEnable))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(regName, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(regPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(rbDefault, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btSave, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btCancel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btEdit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(3, 3, 3))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {chEnable, lbTitle});

    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btCancel;
    private javax.swing.JButton btEdit;
    private javax.swing.JButton btSave;
    private javax.swing.JCheckBox chEnable;
    private javax.swing.JLabel lbTitle;
    private javax.swing.JRadioButton rbDefault;
    private org.dz.Registro regName;
    private org.dz.Registro regPrice;
    // End of variables declaration//GEN-END:variables

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public void caretUpdate(CaretEvent e) {
        band = false;
        if (e.getSource().equals(regName.getComponent())) {
            if (editingPres != null) {
                String value = regName.getText();
                try {
                    if (!editingPres.getName().equalsIgnoreCase(value)) {
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
            if (editingPres != null && !regPrice.getText().isEmpty()) {
                double value = Double.parseDouble(regPrice.getText());
                try {
                    if (editingPres.getPrice() != value) {
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
        }
        updateButtonSave();

    }
    
    private void updateButtonSave() {
        if (band) {

            btSave.setToolTipText("Existen cambios sin guardar");
            btSave.setEnabled(true);
            btSave.setActionCommand(AC_UPDATE_PRESENTATION);

        } else {
            btSave.setBackground(BCBACK);
            btSave.setToolTipText("Todos los datos estan guardados");
            btSave.setEnabled(false);
//            btAdd.removeActionListener(this);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (AC_ENABLE_PRESS.equals(e.getActionCommand())) {
            boolean selected = chEnable.isSelected();
            presentation.setEnabled(selected);
            if (app.getControl().updatePresentation(presentation)) {
                changeToEnabled(selected);
            } else {
                presentation.setEnabled(!selected);
                changeToEnabled(!selected);
                chEnable.setSelected(!selected);
            }
        } else if (AC_CHANGE_DEFAULT.equals(e.getActionCommand())) {
            boolean selected = rbDefault.isSelected();
            if (app.getControl().updatePresentationToDefault(presentation)) {
                changeToDefault(selected);
                pcs.firePropertyChange(AC_CHANGE_DEFAULT, null, presentation);
            }

        } else if (AC_CANCEL_PANEL.equals(e.getActionCommand())) {
            cancelPanel();
        } else if (AC_SAVE_PRESENTATION.equals(e.getActionCommand())) {
            Presentation presentation = parsePresentation();
            if (presentation != null) {
                app.getControl().addPresentation(presentation);
                pcs.firePropertyChange(AC_SAVE_PRESENTATION, null, presentation);
                cancelPanel();
            }
        } else if (AC_UPDATE_PRESENTATION.equals(e.getActionCommand())) {
            Presentation presentation = parsePresentation();
            if (presentation != null) {
                app.getControl().updatePresentation(presentation);
                pcs.firePropertyChange(AC_EDIT_PRESENTATION, null, presentation);
                cancelPanel();                
            }
        } else if (AC_EDIT_PRESENTATION.equals(e.getActionCommand())) {
            app.getGuiManager().showPanelEditPress(app.getGuiManager().getPanelAddProduct(), presentation);
        }
    }

    public void changeToEnabled(boolean selected) {
        regName.setEnabled(selected);
        regPrice.setEnabled(selected);
        lbTitle.setEnabled(selected);
        lbTitle.setBackground(selected ? color1 : color2);
        chEnable.setBackground(selected ? color1 : color2);
        changeToDefault(rbDefault.isSelected());
        rbDefault.setEnabled(selected);
    }

    public void changeToDefault(boolean selected) {
        if (selected) {
            rbDefault.setSelected(true);
            rbDefault.setEnabled(false);
            setBorder(BorderFactory.createLineBorder(color3, 2, true));
            lbTitle.setBackground(color3);
            chEnable.setBackground(color3);
        } else {
            rbDefault.setSelected(false);
            rbDefault.setEnabled(true);
            setBorder(BorderFactory.createLineBorder(color1.darker(), 2, true));
            lbTitle.setBackground(color1);
            chEnable.setBackground(color1);
        }
    }

    public boolean isDefaultPres() {
        if (presentation != null) {
            return presentation.isDefault();
        }
        return false;
    }

    public boolean isEnabledPres() {
        if (presentation != null) {
            return presentation.isEnabled();
        }
        return false;
    }

    public Presentation getPresentation() {
        return presentation;
    }

}
