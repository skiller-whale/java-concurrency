import java.util.List;
import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.*;

public class Exercise3 {
    public static void main(String[] args) throws InterruptedException, FileNotFoundException {
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        final int MAX_TIME       = 2000;

        // This is the bounds for the number of lines in the image we're going to render.
        // lower = definitely fast enough, upper bound = definitely too slow.
        int lower = 9, upper = 100000;
        boolean warmup = true;
        while (upper - lower > 100) {

            // Decide how big the image should be based on the upper & lower bounds
            int height = lower + ((upper - lower) / 2);
            int width  = height*16/9;
            System.out.printf("Rendering %05d lines: ", width, height);

            // Render the image, but bail out if it takes too long.
            TinyRayTracer rt = new TinyRayTracer(Scene.DEFAULT, width, height, 1.05f);
            long t0 = System.currentTimeMillis();
            byte[][] lines = new byte[height][];
            for (int i = 0; i < height && System.currentTimeMillis() < t0 + MAX_TIME; i++)
                lines[i] = rt.renderLine(i);
            long t1 = System.currentTimeMillis();

            // Decide whether the upper or lower bound needs to move depending on whether
            // we made the deadline or not.
            if (warmup) {
                System.out.println("Warmup");
                warmup = false;
            } else if (t1 - t0 > MAX_TIME) {
                System.out.println("❌");
                upper = height;
            } else {
                System.out.println("✅");
                lower = height;
            }
        }
        System.out.println("Largest height picture we could render in "+MAX_TIME+"ms is "+lower+" lines");
    }
}