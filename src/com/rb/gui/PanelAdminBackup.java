/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rb.gui;


import com.rb.Aplication;
import com.rb.Configuration;
import com.rb.GUIManager;
import com.rb.Utiles;
import com.rb.domain.Backup;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.dz.MyListModel;
import org.dz.PanelCaptura;


/**
 *
 * @author ballestax
 */
public class PanelAdminBackup extends PanelCaptura implements ActionListener, CaretListener, ListSelectionListener, PropertyChangeListener {

    private final Aplication app;
    public static final String AC_BACKUP_MANUAL = "AC_BACKUP_MANUAL";
    public static final String AC_SHOW_FILECHOOSER = "AC_SHOW_FILECHOOSER";
    public static final String AC_AUTO_SELECTED = "AC_AUTO_SELECTED";
    public static final String AC_AUTO_NO_SELECTED = "AC_AUTO_NO_SELECTED";
    public static final String AC_DELETE = "AC_DELETE";
    public static final String AC_RESTORE = "AC_RESTORE";

    private String backupDIR;
    private SimpleDateFormat format, formatOut;
    private String DIR;
    private MyListModel model;
    private Backup selBackup;

    /**
     * Creates new form PanelAdminBackup
     *
     * @param app
     */
    public PanelAdminBackup(Aplication app) {
        this.app = app;
        initComponents();
        createComponents();
    }

    private void createComponents() {

        format = new SimpleDateFormat("yyyyMMddHHmm");
        formatOut = new SimpleDateFormat("dd/MMMM/yyyy, HH:mm");

        btBackupManual.setActionCommand(AC_BACKUP_MANUAL);
        btBackupManual.addActionListener(this);
        btBrowse.setActionCommand(AC_SHOW_FILECHOOSER);
        btBrowse.addActionListener(this);
        tfDestino.setEditable(false);
        backupDIR = app.getConfiguration().getProperty(Configuration.BACKUP_LAST_DIR, Aplication.getDirTrabajo());
        tfDestino.setText(backupDIR);
        tfName.addCaretListener(this);
        btBrowse.setIcon(new ImageIcon(app.getImgManager().getImagen(app.getFolderIcons()+"folder-open.png", 18, 18)));

        chNameAuto.setActionCommand(AC_AUTO_SELECTED);
        chNameAuto.addActionListener(this);

        btBackupManual.setEnabled(true);

        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addListSelectionListener(this);
        list.setCellRenderer(new MyListCellRender());
        model = new MyListModel();
        String property = app.getConfiguration().getProperty(Configuration.BACKUP_LIST, "");        
        if (!property.isEmpty()) {
            String[] split = property.split(";;");
            for (int i = 0; i < split.length; i++) {
                String split1 = split[i];
                addFileToList(split1);
            }
        }
//        model.addLista(new ArrayList<>(Arrays.asList(split)), true);
        list.setModel(model);

        btEliminar.setActionCommand(AC_DELETE);
        btEliminar.addActionListener(this);
        btEliminar.setEnabled(false);
        btRestaurar.setActionCommand(AC_RESTORE);
        btRestaurar.addActionListener(this);
        btRestaurar.setEnabled(false);

        lbOperation.setVisible(false);
        progress.setVisible(false);

        showMessage();
    }

    private void doBackup() {
        final String _DIR = Paths.get(backupDIR, "bck_" + format.format(new Date())).toString();
        String mess = "<html><p>Se realizara una copia de seguridad en:</p>"
                + "<p color=green>" + _DIR + "</p></html>";
        int confirm = JOptionPane.showConfirmDialog(null, mess, "Conmfirmar copia de seguridad", JOptionPane.PLAIN_MESSAGE);
        if (confirm == JOptionPane.OK_OPTION) {
            app.getGuiManager().setWaitCursor();
            SwingWorker sw = new SwingWorker<Object, Object>() {

                @Override
                protected Object doInBackground() throws Exception {
                    lbOperation.setVisible(true);
                    lbOperation.setText("Realizando copia de seguridad");
                    app.getCtrlBackup().doBackup(_DIR);
                    progress.setIndeterminate(true);
                    return true;
                }

                @Override
                protected void done() {
                    progress.setIndeterminate(false);
                    progress.setVisible(false);
                    app.getConfiguration().setProperty(Configuration.BACKUP_LAST_DIR, backupDIR, true);
                    addFileToList(_DIR);
                    String prop = app.getConfiguration().getProperty(Configuration.BACKUP_LIST, "");
                    app.getConfiguration().setProperty(Configuration.BACKUP_LIST, prop + ";;" + _DIR, true);
                }

            };
            sw.execute();
            lbOperation.setVisible(false);
            lbOperation.setText("");
            app.getGuiManager().setDefaultCursor();
        }
    }

