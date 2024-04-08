import java.io.*;
import java.util.*;
import java.net.*;
import java.nio.file.*;
import java.util.stream.Collectors;
import java.util.stream.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.*;

/* RACE CONDITIONS
 * ---------------
 *
 * The `addUniqueLine` function writes new lines to a file, but only if they
 * are not already in the file, ensuring that no repeats are written.
 *
 * The `fetchQuote` function uses `addUniqueLine` to create a file which
 * contains all of the unique quotes returned by 'http://shakespeare.com/quote'.
 */

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        var myLatch = new CountDownLatch(300);
        final Utility object = new Utility();
        object.createQuotes();
        Runnable r = () -> {
            try {
                object.fetchQuote();
            } catch (InterruptedException | IOException ex) {
                System.out.println(ex);
            } finally { myLatch.countDown(); }
        };
        for (var i = 0; i < 300; i++) {
            Thread.ofVirtual().start(r);
        }
        myLatch.await();
    }
}

class Utility {
    static final Path quotesPath = Paths.get("quotes.txt");
    static Set<String> currentLines;

    void createQuotes() throws IOException {
        List<String> lines = Collections.emptyList();
        Files.write(quotesPath, lines, StandardCharsets.UTF_8);
    }

    void addUniqueLine(Path file, String line) throws IOException {
        List<String> lines = Files.readAllLines(file);
        if (!lines.contains(line)) {
            lines.add(line);
        }
        Files.write(file, (String.join("\n", lines)+"\n").getBytes(), StandardOpenOption.CREATE);
    }

    void fetchQuote() throws InterruptedException, IOException  {
        FakeRequest request = new FakeRequest();
        String url = "http://shakespeare.com/quote/";
        String quote = request.get(url);
        addUniqueLine(quotesPath, quote);
        System.out.printf("Fetched: %s\n", quote);
    }
}
