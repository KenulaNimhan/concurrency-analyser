package analyser.config;

public enum ConfigKey {
    TO_PRODUCE("how many items needed to be produced & consumed",
            new int[]{1, 2_500_000}),
    PRODUCER_COUNT("how many producer threads",
            new int[]{1, 5000}),
    CONSUMER_COUNT("how many consumer threads",
            new int[]{1, 5000}),
    CAP("max no. of elements the data structure can hold",
            new int[]{1, 2_500_000}),
    ELEMENT_SIZE("size per element (in bytes)",
            new int[]{1, 2048}),
    OPERATIONAL_SCALE("estimated CPU usage for each operation (0 - 10 scale)",
            new int[]{0, 10});

    private final String prompt;
    private final int[] range;

    ConfigKey(String prompt, int[] range) {
        this.prompt = prompt;
        this.range = range;
    }

    public String getPrompt() {
        return this.prompt;
    }

    public int[] getRange() {
        return this.range;
    };
}
