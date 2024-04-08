import java.io.*;
import java.util.*;
import java.util.stream.*;
import java.util.concurrent.CountDownLatch;

/* SYNCHRONIZATION
 * ---------------
 *
 * At the moment, the code below fetches 10 Shakespeare quotes from a website
 * in a loop. Each of the requests is slow, so collecting 10 takes some time.
 *
 * Update the `main` method so that 10 separate threads are used to fetch the quotes.
 *
 * You should also make sure that the main thread accurately tries to get the
 * total length of the quotes.
 *
 * Do not edit the `getQuote` function.
 */

public class Main {
    public static void main(String[] args) throws InterruptedException, IOException {
        final Utility object = new Utility();
        final List<String> quotes = new ArrayList<String>(10);
        for (int i = 0; i < 10; i++) {
            try {
                String quote = object.getQuote();
                quotes.add(quote);
            } catch (InterruptedException | IOException ex) {
                System.out.println(ex);
            }
        }
        int total = quotes.stream().mapToInt(String::length).sum();
        System.out.printf("Total length of quotes: %s characters\n", total);
    }
}

class Utility {
    String getQuote() throws InterruptedException, IOException  {
        final long start = System.currentTimeMillis();
        FakeRequest request = new FakeRequest();
        String quote = request.get("http://shakespeare.com/quote/");
        final long speed = (System.currentTimeMillis() - start) / 1000;
        System.out.printf("%s second(s): %s\n", speed, quote);
        return quote;
    }
}
