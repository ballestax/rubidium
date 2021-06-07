package com.bacon.gui.util;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.Document;
import org.bx.UpperCaseTextField;

public class Registro extends JComponent implements Reseteable, CaretListener {

    private Box box;
    private int status;
    public static final int NORMAL = 1;
    public static final int EDITING = 2;
    public static final int ERROR = 3;
    public boolean popup = false;
    private JPopupMenu menu;

    public Registro() {
        super();
        box = new Box(axis);
        bordered = true;
        docLim = null;
        fontCampoDefault = new Font("Tahoma", 1, 14);
        fontLabelDefault = new Font("Arial", 0, 11);
        this.axis = BoxLayout.Y_AXIS;
        this.stLabel = "Label";
        this.stCampo = "";
        inicializar(null);
    }

    public Registro(int axis, String stLabel, String stCampo) {
        super();
        box = new Box(axis);
        bordered = true;
        docLim = null;
        fontCampoDefault = new Font("Tahoma", 1, 14);
        fontLabelDefault = new Font("Arial", 0, 11);
        this.axis = axis;
        this.stLabel = stLabel;
        this.stCampo = stCampo;
        inicializar(null);
    }

    public Registro(int axis, String stLabel, String stCampo, int widthLabel) {
        super();
        box = new Box(axis);
        bordered = true;
        docLim = null;
        fontCampoDefault = new Font("Tahoma", 1, 14);
        fontLabelDefault = new Font("Arial", 0, 11);
        this.axis = axis;
        this.stLabel = stLabel;
        this.stCampo = stCampo;
        this.widthLabel = widthLabel;
        inicializar(null);
    }

    public Registro(int axis, String stLabel, String stCampo, int width, int widthLabel) {
        super();
        box = new Box(axis);
        bordered = true;
        docLim = null;
        fontCampoDefault = new Font("Tahoma", 1, 14);
        fontLabelDefault = new Font("Arial", 0, 11);
        this.axis = axis;
        this.stLabel = stLabel;
        this.stCampo = stCampo;
        this.width = width;
        this.widthLabel = widthLabel;
        inicializar(null);
    }

    public Registro(int axis, String stLabel, String stCampo, Font fontLabel) {
        super();
        box = new Box(axis);
        bordered = true;
        docLim = null;
        fontCampoDefault = new Font("Tahoma", 1, 14);
        fontLabelDefault = new Font("Arial", 0, 11);
        this.axis = axis;
        this.axis = axis;
        this.stLabel = stLabel;
        this.stCampo = stCampo;
        this.fontLabel = fontLabel;
        fontCampo = fontCampoDefault;
        inicializar(null);
    }

    public Registro(int axis, String stLabel, String stCampo, Font fontLabel, Document docLim) {
        super();
        box = new Box(axis);
        bordered = true;
        this.docLim = null;
        fontCampoDefault = new Font("Tahoma", 1, 14);
        fontLabelDefault = new Font("Arial", 0, 11);
        this.axis = axis;
        this.axis = axis;
        this.stLabel = stLabel;
        this.stCampo = stCampo;
        this.fontLabel = fontLabel;
        fontCampo = fontCampoDefault;
        this.docLim = docLim;
        inicializar(null);
    }

    public Registro(int axis, String stLabel, String stCampo, Font fontLabel, AbstractAction action, Document docLim) {
        super();
        box = new Box(axis);
        bordered = true;
        this.docLim = null;
        fontCampoDefault = new Font("Tahoma", 1, 14);
        fontLabelDefault = new Font("Arial", 0, 11);
        this.axis = axis;
        this.axis = axis;
        this.stLabel = stLabel;
        this.stCampo = stCampo;
        this.action = action;
        this.fontLabel = fontLabel;
        fontCampo = fontCampoDefault;
        this.docLim = docLim;
        inicializar(null);
    }

