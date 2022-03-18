package main;

import eu.quanticol.moonlight.core.base.*;
import eu.quanticol.moonlight.core.formula.Formula;
import eu.quanticol.moonlight.core.space.DefaultDistanceStructure;
import eu.quanticol.moonlight.core.space.DistanceStructure;
import eu.quanticol.moonlight.core.space.LocationService;
import eu.quanticol.moonlight.core.space.SpatialModel;
import eu.quanticol.moonlight.domain.BooleanDomain;
import eu.quanticol.moonlight.domain.DoubleDomain;
import eu.quanticol.moonlight.formula.*;
import eu.quanticol.moonlight.formula.classic.NegationFormula;
import eu.quanticol.moonlight.formula.classic.OrFormula;
import eu.quanticol.moonlight.formula.spatial.EscapeFormula;
import eu.quanticol.moonlight.online.monitoring.OnlineSpatialTemporalMonitor;
import eu.quanticol.moonlight.online.signal.Update;
import eu.quanticol.moonlight.util.Stopwatch;
import eu.quanticol.moonlight.util.Utils;

import java.util.*;
import java.util.function.Function;

public class DELETEOnlineCitySignalDefinition {
    int size;
    final double SECURITY_DISTANCE = 7;
    static final int MAX_PERSONS = 24;
    static final int MAX_DECIBELS = 70;
    List<String> places;
    List<Integer> noiseLevel1, noiseLevel2;
    Random rand;
    SpatialModel<Double> city;

    private static Map<String,
            Function<Parameters,
                    Function<Integer, Boolean>>>
            atomicFormulas;

    HashMap<String, Function<SpatialModel<Double>, DistanceStructure<Double, ?>>> distanceFunctions;

    private static LocationService<Double, Double> locSvc;

    public DELETEOnlineCitySignalDefinition(){
        this.size = 6;
        places = Arrays.asList("Hospital", "School", "MetroStop", "School", "MainSquare", "BusStop");
        noiseLevel1 = Arrays.asList(60, 60, 40, 50, 60, 60);
        noiseLevel2 = Arrays.asList(80, 80, 80, 80, 80, 80);
        rand = new Random();

        HashMap<Pair<Integer, Integer>, Double> cityMap = new HashMap<>();
        cityMap.put(new Pair<>(0, 2), 4.0);
        cityMap.put(new Pair<>(2, 0), 4.0);
        cityMap.put(new Pair<>(2, 3), 8.0);
        cityMap.put(new Pair<>(0, 4), 7.0);
        cityMap.put(new Pair<>(0, 5), 25.0);
        cityMap.put(new Pair<>(2, 4), 5.0);
        cityMap.put(new Pair<>(4, 2), 5.0);
        cityMap.put(new Pair<>(4, 5), 9.0);
        cityMap.put(new Pair<>(1, 4), 14.0);
        cityMap.put(new Pair<>(1, 5), 19.0);
        city = Utils.createSpatialModel(size, cityMap);

        locSvc = Utils.createLocServiceStatic(0, 1, 10, city);
        setAtomicFormulas();
        setDistanceFunctions();

        Formula noiseNearbySchool = formula();
        System.out.println("Check online");
        checkOnline(noiseNearbySchool);
    }

     private void checkOnline(Formula f) {
        Stopwatch rec = Stopwatch.start();

        OnlineSpatialTemporalMonitor<Double, Tuple, Boolean> m =
                onlineMonitorInit(f);


        /*ALDATUTAKOA*/
        List<Update> updates = new ArrayList<>();

        TupleType tupleType = TupleType.of(String.class, Integer.class, Integer.class);

        List<Tuple> signalSP = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            signalSP.add(Tuple.of(tupleType, places.get(i), noiseLevel1.get(i), 3));
        }

        updates.add(new Update<Double, List<Tuple>>(0.0, 1.0, signalSP));
        System.out.println(0 +"------"+ m.monitor(updates.get(0)).getValueAt(0.0));

        List<Tuple> signalSP2 = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            signalSP2.add(Tuple.of(tupleType, places.get(i), noiseLevel2.get(i), 3));
        }
        updates.add(new Update<Double, List<Tuple>>(3.0, 5.0, signalSP2));

        System.out.println(1 +"------"+ m.monitor(updates.get(1)).getValueAt(4.0));


        //updates.forEach(m::monitor);
        rec.stop();
        System.out.println(rec.getDuration());

        /*ALDATUTAKOA*/
    }

    private OnlineSpatialTemporalMonitor<Double, Tuple, Boolean> onlineMonitorInit(Formula f) {
        Map<String, Function<Tuple, Box<Boolean>>> atoms = setOnlineAtoms();

        return new OnlineSpatialTemporalMonitor<Double, Tuple, Boolean>(f, size, new BooleanDomain(),
                locSvc, atoms, distanceFunctions);
    }

    public static Map<String, Function<Tuple, Box<Boolean>>> setOnlineAtoms() {
        Map<String, Function<Tuple, Box<Boolean>>> atoms = new HashMap<>();
        atoms.put("noiseLevel", a -> booleanInterval((Integer) a.getIthValue(1 )< MAX_DECIBELS));
        atoms.put("isSchool", a -> booleanInterval("School".equals(a.getIthValue(0))));
        atoms.put("manyPeople", a -> booleanInterval((Integer) a.getIthValue(2) < MAX_PERSONS));
        return atoms;
    }

    private static Box<Boolean> booleanInterval(boolean cond) {
        return cond ? new Box<>(true, true) :
                new Box<>(false, false);
    }

    private void setDistanceFunctions() {
        distanceFunctions = new HashMap<>();
        distanceFunctions.put("distance",
                m -> new DefaultDistanceStructure<Double, Double>(x -> x,
                        new DoubleDomain(), SECURITY_DISTANCE, Double.MAX_VALUE, city));
    }

    private void setAtomicFormulas() {
        atomicFormulas = new HashMap<>();
        atomicFormulas.put("noiseLevel", par -> a -> a< MAX_DECIBELS);
        atomicFormulas.put("isSchool", par -> a -> "School".equals(a));
        atomicFormulas.put("manyPeople", par -> a -> a < MAX_PERSONS);
    }

    private static Formula formula() {
        Formula noiseLevel = new AtomicFormula("noiseLevel");
        Formula isSchool = new AtomicFormula("isSchool");
        Formula noiseNearby = new EscapeFormula("distance", noiseLevel);
        return new OrFormula(new NegationFormula(isSchool), noiseNearby);
        //return noiseLevel;
        //return new EscapeFormula("distance", noiseLevel);
    }

    public static void main(String[] args) {
        DELETEOnlineCitySignalDefinition p = new DELETEOnlineCitySignalDefinition();
    }
}