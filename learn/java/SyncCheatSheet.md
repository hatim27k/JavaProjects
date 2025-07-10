
Here's a concise handout on major Java Synchronization Constructs, useful for a coding interview.

-----

# **Java Synchronization Constructs Cheat Sheet**

## **I. Core Java Primitives (Intrinsic Locks / Monitors)**

These are built into every Java object.

### **1. `synchronized` Keyword**

  * **Purpose:** Ensures that only one thread can execute a critical section of code at a time, preventing race conditions. Also guarantees memory visibility (all changes made inside a `synchronized` block/method are visible to threads acquiring the same lock later).
  * **Mechanism:** Uses an "intrinsic lock" or "monitor" associated with an object. A thread must acquire the lock to enter, and releases it upon exit.

| Usage             | Syntax                      | Lock Acquired On           | When to Use                                  |
| :---------------- | :-------------------------- | :------------------------- | :------------------------------------------- |
| **Synchronized Method** | `public synchronized void methodA() { ... }` | `this` (the instance of the object) | Protecting instance state of an object.      |
| **Static Synchronized Method** | `public static synchronized void methodB() { ... }` | `ClassName.class` (the Class object) | Protecting static state of a class.          |
| **Synchronized Block** | `synchronized (objectReference) { ... }` | `objectReference` (any object) | Finer-grained control; protecting specific shared resources, not the whole method. |

**Example (Block):**

```java
class Counter {
    private int count = 0;
    private final Object lock = new Object(); // Private lock object for synchronization

    public void increment() {
        synchronized (lock) { // Only one thread can execute this block at a time
            count++;
        }
    }

    public int getCount() {
        synchronized (lock) {
            return count;
        }
    }
}
```

### **2. `wait()`, `notify()`, `notifyAll()`**

  * **Purpose:** Inter-thread communication. Allows threads to pause execution (`wait()`) until a specific condition is met, and for other threads to signal (`notify()`/`notifyAll()`) that the condition might now be true.
  * **Key Rule:** MUST be called from within a `synchronized` block or method, and on the same object that is used for synchronization (the monitor object). Failure to do so throws `IllegalMonitorStateException`.
  * **Behavior:**
      * `wait()`: Releases the lock it holds and enters a WAITING or TIMED\_WAITING state. When notified, it re-acquires the lock and resumes.
      * `notify()`: Wakes up a single thread waiting on this object's monitor. Which thread gets woken up is non-deterministic.
      * `notifyAll()`: Wakes up all threads waiting on this object's monitor.

**Example (Producer-Consumer):**

```java
class MessageQueue {
    private String message;
    private boolean empty = true;

    public synchronized String take() { // Called by consumer
        while (empty) { // Spurious wakeup: always check condition in loop
            try { wait(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
        empty = true;
        notifyAll(); // Notify producer that queue is empty
        return message;
    }

    public synchronized void put(String msg) { // Called by producer
        while (!empty) {
            try { wait(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
        empty = false;
        this.message = msg;
        notifyAll(); // Notify consumer that message is available
    }
}
```

-----

## **II. High-Level Concurrency Utilities (`java.util.concurrent.locks`, `java.util.concurrent`)**

These provide more flexible and powerful alternatives to intrinsic locks and `wait`/`notify`.

### **1. `Lock` Interface (e.g., `ReentrantLock`)**

  * **Purpose:** An explicit lock implementation offering more control than `synchronized`.
  * **Features:**
      * **Reentrancy:** A thread can acquire a lock it already holds.
      * **Fairness (optional):** Locks can be configured to grant access to the longest-waiting thread.
      * **Timed `tryLock()`:** Attempt to acquire lock with a timeout.
      * **Interruptible `lock()`:** A thread waiting for a lock can be interrupted.
  * **Common Methods:**
      * `lock()`: Acquires the lock. Blocks if unavailable.
      * `unlock()`: Releases the lock. MUST be called in a `finally` block to prevent deadlocks.
      * `tryLock()`: Attempts to acquire the lock without blocking.
      * `lockInterruptibly()`: Acquires the lock unless the current thread is interrupted.

**Example:**

```java
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class MySafeResource {
    private int value = 0;
    private final Lock lock = new ReentrantLock(); // Explicit lock

    public void increment() {
        lock.lock(); // Acquire lock
        try {
            value++;
        } finally {
            lock.unlock(); // Release lock in finally block
        }
    }

    public int getValue() {
        lock.lock();
        try {
            return value;
        } finally {
            lock.unlock();
        }
    }
}
```

### **2. `Condition` Interface (from `Lock`)**

  * **Purpose:** Provides `wait`/`notify`-like functionality but tied to a `Lock` instance, allowing multiple "wait sets" for a single lock.
  * **Common Methods:**
      * `await()`: Equivalent to `wait()`, but on a `Condition` object.
      * `signal()`: Equivalent to `notify()`.
      * `signalAll()`: Equivalent to `notifyAll()`.

**Example (using `ReentrantLock` and `Condition`):**

