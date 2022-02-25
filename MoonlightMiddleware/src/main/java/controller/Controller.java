package controller;

import eu.quanticol.moonlight.domain.AbstractInterval;
import eu.quanticol.moonlight.formula.Formula;
import eu.quanticol.moonlight.io.MoonLightRecord;
import eu.quanticol.moonlight.space.DistanceStructure;
import eu.quanticol.moonlight.space.SpatialModel;
import services.MonitorType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 *  The interface between the user/client and the middleware
 *  It provides the setup methods for clarifying the data and services
 *  on which the Middleware should work
 */
public interface Controller {

    /**
     * Setup methods
     */
    void setDataSource(String sourceBrokerId);
    void setConnectionType(ConnType connectionType);
    void setMonitorType(MonitorType monitorType);
    void setFormula(Formula formula);
    void setSpatialModel(SpatialModel<Double> model);
    void setAtomicFormulas(Map<String, Function<MoonLightRecord, AbstractInterval<Boolean>>> atoms);
    void setDistanceFunctions(HashMap<String, Function<SpatialModel<Double>, DistanceStructure<Double, ?>>> distanceFunctions);

    /**
     * Running method
     * @return true if it is running/has run, false otherwise.
     */
    boolean run();

    /**
     * @return the result of the services run from the middleware
     */
    List<String> getResults();
}
