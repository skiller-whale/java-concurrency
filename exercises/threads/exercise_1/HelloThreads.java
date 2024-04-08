public class HelloThreads {
    public static void main(String[] args) throws InterruptedException {
        for (var i = 0; i < 10; i++) {
            final var digit = i;
            Runnable r = () -> { System.out.print(digit); };
            Thread.ofVirtual().start(r);
        }
        Thread.sleep(1000);
        System.out.println("Done!");
    }
}
