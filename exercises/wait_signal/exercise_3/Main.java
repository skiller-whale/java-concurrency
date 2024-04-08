import java.util.*;
import java.util.concurrent.CountDownLatch;

/* Test rig for MyBlockingQueue<T>.
 * This runs a test with 500 producers and 500 consumers, each of which performs 500 iterations.
 * The test is run 10 times and the total running time for each thread is printed.
 * The test is run 10 times to get a better estimate of the average running time.
 */
public class Main {
    public static final int PRODUCERS = 500, CONSUMERS = 500, ITERATIONS = 5000;
    public static long totalRuntime   = 0;
    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            runTest();
        }
        System.out.printf("Average running time for %d producers, %d consumers = %dms\n", PRODUCERS, CONSUMERS, totalRuntime / 10);
    }

    public static void runTest() throws InterruptedException {
        MyBlockingQueue<Integer> queue  = new MyBlockingQueue<Integer>();
        final long start                = System.currentTimeMillis();
        var myLatch                     = new CountDownLatch(PRODUCERS + CONSUMERS);

        for (int i = 0; i < PRODUCERS; i++) {
            Runnable producer = () -> {
                try {
                    for (int j = 0; j < PRODUCERS; j++) {
                        queue.push(j * PRODUCERS);
                    }
                    myLatch.countDown();
                } catch (InterruptedException ex) {
                    System.out.println(ex);
                }
            };
            Thread.ofPlatform().start(producer);
        }
        for (int i = 0; i < CONSUMERS; i++) {
            Runnable consumer = () -> {
                try {
                    for (int j = 0; j < CONSUMERS; j++) {
                        queue.pop();
                    }
                    myLatch.countDown();
                } catch (InterruptedException ex) {
                    System.out.println(ex);
                }
            };
            Thread.ofPlatform().start(consumer);
        }
        myLatch.await();
        final long elapsed = System.currentTimeMillis() - start;
        totalRuntime += elapsed;
        System.out.printf("Total running time for %d producers, %d consumers = %dms\n", PRODUCERS, CONSUMERS, elapsed);
    }
}
