package dataStorages;

import eu.quanticol.moonlight.io.MoonLightRecord;
import eu.quanticol.moonlight.signal.online.TimeChain;
import eu.quanticol.moonlight.signal.online.TimeSegment;
import eu.quanticol.moonlight.signal.online.Update;
import messages.Message;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * All the values of the sensors will be stored here
 * There is a time chain for every sensor to track the values
 * @param <V> Values of a unique sensor
 */
public class DataStoringTimeChain<V>{
    List<TimeChain<Double, V>> timechains;
    List<Update<Double, V>> updates = new ArrayList<>();
    private final int size;

    public DataStoringTimeChain(int size){
        this.size = size;
        timechains = new ArrayList<>();
        for(int i = 0; i < size; i++){
            //TODO: Think how to initialize the timechain
            TimeChain<Double, V> timeChain = new TimeChain<Double, V>(100.0);
            timechains.add(timeChain);
        }
    }

    public void saveNewValue(int id, double time, V value){
        if(id < 0 || id > timechains.size()){
            throw new UnsupportedOperationException("Not valid ID");
        }
        else{
            timechains.get(id).add(new TimeSegment<>(time, value));
        }
    }

    public TimeChain<Double, List<V>> getDataToMonitor(){
        //TODO: Create a single TimeChain with all the TimeChains
        /*Update.asTimeChain(updates);
        List<List<Update<Double, V>>> timechainsUpdates = new ArrayList<>();
        for (int i = 0; i< size; i++){
            timechainsUpdates.add(timechains.get(i).toUpdates());
        }*/
        return null;
    }

    public void deleteValues(){
        //TODO: DELETE THE TIME CHAIN VALUES
    }
}
