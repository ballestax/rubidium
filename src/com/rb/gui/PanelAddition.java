package com.rb.gui;

import com.rb.Aplication;
import com.rb.domain.Additional;
import com.rb.domain.AdditionalPed;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.SpinnerNumberModel;
import org.apache.commons.lang3.StringUtils;
import org.dz.PanelCaptura;

/**
 *
 * @author lrod
 */
public class PanelAddition extends PanelCaptura implements ActionListener {

    private final Aplication app;
    private final Additional addition;
    private int lastValue;

    /**
     * Creates new form PanelAddition
     *
     * @param app
     * @param addition
     */
    public PanelAddition(Aplication app, Additional addition) {
        this.app = app;
        this.addition = addition;
        lastValue = 1;
        initComponents();
        createComponents();
    }

    private void createComponents() {

        MouseAdapter mouseClick = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                chSel.setSelected(!chSel.isSelected());
                activate(chSel.isSelected());
            }
        };

        setBorder(BorderFactory.createLineBorder(Color.lightGray, 1, true));
        lbPrice.setForeground(Color.blue.darker());
        lbPrice.addMouseListener(mouseClick);

        lbName.setPreferredSize(new Dimension(160, 20));
        lbName.setMinimumSize(new Dimension(160, 20));
        lbName.addMouseListener(mouseClick);

        lbName.setText(StringUtils.capitalize(addition.getName()));
        lbName.setToolTipText(StringUtils.capitalize(addition.getName()));
        lbPrice.setToolTipText(StringUtils.capitalize(addition.getName()));
        lbPrice.setText(app.getCurrencyFormat().format(addition.getPrecio()));

        SpinnerNumberModel spModel = new SpinnerNumberModel(1, 1, 100, 1);
        spCant.setModel(spModel);

        chSel.setActionCommand(AC_SEL_ADDITION);
        chSel.addActionListener(this);

        activate(false);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        chSel = new javax.swing.JCheckBox();
        lbName = new javax.swing.JLabel();
        lbPrice = new javax.swing.JLabel();
        spCant = new javax.swing.JSpinner();

        setToolTipText("");

        lbName.setFont(new java.awt.Font("Ubuntu", 0, 14)); // NOI18N
        lbName.setText("jLabel1");

        lbPrice.setFont(new java.awt.Font("Ubuntu", 1, 13)); // NOI18N
        lbPrice.setText("jLabel2");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(chSel)
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbPrice, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lbName, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spCant, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(spCant)
                            .addComponent(chSel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lbName)
                        .addGap(0, 0, 0)
                        .addComponent(lbPrice)))
                .addGap(2, 2, 2))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox chSel;
    private javax.swing.JLabel lbName;
    private javax.swing.JLabel lbPrice;
    private javax.swing.JSpinner spCant;
    // End of variables declaration//GEN-END:variables

    @Override
    public void reset() {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (AC_SEL_ADDITION.equals(e.getActionCommand())) {
            activate(chSel.isSelected());
        }
    }

    public void activate(boolean act) {
        lbName.setEnabled(act);
        lbPrice.setEnabled(act);
        spCant.setEnabled(act);
        lastValue = Integer.parseInt(spCant.getValue().toString());
        chSel.setSelected(act);
        if (act) {
            spCant.setValue(lastValue);
        } else {
            spCant.setValue(1);
        }

    }

    public Additional getAddition() {
        return addition;
    }

    public AdditionalPed getAdditionPed() {
        return new AdditionalPed(addition, getQuantity());
    }

    public int getQuantity() {
        return Integer.parseInt(spCant.getValue().toString());
    }

    public void setQuantity(int quantity) {
        spCant.setValue(quantity);
    }

    public boolean isSelected() {
        return chSel.isSelected();
    }

    public static final String AC_SEL_ADDITION = "AC_SEL_ADDITION";

}