    private void addFileToList(String file) {
        //System.err.println(file);
        try {
            File f = new File(file);
            if (f.isFile()) {
                BasicFileAttributes attrib = Files.readAttributes(Paths.get(file, ""), BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
                FileTime creation = attrib.creationTime();
                long size = attrib.size();               
                Backup back = new Backup();
                back.setName(f.getName());
                back.setCreationDate(new Date(creation.toMillis()));
                back.setPath(file);
                back.setSize(size);
                model.addElemento(back);
            }
        } catch (IOException e) {
            System.err.println("IOException:"+e.getMessage());
        }
    }

    private void showFileChooser() {
        JFileChooser fc = new JFileChooser();
        fc.setMultiSelectionEnabled(false);
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fc.getSelectedFile();
            tfDestino.setText(selectedFile.getPath());
            backupDIR = selectedFile.getPath();
            showMessage();
        }
    }

    private void showMessage() {
        boolean ok = false;
        if (verifyBackupDIR(backupDIR)) {
            lbMessage.setText("<html><p color=blue>" + backupDIR + "</p></html>");
            if (!tfName.getText().isEmpty()) {
                Path PATH;
                try {
                    PATH = Paths.get(backupDIR, tfName.getText());
                    lbMessage.setText("<html><p color=green>" + PATH + "</p></html>");
                    ok = true;
                } catch (InvalidPathException e) {
                    lbMessage.setText("<html><p color=red>No ha especificado un nombre valido:" + backupDIR + "</p></html>");
                }
            }
        } else {
            lbMessage.setText("<html><p color=red>No se encuentra el directorio:" + backupDIR + "</p></html>");
        }

        if (ok) {
            DIR = Paths.get(backupDIR, tfName.getText()).toString();
        }
        btBackupManual.setEnabled(ok);

    }

