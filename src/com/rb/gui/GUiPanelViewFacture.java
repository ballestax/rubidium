/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rb.gui;



import com.rb.Aplication;
import com.rb.AppBasic;
import com.rb.GUIManager;
import com.rb.PDFGenerator;
import com.rb.domain.Invoice;
import com.rb.domain.ProductoPed;
import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import org.dz.PanelCaptura;

/**
 *
 * @author ballestax
 */
public class GUiPanelViewFacture extends PanelCaptura {

    private Aplication app;
    private JEditorPane editorPane;
    private final DecimalFormat format;
    private JButton btnImprimir;
    private JButton btnCerrar;
    private Box box;
    private JButton btnRevisar;
    private JButton btnEliminar;
    private final SimpleDateFormat formFecha;
    private int factura;
    public static final int FACT_ENTRADA = 0;
    public static final int FACT_SALIDA = 1;
    private JScrollPane scroll;
    private Invoice invoice;

    public GUiPanelViewFacture(Aplication app) {
        this.app = app;
//        pcs = new PropertyChangeSupport(this);
//        format = new DecimalFormat("#.##");
        format = app.getDCFORM_W();
        formFecha = new SimpleDateFormat("dd MMMM yyyy");
        createComponents();
    }

    private void createComponents() {
        editorPane = new JEditorPane();
        box = new Box(BoxLayout.X_AXIS);
        btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                getRootPane().getParent().setVisible(false);
            }
        });

        btnImprimir = new JButton("Imprimir");
        btnImprimir.setActionCommand(ACTION_PRINT_FACTURE);
        btnImprimir.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
//                System.err.println(salida);
//                pcs.firePropertyChange(ACTION_PRINT_FACTURE, null, entrada);
//                imprimirFacturaSalida(salida);
                generarFacturaPDF(invoice);
            }
        });

        btnRevisar = new JButton("Facturar");
        btnRevisar.setActionCommand(ACTION_REVIEW_FACTURE);
        btnRevisar.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                boolean bImp = factura == FACT_SALIDA;
                if (bImp) {
                    app.getGuiManager().reviewFacture(invoice);
                } else {
//                    app.getGuiManager().reviewFacture(entrada);
                }
                getRootPane().getParent().setVisible(false);
            }
        });

        btnEliminar = new JButton("Eliminar");
        btnEliminar.setActionCommand(ACTION_ANULAR_FACTURE);
        btnEliminar.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                boolean bImp = factura == FACT_ENTRADA;
                StringBuilder msg = new StringBuilder();
                msg.append("<html><font color=orange>Esta seguro que desea eliminar la factura N°</font>");
                msg.append("<font color=blue>").append(invoice.getFactura());
                msg.append(" </font><font color=orange>del</font> ");
                msg.append("<font color=blue>").append(formFecha.format(invoice.getFecha()) + "</font>");
                msg.append("<p><font color=orange>Tambien se eliminaran los productos de esta factura</font></p></html>");
                int opt = JOptionPane.showConfirmDialog(null, msg, "Advertencia", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                if (opt == JOptionPane.OK_OPTION) {
//                    try {
////                        if (bImp) {
////                            JDBCEntradaDAO importDAO = (JDBCEntradaDAO) (JDBCDAOFactory.getInstance().getEntradaDAO());
////                            importDAO.deleteImportacion(salida.getNumDecImportacion());
////                            pcs.firePropertyChange("AC_UPDATE_BILLS_LIST", null, salida);
////                        } else {
////                            JDBCSalidaDAO exportDAO = (JDBCSalidaDAO) (JDBCDAOFactory.getInstance().getSalidaDAO());
////                            exportDAO.deleteSalida(entrada.getNumeroFormulario(), entrada.getProductosExportados());
////                            pcs.firePropertyChange("AC_UPDATE_BILLS_LIST", null, entrada);
////                        }
////                        getRootPane().getParent().setVisible(false);
//
//                    } catch (DAOException ex) {
//                        GUIManager.showErrorMessage(null, ex.getMessage() + "\n" + ex.getCause(), "Error");
//                    }
                }
            }
        });
        box.add(btnRevisar);
        box.add(Box.createHorizontalGlue());
        box.add(btnCerrar);
        box.add(Box.createHorizontalStrut(4));

