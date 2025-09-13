package analyser.config;

public class Configuration {
    private int toProduce;
    private int toConsume;
    private int producerCount;
    private int consumerCount;
    private int cap;
    private int elementSize;
    private int operationalScale;

    public Configuration() {}

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

    public void setToProduce(int toProduce) {
        this.toProduce = this.toConsume = toProduce;
    }

    public void setProducerCount(int producerCount) {
        this.producerCount = producerCount;
    }

    public void setConsumerCount(int consumerCount) {
        this.consumerCount = consumerCount;
    }

    public void setCap(int cap) {
        this.cap = cap;
    }

    public void setElementSize(int elementSize) {
        this.elementSize = elementSize;
    }

    public void setOperationalScale(int operationalScale) {
        this.operationalScale = operationalScale;
    }
}
