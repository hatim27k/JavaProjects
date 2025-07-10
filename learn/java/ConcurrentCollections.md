Okay, let's expand on **Streams**, **Lambda Expressions**, and **Concurrent Data Structures** with more detailed explanations and practical examples, suitable for an SDE interview.

-----

# **Java Interview Coding Handout (Extended)**

## **III. Advanced Java 8+ Features**

### **1. Lambda Expressions (Concise Functional Programming)**

Lambda expressions provide a concise way to represent an instance of a *functional interface* (an interface with a single abstract method). They are widely used with Streams and `java.util.concurrent` APIs.

**Syntax:** `(parameters) -> expression` or `(parameters) -> { statements; }`

**Common Functional Interfaces & Lambda Usage:**

  * **`Runnable` (no args, no return):**

    ```java
    // Old way
    // new Thread(new Runnable() { public void run() { System.out.println("Old Thread"); } }).start();
    // Lambda
    Runnable task = () -> System.out.println("Lambda Thread running!");
    new Thread(task).start();
    ```

  * **`Comparator<T>` (two args, returns int):**

    ```java
    import java.util.Arrays;
    import java.util.Comparator;
    import java.util.List;

    List<String> names = Arrays.asList("Alice", "Charlie", "Bob");

    // Old way
    // Collections.sort(names, new Comparator<String>() {
    //     @Override public int compare(String s1, String s2) { return s1.compareTo(s2); }
    // });
    // Lambda
    names.sort((s1, s2) -> s1.compareTo(s2)); // Natural order
    System.out.println("Sorted names: " + names); // [Alice, Bob, Charlie]

    // Sort by length (descending)
    names.sort((s1, s2) -> Integer.compare(s2.length(), s1.length()));
    System.out.println("Sorted by length (desc): " + names); // [Charlie, Alice, Bob]
    ```

  * **`Predicate<T>` (one arg, returns boolean):** Used for filtering.

    ```java
    import java.util.function.Predicate;

    Predicate<String> startsWithA = s -> s.startsWith("A");
    System.out.println("Is 'Apple' starting with A? " + startsWithA.test("Apple")); // true
    ```

  * **`Consumer<T>` (one arg, no return):** Used for performing an action.

    ```java
    import java.util.function.Consumer;

    Consumer<String> printUpperCase = s -> System.out.println(s.toUpperCase());
    printUpperCase.accept("hello"); // HELLO
    ```

  * **`Function<T, R>` (one arg T, returns R):** Used for transformation/mapping.

    ```java
    import java.util.function.Function;

    Function<Integer, String> intToString = i -> "Number: " + i;
    System.out.println(intToString.apply(123)); // Number: 123
    ```

**Method References:**
Shorthand for lambdas that simply call an existing method.

  * `ClassName::staticMethodName`
  * `object::instanceMethodName`
  * `ClassName::instanceMethodName` (first arg is receiver)
  * `ClassName::new` (constructor reference)

<!-- end list -->

```java
// Lambda: s -> System.out.println(s)
names.forEach(System.out::println); // Method reference for Consumer

// Lambda: s1, s2 -> s1.compareTo(s2)
names.sort(String::compareTo); // Method reference for Comparator

// For a constructor
// Supplier<List<String>> listSupplier = () -> new ArrayList<>();
// Supplier<List<String>> listSupplier = ArrayList::new; // Method reference
```

### **2. Java Streams API (Functional Data Processing)**

Streams provide a declarative way to process collections of data. They enable fluent, chainable operations and support parallel processing.

**Key Concepts:**

  * **Source:** A collection, array, `IO` source, etc., from which elements are streamed.
  * **Intermediate Operations:** Transform a stream into another stream. They are **lazy** (not executed until a terminal operation is called). Examples: `filter()`, `map()`, `sorted()`, `distinct()`, `limit()`, `skip()`.
  * **Terminal Operations:** Produce a result or a side-effect. They trigger the execution of the stream pipeline. Examples: `forEach()`, `collect()`, `reduce()`, `count()`, `min()`, `max()`, `findFirst()`, `anyMatch()`, `allMatch()`, `noneMatch()`.
  * **`Optional<T>`:** Used by some terminal operations (like `findFirst`, `min`, `max`) to represent the possible absence of a value, preventing `NullPointerExceptions`.

**Common Stream Operations with Examples:**

