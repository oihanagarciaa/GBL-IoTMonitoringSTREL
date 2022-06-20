package data_storages;

import java.util.ArrayList;
import java.util.List;

public class TimerSynchronization {
    private final int size;
    private List<Double> times;

    public TimerSynchronization(int size){
        this.size = size;
        times = new ArrayList<>();
        for (int i = 0; i < size; i++){
            times.add(null);
        }
    }

    public boolean allValuesPresent(){
        for (int i = 0; i < size; i++){
            if(times.get(i) == null){
                return false;
            }
        }
        return true;
    }

    public double getIthTimeDifference(int i){
        return times.get(i);
    }

    public void setNewTime(int i, double time){
        times.set(i, time);
    }
}
