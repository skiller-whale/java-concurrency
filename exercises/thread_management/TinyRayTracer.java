import java.util.Random;

/**
 * This is a CPU-intensive task that renders a 3D scene using ray-tracing.
 * You don't need to know the algorithms or alter this file!
 * It's just an example of a CPU-intensive task that can be benefit from parallelism.
 *
 * You only need to use the 3 documented methods to produce a picture:
 *
 * - `TinyRayTracer(int width, int height, float fov)` to prepare to render an image of a given size and field-of-view (use 1.05f).
 * - `byte[] renderLine(int line)` to render a single line of the image.
 * - `TinyRayTracer.writePPM(java.io.PrintStream out)` to write the image to a stream.
 *
 * It takes about 5s to generate a 1080p image on a 2020 laptop.
 *
 * Adapted from https://github.com/ssloy/tinyraytracer by Dmitry Sokolov 🙏🏻
 */

public record TinyRayTracer(Scene scene, int width, int height, float fov) {
    public TinyRayTracer(int width, int height, float fov) { this(Scene.DEFAULT, width, height, fov); }

    Intersection sceneIntersect(Vec3 orig, Vec3 dir) {
        Intersection intersection = Intersection.NONE;
        for (Solid object : scene.solids())
            intersection = intersection.closer(object.intersectRay(orig, dir));
        return intersection;
    }

    ColorVec3 castRay(Vec3 orig, Vec3 dir, int depth) {
        if (depth>4) return scene.background();
        Intersection sceneI = sceneIntersect(orig, dir);
        if (sceneI == Intersection.NONE) return scene.background();

        Vec3 reflectDir = dir.reflect(sceneI.normal()).normalized();
        Vec3 refractDir = dir.refract(sceneI.normal(), sceneI.material().refractive_index(), 1.f).normalized();
        ColorVec3 reflectColor = castRay(sceneI.point(), reflectDir, depth + 1);
        ColorVec3 refractColor = castRay(sceneI.point(), refractDir, depth + 1);

        float diffuse_light_intensity = 0, specular_light_intensity = 0;
        for (Vec3 light : scene.lights()) { // checking if the point lies in the shadow of the light
            Vec3 light_dir = light.sub(sceneI.point()).normalized();
            Intersection shadowI = sceneIntersect(sceneI.point(), light_dir);
            if (shadowI != Intersection.NONE && shadowI.point().sub(sceneI.point()).norm() < light.sub(sceneI.point()).norm()) continue;
            diffuse_light_intensity  += Math.max(0.f, light_dir.mul(sceneI.normal()));
            specular_light_intensity += (float)Math.pow(
                Math.max(0.f, -light_dir.neg().reflect(sceneI.normal()).mul(dir)),
                sceneI.material().specular_exponent()
            );
        }
        return new ColorVec3(
            sceneI.material().diffuse_color().
            mul(diffuse_light_intensity).
            mul(sceneI.material().albedo()[0]).
            add(
                new Vec3(1.f,1.f,1.f).
                mul(specular_light_intensity).
                mul(sceneI.material().albedo()[1])
            ).
            add(
                reflectColor.
                mul(sceneI.material().albedo()[2])
            ).
            add(refractColor.mul(sceneI.material().albedo()[3]))
        );
    }

    /**
     * Renders a single line of the image, returns a byte array of [r,g,b,r,g,b,...] values.
     *
     * @param line the line to render (0 to height-1, counting from the top)
     * @return 3 bytes per pixel in the line (r,g,b repeated)
     */
    public byte[] renderLine(int line) {
        byte[] out = new byte[width*3];
        for (int col = 0; col < width; col++) {
            if (Thread.interrupted()) return null;
            float dir_x =  (col + 0.5f) -  width/2.f;
            float dir_y = -(line + 0.5f) + height/2.f; // this flips the image at the same time
            float dir_z = -height/(2.f*(float)Math.tan(fov/2.f));
            castRay(new Vec3(0,0,0), new Vec3(dir_x, dir_y, dir_z).normalized(), 0).writeRGB(out, col*3);
        }
        return out;
    }