```java
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class StreamApiDemo {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        List<String> words = Arrays.asList("apple", "banana", "cat", "dog", "elephant", "cat");

        System.out.println("--- Filtering and Mapping (Intermediate & Terminal) ---");
        // Filter even numbers, square them, and print
        numbers.stream()
               .filter(n -> n % 2 == 0)      // Intermediate: keeps only even numbers
               .map(n -> n * n)             // Intermediate: transforms n to n*n
               .forEach(System.out::println); // Terminal: prints each result (side-effect)
        // Output: 4, 16, 36, 64, 100

        System.out.println("\n--- Collecting to List ---");
        // Filter words longer than 3 chars, convert to uppercase, collect into a new List
        List<String> longWordsUpperCase = words.stream()
                                               .filter(s -> s.length() > 3)
                                               .map(String::toUpperCase)
                                               .collect(Collectors.toList());
        System.out.println("Long words (uppercase): " + longWordsUpperCase); // [APPLE, BANANA, ELEPHANT]

        System.out.println("\n--- Reduction Operations ---");
        // Sum of all numbers
        int sum = numbers.stream().reduce(0, (a, b) -> a + b); // Initial value 0, accumulator (a+b)
        System.out.println("Sum of numbers: " + sum); // 55

        // Product of first 5 numbers
        Optional<Integer> product = numbers.stream()
                                           .limit(5)
                                           .reduce((a, b) -> a * b); // No initial value, returns Optional
        product.ifPresent(p -> System.out.println("Product of first 5: " + p)); // 120

        // Average of numbers
        double average = numbers.stream()
                                .mapToInt(Integer::intValue) // Convert to IntStream for optimized operations
                                .average()                   // Returns OptionalDouble
                                .orElse(0.0);                // Default if empty stream
        System.out.println("Average of numbers: " + average); // 5.5

        System.out.println("\n--- Grouping and Counting ---");
        // Group words by their first letter
        Map<Character, List<String>> wordsByFirstLetter = words.stream()
                                                               .collect(Collectors.groupingBy(s -> s.charAt(0)));
        System.out.println("Words grouped by first letter: " + wordsByFirstLetter);
        // Output: {c=[cat, cat], d=[dog], a=[apple], b=[banana], e=[elephant]}

        // Count occurrences of each word
        Map<String, Long> wordCounts = words.stream()
                                            .collect(Collectors.groupingBy(s -> s, Collectors.counting()));
        System.out.println("Word counts: " + wordCounts); // {dog=1, cat=2, apple=1, banana=1, elephant=1}

        System.out.println("\n--- Finding and Matching ---");
        // Find the first even number
        Optional<Integer> firstEven = numbers.stream()
                                             .filter(n -> n % 2 == 0)
                                             .findFirst(); // Terminal: returns Optional
        firstEven.ifPresent(n -> System.out.println("First even number: " + n)); // 2

        // Check if any word starts with 'z'
        boolean anyStartsWithZ = words.stream().anyMatch(s -> s.startsWith("z")); // Terminal
        System.out.println("Any word starts with 'z'? " + anyStartsWithZ); // false
    }
}
```

**Explanation for Interview (Streams):**
"Java Streams provide a functional approach to process data collections. They consist of a source, zero or more intermediate operations (which are lazy and transform the stream), and a single terminal operation (which triggers execution and produces a result). I can `filter` elements, `map` them to a different type, `collect` them into various data structures, or perform `reduce` operations like summing or averaging. The `Optional` type is important for handling cases where a result might be absent, preventing `NullPointerExceptions`."

-----

## **IV. Concurrent Data Structures (`java.util.concurrent`)**

Beyond basic `synchronized` blocks or `Collections.synchronizedX` wrappers, `java.util.concurrent` offers highly optimized, scalable, and sophisticated data structures.

**Why `java.util.concurrent` over `Collections.synchronizedX`?**

  * **Granularity:** `Collections.synchronizedList()` etc., synchronize the *entire* collection on *every* method call. This is a coarse-grained lock and becomes a performance bottleneck under high contention.
  * **Scalability:** `java.util.concurrent` collections often use more fine-grained locking (e.g., segment locking in `ConcurrentHashMap`) or lock-free algorithms (e.g., using CAS operations) to allow multiple threads to operate concurrently without blocking each other as much.
  * **Specific Use Cases:** They are designed for specific concurrent patterns (like producer-consumer, read-heavy, etc.), offering better performance and semantics.
  * **Iteration Safety:** `Collections.synchronizedX` still requires external synchronization for iteration to prevent `ConcurrentModificationException`. `java.util.concurrent` collections often provide weakly consistent iterators that don't throw this exception, though they might not reflect changes made after the iterator was created.