    private boolean verifyBackupDIR(String dir) {
        Path get = Paths.get(dir, "");
        return Files.exists(get, LinkOption.NOFOLLOW_LINKS);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        tfDestino = new javax.swing.JTextField();
        btBrowse = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        tfName = new javax.swing.JTextField();
        chNameAuto = new javax.swing.JCheckBox();
        btBackupManual = new javax.swing.JButton();
        lbMessage = new javax.swing.JLabel();
        progress = new javax.swing.JProgressBar();
        lbOperation = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        list = new javax.swing.JList();
        btRestaurar = new javax.swing.JButton();
        btEliminar = new javax.swing.JButton();

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setBackground(java.awt.Color.lightGray);
        jLabel1.setText("Backup Manual");
        jLabel1.setOpaque(true);

        jLabel2.setText("Elija donde guardar la copia de seguridad:");

        jLabel3.setText("Escriba el nombre para esta copia de seguridad:");

        chNameAuto.setFont(new java.awt.Font("Ubuntu", 0, 12)); // NOI18N
        chNameAuto.setText("Auto");

        btBackupManual.setText("Backup");

        lbOperation.setFont(new java.awt.Font("Ubuntu", 0, 18)); // NOI18N
        lbOperation.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btBackupManual, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbMessage, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(btBrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(tfName)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(tfDestino))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(chNameAuto)))))
                .addGap(148, 148, 148))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(progress, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 438, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbOperation, javax.swing.GroupLayout.PREFERRED_SIZE, 408, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel2, jLabel3});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tfDestino, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btBrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfName, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chNameAuto))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbMessage, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btBackupManual)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(progress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbOperation, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btBrowse, tfDestino});

        jScrollPane1.setViewportView(list);

        btRestaurar.setText("Restaurar");

        btEliminar.setText("Eliminar");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 445, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btEliminar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btRestaurar))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 436, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btRestaurar)
                            .addComponent(btEliminar))
                        .addGap(0, 14, Short.MAX_VALUE)))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btBackupManual;
    private javax.swing.JButton btBrowse;
    private javax.swing.JButton btEliminar;
    private javax.swing.JButton btRestaurar;
    private javax.swing.JCheckBox chNameAuto;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbMessage;
    private javax.swing.JLabel lbOperation;
    private javax.swing.JList list;
    private javax.swing.JProgressBar progress;
    private javax.swing.JTextField tfDestino;
    private javax.swing.JTextField tfName;
    // End of variables declaration//GEN-END:variables

    @Override
    public void actionPerformed(ActionEvent e) {

        if (AC_BACKUP_MANUAL.equals(e.getActionCommand())) {
            doBackup();
        } else if (AC_SHOW_FILECHOOSER.equals(e.getActionCommand())) {
            showFileChooser();
        } else if (AC_AUTO_SELECTED.equals(e.getActionCommand())) {
            boolean selected = chNameAuto.isSelected();
            if (selected) {
                tfName.setEditable(false);
                String name = "bck_" + app.getFormatoFecha().format(new Date()) + "-hhmm";
                tfName.setText(name);
            } else {
                tfName.setEditable(true);
                tfName.setText("");
            }
        } else if (AC_RESTORE.equals(e.getActionCommand())) {
            String html = "<html>Desea restaurar los datos de la copia de seguridad"
                    + "<p><p>"
                    + "</html>";
            int confirm = JOptionPane.showConfirmDialog(null, html, "Comfirmar restauracion", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                if (selBackup != null) {
                    System.out.println(selBackup.getPath());
                    try {
                        ArrayList<String> sql = Utiles.splitArchivoEnLineas(selBackup.getPath(), true);
//                        Arrays.toString(sql.toArray());
                        app.getCtrlBackup().restoreBackup(sql, this);
                    } catch (IOException ex) {
                        System.err.println("Restoring backup:"+ex.getMessage());
                    }
                }
            }
        } else if (AC_DELETE.equals(e.getActionCommand())) {
            String html = "<html>Desea eliminar la copia de seguridad"
                    + "<p><p>"
                    + "</html>";
            int confirm = JOptionPane.showConfirmDialog(null, html, "Comfirmar eliminacion", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                if (selBackup != null) {
                    Path get = null;
                    try {
                        get = Paths.get(selBackup.getPath(), "");
                        Files.delete(get);
                        model.removeElemento(selBackup);
                    } catch (NoSuchFileException x) {
                        GUIManager.showErrorMessage(null, "No se encuentra el archivo: " + get, "Error");
                        System.err.format("%s: no such" + " file or directory%n", get);
                    } catch (DirectoryNotEmptyException x) {
                        System.err.format("%s not empty%n", get);
                    } catch (IOException x) {
                        GUIManager.showErrorMessage(null, "No es posible eliminar el archivo: " + get, "Error");
                        System.err.println(x);
                    }
                }
            }
        }
    }

    @Override
    public void reset() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void caretUpdate(CaretEvent e) {
        if (e.getSource().equals(tfName)) {
            showMessage();
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        boolean enable = list.getSelectedIndex() != -1;
        selBackup = (Backup) list.getSelectedValue();
        btEliminar.setEnabled(enable);
        btRestaurar.setEnabled(enable);

    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case "RESTORE_INIT":
                progress.setVisible(true);
                progress.setIndeterminate(true);
                lbOperation.setVisible(true);
                lbOperation.setText("Restaurando");
                break;
            case "RESTORE_TRUNCATE":
                break;
            case "RESTORE_INSERT":
                break;
            case "RESTORE_ERROR":
                progress.setIndeterminate(false);
                progress.setVisible(false);
                break;
            case "RESTORE_FINALLY":
                progress.setIndeterminate(false);
                progress.setVisible(false);
                lbOperation.setVisible(false);
                break;
        }
    }

    public class MyListCellRender extends JLabel implements ListCellRenderer<Object> {

        public MyListCellRender() {
            setOpaque(true);
            setBorder(BorderFactory.createLineBorder(Color.blue.brighter()));
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Object> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            if (value != null) {
                Backup backup = (Backup) value;
                String text = "<html><table width=100%><tr>";
                text += "<td color=" + "blue" + ">" + backup.getName() + "</td>";
                text += "<td color=" + "blue" + ">" + formatOut.format(backup.getCreationDate()) + "</td>";
                text += "<td color=" + "blue" + ">" + (backup.getSize() / 1024) + " Kb</td>";
                text += "</table></tr></html>";
                setText(text);
            }
            if (isSelected) {
                setForeground(Color.black);
                setBackground(list.getSelectionBackground());
                if (cellHasFocus) {
                    setBorder(BorderFactory.createLineBorder(Color.darkGray));
                } else {
                    setBorder(BorderFactory.createLineBorder(Color.lightGray));
                }
            } else {
                setBackground(list.getBackground());
                setForeground(Color.black);
                setBorder(UIManager.getBorder("List.cellBorder"));
            }
            return this;
        }

    }

}