    public Registro(int axis, String stLabel, String stCampo, int width, Font fontLabel) {
        super();
        box = new Box(axis);
        bordered = true;
        docLim = null;
        fontCampoDefault = new Font("Tahoma", 1, 14);
        fontLabelDefault = new Font("Arial", 0, 11);
        this.axis = axis;
        this.axis = axis;
        this.stLabel = stLabel;
        this.stCampo = stCampo;
        this.width = width;
        this.fontLabel = fontLabel;
        fontCampo = fontCampoDefault;
        inicializar(null);
    }

    public Registro(int axis, String stLabel, String stCampo, int width, Font fontLabel, Document docLim) {
        super();
        box = new Box(axis);
        bordered = true;
        this.docLim = null;
        fontCampoDefault = new Font("Tahoma", 1, 14);
        fontLabelDefault = new Font("Arial", 0, 11);
        this.axis = axis;
        this.stLabel = stLabel;
        this.stCampo = stCampo;
        this.width = width;
        bordered = true;
        this.fontLabel = fontLabel;
        fontCampo = fontCampoDefault;
        this.docLim = docLim;
        inicializar(null);
    }

    public Registro(int axis, String stLabel, String stCampo, int width, AbstractAction action, Font fontLabel) {
        super();
        box = new Box(axis);
        bordered = true;
        docLim = null;
        fontCampoDefault = new Font("Tahoma", 1, 14);
        fontLabelDefault = new Font("Arial", 0, 11);
        this.axis = axis;
        this.stLabel = stLabel;
        this.stCampo = stCampo;
        this.width = width;
        this.action = action;
        this.fontLabel = fontLabel;
        fontCampo = fontCampoDefault;
        inicializar(null);
    }

    public Registro(int axis, String stLabel, String stCampo, int width, AbstractAction action) {
        super();
        box = new Box(axis);
        bordered = true;
        docLim = null;
        fontCampoDefault = new Font("Tahoma", 1, 14);
        fontLabelDefault = new Font("Arial", 0, 11);
        this.axis = axis;
        this.stLabel = stLabel;
        this.stCampo = stCampo;
        this.width = width;
        this.action = action;
        fontLabel = fontLabelDefault;
        fontCampo = fontCampoDefault;
        inicializar(null);
    }

    public Registro(int axis, String stLabel, String stCampo, int width, int widthLabel, AbstractAction action) {
        super();
        box = new Box(axis);
        bordered = true;
        docLim = null;
        fontCampoDefault = new Font("Tahoma", 1, 14);
        fontLabelDefault = new Font("Arial", 0, 11);
        this.axis = axis;
        this.stLabel = stLabel;
        this.stCampo = stCampo;
        this.width = width;
        this.action = action;
        this.widthLabel = widthLabel;
        fontLabel = fontLabelDefault;
        fontCampo = fontCampoDefault;
        inicializar(null);
    }

    public Registro(int axis, String stLabel, JComponent campo) {
        super();
        box = new Box(axis);
        bordered = true;
        docLim = null;
        fontCampoDefault = new Font("Tahoma", 1, 14);
        fontLabelDefault = new Font("Arial", 0, 11);
        this.axis = axis;
        this.stLabel = stLabel;
        stCampo = "";
        fontLabel = fontLabelDefault;
        fontCampo = fontCampoDefault;
        inicializar(campo);
    }

    public Registro(int axis, String stLabel, JComponent campo, int widthLabel) {
        super();
        box = new Box(axis);
        bordered = true;
        docLim = null;
        fontCampoDefault = new Font("Tahoma", 1, 14);
        fontLabelDefault = new Font("Arial", 0, 11);
        this.axis = axis;
        this.stLabel = stLabel;
        this.widthLabel = widthLabel;
        stCampo = "";
        fontLabel = fontLabelDefault;
        fontCampo = fontCampoDefault;
        inicializar(campo);
    }

    public Registro(int axis, String stLabel, JComponent campo, boolean bordered) {
        super();
        box = new Box(axis);
        this.bordered = true;
        docLim = null;
        fontCampoDefault = new Font("Tahoma", 1, 14);
        fontLabelDefault = new Font("Arial", 0, 11);
        this.axis = axis;
        this.stLabel = stLabel;
        stCampo = "";
        this.bordered = bordered;
        fontLabel = fontLabelDefault;
        fontCampo = fontCampoDefault;
        inicializar(campo);
    }

