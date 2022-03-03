package data;

import eu.quanticol.moonlight.signal.online.Update;

import java.util.ArrayList;
import java.util.List;

/**
 * - Collect the received messages,
 * - When all the locations are present,
 * - it aggregates them into a single Update
 * TODO: not clear now, maybe not needed
 */
public class UpdateBuilder<E> {
    private final int numberOfLocations;
    private List<E> data;

    public UpdateBuilder(int numberOfLocations) {
        this.numberOfLocations = numberOfLocations;
        data = new ArrayList<>(numberOfLocations);
    }

    public Update<Double, E> getUpdate() {
        return null;
    }
}
