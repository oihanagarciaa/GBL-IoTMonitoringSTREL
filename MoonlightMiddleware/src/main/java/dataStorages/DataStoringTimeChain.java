package dataStorages;

import dataConverters.TimeChainConverter;
import eu.quanticol.moonlight.signal.DataHandler;
import eu.quanticol.moonlight.signal.EnumerationHandler;
import eu.quanticol.moonlight.signal.RecordHandler;
import eu.quanticol.moonlight.signal.Segment;
import eu.quanticol.moonlight.signal.online.TimeChain;
import eu.quanticol.moonlight.signal.online.TimeSegment;

import java.util.*;
import java.util.stream.Collectors;

/**
 * All the values of the sensors will be stored here
 * There is a time chain for every sensor to track the values
 * @param <V> Values of a unique sensor
 */
public class DataStoringTimeChain<V>{
    List<TimeChain<Double, V>> sensorValuesTimeChains;
    private final int size;
    private final TimeChainConverter<V> timeChainConverter;

    public DataStoringTimeChain(int size, V defaultValue){
        timeChainConverter = new TimeChainConverter<>(size, defaultValue);
        this.size = size;
        initTimeChains();
    }

    private void initTimeChains(){
        sensorValuesTimeChains = new ArrayList<>();
        for(int i = 0; i < size; i++){
            TimeChain<Double, V> timeChain = new TimeChain<Double, V>(Double.MAX_VALUE);
            sensorValuesTimeChains.add(timeChain);
        }
    }

    public void saveNewValue(int id, double time, V value){
        sensorValuesTimeChains.get(id).add(new TimeSegment<>(time, value));
    }


    public TimeChain<Double, List<V>> getDataToMonitor(){
        return timeChainConverter.fromMessageToData(sensorValuesTimeChains);
    }

    public List<V> getAllValues(){
        List<V> listRecords = new ArrayList<>();
        for(int i = 0; i < size; i++){
            for (int j = 0; j < sensorValuesTimeChains.get(i).size(); j++){
                listRecords.add(sensorValuesTimeChains.get(i).get(j).getValue());
            }
        }
        return listRecords;
    }
}
