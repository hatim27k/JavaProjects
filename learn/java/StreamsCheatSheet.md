Okay, here's a concise handout specifically focused on Java Stream Pipelining and Lambda Expressions, designed for quick reference in a coding interview.

-----

# **Java 8+ Streams & Lambdas Cheat Sheet**

## **I. Lambda Expressions (Concise Anonymous Functions)**

**Purpose:** Provide a compact way to represent instances of *functional interfaces* (interfaces with a single abstract method). Enables functional programming paradigms in Java.

**Syntax:** `(parameters) -> expression` OR `(parameters) -> { statements; }`

| Parameters             | Body                 | Example Usage (Functional Interface)               |
| :--------------------- | :------------------- | :------------------------------------------------- |
| `()` (no params)       | `expression`         | `() -> System.out.println("Hello")` (Runnable)     |
| `x` (single param)     | `expression`         | `x -> x * x` (Function\<Integer, Integer\>)          |
| `(x, y)` (multiple)    | `expression`         | `(x, y) -> x + y` (BinaryOperator\<Integer\>)        |
| `(x)` (explicit type)  | `expression`         | `(String s) -> s.toUpperCase()` (Function\<String, String\>) |
| `{ statements; }`      | Multiple statements | `s -> { System.out.println(s); return s.length(); }` |

**Key Concept: Functional Interface**
An interface with exactly one abstract method. Lambda expressions are syntactic sugar for creating instances of these interfaces.

  * **Examples:** `Runnable`, `Callable`, `Comparator`, `Predicate`, `Consumer`, `Function`, `Supplier`.

**Method References (Shorthand Lambdas)**
A more compact syntax for lambdas that simply call an existing method.

| Type of Reference        | Syntax                 | Example Lambda           | Method Reference       |
| :----------------------- | :--------------------- | :----------------------- | :--------------------- |
| Static Method            | `ClassName::methodName` | `s -> ClassName.method(s)` | `Integer::parseInt`    |
| Instance Method          | `object::methodName`   | `s -> myObject.method(s)`  | `System.out::println`  |
| Arbitrary Object of Type | `ClassName::methodName` | `s -> s.length()`        | `String::length`       |
| Constructor              | `ClassName::new`       | `() -> new ArrayList<>()`  | `ArrayList::new`       |

-----

## **II. Java Stream Pipelining (Declarative Data Processing)**

**Purpose:** Provide a sequence of elements supporting sequential and parallel aggregate operations. Enables a declarative style of processing data.

**Core Idea: The Pipeline**
A stream pipeline consists of:

1.  **Source:** A collection, array, I/O channel, generator, etc. (e.g., `list.stream()`, `Arrays.stream(arr)`).
2.  **Intermediate Operations (0 or more):** Transform a stream into another stream. They are **lazy** and build the pipeline.
3.  **Terminal Operation (Exactly 1):** Consumes the stream, produces a result, or a side-effect. Triggers pipeline execution.

### **1. Intermediate Operations (Returns a `Stream`, Lazy)**

These operations are chained to form the processing pipeline.

| Method          | Purpose                         | Example                                            |
| :-------------- | :------------------------------ | :------------------------------------------------- |
| `filter(Predicate<T>)` | Selects elements matching a condition. | `stream.filter(s -> s.startsWith("A"))`           |
| `map(Function<T, R>)` | Transforms elements to a new type/value. | `stream.map(String::toUpperCase)`                 |
| `flatMap(Function<T, Stream<R>>)` | Transforms each element into a stream and flattens them into a single stream. | `stream.flatMap(list -> list.stream())`         |
| `distinct()`    | Returns a stream with unique elements. | `stream.distinct()`                                |
| `sorted()`      | Sorts elements (natural order). | `stream.sorted()`                                  |
| `sorted(Comparator<T>)` | Sorts elements using a custom `Comparator`. | `stream.sorted(Comparator.reverseOrder())`         |
| `limit(long n)` | Truncates the stream to `n` elements. | `stream.limit(5)`                                  |
| `skip(long n)`  | Skips the first `n` elements.  | `stream.skip(2)`                                   |
| `peek(Consumer<T>)` | Performs an action on each element as it passes, useful for debugging. | `stream.peek(System.out::println)`               |

