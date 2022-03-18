package dataConverters;

import eu.quanticol.moonlight.offline.signal.Segment;
import eu.quanticol.moonlight.online.signal.TimeChain;

import java.util.*;
import java.util.stream.Collectors;

/**
 * There are n TimeChains, one for each sensor
 * Converts all the TimeChains into a single one
 */
public class TimeChainConverter<V> {
    private final V defaultValue;
    private final int size;
    private double previousTime;

    public TimeChainConverter(int size, V defaultValue){
        this.defaultValue = defaultValue;
        this.size = size;
        this.previousTime = 0;
    }

    public TimeChain<Double, List<V>> fromListToTimeChain(List<TimeChain<Double, V>> timeChainList, double finalTime) {
        Map<Double, Map<Integer, V>> valuesMap = createAValueMap(timeChainList);
        return createOneTimeChain(valuesMap, finalTime);
    }

    private Map<Double, Map<Integer, V>> createAValueMap(List<TimeChain<Double, V>> timeChainList){
        Map<Double, Map<Integer, V>> valuesMap = new HashMap<>();

        for(int i = 0; i < size; i++) {
            for (int j = 0; j < timeChainList.get(i).size(); j++) {
                double start = timeChainList.get(i).get(j).getStart();
                if(start < previousTime) start = previousTime;
                V value = timeChainList.get(i).get(j).getValue();

                Map<Integer, V> posValueMap = valuesMap.get(start);
                if (posValueMap == null) {
                    posValueMap = new HashMap<>();
                }
                posValueMap.put(i, value);
                valuesMap.put(start, posValueMap);
            }
        }
        return valuesMap;
    }

    //TODO: Think how to do this better
    private TimeChain<Double, List<V>> createOneTimeChain(Map<Double, Map<Integer, V>> valuesMap, double finalTime){
        TimeChain<Double, List<V>> returnTimeChain = new TimeChain<>(finalTime);
        List<V> listValues = initDefaultValues(defaultValue);

        List<Double> sortedList = new ArrayList<>(valuesMap.keySet());
        Collections.sort(sortedList);
        sortedList.stream().forEach(time->{
            Map<Integer, V> posValueMap = valuesMap.get(time);
            posValueMap.keySet().stream().forEach(index -> listValues.set(index, posValueMap.get(index)));
            List<V> copy = listValues.stream().collect(Collectors.toList());
            //TODO: Sometimes throws a exception
            returnTimeChain.add(new Segment<>(time, copy));
        });
        previousTime = finalTime;
        return returnTimeChain;
    }

    private List<V> initDefaultValues(V defaultValue){
        List<V> listValues = new ArrayList<>();
        for(int i = 0; i < size; i++){
            listValues.add(defaultValue);
        }
        return listValues;
    }
}
