import java.io.*;

public class Exercise4 {
    public static void main(String[] args) {
        long t0 = System.currentTimeMillis();
        for (int frame=0; frame < 100; frame++) {
            TinyRayTracer rt = new TinyRayTracer(Scene.frame(0, frame, 100) , 1080, 720, 1.05f);

            // Each line of the image is another byte[] containing the pixel data.
            // For each pixel across, there are 3 bytes, for red, green and blue.
            // So e.g. there would be 3240 bytes for a line that is 1080 pixels across.
            byte[][] lines = new byte[720][];
            for (int i = 0; i < 720; i++)
                lines[i] = rt.renderLine(i);

            // A static utility function to write the byte array to a standard image file.
            try (PrintStream out = new PrintStream(new FileOutputStream("scene" + frame + ".ppm"))) {
                TinyRayTracer.writePPM(lines, out);
            }
            catch (IOException e) {
                System.err.println("Error writing frame " + frame + ": " + e.getMessage());
                break;
            }
        }
        long t1 = System.currentTimeMillis();
        System.out.println("Time: " + (t1 - t0) + "ms");
    }
}