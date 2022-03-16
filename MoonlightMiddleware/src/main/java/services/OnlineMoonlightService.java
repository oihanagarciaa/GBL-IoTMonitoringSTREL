package services;

import eu.quanticol.moonlight.domain.BooleanDomain;
import eu.quanticol.moonlight.formula.Formula;
import eu.quanticol.moonlight.signal.online.TimeChain;
import eu.quanticol.moonlight.space.DistanceStructure;
import eu.quanticol.moonlight.space.LocationService;
import eu.quanticol.moonlight.space.SpatialModel;
import eu.quanticol.moonlight.domain.AbstractInterval;
import eu.quanticol.moonlight.io.MoonLightRecord;
import eu.quanticol.moonlight.monitoring.online.OnlineSpaceTimeMonitor;
import eu.quanticol.moonlight.signal.online.SpaceTimeSignal;
import eu.quanticol.moonlight.signal.online.Update;
import eu.quanticol.moonlight.space.StaticLocationService;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class OnlineMoonlightService implements Service<MoonLightRecord,
        SpaceTimeSignal<Double, AbstractInterval<Boolean>>>{

    private final Formula formula;
    private final SpatialModel<Double> spatialModel;
    private final Map<String, Function<MoonLightRecord, AbstractInterval<Boolean>>> atoms;
    private final Map<String, Function<SpatialModel<Double>, DistanceStructure<Double, ?>>> distanceFunctions;
    private final LocationService<Double, Double> locSvc;

    private OnlineSpaceTimeMonitor<?, MoonLightRecord, Boolean> onlineMonitor;

    private SpaceTimeSignal<Double, AbstractInterval<Boolean>> results;

    public OnlineMoonlightService(Formula formula, SpatialModel<Double> model,
            Map<String, Function<MoonLightRecord, AbstractInterval<Boolean>>> atoms,
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
    public void run(Update<Double, List<MoonLightRecord>> update) {
        results = onlineMonitor.monitor(update);
    }

    @Override
    public void run(TimeChain<Double, List<MoonLightRecord>> timeChain) {
        results = onlineMonitor.monitor(timeChain);
    }

    @Override
    public void init() {
        onlineMonitor = new OnlineSpaceTimeMonitor<>(
                formula, spatialModel.size(), new BooleanDomain(),
                locSvc, atoms, distanceFunctions);
    }

    @Override
    public void stop() {
        onlineMonitor = null;
    }


    @Override
    public SpaceTimeSignal<Double, AbstractInterval<Boolean>> getResponseFromService() {
        return results;
    }

}
