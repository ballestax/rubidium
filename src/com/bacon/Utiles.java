/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bacon;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;
import org.apache.xmlbeans.impl.common.Levenshtein;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

/**
 *
 * @author ballestax
 */
public class Utiles {

    public static int[] removeDuplicados(int[] dat) {
        List<Integer> tmp = new ArrayList();
        for (int i = 0; i < dat.length; i++) {
            if (!tmp.contains(dat[i])) {
                tmp.add(dat[i]);
            }
        }
        int[] sal = new int[tmp.size()];
        for (int i = 0; i < tmp.size(); i++) {
            sal[i] = tmp.get(i);
        }
        return sal;
    }

    public static int[] truncar(int[] dat, int min, int max) {
        List<Integer> tmp = new ArrayList();
        for (int i = 0; i < dat.length; i++) {
            if (dat[i] >= min && dat[i] <= max) {
                tmp.add(dat[i]);
            }
        }
        int[] sal = new int[tmp.size()];
        for (int i = 0; i < tmp.size(); i++) {
            sal[i] = tmp.get(i);
        }
        return sal;
    }

    public static int[] removeIndex(int[] array, int index) {
        if (index >= 0 && index < array.length) {
            int[] array1 = Arrays.copyOf(array, index);
            int[] array2 = Arrays.copyOfRange(array, index + 1, array.length);

            int l1 = array1.length;
            int l2 = array2.length;
            array = Arrays.copyOf(array1, l1 + l2);
            for (int i = l1; i < array.length; i++) {
                array[i] = array2[i - l1];
            }
//            return array;
        }
        return array;
    }

    public static String recortarString(String cad, int w, Graphics g, Font f) {
        FontMetrics fontMet = g.getFontMetrics(f);
        Rectangle2D b;
        b = fontMet.getStringBounds(cad, g);
        while (b.getWidth() > w) {
            cad = cad.substring(0, cad.length() - 1);
            b = fontMet.getStringBounds(cad, g);
        }
        return cad;
    }

