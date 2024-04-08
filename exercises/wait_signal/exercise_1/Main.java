import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/* Test rig for MyCountDownLatch.
 * This runs a test with 2000 threads, each of which just counts up to 500.
 * The test is run 10 times and the total running time is printed.
 * The test is run 10 times to get a better estimate of the average running time.
 */
public class Main {
    public static final Random random  = new Random();
    public static final int TESTS = 10, COUNTDOWN_THREADS = 200000, AWAIT_THREADS = 5000;
    public static long totalRuntime    = 0;
    public static void main(String[] args) throws InterruptedException {
        System.out.print("Testing MyCountDownLatch with "+COUNTDOWN_THREADS+" countdown threads and "+AWAIT_THREADS+" await threads × "+TESTS+"\n");
        for (int i = 0; i < TESTS; i++) {
            runTest();
        }
        System.out.printf("\nAverage: %dms\n", totalRuntime / TESTS);
    }

    public static void runTest() throws InterruptedException {
        final long start = System.currentTimeMillis();
        var myLatch      = new MyCountDownLatch(COUNTDOWN_THREADS);
        for (int i = 0; i < COUNTDOWN_THREADS; i++) {
            Thread.ofVirtual().start(() -> {
                try { Thread.sleep(200); }
                catch (InterruptedException e) { }
                myLatch.countDown();
            });
        }
        List<Thread> awaiters = new ArrayList<Thread>();
        for (int i = 0; i < AWAIT_THREADS; i++) {
            awaiters.add(Thread.ofVirtual().start(() -> myLatch.await()));
        }
        for (var t : awaiters) {
            t.join();
        }
        if (myLatch.count() != 0) {
            System.out.println("Count is not zero, is await() working properly?");
        } else {
            final long elapsed = System.currentTimeMillis() - start;
            totalRuntime += elapsed;
            System.out.print(elapsed+"ms ");
        }
    }
}
