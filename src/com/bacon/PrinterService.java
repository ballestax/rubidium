package com.bacon;

import com.bacon.domain.Additional;
import com.bacon.domain.Client;
import com.bacon.domain.Ingredient;
import com.bacon.domain.Invoice;
import com.bacon.domain.Presentation;
import com.bacon.domain.ProductoPed;
import com.bacon.domain.Table;
import com.bacon.domain.Waiter;
import com.bacon.gui.PanelPedido;
import static com.bacon.gui.PanelPedido.TIPO_DOMICILIO;
import com.github.anastaciocintra.escpos.EscPos;
import com.github.anastaciocintra.escpos.EscPosConst;
import com.github.anastaciocintra.escpos.Style;
import com.github.anastaciocintra.escpos.image.BitImageWrapper;
import com.github.anastaciocintra.escpos.image.Bitonal;
import com.github.anastaciocintra.escpos.image.BitonalThreshold;
import com.github.anastaciocintra.escpos.image.EscPosImage;
import com.github.anastaciocintra.output.PrinterOutputStream;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Level;
import javax.print.PrintService;
import org.bx.Imagenes;

/**
 *
 * @author lrod
 */
public class PrinterService {

    private static Aplication app;

    private PrinterService() {

    }

    public static PrinterService getInstance(Aplication app) {
        PrinterService.app = app;
        return PrinterServiceHolder.INSTANCE;
    }

    private static class PrinterServiceHolder {

        private static final PrinterService INSTANCE = new PrinterService();
    }

