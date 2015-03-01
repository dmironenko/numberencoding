package com.numberencoding;

import com.util.NumberDecoder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public abstract class NumberEncoder {

    static final String WORD_SEPARATOR = " ";
    static final String TN_WORD_SEPARATOR = ": ";

    final Map<String, List<String>> wordsByTn = new HashMap<>();

    private final ExecutorService executor = Executors.newFixedThreadPool(Math.max(2, Runtime.getRuntime().availableProcessors()));

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
     * Returns for a given phone number all possible encodings by words.
     * This method must be thread safe
     */
    protected abstract List<String> encode(String s);

    /**
     * Encodes list of telephone numbers to list of String in next format "tn: encode(tn)"
     */
    public final List<String> encode(List<String> tns) {
        Objects.requireNonNull(tns, "tns cannot be null");

        List<String> result = new LinkedList<>();

        for (String tn : tns) {
            List<String> encodedWords = encode(normalizeTn(tn));
            result.addAll(toPrintString(encodedWords, tn));
        }
        return result;
    }

    /**
     * Encodes list of telephone numbers concurrently. Works best if you have many tns
     */
    public final List<String> encodeParallel(List<String> tns) throws ExecutionException, InterruptedException {
        Objects.requireNonNull(tns, "tns cannot be null");

        List<Future<List<String>>> futures = new LinkedList<>();
        for (String tn : tns) {
            futures.add(executor.submit(new SingleTnEncodeCallable(tn)));
        }

        List<String> result = new LinkedList<>();

        for (Future<List<String>> future : futures) {
            result.addAll(future.get());
        }

        return result;
    }

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
    private String normalizeTn(String tn) {
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
    private List<String> toPrintString(List<String> encode, String tn) {
        List<String> result = new ArrayList<>(encode.size());

        for (String word : encode) {
            result.add(tn + TN_WORD_SEPARATOR + word);
        }
        return result;
    }

    /**
     * Shutdowns encoder
     */
    public void shutDown() {
        executor.shutdown();
    }

    private class SingleTnEncodeCallable implements Callable<List<String>> {
        private final String tn;

        private SingleTnEncodeCallable(String tn) {
            this.tn = tn;
        }

        @Override
        public List<String> call() {
            List<String> encode = encode(normalizeTn(tn));
            return toPrintString(encode, tn);
        }
    }
}
