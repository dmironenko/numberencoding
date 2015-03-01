package numberencoding;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class App {

    public static void main(String[] arg) throws IOException {
        NumberEncoder encoder = getNumberEncoder("big_dictionary.txt");

        List<String> tns;

        if (arg == null || arg.length == 0) {
            tns = Arrays.asList("04824048240482404824048240482404824048240482404824",
                    "112",
                    "5624-82",
                    "4824",
                    "0721/608-4067",
                    "10/783--5",
                    "1078-913-5",
                    "381482",
                    "04824");
        } else {
            // Assuming args are tns
            tns = Arrays.asList(arg);
        }

        List<String> encodeWords = encoder.encode(tns);

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
