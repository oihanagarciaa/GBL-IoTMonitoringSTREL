package dataStorages;

import eu.quanticol.moonlight.io.MoonLightRecord;
import eu.quanticol.moonlight.signal.Segment;
import eu.quanticol.moonlight.signal.online.TimeChain;
import eu.quanticol.moonlight.signal.online.TimeSegment;
import eu.quanticol.moonlight.signal.online.Update;

import java.util.ArrayList;
import java.util.Comparator;
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

    /**
     * There are n TimeChains, one for each sensor
     * Converts all the TimeChains in a single one
     * @return A single TimeChain
     */
    public TimeChain<Double, List<V>> getDataToMonitor(){
        //TODO: Create a single TimeChain with all the TimeChains
        List<V> valueList = new ArrayList<>();
        double endTime = timechains.stream().max(Comparator.comparing(TimeChain::getEnd)).get().getEnd();
        TimeChain<Double, List<V>> joinedTimeChain = new TimeChain<>(endTime);
        List<TimeChain<Double, V>> timeChainsCopy = List.copyOf(timechains);
        boolean stop = false;
        double time = timechains.stream().min(Comparator.comparing(TimeChain::getStart)).get().getStart();

        while (stop == false){
            for(int i = 0; i < size; i++){
                valueList.add(timeChainsCopy.get(i).get(0).getValue());
            }
            joinedTimeChain.add(new Segment<>(time, valueList));
            time = timeChainsCopy.stream().min(Comparator.comparing(TimeChain::getEnd)).get().getEnd();
            double finalTime = time;
            timeChainsCopy.removeIf(value -> value.get(0).getEnd() <= finalTime);
            if(time == endTime) stop = true;
        }

        return joinedTimeChain;
    }

    public void deleteValues(){
        timechains.removeAll(timechains);
    }
}