    public void imprimirFactura(Invoice invoice, String printerName) {

        Waiter waiter = null;
        Table table = null;
        Client client = null;
        try {
            waiter = app.getControl().getWaitersByID(invoice.getIdWaitress());
            table = app.getControl().getTableByID(invoice.getTable());

            client = app.getControl().getClient(invoice.getIdCliente().toString());
        } catch (Exception e) {
        }

        PrintService printService = PrinterOutputStream.getPrintServiceByName(printerName);
        EscPos escpos;
        try {
            Bitonal algorithm = new BitonalThreshold(127);
            // creating the EscPosImage, need buffered image and algorithm.
//            URL githubURL = getURL("logo1.png");
//            System.out.println("githubURL = " + githubURL);
//            BufferedImage imagen = ImageIO.read(githubURL);
            Image imagen = app.getImgManager().getImagen("gui/img/" + "logo2.png", 150, 150);
            BufferedImage buffImagen = Imagenes.toBuffereredImage(imagen);
            EscPosImage escposImage = new EscPosImage(buffImagen, algorithm);

            // this wrapper uses esc/pos sequence: "ESC '*'"
            BitImageWrapper imageWrapper = new BitImageWrapper();

            Style font2 = new Style().setFontSize(Style.FontSize._1, Style.FontSize._1).setJustification(EscPosConst.Justification.Center);
            Style font3 = new Style().setFontSize(Style.FontSize._1, Style.FontSize._1);
            Style font4 = new Style().setFontSize(Style.FontSize._1, Style.FontSize._1).setJustification(EscPosConst.Justification.Right);
            Style font5 = new Style().setFontSize(Style.FontSize._1, Style.FontSize._2).setJustification(EscPosConst.Justification.Center);

            escpos = new EscPos(new PrinterOutputStream(printService));
            imageWrapper.setJustification(EscPosConst.Justification.Center);
            escpos.write(imageWrapper, escposImage);
            escpos.feed(1);
            escpos.writeLF(new Style().setFontSize(Style.FontSize._3, Style.FontSize._3).setJustification(EscPosConst.Justification.Center),
                    "Bacon 57 Burger");
            escpos.writeLF(font2, "NIT. 1129518949-8");
            escpos.writeLF(font2, "Calle 18 # 5-59");
            escpos.writeLF(font2, "321 5944870");
            escpos.feed(1);
            if (invoice.getTipoEntrega() == PanelPedido.TIPO_LOCAL) {

                escpos.writeLF(font3, "Mesa:   " + (table != null ? table.getName() : "- - -"));
                escpos.writeLF(font3, "Mesero: " + (waiter != null ? waiter.getName().toUpperCase() : "- - -"));
            } else {

                escpos.writeLF(font3, "Cliente:   " + (client != null ? client.getCellphone() : "- - -"));
                escpos.writeLF(font3, "Direccion: " + (client != null && !client.getAddresses().isEmpty() ? client.getAddresses().get(0) : "- - -"));
            }
            escpos.feed(1);
            escpos.writeLF(font3, String.format("Tiquete N°: %1s %25.25s", invoice.getFactura(), app.DF_FULL.format(invoice.getFecha())));
            escpos.feed(1);

            String column1Format = "%3.3s";  // fixed size 3 characters, left aligned
            String column2Format = "%-25.25s";  // fixed size 8 characters, left aligned
            String column3Format = "%7.7s";   // fixed size 6 characters, right aligned
            String column4Format = "%9.9s";   // fixed size 6 characters, right aligned
            String formatInfo = column1Format + " " + column2Format + " " + column3Format + " " + column4Format;

            escpos.writeLF(font2, "===============================================");
            List<ProductoPed> products = invoice.getProducts();
            for (int i = 0; i < products.size(); i++) {
                ProductoPed product = products.get(i);
                Presentation presentation = product.getPresentation();
                double priceFinal = product.getPrecio() + product.getValueAdicionales();
                escpos.writeLF(String.format(formatInfo, product.getCantidad(), (product.getProduct().getName()).toUpperCase(),
                        app.DCFORM_P.format(priceFinal), app.DCFORM_P.format(product.getCantidad() * priceFinal)));
                String stPres = "";
                if (presentation != null) {
                    stPres = " (" + presentation.getName() + ")";
                    escpos.writeLF("    " + stPres);
                }

                for (int j = 0; j < product.getAdicionales().size(); j++) {
                    Additional adic = product.getAdicionales().get(j).getAdditional();
                    int cant = product.getAdicionales().get(j).getCantidad();
                    StringBuilder stb = new StringBuilder();
                    stb.append("+").append(adic.getName()).append("(x").append(cant).append(")");
                    escpos.writeLF("    " + stb.toString());
                }
            }

            BigDecimal total = invoice.getValor();

            escpos.writeLF(font2, "________________________________________________");
            escpos.writeLF(String.format(formatInfo, "", "Subtotal:", "", app.DCFORM_P.format(total)));

            if (invoice.getTipoEntrega() == TIPO_DOMICILIO) {
                escpos.writeLF(font2, "________________________________________________");

                escpos.writeLF(String.format(formatInfo, "1", "Domicilio", "", app.DCFORM_P.format(invoice.getValorDelivery())));
                total = total.add(invoice.getValorDelivery());
            } else if (invoice.getTipoEntrega() == PanelPedido.TIPO_LOCAL) {
                escpos.writeLF(font2, "________________________________________________");
                if (invoice.getPorcService() > 0) {
                    escpos.writeLF(String.format(formatInfo, "", "Servicio voluntario", invoice.getPorcService() + "%", app.DCFORM_P.format(invoice.getValueService())));
                } else {
                    escpos.writeLF("No incluye servicio voluntario");
                }

                total = total.add(invoice.getValor().multiply(BigDecimal.valueOf(invoice.getPorcService() / 100)));
            }
            escpos.writeLF(font2, "________________________________________________");

            escpos.writeLF(String.format(formatInfo, "", "", "Total:", app.DCFORM_P.format(total)));

            escpos.writeLF(font2, "================================================");

            escpos.feed(1);

            escpos.writeLF(font2, "Gracias por su compra");
            
            
            escpos.writeLF(font5, "#QuedateEnCasa");
            
            escpos.feed(5);

            escpos.cut(EscPos.CutMode.FULL);

            escpos.close();

        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(PanelPedido.class.getName()).log(Level.ALL.SEVERE, null, ex);
        }
    }

