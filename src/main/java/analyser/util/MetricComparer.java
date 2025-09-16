package analyser.util;

import java.util.*;

public class MetricComparer {
    List<StackPerformanceMetrics> metricsList = new ArrayList<>();

    public void addToList(StackPerformanceMetrics val) {
        metricsList.add(val);
    }

    public void printCorrectnessInfo() {
        for (StackPerformanceMetrics stack: metricsList) {
            if (stack.hasDuplicates(stack.getProducedData())) {
                System.out.println(stack.getStackName()+" has duplicated produced data");
            }
            if (stack.hasDuplicates(stack.getConsumedData())) {
                System.out.println(stack.getStackName()+" has duplicated consumed data");
            }
            if (!stack.consumeCountEqualsProduced()) {
                System.out.println(stack.getStackName()+"'s consume count and produced count is not equal");
            }
        }
    }

    public void printComparison() {
        System.out.printf("""
             --------------------------------------------------------------------------------------------------------
                                                      PERFORMANCE METRICS
             --------------------------------------------------------------------------------------------------------
             %-20s %-15s %-20s %-25s %-15s
             
             """, "STACK", "ERROR COUNT", "TOTAL TIME (ms)", "THROUGHPUT (ops/ms)", "AVG LATENCY (ms/ops)");

        for (StackPerformanceMetrics metric: metricsList) {
            System.out.printf("""
                %-20s %-15s %-20s %-25.3f %.4f \n
                """,
                metric.getStackName(),
                metric.getErrorCount(),
                metric.getTotalTime(),
                metric.calculateThroughput(),
                metric.getAvgLatency()
            );
        }

        for (int i=0; i<104; i++) {
            System.out.print("-");
        }
        System.out.println();
    }
}
