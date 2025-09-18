package analyser.util;

public enum QueueType {
    BASIC("Basic Queue"),
    NAIVE_SYNC("Naive Sync Queue"),
    SYNC("Synchronised Queue"),
    LOCK_BASED("Lock Based Queue"),
    LOCK_FREE("Lock Free Queue");

    private final PerformanceMetrics metrics;

    QueueType(String name) {
        this.metrics = new PerformanceMetrics(name);
    }

    public PerformanceMetrics getMetrics() {
        return metrics;
    }
}
