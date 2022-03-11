package dataStorages;

import messages.Message;
import services.Service;

import java.util.Collection;
import java.util.List;

public class ConstantSizeBuffer<E> implements Buffer<E>{
    private final int maxCapacity;
    DataStoringTimeChain<E> storingTimeChain;
    private final Service<E, ?> connectedService;
    int counter;

    public ConstantSizeBuffer(int spatialModelSize, int bufferSize, Service<E, ?> serviceToConnect) {
        counter = 0;

        maxCapacity = bufferSize;
        connectedService = serviceToConnect;

        storingTimeChain = new DataStoringTimeChain<>(spatialModelSize);
    }

    private boolean bufferIsFull() {
        return counter == maxCapacity;
    }

    @Override
    public boolean add(Message message) {
        storingTimeChain.saveNewValue(message.getId(), message.getTime(), (E) message.getValueElement());
        counter ++;
        if(bufferIsFull()) {
            connectedService.run(storingTimeChain.getDataToMonitor());
            flush();
            return true;
        }
        return false;
    }

    @Override
    public Collection<E> get() {
        return storingTimeChain.getAllValues();
    }

    @Override
    public void flush() {
        counter = 0;
        storingTimeChain.deleteValues();
    }
}
