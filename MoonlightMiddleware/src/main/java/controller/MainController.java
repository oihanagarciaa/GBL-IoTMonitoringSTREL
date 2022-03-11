package controller;

import dataStorages.Buffer;
import DELETEdataConverters.DataConverter;
import dataStorages.FixedRateTimeBuffer;
import DELETEdataConverters.MoonlightRecordConverter;
import eu.quanticol.moonlight.domain.AbstractInterval;
import eu.quanticol.moonlight.formula.Formula;
import eu.quanticol.moonlight.io.MoonLightRecord;
import eu.quanticol.moonlight.signal.online.SpaceTimeSignal;
import eu.quanticol.moonlight.space.DistanceStructure;
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
public class MainController implements Controller {
    //TODO: subscriber receives String, maybe change to Message
    Subscriber<String> subscriber;
    DataConverter<MoonLightRecord, String> dataConverter;
    String broker;
    Service<MoonLightRecord, SpaceTimeSignal<Double, AbstractInterval<Boolean>>> service;
    private ConnType connectionType;
    private MonitorType monitorType;
    Buffer<MoonLightRecord> buffer;
    private SpatialModel<Double> model;
    private Formula formula;
    private SpaceTimeSignal<Double, AbstractInterval<Boolean>> result;
    private Map<String, Function<MoonLightRecord, AbstractInterval<Boolean>>> atoms;
    private HashMap<String, Function<SpatialModel<Double>, DistanceStructure<Double, ?>>> distanceFunctions;

    public void initializeService() {
        if(monitorType == MonitorType.ONLINE_MOONLIGHT) {
            service = new OnlineMoonlightService(formula, model, atoms, distanceFunctions);
            dataConverter = new MoonlightRecordConverter();
            //buffer = new ConstantSizeBuffer<>(3, service);
            buffer = new FixedRateTimeBuffer<MoonLightRecord>(this, model.size(), service, 5000);
        } else {
            throw new UnsupportedOperationException("Not supported monitor type");
        }
        service.init();
    }

    public void establishConnection() {
        if(connectionType == ConnType.MQTT){
            subscriber = new MQTTSubscriber(broker, this);
            //TODO: Declare the topic somewhere else
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
            return true;
        } catch (Exception e) {
            // TODO: dangerous catch-all exception, refactor
            return false;
        }
    }

    @Override
    public List<String> getResults() {
        updateResponse();
        return result.getSegments().toList().stream() // converts signal to a list
                     .map(Object::toString) // convert signal segments to strings
                     .collect(Collectors.toList()); // recollect as list of strings
    }

}
