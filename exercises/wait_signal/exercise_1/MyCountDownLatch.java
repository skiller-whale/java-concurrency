import java.util.concurrent.locks.*;

public class MyCountDownLatch {
    private int count;
    private Lock lock = new ReentrantLock();

    public int count() {
        lock.lock();
        try { return count; }
        finally { lock.unlock(); }
    }

    public MyCountDownLatch(int count) {
        this.count = count;
    }

    public void countDown() {
        lock.lock();
        try {
            if (count > 0) {
                count--;
            }
        } finally { lock.unlock(); }
    }

    public void await() {
        // TODO: implement this method
    }
}
