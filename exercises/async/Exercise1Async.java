import java.util.*;
import java.net.URI;
import java.net.http.*;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class Exercise1Async {

    // You'll need this
    static final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    // "Whales, Dolphins, and Porpoises of the Western North Atlantic", Caldwell et. al.
    static final URI     bookUri = URI.create("https://www.gutenberg.org/cache/epub/33527/pg33527.txt");
    static final HttpClient http = HttpClient.newBuilder().build();

    public static Future<List<Integer>> indexWhalesAsync(Future<String> input) {
        // TODO: Convert the synchronous indexWhalesAsync to use Futures for its arguments so that nothing blocks.
        // The method signature above is correct.
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException, java.io.IOException {
        HttpRequest request = HttpRequest.newBuilder().uri(bookUri).GET().build();

        // HttpClient is one API in core Java that has an async version
        Future<HttpResponse<String>> responseFuture = http.sendAsync(request, BodyHandlers.ofString());
        Future<String> bodyFuture = // TODO: get the body from the responseFuture
        Future<List<Integer>> whaleIndexFuture = indexWhalesAsync(bodyFuture);

        // And look - here we're free! All the work is scheduled, we could do something else before
        // printing the result. But TODO: print the result.
    }
}
