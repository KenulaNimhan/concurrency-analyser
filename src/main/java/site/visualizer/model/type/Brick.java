package site.visualizer.model.type;

import java.util.UUID;

// mimics a brick help to visualize stacking concept.
public class Brick {
    private final UUID uniqueID;

    public Brick(UUID uniqueID) {
        this.uniqueID = UUID.randomUUID();
    }

    @Override
    public String toString() {
        return String.valueOf(uniqueID);
    }
}
