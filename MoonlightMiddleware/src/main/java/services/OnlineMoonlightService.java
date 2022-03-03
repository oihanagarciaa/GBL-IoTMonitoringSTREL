package services;

import eu.quanticol.moonlight.domain.BooleanDomain;
import eu.quanticol.moonlight.formula.Formula;
import eu.quanticol.moonlight.space.DistanceStructure;
import eu.quanticol.moonlight.space.LocationService;
import eu.quanticol.moonlight.space.SpatialModel;
import eu.quanticol.moonlight.domain.AbstractInterval;
import eu.quanticol.moonlight.io.MoonLightRecord;
import eu.quanticol.moonlight.monitoring.online.OnlineSpaceTimeMonitor;
import eu.quanticol.moonlight.signal.online.SpaceTimeSignal;
import eu.quanticol.moonlight.signal.online.Update;
import eu.quanticol.moonlight.util.Utils;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class OnlineMoonlightService implements Service<Update<Double, List<MoonLightRecord>>,
        SpaceTimeSignal<Double, AbstractInterval<Boolean>>>{

    private final Formula formula;
    private final SpatialModel<Double> spatialModel;
    private final Map<String, Function<MoonLightRecord, AbstractInterval<Boolean>>> atoms;
    private final Map<String, Function<SpatialModel<Double>, DistanceStructure<Double, ?>>> distanceFunctions;
    private final LocationService<Double, Double> locSvc;

    private OnlineSpaceTimeMonitor<?, MoonLightRecord, Boolean> onlineMonitor;

    private Update<Double, List<MoonLightRecord>> newUpdate;
    private SpaceTimeSignal<Double, AbstractInterval<Boolean>> results;

    public OnlineMoonlightService(Formula formula, SpatialModel<Double> model,
            Map<String, Function<MoonLightRecord, AbstractInterval<Boolean>>> atoms,
            Map<String, Function<SpatialModel<Double>, DistanceStructure<Double, ?>>> distanceFunctions) {
        this.formula = formula;
        this.spatialModel = model;
        this.atoms = atoms;
        //TODO: change start, end and dt?
        locSvc = Utils.createLocServiceStatic(0, 1, 1000, this.spatialModel);
        this.distanceFunctions = distanceFunctions;
    }

    @Override
    public boolean isRunning() {
        return onlineMonitor!=null;
    }

    @Override
    public void run() {
        results = onlineMonitor.monitor(newUpdate);
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
    public void updateService(Update<Double, List<MoonLightRecord>> update) {
        newUpdate = update;
    }

    @Override
    public SpaceTimeSignal<Double, AbstractInterval<Boolean>> getResponseFromService() {
        return results;
    }

}
