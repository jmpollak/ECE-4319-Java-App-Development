package Multithreading;

public class RunnableTask implements Runnable
{
    int id;
    public RunnableTask(int id)
    {
        this.id = id;
    }
    @Override
    public void run()
    {
        int i = 0;
        while (true)
        {
            System.out.println(id + ": New Runnable is running..." + i++);
            try
            {
                // Wait for one sec so it doesn't print too fast
                Thread.sleep(1000);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }
}
