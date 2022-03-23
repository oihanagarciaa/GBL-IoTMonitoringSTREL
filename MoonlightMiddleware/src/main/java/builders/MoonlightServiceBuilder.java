package builders;

import dataStorages.Buffer;
import dataStorages.ConstantSizeBuffer;

import eu.quanticol.moonlight.core.base.Box;
import eu.quanticol.moonlight.core.base.Tuple;
import eu.quanticol.moonlight.core.formula.Formula;
import eu.quanticol.moonlight.core.signal.SpaceTimeSignal;
import eu.quanticol.moonlight.core.space.DistanceStructure;
import eu.quanticol.moonlight.core.space.SpatialModel;

import messages.Message;
import services.MonitorType;
import services.OnlineMoonlightService;
import services.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Main entry point of the middleware
 */
public class MoonlightServiceBuilder {
    //If I want to access a variable from a test make it protected
    private Service service;
    private MonitorType monitorType;
    private SpatialModel<Double> spatialModel;
    private Formula formula;
    private Map<String, Function<Tuple, Box<Boolean>>> atoms;
    private Map<String, Function<SpatialModel<Double>, DistanceStructure<Double, ?>>> distanceFunctions;

    public MoonlightServiceBuilder(SpatialModel<Double> spatialModel,
                                   Formula formula,
                                   Map<String, Function<Tuple, Box<Boolean>>> atoms,
                                   Map<String, Function<SpatialModel<Double>, DistanceStructure<Double, ?>>> distFunctions)
    {
        this.monitorType = MonitorType.ONLINE_MOONLIGHT;
        this.spatialModel = spatialModel;
        this.formula = formula;
        this.atoms = atoms;
        this.distanceFunctions = distFunctions;
    }

    public void initializeService() {
        if(monitorType == MonitorType.ONLINE_MOONLIGHT) {
            service = new OnlineMoonlightService(formula, spatialModel, atoms, distanceFunctions);
        } else {
            throw new UnsupportedOperationException("Not supported monitor type");
        }
        service.init();
    }

    public boolean run() {
        try {
            initializeService();
            return true;
        } catch (UnsupportedOperationException e) {
            return false;
        }
    }
}
