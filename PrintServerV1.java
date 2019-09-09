import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

public class PrintServerV1 implements Runnable {
    private final static Queue<String> requests = new LinkedList<String>();
    private static Lock printSeverV1Lock = new ReentrantLock();
    private Condition sufficientPrintCondition;

    public PrintServerV1() {
        new Thread(this).start();
//        printSeverV1Lock = new ReentrantLock();
        sufficientPrintCondition = printSeverV1Lock.newCondition();
    }

    public void printRequest(String s) {
        printSeverV1Lock.lock();
        try{
            System.out.println("client add print request: " + s);
            requests.add(s);
            sufficientPrintCondition.signalAll();
        } finally{
            printSeverV1Lock.unlock();
        }
    }

    public void printRun() throws InterruptedException
    {
        printSeverV1Lock.lock();
        try{
            while(requests.size() == 0)
            {
                System.out.println("need tp wait ....");
                sufficientPrintCondition.await();
            }
            for(int i = 0; i < requests.size(); i++)
            {
                realPrint(requests.remove());
            }
        } finally{
            printSeverV1Lock.unlock();
        }
    }

    public void run()
    {
        try{
            printRun();
            Thread.sleep(1);
        }
        catch (InterruptedException exception)
        {

        }
    }

//    public void run() throws InterruptedException
//    {
//        printSeverV1Lock.lock();
//        try{
//            while(requests.size() == 0)
//            {
//                System.out.println("need tp wait ....");
//                sufficientPrintCondition.await();
//            }
//            for(int i = 0; i < requests.size(); i++)
//            {
//                realPrint(requests.remove());
//            }
//        } finally{
//            printSeverV1Lock.unlock();
//        }
//
//    }


    private void realPrint(String s) {
// do the real work of outputting the string to the screen
        System.out.println("print out: " + s);
    }


    public static void main(String[] args)
    {
        PrintServerV1 client_1 = new PrintServerV1();
        new Thread(client_1).start();
        client_1.printRequest("Hello");

        PrintServerV1 client_2 = new PrintServerV1();
        new Thread(client_2).start();
        client_2.printRequest("Hi");

        PrintServerV1 client_3 = new PrintServerV1();
        new Thread(client_3).start();
        client_3.printRequest("Howdy");

        PrintServerV1 manager = new PrintServerV1();
        new Thread(manager).start();
        manager.run();
    }
}


