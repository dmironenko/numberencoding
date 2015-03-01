package com;

import com.numberencoding.NumberEncoder;
import com.numberencoding.RecursiveEncoder;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

public class App {

    public static void main(String[] arg) throws Exception {
        NumberEncoder encoder = getNumberEncoder("dictionary.txt");

        List<String> tns;

        if (arg == null || arg.length == 0) {
            tns = Files.readAllLines(getPath("tns.txt"), Charset.defaultCharset());
        } else {
            // Assuming args[0] contains path to file with tns
            tns = Files.readAllLines(Paths.get(arg[0]), Charset.defaultCharset());
        }

        List<String> encodeWords;

        if (tns.size() == 1) {
            encodeWords = encoder.encode(tns);
        } else {
            encodeWords = encoder.encodeParallel(tns);
        }

        for (String encodedWord : encodeWords) {
            System.out.println(encodedWord);
        }

        encoder.shutDown();
    }

    private static NumberEncoder getNumberEncoder(String dictionaryName) throws IOException, URISyntaxException {
        Path dictPath = getPath(dictionaryName);
        return new RecursiveEncoder(Files.readAllLines(dictPath, Charset.defaultCharset()));
    }

    private static Path getPath(String dictionaryName) throws URISyntaxException {
        URL url = Objects.requireNonNull(App.class.getClassLoader().getResource(dictionaryName));
        return Paths.get(url.toURI());
    }
}
