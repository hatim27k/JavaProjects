import java.util.concurrent.ThreadLocalRandom;

public class Demo implements Runnable {
    private Integer count = 0;
    private Object lock = new Object();

    public synchronized Integer getCount() {
        return count;
    }

    public synchronized void setCount(Integer count) {
        this.count = count;
    }

    public synchronized void incrementCount() {
        this.count++;
    }

    public synchronized void decrementCount() {
        this.count--;
    }

    public synchronized void resetCount() {
        this.count = 0;
    }

    public synchronized boolean isCountZero() {
        return count == 0;
    }

    public synchronized boolean isCountPositive() {
        return count > 0;
    }

    public synchronized boolean isCountNegative() {
        return count < 0;
    }

    public synchronized void printCount() {
        System.out.println("Current count: " + count);
    }

    public void useLock() {
        long threadId = Thread.currentThread().threadId();
        System.out.println("Before using lock: " + lock + " | Thread ID: " + threadId);
        long sleepTime = ThreadLocalRandom.current().nextLong(1, 2000);
        try {
            Thread.sleep(sleepTime); // Simulate some work
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Using the lock to synchronize access to a shared resource
        synchronized (lock) {
            System.out.println("Using lock: " + lock + " | Thread ID: " + threadId);
        }
        System.out.println("After using lock: " + lock + " | Thread ID: " + threadId);
    }

    @Override
    public void run() {
        System.out.println("Thread is running: Thread ID: " + Thread.currentThread().threadId());
    }
}
