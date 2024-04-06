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
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.xssf.usermodel.XSSFBorderFormatting;
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
                        if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
                            cellValue = cell.getStringCellValue();
                        } else if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
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
            XSSFColor CF_DIAS = new XSSFColor(Color.lightGray);
            XSSFColor CL_DIAS = new XSSFColor(new Color(10, 5, 5));
            XSSFColor CF_HORAS = new XSSFColor(Color.lightGray);
            XSSFColor CL_HORAS = new XSSFColor(new Color(25, 2, 3));
            XSSFColor BLANCO = new XSSFColor(Color.white);
            XSSFColor NEGRO = new XSSFColor(Color.black);
            XSSFColor AZUL = new XSSFColor(new Color(0, 0, 160));
            XSSFColor VERDE = new XSSFColor(new Color(13, 73, 15));
//            XSSFColor CF_CELDA = new XSSFColor(new Color(249, 255, 203));
            XSSFColor CF_CELDA = new XSSFColor(Color.white);
            final short CENTER = XSSFCellStyle.ALIGN_CENTER;
            final short LEFT = XSSFCellStyle.ALIGN_LEFT;
            final short RIGHT = XSSFCellStyle.ALIGN_RIGHT;
            XSSFCellStyle stiloRight = getStilox(book, CF_CELDA, NEGRO, BLANCO, (short) 10, RIGHT, CENTER, all, format1);
            XSSFCellStyle stiloLeft = getStilox(book, CF_CELDA, NEGRO, BLANCO, (short) 10, LEFT, CENTER, all, format1);

            int fil = 0;

            listener.propertyChange(new PropertyChangeEvent(modelo, TAREA_INICIADA, 0, modelo.getRowCount()));
            //Row title
            row = sheet.createRow(fil);
            cell = row.createCell(0);
            cell.setCellValue(title);
            cell.setCellStyle(getStilox(book, BLANCO, VERDE, VERDE, (short) 12, LEFT, CENTER, all));
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
                cell.setCellStyle(getStilox(book, CF_DIAS, NEGRO, CL_DIAS, (short) 11, CENTER, CENTER, all));
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
                            cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                            cell.setCellStyle(stiloRight);
                        } else if (obj instanceof Double || obj instanceof BigDecimal) {                            
                            cell.setCellValue(Double.parseDouble(obj.toString()));
                            cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                            cell.setCellStyle(stiloRight);
                        } else if (obj instanceof Long) {
                            cell.setCellValue(Long.parseLong(obj.toString()));
                            cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                            cell.setCellStyle(stiloRight);
                        } else {
                            cell.setCellType(Cell.CELL_TYPE_STRING);
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
            XSSFColor CF_DIAS = new XSSFColor(Color.lightGray);
            XSSFColor CL_DIAS = new XSSFColor(new Color(10, 5, 5));
            XSSFColor BLANCO = new XSSFColor(Color.white);
            XSSFColor NEGRO = new XSSFColor(Color.black);
            XSSFColor VERDE = new XSSFColor(new Color(13, 73, 15));
            XSSFColor CF_CELDA = new XSSFColor(Color.white);
            final short CENTER = XSSFCellStyle.ALIGN_CENTER;
            final short LEFT = XSSFCellStyle.ALIGN_LEFT;
            final short RIGHT = XSSFCellStyle.ALIGN_RIGHT;
            XSSFCellStyle stiloRight = getStilox(book, CF_CELDA, NEGRO, BLANCO, (short) 10, RIGHT, CENTER, all, format1);
            XSSFCellStyle stiloLeft = getStilox(book, CF_CELDA, NEGRO, BLANCO, (short) 10, LEFT, CENTER, all, format1);

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
                    cell.setCellStyle(getStilox(book, BLANCO, VERDE, VERDE, (short) 12, LEFT, CENTER, all));
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
                        cell.setCellStyle(getStilox(book, CF_DIAS, NEGRO, CL_DIAS, (short) 11, CENTER, CENTER, all));
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
                                    cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                                    cell.setCellStyle(stiloRight);
                                } else if (obj instanceof Double) {                                    
                                    cell.setCellValue(Double.parseDouble(obj.toString()));
                                    cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                                    cell.setCellStyle(stiloRight);
                                } else if (obj instanceof Long) {
                                    cell.setCellValue(Long.parseLong(obj.toString()));
                                    cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                                    cell.setCellStyle(stiloRight);
                                } else {
                                    cell.setCellType(Cell.CELL_TYPE_STRING);
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
            short hAlignment,
            short vAlignment,
            Insets bordes) {
        return getStilox(book, FgColor, BdColor, FontColor, FontTam, hAlignment, vAlignment, bordes, (short) 0);
    }

    private XSSFCellStyle getStilox(
            XSSFWorkbook book,
            XSSFColor FgColor,
            XSSFColor BdColor,
            XSSFColor FontColor,
            short FontTam,
            short hAlignment,
            short vAlignment,
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
                style.setBorderTop(XSSFBorderFormatting.BORDER_THIN);
                style.setTopBorderColor(BdColor);
            }
            if (bordes.bottom > 0) {
                style.setBorderBottom(XSSFBorderFormatting.BORDER_THIN);
                style.setBottomBorderColor(BdColor);
            }
            if (bordes.left > 0) {
                style.setBorderLeft(XSSFBorderFormatting.BORDER_THIN);
                style.setLeftBorderColor(BdColor);
            }
            if (bordes.right > 0) {
                style.setBorderRight(XSSFBorderFormatting.BORDER_THIN);
                style.setRightBorderColor(BdColor);
            }
        }
        style.setVerticalAlignment(vAlignment);
        style.setAlignment(hAlignment);
        style.setDataFormat(format);
//        style.setFillBackgroundColor(new XSSFColor(Color.orange).getIndexed());
        style.setFillForegroundColor(FgColor);
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        return style;
    }

}
