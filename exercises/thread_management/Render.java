/* Small example so you can see how to drive the ray tracer
 *
 * e.g. compile with `javac Render.java TinyRayTracer.java` and run with `java Render > image.ppm`
 *
 * You should be able to open `image.ppm` in any image viewer.
 */

public class Render {
    public static void main(String[] args) {
        // The ray-tracer just needs to know the width, height, and field-of-view of the image.
        TinyRayTracer rt = new TinyRayTracer(1080, 720, 1.05f);

        // Each line of the image is another byte[] containing the pixel data.
        // For each pixel across, there are 3 bytes, for red, green and blue.
        // So e.g. there would be 3240 bytes for a line that is 1080 pixels across.
        byte[][] lines = new byte[720][];
        for (int i = 0; i < 720; i++)
            lines[i] = rt.renderLine(i);

        // A static utility function to write the byte array to a standard image file.
        TinyRayTracer.writePPM(lines, System.out);
    }
}