package analyser.config;

public class Configurable {
    private int toProduce;
    private int toConsume;
    private int producerCount;
    private int consumerCount;
    private int cap;
    private int elementSize;
    private int operationalScale;

    public Configurable() {}

    // GETTERS
    public int getToProduce() {
        return toProduce;
    }

    public int getToConsume() {
        return toConsume;
    }

    public int getProducerCount() {
        return producerCount;
    }

    public int getConsumerCount() {
        return consumerCount;
    }

    public int getCap() {
        return cap;
    }

    public int getElementSize() {
        return elementSize;
    }

    public int getOperationalScale() {
        return operationalScale;
    }

    // SETTERS

    public Configurable setToProduce(int toProduce) {
        this.toProduce = toProduce;
        return this;
    }

    public Configurable setToConsume(int toConsume) {
        this.toConsume = toConsume;
        return this;
    }

    public Configurable setProducerCount(int producerCount) {
        this.producerCount = producerCount;
        return this;
    }

    public Configurable setConsumerCount(int consumerCount) {
        this.consumerCount = consumerCount;
        return this;
    }

    public Configurable setCap(int cap) {
        this.cap = cap;
        return this;
    }

    public Configurable setElementSize(int elementSize) {
        this.elementSize = elementSize;
        return this;
    }

    public Configurable setOperationalScale(int operationalScale) {
        this.operationalScale = operationalScale;
        return this;
    }
}