    /**
     * Writes the framebuffer to a PPM file.
     *
     * @param lines an array of byte[] rows, with 3 bytes per pixel (r,g,b repeated)
     * @param out the stream to write to (e.g. `System.out`)
     */
    public static void writePPM(byte[][] lines, java.io.PrintStream out) {
        out.println("P6\n" + lines[0].length/3 + " " + lines.length + "\n255");
        for (byte[] line : lines) out.write(line, 0, line.length);
        out.flush();
    }

    /**
     * Writes the framebuffer to a PPM file.
     *
     * @param lines an List of byte[] rows, with 3 bytes per pixel (r,g,b repeated)
     * @param out the stream to write to (e.g. `System.out`)
     */
    public static void writePPM(java.util.List<byte[]> lines, java.io.PrintStream out) {
        out.println("P6\n" + lines.get(0).length/3 + " " + lines.size() + "\n255");
        for (byte[] line : lines) out.write(line, 0, line.length);
        out.flush();
    }
}

class Vec3 {
    public final float x, y, z;
    Vec3() { this(0,0,0);}
    Vec3(float x, float y, float z) { this.x = x; this.y = y; this.z = z; }
    Vec3(double x, double y, double z) { this.x = (float) x; this.y = (float) y; this.z = (float) z; }
    Vec3 mul(float v) { return new Vec3(x*v, y*v, z*v); }
    float mul(Vec3 v) { return x*v.x + y*v.y + z*v.z; }
    Vec3 add(Vec3 v) { return new Vec3(x+v.x, y+v.y, z+v.z); }
    Vec3 sub(Vec3 v) { return new Vec3(x-v.x, y-v.y, z-v.z); }
    Vec3 neg() { return new Vec3(-x, -y, -z); }
    float norm() { return (float) Math.sqrt(x*x+y*y+z*z); }
    Vec3 normalized() { return mul(1.f/norm()); }
    Vec3 cross(Vec3 v2) { return new Vec3(y*v2.z - z*v2.y, z*v2.x - x*v2.z, x*v2.y - y*v2.x); }
    Vec3 reflect(Vec3 n) { return sub(n.mul(2.f*mul(n))); }
    Vec3 refract(Vec3 n, float eta_t, float eta_i) { // Snell's law
    // default for eta_i = 1.0
        float cosi = - Math.max(-1.f, Math.min(1.f, mul(n)));
        if (cosi<0) return refract(n.neg(), eta_i, eta_t); // if the ray comes from the inside the object, swap the air and the media
        float eta = eta_i / eta_t;
        float k = 1 - eta*eta*(1 - cosi*cosi);
        return k<0 ? new Vec3(1,0,0) : mul(eta).add(n.mul(eta*cosi - (float) Math.sqrt(k))); // k<0 = total reflection, no ray to refract. I refract it anyways, this has no physical meaning
    }
}

class ColorVec3 extends Vec3 {
    public ColorVec3() { super(); }
    public ColorVec3(Vec3 v) { super(v.x, v.y, v.z); }
    public ColorVec3(float x, float y, float z) { super(x,y,z); }
    public void writeRGB(byte[] out, int off) {
        float max = Math.max(1.f, (Math.max(x, Math.max(y, z))));
        out[off+0] = (byte) (255.f*x/max);
        out[off+1] = (byte) (255.f*y/max);
        out[off+2] = (byte) (255.f*z/max);
    }
}

record Material(float refractive_index, float[] albedo, ColorVec3 diffuse_color, float specular_exponent) {
    static final Material ivory = new Material(1.0f, new float[]{0.6f,  0.3f, 0.1f, 0.0f}, new ColorVec3(0.4f, 0.4f, 0.3f),   50.f);
    static final Material glass = new Material(1.5f, new float[]{0.0f,  0.5f, 0.1f, 0.8f}, new ColorVec3(0.6f, 0.7f, 0.8f),  125.f);
    static final Material redRubber = new Material(1.0f, new float[]{0.9f,  0.1f, 0.0f, 0.0f}, new ColorVec3(0.3f, 0.1f, 0.1f),   10.f);
    static final Material mirror = new Material(1.0f, new float[]{0.0f, 10.0f, 0.8f, 0.0f}, new ColorVec3(1.0f, 1.0f, 1.0f), 1425.f);
    static final Material check1 = new Material(1.f, new float[]{2f,0,0,0}, new ColorVec3(.3f,.3f,.3f), 0);
    static final Material check2 = new Material(1.f, new float[]{2f,0,0,0}, new ColorVec3(.3f,.2f,.1f), 0);
}

