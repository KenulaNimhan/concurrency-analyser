package site.analyser.util;

import java.util.UUID;

// mimics a brick help to visualize stacking concept.
public class Element {
    private final UUID uniqueID;
    private final String createdBy;

    public Element() {
        this.uniqueID = UUID.randomUUID();
        this.createdBy = Thread.currentThread().getName();
    }

    public UUID getUniqueID() {
        return uniqueID;
    }

    @Override
    public String toString() {
        return uniqueID+" by "+createdBy;
    }
}
