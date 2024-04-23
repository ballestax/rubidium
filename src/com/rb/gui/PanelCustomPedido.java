/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rb.gui;

import com.rb.Aplication;
import com.rb.domain.Additional;
import com.rb.domain.AdditionalPed;
import com.rb.domain.Ingredient;
import com.rb.domain.Presentation;
import com.rb.domain.Product;
import com.rb.domain.ProductoPed;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.Document;

import org.dz.PanelCapturaMod;
import org.dz.SpinnerNumberModelo;
import org.dz.Utiles;

/**
 *
 * @author lrod
 */
public class PanelCustomPedido extends PanelCapturaMod implements ActionListener {
    
    private final Aplication app;
    private final Product product;
    private DecimalFormat DCFORM_P;
    private SpinnerNumberModelo spModel;
    private ArrayList<Ingredient> ingredients;
    private Document document;
    private SpinnerNumberModelo spPriceModel;
    private NumberFormat NF;
    private ButtonGroup bgPres;
    private JPanel pnAdditionals;
    private JPanel pnCoccion;
    private boolean modeEdit;
    private int rowSelected;

    /**
     * Creates new form PanelCustomPedido
     *
     * @param app
     * @param product
     */
    public PanelCustomPedido(Aplication app, Product product) {
        this.app = app;
        this.product = product;
        initComponents();
        createComponents();
    }
    
    private void createComponents() {
        modeEdit = false;
        
        spModel = new SpinnerNumberModelo(1, 1, null, 1);
        spPriceModel = new SpinnerNumberModelo(product.getPrice(), product.getPrice(), null, 1000d);
        
        String image = product.getImage();
        
        ImageIcon icon = new ImageIcon(app.getImgManager().getImagen(image, 100, 100));
        
        Font font1 = new Font("Tahoma", 1, 17);
        Font font2 = new Font("Serif", 2, 12);
        Font font3 = new Font("Sans", 1, 16);
        
        NF = DecimalFormat.getCurrencyInstance();
        NF.setMaximumFractionDigits(0);
        
        lbImage.setIcon(icon);
        lbName.setFont(font1);
        lbName.setText(product.getName().toUpperCase());
        lbName.setOpaque(true);
        lbName.setForeground(Color.blue.darker().darker());
        lbName.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.red));
        lbDescription.setFont(new Font("Serif", 0, 10));
        lbDescription.setFont(font2);
        lbDescription.setText("<html><p>" + product.getDescription() + "</p></html>");
        lbDescription.setOpaque(true);
//        lbDescription.setForeground(Color.gray);
        lbPrice.setText(NF.format(product.getPrice()));
        lbPrice.setFont(font3);
        lbPrice.setOpaque(true);
        lbPrice.setForeground(Color.red.darker());
        lbPrice.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 2, Color.red));
        
        spPrice.setValue((product.getPrice()));
        spPrice.setFont(font3);
        spPrice.setOpaque(true);
        spPrice.setForeground(Color.red.darker());
        spPrice.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 2, Color.red));