    public void imprimirPedido(Invoice invoice, String printerName) {

        Waiter waiter = null;
        Table table = null;
        Client client = null;
        try {
            waiter = app.getControl().getWaitersByID(invoice.getIdWaitress());
            table = app.getControl().getTableByID(invoice.getTable());

            client = app.getControl().getClient(invoice.getIdCliente().toString());
        } catch (Exception e) {
        }

        PrintService printService = PrinterOutputStream.getPrintServiceByName(printerName);
        EscPos escpos;
        try {

            Style font2 = new Style().setFontSize(Style.FontSize._1, Style.FontSize._1).setJustification(EscPosConst.Justification.Center);
            Style font3 = new Style().setFontSize(Style.FontSize._2, Style.FontSize._2);
            Style font4 = new Style().setFontSize(Style.FontSize._1, Style.FontSize._1).setJustification(EscPosConst.Justification.Right);
            Style font5 = new Style().setFontSize(Style.FontSize._2, Style.FontSize._2).setJustification(EscPosConst.Justification.Right);

            escpos = new EscPos(new PrinterOutputStream(printService));
            escpos.feed(1);

            escpos.writeLF(font3, String.format("%1s", invoice.getFactura()));
            escpos.write(font2, String.format("%20.20s", app.DF_SL.format(invoice.getFecha())));
            escpos.write(font3, String.format("%8.8s", app.DF_TIME.format(invoice.getFecha())));
            escpos.writeLF("");

            if (invoice.getTipoEntrega() == PanelPedido.TIPO_LOCAL) {
                escpos.write(font2, "Mesa:   ");
                escpos.writeLF(font5, (table != null ? table.getName() : "- - -"));
                escpos.write(font2, "Mesero: ");
                escpos.writeLF(font5, (waiter != null ? waiter.getName() : "- - -"));
            } else {
                escpos.writeLF(font5, "Domicilio");
            }
            escpos.feed(1);

            String column1Format = "%3.3s";  // fixed size 3 characters, left aligned
            String column2Format = "%-20.20s";  // fixed size 8 characters, left aligned
            String column3Format = "%7.7s";   // fixed size 6 characters, right aligned
            String column4Format = "%12.12s";   // fixed size 6 characters, right aligned
            String formatInfo = column1Format + " " + column2Format + " " + column3Format + " " + column4Format;

            escpos.writeLF(font2, "===============================================");
            List<ProductoPed> products = invoice.getProducts();
            for (int i = 0; i < products.size(); i++) {
                ProductoPed product = products.get(i);
                Presentation presentation = product.getPresentation();                
                double priceFinal = product.getPrecio() + product.getValueAdicionales();
                escpos.writeLF(String.format(formatInfo, product.getCantidad(), (product.getProduct().getName()).toUpperCase(),
                        "", app.DCFORM_P.format(priceFinal)));
                String stPres = "";
                if (presentation != null) {
                    stPres = " (" + presentation.getName() + ")";
                    escpos.writeLF("    " + stPres);
                }                

                StringBuilder stb = new StringBuilder();
                if (product.getExclusiones().size() > 0) {                    
                    stb.append("Sin: ");
                    for (int j = 0; j < product.getExclusiones().size(); j++) {
                        Ingredient ing = product.getExclusiones().get(j);
                        stb.append(ing.getName()).append(" - ");
                    }
                    stb.substring(0, stb.length() - 3);
                    escpos.writeLF("    " + stb.toString());
                }

                stb = new StringBuilder();
                for (int j = 0; j < product.getAdicionales().size(); j++) {
                    Additional adic = product.getAdicionales().get(j).getAdditional();
                    int cant = product.getAdicionales().get(j).getCantidad();
                    stb.append("+").append(adic.getName()).append("(x").append(cant).append(")");
                    escpos.writeLF("    " + stb.toString());
                    stb.setLength(0);
                }
                if (i != products.size() - 1) {
                    escpos.writeLF(font2, ". . . . . . . . . . . . . . . . . . . . . .");
                }
            }

            escpos.writeLF(font2, "================================================");

            escpos.feed(1);

            escpos.feed(5);

            escpos.cut(EscPos.CutMode.FULL);

            escpos.close();

        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(PanelPedido.class.getName()).log(Level.ALL.SEVERE, null, ex);
        }
    }

}
