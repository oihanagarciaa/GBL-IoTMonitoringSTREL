package dataStorages;

import builders.MoonlightServiceBuilder;
import eu.quanticol.moonlight.online.signal.TimeChain;
import messages.CommonSensorsMessage;
import services.Service;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class FixedTimeBuffer<E> implements Buffer<E>{
    private final Timer timer;
    private final int size;
    private final Service service;
    private final DataStoringTimeChain<E> storingTimeChain;
    private MoonlightServiceBuilder controller;
    private E defaultValue;

    public FixedTimeBuffer(MoonlightServiceBuilder controller, int spatialModelSize, Service serviceToConnect, long timePeriod){
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
                //service.run(storingTimeChain.getDataToMonitor());
                flush();
                //controller.updateResponse();
            }
        }
    }

    @Override
    public boolean add(CommonSensorsMessage message) {
        //if(defaultValue == null) defaultValue = (E) message.getDefaultValue();
        //storingTimeChain.saveNewValue(message.getId(), message.getTime(), (E) message.getValueElement());
        return false;
    }

    @Override
    public TimeChain<Double, List<E>> get() {
        return storingTimeChain.getDataToMonitor(10.0);
    }

    @Override
    public void flush() {
        //TODO: ?¿???¿?
        //storingTimeChain.deleteValues();
    }
}