    public Registro(int axis, String stLabel, JComponent campo, int width, int widthLabel) {
        super();
        box = new Box(axis);
        bordered = true;
        docLim = null;
        fontCampoDefault = new Font("Tahoma", 1, 14);
        fontLabelDefault = new Font("Arial", 0, 11);
        this.axis = axis;
        this.stLabel = stLabel;
        stCampo = "";
        bordered = true;
        this.width = width;
        this.widthLabel = widthLabel;
        fontLabel = fontLabelDefault;
        fontCampo = fontCampoDefault;
        inicializar(campo);
    }

    public Registro(int axis, String stLabel, JComponent campo, int width, boolean bordered, Font fontLabel, Font fontCampo) {
        super();
        box = new Box(axis);
        this.bordered = true;
        docLim = null;
        fontCampoDefault = new Font("Tahoma", 1, 14);
        fontLabelDefault = new Font("Arial", 0, 11);
        this.axis = axis;
        this.stLabel = stLabel;
        stCampo = "";
        this.bordered = bordered;
        this.width = width;
        this.fontCampo = fontCampo;
        this.fontLabel = fontLabel;
        inicializar(campo);
    }

    public Registro(int axis, String stLabel, JComponent campo, int width, Font fontLabel, Font fontCampo) {
        super();
        box = new Box(axis);
        bordered = true;
        docLim = null;
        fontCampoDefault = new Font("Tahoma", 1, 14);
        fontLabelDefault = new Font("Arial", 0, 11);
        this.axis = axis;
        this.stLabel = stLabel;
        stCampo = "";
        bordered = false;
        this.width = width;
        this.fontCampo = fontCampo;
        this.fontLabel = fontLabel;
        inicializar(campo);
    }

    public Registro(int axis, String stLabel, JComponent campo, int width, Font fontLabel) {
        super();
        box = new Box(axis);
        bordered = true;
        docLim = null;
        fontCampoDefault = new Font("Tahoma", 1, 14);
        fontLabelDefault = new Font("Arial", 0, 11);
        this.axis = axis;
        this.stLabel = stLabel;
        stCampo = "";
        bordered = bordered;
        this.width = width;
        fontCampo = fontCampoDefault;
        this.fontLabel = fontLabel;
        inicializar(campo);
    }

    public Registro(int axis, String stLabel, JComboBox campo) {
        super();
        box = new Box(axis);
        bordered = true;
        docLim = null;
        fontCampoDefault = new Font("Tahoma", 1, 14);
        fontLabelDefault = new Font("Arial", 0, 11);
        this.axis = axis;
        this.stLabel = stLabel;
        stCampo = "";
        fontCampo = fontCampoDefault;
        inicializar(campo);
    }

    /*
    * Constructor JcomboBox
     */
    public Registro(int axis, String stLabel, String[] opcs, int width, boolean bordered, Font fontLabel, Font fontCampo) {
        super();
        box = new Box(axis);
        this.bordered = true;
        docLim = null;
        fontCampoDefault = new Font("Tahoma", 1, 14);
        fontLabelDefault = new Font("Arial", 0, 11);
        this.axis = axis;
        this.stLabel = stLabel;
        stCampo = "";
        this.bordered = bordered;
        this.width = width;
        this.fontCampo = fontCampo;
        this.fontLabel = fontLabel;
        JComboBox combo = new JComboBox(opcs);
        inicializar(combo);
    }

    public Registro(int axis, String stLabel, String[] opcs) {
        super();
        box = new Box(axis);
        this.bordered = true;
        docLim = null;
        fontCampoDefault = new Font("Tahoma", 1, 14);
        fontLabelDefault = new Font("Arial", 0, 11);
        this.axis = axis;
        this.stLabel = stLabel;
        stCampo = "";
        JComboBox combo = new JComboBox(opcs);
        inicializar(combo);
    }

