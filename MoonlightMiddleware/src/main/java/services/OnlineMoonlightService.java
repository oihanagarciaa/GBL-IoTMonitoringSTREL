package services;

import eu.quanticol.moonlight.formula.Formula;
import eu.quanticol.moonlight.space.SpatialModel;
import moonlight.OnlineMoonlightSetup;
import eu.quanticol.moonlight.domain.AbstractInterval;
import eu.quanticol.moonlight.io.MoonLightRecord;
import eu.quanticol.moonlight.monitoring.online.OnlineSpaceTimeMonitor;
import eu.quanticol.moonlight.signal.online.SpaceTimeSignal;
import eu.quanticol.moonlight.signal.online.Update;

import java.util.List;

public class OnlineMoonlightService implements Service<Update<Double, List<MoonLightRecord>>,
        SpaceTimeSignal<Double, AbstractInterval<Boolean>>>{
    private OnlineMoonlightSetup monitorController;
    private Update<Double, List<MoonLightRecord>> newUpdate;
    private SpaceTimeSignal<Double, AbstractInterval<Boolean>> results;

    public OnlineMoonlightService(Formula formula, SpatialModel<?> model) {
        monitorController = new OnlineMoonlightSetup();
    }

    private OnlineSpaceTimeMonitor<Double, MoonLightRecord, Boolean> onlineMonitor;

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
        Formula onlineNoiseNearbySchool = monitorController.formula();
        onlineMonitor = monitorController.onlineMonitorInit(onlineNoiseNearbySchool);
    }

    @Override
    public void stop() {
        onlineMonitor = null;
    }

    @Override
    public void askService(Update<Double, List<MoonLightRecord>> update) {
        newUpdate = update;
    }

    @Override
    public SpaceTimeSignal<Double, AbstractInterval<Boolean>> getResponseFromService() {
        return results;
    }

}