```java
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class DataBuffer {
    private String data;
    private boolean hasData = false;
    private final Lock lock = new ReentrantLock();
    private final Condition dataAvailable = lock.newCondition(); // Condition for consumers
    private final Condition bufferEmpty = lock.newCondition();   // Condition for producers

    public void put(String d) throws InterruptedException {
        lock.lock();
        try {
            while (hasData) {
                bufferEmpty.await(); // Producer waits if buffer full
            }
            this.data = d;
            hasData = true;
            dataAvailable.signalAll(); // Signal consumers
        } finally {
            lock.unlock();
        }
    }

    public String take() throws InterruptedException {
        lock.lock();
        try {
            while (!hasData) {
                dataAvailable.await(); // Consumer waits if no data
            }
            hasData = false;
            bufferEmpty.signalAll(); // Signal producers
            return data;
        } finally {
            lock.unlock();
        }
    }
}
```

### **3. `Semaphore`**

  * **Purpose:** Controls access to a fixed number of resources. A semaphore maintains a set of "permits." Threads must acquire a permit to access the resource and release it when done.
  * **Common Methods:**
      * `acquire()`: Acquires a permit. Blocks if no permits available.
      * `release()`: Releases a permit.
  * **When to Use:** Limiting concurrent access to a pool of resources (e.g., database connections, file handles).

**Example:**

```java
import java.util.concurrent.Semaphore;

class ResourcePool {
    private final Semaphore semaphore = new Semaphore(3); // Allows 3 concurrent users

    public void useResource(int id) {
        try {
            semaphore.acquire(); // Acquire a permit
            System.out.println("Thread " + id + " acquired resource.");
            Thread.sleep(1000); // Simulate using resource
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            semaphore.release(); // Release the permit
            System.out.println("Thread " + id + " released resource.");
        }
    }
}
// In main: new Thread(() -> pool.useResource(1)).start();
```

### **4. `CountDownLatch`**

  * **Purpose:** A synchronization aid that allows one or more threads to wait until a set of operations being performed in other threads completes. It's a "one-time gate."
  * **Common Methods:**
      * `countDown()`: Decrements the latch's count.
      * `await()`: Blocks until the count reaches zero.
  * **When to Use:** Main thread waiting for N worker threads to finish their initial setup or tasks.

**Example:**

```java
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LatchDemo {
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(3); // Wait for 3 tasks
        ExecutorService executor = Executors.newFixedThreadPool(3);

        for (int i = 0; i < 3; i++) {
            final int taskId = i + 1;
            executor.submit(() -> {
                System.out.println("Task " + taskId + " started.");
                try { Thread.sleep(500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                System.out.println("Task " + taskId + " completed.");
                latch.countDown(); // Task finished, decrement latch
            });
        }

        System.out.println("Main thread waiting for tasks...");
        latch.await(); // Main thread waits here until count is 0
        System.out.println("All tasks finished. Main thread continues.");

        executor.shutdown();
    }
}
```

### **5. `CyclicBarrier`**

  * **Purpose:** A reusable synchronization barrier for a fixed number of threads. Threads wait at the barrier until all threads (or a specified number) have arrived. Once all arrive, the barrier "breaks" and allows all threads to proceed. Can run a barrier action.
  * **Common Method:** `await()`: Threads call this when they reach the barrier.
  * **When to Use:** Multi-phase computations where each phase can only start after all threads complete the previous phase.

### **6. `Exchanger`**

  * **Purpose:** Allows two threads to exchange objects at a synchronization point. Each thread calls `exchange()` and receives the object offered by the other thread.
  * **Common Method:** `exchange(V x)`

-----

## **III. Visibility Only: `volatile` Keyword**

  * **Purpose:** Guarantees that reads from and writes to a `volatile` variable are always read from/written to main memory, not from a thread's local cache. Ensures visibility of changes across threads.
  * **Important:** `volatile` does **NOT** guarantee atomicity for compound operations (e.g., `i++` is not atomic even on a `volatile int`).
  * **When to Use:** For simple flags or status variables that are modified by one thread and read by others.

**Example:**

```java
class SharedFlag {
    public volatile boolean running = true; // Ensures changes to 'running' are visible
    // public boolean running = true; // Without volatile, changes might not be seen immediately

    public void stop() {
        running = false;
    }

    public void doWork() {
        while (running) {
            // Do some work
        }
        System.out.println("Worker stopped.");
    }
}
```

-----

## **IV. Atomic Variables (`java.util.concurrent.atomic`)**

  * **Purpose:** Provide atomic (indivisible) operations on single variables without explicit locking. They achieve this using Compare-And-Swap (CAS) CPU instructions.
  * **Classes:** `AtomicInteger`, `AtomicLong`, `AtomicBoolean`, `AtomicReference<V>`, `AtomicStampedReference<V>`, `AtomicMarkableReference<V>`, etc.
  * **Common Methods:** `get()`, `set()`, `incrementAndGet()`, `decrementAndGet()`, `compareAndSet(expectedValue, updateValue)` (atomically sets to `updateValue` if current value is `expectedValue`).
  * **When to Use:** Simple, thread-safe counters, flags, or single-variable updates. Often more performant than `synchronized` for these specific cases under high contention.

**Example:**

```java
import java.util.concurrent.atomic.AtomicInteger;

class AtomicCounter {
    private AtomicInteger count = new AtomicInteger(0);

    public void increment() {
        count.incrementAndGet(); // Atomic increment
    }

    public int getCount() {
        return count.get();
    }

    public boolean compareAndSet(int expected, int update) {
        return count.compareAndSet(expected, update); // Atomic conditional update
    }
}
```

-----

This handout covers the essential synchronization constructs you're likely to encounter or need to implement in a Java SDE interview.