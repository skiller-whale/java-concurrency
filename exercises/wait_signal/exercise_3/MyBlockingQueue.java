import java.util.*;
import java.util.concurrent.locks.*;

class MyBlockingQueue<T> {
    static final int SIZE = 500; // Limit queue size
    final Queue<T> items  = new LinkedList<T>();
    final Lock lock       = new ReentrantLock();

    T pop() throws InterruptedException {
        lock.lock();
        try {
            while (items.size() == 0) {
                lock.unlock();
                lock.lock();
            }
            return items.poll();
        } finally { lock.unlock(); }
    }

    boolean push(T item) throws InterruptedException {
        lock.lock();
        try {
            while (items.size() == SIZE) {
                lock.unlock();
                lock.lock();
            }
            items.offer(item);
            return true;
        } finally { lock.unlock(); }
    }
}
