package dataStorages;

import messages.Message;
import services.Service;

import java.util.ArrayList;
import java.util.List;

//TODO: This is not used anymore
public class ConstantSizeBuffer<E> implements Buffer<E>{
    private final int maxCapacity;
    private List<E> elements;
    private final Service<E, ?> connectedService;

    public ConstantSizeBuffer(int bufferSize, Service<E, ?> serviceToConnect) {
        maxCapacity = bufferSize;
        elements = new ArrayList<>(bufferSize);
        connectedService = serviceToConnect;
    }

    private boolean bufferIsFull() {
        return elements.size() == maxCapacity - 1;
    }

    @Override
    public boolean add(Message message) {
        elements.add((E) message.getValueElement());
        if(bufferIsFull()) {
            //TODO: This is wrong
            //connectedService.updateService(get());
            //connectedService.run(get());
            flush();
            return true;
        }
        return false;
    }

    @Override
    public List<E> get() {
        return elements;
    }

    @Override
    public void flush() {
        elements = new ArrayList<>(maxCapacity);
    }
}
