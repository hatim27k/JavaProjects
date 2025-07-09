import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Main {
    public static void main(String[] args) {
        // hello world
        System.out.println("Hello, World! Thread ID: " + Thread.currentThread().threadId());
        
        // This is a simple Java program that prints "Hello, World!" to the console.
        // It serves as a basic example of how to write and run a Java application.

        Demo demo = new Demo();
        // Thread thread = new Thread(demo);
        // thread.start();
        
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            Thread thread = new Thread(() -> {
                System.out.println("Thread is running: Thread ID: " + Thread.currentThread().threadId());
                demo.incrementCount();
                try {
                    long sleepTime = ThreadLocalRandom.current().nextLong(1, 2000);
                    Thread.sleep(sleepTime); // Simulate some work
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                demo.decrementCount();
                demo.useLock();
                demo.printCount();
            });

            threads.add(thread);
            thread.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // After all threads have completed, print the final count
        demo.printCount();
    }
}



