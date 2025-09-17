package analyser.use;

import analyser.config.Configuration;
import analyser.config.Configurator;
import analyser.structure.queue.Queue;
import analyser.structure.queue.impl.BasicQueue;
import analyser.structure.queue.impl.LockBasedQueue;
import analyser.structure.queue.impl.NaiveSyncQueue;
import analyser.structure.queue.impl.SyncQueue;
import analyser.util.Element;
import analyser.util.MetricComparer;
import analyser.util.PerformanceMetrics;
import analyser.util.ThreadType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UseQueue {
    private static final Configurator configurator = new Configurator();
    private static Configuration testData = new Configuration();

    private static final PerformanceMetrics basicQueueMetrics = new PerformanceMetrics("Basic Queue");
    private static final PerformanceMetrics naiveSyncQueueMetrics = new PerformanceMetrics("Naive Sync Queue");
    private static final PerformanceMetrics syncQueueMetrics = new PerformanceMetrics("Synchronised Queue");
    private static final PerformanceMetrics lockQueueMetrics = new PerformanceMetrics("Lock Based Queue");

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
        BasicQueue<Element> basicQueue = new BasicQueue<>(testData.getCap());
        NaiveSyncQueue<Element> basicSyncQueue = new NaiveSyncQueue<>(testData.getCap());
        SyncQueue<Element> syncQueue = new SyncQueue<>(testData.getCap());
        LockBasedQueue<Element> lockBasedQueue = new LockBasedQueue<>(testData.getCap());

        // running the configured scenarios for each stack and collecting metrics
        runThreads(basicQueue, basicQueueMetrics);
        runThreads(basicSyncQueue, naiveSyncQueueMetrics);
        runThreads(syncQueue, syncQueueMetrics);
        runThreads(lockBasedQueue, lockQueueMetrics);

        // adding all metrics to comparer
        comparer.addToList(basicQueueMetrics);
        comparer.addToList(naiveSyncQueueMetrics);
        comparer.addToList(syncQueueMetrics);
        comparer.addToList(lockQueueMetrics);

        comparer.printComparison();

    }

    private static void runThreads(Queue<Element> queue, PerformanceMetrics metrics) throws InterruptedException {
        // creating configured thread pools
        Thread[] producerThreads = getConfiguredThreadPool(
                ThreadType.PRODUCER, queue,metrics);
        Thread[] consumerThreads = getConfiguredThreadPool(
                ThreadType.CONSUMER, queue,metrics);

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

    private static Runnable getProducerRunnable(int quota, Queue<Element> queue, PerformanceMetrics metrics) {
        return () -> {
            var opsBegin = System.nanoTime();
            for (int i=0; i<quota; i++) {
                try {
                    var newElement = new Element(testData.getElementSize());
                    newElement.compute(testData.getOperationalScale());
                    queue.enqueue(newElement);
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

    private static Runnable getConsumerRunnable(int quota, Queue<Element> queue, PerformanceMetrics metrics) {
//        if (stack instanceof TreiberStack<Element>) {
//            return () -> {
//                var opsBegin = System.nanoTime();
//                int consumed = 0;
//                while (consumed < quota) {
//                    try {
//                        var obj = stack.pop();
//                        if (obj != null ) {
//                            obj.compute(testData.getOperationalScale());
//                            metrics.addToConsumedData(String.valueOf(obj.getUniqueID()));
//                            metrics.incrementConsumedCount();
//
//                            consumed++;
//                        } else {
//                            Thread.yield();
//                        }
//                    } catch (Exception e) {
//                        metrics.incrementErrorCount();
//                    }
//                }
//                var opsEnd = System.nanoTime();
//                var latencyForThread = (opsEnd-opsBegin) / quota;
//                metrics.addLatency(latencyForThread);
//            };
//        }

        return () -> {
            var opsBegin = System.nanoTime();
            for (int i=0; i<quota; i++) {
                try {
                    var obj = queue.dequeue();
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
            Queue<Element> queue,
            PerformanceMetrics metrics
    ) {
        ArrayList<Thread> threadPool = new ArrayList<>();
        if (threadType == ThreadType.PRODUCER) {

            int baseForProducers = testData.getToProduce() / testData.getProducerCount();
            int extraForProducers = testData.getToProduce() % testData.getProducerCount();

            for (int i=0; i<testData.getProducerCount(); i++) {
                int quota = baseForProducers;
                if (i < extraForProducers) quota += 1;

                threadPool.add(new Thread(getProducerRunnable(quota, queue, metrics), "Producer "+i));
            }
        }
        if (threadType == ThreadType.CONSUMER) {

            int baseForConsumers = testData.getToConsume() / testData.getConsumerCount();
            int extraForConsumers = testData.getToConsume() % testData.getConsumerCount();

            for (int i=0; i<testData.getConsumerCount(); i++) {
                int quota = baseForConsumers;
                if (i < extraForConsumers) quota += 1;

                threadPool.add(new Thread(getConsumerRunnable(quota, queue, metrics), "Consumer "+i));
            }
        }
        return threadPool.toArray(new Thread[0]);
    }
}
