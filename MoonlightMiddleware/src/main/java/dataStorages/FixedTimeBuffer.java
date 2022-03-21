package dataStorages;

import controller.MainController;
import messages.Message;
import services.Service;

import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;

public class FixedTimeBuffer<E> implements Buffer<E>{
    private final Timer timer;
    private final int size;
    private final Service<E, ?> service;
    private final DataStoringTimeChain<E> storingTimeChain;
    private MainController controller;
    private E defaultValue;

    public FixedTimeBuffer(MainController controller, int spatialModelSize, Service<E, ?> serviceToConnect, long timePeriod){
        this.controller = controller;

        size = spatialModelSize;
        service = serviceToConnect;

        storingTimeChain = new DataStoringTimeChain<>(size);

        timer = new Timer("Timer");
        timer.scheduleAtFixedRate(new TaskMonitorAndFlush(), 0, timePeriod);
    }

    public class TaskMonitorAndFlush extends TimerTask {
        @Override
        public void run() {
            if(storingTimeChain.allValuesPresent()){
                storingTimeChain.setDefaultValue(defaultValue);
                service.run(storingTimeChain.getDataToMonitor());
                flush();
                controller.updateResponse();
            }
        }
    }

    @Override
    public boolean add(Message message) {
        if(defaultValue == null) defaultValue = (E) message.getDefaultValue();
        storingTimeChain.saveNewValue(message.getId(), message.getTime(), (E) message.getValueElement());
        return false;
    }

    @Override
    public Collection<E> get() {
        return storingTimeChain.getAllValues();
    }

    @Override
    public void flush() {
        //TODO: ?¿???¿?
        //storingTimeChain.deleteValues();
    }
}
