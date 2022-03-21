package dataStorages;

import messages.Message;
import services.Service;

import java.util.Collection;

public class ConstantSizeBuffer<E> implements Buffer<E>{
    private final int maxCapacity;
    DataStoringTimeChain<E> storingTimeChain;
    private final Service<E, ?> connectedService;
    int counter;

    public ConstantSizeBuffer(int spatialModelSize, int bufferSize, Service<E, ?> serviceToConnect) {
        counter = 0;

        maxCapacity = bufferSize;
        connectedService = serviceToConnect;

        //TODO: Change the null value
        storingTimeChain = new DataStoringTimeChain<>(spatialModelSize);
    }

    private boolean bufferIsFull() {
        return counter >= maxCapacity;
    }

    @Override
    public boolean add(Message message) {
        storingTimeChain.saveNewValue(message.getId(), message.getTime(), (E) message.getValueElement());
        counter ++;
        if(bufferIsFull() && storingTimeChain.allValuesPresent()) {
            storingTimeChain.setDefaultValue((E) message.getDefaultValue());
            connectedService.run(storingTimeChain.getDataToMonitor());
            flush();
            return true;
        }
        return false;
    }

    @Override
    public Collection<E> get() {
        return (Collection<E>)storingTimeChain.getAllValues();
    }

    @Override
    public void flush() {
        counter = 0;
        //storingTimeChain.deleteValues();
    }
}
