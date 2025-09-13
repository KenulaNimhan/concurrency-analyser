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

    public void compute(int scale) {
        for (int i=0; i<scale; i++) {
            array[i] += 1;
        }
    }

    public UUID getUniqueID() {
        return uniqueID;
    }

    @Override
    public String toString() {
        return uniqueID+" by "+createdBy;
    }
}