### **2. Terminal Operations (Returns non-`Stream`, Eager, Consumes Stream)**

These operations trigger the execution of the stream pipeline and produce a final result or side-effect. A stream can only be consumed once.

| Method                       | Purpose                               | Example & Return Type                                       |
| :--------------------------- | :------------------------------------ | :---------------------------------------------------------- |
| `forEach(Consumer<T>)`       | Performs an action for each element (side-effect). | `stream.forEach(System.out::println)` (void)              |
| `collect(Collector<T, A, R>)` | Gathers elements into a new collection or summary. | `stream.collect(Collectors.toList())` (List), \<br\> `stream.collect(Collectors.toMap(k -> k, String::length))` (Map), \<br\> `stream.collect(Collectors.groupingBy(String::length))` (Map), \<br\> `stream.collect(Collectors.counting())` (Long) |
| `reduce(identity, accumulator)` | Combines elements into a single result. | `stream.reduce(0, Integer::sum)` (Integer)                 |
| `count()`                    | Returns the number of elements.        | `stream.count()` (long)                                     |
| `min(Comparator<T>)`         | Finds the minimum element.             | `stream.min(Comparator.naturalOrder())` (Optional\<T\>)     |
| `max(Comparator<T>)`         | Finds the maximum element.             | `stream.max(Comparator.naturalOrder())` (Optional\<T\>)     |
| `anyMatch(Predicate<T>)`     | Checks if any element matches.         | `stream.anyMatch(s -> s.contains("a"))` (boolean)         |
| `allMatch(Predicate<T>)`     | Checks if all elements match.          | `stream.allMatch(s -> s.length() > 0)` (boolean)          |
| `noneMatch(Predicate<T>)`    | Checks if no elements match.           | `stream.noneMatch(s -> s.startsWith("Z"))` (boolean)      |
| `findFirst()`                | Returns the first element found.       | `stream.findFirst()` (Optional\<T\>)                        |
| `findAny()`                  | Returns any element (useful for parallel streams). | `stream.findAny()` (Optional\<T\>)                        |

**Important: `Optional<T>`**
A container object that may or may not contain a non-null value. Used by terminal operations (e.g., `min`, `max`, `findFirst`, `reduce` without identity) that might not return a value.

  * **Methods:** `isPresent()`, `isEmpty()` (Java 11+), `get()`, `orElse(T other)`, `orElseThrow()`, `ifPresent(Consumer<T>)`.

### **Consolidated Example Pipeline**

```java
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StreamPipelineExample {
    public static void main(String[] args) {
        List<String> words = Arrays.asList("apple", "banana", "cat", "dog", "elephant", "bat");

        // Example Pipeline:
        // 1. Filter words starting with 'b'
        // 2. Convert to uppercase
        // 3. Remove duplicates
        // 4. Sort alphabetically
        // 5. Collect into a Map: key=word, value=length
        Map<String, Integer> processedWords = words.stream() // Source
            .filter(s -> s.startsWith("b"))                     // Intermediate
            .map(String::toUpperCase)                           // Intermediate
            .distinct()                                         // Intermediate
            .sorted()                                           // Intermediate
            .collect(Collectors.toMap(
                word -> word,       // Key mapper
                String::length      // Value mapper
            ));                                                 // Terminal

        System.out.println(processedWords); // Output: {BANANA=6, BAT=3}
    }
}
```

-----

**Key Takeaways for Interviews:**

  * **Lambdas:** Concise syntax for functional interfaces, improve code readability.
  * **Streams:** Declarative, chainable operations (filter, map, collect, etc.), lazy evaluation for intermediate steps, support parallel processing.
  * **`Optional`:** Crucial for handling potentially absent values cleanly.

-----