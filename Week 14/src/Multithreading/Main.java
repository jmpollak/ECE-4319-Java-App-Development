package Multithreading;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main
{
    public static void main(String[] args)
    {
        // Thread Implementation
        // Creating and naming my Threads
        NewThread t1 = new NewThread();
        t1.setName("MyThread-1");

        NewThread t2 = new NewThread();
        t2.setName("MyThread-2");

        // Starting the threads
        t1.start();
        t2.start();

        //Executor Service Implementation
        // Initialize Executor Service
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        // Starting the service
        executorService.execute(new RunnableTask(1));
        executorService.execute(new RunnableTask(2));
    }
}
