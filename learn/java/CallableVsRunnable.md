In Java, both `Runnable` and `Callable` are interfaces used to define tasks that can be executed by a separate thread, often within an `ExecutorService`. The key differences lie in their return type and exception handling.

### `Runnable`

  * **Purpose:** `Runnable` is a functional interface introduced in Java 1.0. It represents a task that can be executed by a thread.
  * **Method Signature:** It has a single method:
    ```java
    void run();
    ```
  * **Return Value:** The `run()` method does not return any value (`void`). This means you cannot get a result directly from a `Runnable` task.
  * **Exception Handling:** The `run()` method cannot throw checked exceptions (it only throws `RuntimeException` or `Error`). If a checked exception occurs within the `run()` method, it must be caught and handled internally within the `run()` method, or re-thrown as an unchecked exception.
  * **Use Cases:**
      * When you need to execute a task asynchronously without expecting a result back.
      * When you just want to execute some code on a separate thread (e.g., background logging, UI updates).
      * When creating traditional threads: `new Thread(new MyRunnable()).start();`

**Example:**

```java
class MyRunnable implements Runnable {
    private String taskName;

    public MyRunnable(String taskName) {
        this.taskName = taskName;
    }

    @Override
    public void run() {
        System.out.println(taskName + " is running (Runnable).");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore the interrupted status
            System.err.println(taskName + " was interrupted.");
        }
        System.out.println(taskName + " finished (Runnable).");
    }
}

// Usage with ExecutorService
// ExecutorService executor = Executors.newFixedThreadPool(2);
// executor.submit(new MyRunnable("Task 1"));
// executor.submit(new MyRunnable("Task 2"));
// executor.shutdown();
```

### `Callable`

  * **Purpose:** `Callable` is a functional interface introduced in Java 5.0 as part of the `java.util.concurrent` package. It represents a task that can return a result and throw an exception.
  * **Method Signature:** It has a single method:
    ```java
    V call() throws Exception;
    ```
    Where `V` is the type of the result returned by the task.
  * **Return Value:** The `call()` method returns a value of type `V`. When a `Callable` is submitted to an `ExecutorService`, it returns a `Future` object, which can be used to retrieve the result once the task completes.
  * **Exception Handling:** The `call()` method can throw checked exceptions, which makes it more flexible for error handling compared to `Runnable`. The exceptions can be caught when retrieving the result from the `Future` object.
  * **Use Cases:**
      * When you need to perform a computation on a separate thread and retrieve its result.
      * When you need to handle exceptions thrown by the task in a structured way.
      * Often used with `ExecutorService` and `Future` for parallel processing where results are needed.

**Example:**

```java
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class MyCallable implements Callable<String> {
    private String taskName;

    public MyCallable(String taskName) {
        this.taskName = taskName;
    }

    @Override
    public String call() throws Exception {
        System.out.println(taskName + " is running (Callable).");
        Thread.sleep(1500); // Simulate some work
        if (taskName.equals("Task C")) {
            throw new RuntimeException("Error in " + taskName);
        }
        System.out.println(taskName + " finished (Callable).");
        return "Result from " + taskName;
    }
}

// Usage with ExecutorService
public class CallableExample {
    public static void main(String[] args) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        Future<String> futureA = executor.submit(new MyCallable("Task A"));
        Future<String> futureB = executor.submit(new MyCallable("Task B"));
        Future<String> futureC = executor.submit(new MyCallable("Task C")); // Will throw an exception

        try {
            System.out.println(futureA.get()); // Blocks until result is available
            System.out.println(futureB.get());
            System.out.println(futureC.get()); // Will throw ExecutionException due to RuntimeException in call()
        } catch (Exception e) {
            System.err.println("Caught exception: " + e.getMessage());
        } finally {
            executor.shutdown();
        }
    }
}
```

### Summary of Differences:

| Feature           | `Runnable`                   | `Callable`                           |
| :---------------- | :--------------------------- | :----------------------------------- |
| **Method** | `run()`                      | `call()`                             |
| **Return Value** | `void` (no return value)     | `V` (returns a typed result)         |
| **Exceptions** | Cannot throw checked exceptions | Can throw checked exceptions         |
| **Introduced In** | Java 1.0                     | Java 5.0                             |
| **Typical Use** | Fire-and-forget tasks        | Tasks requiring a result or specific exception handling |
| **Execution** | Executed by `Thread` or `ExecutorService.submit(Runnable)` | Executed by `ExecutorService.submit(Callable)` (returns `Future`) |

In modern Java concurrent programming, `Callable` is generally preferred when you need to retrieve a result from a task or when the task might throw a checked exception, leveraging the `ExecutorService` and `Future` framework for more robust and flexible asynchronous execution.