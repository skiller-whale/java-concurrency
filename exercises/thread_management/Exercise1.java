import java.util.concurrent.*;

public class Exercise1 {
    public static void main(String[] args) {
        TinyRayTracer rt = new TinyRayTracer(3072, 2304, 1.05f);

        long t0 = System.currentTimeMillis();

        byte[][] lines = new byte[rt.height()][];
        Executor executor = Executors.newVirtualThreadPerTaskExecutor();

        // Use this executor to render the lines in parallel.

        for (int i = 0; i < rt.height(); i++)
            lines[i] = rt.renderLine(i);
        long t1 = System.currentTimeMillis();

        System.err.println("Render time: " + (t1-t0) + "ms");

        // Don't forget to wait for the lines to finish rendering,
        // e.g. with CountDownLatch
        // otherwise you'll see NullPointerExceptions from this method.

        TinyRayTracer.writePPM(lines, System.out);
    }
}
