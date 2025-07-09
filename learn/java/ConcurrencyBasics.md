Concurrency in Java is about enabling your program to execute multiple tasks seemingly at the same time. This is crucial for building responsive and efficient applications, especially in today's multi-core processor systems.

Here are the basics of concurrency in Java:

### 1\. Threads

At the heart of Java concurrency are **threads**. A thread is the smallest unit of processing that can be scheduled by an operating system. When a Java program starts, it begins with a single thread, called the "main" thread. You can then create and manage additional threads to perform tasks concurrently.

There are two primary ways to create threads in Java:

  * **Extending the `Thread` class:**
    ```java
    class MyThread extends Thread {
        public void run() {
            System.out.println("Thread is running: " + Thread.currentThread().getName());
            // Task to be executed by this thread
        }

        public static void main(String[] args) {
            MyThread thread1 = new MyThread();
            thread1.start(); // Starts the thread, which calls its run() method
        }
    }
    ```
  * **Implementing the `Runnable` interface:** This is generally preferred because it allows your class to extend another class if needed (Java doesn't support multiple inheritance of classes).
    ```java
    class MyRunnable implements Runnable {
        public void run() {
            System.out.println("Runnable is running: " + Thread.currentThread().getName());
            // Task to be executed by this thread
        }

        public static void main(String[] args) {
            Thread thread2 = new Thread(new MyRunnable());
            thread2.start(); // Starts the thread, which calls the Runnable's run() method
        }
    }
    ```
    The `start()` method is crucial; it allocates a new call stack for the thread and then calls its `run()` method. Calling `run()` directly would execute it in the current thread, defeating the purpose of concurrency.

### 2\. Thread Lifecycle

A Java thread can be in one of several states:

  * **NEW:** A thread that has been created but not yet started.
  * **RUNNABLE:** A thread that is executing or ready to execute (waiting for CPU time).
  * **BLOCKED:** A thread that is waiting for a monitor lock to enter a `synchronized` block/method.
  * **WAITING:** A thread that is waiting indefinitely for another thread to perform a particular action (e.g., via `wait()`).
  * **TIMED\_WAITING:** A thread that is waiting for another thread to perform an action for a specified waiting time (e.g., via `sleep()` or `wait(long timeout)`).
  * **TERMINATED:** A thread that has completed its execution.

### 3\. Synchronization

When multiple threads access shared resources (like a shared variable or an object), you can run into problems like **race conditions** (where the outcome depends on the unpredictable timing of multiple threads) and **memory consistency errors** (where changes made by one thread are not immediately visible to others).

Java provides mechanisms for **synchronization** to prevent these issues:

  * **`synchronized` keyword:** This is the most fundamental way to achieve thread safety.

      * **Synchronized Methods:** When a method is declared `synchronized`, only one thread can execute that method on a given object instance at any time.
        ```java
        class Counter {
            private int count = 0;

            public synchronized void increment() {
                count++;
            }

            public synchronized int getCount() {
                return count;
            }
        }
        ```
      * **Synchronized Blocks:** You can synchronize on a specific object. This provides finer-grained control, allowing you to protect only the critical section of code that accesses shared resources.
        ```java
        class SharedResource {
            private int data = 0;
            private final Object lock = new Object(); // A dedicated object for locking

            public void updateData(int newValue) {
                synchronized (lock) { // Only one thread can acquire this lock at a time
                    data = newValue;
                }
            }

            public int getData() {
                synchronized (lock) {
                    return data;
                }
            }
        }
        ```
        The object passed to `synchronized` is called a **monitor** or **intrinsic lock**. When a thread enters a synchronized block, it acquires the monitor lock. Other threads attempting to enter a synchronized block on the same monitor will be blocked until the first thread releases the lock (when it exits the block).

  * **`volatile` keyword:** The `volatile` keyword ensures that changes to a variable are immediately visible to other threads. It doesn't provide atomicity (e.g., for incrementing a counter), but it guarantees memory visibility.

    ```java
    class Flag {
        public volatile boolean shouldStop = false;
    }
    ```

### 4\. Inter-Thread Communication

Threads often need to communicate with each other. Java's `Object` class provides fundamental methods for this, which must be used within `synchronized` blocks:

  * `wait()`: Causes the current thread to wait until another thread invokes the `notify()` or `notifyAll()` method for this object. The thread releases the lock it holds and goes into a WAITING state.
  * `notify()`: Wakes up a single thread that is waiting on this object's monitor.
  * `notifyAll()`: Wakes up all threads that are waiting on this object's monitor.

This "producer-consumer" pattern is a classic example of inter-thread communication.

### 5\. High-Level Concurrency Utilities (`java.util.concurrent`)

The `java.util.concurrent` package (introduced in Java 5) provides a rich set of high-level tools that simplify concurrent programming and are generally preferred over direct thread management and `synchronized` blocks for complex scenarios.

  * **Executors and Thread Pools:**

      * `ExecutorService`: Manages a pool of threads. Instead of creating a new `Thread` for each task, you submit `Runnable` or `Callable` (tasks that return a result) to an `ExecutorService`. This reuses threads, reducing overhead.
      * `Executors` class: Provides factory methods to create different types of `ExecutorService` (e.g., `newFixedThreadPool`, `newCachedThreadPool`, `newSingleThreadExecutor`).

    <!-- end list -->

    ```java
    import java.util.concurrent.ExecutorService;
    import java.util.concurrent.Executors;

    public class ExecutorExample {
        public static void main(String[] args) {
            ExecutorService executor = Executors.newFixedThreadPool(2); // Creates a pool of 2 threads

            executor.submit(() -> System.out.println("Task 1 executed by " + Thread.currentThread().getName()));
            executor.submit(() -> System.out.println("Task 2 executed by " + Thread.currentThread().getName()));
            executor.submit(() -> System.out.println("Task 3 executed by " + Thread.currentThread().getName()));

            executor.shutdown(); // Initiates an orderly shutdown
        }
    }
    ```

  * **Locks (`java.util.concurrent.locks`):** Provides more flexible and powerful locking mechanisms than the `synchronized` keyword, such as `ReentrantLock` (which offers fairness, timed lock attempts, and interruptible locking) and `ReadWriteLock`.

  * **Concurrent Collections:** Thread-safe collection classes that are optimized for concurrent access, reducing the need for explicit synchronization. Examples include `ConcurrentHashMap`, `CopyOnWriteArrayList`, and various `BlockingQueue` implementations.

  * **Atomic Variables (`java.util.concurrent.atomic`):** Classes like `AtomicInteger`, `AtomicLong`, `AtomicBoolean` provide atomic operations on single variables without using locks. This is efficient for simple updates.

  * **Synchronizers:**

      * `CountDownLatch`: Allows one or more threads to wait until a set of operations being performed by other threads completes.
      * `CyclicBarrier`: Allows a set of threads to all wait for each other to reach a common barrier point.
      * `Semaphore`: Controls access to a shared resource by maintaining a set of permits.

### 6\. Common Concurrency Problems

While concurrency offers benefits, it introduces complexities and potential problems:

  * **Deadlock:** Two or more threads are blocked indefinitely, waiting for each other to release a resource.
  * **Livelock:** Threads repeatedly change their state in response to other threads, but no thread makes progress.
  * **Starvation:** A thread is perpetually denied access to a shared resource by other greedy threads.
  * **Race Conditions:** As mentioned, when the correctness of a program depends on the relative timing or interleaving of multiple threads.
  * **Memory Consistency Errors:** When threads have inconsistent views of shared data.

### Conclusion

Java's concurrency features are powerful but require careful consideration. Starting with the basic `Thread` and `Runnable` concepts is essential, but for real-world applications, leveraging the higher-level APIs in `java.util.concurrent` is highly recommended as they abstract away many complexities and provide more robust solutions. Understanding synchronization, thread safety, and common concurrency pitfalls is key to writing reliable concurrent Java applications.