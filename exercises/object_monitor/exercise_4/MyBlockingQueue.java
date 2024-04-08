import java.util.*;
import java.util.function.BooleanSupplier;

class MyBlockingQueue<T> {
    static final int SIZE = 100; // Limit queue size
    final Queue<T> items  = new LinkedList<T>();

    T pop() throws InterruptedException {
        while (items.size() == 0) { /* spin-wait */}
        return items.poll();
    }

    void push(T item) throws InterruptedException {
        while (items.size() == SIZE) { /* spin-wait */}
        items.offer(item);
    }
}
