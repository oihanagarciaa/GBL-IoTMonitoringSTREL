package dataStorages;

import eu.quanticol.moonlight.domain.AbstractInterval;
import eu.quanticol.moonlight.domain.BooleanDomain;
import eu.quanticol.moonlight.domain.DoubleDistance;
import eu.quanticol.moonlight.formula.*;
import eu.quanticol.moonlight.io.MoonLightRecord;
import eu.quanticol.moonlight.monitoring.online.OnlineSpaceTimeMonitor;
import eu.quanticol.moonlight.signal.DataHandler;
import eu.quanticol.moonlight.signal.EnumerationHandler;
import eu.quanticol.moonlight.signal.RecordHandler;
import eu.quanticol.moonlight.signal.online.Update;
import eu.quanticol.moonlight.space.DistanceStructure;
import eu.quanticol.moonlight.space.LocationService;
import eu.quanticol.moonlight.space.SpatialModel;
import eu.quanticol.moonlight.util.Pair;
import eu.quanticol.moonlight.util.Stopwatch;
import eu.quanticol.moonlight.util.Utils;

import java.util.*;
import java.util.function.Function;

public class DELETEOnlineCitySignalDefinition {
    int size;
    final double SECURITY_DISTANCE = 7;
    final int MAX_PERSONS = 24;
    final int MAX_DECIBELS = 70;
    List<String> places;
    List<Integer> noiseLevel1, noiseLevel2;
    Random rand;
    SpatialModel<Double> city;

    private static Map<String,
            Function<Parameters,
                    Function<MoonLightRecord, Boolean>>>
            atomicFormulas;

    private static HashMap<String, Function<SpatialModel<Double>,
            DistanceStructure<Double, ?>>> distanceFunctions;

    private static LocationService<Double, Double> locSvc;

    public DELETEOnlineCitySignalDefinition(){
        this.size = 6;
        places = Arrays.asList("Hospital", "School", "MetroStop", "School", "MainSquare", "BusStop");
        noiseLevel1 = Arrays.asList(60, 70, 40, 50, 90, 80);
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

        OnlineSpaceTimeMonitor<Double, MoonLightRecord, Boolean> m =
                onlineMonitorInit(f);

        List<Update<Double, List<MoonLightRecord>>> updates =
                new ArrayList<>();

        /*ALDATUTAKOA*/
        RecordHandler factory = new RecordHandler(new EnumerationHandler<>(String.class, places.toArray(new String[0])), DataHandler.INTEGER, DataHandler.INTEGER);
        List<MoonLightRecord> signalSP = new ArrayList<MoonLightRecord>();
        for (int i = 0; i < size; i++) {
            signalSP.add(factory.fromObjectArray(places.get(i), noiseLevel1.get(i), rand.nextInt(45)));
        }

        /*for(int i = 0; i < 10 ; i++){
            Double j = Double.valueOf(i);
            updates.add(new Update<Double, List<MoonLightRecord>>(j, j+1, signalSP));
                System.out.println(j +"------"+ m.monitor(updates.get(i)).getValueAt(j));
        }*/


        updates.add(new Update<Double, List<MoonLightRecord>>(0.0, 1.0, signalSP));
        System.out.println(0 +"------"+ m.monitor(updates.get(0)).getValueAt(0.0));

        List<MoonLightRecord> signalSP2 = new ArrayList<MoonLightRecord>();
        for (int i = 0; i < size; i++) {
            signalSP2.add(factory.fromObjectArray(places.get(i), 10, 3));
        }
        updates.add(new Update<Double, List<MoonLightRecord>>(3.0, 5.0, signalSP2));

        System.out.println(1 +"------"+ m.monitor(updates.get(1)).getValueAt(1.0));
        System.out.println(2 +"------"+ m.monitor(updates.get(1)).getValueAt(2.0));
        System.out.println(3 +"------"+ m.monitor(updates.get(1)).getValueAt(3.0));
        System.out.println(4 +"------"+ m.monitor(updates.get(1)).getValueAt(4.0));
        System.out.println(5 +"------"+ m.monitor(updates.get(1)).getValueAt(5.0));



        //updates.forEach(m::monitor);
        rec.stop();
        System.out.println(rec.getDuration());

        /*ALDATUTAKOA*/
    }

    private OnlineSpaceTimeMonitor<Double, MoonLightRecord, Boolean> onlineMonitorInit(Formula f) {
        Map<String, Function<MoonLightRecord, AbstractInterval<Boolean>>> atoms = setOnlineAtoms();

        return new OnlineSpaceTimeMonitor<>(f, size, new BooleanDomain(),
                locSvc, atoms, distanceFunctions);
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

    private static Formula formula() {
        Formula noiseLevel = new AtomicFormula("noiseLevel");
        Formula isSchool = new AtomicFormula("isSchool");
        Formula noiseNearby = new EscapeFormula("distance", noiseLevel);
        //return new OrFormula(new NegationFormula(isSchool), noiseNearby);
        return noiseLevel;
        //return new EscapeFormula("distance", noiseLevel);
    }

    public static void main(String[] args) {
        DELETEOnlineCitySignalDefinition p = new DELETEOnlineCitySignalDefinition();
    }
}