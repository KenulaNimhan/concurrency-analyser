package analyser.use;

import analyser.config.Configuration;
import analyser.structure.queue.Queue;
import analyser.structure.queue.impl.*;
import analyser.util.*;
import analyser.util.constant.QueueType;
import analyser.util.constant.ThreadType;

import java.util.*;

public class UseQueue implements User{

    private static final Map<QueueType, Queue<Element>> queueMetricMap = new HashMap<>();
    private static final MetricComparer comparer = new MetricComparer();
    private final Configuration testData;

    public UseQueue(Configuration testData) {
        this.testData = testData;
    }

    public void use() throws InterruptedException {

        // creating stacks to test
        BasicQueue<Element> basicQueue = new BasicQueue<>(testData.getCap());
        NaiveSyncQueue<Element> naiveSyncQueue = new NaiveSyncQueue<>(testData.getCap());
        SyncQueue<Element> syncQueue = new SyncQueue<>(testData.getCap());
        LockBasedQueue<Element> lockBasedQueue = new LockBasedQueue<>(testData.getCap());
        LockFreeQueue<Element> lockFreeQueue = new LockFreeQueue<>(testData.getCap());

        queueMetricMap.put(QueueType.BASIC, basicQueue);
        queueMetricMap.put(QueueType.NAIVE_SYNC, naiveSyncQueue);
        queueMetricMap.put(QueueType.SYNC, syncQueue);
        queueMetricMap.put(QueueType.LOCK_BASED, lockBasedQueue);
        queueMetricMap.put(QueueType.LOCK_FREE, lockFreeQueue);

        // running the configured scenarios for each stack and collecting metrics
        for (QueueType queueType: QueueType.values()) {
            runThreads(queueMetricMap.get(queueType), queueType.getMetrics());
        }

        // adding all metrics to comparer
        for (QueueType queueType: QueueType.values()) {
            comparer.addToList(queueType.getMetrics());
        }

        comparer.printComparison();
    }

    private void runThreads(Queue<Element> queue, PerformanceMetrics metrics) throws InterruptedException {
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

    private Runnable getProducerRunnable(int quota, Queue<Element> queue, PerformanceMetrics metrics) {
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

    private Runnable getConsumerRunnable(int quota, Queue<Element> queue, PerformanceMetrics metrics) {
        if (queue instanceof LockFreeQueue<Element>) {
            return () -> {
                var opsBegin = System.nanoTime();
                int consumed = 0;
                while (consumed < quota) {
                    try {
                        var obj = queue.dequeue();
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

    private Thread[] getConfiguredThreadPool(
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
