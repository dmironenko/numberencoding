package com.numberencoding;

import com.util.NumberDecoder;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class NumberEncoder {

    static final String WORD_SEPARATOR = " ";
    static final String TN_WORD_SEPARATOR = ": ";

    final Map<String, List<String>> wordsByTn = new HashMap<>();

    /**
     * Constructor processes dictionary and stores it map next way:
     * - Key for each entry is normalized word decoded into telephone number
     * - Value is list of words corresponding it's key (due to different words can be decoded to same key)
     */
    NumberEncoder(List<String> dictionary) {
        Objects.requireNonNull(dictionary, "dictionary cannot be null");

        for (String word : dictionary) {
            String decodedWord = NumberDecoder.decode(normalizeWord(word));

            List<String> words = wordsByTn.get(decodedWord);
            if (words == null) {
                words = new LinkedList<>();
            }
            words.add(word);

            wordsByTn.put(decodedWord, words);
        }
    }

    /**
     * Encodes list of telephone numbers to list of String in next format "tn: encode(tn)"
     * This method must be thread safe
     */
    public abstract List<String> encode(String s);

    /**
     * Verifies condition:
     * In a partial encoding that currently covers k digits, digit k+1 is encoded by itself if and only if,
     * first, digit k was not encoded by a digit and, second, there is no word in the dictionary
     * that can be used in the encoding starting at digit k+1
     *
     * @param startIndex from which index of tn to verify
     * @param tn         telephone number
     * @return true if it is allowed to insert digit, false otherwise
     */
    boolean isDigitInsertAllowed(int startIndex, String tn) {
        for (int i = startIndex + 1; i < tn.length(); i++) {
            if (wordsByTn.containsKey(tn.substring(startIndex, i)))
                return false;
        }
        return true;
    }

    /**
     * Normalized word - removes umlaut characters
     *
     * @param word word to normalize
     * @return normalized word
     */
    private String normalizeWord(String word) {
        return word.replaceAll("\"", "");
    }

    /**
     * Normalized telephone number - removes dashes and slashes
     *
     * @param tn tn to normalize
     * @return normalized tn
     */
    protected String normalizeTn(String tn) {
        StringBuilder sb = new StringBuilder();

        for (char c : tn.toCharArray()) {
            if (Character.isDigit(c)) {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    /**
     * Prints the phone number followed by a colon, a single(!) space,
     * and the encoding on one line; trailing spaces are not allowed.
     */
    protected String toPrintString(String word, String tn) {
        return tn + TN_WORD_SEPARATOR + word;
    }
}
