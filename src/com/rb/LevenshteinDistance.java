/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rb;

/**
 *
 * @author ballestax
 */
public class LevenshteinDistance {

    private static int minimum(int a, int b, int c) {
        return Math.min(Math.min(a, b), c);
    }

    public static int computeLevenshteinDistance(String str1, String str2) {
        int[][] distance = new int[str1.length() + 1][str2.length() + 1];
        for (int i = 0; i <= str1.length(); i++) {
            distance[i][0] = i;
        }
        for (int j = 1; j <= str2.length(); j++) {
            distance[0][j] = j;
        }
        for (int i = 1; i <= str1.length(); i++) {
            for (int j = 1; j <= str2.length(); j++) {
                distance[i][j] = minimum(
                        distance[i - 1][j] + 1,
                        distance[i][j - 1] + 1,
                        distance[i - 1][j - 1] + ((str1.charAt(i - 1) == str2.charAt(j - 1)) ? 0 : 1));
            }
        }
        return distance[str1.length()][str2.length()];
    }

    private void compareCase() {

    }

    protected final int[][] vclv() {

        int[][] cd = {{1375, 1320, 792, 803},
        {1419, 1463, 1419, 1496},
        {1474, 1485, 792, 1507},
        {803, 1331, 1485, 1375},
        {847, 1320, 803, 792},
        {1353, 1463, 1331, 803},
        {1353, 803, 1331, 1078}};

        int[] c1 = Utiles.getColumna(cd, 0);
        for (int i = 0; i < c1.length; i++) {
            cd[i][0] = cd[i][3];
            cd[i][3] = c1[i];
        }

        int[][] cdr = org.dz.Utiles.matrizRotada180(cd);
        int[][] esp = new int[cdr.length][cdr[0].length];
        org.dz.Utiles.llenarMatrizRf(esp, 21);
        for (int i = 0; i < cdr.length; i++) {
            for (int j = 0; j < cdr[0].length; j++) {
                cdr[i][j] = (cdr[i][j] / 11) - esp[i][j];
            }
        }

        return cd;
    }
}
