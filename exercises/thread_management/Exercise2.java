import java.util.concurrent.*;

public class Exercise2 {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        TinyRayTracer rt = new TinyRayTracer(3072, 2304, 1.05f);

        long t0 = System.currentTimeMillis();
        Executor executor = Executors.newVirtualThreadPerTaskExecutor();

        byte[][] lines = new byte[rt.height()][];
        CountDownLatch latch = new CountDownLatch(rt.height());
        for (int i = 0; i < rt.height(); i++) {
            final int iFinal = i;
            executor.execute(() -> {
                lines[iFinal] = rt.renderLine(iFinal);
                latch.countDown();
            });
        }
        try {
            latch.await();
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }

        long t1 = System.currentTimeMillis();
        System.err.println("Render time: " + (t1-t0) + "ms");

        TinyRayTracer.writePPM(lines, System.out);
    }
}