    public Registro(int axis, String stLabel, Object[] opcs) {
        super();
        box = new Box(axis);
        this.bordered = true;
        docLim = null;
        fontCampoDefault = new Font("Tahoma", 1, 14);
        fontLabelDefault = new Font("Arial", 0, 11);
        this.axis = axis;
        this.stLabel = stLabel;
        stCampo = "";
        JComboBox combo = new JComboBox(opcs);
        inicializar(combo);
    }

    public Registro(int axis, String stLabel, String[] opcs, int widthlabel) {
        super();
        box = new Box(axis);
        this.bordered = true;
        docLim = null;
        fontCampoDefault = new Font("Tahoma", 1, 14);
        fontLabelDefault = new Font("Arial", 0, 11);
        this.axis = axis;
        this.widthLabel = widthlabel;
        this.stLabel = stLabel;
        stCampo = "";
        JComboBox combo = new JComboBox<>(opcs);
        inicializar(combo);
    }

    public synchronized void addFocusListener(FocusListener l) {
        super.addFocusListener(l);
        if (campo != null) {
            campo.addFocusListener(l);
        }
    }

    public void setComponent(JComponent component) {
        inicializar(component);
        this.updateUI();
    }

    public void setAction(AbstractAction action) {
        this.action = action;
        inicializar(campo);
    }

    private void inicializar(JComponent componente) {
        removeAll();
        box.removeAll();
        setLayout(new BorderLayout());
        UIManager.put("ComboBox.disabledBackground", Color.white);
        UIManager.put("ComboBox.disabledForeground", new Color(100, 100, 100));
        bordeError = BorderFactory.createLineBorder(Color.red, 1, true);
        bordeNormal = BorderFactory.createLineBorder(Color.darkGray, 1, true);
        bordeEditing = BorderFactory.createLineBorder(Color.blue, 1, true);
        status = NORMAL;
        if (bordered) {
            setBorder(BorderFactory.createLineBorder(Color.darkGray, 1, true));
        }
        box.setAlignmentX(Component.LEFT_ALIGNMENT);
        if (componente == null) {
            campo = new JTextField(stCampo);
            campo.setBorder(null);
            if (docLim != null) {
                ((JTextField) campo).setDocument(docLim);
            }
            ((JTextField) campo).addCaretListener(this);
        } else {
            campo = componente;
            if (campo instanceof JTextField) {
                ((JTextField) campo).addCaretListener(this);
            } else if (campo instanceof MyDatePickerImp) {
                ((MyDatePickerImp) campo).addCaretListener(this);
            } else if (campo instanceof JTextArea) {
                ((JTextArea) campo).addCaretListener(this);
            }
        }
        campo.setFont(fontCampo);
        label = new JLabel((new StringBuilder()).append(" ").append(stLabel).append(" ").toString());
        if (axis == 0) {
            campo.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.black));
        } else {
            campo.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.black));
        }
        campo.setPreferredSize(new Dimension(width, 20));
        campo.setMinimumSize(new Dimension(width, 20));
        if (widthLabel > 0) {
            label.setPreferredSize(new Dimension(widthLabel, 20));
            label.setMinimumSize(new Dimension(widthLabel, 20));
        }

        FocusListener focusListeners[] = campo.getFocusListeners();
        FocusListener arr$[] = focusListeners;
        int len$ = arr$.length;
        for (int i$ = 0; i$ < len$; i$++) {
            FocusListener focusListener = arr$[i$];
            campo.addFocusListener(focusListener);
        }
        label.setFont(fontLabel);
