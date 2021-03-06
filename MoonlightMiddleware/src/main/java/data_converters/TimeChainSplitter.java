package data_converters;

import eu.quanticol.moonlight.online.signal.TimeChain;

import java.util.ArrayList;
import java.util.List;

public class TimeChainSplitter<V>{
    private double timeEnd;

    public TimeChainSplitter(){
        //Do nothing
    }

    public List<TimeChain<Double, V>>[] splitTimeChain(List<TimeChain<Double, V>> timeChainList, double time){
        timeEnd = time - 1;
        List<TimeChain<Double, V>> [] timeChains = new List[2];
        timeChains[0] = split1stToMonitor(timeChainList);
        timeChains[1] = split2ndNewStorage(timeChainList);
        return timeChains;
    }

    private List<TimeChain<Double, V>> split1stToMonitor(List<TimeChain<Double, V>> timeChainList){
        List<TimeChain<Double, V>> splitedTimeChain = new ArrayList<>();
        for (int i = 0; i < timeChainList.size(); i++){
            TimeChain<Double, V> timeChain = timeChainList.get(i);
            int to = getLastStart(timeChain) + 1;
            try {
                splitedTimeChain.add(timeChainList.get(i).subChain(0, to, timeEnd));
            }catch (IndexOutOfBoundsException | IllegalArgumentException e){
                splitedTimeChain.add(new TimeChain<>(timeEnd));
            }
        }
        return splitedTimeChain;
    }

    private List<TimeChain<Double, V>> split2ndNewStorage(List<TimeChain<Double, V>> timeChainList){
        List<TimeChain<Double, V>> splitedTimeChain = new ArrayList<>();
        for (int i = 0; i < timeChainList.size(); i++){
            TimeChain<Double, V> timeChain = timeChainList.get(i);
            int from = getLastStart(timeChain);
            if(from < 0) from = 0;
            try {
                splitedTimeChain.add(timeChain.subChain(from, timeChain.size(), Double.MAX_VALUE));
            } catch (IllegalArgumentException e){
                splitedTimeChain.add(timeChain);
            }
        }
        return splitedTimeChain;
    }

    private int getLastStart(TimeChain<Double, V> timeChainList){
        int lastStart = -1;
        for (int i = 0; i < timeChainList.size(); i++){
            if(timeChainList.get(i).getStart() < timeEnd){
                lastStart = i;
            }
        }
        return lastStart;
    }
}
