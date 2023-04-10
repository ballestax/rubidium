package com.bacon.gui;

import com.bacon.Aplication;
import com.bacon.domain.Table;
import com.bacon.domain.Waiter;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.dz.PanelCapturaMod;

/**
 *
 * @author lrod
 */
public class PanelTakeOrders extends PanelCapturaMod implements ActionListener {

    private final Aplication app;
    private PanelOrders pnOrders;
    private PanelSelProducts pnSelProducts;
    private JPanel pnTables;
    private HashMap<String, TableRender> mapTables;

    /**
     * Creates new form PanelTakeOrders
     *
     * @param app
     */
    public PanelTakeOrders(Aplication app) {
        this.app = app;
        mapTables = new HashMap<>();
        initComponents();
        createComponents();
    }

    private void createComponents() {

        pnContainer.setLayout(new BorderLayout());
        pnContainer.setBorder(bordeError);

        pnOrders = app.getGuiManager().getPanelOrders();
        pnOrders.addPropertyChangeListener(this);

        pnTables = new JPanel();
        GridLayout layout = new GridLayout(4, 4, 15, 15);
        pnTables.setLayout(layout);

        ArrayList<Table> tableslList = app.getControl().getTableslList("", "");
        for (Table table : tableslList) {

            TableRender render = new TableRender();

            render.setup(table);
            render.setBorder(BorderFactory.createEtchedBorder());
            render.setActionCommand(AC_SEL_TABLE_ + table.getName());
            render.addPropertyChangeListener(this);
            mapTables.put(table.getName(), render);

            pnTables.add(render);
        }

//        remove(jSplitPane1);
        pnContainer.add(pnTables);
//        updateUI();

//        jSplitPane1.setLeftComponent(pnTables);
//        jSplitPane1.setRightComponent(new JPanel());
//        jSplitPane1.setDividerLocation(0.5);
    }
    public static final String AC_SEL_TABLE_ = "AC_SEL_TABLE_";

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().contains(AC_SEL_TABLE_)) {
            Table table = (Table) evt.getOldValue();
            if (table.getIdOrder() > 0) {
                Waiter waiter = app.getControl().getWaitressByID(table.getIdWaiter());
                showTakeOrder(waiter, table);
                return;
            }

            ArrayList<Waiter> waitresslList = app.getControl().getWaitresslList("status=1", "name");
            Object selected = JOptionPane.showInputDialog(null, "Seleccione mesero:", "Mesero",
                    JOptionPane.QUESTION_MESSAGE, null,
                    waitresslList.toArray(), null);

            if (selected != null) {
                Waiter mesero = (Waiter) selected;
                TableRender render = (TableRender) evt.getNewValue();
                String html = "<html><font color=" + mesero.getColor() + ">" + mesero.getName().toUpperCase() + "</html>";
//                render.setPeople(Utiles.aleatorio(1, 4));
                
                render.setWaiter(html);
                showTakeOrder(mesero, render.getTable());
            }
        } else if (evt.getPropertyName().equals(AC_CLEAR_TABLE)) {
            Table table = (Table) evt.getNewValue();
            TableRender render = mapTables.get(table.getName());
            if (render != null) {
                render.setPeople(0);
                render.setWaiter(null);
            }
        } else if (evt.getPropertyName().equals(AC_UPDATE_TABLE)) {
            Table table = (Table) evt.getNewValue();
            Long idOrder = (Long) evt.getOldValue();
            table.setIdOrder(idOrder);
            TableRender render = mapTables.get(table.getName());
            if (render != null) {
                if (table.getStatus() == Table.TABLE_ST_PEDIDO_EN_COCINA) {
                    Image imagen = app.getImgManager().getImagen(app.getFolderIcons() + "upload.png", 20, 20);
                    render.setOrder("#"+idOrder);
                    render.setStatus(table.getStatus());
                    render.setIcon(new ImageIcon(imagen));
                }
            }
        }
    }
    public static final String AC_CLEAR_TABLE = "AC_CLEAR_TABLE";
    public static final String AC_UPDATE_TABLE = "AC_UPDATE_TABLE";

    @Override
    public void actionPerformed(ActionEvent e) {
//        if (e.getActionCommand().contains(AC_SEL_TABLE_)) {
//            String table = e.getActionCommand().substring(AC_SEL_TABLE_.length());
//            ArrayList<Waiter> waitresslList = app.getControl().getWaitresslList("status=1", "name");
//            Object selected = JOptionPane.showInputDialog(null, "Seleccione mesero:", "Mesero",
//                    JOptionPane.QUESTION_MESSAGE, null,
//                    waitresslList.toArray(), null);
//
//            if (selected != null) {
//                Waiter mesero = (Waiter) selected;                
//                JButton btn = ((JButton) e.getSource());
//                btn.setText("<html>Mesa:<br><font color=blue size=+1>" + table + "<br><font color=red>" + mesero.getName() + "</html>");
//            }
//
//        }
    }

    private void showTakeOrder(Waiter waiter, Table table) {
        
        pnOrders.setupData(waiter, table);
        table.setIdWaiter(waiter.getId());

        pnSelProducts = app.getGuiManager().getPaneSelProducts();

        jSplitPane1.setRightComponent(pnOrders);
        jSplitPane1.setLeftComponent(pnSelProducts);

        pnContainer.removeAll();
        pnContainer.add(jSplitPane1);
        pnContainer.updateUI();
    }

    public void showTables() {
        pnContainer.removeAll();
        pnContainer.add(pnTables);
        pnContainer.updateUI();
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
        pnContainer = new javax.swing.JPanel();

        javax.swing.GroupLayout pnContainerLayout = new javax.swing.GroupLayout(pnContainer);
        pnContainer.setLayout(pnContainerLayout);
        pnContainerLayout.setHorizontalGroup(
            pnContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        pnContainerLayout.setVerticalGroup(
            pnContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 276, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnContainer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnContainer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JPanel pnContainer;
    // End of variables declaration//GEN-END:variables
}
