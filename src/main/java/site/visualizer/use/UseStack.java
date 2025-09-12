package site.visualizer.use;

import site.visualizer.StackPerformanceMetrics;
import site.visualizer.model.Configurable;
import site.visualizer.model.ThreadType;
import site.visualizer.model.structure.stack.AdvSyncStack;
import site.visualizer.model.structure.stack.LockBasedStack;
import site.visualizer.model.structure.stack.Stack;
import site.visualizer.model.type.Brick;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class UseStack {
    private static final Scanner scan = new Scanner(System.in);
    private static final Configurable testData = new Configurable();

    private static final ReentrantLock prodLock = new ReentrantLock();
    private static final ReentrantLock conLock = new ReentrantLock();

    private static final StackPerformanceMetrics<Brick> syncStackMetrics = new StackPerformanceMetrics<>("Synchronised Stack");
    private static final StackPerformanceMetrics<Brick> lockStackMetrics = new StackPerformanceMetrics<>("Lock Based Stack");


    public static void main(String[] args) throws InterruptedException {

        // gathering input from user to configure
        System.out.print("how many items needed to be produced & consumed: ");
        var toProduce = scan.nextInt();
        System.out.print("how many producer threads: ");
        var producerCount = scan.nextInt();
        System.out.print("how many consumer threads: ");
        var consumerCount = scan.nextInt();
        System.out.print("max no. of elements the data structure can hold: ");
        var stackHeight = scan.nextInt();

        testData.setCap(stackHeight)
                .setToProduce(toProduce)
                .setToConsume(toProduce)
                .setProducerCount(producerCount)
                .setConsumerCount(consumerCount);

        // test stacks
        AdvSyncStack<Brick> syncStack = new AdvSyncStack<>(testData.getCap());
        LockBasedStack<Brick> lockBasedStack = new LockBasedStack<>(testData.getCap());

        runThreads(syncStack, syncStackMetrics);
        runThreads(lockBasedStack, lockStackMetrics);

        System.out.println(syncStackMetrics);
        System.out.println(lockStackMetrics);

    }

    private static <T> void runThreads(Stack<T> stack, StackPerformanceMetrics<T> metrics) throws InterruptedException {
        // process for sync stack
        Thread[] producersForSyncStack = getConfiguredThreadPool(
                ThreadType.PRODUCER, stack,metrics);
        Thread[] consumersForSyncStack = getConfiguredThreadPool(
                ThreadType.CONSUMER, stack,metrics);

        var start = System.nanoTime();
        // starting
        for (Thread producer: producersForSyncStack) producer.start();
        for (Thread consumer: consumersForSyncStack) consumer.start();
        // joining
        for (Thread producer: producersForSyncStack) producer.join();
        for (Thread consumer: consumersForSyncStack) consumer.join();
        var end = System.nanoTime();

        metrics.setTotalTime((end-start) / 1_000_000);
    }

    @SuppressWarnings("unchecked")
    private static <T> Runnable getProducerRunnable(int quota, Stack<T> stack, StackPerformanceMetrics<T> metrics) {
        return () -> {
            var opsBegin = System.nanoTime();
            for (int i=0; i<quota; i++) {
                try {
                    var obj = (T) new Object();
                    prodLock.lock();
                    try {
                        stack.push(obj);
                        metrics.addToProducedData(obj);
                    }
                    finally {
                        prodLock.unlock();
                    }
                    metrics.incrementProducedCount();
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }
            }
            var opsEnd = System.nanoTime();
            var latencyForThread = (opsEnd-opsBegin) / quota;
            metrics.addLatency(latencyForThread);
        };
    }

    private static <T> Runnable getConsumerRunnable(int quota, Stack<T> stack, StackPerformanceMetrics<T> metrics) {
        return () -> {
            var opsBegin = System.nanoTime();
            for (int i=0; i<quota; i++) {
                try {
                    conLock.lock();
                    try {
                        var obj = stack.pop();
                        metrics.addToConsumedData(obj);
                    } finally {
                        conLock.unlock();
                    }
                    metrics.incrementConsumedCount();
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }
            }
            var opsEnd = System.nanoTime();
            var latencyForThread = (opsEnd-opsBegin) / quota;
            metrics.addLatency(latencyForThread);
        };
    }

    private static <T> Thread[] getConfiguredThreadPool(
            ThreadType threadType,
            Stack<T> stack,
            StackPerformanceMetrics<T> metrics
    ) {
        ArrayList<Thread> threadPool = new ArrayList<>();
        if (threadType == ThreadType.PRODUCER) {

            int baseForProducers = testData.getToProduce() / testData.getProducerCount();
            int extraForProducers = testData.getToProduce() % testData.getProducerCount();

            for (int i=0; i<testData.getProducerCount(); i++) {
                int quota = baseForProducers;
                if (i < extraForProducers) quota += 1;

                threadPool.add(new Thread(getProducerRunnable(quota, stack, metrics), "Producer "+i));
            }
        }
        if (threadType == ThreadType.CONSUMER) {

            int baseForConsumers = testData.getToConsume() / testData.getConsumerCount();
            int extraForConsumers = testData.getToConsume() % testData.getConsumerCount();

            for (int i=0; i<testData.getConsumerCount(); i++) {
                int quota = baseForConsumers;
                if (i < extraForConsumers) quota += 1;

                threadPool.add(new Thread(getConsumerRunnable(quota, stack, metrics), "Consumer "+i));
            }
        }
        return threadPool.toArray(new Thread[0]);
    }
}