### **1. `ConcurrentHashMap` (Revisited)**

  * **Key Feature:** High concurrency for map operations. Divides the map into segments (or uses a more dynamic approach in Java 8+) and allows independent operations on different segments.
  * **Use Case:** Caches, shared configurations, global registries where multiple threads need to read/write map entries.

<!-- end list -->

```java
import java.util.concurrent.ConcurrentHashMap;
// (Example provided in previous handout is sufficient for demonstrating its core use)
// Key methods: put(K, V), get(K), remove(K), putIfAbsent(K, V)
// All operations are thread-safe and highly concurrent.
```

### **2. `BlockingQueue` Implementations (For Producer-Consumer)**

  * **Key Feature:** Operations block when the queue is full (`put()`) or empty (`take()`), providing flow control without explicit `wait()`/`notify()`.
  * **Implementations:**
      * `ArrayBlockingQueue`: Bounded, backed by an array. Fixed capacity.
      * `LinkedBlockingQueue`: Optionally bounded, backed by a linked list. Generally higher throughput than `ArrayBlockingQueue` when not bounded due to separate locks for `put` and `take`.
      * `PriorityBlockingQueue`: Unbounded, elements are ordered by priority.
      * `SynchronousQueue`: A queue of zero capacity. Each `put` must wait for a `take`, and vice versa. Useful for hand-off scenarios.
  * **Use Case:** Task queues, message queues, decoupling stages in a pipeline.

<!-- end list -->

```java
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class BlockingQueueDetailedDemo {
    public static void main(String[] args) throws InterruptedException {
        // A bounded queue of size 5
        BlockingQueue<String> queue = new ArrayBlockingQueue<>(5);

        ExecutorService executor = Executors.newFixedThreadPool(2);

        // Producer
        Runnable producer = () -> {
            try {
                for (int i = 0; i < 10; i++) {
                    String data = "Data-" + i;
                    queue.put(data); // Blocks if queue is full
                    System.out.println(Thread.currentThread().getName() + " produced: " + data + " (Queue size: " + queue.size() + ")");
                    Thread.sleep(50); // Simulate work
                }
                queue.put("STOP"); // Sentinel to signal end
                System.out.println(Thread.currentThread().getName() + " produced STOP.");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println(Thread.currentThread().getName() + " Producer interrupted.");
            }
        };

        // Consumer
        Runnable consumer = () -> {
            try {
                while (true) {
                    String data = queue.take(); // Blocks if queue is empty
                    if ("STOP".equals(data)) {
                        System.out.println(Thread.currentThread().getName() + " consumed STOP. Exiting.");
                        break;
                    }
                    System.out.println(Thread.currentThread().getName() + " consumed: " + data + " (Queue size: " + queue.size() + ")");
                    Thread.sleep(200); // Simulate work
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println(Thread.currentThread().getName() + " Consumer interrupted.");
            }
        };

        executor.submit(producer);
        executor.submit(consumer);

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
        System.out.println("BlockingQueue demo finished.");
    }
}
```

### **3. `CopyOnWriteArrayList` / `CopyOnWriteArraySet`**

  * **Key Feature:** All mutative operations (add, set, remove, etc.) create a **new copy** of the underlying array. Reads operate on the old, immutable array.
  * **When to Use:**
      * Collections that are **read-heavy** and rarely modified.
      * You need **snapshot-in-time** iterators (iterators won't see changes made after creation, and won't throw `ConcurrentModificationException`).
  * **When NOT to Use:**
      * Collections that are frequently modified due to the high overhead of copying the entire array.
      * Collections with very large sizes if modifications are even moderately frequent.

<!-- end list -->

