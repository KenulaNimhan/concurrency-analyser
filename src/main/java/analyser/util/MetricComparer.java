package analyser.util;

import java.util.*;

public class MetricComparer {
    List<StackPerformanceMetrics> metricsList = new ArrayList<>();
    private final Map<String, Double> throughPutMap = new HashMap<>();
    private final Map<String, Double> latencyMap = new HashMap<>();
    private final Map<String, Integer> errorCountMap = new HashMap<>();

    public void populate() {
        for (StackPerformanceMetrics metric: metricsList) {
            throughPutMap.put(metric.getStackName(), metric.calculateThroughput());
            latencyMap.put(metric.getStackName(), metric.getAvgLatency());
            errorCountMap.put(metric.getStackName(), metric.getErrorCount());
        }
    }

    public void addToList(StackPerformanceMetrics val) {
        metricsList.add(val);
    }

    public String printErrorCount() {
        var returnString = "";

        for (String stack: errorCountMap.keySet()) {
            returnString = returnString.concat(stack+" : "+errorCountMap.get(stack)+"\n");
        }

        return returnString;
    }

    public String printThroughput() {
        var returnString = "";

        for (String stack: throughPutMap.keySet()) {
            returnString = returnString.concat(stack+" : "+throughPutMap.get(stack)+" ops/ms \n");
        }

        return returnString;
    }

    public String printLatency() {
        var returnString = "";

        for (String stack: throughPutMap.keySet()) {
            returnString = returnString.concat(stack+" : "+latencyMap.get(stack)+" ms/ops \n");
        }

        return returnString;
    }

    public void printComparison() {
        System.out.printf("""
                 -------------------------------------------------------------------------------------------------------
                                                         PERFORMANCE METRICS
                 -------------------------------------------------------------------------------------------------------
                 %-20s %-15s %-15s %-20s %-15s
                 
                 """, "STACK", "ERROR COUNT", "TOTAL TIME (ms)", "THROUGHPUT (ops/ms)", "LATENCY (ms/ops)");

        for (StackPerformanceMetrics metric: metricsList) {
            System.out.printf("""
                %-20s %-15s %-15s %-20.3f %.4f \n
                """,
                metric.getStackName(),
                errorCountMap.get(metric.getStackName()),
                metric.getTotalTime(),
                throughPutMap.get(metric.getStackName()),
                latencyMap.get(metric.getStackName())
            );
        }
    }
}
