/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.  
 */
package com.rb.gui.util;


import com.rb.Aplication;
import com.rb.Field;
import com.rb.Filter;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import org.dz.Resources;


/**
 *
 * @author ballestax
 */
public class BoxRowFilter extends Box implements ActionListener {

    private JComboBox cbField;
    private JComboBox cbCondition;
    private JTextField tfValue;
    private JComboBox cbOperator;
    private ArrayList<Field> fields;
    private DefaultComboBoxModel<Field> cbModelFields;
    private DefaultComboBoxModel cbModelCondition;
    private JCheckBox check;
    private boolean enableOperator;
    private Box bBot;
    private Registro regOperator;
    private Registro regValue;
    private Registro regCondition;
    private Registro regField;
    private PropertyChangeSupport pcs;
    private JButton btDelete;
    private JButton btAdd;
    private Aplication app;
    private Filter filter;
    private boolean first;

    public BoxRowFilter(Aplication app, int axis, ArrayList<Field> fields) {
        this(app, axis, fields, true);
    }

    public BoxRowFilter(Aplication app, int axis, ArrayList<Field> fields, boolean enabled) {
        super(axis);
        this.app = app;
        pcs = new PropertyChangeSupport(this);
        this.fields = new ArrayList<>(fields);
        this.fields.add(0, null);
        createComponent();
        setEnabled(enabled);
        enableOperator = true;
    }

    private void createComponent() {
        cbModelFields = new DefaultComboBoxModel(fields.toArray());
        cbField = new JComboBox(cbModelFields);
        cbField.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Field fieldSelected = (Field) cbField.getSelectedItem();
                if (fieldSelected == null) {
                    cbCondition.setEnabled(false);
                    tfValue.setEnabled(false);
//                    cbOperator.setEnabled(false);
                } else {
                    cbCondition.setEnabled(true);
                    tfValue.setEnabled(true);
//                    cbOperator.setEnabled(true);
                    if (fieldSelected.getType() == Field.T_TEXT) {
                        cbModelCondition = new DefaultComboBoxModel(Filter.FILTROS_TEXTO);
                    } else if (fieldSelected.getType() == Field.T_NUMERIC) {
                        cbModelCondition = new DefaultComboBoxModel(Filter.FILTROS_NUMERO);
                    } else if (fieldSelected.getType() == Field.T_BOOLEAN) {
                        cbModelCondition = new DefaultComboBoxModel(Filter.FILTROS_BOOLEANOS);
                    } else if (fieldSelected.getType() == Field.T_DATE) {
                        cbModelCondition = new DefaultComboBoxModel();
                    }
                }
                cbCondition.setModel(cbModelCondition);
            }
        });
        regField = new Registro(BoxLayout.Y_AXIS, "Campo", cbField, 150);

        cbModelCondition = new DefaultComboBoxModel();
        cbCondition = new JComboBox(cbModelCondition);
        regCondition = new Registro(BoxLayout.Y_AXIS, "Condicion", cbCondition, 150);

        tfValue = new JTextField();
        regValue = new Registro(BoxLayout.Y_AXIS, "Valor", tfValue, 100);

        cbOperator = new JComboBox(new DefaultComboBoxModel(Filter.OPERADORES));
        cbOperator.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                String item = cbOperator.getSelectedItem().toString();
//                int ind = cbOperator.getSelectedIndex();
//                setEnabled(check.isSelected());
            }
        });
        regOperator = new Registro(BoxLayout.Y_AXIS, "Operador", cbOperator, 50);

        check = new JCheckBox();
        check.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                setEnabled(check.isSelected());
            }
        });
