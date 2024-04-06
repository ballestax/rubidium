/*
 * To change this license footer, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rb;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.tool.xml.ElementList;
import com.itextpdf.tool.xml.XMLWorker;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.itextpdf.tool.xml.css.CssFile;
import com.itextpdf.tool.xml.css.StyleAttrCSSResolver;
import com.itextpdf.tool.xml.html.Tags;
import com.itextpdf.tool.xml.parser.XMLParser;
import com.itextpdf.tool.xml.pipeline.css.CSSResolver;
import com.itextpdf.tool.xml.pipeline.css.CssResolverPipeline;
import com.itextpdf.tool.xml.pipeline.end.ElementHandlerPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipelineContext;
import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import org.dz.MyDefaultTableModel;


/**
 *
 * @author LuisR
 */
public class PDFGenerator {

    private final Aplication app;
    private java.awt.Image imagen;
    private Image img;
    public static final Font FONT_T = new Font(FontFamily.HELVETICA, 14, Font.BOLD, new BaseColor(190, 0, 0));
    public static final Font FONT = new Font(FontFamily.HELVETICA, 8, Font.NORMAL, new BaseColor(20, 45, 120));
    public static final Font FONT_F = new Font(FontFamily.HELVETICA, 9, Font.NORMAL, new BaseColor(20, 45, 170));

    public PDFGenerator(Aplication app) {
        this.app = app;
    }

    class TableFooter extends PdfPageEventHelper {

        /**
         * Alternating phrase for the header.
         */
        Phrase[] header = new Phrase[2];
        /**
         * Current page number (will be reset for every chapter).
         */
        int pagenumber;

        /**
         * Initialize one of the headers.
         *
         * @see com.itextpdf.text.pdf.PdfPageEventHelper#onOpenDocument(
         * com.itextpdf.text.pdf.PdfWriter, com.itextpdf.text.Document)
         */
        public void onOpenDocument(PdfWriter writer, Document document) {
            header[0] = new Phrase("Dero Inventario", FONT_F);
        }

        /**
         * Increase the page number.
         *
         * @see com.itextpdf.text.pdf.PdfPageEventHelper#onStartPage(
         * com.itextpdf.text.pdf.PdfWriter, com.itextpdf.text.Document)
         */
        public void onStartPage(PdfWriter writer, Document document) {
            pagenumber++;
        }

        /**
         * Adds the header and the footer.
         *
         * @see com.itextpdf.text.pdf.PdfPageEventHelper#onEndPage(
         * com.itextpdf.text.pdf.PdfWriter, com.itextpdf.text.Document)
         */
        public void onEndPage(PdfWriter writer, Document document) {
            Rectangle rect = writer.getBoxSize("art");
            ColumnText.showTextAligned(writer.getDirectContent(),
                    Element.ALIGN_LEFT, header[0],
                    rect.getLeft(), rect.getBottom() - 12, 0);
            ColumnText.showTextAligned(writer.getDirectContent(),
                    Element.ALIGN_RIGHT, new Phrase(String.format("Pagina %d", pagenumber), FONT_F),
                    (rect.getRight()) - 20, rect.getBottom() - 12, 0);
        }
    }

    public void createDocument(String filename, MyDefaultTableModel model, String title, String html, float[] withs, int[] align) throws DocumentException, FileNotFoundException, BadElementException, IOException {
//        Document document = new Document();
        Document document = new Document(PageSize.LETTER);
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
        TableFooter event = new TableFooter();
        writer.setBoxSize("art", new Rectangle(26, 34, 559, 788));
        writer.setPageEvent(event);
        //PDF on memory
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        PdfWriter.getInstance(document, baos);
//        Margenes  
//        document.setPageSize(PageSize.A5);
//        document.setMargins(36, 72, 108, 180);
//        document.setMarginMirroring(true);
        imagen = app.getImgManager().getImagen("gui/img/LlantasAmerica.png", 600, 200);
        img = Image.getInstance(imagen, Color.white);
        document.open();
//        document.add(img);
        PdfContentByte canvas = writer.getDirectContentUnder();
//        Image image = Image.getInstance(IMAGE);
//        img.scaleAbsolute(PageSize.A4);
        float height = img.getHeight();
        float width = img.getWidth();
        img.setAbsolutePosition(PageSize.A4.getWidth() / 2 - width / 2, PageSize.A4.getHeight() / 2 - height / 2);
        canvas.saveState();
        PdfGState state = new PdfGState();
        state.setFillOpacity(0.1f);
        canvas.setGState(state);
        canvas.addImage(img);
        canvas.restoreState();

        document.add(createTableEncabezado());
        LineSeparator line
                = new LineSeparator(1, 100, null, Element.ALIGN_CENTER, -2);
        Paragraph stars = new Paragraph(20);
        stars.add(new Chunk(line));
        stars.setSpacingAfter(30);
        document.add(stars);
        if (html != null && !html.isEmpty()) {
            document.add(getTableHtml(html));
        }

        document.add(createTable(model, title, withs, align));
        document.close();
    }

