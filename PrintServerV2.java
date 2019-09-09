import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

public class PrintServerV2 implements Runnable{
    private final static Queue<String> requests = new LinkedList<String>();
    private static Lock printServerV2Lock = new ReentrantLock();
 //   private Lock printServerV2Lock;
    private Condition sufficientPrintCondition;

    public PrintServerV2() {
        new Thread(this).start();
 //       printServerV2Lock = new ReentrantLock();
        sufficientPrintCondition = printServerV2Lock.newCondition();
    }

    public synchronized void printRequest(String s) {

        printServerV2Lock.lock();
        try{
            System.out.println("client add print request: " + s);
            requests.add(s);
            sufficientPrintCondition.notifyAll();
        } finally{
            printServerV2Lock.unlock();
        }
    }

    public synchronized void printRun() throws InterruptedException
    {

        printServerV2Lock.lock();
        try{
            while(requests.size() == 0)
            {
                System.out.println("need tp wait ....");
                sufficientPrintCondition.wait();
            }
            for(int i = 0; i < requests.size(); i++)
            {
                realPrint(requests.remove());
            }
        } finally{
            printServerV2Lock.unlock();
        }
    }

    public void run()
    {
        try{
            printRun();
  //          Thread.sleep(1);
        }
        catch (InterruptedException exception)
        {

        }
    }


    private void realPrint(String s) {
// do the real work of outputting the string to the screen
        System.out.println("print out: " + s);
    }


    public static void main(String[] args)
    {
        PrintServerV2 client_1 = new PrintServerV2();
        new Thread(client_1).start();
        client_1.printRequest("Hello");

        PrintServerV2 client_2 = new PrintServerV2();
        new Thread(client_2).start();
        client_2.printRequest("Hi");

        PrintServerV2 client_3 = new PrintServerV2();
        new Thread(client_3).start();
        client_3.printRequest("Howdy");

        PrintServerV2 manager = new PrintServerV2();
        new Thread(manager).start();
        manager.run();
    }

}
