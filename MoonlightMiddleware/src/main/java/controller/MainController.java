package controller;

import dataconverter.DataConverter;
import dataconverter.OnlineMoonlightDataConverter;
import eu.quanticol.moonlight.domain.AbstractInterval;
import eu.quanticol.moonlight.formula.Formula;
import eu.quanticol.moonlight.io.MoonLightRecord;
import eu.quanticol.moonlight.signal.online.SegmentInterface;
import eu.quanticol.moonlight.signal.online.SpaceTimeSignal;
import eu.quanticol.moonlight.signal.online.TimeChain;
import eu.quanticol.moonlight.signal.online.Update;
import eu.quanticol.moonlight.space.SpatialModel;
import messages.Message;
import services.MonitorType;
import services.OnlineMoonlightService;
import services.Service;
import services.ServiceHandler;
import subscriber.MQTTSubscriber;
import subscriber.Subscriber;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Main entry point of the middleware
 */
public class MainController implements Controller{
    ServiceHandler<Update<Double, List<MoonLightRecord>>, Message> serviceHandler;
    Subscriber<Message> subscriber;
    DataConverter<Update<Double, List<MoonLightRecord>>, Message> dataConverter;
    String broker = "tcp://broker.mqttdashboard.com:1883";
    Service<Update<Double, List<MoonLightRecord>>, SpaceTimeSignal<Double, AbstractInterval<Boolean>>> service;
    private ConnType connectionType;
    private MonitorType monitorType;
    private SpatialModel<?> model;
    private Formula formula;
    private SpaceTimeSignal<Double, AbstractInterval<Boolean>> result;

    public void initializeService() {
        if(monitorType == MonitorType.ONLINE_MOONLIGHT) {
            //TODO: add distance function, atoms
            service = new OnlineMoonlightService(formula, model);
        } else
            throw new UnsupportedOperationException("Not supported monitor type");

        service.init();
    }

    public void establishConnection() {
        if(connectionType == ConnType.MQTT){
            //TODO: create a subscriber. One subscriber for each protocol?
            subscriber = new MQTTSubscriber(broker, this);
        }else if (connectionType == ConnType.REST){
            throw new UnsupportedOperationException("Rest not implemented");
        }else
            throw new UnsupportedOperationException("Unknown connection type");

    }

    public void runForever() {
        //TODO: run forever
        /*
        What does this do?
        ...
        1. getmessage?
        2. convert the data
        3. send update to the service
        4. serviceHandler.startService();
        5. serviceHandler.getResults(); (?)
         */
        throw new UnsupportedOperationException("This is not implemented yet");
    }

    public void updateData(Message message) {
        service.askService(dataConverter.fromMessageToMonitorData(message));
        service.run();
        result = service.getResponseFromService();

    }

    @Override
    public void setDataSource(String sourceId) {
        broker = sourceId;
    }

    @Override
    public void setConnectionType(ConnType connectionType) {
        this.connectionType = connectionType;
    }

    @Override
    public void setMonitorType(MonitorType monitorType) {
        this.monitorType = monitorType;

    }

    @Override
    public void setFormula(Formula formula) {
        this.formula = formula;
    }

    @Override
    public void setSpatialModel(SpatialModel<?> model) {
        this.model = model;
    }

    @Override
    public boolean run() {
        try {
            initializeService();
            establishConnection();
            subscriber.subscribe("iot/sensors");
            dataConverter = new OnlineMoonlightDataConverter();
            return true;
        } catch (Exception e) {
            // TODO: dangerous catch-all exception, refactor
            return false;
        }
    }

    @Override
    public List<String> getResults() {
        return result.getSegments().toList().stream() // converts signal to a list
                     .map(Object::toString) // convert signal segments to strings
                     .collect(Collectors.toList()); // recollect as list of strings
    }
}