//        Registro regCheck = new Registro(BoxLayout.Y_AXIS, "Hab", check, 10);

        btDelete = new JButton("");
        btDelete.setPreferredSize(new Dimension(16, 16));
        btDelete.setIcon(new ImageIcon(Resources.getImagen(app.getFolderIcons()+"delete-icon.png", Aplication.class, 12, 12)));
        btDelete.setActionCommand("REMOVE_ROWFILTER");
        btDelete.addActionListener(this);
        btAdd = new JButton("");
        btAdd.setPreferredSize(new Dimension(16, 16));
        btAdd.setIcon(new ImageIcon(Resources.getImagen(app.getFolderIcons()+"add-icon.png", Aplication.class, 12, 12)));
        btAdd.setActionCommand("ADD_ROWFILTER");
        btAdd.addActionListener(this);
        bBot = new Box(BoxLayout.Y_AXIS);
        bBot.add(btDelete);
        bBot.add(btAdd);
        makeStructure();
    }

    public void updateFields(ArrayList<Field> fields) {
        this.fields = fields;
        cbModelFields.removeAllElements();
        Field selected = (Field) cbModelFields.getSelectedItem();
        for (Field field : fields) {
            cbModelFields.addElement(field);
        }
        if (selected != null) {
            cbModelFields.setSelectedItem(selected);
        }
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
        cbField.setSelectedItem(filter.getField());
        int type = filter.getField().getType();
        if (type == Field.T_TEXT) {
            cbModelCondition = new DefaultComboBoxModel(Filter.FILTROS_TEXTO);
        } else if (type == Field.T_NUMERIC) {
            cbModelCondition = new DefaultComboBoxModel(Filter.FILTROS_NUMERO);
        } else if (type == Field.T_BOOLEAN) {
            cbModelCondition = new DefaultComboBoxModel(Filter.FILTROS_BOOLEANOS);
        } else if (type == Field.T_DATE) {
            cbModelCondition = new DefaultComboBoxModel();
        }
        cbCondition.setModel(cbModelCondition);        
        cbCondition.setSelectedItem(filter.getConditionTitle());
        tfValue.setText(filter.getValue());
        cbOperator.setSelectedItem(filter.getOperator());
    }

    private void makeStructure() {
        add(check);
        add(Box.createHorizontalStrut(5));
        add(regOperator);
        add(Box.createHorizontalStrut(5));
        add(regField);
        add(Box.createHorizontalStrut(5));
        add(regCondition);
        add(Box.createHorizontalStrut(5));
        add(regValue);
        add(Box.createHorizontalStrut(5));
        add(bBot);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        cbField.setEnabled(enabled);
        cbCondition.setEnabled(enabled);
        tfValue.setEnabled(enabled);
//        cbOperator.setEnabled(enabled);
    }

    public boolean isEnableOperator() {
        return enableOperator;
    }

    public void setEnableOperator(boolean enableOperator) {
        if (this.enableOperator != enableOperator) {
            this.enableOperator = enableOperator;
            regOperator.setEnabled(enableOperator);
            btDelete.setEnabled(false);

        }
    }

    public void setFirst(boolean first) {
        this.first = first;
        if (first) {
            check.setSelected(true);
            check.setEnabled(false);
            cbOperator.setSelectedItem(null);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("ADD_ROWFILTER")) {
            btAdd.setEnabled(false);
            pcs.firePropertyChange("ADD_ROWFILTER", null, 1);
        } else if (e.getActionCommand().equals("REMOVE_ROWFILTER")) {

            pcs.firePropertyChange("REMOVE_ROWFILTER", null, this);
        }
    }

    public void addPropertyChangelistener(PropertyChangeListener pcl) {
        pcs.addPropertyChangeListener(pcl);
    }

    public void removePropertyChangelistener(PropertyChangeListener pcl) {
        pcs.removePropertyChangeListener(pcl);
    }

    public void enableAddButton(boolean enabled) {
        btAdd.setEnabled(enabled);
    }

    public Filter getFilter() {
        Filter filter = null;
        if (cbField.getSelectedItem() != null) {
            Field field = (Field) cbField.getSelectedItem();
            if (cbCondition.getSelectedItem() != null) {
                String condition = cbCondition.getSelectedItem().toString();                
                filter = new Filter(field, tfValue.getText(), app.getCtrlFilters().getIndexFilter(condition));
                filter.setConditionTitle(condition);
                if (cbOperator.getSelectedItem()!=null) {
                    filter.setOperator(cbOperator.getSelectedItem().toString());
                } else {
                    filter.setOperator("");
                }
                this.filter = filter;
            }
        }
        return filter;
    }
}
