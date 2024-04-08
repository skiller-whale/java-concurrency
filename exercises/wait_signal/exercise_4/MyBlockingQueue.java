import java.util.concurrent.locks.*;
import java.util.LinkedList;
import java.util.Queue;

class MyBlockingQueue<T> {
    static final int QUEUE_SIZE        = 2; // Much smaller queue size
    final Queue<T> items               = new LinkedList<T>();
    final Lock lock                    = new ReentrantLock();
    final Condition hasSpaceOrhasItems = lock.newCondition();

    T pop() throws InterruptedException {
        lock.lock();
        try {
            while (items.size() == 0) {
               hasSpaceOrhasItems.await();
            }
            return items.poll(); // Pop front item
        } finally {
            hasSpaceOrhasItems.signal();
            lock.unlock();
        }
    }

    void push(T item) throws InterruptedException {
        lock.lock();
        try {
            if (items.size() == QUEUE_SIZE) {
               hasSpaceOrhasItems.await();
            }
            items.offer(item); // Push item to back
        } finally {
            hasSpaceOrhasItems.signal();
            lock.unlock();
        }
    }
}
