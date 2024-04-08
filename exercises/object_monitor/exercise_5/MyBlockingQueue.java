import java.util.*;
import java.util.function.BooleanSupplier;

class MyBlockingQueue<T> {
    static final int SIZE = 100; // Limit queue size
    final Queue<T> items  = new LinkedList<T>();

    public synchronized T pop() {
        while (items.size() == 0) {
            try { wait(); }
            catch (InterruptedException ex) { }
        }
        notifyAll();
        return items.poll();
    }

    public synchronized void push(T item) {
        while (items.size() == SIZE) {
            try { wait(); }
            catch (InterruptedException ex) { }
        }
        notifyAll();
        items.offer(item);
    }
}
