package com.numberencoding;

import com.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Recursive implementation of encoder. Doesn't require long stack and work pretty fast for 50 digit tn
 */
public class RecursiveEncoder extends NumberEncoder {

    public RecursiveEncoder(List<String> dictionary) {
        super(dictionary);
    }

    @Override
    public List<String> encode(String tn) {
        // Just return empty list if tn is invalid
        String normalizedTn = normalizeTn(tn);
        if (StringUtils.isEmpty(normalizedTn)) {
            return Collections.emptyList();
        }

        return encode(tn, normalizedTn, 0, false, new ArrayList<String>(), new LinkedList<String>());
    }

    /**
     * Recursively searches for all possible word + digit combinations
     * Algorithm :
     * For each substring of tn starting from first digit to whole tn
     * 1. search for its encoding in dictionary :
     * 1.1 if found add to currentEncode and recursively search for encoding for the rest of tn
     * 1.2 if not check if we can add it as digit if yes - add it to currentEncode, recursively search for encoding for the rest of tn
     * <p/>
     * 2. Encoding is found only in case we were able do encode whole tn
     */
    private List<String> encode(String tn, String normalizedTn, int startIndex, boolean lastWasDigit, List<String> currentEncode, List<String> result) {
        // Whole phone number string encoded -> store result
        if (startIndex == normalizedTn.length()) {
            String words = StringUtils.join(currentEncode, WORD_SEPARATOR);
            result.add(toPrintString(words, tn));
            return result;
        }

        for (int i = startIndex + 1; i <= normalizedTn.length(); i++) {
            String subTn = normalizedTn.substring(startIndex, i);

            List<String> words = wordsByTn.get(subTn);
            if (words != null) {
                for (String word : words) {
                    // Proceed with copy (not currentEncode) so we don't break it if further encoding will not be found with this word
                    List<String> copy = new ArrayList<>(currentEncode);
                    copy.add(word);

                    encode(tn, normalizedTn, i, false, copy, result);
                }
            } else {
                // Word was not found - try to insert digit
                if (!lastWasDigit && subTn.length() == 1 && isDigitInsertAllowed(startIndex, normalizedTn)) {
                    String digit = normalizedTn.substring(startIndex, startIndex + 1);

                    List<String> copy = new ArrayList<>(currentEncode);
                    copy.add(digit);

                    encode(tn, normalizedTn, startIndex + 1, true, copy, result);
                }
            }
        }
        return result;
    }
}
