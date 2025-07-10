Okay, here's a concise handout covering essential Java syntax and built-in data structures for a software developer coding interview. It's designed for quick reference, not in-depth learning.

-----

# **Java Interview Coding Handout**

## **I. Core Java Syntax & Concepts**

### **1. Basic Structure**

```java
// Single-line comment
/* Multi-line comment */

// Class Definition
public class MyClass { // public: accessible from anywhere
    // Main method: entry point of the application
    public static void main(String[] args) {
        // Your code here
    }

    // Other methods
    public void myMethod(int param) {
        // ...
    }
}
```

### **2. Variables & Data Types**

  * **Primitive Types (Value Types):**
      * `byte` (1 byte), `short` (2 bytes), `int` (4 bytes), `long` (8 bytes)
      * `float` (4 bytes), `double` (8 bytes)
      * `char` (2 bytes, Unicode)
      * `boolean` (1 bit, true/false)
  * **Reference Types (Objects):**
      * `String`
      * Arrays (`int[]`, `String[]`, etc.)
      * Custom classes (`MyObject obj = new MyObject();`)
  * **Declaration & Initialization:**
    ```java
    int age = 30;
    String name = "Alice";
    boolean isActive = true;
    double price = 99.99;
    char initial = 'J';
    final int MAX_VALUE = 100; // 'final' for constants (cannot be reassigned)
    ```

### **3. Operators**

  * **Arithmetic:** `+`, `-`, `*`, `/`, `%` (modulus)
  * **Assignment:** `=`, `+=`, `-=`, `*=`, `/=`, `%=`
  * **Comparison (Relational):** `==`, `!=`, `>`, `<`, `>=`, `<=` (for primitive values)
      * **`==` for Objects:** Compares references (memory addresses), NOT content. Use `equals()` for content comparison.
  * **Logical:** `&&` (AND), `||` (OR), `!` (NOT)
  * **Increment/Decrement:** `++`, `--` (pre- vs. post-increment)

### **4. Control Flow**

  * **`if-else if-else`:**
    ```java
    if (condition1) { /* code */ }
    else if (condition2) { /* code */ }
    else { /* code */ }
    ```
  * **`switch`:** (Java 7+ allows `String` in switch)
    ```java
    switch (value) {
        case 1: /* code */; break;
        case 2: /* code */; break;
        default: /* code */; // Optional
    }
    ```
  * **`for` loop:**
    ```java
    for (int i = 0; i < 10; i++) { /* code */ }
    // Enhanced for loop (for-each)
    for (String item : collectionOrArray) { /* code */ }
    ```
  * **`while` loop:**
    ```java
    while (condition) { /* code */ }
    ```
  * **`do-while` loop:** (Executes at least once)
    ```java
    do { /* code */ } while (condition);
    ```
  * **`break`**: Exits loop/switch.
  * **`continue`**: Skips current iteration, proceeds to next.

### **5. Methods**

```java
// Method definition
public static int addNumbers(int a, int b) {
    return a + b;
}

// Method call
int sum = addNumbers(5, 3);
```

### **6. Classes & Objects**

```java
public class Dog {
    String name; // Instance variable
    int age;

    // Constructor
    public Dog(String name, int age) {
        this.name = name; // 'this' refers to the current object's instance variable
        this.age = age;
    }

    // Instance method
    public void bark() {
        System.out.println(name + " says Woof!");
    }

    // Static method (belongs to the class, not an object)
    public static void describeAnimal() {
        System.out.println("Dogs are loyal animals.");
    }
}

// In main method or another class:
// Dog myDog = new Dog("Buddy", 3); // Create an object (instance)
// myDog.bark();
// Dog.describeAnimal();
```

### **7. Exceptions Handling**

```java
try {
    // Code that might throw an exception
    int result = 10 / 0; // ArithmeticException
} catch (ArithmeticException e) {
    System.err.println("Cannot divide by zero: " + e.getMessage());
} catch (NullPointerException e) {
    System.err.println("Null reference error: " + e.getMessage());
} catch (Exception e) { // Generic catch, handles any other exception
    System.err.println("An unexpected error occurred: " + e.getMessage());
} finally {
    // Optional: Code that always executes, regardless of exception
    System.out.println("Cleanup complete.");
}
```

### **8. Generics**

  * Enable type-safe collections and methods.
  * `<T>` (Type), `<E>` (Element), `<K, V>` (Key, Value)

<!-- end list -->

```java
List<String> names = new ArrayList<>();
// Names.add(123); // Compile-time error due to generics
```

### **9. Lambda Expressions (Java 8+)**

  * Concise way to represent a functional interface (interface with a single abstract method).
  * Syntax: `(parameters) -> expression` or `(parameters) -> { statements; }`

<!-- end list -->

```java
// Traditional anonymous inner class
// Runnable r = new Runnable() { public void run() { System.out.println("Hello"); } };
// Lambda equivalent
Runnable r = () -> System.out.println("Hello Lambda!");

// For a method taking a functional interface (e.g., Predicate)
// List<String> filteredList = myList.stream().filter(s -> s.startsWith("A")).collect(Collectors.toList());
```

