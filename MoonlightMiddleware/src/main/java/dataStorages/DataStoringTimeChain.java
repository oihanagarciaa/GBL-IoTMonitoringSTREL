package dataStorages;

import dataConverters.TimeChainConverter;
import dataConverters.TimeChainSplitter;
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
    private V defaultValue;

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
            TimeChain<Double, V> timeChain = new TimeChain<Double, V>(Double.MAX_VALUE);
            timeChainList.add(timeChain);
        }
    }

    public void saveNewValue(int id, double time, V value){
        timeChainList.get(id).add(new TimeSegment<>(time, value));
        if(!allElements) locations[id] = 1;
        allValuesPresent();
        //TODO: change time
        if(this.time < time) this.time = time;
    }

    //TODO: Think how to specify the time
    double time = 0;
    public TimeChain<Double, List<V>> getDataToMonitor(){
        double time = this.time-1;
        List<TimeChain<Double, V>> monitorTimeChain = getTimeChainToMonitor(time);
        return timeChainConverter.fromListToTimeChain(monitorTimeChain, time, defaultValue);
    }

    public void setDefaultValue(V defaultValue){
        this.defaultValue = defaultValue;
    }

    private List<TimeChain<Double,V>> getTimeChainToMonitor(double time) {
        List<TimeChain<Double,V>>[] timeChainArray =
                timeChainSplitter.splitTimeChain(timeChainList, time);
        timeChainList = timeChainArray[1];
        return timeChainArray[0];
    }

    public List<V> getAllValues(){
        List<V> listRecords = new ArrayList<>();
        for(int i = 0; i < size; i++){
            for (int j = 0; j < timeChainList.get(i).size(); j++){
                listRecords.add(timeChainList.get(i).get(j).getValue());
            }
        }
        return listRecords;
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
}