    public void createDocument(String filename, ArrayList<MyDefaultTableModel> modelos, String[] titles, String[] htmls, float[][] withs, int[][] align) throws DocumentException, FileNotFoundException, BadElementException, IOException {
//        Document document = new Document();
        Document document = new Document(PageSize.LETTER);
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
        TableFooter event = new TableFooter();
        writer.setBoxSize("art", new Rectangle(26, 34, 559, 788));
        writer.setPageEvent(event);
        //PDF on memory
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        PdfWriter.getInstance(document, baos);
//        Margenes  
//        document.setPageSize(PageSize.A5);
//        document.setMargins(36, 72, 108, 180);
//        document.setMarginMirroring(true);
        imagen = app.getImgManager().getImagen("gui/img/LlantasAmerica.png", 600, 200);
        img = Image.getInstance(imagen, Color.white);
        document.open();
//        document.add(img);
        PdfContentByte canvas = writer.getDirectContentUnder();
//        Image image = Image.getInstance(IMAGE);
//        img.scaleAbsolute(PageSize.A4);
        float height = img.getHeight();
        float width = img.getWidth();
        img.setAbsolutePosition(PageSize.A4.getWidth() / 2 - width / 2, PageSize.A4.getHeight() / 2 - height / 2);
        canvas.saveState();
        PdfGState state = new PdfGState();
        state.setFillOpacity(0.1f);
        canvas.setGState(state);
        canvas.addImage(img);
        canvas.restoreState();

        document.add(createTableEncabezado());
        LineSeparator line
                = new LineSeparator(1, 100, null, Element.ALIGN_CENTER, -2);
        Paragraph stars = new Paragraph(20);
        stars.add(new Chunk(line));
        stars.setSpacingAfter(30);
        document.add(stars);
        for (int i = 0; i < htmls.length; i++) {
            String html = htmls[i];
            if (html != null && !html.isEmpty()) {
                document.add(getTableHtml(html));
                document.add(stars);
            }
        }

        for (int i = 0; i < modelos.size(); i++) {
            MyDefaultTableModel modelo = modelos.get(i);
            document.add(createTable(modelo, titles[i], withs[i], align[i]));
            document.add(stars);
        }

        document.close();
    }

    public void createDocument(String filename, String title, String html) throws DocumentException, FileNotFoundException, BadElementException, IOException {
        Document document = new Document(PageSize.LETTER);
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
        TableFooter event = new TableFooter();
        writer.setBoxSize("art", new Rectangle(26, 34, 559, 788));
        writer.setPageEvent(event);

//        imagen = app.getImgManager().getImagen("gui/img/LlantasAmerica.png", 600, 200);
//        img = Image.getInstance(imagen, Color.white);
        document.open();
////        document.add(img);
//        PdfContentByte canvas = writer.getDirectContentUnder();
////        Image image = Image.getInstance(IMAGE);
////        img.scaleAbsolute(PageSize.A4);
//        float height = img.getHeight();
//        float width = img.getWidth();
//        img.setAbsolutePosition(PageSize.A4.getWidth() / 2 - width / 2, PageSize.A4.getHeight() / 2 - height / 2);
//        canvas.saveState();
//        PdfGState state = new PdfGState();
//        state.setFillOpacity(0.1f);
//        canvas.setGState(state);
//        canvas.addImage(img);
//        canvas.restoreState();
        String CSS = "tr { border: 1; text-align: left; } th { background-color: lightgreen; padding: 3px; } td {background-color: white;  padding: 3px; font-size: 11pt; } .label{background-color: #FFAAAA;  padding: 3px; font-size: 10pt;, border:1px;}";
        ElementList parseHtml = parseHtml(html, CSS);
        for (int i = 0; i < parseHtml.size(); i++) {
            Element get = parseHtml.get(i);
            document.add(get);
        }

        document.close();

    }

    private PdfPTable createTableEncabezado() {

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100f);
        table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
        // the cell object

        PdfPCell cell = new PdfPCell();
        cell = new PdfPCell(new Phrase("LLANTAS AMERICA", FONT_T));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setColspan(3);
        cell.setRowspan(2);
        table.addCell(cell);

        cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        img.setWidthPercentage(100);
        cell.addElement(img);
        cell.setRowspan(4);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Calle 16 N° 11-67 Maicao - La Guajira - Colombia", FONT));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setColspan(3);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Telefonos: (035)7250726", FONT));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setColspan(3);
        table.addCell(cell);

        table.completeRow();

        return table;

    }

    private PdfPTable createTable(MyDefaultTableModel model, String title, float[] withs, int[] align) {

        int COLS = model.getColumnCount();

//        PdfPTable table = new PdfPTable(COLS);
        PdfPTable table = new PdfPTable(withs);

        table.setWidthPercentage(100f);
        table.getDefaultCell().setUseAscender(true);
        table.getDefaultCell().setUseDescender(true);

        Font f = new Font();
        f.setColor(BaseColor.BLUE);

        Font f1 = new Font();
        f1.setSize(9);

        Font f2 = new Font();
        f2.setSize(10);

        PdfPCell cell = new PdfPCell();
        cell = new PdfPCell(new Phrase(title, f));
        cell.setBackgroundColor(new BaseColor(204, 242, 255));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setColspan(withs.length);
        table.addCell(cell);
        table.getDefaultCell().setBackgroundColor(BaseColor.LIGHT_GRAY);
        for (int j = 0; j < 2; j++) {
            for (int i = 0; i < COLS; i++) {
                if (align[i] != -1) {
                    table.addCell(new Phrase(model.getColumnName(i), f2));
                }
            }
        }
        table.getDefaultCell().setBackgroundColor(null);
        table.setHeaderRows(3);
        table.setFooterRows(1);

        for (int i = 0; i < model.getRowCount(); i++) {
            for (int j = 0; j < model.getColumnCount(); j++) {
                if (align[j] != -1) {
                    String val = model.getValueAt(i, j) != null ? model.getValueAt(i, j).toString() : "";
                    if (align[j] == 2) {
                        try {
                            val = app.DCFORM_P.format(Double.parseDouble(val));
                        } catch (Exception e) {
                        }
                    }
                    cell = new PdfPCell(new Phrase(val, f1));
                    cell.setHorizontalAlignment(align[j]);
                    table.addCell(cell);
                }
            }
        }
        return table;

    }

    public Image getWatermarkedImage(PdfContentByte cb, Image img, String watermark) throws DocumentException {
        float width = img.getScaledWidth();
        float height = img.getScaledHeight();
        PdfTemplate template = cb.createTemplate(width, height);
        template.addImage(img, width, 0, 0, height, 0, 0);
        ColumnText.showTextAligned(template, Element.ALIGN_CENTER,
                new Phrase(watermark, FONT), width / 2, height / 2, 30);
        return Image.getInstance(template);
    }

    public PdfPTable getTableHtml(String htmlSt) throws IOException {
        String CSS = "tr { text-align: left; } th { background-color: lightgreen; padding: 3px; } td {background-color: white;  padding: 3px; font-size: 11pt; } .label{background-color: #CCCCFF;  padding: 3px; font-size: 10pt;}";
        return getTableHtml(htmlSt, CSS);
    }

    public PdfPTable getTableHtml(String htmlSt, String CSS) throws IOException {

//        System.out.println(htmlSt);
        CSSResolver cssResolver = new StyleAttrCSSResolver();
        CssFile cssFile = XMLWorkerHelper.getCSS(new ByteArrayInputStream(CSS.getBytes()));
        cssResolver.addCss(cssFile);

        // HTML
        HtmlPipelineContext htmlContext = new HtmlPipelineContext(null);
        htmlContext.setTagFactory(Tags.getHtmlTagProcessorFactory());

        // Pipelines
        ElementList elements = new ElementList();
        ElementHandlerPipeline pdf = new ElementHandlerPipeline(elements, null);
        HtmlPipeline html = new HtmlPipeline(htmlContext, pdf);
        CssResolverPipeline css = new CssResolverPipeline(cssResolver, html);

        // XML Worker
        XMLWorker worker = new XMLWorker(css, true);
        XMLParser p = new XMLParser(worker);
        p.parse(new ByteArrayInputStream(htmlSt.getBytes()));

        PdfPTable table = (PdfPTable) elements.get(0);
        table.setWidthPercentage(100f);
        table.setSpacingAfter(10.0f);
        return table;
    }

    public ElementList parseHtml(String htmlSt, String CSS) throws IOException {

//        System.out.println(htmlSt);
        CSSResolver cssResolver = new StyleAttrCSSResolver();
        CssFile cssFile = XMLWorkerHelper.getCSS(new ByteArrayInputStream(CSS.getBytes()));
        cssResolver.addCss(cssFile);

        // HTML
        HtmlPipelineContext htmlContext = new HtmlPipelineContext(null);
        htmlContext.setTagFactory(Tags.getHtmlTagProcessorFactory());

        // Pipelines
        ElementList elements = new ElementList();
        ElementHandlerPipeline pdf = new ElementHandlerPipeline(elements, null);
        HtmlPipeline html = new HtmlPipeline(htmlContext, pdf);
        CssResolverPipeline css = new CssResolverPipeline(cssResolver, html);

        // XML Worker
        XMLWorker worker = new XMLWorker(css, true);
        XMLParser p = new XMLParser(worker);
        p.parse(new ByteArrayInputStream(htmlSt.getBytes()));

        return elements;
    }

}
