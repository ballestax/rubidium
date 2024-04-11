package com.rb;

import com.rb.domain.Additional;
import com.rb.domain.Client;
import com.rb.domain.ConfigDB;
import com.rb.domain.Ingredient;
import com.rb.domain.Invoice;
import com.rb.domain.Order;
import com.rb.domain.Item;
import com.rb.domain.Presentation;
import com.rb.domain.ProductoPed;
import com.rb.domain.Table;
import com.rb.domain.Waiter;
import com.rb.gui.PanelPedido;
import static com.rb.gui.PanelPedido.TIPO_DOMICILIO;
import com.github.anastaciocintra.escpos.EscPos;
import com.github.anastaciocintra.escpos.EscPosConst;
import com.github.anastaciocintra.escpos.Style;
import com.github.anastaciocintra.escpos.image.BitImageWrapper;
import com.github.anastaciocintra.escpos.image.Bitonal;
import com.github.anastaciocintra.escpos.image.BitonalThreshold;
import com.github.anastaciocintra.escpos.image.CoffeeImage;
import com.github.anastaciocintra.escpos.image.CoffeeImageImpl;
import com.github.anastaciocintra.escpos.image.EscPosImage;
import com.github.anastaciocintra.output.PrinterOutputStream;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import javax.print.PrintService;
import org.dz.Imagenes;

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
            waiter = app.getControl().getWaitressByID(invoice.getIdWaitress());
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
            Image imagen = app.getImgManager().getImagen("gui/img/" + "logo-2.png", 150, 150);
            BufferedImage buffImagen = Imagenes.toBuffereredImage(imagen);
            CoffeeImage cfImage = new CoffeeImageImpl(buffImagen);
            EscPosImage escposImage = new EscPosImage(cfImage, algorithm);

            //DATOS PARA LA FACTURA
            ConfigDB config = app.getControl().getConfigLocal(com.rb.Configuration.BS_NAME);
            String BS_NAME = config != null ? config.getValor() : app.getConfiguration().getProperty(Configuration.BS_NAME);

            config = app.getControl().getConfigLocal(com.rb.Configuration.BS_ID);
            String BS_ID = config != null ? config.getValor() : app.getConfiguration().getProperty(Configuration.BS_ID);

            config = app.getControl().getConfigLocal(com.rb.Configuration.BS_ADDRESS);
            String BS_ADDRESS = config != null ? config.getValor() : app.getConfiguration().getProperty(Configuration.BS_ADDRESS);

            config = app.getControl().getConfigLocal(com.rb.Configuration.BS_PHONE);
            String BS_PHONE = config != null ? config.getValor() : app.getConfiguration().getProperty(Configuration.BS_PHONE);

            config = app.getControl().getConfigLocal(com.rb.Configuration.BS_CUSTOM_TOP);
            String BS_CUSTOM1 = config != null ? config.getValor() : app.getConfiguration().getProperty(Configuration.BS_CUSTOM_TOP);

            config = app.getControl().getConfigLocal(com.rb.Configuration.BS_CUSTOM_BOTTON);
            String BS_CUSTOM2 = config != null ? config.getValor() : app.getConfiguration().getProperty(Configuration.BS_CUSTOM_BOTTON);
            
            config = app.getControl().getConfigLocal(com.rb.Configuration.BS_CUSTOM_SERVICE);
            String BS_CUSTOM3 = config != null ? config.getValor() : app.getConfiguration().getProperty(Configuration.BS_CUSTOM_SERVICE);

            config = app.getControl().getConfigLocal(com.rb.Configuration.BS_CUSTOM_QUALITY_MSG);
            String BS_QUALITY_MESSAGE = config != null ? config.getValor() : app.getConfiguration().getProperty(Configuration.BS_CUSTOM_QUALITY_MSG);

            config = app.getControl().getConfigLocal(com.rb.Configuration.BS_CUSTOM_QUALITY_ENABLED);
            String BS_QUALITY_ENABLED = config != null ? config.getValor() : app.getConfiguration().getProperty(Configuration.BS_CUSTOM_QUALITY_ENABLED);

            config = app.getControl().getConfigLocal(com.rb.Configuration.BS_CUSTOM_QUALITY_SCALE);
            String BS_QUALITY_SCALE = config != null ? config.getValor() : app.getConfiguration().getProperty(Configuration.BS_CUSTOM_QUALITY_SCALE);

            // this wrapper uses esc/pos sequence: "ESC '*'"
            BitImageWrapper imageWrapper = new BitImageWrapper();

            Style font2 = new Style().setFontSize(Style.FontSize._1, Style.FontSize._1).setJustification(EscPosConst.Justification.Center);
            Style font2Bold = new Style().setFontSize(Style.FontSize._1, Style.FontSize._1).setJustification(EscPosConst.Justification.Center).setBold(true);
            Style font3 = new Style().setFontSize(Style.FontSize._1, Style.FontSize._1);
            Style font4 = new Style().setFontSize(Style.FontSize._1, Style.FontSize._1).setJustification(EscPosConst.Justification.Right);
            Style font5 = new Style().setFontSize(Style.FontSize._1, Style.FontSize._2).setJustification(EscPosConst.Justification.Center);

            escpos = new EscPos(new PrinterOutputStream(printService));
            imageWrapper.setJustification(EscPosConst.Justification.Center);
            escpos.write(imageWrapper, escposImage);
            escpos.feed(1);
            escpos.writeLF(new Style().setFontSize(Style.FontSize._3, Style.FontSize._3).setJustification(EscPosConst.Justification.Center),
                    BS_NAME);
            escpos.writeLF(font2, BS_ID);
            escpos.writeLF(font2, BS_ADDRESS);
            escpos.writeLF(font2, BS_PHONE);
            escpos.feed(1);

            escpos.writeLF(font2, BS_CUSTOM1);
            escpos.feed(1);

            String cliente = app.getConfiguration().getProperty(Configuration.CLIENT_NAME, "LOCAL");
            if (invoice.getTipoEntrega() == PanelPedido.TIPO_LOCAL) {
                escpos.writeLF(font3, "Cliente: " + cliente);
                escpos.writeLF(font3, "Mesa:    " + (table != null ? table.getName() : "- - -"));
                escpos.writeLF(font3, "Mesero:  " + (waiter != null ? waiter.getName().toUpperCase() : "- - -"));
            } else {

                escpos.writeLF(font3, "Cliente:   " + (client != null ? client.getCellphone() : "- - -"));
                escpos.writeLF(font3, "Direccion: " + (client != null && !client.getAddresses().isEmpty() ? client.getAddresses().get(0) : "- - -"));
            }
            escpos.feed(1);

            config = app.getControl().getConfigLocal(Configuration.DOCUMENT_NAME);
            String docName = config != null ? config.getValor() : "Ticket N°:";
            escpos.writeLF(font3, String.format(docName + " %1s %25.25s", invoice.getFactura(), app.DF_FULL.format(invoice.getFecha())));
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

                escpos.writeLF(String.format(formatInfo, invoice.getNumDeliverys(), "Domicilio", "", app.DCFORM_P.format(invoice.getValorDelivery())));
                total = total.add(invoice.getValorDelivery());
            } else if (invoice.getTipoEntrega() == PanelPedido.TIPO_LOCAL) {
                escpos.writeLF(font2, "________________________________________________");
                if (invoice.getPorcService() > 0) {
                    escpos.writeLF(font2Bold, String.format(formatInfo, "", "Servicio voluntario", invoice.getPorcService() + "%", app.DCFORM_P.format(invoice.getValueService())));
                } else {
                    escpos.writeLF("No incluye servicio voluntario");
                }

                total = total.add(invoice.getValor().multiply(BigDecimal.valueOf(invoice.getPorcService() / 100)));
            }
            escpos.writeLF(font2, "________________________________________________");

            escpos.writeLF(String.format(formatInfo, "", "", "Total:", app.DCFORM_P.format(total)));

            escpos.writeLF(font2, "================================================");
            escpos.feed(1);

            if (invoice.getTipoEntrega() == PanelPedido.TIPO_LOCAL) {
                if (invoice.getPorcService() > 0) {
                    escpos.writeLF(font2Bold, "***ADVERTENCIA DE SERVICIO VOLUNTARIO***");                    
                    escpos.writeLF(font2, BS_CUSTOM3);
//                    escpos.writeLF(font2, "Segun lo establecido en la Ley 1935 del 3/ago/2018"
//                            + "(Art. 1,2, parágrafo 1, art. 3, par. 1, art 4)");
//                    escpos.writeLF(font2, "Se informa a los consumidores que este"
//                     +"   establecimiento de comercio sugiere a sus consumidores "
//                     +"una propina correspondiente al 8% del valor de su cuenta,"
//                     +" el cual podrá ser rechazado o modificado por usted "
//                     +"de acuerdo a su valoración del servicio prestado. ");
//                    escpos.writeLF(font2, "Indíquele a la persona que lo atiende si quiere pagar el valor del servicio voluntario en la factura."
//                            + " En caso de inconveniente comuníquese con nuestras líneas de atención.");
                }
            }

            if (invoice.getTipoEntrega() == PanelPedido.TIPO_LOCAL && Boolean.parseBoolean(BS_QUALITY_ENABLED)) {
                escpos.feed(2);
                escpos.writeLF(font2, BS_QUALITY_MESSAGE);
                escpos.writeLF(font2, "________________________________________________");
//                escpos.writeLF(font2, "|    Buena      |     Regular   |     Mala     |");
                escpos.writeLF(font2, BS_QUALITY_SCALE);
                escpos.writeLF(font2, "________________________________________________");
            }

            escpos.feed(1);

            escpos.writeLF(font2, BS_CUSTOM2);

