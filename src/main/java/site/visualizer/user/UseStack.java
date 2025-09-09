package site.visualizer.user;

import site.visualizer.model.structure.stack.LockBasedStack;

public class UseStack {

    private static final int testCap = 5_000_000;
    public static void main(String[] args) throws InterruptedException {

//        BasicStack stack = new BasicStack(testCap);
//        SyncStack stack = new SyncStack(testCap);
//        AdvSyncStack stack = new AdvSyncStack(testCap);
        LockBasedStack stack = new LockBasedStack(testCap);

        Runnable pusherOne = () -> {
            for (int i=0; i<testCap/2; i++) {
                try {
                    stack.push(i);
                } catch (Exception e) {
                    System.out.println("\u001B[31m"+e.getMessage()+"\u001B[0m");
                }
            }
        };

        Runnable pusherTwo = () -> {
            for (int i=testCap/2; i<testCap; i++) {
                try {
                    stack.push(i);
                } catch (Exception e) {
                    System.out.println("\u001B[31m"+e.getMessage()+"\u001B[0m");
                }
            }
        };

        Runnable popperOne = () -> {
            for (int i=0; i<testCap/2; i++) {
                try {
                    stack.pop();
                } catch (InterruptedException e) {
                    System.out.println("\u001B[31m"+e.getMessage()+"\u001B[0m");
                }
            }
        };

        Runnable popperTwo = () -> {
            for (int i=testCap/2; i<testCap; i++) {
                try {
                    stack.pop();
                } catch (InterruptedException e) {
                    System.out.println("\u001B[31m"+e.getMessage()+"\u001B[0m");
                }
            }
        };

        Thread producerThread = new Thread(pusherOne);
        Thread producerThreadTwo = new Thread(pusherTwo);
        Thread consumerThread = new Thread(popperOne);
        Thread consumerThreadTwo = new Thread(popperTwo);

        var start = System.currentTimeMillis();

        producerThread.start();
        consumerThread.start();
        producerThreadTwo.start();
        consumerThreadTwo.start();

        producerThread.join();
        consumerThread.join();
        producerThreadTwo.join();
        consumerThreadTwo.join();

        var end = System.currentTimeMillis();
        var totalTime = end-start;

        System.out.println("Stack size: "+stack.size());
        System.out.println("Runtime: "+totalTime+" ms");


    }
}
