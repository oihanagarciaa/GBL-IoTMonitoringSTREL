package data;

import eu.quanticol.moonlight.signal.online.Update;

import java.util.ArrayList;
import java.util.List;

/**
 * - Collect the received messages,
 * - When all the locations are present,
 * - it aggregates them into a single Update
 */
public class UpdateBuilder<E> {
    DataStoringTimeChain<E> dataStoringTimeChain;
    private final int numberOfLocations;
    private List<E> data;

    public UpdateBuilder(int numberOfLocations) {
        dataStoringTimeChain = new DataStoringTimeChain<>(numberOfLocations);
        this.numberOfLocations = numberOfLocations;
        data = new ArrayList<>(numberOfLocations);
    }

    public void addNewValue(int id, double time, E value){
        //TODO: Deal with the control of the locations
        // (i.e. all locations are present or a new value is going to override a value)
        dataStoringTimeChain.saveNewValue(id, time, value);
        data.set(id, value);
    }

    //TODO: Maybe change the way to create a new update
    public Update<Double, List<E>> getUpdate(double startTime, double endTime) {
        Update<Double, List<E>> update;
        List<E> values = new ArrayList<>();
        List<List<Update<Double, E>>> allUpdates = dataStoringTimeChain.getUpdates();
        for (List<Update<Double, E>> updates:allUpdates){
            updates.stream().forEach(u -> {
                if(u.getStart() <= startTime && u.getEnd() >=endTime){
                    values.add(u.getValue());
                }
            });
        }
        update = new Update<>(startTime, endTime, values);
        return update;
    }
}
