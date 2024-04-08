import java.util.*;
import java.net.URI;
import java.net.http.*;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.concurrent.*;

public class Exercise1 {
    // "Whales, Dolphins, and Porpoises of the Western North Atlantic", Caldwell et. al.
    static final URI                      bookUri = URI.create("https://www.gutenberg.org/cache/epub/33527/pg33527.txt");
    static final HttpClient                  http = HttpClient.newBuilder().build();

    public static List<Integer> indexWhales(String input) {
        String word = "whale";
        int offset = -word.length();
        List<Integer> index = new ArrayList<Integer>();
        while (offset != -1) {
            offset = input.indexOf(word, offset+word.length());
            index.add(offset);
            // offset always points at the last-found occurence, hence the +word.length()
        }
        return index;
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException, java.io.IOException {
        HttpRequest         request = HttpRequest.newBuilder().uri(bookUri).GET().build();
        HttpResponse<String> result = http.send(request, BodyHandlers.ofString());
        List<Integer>    whaleIndex = indexWhales(result.body());
        System.out.println(whaleIndex.size());
    }
}
