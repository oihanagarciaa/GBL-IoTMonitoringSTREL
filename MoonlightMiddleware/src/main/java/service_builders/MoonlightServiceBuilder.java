package service_builders;

import eu.quanticol.moonlight.core.base.Box;
import eu.quanticol.moonlight.core.base.Tuple;
import eu.quanticol.moonlight.core.formula.Formula;
import eu.quanticol.moonlight.core.space.DistanceStructure;
import eu.quanticol.moonlight.core.space.SpatialModel;

import services.MonitorType;
import services.OnlineMoonlightService;
import services.Service;

import java.util.Map;
import java.util.function.Function;

public class MoonlightServiceBuilder implements ServiceBuilder {
    //If I want to access a variable from a test make it protected
    private Service service;
    private final MonitorType monitorType;
    private final SpatialModel<Double> spatialModel;
    private final Formula formula;
    private final Map<String, Function<Tuple, Box<Boolean>>> atoms;
    private final Map<String, Function<SpatialModel<Double>, DistanceStructure<Double, ?>>> distanceFunctions;
    private final int bufferSize;

    public MoonlightServiceBuilder(SpatialModel<Double> spatialModel,
                                   Formula formula,
                                   Map<String, Function<Tuple, Box<Boolean>>> atoms,
                                   Map<String, Function<SpatialModel<Double>, DistanceStructure<Double, ?>>> distFunctions,
                                   int bufferSize)
    {
        this.monitorType = MonitorType.ONLINE_MOONLIGHT;
        this.spatialModel = spatialModel;
        this.formula = formula;
        this.atoms = atoms;
        this.distanceFunctions = distFunctions;
        this.bufferSize = bufferSize;
    }

    /* it takes the if else as a duplication:
     * see polymorphism [Clean code]
     */
    @Override
    public void initializeService() {
        if(monitorType == MonitorType.ONLINE_MOONLIGHT) {
            service = new OnlineMoonlightService(formula, spatialModel, atoms, distanceFunctions, bufferSize);
        } else {
            throw new UnsupportedOperationException("Not supported monitor type");
        }
        service.init();
    }

    /* TODO:
        it takes the catch as a duplication:
        see Template method or strategy
     */
    @Override
    public boolean run() {
        try {
            initializeService();
            return true;
        } catch (UnsupportedOperationException e) {
            return false;
        }
    }

    @Override
    public Service getService() {
        return service;
    }
}
