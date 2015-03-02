package com.numberencoding;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class NumberEncoderTest {

    private static final String SMALL_DICTIONARY_TXT = "small_dictionary.txt";
    private static final String BIG_DICTIONARY_TXT = "dictionary.txt";

    private static final List<String> TNS = Arrays.asList(
            "112",
            "5624-82",
            "4824",
            "0721/608-4067",
            "10/783--5",
            "1078-913-5",
            "381482",
            "04824"
    );

    private static final List<String> EXPECTED = Arrays.asList(
            "5624-82: mir Tor",
            "5624-82: Mix Tor",
            "4824: Torf",
            "4824: fort",
            "4824: Tor 4",
            "10/783--5: neu o\"d 5",
            "10/783--5: je bo\"s 5",
            "10/783--5: je Bo\" da",
            "381482: so 1 Tor",
            "04824: 0 Torf",
            "04824: 0 fort",
            "04824: 0 Tor 4");

    private static NumberEncoder smallDictionaryEncoder;
    private static NumberEncoder bigDictionaryEncoder;

    @BeforeClass
    public static void setUp() throws Exception {
        smallDictionaryEncoder = getNumberEncoder(SMALL_DICTIONARY_TXT);
        bigDictionaryEncoder = getNumberEncoder(BIG_DICTIONARY_TXT);
    }

    @Test
    public void sunnyDay() throws IOException {
        List<String> actual = new LinkedList<>();
        for (String tn : TNS) {
            actual.addAll(smallDictionaryEncoder.encode(tn));
        }
        assertThat(actual).hasSameSizeAs(EXPECTED).containsOnlyElementsOf(EXPECTED);
    }

    @Test
    public void encodedNotTnShouldReturnEmptyList() {
        List<String> actual = smallDictionaryEncoder.encode("Hello");
        assertThat(actual).isEmpty();
    }

    // Just to verify that encoding of 50 digit number with big dictionary takes less than 1 second
    @Test(timeout = 1000)
    public void manyDigits() throws IOException {
        List<String> actual = bigDictionaryEncoder.encode("04824048240482404824048240482404824048240482404824");
        assertTrue(actual.size() > 50000);
    }

    private static NumberEncoder getNumberEncoder(String dictionaryName) throws IOException, URISyntaxException {
        URL url = NumberEncoderTest.class.getClassLoader().getResource(dictionaryName);
        assertNotNull(url);
        Path dictPath = Paths.get(url.toURI());
        return new RecursiveEncoder(Files.readAllLines(dictPath, Charset.defaultCharset()));
    }
}


