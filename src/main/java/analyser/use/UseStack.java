package analyser.use;

import analyser.config.Configurable;
import analyser.config.Configurator;
import analyser.structure.stack.*;
import analyser.util.Element;
import analyser.util.StackPerformanceMetrics;
import analyser.util.ThreadType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UseStack {
    private static final Configurator configurator = new Configurator();
    private static Configurable testData = new Configurable();

    private static final StackPerformanceMetrics basicStackMetrics = new StackPerformanceMetrics("Basic Stack");
    private static final StackPerformanceMetrics naiveSyncStackMetrics = new StackPerformanceMetrics("Naive Sync Stack");
    private static final StackPerformanceMetrics syncStackMetrics = new StackPerformanceMetrics("Synchronised Stack");
    private static final StackPerformanceMetrics lockStackMetrics = new StackPerformanceMetrics("Lock Based Stack");

    public static void main(String[] args) throws InterruptedException {

        // configuring test data
        testData = configurator.configure();

        // creating stacks to test
        BasicStack<Element> basicStack = new BasicStack<>(testData.getCap());
        NaiveSyncStack<Element> basicSyncStack = new NaiveSyncStack<>(testData.getCap());
        SyncStack<Element> syncStack = new SyncStack<>(testData.getCap());
        LockBasedStack<Element> lockBasedStack = new LockBasedStack<>(testData.getCap());

        // running the configured scenarios for each stack and collecting metrics
        runThreads(basicStack, basicStackMetrics);
        runThreads(basicSyncStack, naiveSyncStackMetrics);
        runThreads(syncStack, syncStackMetrics);
        runThreads(lockBasedStack, lockStackMetrics);

        // display metrics
        System.out.println(basicStackMetrics);
        System.out.println(naiveSyncStackMetrics);
        System.out.println(syncStackMetrics);
        System.out.println(lockStackMetrics);

    }

    private static void runThreads(Stack<Element> stack, StackPerformanceMetrics metrics) throws InterruptedException {
        // creating configured thread pools
        Thread[] producerThreads = getConfiguredThreadPool(
                ThreadType.PRODUCER, stack,metrics);
        Thread[] consumerThreads = getConfiguredThreadPool(
                ThreadType.CONSUMER, stack,metrics);

        List<Thread> allThreads = new ArrayList<>();
        Collections.addAll(allThreads, producerThreads);
        Collections.addAll(allThreads, consumerThreads);
        Collections.shuffle(allThreads);

        var start = System.nanoTime();
        // starting
        for (Thread thread: allThreads) thread.start();
        // joining
        for (Thread thread: allThreads) thread.join();
        var end = System.nanoTime();

        metrics.setTotalTime((end-start) / 1_000_000);
    }

    private static Runnable getProducerRunnable(int quota, Stack<Element> stack, StackPerformanceMetrics metrics) {
        return () -> {
            var opsBegin = System.nanoTime();
            for (int i=0; i<quota; i++) {
                try {
                    var newElement = new Element(testData.getElementSize());
                    newElement.compute(testData.getOperationalScale());
                    stack.push(newElement);
                    metrics.addToProducedData(String.valueOf(newElement.getUniqueID()));
                    metrics.incrementProducedCount();
                } catch (Exception e) {
                    metrics.incrementErrorCount();
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
                    obj.compute(testData.getOperationalScale());
                    metrics.addToConsumedData(String.valueOf(obj.getUniqueID()));
                    metrics.incrementConsumedCount();
                } catch (Exception e) {
                    metrics.incrementErrorCount();
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