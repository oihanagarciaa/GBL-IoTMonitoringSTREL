package mycontrollers;

import eu.quanticol.moonlight.domain.AbstractInterval;
import eu.quanticol.moonlight.domain.BooleanDomain;
import eu.quanticol.moonlight.domain.DoubleDistance;
import eu.quanticol.moonlight.domain.SignalDomain;
import eu.quanticol.moonlight.formula.*;
import eu.quanticol.moonlight.io.MoonLightRecord;
import eu.quanticol.moonlight.monitoring.SpatialTemporalMonitoring;
import eu.quanticol.moonlight.monitoring.online.OnlineSpaceTimeMonitor;
import eu.quanticol.moonlight.monitoring.spatialtemporal.SpatialTemporalMonitor;
import eu.quanticol.moonlight.signal.Signal;
import eu.quanticol.moonlight.signal.SpatialTemporalSignal;
import eu.quanticol.moonlight.signal.online.SpaceTimeSignal;
import eu.quanticol.moonlight.signal.online.Update;
import eu.quanticol.moonlight.space.DistanceStructure;
import eu.quanticol.moonlight.space.LocationService;
import eu.quanticol.moonlight.space.SpatialModel;
import eu.quanticol.moonlight.util.Utils;
import moonlight.model.SpatialModelSetup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class MonitorController {
    final int SIZE = 6;
    final double SECURITY_DISTANCE = 7;
    final int MAX_PERSONS = 24;
    final int MAX_DECIBELS = 70;

    SpatialModel<Double> city;

    SpatialTemporalMonitor<Double, MoonLightRecord, Boolean> offlineMonitor;

    private static Map<String, Function<Parameters, Function<MoonLightRecord, Boolean>>> atomicFormulas;

    private static HashMap<String, Function<SpatialModel<Double>, DistanceStructure<Double, ?>>> distanceFunctions;

    OnlineSpaceTimeMonitor<Double, MoonLightRecord, Boolean> onlineMonitor;
    private static LocationService<Double, Double> locSvc;

    List<Update<Double, List<MoonLightRecord>>> updates;

    public MonitorController(){
        city = SpatialModelSetup.buildSpatialModel(SIZE);
        locSvc = Utils.createLocServiceStatic(0, 1, 10, city);

        setAtomicFormulas();
        setDistanceFunctions();

        updates = new ArrayList<>();
        Formula onlineNoiseNearbySchool = formula();
        onlineMonitorInit(onlineNoiseNearbySchool);

        Formula offlineFormula = offlineFormula();
        offlineMonitorInit(offlineFormula);
    }

    //////////CALLING MONITORS
    public void monitorCitySignal(double time, SpatialTemporalSignal citySignal){
        System.out.println("OFFLINE MONITOR");
        LocationService<Double, Double> locService = Utils.createLocServiceStatic(0, 1, time,city);
        SpatialTemporalSignal<Boolean> out = offlineMonitor.monitor(locService, citySignal);
        printResult("Noise nearby a school", time+1, out.getSignals());
    }

    private void printResult(String name, double time, List<Signal<Boolean>> signals){
        System.out.print(name+"\t  ");
        for(double j = 0; j < time; j++) {
            System.out.print("\n TIME "+j+"\t");
            for (int i = 0; i < SIZE; i++) {
                System.out.print(signals.get(i).getValueAt(j).toString().substring(0, 1).toUpperCase() + ",  ");
            }
        }
        System.out.println("");
    }

    public void monitorOnlineCitySignal(Update<Double, List<MoonLightRecord>> u){
        SpaceTimeSignal <Double, AbstractInterval<Boolean>> sts = onlineMonitor.monitor(u);

        /////////////////////////
        updates.add(u);
        System.out.println("ONLINE MONITOR: ");
        for(double i = 0.0; i<updates.get(updates.size()-1).getEnd(); i++){
            System.out.println("TIME: "+i+" "+sts.getValueAt(i));
        }
    }

    //////////OFFLINE MONITOR DECLARATION
    public void offlineMonitorInit(Formula f){
        HashMap<String, Function<Parameters, Function<MoonLightRecord, Boolean>>> atoms = setOfflineAtoms();

        SignalDomain<Boolean> module = new BooleanDomain();

        SpatialTemporalMonitoring<Double, MoonLightRecord, Boolean> monitorFactory = new SpatialTemporalMonitoring<Double, MoonLightRecord, Boolean>(atoms, distanceFunctions, module, true);
        offlineMonitor = monitorFactory.monitor(f, null);
    }

    public HashMap<String, Function<Parameters, Function<MoonLightRecord, Boolean>>> setOfflineAtoms(){
        HashMap<String, Function<Parameters, Function<MoonLightRecord, Boolean>>> atomicPropositions = new HashMap<>();
        atomicPropositions.put("noiseLevel", par -> a -> a.get(1, Integer.class)< MAX_DECIBELS);
        atomicPropositions.put("isSchool", par -> a -> "School".equals(a.get(0, String.class)));
        atomicPropositions.put("manyPeople", par -> a -> a.get(2, Integer.class) < MAX_PERSONS);
        return atomicPropositions;
    }

    private Formula offlineFormula() {
        Formula controlPeople = new AtomicFormula("manyPeople");
        Formula isSchool = new AtomicFormula("isSchool");
        Formula noiseLevel = new AtomicFormula("noiseLevel");
        Formula noiseNearby = new EscapeFormula("distance", noiseLevel);

        return new OrFormula(new NegationFormula(isSchool), noiseNearby);
    }

    //////////ONLINE MONITOR DECLARATION
    public OnlineSpaceTimeMonitor<Double, MoonLightRecord, Boolean> onlineMonitorInit(Formula f){
        Map<String, Function<MoonLightRecord, AbstractInterval<Boolean>>> atoms = setOnlineAtoms();

        onlineMonitor = new OnlineSpaceTimeMonitor<>(f, SIZE, new BooleanDomain(),
                locSvc, atoms, distanceFunctions);

        return onlineMonitor;
    }

    private  Map<String, Function<MoonLightRecord, AbstractInterval<Boolean>>> setOnlineAtoms() {
        Map<String, Function<MoonLightRecord, AbstractInterval<Boolean>>> atoms = new HashMap<>();
        atoms.put("noiseLevel", a -> booleanInterval(a.get(1, Integer.class)< MAX_DECIBELS));
        atoms.put("isSchool", a -> booleanInterval("School".equals(a.get(0, String.class))));
        atoms.put("manyPeople", a -> booleanInterval(a.get(2, Integer.class) < MAX_PERSONS));
        return atoms;
    }

    private static AbstractInterval<Boolean> booleanInterval(boolean cond) {
        return cond ? new AbstractInterval<>(true, true) :
                new AbstractInterval<>(false, false);
    }

    //////////FORMULAS
    private void setDistanceFunctions() {
        distanceFunctions = new HashMap<>();
        distanceFunctions.put("distance",
                m -> new DistanceStructure<Double, Double>(x -> x,
                        new DoubleDistance(), SECURITY_DISTANCE, Double.MAX_VALUE, city));
    }

    private void setAtomicFormulas() {
        atomicFormulas = new HashMap<>();
        atomicFormulas.put("noiseLevel", par -> a -> a.get(1, Integer.class)< MAX_DECIBELS);
        atomicFormulas.put("isSchool", par -> a -> "School".equals(a.get(0, String.class)));
        atomicFormulas.put("manyPeople", par -> a -> a.get(2, Integer.class) < MAX_PERSONS);
    }

    public static Formula formula() {
        Formula noiseLevel = new AtomicFormula("noiseLevel");
        /*Formula isSchool = new AtomicFormula("isSchool");
        Formula noiseNearby = new EscapeFormula("distance", noiseLevel);
        return new OrFormula(new NegationFormula(isSchool), noiseNearby);*/
        return noiseLevel;
        //return new EscapeFormula("distance", noiseLevel);
    }

}
