package controller;

import dataconverter.DataConverter;
import dataconverter.OnlineMoonlightDataConverter;
import eu.quanticol.moonlight.domain.AbstractInterval;
import eu.quanticol.moonlight.formula.Formula;
import eu.quanticol.moonlight.io.MoonLightRecord;
import eu.quanticol.moonlight.signal.online.SpaceTimeSignal;
import eu.quanticol.moonlight.signal.online.Update;
import eu.quanticol.moonlight.space.DistanceStructure;
import eu.quanticol.moonlight.space.LocationService;
import eu.quanticol.moonlight.space.SpatialModel;
import messages.Message;
import services.MonitorType;
import services.OnlineMoonlightService;
import services.Service;
import subscriber.MQTTSubscriber;
import subscriber.Subscriber;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Main entry point of the middleware
 */
public class MainController implements Controller{
    Subscriber<Message> subscriber;
    DataConverter<Update<Double, List<MoonLightRecord>>, Message> dataConverter;
    String broker;
    Service<Update<Double, List<MoonLightRecord>>, SpaceTimeSignal<Double, AbstractInterval<Boolean>>> service;
    private ConnType connectionType;
    private MonitorType monitorType;
    private SpatialModel<?> model;
    private Formula formula;
    private SpaceTimeSignal<Double, AbstractInterval<Boolean>> result;
    private Map<String, Function<MoonLightRecord, AbstractInterval<Boolean>>> atoms;
    private LocationService<Double,?> locSvc;
    private HashMap<String, Function<SpatialModel<?>, DistanceStructure<?, Double>>> distanceFunctions;

    public void initializeService() {
        if(monitorType == MonitorType.ONLINE_MOONLIGHT) {
            //TODO: add distance function, atoms
            service = new OnlineMoonlightService(formula, model, atoms, locSvc, distanceFunctions);
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
    public void setAtomicFormulas(Map<String, Function<MoonLightRecord, AbstractInterval<Boolean>>> atoms) {
        this.atoms = atoms;
    }

    @Override
    public void setLocationService(LocationService<Double, ?> locSvc) {
        this.locSvc = locSvc;
    }

    @Override
    public void setDistanceFunctions(HashMap<String, Function<SpatialModel<?>, DistanceStructure<?, Double>>> distanceFunctions) {
        this.distanceFunctions = distanceFunctions;
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
