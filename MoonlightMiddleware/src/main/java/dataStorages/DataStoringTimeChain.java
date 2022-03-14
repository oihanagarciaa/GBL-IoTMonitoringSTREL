package dataStorages;

import eu.quanticol.moonlight.io.MoonLightRecord;
import eu.quanticol.moonlight.signal.DataHandler;
import eu.quanticol.moonlight.signal.EnumerationHandler;
import eu.quanticol.moonlight.signal.RecordHandler;
import eu.quanticol.moonlight.signal.Segment;
import eu.quanticol.moonlight.signal.online.TimeChain;
import eu.quanticol.moonlight.signal.online.TimeSegment;
import eu.quanticol.moonlight.signal.online.Update;

import java.util.*;
import java.util.stream.Stream;

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
        initTimeChains();
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
        TimeChain<Double, List<V>> returnTimeChain = new TimeChain<>(Double.MAX_VALUE);
        Map<Double, List<V>> map = new HashMap<>();
        //TODO: Quit factory
        RecordHandler factory;
        List<String> places;
        places = Arrays.asList("Hospital", "School", "MetroStop", "School", "MainSquare", "BusStop");
        factory = new RecordHandler(new EnumerationHandler<>(
                String.class, places.toArray(new String[0])), DataHandler.INTEGER, DataHandler.INTEGER);
        for(int i = 0; i < size; i++){
            for (int j = 0; j < timechains.get(i).size(); j++){
                double start =  timechains.get(i).get(j).getStart();
                V value = timechains.get(i).get(j).getValue();
                List<V> listValues = map.get(start);
                if( listValues == null){
                    listValues = new ArrayList<>();
                    for(int k = 0; k < size; k++){
                        listValues.add((V) factory.fromObjectArray("", null, null));
                    }
                }
                listValues.add(i, value);
                map.put(start, listValues);
            }
        }
        List<Double> sortedList = new ArrayList<>(map.keySet());
        Collections.sort(sortedList);
        sortedList.stream().forEach(time->returnTimeChain.add(new Segment<>(time, map.get(time))));
        return returnTimeChain;
    }

    public List<V> getAllValues(){
        List<V> listRecords = new ArrayList<>();
        for(int i = 0; i < size; i++){
            for (int j = 0; j < timechains.get(i).size(); j++){
                listRecords.add(timechains.get(i).get(j).getValue());
            }
        }
        return listRecords;
    }

    public void deleteValues(){
        timechains.removeAll(timechains);
        initTimeChains();
    }

    private void initTimeChains(){
        timechains = new ArrayList<>();
        for(int i = 0; i < size; i++){
            TimeChain<Double, V> timeChain = new TimeChain<Double, V>(Double.MAX_VALUE);
            timechains.add(timeChain);
        }
    }
}
