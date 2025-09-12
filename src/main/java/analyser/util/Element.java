package analyser.util;

import java.util.UUID;

// configurable unique element
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
