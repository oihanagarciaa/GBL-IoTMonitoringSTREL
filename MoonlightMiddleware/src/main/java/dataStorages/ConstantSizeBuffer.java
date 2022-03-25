package dataStorages;

import eu.quanticol.moonlight.online.signal.TimeChain;
import messages.CommonSensorsMessage;

import java.util.List;

public class ConstantSizeBuffer<E> implements Buffer<E>{
    private final int maxCapacity;
    private final DataStoringTimeChain<E> storingTimeChain;
    private final TimerSynchronization timerSynchronization;
    private boolean canMonitor;
    int counter;
    double time;

    public ConstantSizeBuffer(int spatialModelSize, int bufferSize) {
        time = 0;
        counter = 0;
        maxCapacity = bufferSize;
        storingTimeChain = new DataStoringTimeChain<>(spatialModelSize);
        timerSynchronization = new TimerSynchronization(spatialModelSize);
        canMonitor = false;
    }

    private boolean bufferIsFull() {
        return counter >= maxCapacity;
    }

    //TODO: Think how to declare the messages better
    @Override
    public boolean add(CommonSensorsMessage message) {
        int id = message.getId();
        double time = message.getTime();
        if(canMonitor){
            if(time > this.time) this.time = time;
            storingTimeChain.saveNewValue(id,
                    time-timerSynchronization.getIthTimeDifference(id), (E) message.getValue());
            counter++;
            if (bufferIsFull()) return true;
        }else {
            timerSynchronization.setNewTime(id, time);
            storingTimeChain.saveNewValue(id,
                    time, (E) message.getValue());
            if(timerSynchronization.allValuesPresent()){
                canMonitor = true;
                storingTimeChain.initStoringTimeChain();
            }
        }
        return false;
    }

    @Override
    public TimeChain<Double, List<E>> get() {
        return storingTimeChain.getDataToMonitor(time);
    }

    @Override
    public void flush() {
        counter = 0;
    }
}
