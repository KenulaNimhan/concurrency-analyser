package site.visualizer.use;

import site.visualizer.StackPerformanceMetrics;
import site.visualizer.model.Configurable;
import site.visualizer.model.ThreadType;
import site.visualizer.model.structure.stack.AdvSyncStack;
import site.visualizer.model.structure.stack.LockBasedStack;
import site.visualizer.model.structure.stack.Stack;
import site.visualizer.model.type.Element;

import java.util.*;

public class UseStack {
    private static final Scanner scan = new Scanner(System.in);
    private static final Configurable testData = new Configurable();

    private static final StackPerformanceMetrics<Element> syncStackMetrics = new StackPerformanceMetrics<>("Synchronised Stack");
    private static final StackPerformanceMetrics<Element> lockStackMetrics = new StackPerformanceMetrics<>("Lock Based Stack");


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
        AdvSyncStack<Element> syncStack = new AdvSyncStack<>(testData.getCap());
        LockBasedStack<Element> lockBasedStack = new LockBasedStack<>(testData.getCap());

        runThreads(syncStack, syncStackMetrics);
        runThreads(lockBasedStack, lockStackMetrics);

        System.out.println(syncStackMetrics);
        System.out.println(lockStackMetrics);

    }

    private static <T> void runThreads(Stack<Element> stack, StackPerformanceMetrics<Element> metrics) throws InterruptedException {
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

    private static Runnable getProducerRunnable(int quota, Stack<Element> stack, StackPerformanceMetrics<Element> metrics) {
        return () -> {
            var opsBegin = System.nanoTime();
            for (int i=0; i<quota; i++) {
                try {
                    var newElement = new Element();
//                    System.out.println("adding "+newElement);
                    stack.push(newElement);
                    metrics.addToProducedData(newElement);
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
                    var obj = stack.pop();
                    metrics.addToConsumedData(obj);
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
            Stack<Element> stack,
            StackPerformanceMetrics<Element> metrics
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
