package analyser.use;

import analyser.config.Configuration;
import analyser.config.Configurator;
import analyser.structure.stack.Stack;
import analyser.structure.stack.impl.*;
import analyser.util.*;

import java.util.*;

public class UseStack {
    private static final Configurator configurator = new Configurator();
    private static Configuration testData = new Configuration();

    private static final Map<StackType, Stack<Element>> stackMetricMap = new HashMap<>();

    private static final MetricComparer comparer = new MetricComparer();

    public static void main(String[] args) throws InterruptedException {

        System.out.println("""
                ---------------------------------------------------
                    WELCOME TO CONCURRENCY PERFORMANCE ANALYSER
                ---------------------------------------------------
                enter -1 at any time to exit.
                set configuration settings;
                """);

        // configuring test data
        testData = configurator.configure();

        // creating stacks to test
        BasicStack<Element> basicStack = new BasicStack<>(testData.getCap());
        NaiveSyncStack<Element> naiveSyncStack = new NaiveSyncStack<>(testData.getCap());
        SyncStack<Element> syncStack = new SyncStack<>(testData.getCap());
        LockBasedStack<Element> lockBasedStack = new LockBasedStack<>(testData.getCap());
        LockFreeStack<Element> lockFreeStack = new LockFreeStack<>(testData.getCap());

        stackMetricMap.put(StackType.BASIC, basicStack);
        stackMetricMap.put(StackType.NAIVE_SYNC, naiveSyncStack);
        stackMetricMap.put(StackType.SYNC, syncStack);
        stackMetricMap.put(StackType.LOCK_BASED, lockBasedStack);
        stackMetricMap.put(StackType.LOCK_FREE, lockFreeStack);

        // running the configured scenarios for each stack and collecting metrics
        for (StackType stackType: StackType.values()) {
            runThreads(stackMetricMap.get(stackType), stackType.getMetrics());
        }

        // adding all metrics to comparer
        for (StackType stackType: StackType.values()) {
            comparer.addToList(stackType.getMetrics());
        }

        comparer.printComparison();
    }

    private static void runThreads(Stack<Element> stack, PerformanceMetrics metrics) throws InterruptedException {
        // creating configured thread pools
        Thread[] producerThreads = getConfiguredThreadPool(
                ThreadType.PRODUCER, stack,metrics);
        Thread[] consumerThreads = getConfiguredThreadPool(
                ThreadType.CONSUMER, stack,metrics);

        List<Thread> allThreads = new ArrayList<>();
        Collections.addAll(allThreads, producerThreads);
        Collections.addAll(allThreads, consumerThreads);
        Collections.shuffle(allThreads);

        System.out.println("using "+metrics.getName()+" ...");

        var start = System.nanoTime();
        // starting
        for (Thread thread: allThreads) thread.start();
        // joining
        for (Thread thread: allThreads) thread.join();
        var end = System.nanoTime();

        metrics.setTotalTime((end-start) / 1_000_000);

        System.out.println("metrics collected for "+metrics.getName());
    }

    private static Runnable getProducerRunnable(int quota, Stack<Element> stack, PerformanceMetrics metrics) {
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

    private static Runnable getConsumerRunnable(int quota, Stack<Element> stack, PerformanceMetrics metrics) {
        if (stack instanceof LockFreeStack<Element>) {
            return () -> {
                var opsBegin = System.nanoTime();
                int consumed = 0;
                while (consumed < quota) {
                    try {
                        var obj = stack.pop();
                        if (obj != null ) {
                            obj.compute(testData.getOperationalScale());
                            metrics.addToConsumedData(String.valueOf(obj.getUniqueID()));
                            metrics.incrementConsumedCount();

                            consumed++;
                        } else {
                            Thread.yield();
                        }
                    } catch (Exception e) {
                        metrics.incrementErrorCount();
                    }
                }
                var opsEnd = System.nanoTime();
                var latencyForThread = (opsEnd-opsBegin) / quota;
                metrics.addLatency(latencyForThread);
            };
        }

        return () -> {
            var opsBegin = System.nanoTime();
            for (int i=0; i<quota; i++) {
                try {
                    var obj = stack.pop();
                    if (obj != null ) {
                        obj.compute(testData.getOperationalScale());
                        metrics.addToConsumedData(String.valueOf(obj.getUniqueID()));
                        metrics.incrementConsumedCount();
                    }
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
            PerformanceMetrics metrics
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