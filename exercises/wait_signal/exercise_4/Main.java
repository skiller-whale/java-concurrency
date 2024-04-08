public class Main {
    public static final int TESTS   = 2000;
    public static void main(String[] args) {
        for (int i = 0; i < TESTS; i++) {
            if (!testMyBlockingQueue()) {
                System.out.println("Deadlock on run "+i);
                System.exit(1);
            }
        }
        System.out.println("No deadlock in "+TESTS+" runs");
    }

    interface RunnableInterrupted { void run() throws InterruptedException; }
    public static Thread doTimes(int times, RunnableInterrupted r) {
        return Thread.ofVirtual().start(() -> {
            try { for (int i=0; i<times; i++) { r.run(); } }
            catch (InterruptedException i) { }
        });
    }

    public static boolean testMyBlockingQueue() {
        MyBlockingQueue<Integer> queue = new MyBlockingQueue<Integer>();

        Thread[] thread = new Thread[]{
            doTimes(10, () -> queue.pop()),
            doTimes(20, () -> queue.pop()),
            doTimes(10, () -> queue.pop()),
            doTimes(10, () -> queue.push(1)),
            doTimes(20, () -> queue.push(1)),
            doTimes(10, () -> queue.push(1)),
        };

        try { // make sure all threads have finished
            for (var t=0; t<thread.length; t++)
                if (!thread[t].join(java.time.Duration.ofMillis(1000)))
                    return false;
        }
        catch (InterruptedException i) { System.out.println("Interrupted?"); return false; }
        return true;
    }
}
