package analyser.util;

import java.util.UUID;

// configurable unique element
public class Element {
    private final UUID uniqueID;
    private final String createdBy;
    private final byte[] array;

    public Element() {
        this.uniqueID = UUID.randomUUID();
        this.createdBy = Thread.currentThread().getName();
        this.array = new byte[10];
    }

    public Element(int elementSize) {
        this.uniqueID = UUID.randomUUID();
        this.createdBy = Thread.currentThread().getName();
        this.array = new byte[elementSize];
    }

    public byte[] getArray() {
        return array;
    }

    public UUID getUniqueID() {
        return uniqueID;
    }

    public void compute(int scale) {
        for (int i=0; i<calculateExactComputeCount(scale); i++) array[i] += 1;
    }

    @Override
    public String toString() {
        return uniqueID+" by "+createdBy;
    }

    private int calculateExactComputeCount(int scale) {
        return (array.length * scale * 10 ) / 100;
    }
}
