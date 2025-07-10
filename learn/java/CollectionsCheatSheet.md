Here's a concise handout of major Java Collections, covering both non-concurrent and concurrent options, along with their most frequently used methods.

-----

# **Java Collections Cheat Sheet for SDE Interviews**

## **I. Non-Concurrent Collections (Single-Threaded Use / External Sync.)**

**General Note:** These collections are **NOT thread-safe**. If used in a multi-threaded environment, external synchronization (e.g., `synchronized` blocks or `Collections.synchronizedX` wrappers, though the latter are generally less performant than `java.util.concurrent` classes for high contention) is required.

### **1. `List` Interface: Ordered collection, allows duplicates.**

| Class          | Underlying Data Structure | Order Guarantee  | When to Use                                   | Common Methods                                      |
| :------------- | :------------------------ | :--------------- | :-------------------------------------------- | :-------------------------------------------------- |
| `ArrayList`    | Dynamic Array             | Insertion Order  | Frequent random access (`get(index)`), fast iteration. Adds/removes at end are fast. | `add(E)`, `get(int)`, `set(int, E)`, `remove(int)`, `size()`, `isEmpty()`, `contains(Object)` |
| `LinkedList`   | Doubly Linked List        | Insertion Order  | Frequent insertions/deletions from middle/ends. Good as a Queue/Stack. | `add(E)`, `addFirst(E)`, `addLast(E)`, `get(int)` (slow), `remove(int)`, `removeFirst()`, `removeLast()`, `size()` |

### **2. `Set` Interface: Unordered collection, no duplicates.**

| Class           | Underlying Data Structure | Order Guarantee    | When to Use                                   | Common Methods                                      |
| :-------------- | :------------------------ | :----------------- | :-------------------------------------------- | :-------------------------------------------------- |
| `HashSet`       | Hash Table (HashMap)      | None (Random)      | Fast add, remove, contains (O(1) average).     | `add(E)`, `remove(Object)`, `contains(Object)`, `size()`, `isEmpty()` |
| `LinkedHashSet` | Hash Table + Linked List  | Insertion Order    | Need order preservation with Set features.     | `add(E)`, `remove(Object)`, `contains(Object)`, `size()` |
| `TreeSet`       | Red-Black Tree (TreeMap)  | Natural / Custom   | Need sorted elements. Elements must be `Comparable` or provide `Comparator`. | `add(E)`, `remove(Object)`, `contains(Object)`, `size()`, `first()`, `last()`, `headSet()`, `tailSet()` |

### **3. `Map` Interface: Maps unique keys to values. No duplicate keys.**

| Class           | Underlying Data Structure | Key Order Guarantee | When to Use                                   | Common Methods                                      |
| :-------------- | :------------------------ | :------------------ | :-------------------------------------------- | :-------------------------------------------------- |
| `HashMap`       | Hash Table (Array of Nodes) | None (Random)       | Fast key-value lookups, insertions, deletions (O(1) average). | `put(K, V)`, `get(K)`, `remove(K)`, `containsKey(K)`, `containsValue(V)`, `size()`, `isEmpty()`, `keySet()`, `values()`, `entrySet()` |
| `LinkedHashMap` | Hash Table + Linked List  | Insertion / Access  | Need order preservation of keys. Useful for LRU caches. | `put(K, V)`, `get(K)`, `remove(K)`, `keySet()` etc. (Same as HashMap) |
| `TreeMap`       | Red-Black Tree            | Natural / Custom    | Need sorted keys. Keys must be `Comparable` or provide `Comparator`. | `put(K, V)`, `get(K)`, `remove(K)`, `firstKey()`, `lastKey()`, `headMap()`, `tailMap()` etc. |

### **4. `Queue` & `Deque` Interfaces: For specific retrieval order.**

| Class          | Underlying Data Structure | Order/Behavior | When to Use                                   | Common Methods                                      |
| :------------- | :------------------------ | :------------- | :-------------------------------------------- | :-------------------------------------------------- |
| `ArrayDeque`   | Dynamic Array             | FIFO (Queue) \<br\> LIFO (Stack) | As a general-purpose queue or stack. More efficient than `LinkedList` in most cases. | `offer(E)`, `poll()`, `peek()`, `addFirst(E)`, `addLast(E)`, `removeFirst()`, `removeLast()` |
| `PriorityQueue` | Min-Heap                  | Priority Order | Retrieving elements based on their natural ordering or a `Comparator`. | `offer(E)`, `poll()`, `peek()`, `size()`            |