//        label.setHorizontalAlignment(SwingConstants.LEFT);
//        label.setHorizontalTextPosition(SwingConstants.LEFT);

        box.add(label);
        boolean conIcono = false;

        int heigh = getHeight();
        heigh = heigh > 0 ? heigh : 32;

        Box boxH = null;
        if (action != null) {
            JButton icono = new JButton(action);
            icono.setPreferredSize(new Dimension(heigh, heigh));
            boxH = new Box(0);
            boxH.add(campo);
            boxH.add(icono);
            conIcono = true;
        }
        box.add(((Component) (conIcono ? ((Component) (boxH)) : ((Component) (campo)))));
        add(box, BorderLayout.CENTER);

        if (popup) {

        }
    }

    private void makePopup() {
        if (menu == null) {
            menu = new JPopupMenu();
            Action paste = new AbstractAction("Pegar") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (campo instanceof JTextField) {
                        if (docLim != null) {
                            try {
                                Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();

                                String st = (String) c.getData(DataFlavor.stringFlavor);

                                StringSelection testData;

                                String tmp = st.replaceAll("\\s+", "");

                                //  Add some test data
                                testData = new StringSelection(tmp);
                                c.setContents(testData, testData);

                                ((JTextField) campo).paste();

                                testData = new StringSelection(st);
                                c.setContents(testData, testData);

                            } catch (Exception ex) {
                                ((JTextField) campo).paste();
                            }
                        } else {
                            ((JTextField) campo).paste();
                        }

                    }
                }
            };
//            cut.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control X"));
            menu.add(paste);

            Action copy = new AbstractAction("Copiar") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (campo instanceof JTextField) {
                        ((JTextField) campo).copy();
                    }
                }
            };
//            cut.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control X"));
            menu.add(copy);

            Action cut = new AbstractAction("Cortar") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (campo instanceof JTextField) {
                        ((JTextField) campo).cut();
                    }
                }
            };
