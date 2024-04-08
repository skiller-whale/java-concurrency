import java.util.*;
import java.net.URI;
import java.net.http.*;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.concurrent.*;

public class Exercise2 {
    static final Map<String, URI> books = new HashMap<String, URI>() {{
        put("Moby Dick",
            URI.create("https://gutenberg.org/cache/epub/2701/pg2701.txt"));
        put("Whales, Dolphins, and Porpoises of the Western North Atlantic",
            URI.create("https://www.gutenberg.org/cache/epub/33527/pg33527.txt"));
        put("Treasure Island",
            URI.create("https://gutenberg.org/cache/epub/120/pg120.txt"));
    }};
    static final HttpClient http = HttpClient.newBuilder().build();

    // This searches `input` for `word`, returning a List of character offsets in the input where that word occurs.
    public static List<Integer> indexWord(String input, String word) {
        List<Integer> index = new ArrayList<Integer>();
        for (int offset = -word.length(); offset != -1; index.add(offset)) {
            offset = input.indexOf(word, offset+word.length());
        }
        return index;
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException, java.io.IOException {
        final List<String> WORDS = List.of("whale", "dolphin", "shark");

        for (var book : books.entrySet()) {
            String name = book.getKey();
            URI uri = book.getValue();
            HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();

            CompletableFuture<HttpResponse<String>> future = http.sendAsync(request, BodyHandlers.ofString());

            // Create CompletableFutures here using `thenAccept` to print the number of each of the WORDS in each book
        }

        // Make sure the program prints 9 lines of output reliably
    }
}