//        spPrice.setHorizontalAlignment(SwingConstants.RIGHT);

        spPrice.setModel(spPriceModel);
        
        DCFORM_P = (DecimalFormat) NumberFormat.getInstance();
        DCFORM_P.applyPattern("$ ###,###,###");
        
        lbCant.setText("Cantidad");
        spCantidad.setModel(spModel);
        spCantidad.setForeground(Color.red.darker());
        
        spCantidad.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                mostrarTotal();
            }
            
        });
        
        spPrice.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                mostrarTotal();
            }
            
        });
        
        spCantidad.requestFocus();
        
        chEntry.setActionCommand("AC_CHECK_ENTRY");
        chEntry.addActionListener(this);
        chEntry.setText("Entrada");
        
        btConfirm.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons() + "success.png", 10, 10)));
        btConfirm.setBackground(new Color(153, 255, 153));
        btConfirm.setMargin(new Insets(1, 1, 1, 1));
        btConfirm.setFont(new Font("Arial", 1, 11));
        btConfirm.setActionCommand(AC_CONFIRMAR_PEDIDO);
        btConfirm.addActionListener(this);
        btConfirm.setText("CONFIRMAR");
        
        lbInfo.setText(spCantidad.getValue() + " " + product.getName());
        
        pnIngredients.setLayout(new GridLayout(0, 3, 5, 5));
        ingredients = app.getControl().getIngredientsByProduct(product.getCode());
        
        for (int i = 0; i < ingredients.size(); i++) {
            Ingredient ing = ingredients.get(i);
            if (ing.isOpcional()) {
                JCheckBox check = new JCheckBox("Sin " + ing.getName());
                check.setActionCommand(ing.getCode());
                check.setBorderPainted(true);
                check.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createEtchedBorder(),
                        BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));
                pnIngredients.add(check);
            }
        }
        
        pnAdditionals = new JPanel();
        pnAdditionals.setBorder(BorderFactory.createEmptyBorder(5, 2, 5, 15));
        spAddtionals.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        ArrayList<Additional> adds = app.getControl().getAdditionalList("", "i.name");
        int COLS = 4;
        pnAdditionals.setLayout(new GridLayout(0, COLS, 5, 5));
        spAddtionals.setViewportView(pnAdditionals);
        for (int i = 0; i < adds.size(); i++) {
            Additional add = adds.get(i);
            PanelAddition panAdd = new PanelAddition(app, add);
//            panAdd.setActionCommand(add.getCode());
            pnAdditionals.add(panAdd);
        }
        
        lbTitle3.setText("Elige la presentación");
        lbTitle3.setBackground(Utiles.colorAleatorio(100, 220));
        ArrayList<Presentation> presList = app.getControl().getPresentationsByProduct(product.getId());
        int rows = presList.isEmpty() ? 1 : (3 % presList.size());
        pnPresentations.setLayout(new GridLayout(rows, 4, 5, 5));
        bgPres = new ButtonGroup();
        if (!presList.isEmpty()) {
            for (int i = 0; i < presList.size(); i++) {
                Presentation pres = presList.get(i);
                if (pres.isEnabled()) {
                    PanelPresentation panPres = new PanelPresentation(app, pres);
                    panPres.setSelected(pres.isDefault());
                    panPres.setActionCommand(AC_SEL_PRES);
                    panPres.addActionListener(this);
                    bgPres.add(panPres.getSelector());
                    pnPresentations.add(panPres);
                }
            }
            
        } else {
            lbTitle3.setVisible(false);
            pnPresentations.setVisible(false);
            
        }
        
        if (product.isVariablePrice()) {
            spPrice.setVisible(true);
            lbPrice.setVisible(false);
        } else {
            lbPrice.setVisible(true);
            spPrice.setVisible(false);
        }
        
        pnCoccion = new JPanel();
        pnCoccion.setLayout(new FlowLayout(FlowLayout.LEADING, 10, 10));
        
        ButtonGroup btgTerminos = new ButtonGroup();
        String[] terminos = {"1/2", "3/4", "Bien asada"};
        for (String termino : terminos) {
            JRadioButton rbTermino = new JRadioButton(termino);
            rbTermino.setPreferredSize(new Dimension(120, 20));
            rbTermino.setFont(new Font("sans", 0, 16));
            rbTermino.setBorderPainted(true);
            rbTermino.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEtchedBorder(),
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));
            btgTerminos.add(rbTermino);
            
            pnCoccion.add(rbTermino);
        }
        
        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Adicionales", spAddtionals);
        tabs.add("Ingredientes", pnIngredients);
        tabs.add("Coccion", pnCoccion);
        
        pnContainer.setLayout(new BorderLayout());
        pnContainer.add(tabs);
        pnContainer.updateUI();
        
        mostrarTotal();
        
    }
    public static final String AC_SEL_PRES = "AC_SEL_PRES";
    
    public static final String AC_CONFIRMAR_PEDIDO = "AC_CONFIRMAR_PEDIDO";
    
    public void mostrarTotal() {
        int cant = (int) spCantidad.getValue();
        double price = 0;
        
        if (product.isVariablePrice()) {
            price = spPriceModel.getNumber().doubleValue();
        } else {
            price = product.getPrice();
        }
        
        ArrayList<AdditionalPed> additionals = getArrayAdditionals();
        
        boolean adds = !additionals.isEmpty();
        double valAdds = getValueAdicionales(additionals);
        
        double total = cant * (price + valAdds);
        String stValAdds = adds ? " + " + String.valueOf(valAdds) : "";
        lbInfo.setText(cant + " " + product.getName() + " (" + product.getPrice() + stValAdds + ") x " + DCFORM_P.format(total));
    }
    
    public void showProductPed(ProductoPed productPed, int row) {
        modeEdit = true;
        this.rowSelected = row;
        Product product = productPed.getProduct();
        System.out.println("cantidad:"+productPed.getCantidad());
        spPriceModel.setMinimum(product.getPrice());
        spPriceModel.setValue(product.getPrice());
        lbPrice.setText(NF.format(product.getPrice()));
        spModel.setValue(productPed.getCantidad());
        chEntry.setSelected(productPed.isEntry());
        selectPresentation(productPed.getPresentation());
        selectAdditionals(productPed.getAdicionales());
        selectIngredients(productPed.getExclusiones());
        selectTermino(productPed.getTermino());
        taObs.setText(productPed.getEspecificaciones());
        
    }
    
    private void selectPresentation(Presentation presentation) {
        if (presentation != null) {
            for (Component component : pnPresentations.getComponents()) {
                PanelPresentation pnPres = (PanelPresentation) component;
                Presentation pres = pnPres.getPresentation();
                if (pres.equals(presentation)) {
                    pnPres.setSelected(true);
                    return;
                }
            }
        }
    }
    
    private void selectAdditionals(ArrayList<AdditionalPed> adicionales) {
        List<Additional> collect = adicionales.stream().map(adicional -> adicional.getAdditional()).collect(Collectors.toList());
        for (Component component : pnAdditionals.getComponents()) {
            if (component instanceof PanelAddition) {
                PanelAddition pnAdd = (PanelAddition) component;
                Additional add = pnAdd.getAddition();
                if (collect.contains(add)) {
                    int indexOf = collect.indexOf(add);
                    pnAdd.setQuantity(adicionales.get(indexOf).getCantidad());
                    pnAdd.activate(true);
                }
            }
        }
        
    }
    
    private void selectIngredients(ArrayList<Ingredient> exclusiones) {
        List<String> collect = exclusiones.stream().map(ingred -> ingred.getName()).collect(Collectors.toList());
        for (Component component : pnIngredients.getComponents()) {
            if (component instanceof JCheckBox) {
                String text = ((JCheckBox) component).getText();
                try {
                    String name = text.substring(4);
                    if (collect.contains(name)) {
                        ((JCheckBox) component).setSelected(true);
                    }
                } catch (Exception e) {
                }
                
            }
        }
    }
    
    private void selectTermino(String termino) {
        if (termino != null) {
            for (Component component : pnCoccion.getComponents()) {
                JRadioButton rbTerm = (JRadioButton) component;
                String term = rbTerm.getText();
                if (term.equals(termino)) {
                    rbTerm.setSelected(true);
                    return;
                }
            }
        }
    }
    
    public ProductoPed parseProduct() {
        double price = 0;
        
        ProductoPed prodPed = new ProductoPed(product);
        
        Component[] componentes = pnIngredients.getComponents();
        for (int i = 0; i < componentes.length; i++) {
            Component componente = componentes[i];
            if (componente instanceof JCheckBox) {
                String text = ((JCheckBox) componente).getText();
                String code = ((JCheckBox) componente).getActionCommand();
                boolean sel = ((JCheckBox) componente).isSelected();
                if (sel) {
                    prodPed.addExclusion(app.getControl().getIngredient(code));
                }
            }
        }
        
        prodPed.setPresentation(getPresentation());
        
        prodPed.setAdicionales(getArrayAdditionals());
        
        prodPed.setTermino(getTermino());
        
        prodPed.setEntry(chEntry.isSelected());
        
        prodPed.setEspecificaciones(taObs.getText());
        return prodPed;
    }
    
    public ArrayList<AdditionalPed> getArrayAdditionals() {
        ArrayList<AdditionalPed> adicionales = new ArrayList<>();
        Component[] componentes;
        componentes = pnAdditionals.getComponents();
        for (Component componente : componentes) {
            if (componente instanceof PanelAddition) {
                Additional add = ((PanelAddition) componente).getAddition();
                int cant = ((PanelAddition) componente).getQuantity();
                boolean sel = ((PanelAddition) componente).isSelected();
                if (sel) {
                    adicionales.add(new AdditionalPed(add, cant));
                }
            }
        }
        return adicionales;
    }
    
    public Presentation getPresentation() {
        Component[] componentes;
        componentes = pnPresentations.getComponents();
        for (Component componente : componentes) {
            if (componente instanceof PanelPresentation) {
                PanelPresentation pPres = (PanelPresentation) componente;
                if (pPres.isSelected()) {
                    return pPres.getPresentation();
                }
            }
        }
        return null;
    }
    
    public String getTermino() {
        Component[] componentes;
        componentes = pnCoccion.getComponents();
        for (Component componente : componentes) {
            if (componente instanceof JRadioButton) {
                JRadioButton rbTerm = (JRadioButton) componente;
                if (rbTerm.isSelected()) {
                    return rbTerm.getText();
                }
            }
        }
        return "";
    }
    
    public double getValueAdicionales(ArrayList<AdditionalPed> adicionales) {
        double value = 0;
        for (int i = 0; i < adicionales.size(); i++) {
            Additional adic = adicionales.get(i).getAdditional();
            int cant = adicionales.get(i).getCantidad();
            value += adic.getPrecio() * cant;
        }
        return value;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (AC_CONFIRMAR_PEDIDO.equals(e.getActionCommand())) {
            ProductoPed prodPed = parseProduct();
            if (prodPed != null) {
                int cant = spModel.getNumber().intValue();
                prodPed.setCantidad(cant);
                double value = 0;
                if (product.isVariablePrice()) {
                    value = spPriceModel.getNumber().doubleValue();
                } else if (prodPed.getPresentation() != null) {
                    value = prodPed.getPresentation().getPrice();
                } else {
                    value = product.getPrice();
                }
                
                prodPed.setPrecio(value);
                if (modeEdit) {
                    pcs.firePropertyChange(AC_CUSTOM_MOD, new Object[]{cant, value, rowSelected}, prodPed);
                } else {
                    pcs.firePropertyChange(AC_CUSTOM_ADD, new Object[]{cant, value}, prodPed);
                }
                
                getRootPane().getParent().setVisible(false);
            }
        } else if (AC_SEL_PRES.equals(e.getActionCommand())) {
            Presentation presentation = getPresentation();
            if (presentation != null) {
                lbPrice.setText(NF.format(presentation.getPrice()));
            }
        }
    }
    public static final String AC_CUSTOM_ADD = "AC_CUSTOM_ADD";
    public static final String AC_CUSTOM_MOD = "AC_CUSTOM_MOD";
    
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnIngredients = new javax.swing.JPanel();
        spAddtionals = new javax.swing.JScrollPane();
        lbImage = new javax.swing.JLabel();
        lbName = new javax.swing.JLabel();
        lbDescription = new javax.swing.JLabel();
        lbPrice = new javax.swing.JLabel();
        spCantidad = new javax.swing.JSpinner();
        lbCant = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        taObs = new javax.swing.JTextArea();
        lbInfo = new javax.swing.JLabel();
        btConfirm = new javax.swing.JButton();
        lbTitle3 = new javax.swing.JLabel();
        pnPresentations = new javax.swing.JPanel();
        spPrice = new javax.swing.JSpinner();
        pnContainer = new javax.swing.JPanel();
        chEntry = new javax.swing.JCheckBox();

        pnIngredients.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout pnIngredientsLayout = new javax.swing.GroupLayout(pnIngredients);
        pnIngredients.setLayout(pnIngredientsLayout);
        pnIngredientsLayout.setHorizontalGroup(
            pnIngredientsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 575, Short.MAX_VALUE)
        );
        pnIngredientsLayout.setVerticalGroup(
            pnIngredientsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 150, Short.MAX_VALUE)
        );

        lbName.setFont(new java.awt.Font("Ubuntu", 1, 17)); // NOI18N
        lbName.setText("jLabel1");
        lbName.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lbDescription.setText("jLabel1");
        lbDescription.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lbPrice.setFont(new java.awt.Font("Ubuntu", 1, 17)); // NOI18N
        lbPrice.setText("jLabel1");
        lbPrice.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        spCantidad.setFont(new java.awt.Font("Ubuntu", 0, 36)); // NOI18N

        lbCant.setFont(new java.awt.Font("Ubuntu", 0, 12)); // NOI18N
        lbCant.setText("jLabel1");
        lbCant.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        taObs.setColumns(20);
        taObs.setRows(5);
        jScrollPane1.setViewportView(taObs);

        lbInfo.setText("jLabel1");
        lbInfo.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btConfirm.setFont(new java.awt.Font("Ubuntu", 0, 17)); // NOI18N
        btConfirm.setText("jButton1");

        lbTitle3.setText("jLabel1");
        lbTitle3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        lbTitle3.setOpaque(true);

        pnPresentations.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout pnPresentationsLayout = new javax.swing.GroupLayout(pnPresentations);
        pnPresentations.setLayout(pnPresentationsLayout);
        pnPresentationsLayout.setHorizontalGroup(
            pnPresentationsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        pnPresentationsLayout.setVerticalGroup(
            pnPresentationsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 95, Short.MAX_VALUE)
        );

        spPrice.setModel(new javax.swing.SpinnerNumberModel(0.0d, null, null, 1.0d));

        javax.swing.GroupLayout pnContainerLayout = new javax.swing.GroupLayout(pnContainer);
        pnContainer.setLayout(pnContainerLayout);
        pnContainerLayout.setHorizontalGroup(
            pnContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 579, Short.MAX_VALUE)
        );
        pnContainerLayout.setVerticalGroup(
            pnContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 327, Short.MAX_VALUE)
        );

        chEntry.setText("jCheckBox1");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lbImage, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lbDescription, javax.swing.GroupLayout.PREFERRED_SIZE, 329, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(spCantidad, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE)
                            .addComponent(lbCant, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(spPrice)
                                .addGap(3, 3, 3)
                                .addComponent(lbPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))))
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lbInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chEntry)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lbTitle3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnPresentations, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnContainer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lbImage, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(lbPrice, javax.swing.GroupLayout.DEFAULT_SIZE, 29, Short.MAX_VALUE)
                            .addComponent(spPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbName, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbDescription, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lbCant)
                                .addGap(0, 0, 0)
                                .addComponent(spCantidad)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbTitle3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnPresentations, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnContainer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chEntry, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {lbName, lbPrice, spPrice});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btConfirm, lbInfo});

    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btConfirm;
    private javax.swing.JCheckBox chEntry;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbCant;
    private javax.swing.JLabel lbDescription;
    private javax.swing.JLabel lbImage;
    private javax.swing.JLabel lbInfo;
    private javax.swing.JLabel lbName;
    private javax.swing.JLabel lbPrice;
    private javax.swing.JLabel lbTitle3;
    private javax.swing.JPanel pnContainer;
    private javax.swing.JPanel pnIngredients;
    private javax.swing.JPanel pnPresentations;
    private javax.swing.JScrollPane spAddtionals;
    private javax.swing.JSpinner spCantidad;
    private javax.swing.JSpinner spPrice;
    private javax.swing.JTextArea taObs;
    // End of variables declaration//GEN-END:variables

}