record Intersection(Vec3 point, float distance, Vec3 normal, Material material) {
    static Intersection NONE = new Intersection(null, Float.MAX_VALUE, null, null);
    public Intersection closer(Intersection b) { return distance<b.distance ? this : b; }
}

interface Solid { Intersection intersectRay(Vec3 orig, Vec3 dir); }

record Sphere(Vec3 center, float radius, Material material) implements Solid {
    public Intersection intersectRay(Vec3 orig, Vec3 dir) {
        Vec3 L = center.sub(orig);
        float tca = L.mul(dir);
        float d2 = L.mul(L) - tca*tca;
        if (d2 > radius*radius) return Intersection.NONE;
        float thc = (float)Math.sqrt(radius*radius - d2);
        float t0 = tca - thc, t1 = tca + thc, distance;
        if (t0 > .001f) distance = t0;
        else if (t1 > .001f) distance = t1;
        else return Intersection.NONE;
        Vec3 pt = orig.add(dir.mul(distance));
        return new Intersection(pt, distance, pt.sub(center()).normalized(), material());
    }
}

record CheckerBoard() implements Solid {
    public Intersection intersectRay(Vec3 orig, Vec3 dir) {
        float d = -(orig.y+4)/dir.y; // the checkerboard plane has equation y = -4
        Vec3 p = orig.add(dir.mul(d));
        if (d>.001 && Math.abs(p.x)<10 && p.z<-10 && p.z>-30) {
            var material = ((int)(.5*p.x+1000) + (int)(.5*p.z) & 1) == 1 ? Material.check1 : Material.check2;
            return new Intersection(p, d, new Vec3(0,1,0), material);
        }
        return Intersection.NONE;
    }
}

record Scene(Vec3[] lights, Solid[] solids, ColorVec3 background) {
    static final Scene DEFAULT = new Scene(
        new Vec3[] {
            new Vec3(-20, 20,  20),
            new Vec3( 30, 50, -25),
            new Vec3( 30, 20,  30)
        },
        new Solid[] {
            new CheckerBoard(),
            new Sphere(new Vec3(-3f,    0f,   -16f), 2, Material.ivory),
            new Sphere(new Vec3(-1.0f, -1.5f, -12f), 2, Material.glass),
            new Sphere(new Vec3( 1.5f, -0.5f, -18f), 3, Material.redRubber),
            new Sphere(new Vec3( 7f,    5f,   -18f), 4, Material.mirror),
        },
        new ColorVec3(0.2f, 0.7f, 0.9f)
    );

    static Scene frame(long seed, int n, int max) {
        Random random = new Random(seed);
        Solid[] spheres = new Solid[6];
        double m = 2*Math.PI*n/max;

        for (int i=0; i<spheres.length; i++) {
            spheres[i] = new Sphere(
                new Vec3(
                    (float)(random.nextFloat()*10-5)*Math.sin(m) + (random.nextFloat()*10-5),
                    (float)(random.nextFloat()*10-5)*Math.cos(m) + (random.nextFloat()*10-5),
                    (float)(random.nextFloat()*10-5)*Math.sin(m) - 10.0f
                ),
                (float)(random.nextFloat()*3),
                new Material(
                    random.nextFloat(),
                    new float[]{
                        random.nextFloat(),
                        random.nextFloat(),
                        random.nextFloat(),
                        random.nextFloat()
                    },
                    new ColorVec3(
                        random.nextFloat(),
                        random.nextFloat(),
                        random.nextFloat()
                    ),
                    (float)(random.nextFloat()*10)
                )
            );
        }

        return new Scene(
            new Vec3[] {
                new Vec3(-20, 20,  20),
                new Vec3( 30, 50, -25),
                new Vec3( 30, 20,  30)
            },
            spheres,
            new ColorVec3(0.2f, 0.7f, 0.9f)
        );
    }
}
