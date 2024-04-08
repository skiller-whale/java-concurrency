import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

public class Exercise5 {
    public static void main(String[] args) throws InterruptedException {
        CompletableFuture<List<String>> origin = CompletableFuture.supplyAsync(() -> new ArrayList<String>());

        for (int i=0; i<10000; i++) {
            final int i2 = i;

            // These two clauses...

            origin.thenAcceptAsync(list -> {
                list.add("Incoming data "+i2);
            });

            // ...could be a long way apart in the code.

            origin.thenApplyAsync(list -> {
                int total=0;
                for (String s : list) { total += s.length(); }
                return total;
            }).thenAcceptAsync(total -> {
                updateUiWithLength(total);
            });
        }

        // (waits for all the background threads to finish - you'll learn about ForkJoinPool in the next slide)
        ForkJoinPool.commonPool().awaitQuiescence(10, java.util.concurrent.TimeUnit.SECONDS);

        // Great! Now `origin.join()` has a list of 10000 items...
        System.out.println(origin.join().size()+" items"); // ...oh no.

        // How can we make sure that we spot exceptions when they happen?
    }

    public static void updateUiWithLength(int length) {
        // ...imagine updating a GUI here...
    }
}