-----

## **II. Concurrent Collections (`java.util.concurrent`)**

**General Note:** These collections are designed for **multi-threaded environments**, offering various strategies for thread-safety and performance. They are generally preferred over `Collections.synchronizedX` wrappers for high concurrency.

### **1. `Map`**

| Class               | Underlying Data Structure | Order Guarantee | Thread-Safety Strategy | When to Use                                   | Common Methods (same as `HashMap` plus)          |
| :------------------ | :------------------------ | :-------------- | :--------------------- | :-------------------------------------------- | :------------------------------------------------- |
| `ConcurrentHashMap` | Hash Table                | None            | Fine-grained locking / Lock-free (CAS) | High-performance, concurrent access to a Map. | `put(K, V)`, `get(K)`, `remove(K)`, `putIfAbsent(K, V)`, `compute(K, BiFunction)`, `computeIfPresent(K, BiFunction)` |

### **2. `List` & `Set`**

| Class                  | Underlying Data Structure | Order Guarantee | Thread-Safety Strategy | When to Use                                   | Common Methods (same as non-concurrent plus)      |
| :--------------------- | :------------------------ | :-------------- | :--------------------- | :-------------------------------------------- | :-------------------------------------------------- |
| `CopyOnWriteArrayList` | Array                     | Insertion Order | Copy-on-Write          | **Read-heavy** scenarios with infrequent writes. Iterators are snapshot-in-time and won't throw `ConcurrentModificationException`. | `add(E)`, `get(int)`, `set(int, E)`, `remove(int)`, `size()` |
| `CopyOnWriteArraySet`  | Array (uses CoWAL)        | Insertion Order | Copy-on-Write          | Same as `CopyOnWriteArrayList` for `Set` semantics. | `add(E)`, `remove(Object)`, `contains(Object)`, `size()` |

### **3. `Queue` & `Deque`**

| Class                 | Underlying Data Structure | Order/Behavior | Thread-Safety Strategy | When to Use                                   | Common Methods                                      |
| :-------------------- | :------------------------ | :------------- | :--------------------- | :-------------------------------------------- | :-------------------------------------------------- |
| `ArrayBlockingQueue`  | Bounded Array             | FIFO           | Single Lock            | Bounded producer-consumer pattern. Provides flow control. | `put(E)` (blocks), `take()` (blocks), `offer(E)`, `poll()`, `peek()` |
| `LinkedBlockingQueue` | Linked List (optionally bounded) | FIFO           | Separate Locks for Head/Tail | Bounded/unbounded producer-consumer. Often higher throughput than `ArrayBlockingQueue` for unbounded. | `put(E)`, `take()`, `offer(E)`, `poll()`, `peek()` |
| `ConcurrentLinkedQueue` | Linked List (unbounded) | FIFO           | Lock-free (CAS)        | High-performance, non-blocking producer-consumer. No capacity limits. | `offer(E)`, `poll()`, `peek()`, `isEmpty()`, `size()` |
| `ConcurrentLinkedDeque` | Linked List (unbounded) | FIFO/LIFO      | Lock-free (CAS)        | High-performance, non-blocking queue or stack. | `offerFirst(E)`, `offerLast(E)`, `pollFirst()`, `pollLast()`, `peekFirst()`, `peekLast()` |

### **4. Atomic Variables (Brief Mention - not "collections" but crucial for concurrency)**

These provide atomic (indivisible) operations on single variables without explicit locks, leveraging CPU instructions (CAS - Compare-And-Swap).

  * `AtomicInteger`, `AtomicLong`, `AtomicBoolean`, `AtomicReference<V>`
  * **Common Methods:** `get()`, `set()`, `incrementAndGet()`, `decrementAndGet()`, `compareAndSet(expectedValue, newValue)`

-----

**Key Takeaways for Interviews:**

  * **Non-Concurrent:** Fast for single-threaded use. Need external sync for multithreading.
  * **Concurrent:** Built-in thread-safety, optimized for concurrent access.
      * `ConcurrentHashMap`: For highly concurrent map operations.
      * `BlockingQueue`: For producer-consumer flow control (blocking when full/empty).
      * `CopyOnWriteX`: For read-heavy, write-light collections.
      * `ConcurrentLinkedX`: For high-throughput, non-blocking queues/deques.
  * **Understand trade-offs:** Performance vs. Memory vs. Specific access patterns.

-----