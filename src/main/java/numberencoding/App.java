package numberencoding;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

public class App {

    public static void main(String[] arg) throws IOException {
        NumberEncoder encoder = getNumberEncoder("big_dictionary.txt");
        List<String> encodeWords = encoder.encode("04824048240482404824048240482404824048240482404824");

        for (String encodedWord : encodeWords) {
            System.out.println(encodedWord);
        }
    }

    private static NumberEncoder getNumberEncoder(String dictionaryName) throws IOException {
        URL url = Objects.requireNonNull(App.class.getClassLoader().getResource(dictionaryName));
        Path dictPath = Paths.get(new File(url.getFile()).getPath());
        return new RecursiveEncoder(Files.readAllLines(dictPath, Charset.defaultCharset()));
    }
}
