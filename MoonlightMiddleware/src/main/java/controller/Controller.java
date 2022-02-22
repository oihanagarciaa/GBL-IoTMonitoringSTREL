package controller;

import eu.quanticol.moonlight.formula.Formula;
import eu.quanticol.moonlight.space.SpatialModel;
import messages.Message;
import services.Service;
import services.MonitorType;

import java.util.List;

/**
 *  The interface between the user/client and the middleware
 *  It provides the setup methods for clarifying the data and services
 *  on which the Middleware should work
 */
public interface Controller {

    /**
     * Setup methods
     */
    void setDataSource(String sourceId);
    void setConnectionType(ConnType connectionType);
    void setMonitorType(MonitorType monitorType);
    void setFormula(Formula formula);
    void setSpatialModel(SpatialModel<?> model);

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
