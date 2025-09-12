package site.analyser.util;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class StackPerformanceMetrics {
    private final String stackName;
    private final AtomicInteger producedCount;
    private final AtomicInteger consumedCount;
    private long totalTime;
    private final List<Long> latencies = Collections.synchronizedList(new ArrayList<>());
    private final List<Element> producedData = Collections.synchronizedList(new ArrayList<>());
    private final List<Element> consumedData = Collections.synchronizedList(new ArrayList<>());
    private final AtomicInteger errorCount = new AtomicInteger();


    public StackPerformanceMetrics(String name) {
        this.stackName = name;
        this.producedCount = new AtomicInteger();
        this.consumedCount = new AtomicInteger();
    }

    public String getStackName() {
        return stackName;
    }

    public List<Element> getConsumedData() {
        return consumedData;
    }

    public void setTotalTime(long time) {
        this.totalTime = time;
    }

    public void incrementErrorCount() {errorCount.getAndIncrement();}

    public void incrementProducedCount() {
        producedCount.getAndIncrement();
    }

    public void incrementConsumedCount() {
        consumedCount.getAndIncrement();
    }

    public double calculateThroughput() {
        return (producedCount.get() + consumedCount.get()) / (double) totalTime;
    }

    public void addLatency(long val) {
        latencies.add(val);
    }

    public double getAvgLatency() {
        double total =0;
        for (Long val: latencies) {
            total += val;
        }
        total = total / 1_000_000;
        return total/latencies.size();
    }

    public void addToProducedData(Element val) {
        producedData.addLast(val);
    }

    public void addToConsumedData(Element val) {
        consumedData.addLast(val);
    }

    private String hasDuplicates(List<Element> list) throws Exception {
        if (list.isEmpty()) return "empty list";
        Set<Element> set = new HashSet<>();
        for (Element val: list) {
            if (!set.add(val)) {
                return String.valueOf(true);
            }
        }
        return String.valueOf(false);
    }

    public String printProducedData() {
        String data = "";
        for (Element val: producedData) {
            data = data.concat(String.valueOf(val.getUniqueID()));
        }
        return data;
    }

    public String printConsumedData() {
        String data = "";
        for (Element val: consumedData) {
            data = data.concat(String.valueOf(val.getUniqueID()));
        }
        return data;
    }

    public boolean consumeAmountEqualsProduced() {
        return consumedCount.get() == producedCount.get();
    }

    public boolean isLifo() throws Exception {
        if (hasDuplicates(producedData).equals("true")) throw new Exception("produced data contains duplicates");
        if (hasDuplicates(consumedData).equals("true")) throw new Exception("consumed data contains duplicates");
        for (int i=0; i<producedData.size(); i++) {
            if (!consumedData.reversed().get(i).getUniqueID().equals(producedData.get(i).getUniqueID())) return false;
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
                    error count                          : %s
                    produced count equals consumed count : %b
                    produced data contains duplicates    : %s
                    consumed data contains duplicates    : %s
                    
                    --THROUGHPUT--
                    total time  :   %s ms
                    throughput  :   %.4f ops/ms
                    
                    --LATENCY--
                    avg latency :   %.4f ms/ops
                    
                    ---------------------------------------------
                    """,
                    getStackName(),
                    errorCount.get(),
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
