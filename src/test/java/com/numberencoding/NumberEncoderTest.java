package com.numberencoding;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class NumberEncoderTest {
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

    @Test
    public void sunnyDay() throws IOException {
        NumberEncoder encoder = getNumberEncoder("dictionary.txt");

        List<String> actual = encoder.encode(TNS);
        assertThat(actual).hasSameSizeAs(EXPECTED).containsOnlyElementsOf(EXPECTED);
    }

    // Just to verify that encoding of 50 digit number with big dictionary takes not bigger than 1 second
    @Test(timeout = 1000)
    public void manyDigits() throws IOException {
        NumberEncoder encoder = getNumberEncoder("big_dictionary.txt");
        List<String> actual = encoder.encode(Arrays.asList("04824048240482404824048240482404824048240482404824"));
        assertTrue(actual.size() > 50000);
    }

    private NumberEncoder getNumberEncoder(String dictionaryName) throws IOException {
        URL url = getClass().getClassLoader().getResource(dictionaryName);
        assertNotNull(url);
        Path dictPath = Paths.get(new File(url.getFile()).getPath());
        return new RecursiveEncoder(Files.readAllLines(dictPath, Charset.defaultCharset()));
    }

}


