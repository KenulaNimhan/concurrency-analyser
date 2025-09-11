package site.visualizer.use;

import site.visualizer.model.Configurable;
import site.visualizer.model.ThreadType;
import site.visualizer.model.structure.stack.AdvSyncStack;
import site.visualizer.model.structure.stack.LockBasedStack;
import site.visualizer.model.structure.stack.Stack;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class UseStack {
    private static final Scanner scan = new Scanner(System.in);

    private static final AtomicInteger producedCount = new AtomicInteger();
    private static final AtomicInteger consumedCount = new AtomicInteger();

    private static final List<Long> latencyListForSyncStack = Collections.synchronizedList(new ArrayList<>());
    private static final List<Long> latencyListForLockStack = Collections.synchronizedList(new ArrayList<>());


    public static void main(String[] args) throws InterruptedException {

        // configurable
        Configurable testData = new Configurable();

        // gathering input from user to configure
        System.out.print("how many bricks are needed to be stacked: ");
        var toProduce = scan.nextInt();

        var toConsume = Integer.MAX_VALUE;
        do {
            System.out.print("how many bricks are needed to be removed: ");
            toConsume = scan.nextInt();
            if (toConsume <= toProduce) break;
            System.out.println("needed amount cannot exceed the available amount ");
        } while (true);

        System.out.print("how many workers putting to the stack: ");
        var producerCount = scan.nextInt();
        System.out.print("how many workers taking from the stack: ");
        var consumerCount = scan.nextInt();
        System.out.print("what is the max height the stack can be: ");
        var stackHeight = scan.nextInt();

        testData.setCap(stackHeight)
                .setToProduce(toProduce)
                .setToConsume(toConsume)
                .setProducerCount(producerCount)
                .setConsumerCount(consumerCount);

        // test stacks
        AdvSyncStack<Integer> syncStack = new AdvSyncStack<>(testData.getCap());
        LockBasedStack<Integer> lockBasedStack = new LockBasedStack<>(testData.getCap());

        // process for sync stack
        Thread[] producersForSyncStack = getConfiguredThreadPool(
                ThreadType.PRODUCER, testData, syncStack, latencyListForSyncStack);
        Thread[] consumersForSyncStack = getConfiguredThreadPool(
                ThreadType.CONSUMER, testData, syncStack, latencyListForSyncStack);

        var start = System.nanoTime();
        // starting
        for (Thread producer: producersForSyncStack) producer.start();
        for (Thread consumer: consumersForSyncStack) consumer.start();
        // joining
        for (Thread producer: producersForSyncStack) producer.join();
        for (Thread consumer: consumersForSyncStack) consumer.join();
        var end = System.nanoTime();

        var totalTime = (end-start) / 1_000_000;
        var throughputOfSyncStack = (producedCount.get() +consumedCount.get()) / totalTime;
        System.out.println("time taken for sync stack: " + totalTime + "ms");

        // process for lock based stack
        producedCount.set(0);
        consumedCount.set(0);

        Thread[] producersForLockStack = getConfiguredThreadPool(
                ThreadType.PRODUCER, testData, lockBasedStack, latencyListForLockStack);
        Thread[] consumersForLockStack = getConfiguredThreadPool(
                ThreadType.CONSUMER, testData, lockBasedStack, latencyListForLockStack);

        start = System.nanoTime();

        // starting
        for (Thread producer: producersForLockStack) producer.start();
        for (Thread consumer: consumersForLockStack) consumer.start();
        // joining
        for (Thread producer: producersForLockStack) producer.join();
        for (Thread consumer: consumersForLockStack) consumer.join();

        end = System.nanoTime();
        totalTime = (end-start) / 1_000_000;
        var throughputOfLockStack = (producedCount.get() +consumedCount.get()) / totalTime;
        System.out.println("time taken for lock stack: " + totalTime + "ms");

        System.out.println("--CORRECTNESS--");
        System.out.println("sync stack size: "+syncStack.size());
        System.out.println("lock stack size: "+lockBasedStack.size());

        System.out.println("--THROUGHPUT--");
        System.out.println("sync stack: "+throughputOfSyncStack+" ops/ms");
        System.out.println("lock stack: "+throughputOfLockStack+ " ops/ms");

        Collections.sort(latencyListForSyncStack);
        Collections.sort(latencyListForLockStack);
        System.out.println("--LATENCY--");
        System.out.println("sync - max: "+latencyListForSyncStack.getLast()+" ns");
        System.out.println("sync - min: "+latencyListForSyncStack.getFirst()+" ns");
        System.out.println("lock - max: "+latencyListForLockStack.getLast()+" ns");
        System.out.println("lock - min: "+latencyListForLockStack.getFirst()+" ns");


    }

    @SuppressWarnings("unchecked")
    private static <T> Runnable getProducerRunnable(int quota, Stack<T> stack, List<Long> latencyList) {
        return () -> {
            var opsBegin = System.nanoTime();
            for (int i=0; i<quota; i++) {
                try {
                    stack.push((T) new Object());
                    producedCount.getAndIncrement();
                } catch (IllegalStateException e) {
                    System.out.println("tried to push when full");
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }
            }
            var opsEnd = System.nanoTime();
            var latencyForThread = (opsEnd-opsBegin) / quota;
            latencyList.add(latencyForThread);
        };
    }

    private static <T> Runnable getConsumerRunnable(int quota, Stack<T> stack, List<Long> latencyList) {
        return () -> {
            var opsBegin = System.nanoTime();
            for (int i=0; i<quota; i++) {
                try {
                    stack.pop();
                    consumedCount.getAndIncrement();
                } catch (NoSuchElementException e) {
                    System.out.println("tried to pop when empty");
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }
            }
            var opsEnd = System.nanoTime();
            var latencyForThread = (opsEnd-opsBegin) / quota;
            latencyList.add(latencyForThread);
        };
    }

    private static <T> Thread[] getConfiguredThreadPool(
            ThreadType threadType,
            Configurable testData,
            Stack<T> stack,
            List<Long> latencyList
    ) {
        ArrayList<Thread> threadPool = new ArrayList<>();
        if (threadType == ThreadType.PRODUCER) {

            int baseForProducers = testData.getToProduce() / testData.getProducerCount();
            int extraForProducers = testData.getToProduce() % testData.getProducerCount();

            for (int i=0; i<testData.getProducerCount(); i++) {
                int quota = baseForProducers;
                if (i < extraForProducers) quota += 1;

                threadPool.add(new Thread(getProducerRunnable(quota, stack, latencyList), "Producer "+i));
            }
        }
        if (threadType == ThreadType.CONSUMER) {

            int baseForConsumers = testData.getToConsume() / testData.getConsumerCount();
            int extraForConsumers = testData.getToConsume() % testData.getConsumerCount();

            for (int i=0; i<testData.getConsumerCount(); i++) {
                int quota = baseForConsumers;
                if (i < extraForConsumers) quota += 1;

                threadPool.add(new Thread(getConsumerRunnable(quota, stack, latencyList), "Consumer "+i));
            }
        }
        return threadPool.toArray(new Thread[0]);
    }
}