//            escpos.writeLF(font5, "#QuedateEnCasa");
            escpos.feed(5);

            escpos.cut(EscPos.CutMode.FULL);

            escpos.close();

        } catch (IOException ex) {
            LogManager.getLogger(PanelPedido.class.getName()).log(Level.ERROR, ex.getMessage(), ex);
        }
    }

    public void imprimirPedido(Invoice invoice, String printerName) {

        Waiter waiter = null;
        Table table = null;
        Client client = null;
        try {
            waiter = app.getControl().getWaitressByID(invoice.getIdWaitress());
            table = app.getControl().getTableByID(invoice.getTable());

            client = app.getControl().getClient(invoice.getIdCliente().toString());
        } catch (Exception e) {
        }

        PrintService printService = PrinterOutputStream.getPrintServiceByName(printerName);
        EscPos escpos;
        try {

            Style font2 = new Style().setFontSize(Style.FontSize._1, Style.FontSize._1);
            Style font3 = new Style().setFontSize(Style.FontSize._2, Style.FontSize._1);
            Style font4 = new Style().setFontSize(Style.FontSize._1, Style.FontSize._1).setJustification(EscPosConst.Justification.Right);
            Style font5 = new Style().setFontSize(Style.FontSize._2, Style.FontSize._2).setJustification(EscPosConst.Justification.Right);
            Style font6 = new Style().setFontSize(Style.FontSize._2, Style.FontSize._2);
            Style font7 = new Style().setFontSize(Style.FontSize._1, Style.FontSize._2);

            escpos = new EscPos(new PrinterOutputStream(printService));
            escpos.feed(1);

            escpos.write(font2, "Comanda  :");
            escpos.writeLF(font6, String.format(" %1s", invoice.getFactura()));
            escpos.writeLF("");
            escpos.write(font3, String.format("%20.20s", app.DF_SL.format(invoice.getFecha())));
            escpos.write(font6, String.format("%8.8s", app.DF_TIME.format(invoice.getFecha())));
            escpos.writeLF("");

            if (invoice.getTipoEntrega() == PanelPedido.TIPO_LOCAL) {
                escpos.write(font2, "Mesa:   ");
                escpos.writeLF(font6, (table != null ? table.getName() : "- - -"));
                escpos.write(font2, "Mesero: ");
                escpos.writeLF(font6, (waiter != null ? waiter.getName() : "- - -"));
            } else {
                escpos.writeLF(font6, "Domicilio");
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

                }
                if (product.hasTermino()) {
                    stPres += "  [" + product.getTermino() + "]";
                }
                escpos.writeLF(font3, "    " + stPres);

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

            escpos.writeLF(font2, "****  COCINA ****");

            escpos.feed(5);

            escpos.cut(EscPos.CutMode.FULL);

            escpos.close();

        } catch (IOException ex) {
            LogManager.getLogger(PanelPedido.class.getName()).log(Level.ERROR, ex.getMessage(), ex);
        }
    }

    public void imprimirGuide(Invoice invoice, String printerName) {

        ConfigDB config = app.getControl().getConfigLocal(com.rb.Configuration.BS_NAME);
        String BS_NAME = config != null ? config.getValor() : app.getConfiguration().getProperty(Configuration.BS_NAME);

        Waiter waiter = null;
        Table table = null;
        Client client = null;
        try {
            waiter = app.getControl().getWaitressByID(invoice.getIdWaitress());
            table = app.getControl().getTableByID(invoice.getTable());

            client = app.getControl().getClient(invoice.getIdCliente().toString());
        } catch (Exception e) {
        }

        PrintService printService = PrinterOutputStream.getPrintServiceByName(printerName);
        EscPos escpos;
        try {

            Style font2 = new Style().setFontSize(Style.FontSize._1, Style.FontSize._1).setJustification(EscPosConst.Justification.Center);
            Style font3 = new Style().setFontSize(Style.FontSize._1, Style.FontSize._1);
            Style font4 = new Style().setFontSize(Style.FontSize._1, Style.FontSize._1).setJustification(EscPosConst.Justification.Right);
            Style font5 = new Style().setFontSize(Style.FontSize._2, Style.FontSize._2);

            String column1Format = "%3.3s";  // fixed size 3 characters, left aligned
            String column2Format = "%-25.25s";  // fixed size 8 characters, left aligned
            String column3Format = "%7.7s";   // fixed size 6 characters, right aligned
            String column4Format = "%9.9s";   // fixed size 6 characters, right aligned
            String formatInfo = column1Format + " " + column2Format + " " + column3Format + " " + column4Format;

            escpos = new EscPos(new PrinterOutputStream(printService));
            escpos.feed(1);

            escpos.writeLF(new Style().setFontSize(Style.FontSize._2, Style.FontSize._2).setJustification(EscPosConst.Justification.Center),
                    BS_NAME);
            escpos.feed(1);

            config = app.getControl().getConfig(Configuration.DOCUMENT_NAME);
            String docName = config != null ? config.getValor() : "Ticket N°:";
            escpos.writeLF(font3, String.format(docName + "  %1s", invoice.getFactura()));
            escpos.writeLF(font3, String.format("Fecha:       %1s", app.DF_FULL2.format(invoice.getFecha())));

            escpos.feed(1);
            if (invoice.getTipoEntrega() == PanelPedido.TIPO_LOCAL) {
                escpos.writeLF(font3, "Mesa:     " + (table != null ? table.getName() : "- - -"));
                escpos.writeLF(font3, "Mesero:   " + (waiter != null ? waiter.getName().toUpperCase() : "- - -"));
            } else {
                escpos.writeLF(font3, "Cliente:   " + (client != null ? client.getCellphone() : "- - -"));
                escpos.writeLF(font3, "Direccion: " + (client != null && !client.getAddresses().isEmpty() ? client.getAddresses().get(0) : "- - -"));
            }
            escpos.feed(1);

            BigDecimal total = invoice.getValor();

            escpos.writeLF(font2, "================================================");

            escpos.writeLF(String.format(formatInfo, "", "Subtotal:", "", app.DCFORM_P.format(total)));
            if (invoice.getTipoEntrega() == TIPO_DOMICILIO) {
                escpos.writeLF(font2, "________________________________________________");

                escpos.writeLF(String.format(formatInfo, "1", "Domicilio", "", app.DCFORM_P.format(invoice.getValorDelivery())));
                total = total.add(invoice.getValorDelivery());
            }

            if (invoice.isService() && invoice.getPorcService() > 0) {
                escpos.writeLF(font2, "________________________________________________");

                escpos.writeLF(String.format(formatInfo, invoice.getPorcService(), "Servicio vol.", "", app.DCFORM_P.format(invoice.getValueService())));
                total = total.add(new BigDecimal(invoice.getValueService()));
            }

            escpos.writeLF(font2, "________________________________________________");

            escpos.writeLF(String.format(formatInfo, "", "", "Total:", app.DCFORM_P.format(total)));

            escpos.writeLF(font2, "================================================");

            escpos.feed(4);

            escpos.cut(EscPos.CutMode.FULL);

            escpos.close();

        } catch (IOException ex) {
            LogManager.getLogger(PanelPedido.class.getName()).log(Level.ERROR, ex.getMessage(), ex);
        }
    }

    public void sendPulsePin(String printerName) {
        PrintService printService = PrinterOutputStream.getPrintServiceByName(printerName);
        EscPos escpos;
        try {
            escpos = new EscPos(new PrinterOutputStream(printService));
//            escpos.pulsePin(EscPos.PinConnector.Pin_2, 150, 175);            
            escpos.write(27).write(112).write(0).write(25).write(250);
            escpos.close();
        } catch (IOException ex) {
            LogManager.getLogger(PrintService.class.getName()).log(Level.ERROR, ex.getMessage(), ex);
        }
    }

    public void sendPulsePin(String printerName, List<Integer> data) {
        PrintService printService = PrinterOutputStream.getPrintServiceByName(printerName);
        EscPos escpos;
        try {
            escpos = new EscPos(new PrinterOutputStream(printService));
            for (Integer value : data) {
                escpos.write(value);
            }
            escpos.close();
        } catch (IOException ex) {
            LogManager.getLogger(PrintService.class.getName()).log(Level.ERROR, ex.getMessage(), ex);
        }
    }

    public void sendBuzzerPin(String printerName) {
        PrintService printService = PrinterOutputStream.getPrintServiceByName(printerName);
        EscPos escpos;
        try {
            escpos = new EscPos(new PrinterOutputStream(printService));
//            escpos.write("\\x1b\\x42").write(1).write(20);
            escpos.write(27).write(42).write(0).write(25).write(250);
            escpos.close();
        } catch (IOException ex) {
            LogManager.getLogger(PrintService.class.getName()).log(Level.ERROR, ex.getMessage(), ex);
        }
    }

    public void imprimirInventario(List<Item> list, String TAG, String printerName) {

        PrintService printService = PrinterOutputStream.getPrintServiceByName(printerName);
        EscPos escpos;
        try {

            Style font2 = new Style().setFontSize(Style.FontSize._1, Style.FontSize._1).setJustification(EscPosConst.Justification.Center);
            Style font3 = new Style().setFontSize(Style.FontSize._2, Style.FontSize._2);
            Style font4 = new Style().setFontSize(Style.FontSize._1, Style.FontSize._1).setJustification(EscPosConst.Justification.Right);
            Style font5 = new Style().setFontSize(Style.FontSize._2, Style.FontSize._2).setJustification(EscPosConst.Justification.Right);

            escpos = new EscPos(new PrinterOutputStream(printService));
            escpos.feed(1);

            Date fecha = new Date();

            escpos.writeLF(font3, String.format("%1s", "Inventario:" + TAG));
            escpos.write(font2, String.format("%20.20s", app.DF_SL.format(fecha)));
            escpos.write(font3, String.format("%8.8s", app.DF_TIME.format(fecha)));
            escpos.writeLF("");

            escpos.feed(1);

            String column1Format = "%1.1s";  // fixed size 3 characters, left aligned
            String column2Format = "%-20.20s";  // fixed size 8 characters, left aligned
            String column3Format = "%7.7s";   // fixed size 6 characters, right aligned
            String column4Format = "%12.12s";   // fixed size 6 characters, right aligned
            String formatInfo = column1Format + " " + column2Format + " " + column3Format + " " + column4Format;

            escpos.writeLF(font2, "===============================================");
            for (int i = 0; i < list.size(); i++) {
                Item item = list.get(i);
                escpos.writeLF(String.format(formatInfo, "", item.getName().toUpperCase(),
                        " . . . . ", app.DCFORM_P.format(item.getQuantity())));

            }

            escpos.writeLF(font2, "================================================");

            escpos.feed(5);

            escpos.cut(EscPos.CutMode.FULL);

            escpos.close();

        } catch (IOException ex) {
            LogManager.getLogger(PanelPedido.class.getName()).log(Level.ERROR, ex.getMessage(), ex);
        }
    }

    public void imprimirPedidoStations(Order order, String printerName) {

        Waiter waiter = null;
        Table table = null;
        Client client = null;
        try {
            waiter = app.getControl().getWaitressByID(order.getIdWaitress());
            table = app.getControl().getTableByID(order.getTable());

        } catch (Exception e) {
        }

        PrintService printService = PrinterOutputStream.getPrintServiceByName(printerName);
        EscPos escpos;
        try {

            Style font2 = new Style().setFontSize(Style.FontSize._1, Style.FontSize._1);
            Style font3 = new Style().setFontSize(Style.FontSize._2, Style.FontSize._1);
            Style font4 = new Style().setFontSize(Style.FontSize._1, Style.FontSize._1).setJustification(EscPosConst.Justification.Right);
            Style font5 = new Style().setFontSize(Style.FontSize._2, Style.FontSize._2).setJustification(EscPosConst.Justification.Right);
            Style font6 = new Style().setFontSize(Style.FontSize._2, Style.FontSize._2);
            Style font7 = new Style().setFontSize(Style.FontSize._1, Style.FontSize._2);

            escpos = new EscPos(new PrinterOutputStream(printService));
            escpos.feed(1);

            escpos.write(font2, "Comanda  :");
            escpos.writeLF(font6, String.format(" %1s", order.getId()));
            escpos.writeLF("");
            escpos.write(font3, String.format("%20.20s", app.DF_SL.format(order.getFecha())));
            escpos.write(font6, String.format("%8.8s", app.DF_TIME.format(order.getFecha())));
            escpos.writeLF("");

            if (order.getDeliveryType() == PanelPedido.TIPO_LOCAL) {
                escpos.write(font2, "Mesa:   ");
                escpos.writeLF(font6, (table != null ? table.getName() : "- - -"));
                escpos.write(font2, "Mesero: ");
                escpos.writeLF(font6, (waiter != null ? waiter.getName() : "- - -"));
            } else {
                escpos.writeLF(font6, "Domicilio");
            }
            escpos.feed(1);

            String column1Format = "%3.3s";  // fixed size 3 characters, left aligned
            String column2Format = "%-20.20s";  // fixed size 8 characters, left aligned
            String column3Format = "%7.7s";   // fixed size 6 characters, right aligned
            String column4Format = "%12.12s";   // fixed size 6 characters, right aligned
            String formatInfo = column1Format + " " + column2Format + " " + column3Format + " " + column4Format;

            escpos.writeLF(font2, "===============================================");
            List<ProductoPed> products = order.getProducts();
            for (int i = 0; i < products.size(); i++) {
                ProductoPed product = products.get(i);
                Presentation presentation = product.getPresentation();
                double priceFinal = product.getPrecio() + product.getValueAdicionales();
                escpos.writeLF(String.format(formatInfo, product.getCantidad(), (product.getProduct().getName()).toUpperCase(),
                        "", app.DCFORM_P.format(priceFinal)));
                String stPres = "";
                if (presentation != null) {
                    stPres = " (" + presentation.getName() + ")";

                }
                if (product.hasTermino()) {
                    stPres += "  [" + product.getTermino() + "]";
                }
                escpos.writeLF(font3, "    " + stPres);

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

            escpos.writeLF(font2, "****  COCINA ****");

            escpos.feed(5);

            escpos.cut(EscPos.CutMode.FULL);

            escpos.close();

        } catch (IOException ex) {
            LogManager.getLogger(PanelPedido.class.getName()).log(Level.ERROR, ex.getMessage(), ex);
        }
    }

    public void imprimirPedidoStations(Order order, List<ProductoPed> products, String station, String printerName, boolean isAdicion) {

        Waiter waiter = null;
        Table table = null;
        Client client = null;
        try {
            waiter = app.getControl().getWaitressByID(order.getIdWaitress());
            table = app.getControl().getTableByID(order.getTable());

        } catch (Exception e) {
        }

        System.out.println("printerName = " + printerName);
        PrintService printService = PrinterOutputStream.getPrintServiceByName(printerName);
        EscPos escpos;
        try {

            Style font2 = new Style().setFontSize(Style.FontSize._1, Style.FontSize._1);
            Style font3 = new Style().setFontSize(Style.FontSize._2, Style.FontSize._1);
            Style font4 = new Style().setFontSize(Style.FontSize._1, Style.FontSize._1).setJustification(EscPosConst.Justification.Right);
            Style font5 = new Style().setFontSize(Style.FontSize._2, Style.FontSize._2).setJustification(EscPosConst.Justification.Right);
            Style font6 = new Style().setFontSize(Style.FontSize._2, Style.FontSize._2);
            Style font7 = new Style().setFontSize(Style.FontSize._2, Style.FontSize._1).setJustification(EscPosConst.Justification.Center);;
            Style font8 = new Style().setFontSize(Style.FontSize._1, Style.FontSize._2);
            
            escpos = new EscPos(new PrinterOutputStream(printService));
            escpos.feed(1);

            if (isAdicion) {
                escpos.writeLF(font7, "****  ADICION  ****");
            }

            escpos.write(font2, "Comanda  :");
            escpos.writeLF(font6, String.format(" #%1s", order.getId()));
            escpos.writeLF("");
//            escpos.write(font2, String.format("Fecha:"));
            escpos.writeLF(font3, String.format(" %23s", app.DF_SL.format(order.getFecha())));
//            escpos.write(font2, String.format("Hora:"));
            escpos.writeLF(font6, String.format(" %23s", app.DF_TIME.format(order.getFecha())));            
            escpos.writeLF(font2, "_______________________________________________");
            if (order.getDeliveryType() == PanelPedido.TIPO_LOCAL) {
                escpos.write(font2, "Mesa:   ");
                escpos.writeLF(font6, (table != null ? table.getName() : "- - -"));
                escpos.write(font2, "Mesero: ");
                escpos.writeLF(font6, (waiter != null ? waiter.getName().toUpperCase() : "- - -"));
            } else {
                escpos.writeLF(font6, "Domicilio");
            }
            escpos.feed(1);

            String column1Format = "%3.3s";  // fixed size 3 characters, left aligned
            String column2Format = "%-20.20s";  // fixed size 8 characters, left aligned
            String column3Format = "%7.7s";   // fixed size 6 characters, right aligned
            String column4Format = "%12.12s";   // fixed size 6 characters, right aligned
            String formatInfo = column1Format + " " + column2Format + " " + column3Format + " " + column4Format;

            escpos.writeLF(font2, "================================================");

            for (int i = 0; i < products.size(); i++) {
                ProductoPed product = products.get(i);
                Presentation presentation = product.getPresentation();
                double priceFinal = product.getPrecio() + product.getValueAdicionales();
                escpos.writeLF(String.format(formatInfo, product.getCantidad(), (product.getProduct().getName()).toUpperCase(),
                        "", app.DCFORM_P.format(priceFinal)));
                String stPres = "";
                if (presentation != null) {
                    stPres = " (" + presentation.getName().toUpperCase() + ")";

                }
                if (product.hasTermino() && !product.getTermino().isEmpty()) {
                    stPres += "  [" + product.getTermino() + "]";
                }
                escpos.writeLF(font2, "    " + stPres);

                if (product.hasExcluisones()) {
                    escpos.writeLF(font2, "    - - - - - - - - -  - ");
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
                if (product.hasAdditionals()) {
                    escpos.writeLF(font2, "    - - - - - - - - -  - ");
                }
                stb = new StringBuilder();
                for (int j = 0; j < product.getAdicionales().size(); j++) {
                    Additional adic = product.getAdicionales().get(j).getAdditional();
                    int cant = product.getAdicionales().get(j).getCantidad();
                    stb.append("+ ").append(adic.getName()).append("(x").append(cant).append(")");
                    escpos.writeLF("    " + stb.toString());
                    stb.setLength(0);
                }
                if (i != products.size() - 1) {
                    escpos.writeLF(font2, ". . . . . . . . . . . . . . . . . . . . . . . .");
                }
            }

            escpos.writeLF(font2, "================================================");

            escpos.feed(1);

            escpos.writeLF(font7, "****  " + station.toUpperCase() + "  ****");

            escpos.feed(5);

            escpos.cut(EscPos.CutMode.FULL);

            escpos.close();

        } catch (IOException ex) {
            LogManager.getLogger(PanelPedido.class.getName()).log(Level.ERROR, ex.getMessage(), ex);
        }
    }

}
