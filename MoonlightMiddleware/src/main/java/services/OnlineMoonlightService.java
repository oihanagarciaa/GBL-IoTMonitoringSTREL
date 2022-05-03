package services;

import dataStorages.Buffer;
import dataStorages.ConstantSizeBuffer;
import eu.quanticol.moonlight.core.base.Tuple;
import eu.quanticol.moonlight.core.space.*;
import eu.quanticol.moonlight.domain.BooleanDomain;
import eu.quanticol.moonlight.core.base.Box;
import eu.quanticol.moonlight.core.formula.Formula;
import eu.quanticol.moonlight.core.signal.SpaceTimeSignal;
import eu.quanticol.moonlight.core.space.DistanceStructure;
import eu.quanticol.moonlight.core.space.SpatialModel;
import eu.quanticol.moonlight.online.monitoring.OnlineSpatialTemporalMonitor;
import eu.quanticol.moonlight.space.StaticLocationService;
import main.DataBus;
import messages.CommonSensorsMessage;
import messages.Message;
import messages.ResultsMessage;

import java.util.Map;
import java.util.function.Function;

public class OnlineMoonlightService implements Service{
    private final Formula formula;
    private final SpatialModel<Double> spatialModel;
    private final Map<String, Function<Tuple, Box<Boolean>>> atoms;
    private final Map<String, Function<SpatialModel<Double>, DistanceStructure<Double, ?>>> distanceFunctions;
    private final LocationService<Double, Double> locSvc;

    private OnlineSpatialTemporalMonitor<?, Tuple, Boolean> onlineMonitor;

    private Buffer<Tuple> buffer;
    private DataBus dataBus;

    public OnlineMoonlightService(Formula formula, SpatialModel<Double> model,
            Map<String, Function<Tuple, Box<Boolean>>> atoms,
            Map<String, Function<SpatialModel<Double>, DistanceStructure<Double, ?>>> distanceFunctions) {
        this.formula = formula;
        this.spatialModel = model;
        this.atoms = atoms;
        locSvc = new StaticLocationService<>(this.spatialModel);
        this.distanceFunctions = distanceFunctions;
        //TODO: Change the declaration of the buffer
        buffer = new ConstantSizeBuffer<>(model.size(), 6);
        //buffer = new FixedTimeBuffer<>(this, model.size(),service, 10000);
    }

    @Override
    public boolean isRunning() {
        return onlineMonitor!=null;
    }

    //TODO: Use pattern matching
    @Override
    public void receive(Message message) {
        if(message instanceof CommonSensorsMessage){
            if(buffer.add((CommonSensorsMessage) message)){
                sendResults();
            }
        }
    }

    public void sendResults(){
        SpaceTimeSignal<Double, Box<Boolean>> results;
        results = onlineMonitor.monitor(buffer.get());
        buffer.flush();
        ResultsMessage resultsMessage = new ResultsMessage(results);
        dataBus.offer(resultsMessage);
    }

    @Override
    public void init() {
        onlineMonitor = new OnlineSpatialTemporalMonitor<>(
                formula, spatialModel.size(), new BooleanDomain(), //TODO: change Domain type
                locSvc, atoms, distanceFunctions);
        dataBus = DataBus.getInstance();
    }

    @Override
    public void stop() {
        onlineMonitor = null;
    }
}
