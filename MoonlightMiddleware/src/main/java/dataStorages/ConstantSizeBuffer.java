package dataStorages;

import eu.quanticol.moonlight.online.signal.TimeChain;
import messages.CommonSensorsMessage;

import java.util.List;

public class ConstantSizeBuffer<E> implements Buffer<E>{
    private final int maxCapacity;
    DataStoringTimeChain<E> storingTimeChain;
    int counter;

    public ConstantSizeBuffer(int spatialModelSize, int bufferSize) {
        counter = 0;
        maxCapacity = bufferSize;
        storingTimeChain = new DataStoringTimeChain<>(spatialModelSize);
    }

    private boolean bufferIsFull() {
        return counter >= maxCapacity;
    }

    //TODO: Think how to declare the messages better
    @Override
    public boolean add(CommonSensorsMessage message) {
        storingTimeChain.saveNewValue(message.getId(), message.getTime(), (E) message.getValue());
        counter ++;
        if(bufferIsFull() && storingTimeChain.allValuesPresent()) {
//            storingTimeChain.setDefaultValue((E) message.getDefaultValue());
//            connectedService.run(storingTimeChain.getDataToMonitor());
//            flush();
            return true;
        }
        return false;
    }

    @Override
    public TimeChain<Double, List<E>> get() {
        return storingTimeChain.getDataToMonitor();
    }

    public TimeChain<Double, List<E>> getDataToMonitor(){
        return storingTimeChain.getDataToMonitor();
    }

    @Override
    public void flush() {
        counter = 0;
    }
}
