Here's a concise handout on common Java Functional Interfaces, designed for quick reference in a coding interview.

---

# **Java Functional Interfaces Cheat Sheet**

**Purpose:** A *functional interface* is an interface that has **exactly one abstract method**. They serve as target types for lambda expressions and method references, enabling functional programming in Java 8+.

**`java.util.function` package:** Most standard functional interfaces are found here.

---

## **I. Core Functional Interfaces**

| Interface            | Abstract Method Signature | Parameters & Return | When to Use                                  | Lambda Example                                |
| :------------------- | :------------------------ | :------------------ | :------------------------------------------- | :-------------------------------------------- |
| **`Runnable`** (from `java.lang`) | `void run()`              | `()` $\to$ `void`   | For tasks that don't take input or return output (e.g., threads). | `() -> System.out.println("Run!")`             |
| **`Comparator<T>`** (from `java.util`) | `int compare(T o1, T o2)` | `(T, T)` $\to$ `int` | For custom sorting logic.                     | `(s1, s2) -> s1.length() - s2.length()`     |
| **`Predicate<T>`** | `boolean test(T t)`       | `(T)` $\to$ `boolean` | For checking a condition on an object. Used in `filter()` operations. | `s -> s.startsWith("A")`                      |
| **`Consumer<T>`** | `void accept(T t)`        | `(T)` $\to$ `void`  | For performing an action on an object. Used in `forEach()` operations. | `n -> System.out.println(n * 2)`              |
| **`Function<T, R>`** | `R apply(T t)`            | `(T)` $\to$ `R`     | For transforming an object from one type to another. Used in `map()` operations. | `s -> s.toUpperCase()`                        |
| **`Supplier<T>`** | `T get()`                 | `()` $\to$ `T`      | For generating or supplying an object.        | `() -> new ArrayList<String>()`               |
| **`UnaryOperator<T>`** | `T apply(T t)` (extends `Function<T, T>`) | `(T)` $\to$ `T`     | Specialization of `Function` where input and output types are the same. | `n -> n * n`                                  |
| **`BinaryOperator<T>`** | `T apply(T t1, T t2)` (extends `BiFunction<T, T, T>`) | `(T, T)` $\to$ `T`  | Specialization of `BiFunction` where all three types are the same. Used in `reduce()` operations. | `(a, b) -> a + b`                             |
| **`BiPredicate<T, U>`** | `boolean test(T t, U u)`  | `(T, U)` $\to$ `boolean` | For checking a condition on two objects.       | `(s1, s2) -> s1.equals(s2)`                   |
| **`BiConsumer<T, U>`** | `void accept(T t, U u)`   | `(T, U)` $\to$ `void` | For performing an action on two objects.       | `(k, v) -> System.out.println(k + ":" + v)`   |
| **`BiFunction<T, U, R>`** | `R apply(T t, U u)`       | `(T, U)` $\to$ `R`  | For transforming two objects into one new object. | `(a, b) -> a + " " + b`                       |

---

## **II. Primitive Specializations**

To avoid auto-boxing/unboxing overhead for primitive types (e.g., `int`, `long`, `double`), there are specialized functional interfaces.

| Primitive FI              | Abstract Method Signature   | Parameters & Return | Example                                     |
| :------------------------ | :-------------------------- | :------------------ | :------------------------------------------ |
| `IntPredicate`            | `boolean test(int value)`   | `(int)` $\to$ `boolean` | `i -> i % 2 == 0`                           |
| `LongConsumer`            | `void accept(long value)`   | `(long)` $\to$ `void`   | `l -> System.out.println(l)`                |
| `DoubleFunction<R>`       | `R apply(double value)`     | `(double)` $\to$ `R`    | `d -> String.valueOf(d)`                    |
| `ToIntFunction<T>`        | `int applyAsInt(T value)`   | `(T)` $\to$ `int`       | `s -> s.length()`                           |
| `IntBinaryOperator`       | `int applyAsInt(int l, int r)` | `(int, int)` $\to$ `int` | `(a, b) -> a * b`                           |
| *(And many more, e.g., `LongPredicate`, `DoubleConsumer`, `IntToLongFunction`, etc.)* | | | |

---
**Key Takeaways for Interviews:**

* **Definition:** Interface with a single abstract method.
* **Purpose:** Enable lambdas and method references.
* **Common Use Cases:** Filtering (`Predicate`), transforming (`Function`), consuming (`Consumer`), supplying (`Supplier`), sorting (`Comparator`), running tasks (`Runnable`).
* **Primitive Versions:** Use `IntX`, `LongX`, `DoubleX` versions to avoid performance overhead from boxing/unboxing when dealing with primitive types.

---