package data;

import services.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
    public boolean add(E element) {
        elements.add(element);
        if(bufferIsFull()) {
            connectedService.updateService(get());
            connectedService.run();
            flush();
            return true;
        }
        return false;
    }

    @Override
    public Collection<E> get() {
        return elements;
    }

    @Override
    public void flush() {
        elements = new ArrayList<>(maxCapacity);
    }
}
