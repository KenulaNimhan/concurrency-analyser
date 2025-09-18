package analyser.util.constant;

import analyser.util.PerformanceMetrics;

public enum StackType {
    BASIC("Basic Stack"),
    NAIVE_SYNC("Naive Sync Stack"),
    SYNC("Synchronised Stack"),
    LOCK_BASED("Lock Based Stack"),
    LOCK_FREE("Lock Free Stack");

    private final PerformanceMetrics metrics;

    StackType(String name) {
        this.metrics = new PerformanceMetrics(name);
    }

    public PerformanceMetrics getMetrics() {
        return metrics;
    }


}
