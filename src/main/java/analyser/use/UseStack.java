package analyser.use;

import analyser.config.Configurable;
import analyser.config.Configurator;
import analyser.structure.stack.*;
import analyser.structure.stack.Stack;
import analyser.util.*;

import java.util.*;

public class UseStack {
    private static final Configurator configurator = new Configurator();
    private static Configurable testData = new Configurable();

    private static final StackPerformanceMetrics basicStackMetrics = new StackPerformanceMetrics("Basic Stack");
    private static final StackPerformanceMetrics basicSyncStackMetrics = new StackPerformanceMetrics("Basic Sync Stack");
    private static final StackPerformanceMetrics syncStackMetrics = new StackPerformanceMetrics("Synchronised Stack");
    private static final StackPerformanceMetrics lockStackMetrics = new StackPerformanceMetrics("Lock Based Stack");


    public static void main(String[] args) throws InterruptedException {

        // configuring test data
        testData = configurator.configure();

        // creating stacks to test
        BasicStack<Element> basicStack = new BasicStack<>(testData.getCap());
        SyncStack<Element> basicSyncStack = new SyncStack<>(testData.getCap());
        AdvSyncStack<Element> syncStack = new AdvSyncStack<>(testData.getCap());
        LockBasedStack<Element> lockBasedStack = new LockBasedStack<>(testData.getCap());

        // running the configured scenarios for each stack and collecting metrics
        runThreads(basicStack, basicStackMetrics);
        runThreads(basicSyncStack, basicSyncStackMetrics);
        runThreads(syncStack, syncStackMetrics);
        runThreads(lockBasedStack, lockStackMetrics);

        // display metrics
        System.out.println(basicStackMetrics);
        System.out.println(basicSyncStackMetrics);
        System.out.println(syncStackMetrics);
        System.out.println(lockStackMetrics);

    }

    private static void runThreads(Stack<Element> stack, StackPerformanceMetrics metrics) throws InterruptedException {
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

    private static Runnable getProducerRunnable(int quota, Stack<Element> stack, StackPerformanceMetrics metrics) {
        return () -> {
            var opsBegin = System.nanoTime();
            for (int i=0; i<quota; i++) {
                try {
                    var newElement = new Element();
                    stack.push(newElement);
                    metrics.addToProducedData(newElement);
                    metrics.incrementProducedCount();
                } catch (Exception e) {
                    metrics.incrementErrorCount();
//                    System.out.println(e.getMessage());
                }
            }
            var opsEnd = System.nanoTime();
            var latencyForThread = (opsEnd-opsBegin) / quota;
            metrics.addLatency(latencyForThread);
        };
    }

    private static Runnable getConsumerRunnable(int quota, Stack<Element> stack, StackPerformanceMetrics metrics) {
        return () -> {
            var opsBegin = System.nanoTime();
            for (int i=0; i<quota; i++) {
                try {
                    var obj = stack.pop();
                    metrics.addToConsumedData(obj);
                    metrics.incrementConsumedCount();
                } catch (Exception e) {
                    metrics.incrementErrorCount();
//                    System.out.println(e.getMessage());
                }
            }
            var opsEnd = System.nanoTime();
            var latencyForThread = (opsEnd-opsBegin) / quota;
            metrics.addLatency(latencyForThread);
        };
    }

    private static Thread[] getConfiguredThreadPool(
            ThreadType threadType,
            Stack<Element> stack,
            StackPerformanceMetrics metrics
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
