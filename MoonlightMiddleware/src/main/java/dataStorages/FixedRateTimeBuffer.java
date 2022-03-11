package dataStorages;

import com.sun.tools.javac.Main;
import controller.Controller;
import controller.MainController;
import eu.quanticol.moonlight.signal.online.TimeChain;
import messages.Message;
import services.Service;

import java.util.Collection;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class FixedRateTimeBuffer<E> implements Buffer<E>{
    //TODO: see where to add the timer.cancel()
    Timer timer;
    int size;
    Service<E, ?> service;
    DataStoringTimeChain<E> storingTimeChain;
    MainController controller;

    public FixedRateTimeBuffer(MainController controller, int spatialModelSize, Service<E, ?> serviceToConnect, long timePeriod){
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
        return null;
    }

    @Override
    public void flush() {
        storingTimeChain.deleteValues();
    }
}
