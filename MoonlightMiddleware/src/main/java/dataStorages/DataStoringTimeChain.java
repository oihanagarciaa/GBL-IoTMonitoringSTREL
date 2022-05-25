package dataStorages;

import dataConverters.TimeChainConverter;
import dataConverters.TimeChainSplitter;
import eu.quanticol.moonlight.offline.signal.Segment;
import eu.quanticol.moonlight.online.signal.TimeChain;
import eu.quanticol.moonlight.online.signal.TimeSegment;

import java.util.*;

/**
 * All the values of the sensors will be stored here
 * There is a time chain for every sensor to track the values
 * @param <V> Values of a unique sensor
 */
public class DataStoringTimeChain<V>{
    protected List<TimeChain<Double, V>> timeChainList;
    private final int size;
    private int[] locations;
    private boolean allElements;
    private final TimeChainConverter<V> timeChainConverter;
    private final TimeChainSplitter<V> timeChainSplitter;

    public DataStoringTimeChain(int size){
        timeChainConverter = new TimeChainConverter<>(size);
        timeChainSplitter = new TimeChainSplitter<>();
        this.size = size;
        locations = new int[size];
        for (int i = 0; i < size; i++){
            locations[i] = 0;
        }
        allElements = false;
        initTimeChains();
    }

    private void initTimeChains(){
        timeChainList = new ArrayList<>();
        for(int i = 0; i < size; i++){
            TimeChain<Double, V> timeChain = new TimeChain<>(Double.MAX_VALUE);
            timeChainList.add(timeChain);
        }
    }

    public boolean saveNewValue(int id, double time, V value){
        double lastTime = 0.0;
        if(timeChainList.get(id).size() > 0){
            lastTime = timeChainList.get(id).getLast().getStart();
        }
        if(time >= lastTime){
            timeChainList.get(id).add(new TimeSegment<>(time, value));
            if(!allElements) locations[id] = 1;
            allValuesPresent();
            return true;
        }
        return false;
    }

    public TimeChain<Double, List<V>> getDataToMonitor(double time){
        List<TimeChain<Double, V>> monitorTimeChain = getTimeChainToMonitor(time);
        return timeChainConverter.fromListToTimeChain(monitorTimeChain, time);
    }

    private List<TimeChain<Double,V>> getTimeChainToMonitor(double time) {
        List<TimeChain<Double,V>>[] timeChainArray =
                timeChainSplitter.splitTimeChain(timeChainList, time);
        timeChainList = timeChainArray[1];
        return timeChainArray[0];
    }

    public boolean allValuesPresent(){
        if (allElements) return true;
        else {
            for (int i = 0; i < size; i++){
                if (locations[i] == 0) return false;
            }
            allElements = true;
        }
        return true;
    }

    public void initStoringTimeChain() {
        List<TimeChain<Double, V>> newTimeChainList = new ArrayList<>();
        for(int i = 0; i < size; i++){
            TimeChain<Double, V> newTimeChain = new TimeChain<>(new Segment<>(
                    0.0, timeChainList.get(i).getLast().getValue()), Double.MAX_VALUE);
            newTimeChainList.add(newTimeChain);
        }
        timeChainList = newTimeChainList;
    }
}
