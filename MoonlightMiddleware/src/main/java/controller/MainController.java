package controller;

import dataStorages.Buffer;
import dataStorages.ConstantSizeBuffer;

import eu.quanticol.moonlight.core.base.Box;
import eu.quanticol.moonlight.core.base.Tuple;
import eu.quanticol.moonlight.core.formula.Formula;
import eu.quanticol.moonlight.core.signal.SpaceTimeSignal;
import eu.quanticol.moonlight.core.space.DistanceStructure;
import eu.quanticol.moonlight.core.space.SpatialModel;
import eu.quanticol.moonlight.core.formula.Formula;
import eu.quanticol.moonlight.core.space.DistanceStructure;
import eu.quanticol.moonlight.core.space.SpatialModel;

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
public class MainController implements Controller {
    //TODO: If I want to access a variable from a test make it protected
    Subscriber<String> subscriber;
    String broker;
    Service<Tuple, SpaceTimeSignal<Double, Box<Boolean>>> service;
    private ConnType connectionType;
    private MonitorType monitorType;
    protected Buffer<Tuple> buffer;
    private SpatialModel<Double> model;
    private Formula formula;
    private SpaceTimeSignal<Double, Box<Boolean>> result;
    private Map<String, Function<Tuple, Box<Boolean>>> atoms;
    private HashMap<String, Function<SpatialModel<Double>, DistanceStructure<Double, ?>>> distanceFunctions;

    public void initializeService() throws UnsupportedOperationException{
        if(monitorType == MonitorType.ONLINE_MOONLIGHT) {
            service = new OnlineMoonlightService(formula, model, atoms, distanceFunctions);
            //TODO: how to initialize the buffer
            buffer = new ConstantSizeBuffer<>(model.size(), 6, service);
        } else {
            throw new UnsupportedOperationException("Not supported monitor type");
        }
        service.init();
    }

    public void establishConnection() throws UnsupportedOperationException{
        if(connectionType == ConnType.MQTT){
            subscriber = new MQTTSubscriber(broker, this);
            //TODO: Declare the topic somewhere else
            subscriber.subscribe("institute/thingy/#");
        }else if (connectionType == ConnType.REST){
            throw new UnsupportedOperationException("Rest not implemented");
        }else
            throw new UnsupportedOperationException("Unknown connection type");
    }

    public void updateData(Message message) {
        if(buffer.add(message)){
            updateResponse();
        }
    }

    public void updateResponse() {
        result = service.getResponseFromService();
        //TODO: Quit print line
        System.out.println(getResults());
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
    public void setAtomicFormulas(Map<String, Function<Tuple, Box<Boolean>>> atoms) {
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
            return true;
        } catch (UnsupportedOperationException e) {
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