```java
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CopyOnWriteListDemo {
    public static void main(String[] args) throws InterruptedException {
        List<String> list = new CopyOnWriteArrayList<>();
        list.add("A");
        list.add("B");
        list.add("C");

        ExecutorService executor = Executors.newFixedThreadPool(2);

        // Reader task
        executor.submit(() -> {
            System.out.println(Thread.currentThread().getName() + ": Starting read...");
            Iterator<String> it = list.iterator(); // Iterator gets a snapshot
            while (it.hasNext()) {
                String element = it.next();
                System.out.println(Thread.currentThread().getName() + ": Reading " + element);
                try {
                    Thread.sleep(100); // Simulate reading time
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            System.out.println(Thread.currentThread().getName() + ": Read finished.");
        });

        // Writer task
        executor.submit(() -> {
            try {
                System.out.println(Thread.currentThread().getName() + ": Starting write...");
                Thread.sleep(50); // Give reader a head start
                list.remove("B"); // This creates a new internal copy of the array
                System.out.println(Thread.currentThread().getName() + ": Removed B. List size: " + list.size());
                list.add("D");
                System.out.println(Thread.currentThread().getName() + ": Added D. List size: " + list.size());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        executor.shutdown();
        executor.awaitTermination(2, TimeUnit.SECONDS);

        System.out.println("\nFinal List: " + list);
        // Notice the reader might still print 'B' because its iterator was created on the old snapshot.
    }
}
```

**Explanation for Interview (Copy-on-Write):**
"`CopyOnWriteArrayList` is a thread-safe list variant primarily for scenarios where you have many more reads than writes. When a modification (like add or remove) occurs, the entire underlying array is copied. Reads, however, don't need any synchronization because they operate on an immutable snapshot of the list. This makes read operations extremely fast and highly concurrent, but writes can be expensive. A key benefit is that iterators obtained from `CopyOnWriteArrayList` will not throw `ConcurrentModificationException` because they operate on their own consistent snapshot of the data at the time of iterator creation."

### **4. `ConcurrentLinkedQueue` / `ConcurrentLinkedDeque`**

  * **Key Feature:** Non-blocking, lock-free implementations of `Queue`/`Deque`. They use sophisticated atomic operations (like CAS - Compare-And-Swap) rather than locks for high throughput in highly concurrent scenarios.
  * **When to Use:**
      * High-concurrency scenarios where blocking is undesirable.
      * Producer-consumer patterns where you don't need a bounded queue (i.e., you don't want producers to block if the queue gets too large).
  * **When NOT to Use:**
      * When you need a bounded queue or explicit flow control (use `BlockingQueue` instead).

<!-- end list -->

```java
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ConcurrentLinkedQueueDemo {
    public static void main(String[] args) throws InterruptedException {
        Queue<Integer> queue = new ConcurrentLinkedQueue<>();
        ExecutorService executor = Executors.newFixedThreadPool(4); // Multiple producers/consumers

        // Producers
        for (int i = 0; i < 2; i++) {
            final int producerId = i;
            executor.submit(() -> {
                for (int j = 0; j < 5; j++) {
                    int item = producerId * 10 + j;
                    queue.offer(item); // Non-blocking add
                    System.out.println(Thread.currentThread().getName() + ": Offered " + item);
                    try { Thread.sleep(50); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                }
            });
        }

        // Consumers
        for (int i = 0; i < 2; i++) {
            executor.submit(() -> {
                try {
                    while (true) {
                        Integer item = queue.poll(); // Non-blocking retrieve; returns null if empty
                        if (item != null) {
                            System.out.println(Thread.currentThread().getName() + ": Polled " + item);
                        } else {
                            // System.out.println(Thread.currentThread().getName() + ": Queue empty, waiting...");
                            Thread.sleep(100); // Wait if queue is empty
                        }
                        if (executor.isShutdown() && queue.isEmpty()) break; // Exit condition
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(3, TimeUnit.SECONDS);

        System.out.println("\nFinal Queue (remaining items if any): " + queue);
    }
}
```

**Explanation for Interview (ConcurrentLinkedQueue):**
"`ConcurrentLinkedQueue` is a high-performance, unbounded, lock-free queue. Unlike `BlockingQueue` implementations, `offer()` and `poll()` methods are non-blocking. This means `offer()` will never block (it will always succeed in adding an element as it's unbounded), and `poll()` will return `null` immediately if the queue is empty. It achieves thread-safety using atomic Compare-And-Swap (CAS) operations, making it suitable for very high-throughput, concurrent scenarios where you want to minimize blocking and don't need a fixed capacity."

-----

This expanded handout should provide you with enough detail and practical examples to confidently discuss these crucial Java concurrency and functional programming concepts in an SDE interview.