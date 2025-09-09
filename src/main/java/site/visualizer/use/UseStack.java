package site.visualizer.use;

import site.visualizer.model.structure.stack.AdvSyncStack;
import site.visualizer.model.structure.stack.BasicStack;
import site.visualizer.model.structure.stack.LockBasedStack;
import site.visualizer.model.structure.stack.Stack;
import site.visualizer.model.type.Brick;

import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public class UseStack {
    private static final Scanner scan = new Scanner(System.in);

    private static int toProduce;
    private static int toConsume = Integer.MAX_VALUE;
    private static int producerCount;
    private static int consumerCount;
    private static Stack<Brick> stack;

    public static void main(String[] args) throws InterruptedException {

        // gathering input from user to configure
        System.out.print("how many bricks are there: ");
        toProduce = scan.nextInt();

        while (true) {
            System.out.print("how many bricks are needed: ");
            toConsume = scan.nextInt();
            if (toConsume <= toProduce) break;
            System.out.println("needed amount cannot exceed the available amount ");
        }

        System.out.print("how many workers putting to the stack: ");
        producerCount = scan.nextInt();
        System.out.print("how many workers taking from the stack: ");
        consumerCount = scan.nextInt();
        System.out.print("what is the max height the stack can be: ");
        int stackHeight = scan.nextInt();
        System.out.println("what is the stack type;");
        System.out.print("basic / sync / lockBased : ");
        var stackType = scan.next();

        switch (stackType) {
            case "basic":
                stack = new BasicStack<>(stackHeight);
                break;
            case "sync":
                stack = new AdvSyncStack<>(stackHeight);
                break;
            case "lockBased":
                stack = new LockBasedStack<>(stackHeight);
                break;
            default:
                System.out.println("invalid stack type");
                System.exit(1);
        }

        Thread[] producers = getProducerThreadPool();
        Thread[] consumers = getConsumerThreadPool();

        var start = System.currentTimeMillis();

        // starting
        for (Thread producer: producers) producer.start();
        for (Thread consumer: consumers) consumer.start();
        // joining
        for (Thread producer: producers) producer.join();
        for (Thread consumer: consumers) consumer.join();

        var end = System.currentTimeMillis();
        var totalTime = end-start;

        System.out.println("stack size: "+stack.size());
        System.out.println("Runtime: "+totalTime+" ms");
    }

    private static Runnable getProducerRunnable(int quota) {
        return () -> {
            for (int i=0; i<quota; i++) {
                try {
                    var brick = new Brick();
                    stack.push(brick);
                    System.out.println(Thread.currentThread().getName() + " added brick");
                    System.out.println("stack size " + stack.size());
                } catch (IllegalStateException e) {
                    System.out.println("tried to push when full");
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }
            }
        };
    }

    private static Runnable getConsumerRunnable(int quota) {
        return () -> {
            for (int i=0; i<quota; i++) {
                try {
                    stack.pop();
                    System.out.println(Thread.currentThread().getName() + " removed brick");
                    System.out.println("stack size " + stack.size());
                } catch (NoSuchElementException e) {
                    System.out.println("tried to pop when empty");
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }
            }
        };
    }

    private static Thread[] getProducerThreadPool() {
        Thread[] pool = new Thread[producerCount];

        int baseForProducers = toProduce / producerCount;
        int extraForProducers = toProduce % producerCount;

        for (int i=0; i<producerCount; i++) {
            int quota = baseForProducers;
            if (i < extraForProducers) quota += extraForProducers;

            pool[i] = new Thread(getProducerRunnable(quota), "Producer "+i);
        }

        return pool;
    };

    private static Thread[] getConsumerThreadPool() {
        Thread[] pool = new Thread[consumerCount];

        int baseForConsumers = toConsume / consumerCount;
        int extraForConsumers = toConsume % consumerCount;

        for (int i=0; i<consumerCount; i++) {
            int quota = baseForConsumers;
            if (i < extraForConsumers) quota += extraForConsumers;

            pool[i] = new Thread(getConsumerRunnable(quota), "Consumer "+i);
        }

        return pool;
    };
}
