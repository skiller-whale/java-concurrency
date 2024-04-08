import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/* Test rig for MyBlockingQueue<T> which runs a test with 250 producers and 250 consumers, each
 * of which performs 250 iterations. The test is run 10 times and the total running time is
 * printed.  The test is run 10 times to get a better estimate of the average running time.
 */
public class Main {
    public static final java.util.Random random = new java.util.Random();
    public static final int PRODUCERS = 250, CONSUMERS = 250, ITERATIONS = 250;
    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            runTest();
        }
    }

    public static void runTest() throws InterruptedException {
        MyBlockingQueue<Integer> queue = new MyBlockingQueue<Integer>();

        final long start = System.currentTimeMillis();
        var startLatch = new CountDownLatch(1);
        var finishLatch = new CountDownLatch(PRODUCERS + CONSUMERS);

        for (int i = 0; i < PRODUCERS; i++) {
            final var digit = i;
            Runnable producer = () -> {
                try {
                    startLatch.await();
                    for (int j = 0; j < ITERATIONS; j++) {
                        queue.push(digit);
                    }
                    finishLatch.countDown();
                } catch (InterruptedException ex) {
                    System.out.println(ex);
                }
            };
            Thread.ofPlatform().start(producer);
        }
        for (int i = 0; i < CONSUMERS; i++) {
            Runnable consumer = () -> {
                try {
                    startLatch.await();
                    for (int j = 0; j < ITERATIONS; j++) {
                        queue.pop();
                    }
                    finishLatch.countDown();
                } catch (InterruptedException ex) {
                    System.out.println(ex);
                }
            };
            Thread.ofPlatform().start(consumer);
        }
        startLatch.countDown(); // start all the threads at once
        if (finishLatch.await(10, java.util.concurrent.TimeUnit.SECONDS)) {
            final long elapsed = System.currentTimeMillis() - start;
            System.out.printf("Total running time for %d producers, %d consumers = %dms\n", PRODUCERS, CONSUMERS, elapsed);
        } else {
            System.out.println("Timeout waiting for threads to finish (deadlock?)");
        }
    }
}