### **10. Autoboxing/Unboxing**

  * Automatic conversion between primitive types and their wrapper classes.
      * `int` $\\leftrightarrow$ `Integer`
      * `boolean` $\\leftrightarrow$ `Boolean`
      * `char` $\\leftrightarrow$ `Character`
      * ...and so on for other primitives.

<!-- end list -->

```java
Integer i = 10; // Autoboxing: int to Integer
int j = i;      // Unboxing: Integer to int
```

-----

## **II. Inbuilt Data Structures (Java Collections Framework)**

### **Key Interfaces:**

  * `Collection`: Root interface for all collection classes.
  * `List`: Ordered collection (sequence). Allows duplicate elements.
  * `Set`: Collection that does not allow duplicate elements.
  * `Map`: Object that maps keys to values. Keys must be unique.
  * `Queue`: Collection designed for holding elements prior to processing (FIFO - First-In, First-Out).
  * `Deque`: Double-ended queue, supports adding/removing from both ends (can act as LIFO Stack or FIFO Queue).

### **Common Implementations & Characteristics:**

| Interface | Class                 | Order Guarantee          | Duplicates | Nulls   | Performance (Avg.)     | Thread-Safety | Notes                                       |
| :-------- | :-------------------- | :----------------------- | :--------- | :------ | :----------------------- | :------------ | :------------------------------------------ |
| `List`    | `ArrayList`           | Insertion Order          | Yes        | Yes     | Add/Get: O(1) \<br\> Remove/Insert (middle): O(N) | No            | Backed by dynamic array. Good for random access. |
|           | `LinkedList`          | Insertion Order          | Yes        | Yes     | Add/Remove (ends): O(1) \<br\> Get/Remove (middle): O(N) | No            | Backed by doubly-linked list. Good for insertions/deletions. |
| `Set`     | `HashSet`             | No specific order        | No         | Single  | Add/Remove/Contains: O(1) | No            | Uses `HashMap` internally. Fast. Unordered. |
|           | `LinkedHashSet`       | Insertion Order          | No         | Single  | Add/Remove/Contains: O(1) | No            | Maintains insertion order using linked list. |
|           | `TreeSet`             | Natural / Custom Order   | No         | No      | Add/Remove/Contains: O(log N) | No            | Uses `TreeMap` internally. Elements must be Comparable. |
| `Map`     | `HashMap`             | No specific order        | (Keys No, Values Yes) | Key: Single, Value: Yes | Put/Get: O(1) | No            | Fast. Unordered. Keys must have `hashCode()` and `equals()`. |
|           | `LinkedHashMap`       | Insertion / Access Order | (Keys No, Values Yes) | Key: Single, Value: Yes | Put/Get: O(1) | No            | Maintains insertion order (or access order for LRU cache). |
|           | `TreeMap`             | Natural / Custom Key Order | (Keys No, Values Yes) | Key: No, Value: Yes | Put/Get: O(log N) | No            | Keys must be Comparable. Sorted by keys. |
| `Queue`   | `ArrayDeque` (as Q)   | FIFO                     | Yes        | Yes     | Add/Remove (ends): O(1) | No            | More efficient than `LinkedList` as a Queue/Stack. |
|           | `PriorityQueue`       | Priority Order (min-heap) | Yes        | No      | Add/Poll: O(log N) | No            | Elements retrieved based on natural ordering or `Comparator`. |
| `Deque`   | `ArrayDeque` (as Stack) | LIFO                     | Yes        | Yes     | Push/Pop: O(1)      | No            | Preferred over `Stack` class. |

### **Thread-Safe Alternatives:**

  * **Synchronized Wrappers (from `java.util.Collections`):**
      * `Collections.synchronizedList(new ArrayList<T>())`
      * `Collections.synchronizedSet(new HashSet<T>())`
      * `Collections.synchronizedMap(new HashMap<K,V>())`
      * *Note:* These wrap the non-thread-safe collections, making each *method call* synchronized. This doesn't prevent higher-level race conditions (e.g., iterating while another thread modifies).
  * **Concurrent Collections (`java.util.concurrent`):**
      * `ConcurrentHashMap`: Highly scalable thread-safe Map.
      * `CopyOnWriteArrayList`, `CopyOnWriteArraySet`: For lists/sets where reads are frequent and writes are rare. Writes create a new underlying array.
      * `LinkedBlockingQueue`, `ArrayBlockingQueue`: Thread-safe queues for producer-consumer patterns.
      * `ConcurrentLinkedQueue`, `ConcurrentLinkedDeque`: Non-blocking thread-safe queues.
      * `ConcurrentSkipListMap`, `ConcurrentSkipListSet`: Scalable concurrent sorted Map/Set.
      * *Note:* These offer better concurrency and performance than synchronized wrappers for concurrent access.

### **Quick Tips for Coding Sessions:**

  * **Understand the problem:** Ask clarifying questions.
  * **Think out loud:** Explain your thought process, even if you make mistakes.
  * **Start with brute force:** If stuck, outline a simple, less efficient solution first.
  * **Optimize:** Then think about edge cases, time/space complexity.
  * **Test:** Mentally walk through examples, including edge cases.
  * **Readability:** Use meaningful variable names.
  * **Efficiency:** Be mindful of Big O notation.

-----

**Good luck with your interview\!**