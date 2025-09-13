package analyser.util;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class StackPerformanceMetrics {
    private final String stackName;
    private final AtomicInteger producedCount;
    private final AtomicInteger consumedCount;
    private long totalTime;
    private final List<Long> latencies = Collections.synchronizedList(new ArrayList<>());
    private final List<String> producedData = Collections.synchronizedList(new ArrayList<>());
    private final List<String> consumedData = Collections.synchronizedList(new ArrayList<>());
    private final AtomicInteger errorCount = new AtomicInteger();


    public StackPerformanceMetrics(String name) {
        this.stackName = name;
        this.producedCount = new AtomicInteger();
        this.consumedCount = new AtomicInteger();
    }

    public String getStackName() {
        return stackName;
    }

    public List<String> getConsumedData() {
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

    public void addToProducedData(String val) {
        producedData.addLast(val);
    }

    public void addToConsumedData(String val) {
        consumedData.addLast(val);
    }

    private String hasDuplicates(List<String> list) throws Exception {
        if (list.isEmpty()) return "empty list";
        Set<String> set = new HashSet<>();
        for (String val: list) {
            if (!set.add(val)) {
                return String.valueOf(true);
            }
        }
        return String.valueOf(false);
    }

    public String printProducedData() {
        String data = "";
        for (String val: producedData) {
            data = data.concat(val+" ");
        }
        return data;
    }

    public String printConsumedData() {
        String data = "";
        for (String val: consumedData) {
            data = data.concat(val+" ");
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
            if (!consumedData.reversed().get(i).equals(producedData.get(i))) return false;
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
