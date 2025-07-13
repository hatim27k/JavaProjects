import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

class Dog implements Runnable
{
    @Override
    public void run()
    {
        long random = ThreadLocalRandom.current().nextLong(1, 1000);
        
        try
        {
            Thread.sleep(random);
            String s = String.format("%s with delay as %dms with thread %d", "Dog is running ", random, Thread.currentThread().getId());
            System.out.println(s);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}

class Cat implements Runnable
{
    @Override
    public void run()
    {
        long random = ThreadLocalRandom.current().nextLong(1, 1000);
        
        try
        {
            Thread.sleep(random);
            String s = String.format("%s with delay as %dms with thread %d", "Cat is running ", random, Thread.currentThread().getId());
            System.out.println(s);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}

// Main class should be named 'Solution' and should not be public.
class Solution {
    public static void main(String[] args) {
        System.out.println("Hello, World");
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.execute(new Dog());
        executorService.execute(new Cat());
        executorService.execute(() -> {
            try
            {
                long random = ThreadLocalRandom.current().nextLong(1, 1000);
                Thread.sleep(random);
                String s = String.format("%s with delay as %dms with thread %d", "Mouse is running ", random, Thread.currentThread().getId());
                System.out.println(s);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }}
        );
        executorService.shutdown();
        try
        {
            executorService.awaitTermination(10, TimeUnit.SECONDS);        
        }
        catch(InterruptedException e)
        {
             System.out.println(e.getMessage());
        }
        finally
        {
            System.out.println("Finally over");
        }
    }
}
