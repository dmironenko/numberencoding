package com;

import com.numberencoding.NumberEncoder;
import com.numberencoding.RecursiveEncoder;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class App {

    /**
     * Main method
     * arg[0] - full path to file with dictionary.
     * arg[1] - full path to file with tns.
     * Example of usage :
     * java -jar numberencoding-1.0.jar /srv/dictionary.txt /srv/tns.txt
     */
    public static void main(String[] arg) throws Exception {
        if (arg.length != 2) {
            System.out.println("Please specify valid arguments: ");
            System.out.println("arg[0] - full path to file with dictionary");
            System.out.println("arg[1] - full path to file with tns");
            return;
        }

        Path dictionary = requireExistingPath(arg[0]);
        Path tn = requireExistingPath(arg[1]);

        // Executor with at least two threads
        ExecutorService executor = Executors.newFixedThreadPool(Math.max(2, Runtime.getRuntime().availableProcessors()));

        final NumberEncoder encoder = getNumberEncoder(dictionary);

        // Due to "number of entries in the phone number file: unlimited" read only line by line and now whole file to memory
        try (BufferedReader reader = Files.newBufferedReader(tn, Charset.defaultCharset())) {
            for (; ; ) {
                final String line = reader.readLine();
                if (line == null)
                    break;

                executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        for (String word : encoder.encode(line)) {
                            System.out.println(word);
                        }
                    }
                });
            }
        }

        executor.shutdown();
        // Wait forever till all encoding tasks are done or till it's interrupted
        while (!executor.isTerminated()) {
            executor.awaitTermination(1, TimeUnit.MINUTES);
        }
    }

    /**
     * Returns number encoder by dictionary path
     */
    private static NumberEncoder getNumberEncoder(Path dictPath) throws IOException {
        return new RecursiveEncoder(Files.readAllLines(dictPath, Charset.defaultCharset()));
    }

    /**
     * Verifies that path exists
     */
    private static Path requireExistingPath(String p) throws FileNotFoundException {
        Path path = Paths.get(p);
        if (!Files.exists(path)) {
            throw new FileNotFoundException(path + " file doesn't exists");
        }

        return path;
    }
}
