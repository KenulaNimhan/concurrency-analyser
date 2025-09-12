package site.visualizer;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class StackPerformanceMetrics<T> {
    private final String stackName;
    private final AtomicInteger producedCount;
    private final AtomicInteger consumedCount;
    private long totalTime;
    private final List<Long> latencies = Collections.synchronizedList(new ArrayList<>());
    private final List<T> producedData = Collections.synchronizedList(new ArrayList<>());
    private final List<T> consumedData = Collections.synchronizedList(new ArrayList<>());


    public StackPerformanceMetrics(String name) {
        this.stackName = name;
        this.producedCount = new AtomicInteger();
        this.consumedCount = new AtomicInteger();
    }

    public String getStackName() {
        return stackName;
    }

    public void setTotalTime(long time) {
        this.totalTime = time;
    }

    public void incrementProducedCount() {
        producedCount.getAndIncrement();
    }

    public void incrementConsumedCount() {
        consumedCount.getAndIncrement();
    }

    public long calculateThroughput() {
        return (producedCount.get() + consumedCount.get()) / totalTime;
    }

    public void addLatency(long val) {
        latencies.add(val);
    }

    public double getAvgLatency() {
        double total =0;
        Collections.sort(latencies);
        for (Long val: latencies) {
            total += val;
        }
        return total/latencies.size();
    }

    public void addToProducedData(T val) {
        producedData.addLast(val);
    }

    public void addToConsumedData(T val) {
        consumedData.addLast(val);
    }

    private boolean hasDuplicates(List<T> list) {
        Set<T> set = new HashSet<>();
        for (T val: list) {
            if (!set.add(val)) {
                return true;
            }
        }
        return false;
    }

    public String printProducedData() {
        String data = "";
        for (T val: producedData) {
            data = data.concat(val.toString()+" ");
        }
        return data;
    }

    public String printConsumedData() {
        String data = "";
        for (T val: consumedData) {
            data = data.concat(val.toString()+" ");
        }
        return data;
    }

    public boolean consumedDataEqualsProducedData() {
        return consumedData.size() == producedData.size();
    }

    public boolean consumeAmountEqualsProduced() {
        return consumedCount.get() == producedCount.get();
    }

    public boolean isLifo() throws Exception {
        if (!consumeAmountEqualsProduced()) throw new Exception("produced count and consumed count does not match");
        if (!consumedDataEqualsProducedData()) throw new Exception("produced data and consumed data differs");
        if (hasDuplicates(producedData)) throw new Exception("produced data contains duplicates");
        if (hasDuplicates(consumedData)) throw new Exception("consumed data contains duplicates");
        for (int i=0; i<producedCount.get(); i++) {
            if (consumedData.reversed().get(i) != producedData.get(i)) return false;
        }
        return true;
    }

    @Override
    public String toString() {
        try {
            return String.format("""
                    ------------ PERFORMANCE METRICS ------------
                    
                    Structure : %s
                    
                    --CORRECTNESS--
                    produced count equals consumed count : %b
                    produced data contains duplicates    : %b
                    consumed data contains duplicates    : %b
                    
                    --THROUGHPUT--
                    total time  :   %s ms
                    throughput  :   %s ops/ms
                    
                    --LATENCY--
                    avg latency :   %s ns/ops
                    
                    ---------------------------------------------
                    """,
                    getStackName(),
                    consumeAmountEqualsProduced(),
                    hasDuplicates(producedData),
                    hasDuplicates(consumedData),
                    totalTime,
                    calculateThroughput(),
                    getAvgLatency());
        } catch (Exception e) {
            System.out.println("produced data size: "+producedData.size());
            System.out.println("consumed data size: "+consumedData.size());
            return e.getMessage();
        }
    }
}