    public static String imprimirMatriz(Object[][] matriz) {
        if (matriz == null && matriz.length < 1 && matriz[0].length < 1) {
            return null;
        }
        StringBuilder buf = new StringBuilder("<---Matriz:" + matriz + "--->\n");
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz[0].length; j++) {

                buf.append(matriz[i][j]);
                buf.append(" ");
            }
            buf.append("\n");
        }
        return buf.toString();
    }

    public static String imprimirMatrizAlineada(Object[][] matriz) {
        if (matriz == null && matriz.length < 1 && matriz[0].length < 1) {
            return null;
        }
        int[] max = new int[matriz[0].length];
        int[] col = new int[matriz.length];
        for (int i = 0; i < matriz[0].length; i++) {
            Object[] columna = getColumna(matriz, i);
            for (int j = 0; j < col.length; j++) {
                col[j] = (columna[j] != null) ? columna[j].toString().length() : 4;
            }
            max[i] = org.balx.Utiles.maximo(col);
        }

        StringBuilder buf = new StringBuilder("<---Matriz:" + matriz + "--->\n");
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz[0].length; j++) {

                buf.append(matriz[i][j]);
                int l = (matriz[i][j] != null) ? matriz[i][j].toString().length() : 4;
                for (int k = 0; k < (max[j] + 2) - l; k++) {
                    buf.append(" ");
                }
            }
            buf.append("\n");
        }
        return buf.toString();
    }

    public static Object[] getColumna(Object[][] matriz, int columna) {
        if (matriz == null && columna < 0 && matriz.length > columna) {
            return null;
        }
        Object[] objects = new Object[matriz.length];
        for (int i = 0; i < matriz.length; i++) {
            objects[i] = matriz[i][columna];
        }
        return objects;
    }

    public static int[] getColumna(int[][] matriz, int columna) {
        if (matriz == null && columna < 0 && matriz.length > columna) {
            return null;
        }
        int[] objects = new int[matriz.length];
        for (int i = 0; i < matriz.length; i++) {
            objects[i] = matriz[i][columna];
        }
        return objects;
    }

    public static int[] objToArrayInt(Object[] dat) {
        int[] sal = new int[dat.length];
        for (int i = 0; i < dat.length; i++) {
            sal[i] = (Integer) dat[i];
        }
        return sal;
    }

    public static String[] separarNombre(String nombre) {
        String[] split = nombre.split(" ");
        String nuevoNombre = "";
        for (int i = 0; i < split.length; i++) {
            String string = split[i];
            switch (string.toLowerCase()) {
                case "de":
                case "del":
                case "la":
                case "las":
                case "los":
                case "san":
                    nuevoNombre += string + " ";
                    break;
                default:
                    nuevoNombre += string + "@";
            }
        }

        if (nuevoNombre.charAt(nuevoNombre.length() - 1) == '@') {
            nuevoNombre = nuevoNombre.substring(0, nuevoNombre.length() - 1);
        }
        System.out.println(nuevoNombre);
        return nuevoNombre.split("@");
    }

    public static byte[] bufferedImageToArrayBytes(BufferedImage image) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", baos);
            baos.flush();
            byte[] imagenByte = baos.toByteArray();
            baos.close();
            return imagenByte;
        } catch (IOException ex) {
            Logger.getLogger(Utiles.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     * Verifica que una fecha en formato dd-mm-aaaa sea valida
     *
     * @param formDate
     */
    public static boolean verifyDate(String formDate) {
        int dia = 0, mes = 0, año = 0;
        String[] split = formDate.split("-");
        if (split.length < 3) {
            return false;
        } else {
            try {
                dia = Integer.parseInt(split[0]);
                mes = Integer.parseInt(split[1]);
                año = Integer.parseInt(split[2]);
//                System.err.println(dia+"-"+mes+"-"+año);
            } catch (NumberFormatException e) {
                return false;
            }
            if ((dia < 1 || dia > 31) || (mes < 1 || mes > 12) || (año < 1 || año > 3000)) {
                return false;
            } else {
                if (mes == 1 || mes == 3 || mes == 5 || mes == 7 || mes == 8 || mes == 10 || mes == 12) {
                    return dia <= 31;
                } else if (mes == 2) {
                    if (isAñoBisiesto(año)) {  // febrero
                        return dia <= 29;
                    } else {
                        return dia <= 28;
                    }
                } else {  // Meses de 30 dias
                    return dia <= 30;
                }
            }

        }
    }

    private static boolean isAñoBisiesto(int año) {
        return (año % 4 == 0) && (año % 100 != 0) || (año % 400 == 0);
    }

    public static String calcularPalabraMasCercana(String string1, String[] lista) {
        int distanciaMinima = -1;
        int lev = Integer.MAX_VALUE;
        String cercana = "";
        for (String string : lista) {

            // calcula la distancia entre la palabra de entrada y la palabra actual
            lev = Levenshtein.distance(string1, string);
            //System.err.println(string + ": " + lev);
            // verifica por una coincidencia exacta
            if (lev == 0) {
                // la palabra más cercana es esta (coincidencia exacta)
                cercana = string;
                distanciaMinima = 0;
                return cercana;
            }
            // si esta distancia es menor que la siguiente distancia
            // más corta o si una siguiente palabra más corta aun no se ha encontrado
            if (lev <= distanciaMinima || distanciaMinima < 0) {
                cercana = string;
                distanciaMinima = lev;
            }
        }
        return cercana;
    }

    public static String[] calcularPalabraSimilar(String string1, String[] lista) {
        double math = -1.0;
        double sim = 0.0;
        String cercana = "";
//        System.err.println(Arrays.toString(lista));
        for (String string : lista) {
            // calcula la similaridad entre la palabra de entrada y la palabra actual
            sim = StringSimilarity.compareStrings(string1, string);
//            System.err.println(string + ": " + sim);
            // verifica por una coincidencia exacta
            if (sim == 1.0) {
                // la palabra más cercana es esta (coincidencia exacta)
                cercana = string;
                math = 1.0;
                return new String[]{cercana, math + ""};
            }
            // si esta distancia es menor que la siguiente distancia
            // más corta o si una siguiente palabra más corta aun no se ha encontrado
            if (sim >= math || math < 0.0) {
                cercana = string;
//                System.err.println("cercana:" + cercana + " ->" + math + "=" + sim);
                math = sim;
            }
        }
//        System.err.println(Arrays.toString(lista));
        //System.err.println("sel[" + string1 + "]:" + cercana + "::" + math);
        return new String[]{cercana, math + ""};
    }

    public static double similarity(String s1, String s2) {
        String longer = s1, shorter = s2;
        if (s1.length() < s2.length()) { // longer should always have greater length
            longer = s2;
            shorter = s1;
        }
        int longerLength = longer.length();
        if (longerLength == 0) {
            return 1.0; // both strings are zero length 
        }
        return (longerLength - Levenshtein.distance(longer, shorter)) / (double) longerLength;
    }

    public static void chooseBestOption(int[] distance, double[] similarity) {
        for (int i = 0; i < similarity.length; i++) {
            double s = similarity[i];
            int d = distance[i];
        }
    }

    public static StringBuilder getStringBFromFile(String file) throws IOException {
        ArrayList<String> lines = Utiles.splitArchivoEnLineas(file, true);
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < lines.size(); i++) {
            str.append(lines.get(i));
        }
        return str;
    }

    public static String getNumeroFormateado(int num, int pos) {
        int l = org.dzur.Mat.getCifras(num).length;
        if (l >= pos) {
            return String.valueOf(num);
        } else {
            String form = "";
            for (int i = 0; i < pos - l; i++) {
                form += "0";
            }
            return form + num;
        }
    }

    public static boolean crearDirectorio(Path path) {

        boolean creado = false;
        if (!Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
            try {
                Files.createDirectory(path);
                path = path.toAbsolutePath();                
                System.out.println("\n" + path + " directorio creado.");
                return true;
            } catch (NoSuchFileException e) {
                creado = false;
                System.err.println("\nDirectory creation failed:\n" + e);
            } catch (FileAlreadyExistsException e) {
                creado = false;
                System.err.println("\nDirectory creation failed:\n" + e);
            } catch (IOException e) {
                creado = false;
                System.err.println("\nDirectory creation failed:\n" + e);
            }
        }
        return creado;

    }

    public static String toHex(Color color) {
        return "#" + toBrowserHexValue(color.getRed()) + toBrowserHexValue(color.getGreen()) + toBrowserHexValue(color.getBlue());
    }

    public static String toHex(int r, int g, int b) {
        return "#" + toBrowserHexValue(r) + toBrowserHexValue(g) + toBrowserHexValue(b);
    }

    private static String toBrowserHexValue(int number) {
        StringBuilder builder = new StringBuilder(Integer.toHexString(number & 0xff));
        while (builder.length() < 2) {
            builder.append("0");
        }
        return builder.toString().toUpperCase();
    }

    public static boolean saveImage(BufferedImage image, String format, File file) {
        boolean write = false;
        try {
            write = ImageIO.write(image, format, file);
        } catch (IOException ex) {
            Logger.getLogger(Utiles.class.getName()).log(Level.SEVERE, null, ex);
        }
        return write;
    }

    public static void encProperty(String property, char[] pass) {
        StandardPBEStringEncryptor enc = new StandardPBEStringEncryptor();
        enc.setPassword(String.valueOf(pass));
        String tenc = enc.encrypt(property);
        System.out.println(tenc);
    }

    public static void encrypt(String key, File inputFile, File outputFile) throws CryptoException {
        dcrypt(Cipher.ENCRYPT_MODE, key, inputFile, outputFile);
    }

    public static void decrypt(String key, File inputFile, File outputFile) throws CryptoException {
        dcrypt(Cipher.DECRYPT_MODE, key, inputFile, outputFile);
    }

    private static final void dcrypt(int cipherMode, String key, File inputFile, File outputFile) throws CryptoException {
        final String ALGORITHM = "AES";
        final String TRANSFORMATION = "AES";
        try {
            Key secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(cipherMode, secretKey);

            FileInputStream inputStream = new FileInputStream(inputFile);
            byte[] inputBytes = new byte[(int) inputFile.length()];
            inputStream.read(inputBytes);

            byte[] outputBytes = cipher.doFinal(inputBytes);

            FileOutputStream outputStream = new FileOutputStream(outputFile);
            outputStream.write(outputBytes);

            inputStream.close();
            outputStream.close();

        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | IOException ex) {
            throw new CryptoException("Error encrypting/decrypting file", ex);
        }
    }

    public static class CryptoException extends Exception {

        public CryptoException() {
        }

        public CryptoException(String message, Throwable throwable) {
            super(message, throwable);
        }
    }

    public static ArrayList splitArchivoEnLineas(File archivo, boolean skipLineasVacias)
            throws FileNotFoundException, IOException {
        ArrayList lineas = new ArrayList();
        FileInputStream fis = new FileInputStream(archivo);
        InputStreamReader fr = new InputStreamReader(fis, "UTF-8");
        BufferedReader bfReader = new BufferedReader(fr);
        String linea = null;
        do {
            linea = bfReader.readLine();
            if (!skipLineasVacias) {
                lineas.add(linea);
            } else if (linea != null && !linea.isEmpty()) {
                lineas.add(linea);
            }
        } while (linea != null);
        return lineas;
    }

    public static ArrayList splitArchivoEnLineas(String archivo, boolean skipLineasVacias)
            throws FileNotFoundException, IOException {
        return splitArchivoEnLineas(new File(archivo), skipLineasVacias);
    }

    public synchronized static String decryptCaptcha(String command, String img, String output, String parameters) throws IOException, InterruptedException {

//            StringBuffer sb = new StringBuffer();
//            Process p = Runtime.getRuntime().exec("C://Program Files/Tesseract-OCR/tesseract imag1.jpg D:\\list");
        Process p = Runtime.getRuntime().exec(command + " " + img + " " + output + " " + parameters);
        p.waitFor();    
        ArrayList<String> leerTexto = org.balx.Utiles.leerTexto(output + ".txt");
        if (leerTexto != null && !leerTexto.isEmpty()) {
            return leerTexto.get(0);
        } else {
            return "123asd";
        }

    }
    
    public static int sumarArray(int[] dat) {
        int suma = 0;
        for (int i = 0; i < dat.length; i++) {
            suma += dat[i];
        }
        return suma;
    }

    public static float sumarArray(float[] dat) {
        float suma = 0f;
        for (int i = 0; i < dat.length; i++) {
            suma += dat[i];
        }
        return suma;
    }

    public static double sumarArray(double[] dat) {
        double suma = 0.0;
        for (int i = 0; i < dat.length; i++) {
            suma += dat[i];
        }
        return suma;
    }

}
