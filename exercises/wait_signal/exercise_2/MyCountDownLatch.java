import java.util.concurrent.locks.*;

public class MyCountDownLatch {
    private int count;
    private Lock lock = new ReentrantLock();
    final Condition isZero = lock.newCondition();

    public int count() {
        lock.lock();
        try { return count; }
        finally { lock.unlock(); }
    }

    public MyCountDownLatch(int count) {
        this.count = count;
    }

    public void countDown() {
       // TODO: implement this method
    }

    public void await() {
        lock.lock();
        while (count > 0) {
            try { isZero.await(); }
            catch (InterruptedException e) { }
        }
        lock.unlock();
    }
}
