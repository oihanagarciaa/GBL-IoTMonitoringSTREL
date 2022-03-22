package services;

import eu.quanticol.moonlight.core.base.Tuple;
import eu.quanticol.moonlight.core.space.*;
import eu.quanticol.moonlight.domain.BooleanDomain;
import eu.quanticol.moonlight.core.base.Box;
import eu.quanticol.moonlight.core.formula.Formula;
import eu.quanticol.moonlight.core.signal.SpaceTimeSignal;
import eu.quanticol.moonlight.core.space.DistanceStructure;
import eu.quanticol.moonlight.core.space.SpatialModel;
import eu.quanticol.moonlight.online.monitoring.OnlineSpatialTemporalMonitor;
import eu.quanticol.moonlight.online.signal.TimeChain;
import eu.quanticol.moonlight.online.signal.Update;
import eu.quanticol.moonlight.space.StaticLocationService;
import messages.Message;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class OnlineMoonlightService implements Service<Tuple,
        SpaceTimeSignal<Double, Box<Boolean>>>{

    private final Formula formula;
    private final SpatialModel<Double> spatialModel;
    private final Map<String, Function<Tuple, Box<Boolean>>> atoms;
    private final Map<String, Function<SpatialModel<Double>, DistanceStructure<Double, ?>>> distanceFunctions;
    private final LocationService<Double, Double> locSvc;

    private OnlineSpatialTemporalMonitor<?, Tuple, Boolean> onlineMonitor;

    private SpaceTimeSignal<Double, Box<Boolean>> results;

    public OnlineMoonlightService(Formula formula, SpatialModel<Double> model,
            Map<String, Function<Tuple, Box<Boolean>>> atoms,
            Map<String, Function<SpatialModel<Double>, DistanceStructure<Double, ?>>> distanceFunctions) {
        this.formula = formula;
        this.spatialModel = model;
        this.atoms = atoms;
        locSvc = new StaticLocationService<>(this.spatialModel);
        this.distanceFunctions = distanceFunctions;
    }

    @Override
    public boolean isRunning() {
        return onlineMonitor!=null;
    }

    @Override
    public void run(Update<Double, List<Tuple>> update) {
        results = onlineMonitor.monitor(update);
    }

    @Override
    public void receive(Message message) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void run(TimeChain<Double, List<Tuple>> timeChain) {
        results = onlineMonitor.monitor(timeChain);
    }

    @Override
    public void init() {
        onlineMonitor = new OnlineSpatialTemporalMonitor<>(
                formula, spatialModel.size(), new BooleanDomain(),
                locSvc, atoms, distanceFunctions);
    }

    @Override
    public void stop() {
        onlineMonitor = null;
    }


    @Override
    public SpaceTimeSignal<Double, Box<Boolean>> getResponseFromService() {
        return results;
    }

}