//            cut.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control X"));
            menu.add(cut);

            if (campo instanceof JTextField) {
                campo.setComponentPopupMenu(menu);
            }
        }
    }

    public void setPopup(boolean popup) {
        this.popup = popup;
        makePopup();
    }

    @Override
    public void reset() {
        if (campo instanceof JTextField) {
            ((JTextField) campo).setText("");
        } else if (campo instanceof MyDatePickerImp) {
            ((MyDatePickerImp) campo).setText("");
        }
    }

    public Component getComponent() {
        return campo;
    }

    public Object getObject() {

        if (campo instanceof JTextField) {
            return ((JTextField) campo).getText();
        }

        if (campo instanceof JTextArea) {
            return ((JTextArea) campo).getText();
        }
        if (campo instanceof JComboBox) {
            JComboBox cb = ((JComboBox) campo);
            if (cb.getSelectedItem() != null) {
                return ((JComboBox) campo).getSelectedItem();
            } else {
                return "";
            }
        }
        if (campo instanceof MyDatePickerImp) {
            return ((MyDatePickerImp) campo).getText();
        } else {
            return "";
        }
    }

    public String getText() {
        if (campo instanceof JTextField) {
            return ((JTextField) campo).getText();
        } else if (campo instanceof JComboBox) {
            JComboBox cb = ((JComboBox) campo);
            if (cb.getSelectedItem() != null) {
                return ((JComboBox) campo).getSelectedItem().toString();
            } else {
                return "";
            }
        } else if (campo instanceof MyDatePickerImp) {
            return ((MyDatePickerImp) campo).getText();
        } else if (campo instanceof JTextArea) {
            return ((JTextArea) campo).getText();
        } else {
            return "";
        }
    }

    public void setText(String text) {
        if (campo instanceof JTextField) {
            ((JTextField) campo).setText(text);
        } else if (campo instanceof JComboBox) {
            ((JComboBox) campo).setSelectedItem(text);
        } else if (campo instanceof MyDatePickerImp) {
            ((MyDatePickerImp) campo).setText(text);
        } else if (campo instanceof JTextArea) {
            ((JTextArea) campo).setText(text);
        } else if (campo instanceof JScrollPane) {
            try {
                ((JTextArea) (((JScrollPane) campo).getViewport().getComponent(0))).setText(text);
            } catch (Exception e) {
            }
        }
    }

    public void setItem(Object item) {
        if (campo instanceof JComboBox) {
            ((JComboBox) campo).setSelectedItem(item);
        }
    }

    public void setText(String[] text) {
        if (campo instanceof JComboBox) {
            ((JComboBox) campo).setModel(new DefaultComboBoxModel<>(text));
        }
    }

    public void setText(Object[] object) {
        if (campo instanceof JComboBox) {
            ((JComboBox) campo).setModel(new DefaultComboBoxModel<>(object));
        }
    }

    public int getSelected() {
        if (campo instanceof JComboBox) {
            return ((JComboBox) campo).getSelectedIndex();
        } else {
            return 0;
        }
    }

    public Object getSelectedItem() {
        if (campo instanceof JComboBox) {
            return ((JComboBox) campo).getSelectedItem();
        } else {
            return null;
        }
    }

    public void setSelected(int sel) {
        if (campo instanceof JComboBox) {
            ((JComboBox) campo).setSelectedIndex(sel);
        }
    }

    public void setSelected(Object object) {
        if (campo instanceof JComboBox) {
            ((JComboBox) campo).setSelectedItem(object);
        }
    }

    public void setLabelFont(Font fontLabel) {
        this.fontLabel = fontLabel;
        label.setFont(fontLabel);
    }

    public void setLabelFontSize(int size) {
        if (fontLabel != null) {
            this.fontLabel = fontLabel.deriveFont(size);
            label.setFont(fontLabel);
        }
    }

    public void setLabelText(String labelText) {
        this.stLabel = labelText;
        label.setText(this.stLabel);
    }

    public void setCampoFont(Font fontCampo) {
        this.fontCampo = fontCampo;
        campo.setFont(fontCampo);
    }

    public Document getDocument() {
        if (campo instanceof JTextField) {
            return ((JTextField) campo).getDocument();
        }
        return null;
    }

    public void addCaretListener(CaretListener caretListener) {
        if (campo instanceof JTextField) {
            ((JTextField) campo).addCaretListener(caretListener);
        }
        if (campo instanceof JTextArea) {
            ((JTextArea) campo).addCaretListener(caretListener);
        }
    }

    @Override
    public void caretUpdate(CaretEvent e) {
        if (getBorder().equals(bordeError) || status == ERROR) {
            setBorderToNormal();
        }
    }

    public Border getBordeNormal() {
        return bordeNormal;
    }

    public Border getBordeError() {
        return bordeError;
    }

    public void setBordeError(Border bordeError) {
        this.bordeError = bordeError;
    }

    public void setBordeNormal(Border bordeNormal) {
        this.bordeNormal = bordeNormal;
    }

    public void setBorderToError() {
        status = ERROR;
        setBorder(bordeError);
    }

    public void setBorderToNormal() {
        if (status != NORMAL) {
            status = NORMAL;
            setBorder(bordeNormal);
        }
    }

    public void setBorderToEditing() {
        status = ERROR;
        setBorder(bordeEditing);
    }

    public void setBorder() {
        setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
    }

    public void setBorderColor(Color color) {
        setBorder(BorderFactory.createLineBorder(color, 1, true));

    }

    public void setTint(Color color) {
        setBorder(BorderFactory.createLineBorder(color, 1, true));
        if (axis == 0) {
            campo.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, color));
        } else {
            campo.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, color));
        }
        label.setBackground(color.brighter());
        label.setForeground(color.darker());
    }

    public void setTint(Color color, int thicks) {
        setBorder(BorderFactory.createLineBorder(color, thicks, true));
        if (axis == 0) {
            campo.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, color));
        } else {
            campo.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, color));
        }
        label.setBackground(color.brighter());
        label.setForeground(color.darker());
    }

    @Override
    public void setForeground(Color fg) {
        super.setForeground(fg);
        if (campo instanceof JTextField) {
            ((JTextField) campo).setForeground(fg);
        } else if (campo instanceof JComboBox) {
            ((JComboBox) campo).setForeground(fg);
        } else if (campo instanceof MyDatePickerImp) {
            ((MyDatePickerImp) campo).setFieldForeground(fg);
        }
    }

    public void setTextAligment(int align) {
        if (campo instanceof JTextField) {
            ((JTextField) campo).setHorizontalAlignment(align);
        }
    }

    @Override
    public void setBackground(Color fg) {
        box.setBackground(fg);
        box.setOpaque(true);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        label.setEnabled(enabled);
        campo.setEnabled(enabled);
    }

    public void setEditable(boolean editable) {
        if (editable) {
            setBorder(BorderFactory.createLineBorder(Color.black, 1, true));
        } else {
            setBorder(BorderFactory.createLineBorder(new Color(125, 142, 174), 1, true));
        }
        if (campo instanceof JTextField) {
            Color background = ((JTextField) campo).getBackground();
            ((JTextField) campo).setEditable(editable);
            ((JTextField) campo).setBackground(background);
            if (editable) {
                ((JTextField) campo).setForeground(Color.black);
            } else {
                ((JTextField) campo).setForeground(new Color(100, 100, 100));
            }
        } else if (campo instanceof JComboBox) {
            Color foreground = ((JComboBox) campo).getForeground();
            ((JComboBox) campo).setEnabled(editable);
            ((JComboBox) campo).setForeground(foreground);
        } else if (campo instanceof MyDatePickerImp) {
            ((MyDatePickerImp) campo).setEditable(editable);
        } else if (campo instanceof JTextArea) {
            Color background = ((JTextArea) campo).getBackground();
            ((JTextArea) campo).setEditable(editable);
            ((JTextArea) campo).setBackground(background);
            if (editable) {
                ((JTextArea) campo).setForeground(Color.black);
            } else {
                ((JTextArea) campo).setForeground(new Color(100, 100, 100));
            }
        }
    }

    public void setActionCommand(String actionCommand) {
        if (campo != null) {
            if (campo instanceof JComboBox) {
                ((JComboBox) campo).setActionCommand(actionCommand);
            } else if (campo instanceof JTextField) {
                ((JTextField) campo).setActionCommand(actionCommand);
            }
        }
    }

    public void addActionListener(ActionListener listener) {
        if (campo != null) {
            if (campo instanceof JComboBox) {
                ((JComboBox) campo).addActionListener(listener);
            } else if (campo instanceof JTextField) {
                ((JTextField) campo).addActionListener(listener);
            }
        }
    }

    public void setMaxRowCount(int rows) {
        if (campo != null) {
            if (campo instanceof JComboBox) {
                ((JComboBox) campo).setMaximumRowCount(rows);
            }
        }
    }

    public void updateList(Object[] list) {
        if (campo != null) {
            if (campo instanceof JComboBox) {
                ((JComboBox) campo).setModel(new DefaultComboBoxModel<>(list));
            }
        }
    }

    public void setFontCampo(Font f) {
        if (campo != null) {
            if (campo instanceof MyDatePickerImp) {
                ((MyDatePickerImp) campo).setFontField(f);
            } else {
                campo.setFont(f);
            }
        }
    }

    public void setLabelAlign(int align) {
        label.setHorizontalAlignment(align);
    }

    public void setLabelBackground(Color color) {
        label.setOpaque(true);
        label.setBackground(color);
        box.setBackground(color);
    }

    public void setDocument(Document docLim) {
        this.docLim = docLim;
        ((JTextField) campo).setDocument(this.docLim);
    }

    public void setPadding(int top, int left, int botton, int right) {
        if (campo instanceof JTextField || campo instanceof UpperCaseTextField) {
            ((JTextField) campo).setMargin(new Insets(top, left, botton, right));
        } else if (campo instanceof JTextArea) {
            ((JTextArea) campo).setMargin(new Insets(top, left, botton, right));
        }
    }

    protected Border bordeError;
    protected Border bordeNormal;
    protected Border bordeEditing;
    private JLabel label;
    private JComponent campo;
    private String stCampo;
    private String stLabel;
    private boolean bordered;
    private int width;
    private int widthLabel;
    private AbstractAction action;
    private Font fontLabel;
    private Font fontCampo;
    private Document docLim;
    private final Font fontCampoDefault;
    private final Font fontLabelDefault;
    private int axis;
}
