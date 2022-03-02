package controller;

import dataconverter.DataConverter;
import dataconverter.OnlineMoonlightBuffer;
import eu.quanticol.moonlight.domain.AbstractInterval;
import eu.quanticol.moonlight.formula.Formula;
import eu.quanticol.moonlight.io.MoonLightRecord;
import eu.quanticol.moonlight.signal.online.SpaceTimeSignal;
import eu.quanticol.moonlight.signal.online.Update;
import eu.quanticol.moonlight.space.DistanceStructure;
import eu.quanticol.moonlight.space.SpatialModel;
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
public class MainController implements Controller {
    //TODO: subscriber receives String, maybe change to Message
    Subscriber<String> subscriber;
    DataConverter<Update<Double, List<MoonLightRecord>>, String> dataConverter;
    String broker;
    Service<Update<Double, List<MoonLightRecord>>, SpaceTimeSignal<Double, AbstractInterval<Boolean>>> service;
    private ConnType connectionType;
    private MonitorType monitorType;

    private SpatialModel<Double> model;
    private Formula formula;
    private SpaceTimeSignal<Double, AbstractInterval<Boolean>> result;
    private Map<String, Function<MoonLightRecord, AbstractInterval<Boolean>>> atoms;
    private HashMap<String, Function<SpatialModel<Double>, DistanceStructure<Double, ?>>> distanceFunctions;

    public void initializeService() {
        if(monitorType == MonitorType.ONLINE_MOONLIGHT) {
            service = new OnlineMoonlightService(formula, model, atoms, distanceFunctions);
            dataConverter = new OnlineMoonlightBuffer();
        } else {
            throw new UnsupportedOperationException("Not supported monitor type");
        }
        service.init();
    }

    public void establishConnection() {
        if(connectionType == ConnType.MQTT){
            subscriber = new MQTTSubscriber(broker, this);
            subscriber.subscribe("institute/thingy/#");
        }else if (connectionType == ConnType.REST){
            throw new UnsupportedOperationException("Rest not implemented");
        }else
            throw new UnsupportedOperationException("Unknown connection type");

    }

    public void runForever() {
        //TODO: run forever (Quit?)
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

    //TODO: Aggregation -> think how to do it better
    int cont = 0;
    final static int frecuency = 3;
    public void updateData(int id, String message) {
        cont = (++cont)%frecuency;
        if(cont != 0){
            dataConverter.fromMessageToMonitorData(id, message);
        }else{
            service.updateService(dataConverter.fromMessageToMonitorData(id, message));
            service.run();
            result = service.getResponseFromService();
            //TODO: Quit print line
            System.out.println("Results: "+getResults());
        }
    }

    @Override
    public void setDataSource(String sourceBrokerId) {
        broker = sourceBrokerId;
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
    public void setSpatialModel(SpatialModel<Double> model) {
        this.model = model;
    }

    @Override
    public void setAtomicFormulas(Map<String, Function<MoonLightRecord, AbstractInterval<Boolean>>> atoms) {
        this.atoms = atoms;
    }

    @Override
    public void setDistanceFunctions(HashMap<String, Function<SpatialModel<Double>, DistanceStructure<Double, ?>>> distanceFunctions) {
        this.distanceFunctions = distanceFunctions;
    }

    @Override
    public boolean run() {
        try {
            initializeService();
            establishConnection();
            dataConverter.initDataConverter(model.size());
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
