/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rb;

import java.awt.Color;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.table.DefaultTableModel;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;



/**
 *
 * @author hp
 */
public class XLSManager {

    private static Aplication app;
    public static final String TAREA_ERROR = "error";
    public static final String TAREA_PROGRESO = "pogreso";
    public static final String TAREA_COMPLETADA = "completa";
    public static final String TAREA_INICIADA = "iniciando";

    private XLSManager() {
    }

    public static XLSManager getInstance(Aplication app) {
        XLSManager.app = app;
        return XLSManagerHolder.INSTANCE;
    }

    private static class XLSManagerHolder {

        private static final XLSManager INSTANCE = new XLSManager();
    }

    public ArrayList cargarArchivo(String path) {
        ArrayList lista = new ArrayList();
        DecimalFormat format = new DecimalFormat("#");

        try {
            FileInputStream fis = new FileInputStream(path);
            HSSFWorkbook orig = new HSSFWorkbook(fis);
            HSSFSheet sheet = orig.getSheetAt(0);
            boolean cont = true;
            int indR = 1;
            int i = 0;
            while (cont) {
                HSSFRow row = sheet.getRow(indR);
                if (row != null) {
                    for (i = 0; i < 5; i++) {
                        HSSFCell cell = row.getCell(i);
                        String cellValue = "";
                        if (cell.getCellType() == CellType.STRING) {
                            cellValue = cell.getStringCellValue();
                        } else if (cell.getCellType() == CellType.NUMERIC) {
                            cellValue = format.format(cell.getNumericCellValue());
                        }
                        System.out.print(cellValue + "\t");
                    }
                    indR++;
                    System.out.println();
                } else {
                    cont = false;
                }
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return lista;

    }

    public void exportarTabla(DefaultTableModel modelo, String title, String ruta, PropertyChangeListener listener) {
        exportarTabla(modelo, title, ruta, listener, null);
    }

    public void exportarTabla(DefaultTableModel modelo, String title, String ruta, PropertyChangeListener listener, String[] values) {
        
        try {
            FileOutputStream out = new FileOutputStream(ruta);            
            XSSFWorkbook book = new XSSFWorkbook();
            XSSFSheet sheet = book.createSheet("Reporte 1");
            DataFormat formato = book.createDataFormat();
            short format1 = formato.getFormat("#,###,###,###");
            XSSFRow row = null;
            XSSFCell cell = null;

            Insets top = new Insets(1, 1, 0, 1);
            Insets botton = new Insets(0, 1, 1, 1);
            Insets all = new Insets(1, 1, 1, 1);

            //DefaultIndexedColorMap defIndexedColorMap = new DefaultIndexedColorMap();

            XSSFColor CF_CELDA = new XSSFColor(new byte[]{2,3,4});            
            XSSFColor BLANCO = new XSSFColor(new byte[]{(byte)255,(byte)255,(byte)255});
            XSSFColor NEGRO = new XSSFColor(new byte[]{(byte)0,(byte)0,(byte)0});
            XSSFColor VERDE = new XSSFColor(new byte[]{(byte)0,(byte)255,(byte)0});

            final VerticalAlignment CENTER_V = VerticalAlignment.CENTER;
            final HorizontalAlignment CENTER_H = HorizontalAlignment.CENTER;
            final HorizontalAlignment LEFT = HorizontalAlignment.LEFT;
            final  HorizontalAlignment RIGHT = HorizontalAlignment.RIGHT;
            XSSFCellStyle stiloRight = getStilox(book, CF_CELDA, NEGRO, BLANCO, (short) 10, CENTER_V, RIGHT, all, format1);
            XSSFCellStyle stiloLeft = getStilox(book, CF_CELDA, NEGRO, BLANCO, (short) 10, CENTER_V, LEFT, all, format1);

            int fil = 0;

            listener.propertyChange(new PropertyChangeEvent(modelo, TAREA_INICIADA, 0, modelo.getRowCount()));
            //Row title
            row = sheet.createRow(fil);
            cell = row.createCell(0);
            cell.setCellValue(title);
            cell.setCellStyle(getStilox(book, BLANCO, VERDE, VERDE, (short) 12,CENTER_V, LEFT, all));
            fil++;
            if (values != null) {
                row = sheet.createRow(fil);
                for (int i = 0, j = 0; i < values.length; i++, j += 2) {
                    String value = values[i];
                    String[] split = value.split(";;");
                    cell = row.createCell(j);
                    cell.setCellValue(split[0]);
                    cell = row.createCell(j + 1);
                    cell.setCellValue(split[1]);
                }
                fil++;
            }

            //ColumnHeader Cabeceras
            row = sheet.createRow(fil);
            //Agregado verificacion de boton de ultima columna para omitirlo de la exportacion
            int NCOL = modelo.getColumnCount();
            String UCOL = modelo.getColumnName(NCOL - 1);
            if (UCOL.equalsIgnoreCase("Ver") || UCOL.equalsIgnoreCase("Revisar")) {
                NCOL = NCOL - 1;
            }
            for (int c = 0; c < NCOL; c++) {
                cell = row.createCell(c);
                cell.setCellValue(modelo.getColumnName(c));
                cell.setCellStyle(getStilox(book, CF_CELDA, NEGRO, CF_CELDA, (short) 11, CENTER_V, CENTER_H, all));
                sheet.setColumnWidth(c, 20 * 256);
            }
            sheet.setColumnWidth(0, 20 * 256);
            fil++;

            for (int j = 0; j < modelo.getRowCount(); j++) {
                row = sheet.createRow(fil);
                for (int k = 0; k < NCOL; k++) {
                    Object obj = modelo.getValueAt(j, k);
                    if (obj != null) {
                        cell = row.createCell(k);
                        boolean end = modelo.getRowCount() - 1 == j;
                        if (obj instanceof Integer) {
                            cell.setCellValue(Integer.parseInt(obj.toString()));
                            cell.setCellType(CellType.NUMERIC);
                            cell.setCellStyle(stiloRight);
                        } else if (obj instanceof Double || obj instanceof BigDecimal) {                            
                            cell.setCellValue(Double.parseDouble(obj.toString()));
                            cell.setCellType(CellType.NUMERIC);
                            cell.setCellStyle(stiloRight);
                        } else if (obj instanceof Long) {
                            cell.setCellValue(Long.parseLong(obj.toString()));
                            cell.setCellType(CellType.NUMERIC);
                            cell.setCellStyle(stiloRight);
                        } else {
                            cell.setCellType(CellType.NUMERIC);
                            cell.setCellValue(obj.toString());
                            cell.setCellStyle(stiloLeft);
                        }
                    } else {
                        cell = row.createCell(k);
                        cell.setCellValue("");
                        cell.setCellStyle(stiloLeft);
                    }
                }
                fil++;
                listener.propertyChange(new PropertyChangeEvent(modelo, TAREA_PROGRESO, 0, j));
            }
            book.write(out);
            out.close();
            listener.propertyChange(new PropertyChangeEvent(modelo, TAREA_COMPLETADA, ruta, 1));
        } catch (IOException e) {
            System.out.println("exception:" + e.getMessage());
        }
    }

    public void exportarTablas(HashMap<String, DefaultTableModel> modelos, String title, String ruta, PropertyChangeListener listener) {
        exportarTablas(modelos, title, ruta, listener, null);
    }

    public void exportarTablas(HashMap<String, DefaultTableModel> modelos, String title, String ruta, PropertyChangeListener listener, String[] values) {
        try {
            FileOutputStream out = new FileOutputStream(ruta);
            XSSFWorkbook book = new XSSFWorkbook();
            DataFormat formato = book.createDataFormat();
            short format1 = formato.getFormat("#,###,###,###");
            Insets top = new Insets(1, 1, 0, 1);
            Insets botton = new Insets(0, 1, 1, 1);
            Insets all = new Insets(1, 1, 1, 1);

            DefaultIndexedColorMap defIndexedColorMap = new DefaultIndexedColorMap();
            XSSFColor CF_CELDA = new XSSFColor(new byte[]{2,3,4});            
            XSSFColor BLANCO = new XSSFColor(new byte[]{(byte)255,(byte)255,(byte)255});
            XSSFColor NEGRO = new XSSFColor(new byte[]{(byte)0,(byte)0,(byte)0});
            XSSFColor VERDE = new XSSFColor(new byte[]{(byte)0,(byte)255,(byte)0});

            final VerticalAlignment CENTER_V = VerticalAlignment.CENTER;
            final HorizontalAlignment CENTER_H = HorizontalAlignment.CENTER;
            final HorizontalAlignment LEFT = HorizontalAlignment.LEFT;
            final  HorizontalAlignment RIGHT = HorizontalAlignment.RIGHT;
            XSSFCellStyle stiloRight = getStilox(book, CF_CELDA, NEGRO, BLANCO, (short) 10, CENTER_V, RIGHT, all, format1);
            XSSFCellStyle stiloLeft = getStilox(book, CF_CELDA, NEGRO, BLANCO, (short) 10, CENTER_V, LEFT, all, format1);

            if (modelos != null && !modelos.isEmpty()) {
                boolean init = false;
                int m = 0, ct = 0, tot = 0;
                for (Map.Entry<String, DefaultTableModel> entrySet : modelos.entrySet()) {
                    tot += entrySet.getValue().getRowCount();
                }
                listener.propertyChange(new PropertyChangeEvent(modelos, TAREA_INICIADA, 0, tot));
                for (Map.Entry<String, DefaultTableModel> entrySet : modelos.entrySet()) {
                    String titleSheet = entrySet.getKey();
                    DefaultTableModel modelo = entrySet.getValue();

                    XSSFSheet sheet = book.createSheet(titleSheet);

                    XSSFRow row = null;
                    XSSFCell cell = null;

                    int fil = 0;

                    //Row title
                    row = sheet.createRow(fil);
                    cell = row.createCell(0);
                    cell.setCellValue(title);
                    cell.setCellStyle(getStilox(book, BLANCO, VERDE, VERDE, (short) 12, CENTER_V, LEFT, all));
                    fil++;
                    if (values != null) {
                        row = sheet.createRow(fil);
                        for (int i = 0, j = 0; i < values.length; i++, j += 2) {
                            String value = values[i];
                            String[] split = value.split(";;");
                            cell = row.createCell(j);
                            cell.setCellValue(split[0]);
                            if (split.length > 1) {
                                cell = row.createCell(j + 1);
                                cell.setCellValue(split[1]);
                            }
                        }
                        fil++;
                    }

                    //ColumnHeader Cabeceras
                    row = sheet.createRow(fil);
                    //Agregado verificacion de boton de ultima columna para omitirlo de la exportacion
                    int NCOL = modelo.getColumnCount();
                    String UCOL = modelo.getColumnName(NCOL - 1);
                    if (UCOL.equalsIgnoreCase("Ver") || UCOL.equalsIgnoreCase("Revisar")) {
                        NCOL = NCOL - 1;
                    }
                    for (int c = 0; c < NCOL; c++) {
                        cell = row.createCell(c);
                        cell.setCellValue(modelo.getColumnName(c));
                        cell.setCellStyle(getStilox(book, CF_CELDA, NEGRO, CF_CELDA, (short) 11, CENTER_V, CENTER_H, all));
                        sheet.setColumnWidth(c, 20 * 256);
                    }
                    sheet.setColumnWidth(0, 20 * 256);
                    fil++;

                    for (int j = 0; j < modelo.getRowCount(); j++) {
                        row = sheet.createRow(fil);
                        for (int k = 0; k < NCOL; k++) {
                            Object obj = modelo.getValueAt(j, k);
                            if (obj != null) {
                                cell = row.createCell(k);
                                boolean end = modelo.getRowCount() - 1 == j;
                                if (obj instanceof Integer) {
                                    cell.setCellValue(Integer.parseInt(obj.toString()));
                                    cell.setCellType(CellType.NUMERIC);
                                    cell.setCellStyle(stiloRight);
                                } else if (obj instanceof Double) {                                    
                                    cell.setCellValue(Double.parseDouble(obj.toString()));
                                    cell.setCellType(CellType.NUMERIC);
                                    cell.setCellStyle(stiloRight);
                                } else if (obj instanceof Long) {
                                    cell.setCellValue(Long.parseLong(obj.toString()));
                                    cell.setCellType(CellType.NUMERIC);
                                    cell.setCellStyle(stiloRight);
                                } else {
                                    cell.setCellType(CellType.NUMERIC);
                                    cell.setCellValue(obj.toString());
                                    cell.setCellStyle(stiloLeft);
                                }
                            } else {
                                cell = row.createCell(k);
                                cell.setCellValue("");
                                cell.setCellStyle(stiloLeft);
                            }
                        }
                        fil++;
                        m++;
                        listener.propertyChange(new PropertyChangeEvent(modelo, TAREA_PROGRESO, 0, ct + j));
                    }
                    ct = m;
                    m = 0;
                }
                book.write(out);
                out.close();
                listener.propertyChange(new PropertyChangeEvent(modelos, TAREA_COMPLETADA, ruta, 1));

            }
        } catch (NumberFormatException | IOException e) {

            System.out.println("exception:" + e.getMessage());
            listener.propertyChange(new PropertyChangeEvent(null, TAREA_ERROR, null, e.getMessage()));
        }
    }

    private XSSFCellStyle getStilox(
            XSSFWorkbook book,
            XSSFColor FgColor,
            XSSFColor BdColor,
            XSSFColor FontColor,
            short FontTam,
            VerticalAlignment vAlignment,
            HorizontalAlignment hAlignment,
            Insets bordes) {
        return getStilox(book, FgColor, BdColor, FontColor, FontTam, vAlignment, hAlignment, bordes, (short) 0);
    }

    private XSSFCellStyle getStilox(
            XSSFWorkbook book,
            XSSFColor FgColor,
            XSSFColor BdColor,
            XSSFColor FontColor,
            short FontTam,
            VerticalAlignment vAlignment,
            HorizontalAlignment hAlignment,
            Insets bordes,
            short format) {
        XSSFCellStyle style = book.createCellStyle();

        XSSFFont font = book.createFont();
        font.setFontHeightInPoints(FontTam);
        font.setColor(FontColor);
//        font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
        style.setFont(font);
        if (bordes != null) {
            if (bordes.top > 0) {
                style.setBorderTop(BorderStyle.THIN);
                style.setTopBorderColor(BdColor);
            }
            if (bordes.bottom > 0) {
                style.setBorderBottom(BorderStyle.THIN);
                style.setBottomBorderColor(BdColor);
            }
            if (bordes.left > 0) {
                style.setBorderLeft(BorderStyle.THIN);
                style.setLeftBorderColor(BdColor);
            }
            if (bordes.right > 0) {
                style.setBorderRight(BorderStyle.THIN); 
                style.setRightBorderColor(BdColor);
            }
        }
        
        style.setVerticalAlignment(vAlignment);
        style.setAlignment(hAlignment);
        style.setDataFormat(format);
//        style.setFillBackgroundColor(new XSSFColor(Color.orange).getIndexed());
        style.setFillForegroundColor(FgColor);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

}
