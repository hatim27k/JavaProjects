In Java, exceptions are events that disrupt the normal flow of a program. They are part of a hierarchy that starts with the `Throwable` class. `Throwable` has two main direct subclasses: `Error` and `Exception`.

The distinction between **checked** and **unchecked** exceptions is crucial for how Java handles error reporting and forces developers to write robust code.

-----

## **1. Checked Exceptions**

  * **Definition:** These are exceptions that the Java compiler **forces you to handle**. If a method you call is declared to throw a checked exception, your code *must* either:
    1.  **Catch it:** Using a `try-catch` block.
    2.  **Declare it:** Using the `throws` keyword in your method signature, effectively passing the responsibility of handling it to the calling method.
  * **Hierarchy:** They are subclasses of `java.lang.Exception`, but **NOT** subclasses of `java.lang.RuntimeException`.
  * **When they occur:** Checked exceptions typically represent **recoverable conditions** that are often outside the direct control of the program itself. These are situations where a well-written application *should* anticipate and potentially recover from. They usually involve external factors.
  * **Examples:**
      * `IOException` (e.g., `FileNotFoundException` when trying to open a file that doesn't exist, network connection issues).
      * `SQLException` (problems interacting with a database).
      * `ClassNotFoundException` (when trying to load a class dynamically that isn't found).
      * `InterruptedException` (when a thread is interrupted while waiting or sleeping).
      * `ParseException` (when parsing input that doesn't conform to an expected format).
  * **Why they are "checked":** The compiler checks for their handling at compile time. If you don't handle or declare them, your code won't compile. This forces developers to explicitly think about and design solutions for these potential problems.

**Example of Checked Exception Handling:**

```java
import java.io.FileReader;
import java.io.IOException;

public class CheckedExceptionExample {

    public static void readFile(String filePath) throws IOException { // Declaring it
        FileReader reader = null;
        try {
            reader = new FileReader(filePath); // This can throw FileNotFoundException (a type of IOException)
            int charCode;
            while ((charCode = reader.read()) != -1) { // This can throw IOException
                System.out.print((char) charCode);
            }
        } catch (IOException e) { // Catching it
            System.err.println("Error reading file: " + e.getMessage());
            // You might log the error, display a user-friendly message, or retry.
        } finally {
            if (reader != null) {
                try {
                    reader.close(); // This can also throw IOException
                } catch (IOException e) {
                    System.err.println("Error closing reader: " + e.getMessage());
                }
            }
        }
    }

    // Alternatively, the main method could also declare throws IOException
    public static void main(String[] args) {
        readFile("non_existent_file.txt");
        System.out.println("\nProgram finished.");
    }
}
```

-----

## **2. Unchecked Exceptions**

  * **Definition:** These are exceptions that the Java compiler **does not force you to handle**. You can catch them if you want, but you are not required to.
  * **Hierarchy:** They are subclasses of `java.lang.RuntimeException` or `java.lang.Error`.
  * **When they occur:** Unchecked exceptions typically represent **programming errors** or logical flaws in the code that are usually **not recoverable** at runtime. They indicate issues that should generally be fixed by correcting the code logic rather than by catching the exception.
  * **Examples:**
      * `NullPointerException` (attempting to use an object reference that is `null`).
      * `ArrayIndexOutOfBoundsException` (accessing an array with an invalid index).
      * `ArithmeticException` (e.g., division by zero).
      * `IllegalArgumentException` (a method has been passed an illegal or inappropriate argument).
      * `ClassCastException` (attempting to cast an object to an incompatible type).
      * `StackOverflowError` (a type of `Error`, indicating a serious JVM problem, typically infinite recursion).
      * `OutOfMemoryError` (a type of `Error`, indicating the JVM ran out of memory).
  * **Why they are "unchecked":** The compiler doesn't check for their handling because they are assumed to be due to programmer mistakes. Forcing handling of every `NullPointerException` or `ArrayIndexOutOfBoundsException` would clutter code significantly and wouldn't solve the underlying bug.

**Example of Unchecked Exception (and why you often don't catch them):**

```java
public class UncheckedExceptionExample {

    public static void divide(int a, int b) {
        // This method can throw ArithmeticException if b is 0.
        // The compiler does NOT force us to put a try-catch or declare 'throws ArithmeticException'.
        int result = a / b;
        System.out.println("Result: " + result);
    }

    public static void accessArray() {
        String[] names = {"Alice", "Bob"};
        // This will throw ArrayIndexOutOfBoundsException if index is 2
        System.out.println(names[2]); // Compiler won't complain here
    }

    public static void main(String[] args) {
        // Calling a method that might throw an unchecked exception
        divide(10, 2);
        // divide(10, 0); // This line would cause an ArithmeticException at runtime

        // accessArray(); // This line would cause an ArrayIndexOutOfBoundsException at runtime

        String str = null;
        // System.out.println(str.length()); // This line would cause a NullPointerException at runtime

        // While you *can* catch unchecked exceptions for logging or graceful degradation (e.g., a web service)
        // it's generally better to fix the root cause.
        try {
            divide(10, 0);
        } catch (ArithmeticException e) {
            System.err.println("Caught an unchecked exception (ArithmeticException): " + e.getMessage());
            // In a real application, you might log this error.
            // But ideally, the 'divide' method itself would prevent division by zero before it happens.
        }
    }
}
```

-----

### **Key Differences Summary:**

| Feature           | Checked Exceptions                                 | Unchecked Exceptions                                     |
| :---------------- | :------------------------------------------------- | :------------------------------------------------------- |
| **Compiler Check** | Yes, compiler forces handling/declaration.         | No, compiler does not force handling/declaration.        |
| **Inheritance** | Subclasses of `Exception` (but not `RuntimeException`). | Subclasses of `RuntimeException` or `Error`.             |
| **Purpose/Cause** | For recoverable conditions; external factors; anticipated problems. | For programming errors/logic flaws; usually not recoverable. |
| **Handling** | Must be handled with `try-catch` or `throws`.     | Optional to catch; typically prevented by code correction. |
| **Examples** | `IOException`, `SQLException`, `InterruptedException` | `NullPointerException`, `ArithmeticException`, `IndexOutOfBoundsException` |

Choosing between checked and unchecked exceptions for custom exceptions often depends on whether you expect the *caller* of your code to be able to reasonably recover from the problem. If so, make it checked. If it's a bug in your own code's usage or logic, make it unchecked.