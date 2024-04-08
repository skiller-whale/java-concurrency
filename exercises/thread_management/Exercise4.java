import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

public class Exercise4 {
    private static final FakeRequest fakeRequest = new FakeRequest();
    private static final Integer NUM_URLS = 10;
    private static final List<String> TEST_URLS = new ArrayList<>();
    static {
        for (int i = 1; i <= NUM_URLS; i++) {
            TEST_URLS.add("http://www.speed-testing-website.com/test-" + i);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        List<UrlDuration> urlDurations = new ArrayList<>();

        for (String url : TEST_URLS) {
            urlDurations.add(timeRequest(url));
            System.out.print(".");
        }

        // sort urlDurations by duration
        Collections.sort(urlDurations, Comparator.comparing(UrlDuration::duration));

        System.out.println();
        for (int i = 0; i < NUM_URLS; i++) {
            System.out.printf("Position %s: %s (took %ss)\n",
                              i + 1,
                              urlDurations.get(i).url(),
                              urlDurations.get(i).duration());
        }
    }

    private static UrlDuration timeRequest(String url) throws InterruptedException {
        long start = System.currentTimeMillis();
        fakeRequest.get(url);
        return new UrlDuration(url, (System.currentTimeMillis() - start) / 1000.0);
    }

    private record UrlDuration(String url, Double duration) {}
}
