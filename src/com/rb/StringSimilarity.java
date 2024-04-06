/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rb;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * http://www.catalysoft.com/articles/StrikeAMatch.html
 * http://stackoverflow.com/questions/653157/a-better-similarity-ranking-algorithm-for-variable-length-strings
 * @author modificado para java por ballestax
 */
public class StringSimilarity {

    /**
     * Compares the two strings based on letter pair matches
     *
     * @param str1
     * @param str2
     * @return percentage match from 0.0 to 1.0 where 1.0 is 100%
     */
    public static double compareStrings(String str1, String str2) {
        ArrayList<String> pairs1 = wordLetterPairs(str1.toUpperCase());
        ArrayList<String> pairs2 = wordLetterPairs(str2.toUpperCase());

        int intersection = 0;
        int union = pairs1.size() + pairs2.size();

        for (int i = 0; i < pairs1.size(); i++) {
            for (int j = 0; j < pairs2.size(); j++) {
                if (pairs1.get(i).equals(pairs2.get(j))) {
                    intersection++;
                    //Must remove the match to prevent "GGGG" from appearing to match "GG" with 100% success
                    pairs2.remove(j);
                    break;
                }
            }
        }
        return (2.0 * intersection) / union;
    }

    /**
     * Gets all letter pairs for each individual word in the string
     *
     * @param str
     * @return
     */
    private static ArrayList<String> wordLetterPairs(String str) {
        ArrayList<String> allPairs = new ArrayList<>();

        // Tokenize the string and put the tokens/words into an array
        String[] words = str.split("\\s");
        for (String word : words) {
            if (!(word == null || word.isEmpty())) {
                // Find the pairs of characters
                String[] pairsInWord = letterPairs(word);
                allPairs.addAll(Arrays.asList(pairsInWord));
            }
        }
        return allPairs;
    }

    /**
     * Generates an array containing every two consecutive letters in the input
     * string
     *
     * @param str
     * @return
     */
    private static String[] letterPairs(String str) {
        int numPairs = str.length() - 1;
        String[] pairs = new String[numPairs];
        for (int i = 0; i < numPairs; i++) {
            pairs[i] = str.substring(i, i+2);
        }
        return pairs;
    }

}