//        box.add(btnImprimir);
//        box.add(Box.createHorizontalStrut(4));
//        box.add(Box.createHorizontalStrut(4));
////        box.add(btnEliminar);
//        box.add(Box.createHorizontalStrut(4));
        setLayout(new BorderLayout());
        scroll = new JScrollPane(editorPane);
        add(scroll, BorderLayout.CENTER);
        add(box, BorderLayout.SOUTH);

        editorPane.setEditable(false);
    }
    public static final String ACTION_ANULAR_FACTURE = "ACTION_ANULAR_FACTURE";
    public static final String ACTION_REVIEW_FACTURE = "ACTION_REVIEW_FACTURE";
    public static final String ACTION_PRINT_FACTURE = "ACTION_PRINT_FACTURE";

    public void generarFacturaPDF(Invoice salida) {
        Path p = null;
        try {
            p = Paths.get(app.getDirTrabajo(), "Factura_" + salida.getFactura() + ".pdf");
//            System.err.println(p.toString());
            PDFGenerator pdf = new PDFGenerator(app);
            pdf.createDocument(p.toString(), "Factura " + salida.getFactura(), createFacture(salida));
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        try {
            Desktop.getDesktop().open(p.toFile());
        } catch (Exception e) {
            GUIManager.showErrorMessage(null, "Error: " + e.getMessage(), "Error");
        }

//        PDFGenerator pdfGen = new PDFGenerator(salida, app);
//        SimpleDateFormat formFecha = new SimpleDateFormat("yyyyMMdd");
//        String file = app.getConfiguration().getProperty(Configuration.BILL_FORMAT);
//        String dest = app.getDirFacturas() + File.separator + "factura_"
//                + salida.getNumeroFormulario() + "_" + formFecha.format(salida.getFechaFactura()) + ".pdf";
//        try {
//            pdfGen.generarPDF(file, dest);
//            if (!Files.exists(Paths.get(dest, ""), LinkOption.NOFOLLOW_LINKS)) {
//                return;
//            }
//            Desktop.getDesktop().open(new File(dest));
//
//        } catch (IOException e) {
//            GUIManager.showErrorMessage(null, "Error de lectura.\n" + e.getMessage(), "IOError");
//        } catch (DocumentException e) {
//            GUIManager.showErrorMessage(null, "Error en el documeto.\n" + e.getMessage(), "DOCError");
//        }
    }

    private void imprimirFacturaSalida(Invoice salida) {
//        SalidaPrint impresion = new SalidaPrint(salida, app);
//
//        PrinterJob job = PrinterJob.getPrinterJob();
//        PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
//        aset.add(new MediaPrintableArea(10, 10, 200, 290, MediaPrintableArea.MM));
////        PageFormat pf = job.pageDialog(aset);
//        job.setPrintable(impresion);
//        boolean ok = job.printDialog(aset);
//        if (ok) {
//            try {
//                job.print(aset);
//            } catch (PrinterException ex) {
//                /* The job did not successfully complete */
//                GUIManager.showErrorMessage(this, "Error en la impresion:\n" + ex.getMessage(), "Error de impresion");
//            }
//        }
    }

    

    private String createFacture(Invoice salida) {
        StringBuilder str = new StringBuilder();
        str.append("<html><head>");
        str.append("<title>Factura de venta</title>");
        str.append("<style type=\"text/css\">body {");
        str.append("margin-left: 20px;");
        str.append("margin-top: 10px;");
        str.append("margin-right: 20px;");
        str.append("margin-bottom: 10px;}");
        str.append("</style>");
        str.append("</head>");
        str.append("<body bgcolor=\"#E4DBFF\">");
        str.append("<table border=\"0\">");
        str.append("<tr><td><table width=\"700\" height=\"80\">");
        str.append("<tr width=\"100%\">");
        str.append("<td width=\"20%\"><div align=\"right\">");
        str.append("<img src=\"").append(AppBasic.class.getResource("gui/img/logo.png")).append("\" width=\"100\" height=\"70\"></img>");
        str.append("</div></td>");
        str.append("<td width=\"50%\"><div align=\"left\"><p>LLANTAS AMERICA<br></br>Calle 16 11-67<br></br>Cel. 3013728161<br></br>Maicao - La Guajira</p></div></td>");
        str.append("<td width=\"30%\"><div align=\"right\">");

        str.append("<table width=\"100%\" cellspacing=\"0\" border=\"1\">");
        str.append("<tr><td class=\"label\" width=\"100%\" bgcolor=\"#A4C1FF\">");
        str.append("<h2>").append("Factura").append("</h2></td></tr>");
        str.append("<tr><td width=\"100%\" bgcolor=\"#FFFFFF\"><h1>").append(salida.getFactura()).append("</h1></td>");
        str.append("</tr></table>");
        str.append("</div></td></tr></table></td></tr>");
        str.append("<br></br>");
        str.append("<tr><td><table width=\"100%\" cellspacing=\"1\" border=\"1\">");
        str.append("<tr width=\"700\" bgcolor=\"#A4C1FF\"><td colspan =\"4\" class=\"label\">").append("CLIENTE").append("</td></tr>");
        str.append("<tr><td width=\"100\">").append("Identificacion:").append("</td>");
        str.append("<td width=\"250\" bgcolor=\"#FFFFFF\">").append(salida.getIdCliente()).append("</td>");
        str.append("<td width=\"100\">").append("Cliente:").append("</td>");        
        str.append("<tr></tr>");

        str.append("<tr><td width=\"100\">").append("Fecha:").append("</td>");
        str.append("<td width=\"250\" bgcolor=\"#FFFFFF\">").append(app.DF.format(salida.getFecha())).append("</td>");
        str.append("<td width=\"100\">").append("Tipo:").append("</td>");
        str.append("<td width=\"250\" bgcolor=\"#FFFFFF\">").append("PAGO").append("</td></tr>");
        str.append("</table></td></tr>");
//        str.append("<tr><td>Nota:").append("</td>");
//        str.append("<td colspan=4 bgcolor=\"#F6FFDB\">").append(salida.getNota()).append("</td>").append("</tr>");

        str.append("<br></br>");
        str.append("<tr><td><table width=\"700\" cellspacing=\"0\" border=\"1\">");
        str.append("<tr bgcolor=\"#A4C1FF\">");
        str.append("<td>").append("Producto").append("</td>");
        str.append("<td>").append("Codigo").append("</td>");
        str.append("<td>").append("Cantidad").append("</td>");
        str.append("<td>").append("Valor Unitario").append("</td>");
        str.append("<td>").append("Valor total").append("</td></tr>");
        List<ProductoPed> productos = salida.getProducts();
        for (ProductoPed product : productos) {
//            System.err.println(product);
            str.append("<tr><td bgcolor=\"#F6FFDB\">").append(product.getProduct().getName()).append("</td>");
            str.append("<td bgcolor=\"#FFFFFF\">").append(product.getProduct().getCode()).append("</td>");
            str.append("<td bgcolor=\"#FFFFFF\" align=\"right\">").append(app.DCFORM_P.format(1)).append("</td>");
            str.append("<td bgcolor=\"#FFFFFF\" align=\"right\">").append(app.DCFORM_P.format(2)).append("</td>");
            str.append("<td bgcolor=\"#FFFFFF\" align=\"right\">").append(app.DCFORM_P.format(100)).append("</td>");
            str.append("</tr>");
        }
        str.append("</table></td></tr>");
//        str.append("<br>");
        str.append("<tr><td><table width=\"300\" cellspacing=\"0\" border=\"1\" align=\"right\">");
        str.append("<tr><td bgcolor=\"#A4C1FF\">").append("Total factura:").append("</td>");
        str.append("<td bgcolor=\"#FFFFFF\" align=\"right\">").append(app.DCFORM_P.format(salida.getValor())).append("</td>");
        str.append("</tr></table></td></tr>");
        str.append("<br></br>");
        str.append("<tr><td><table width=\"700\" cellspacing=\"0\" border=\"1\">");
        str.append("<tr><td width=\"100\">").append("Nota:").append("</td>");
        str.append("<td bgcolor=\"#FFFFFF\" align=\"right\">").append(salida.getNota()).append("</td>");
        str.append("</tr></table></td></tr>");
        str.append("<br></br>");
        str.append("</table>");
        str.append("</body></html>");
        return str.toString();
    }

    

    public void showFacture(Invoice salida) {
        factura = FACT_SALIDA;
        editorPane.setContentType("text/html");
        editorPane.setText(createFacture(salida));
        this.invoice = salida;
        btnImprimir.setVisible(false);
//        box.add(btnImprimir, BorderLayout.SOUTH);
//        if (salida.getTipoRegistro() == Salida.FACTURA_MANUAL) {
            btnRevisar.setVisible(true);
//        } else {
//            btnRevisar.setVisible(false);
//        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                scroll.getVerticalScrollBar().setValue(0);
            }
        }
        );
    }

    @Override
    public void reset() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
