package dataStorages;

import controller.MainController;
import messages.Message;
import services.Service;

import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;

public class FixedTimeBuffer<E> implements Buffer<E>{
    Timer timer;
    int size;
    Service<E, ?> service;
    DataStoringTimeChain<E> storingTimeChain;
    MainController controller;

    public FixedTimeBuffer(MainController controller, int spatialModelSize, Service<E, ?> serviceToConnect, long timePeriod){
        this.controller = controller;

        size = spatialModelSize;
        service = serviceToConnect;

        //TODO: Change the null
        storingTimeChain = new DataStoringTimeChain<>(size, null);

        timer = new Timer("Timer");
        timer.scheduleAtFixedRate(new TaskMonitorAndFlush(), 0, timePeriod);
    }

    public class TaskMonitorAndFlush extends TimerTask {
        @Override
        public void run() {
            service.run(storingTimeChain.getDataToMonitor());
            flush();
            controller.updateResponse();
        }
    }

    @Override
    public boolean add(Message message) {
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
