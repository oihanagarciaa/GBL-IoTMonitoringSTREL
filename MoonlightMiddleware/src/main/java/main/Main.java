package main;


import builders.MoonlightServiceBuilder;
import builders.SensorsServiceBuilder;
import eu.quanticol.moonlight.core.base.Box;
import eu.quanticol.moonlight.core.base.Pair;
import eu.quanticol.moonlight.core.base.Tuple;
import eu.quanticol.moonlight.core.formula.Formula;
import eu.quanticol.moonlight.core.space.DefaultDistanceStructure;
import eu.quanticol.moonlight.core.space.DistanceStructure;
import eu.quanticol.moonlight.core.space.SpatialModel;
import eu.quanticol.moonlight.domain.DoubleDomain;
import eu.quanticol.moonlight.formula.AtomicFormula;
import eu.quanticol.moonlight.util.Utils;
import subscriber.ConnType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Main {
    private SensorsServiceBuilder sensorsServiceBuilder;
    private MoonlightServiceBuilder moonlightServiceBuilder;

    public Main(){
        //TODO: the client must pass all the information
        setSensorsServiceBuilderServiceBuilders();
        setMoonlightServiceBuilder();
    }

    public static void main(String[] args) {
        Main main = new Main();
    }

    private void setSensorsServiceBuilderServiceBuilders(){
        String broker = "tcp://localhost:1883";
        String topic = "institute/thingy/#";
        sensorsServiceBuilder = new SensorsServiceBuilder(ConnType.MQTT, broker, topic);
    }

    private void setMoonlightServiceBuilder(){
        int size = 6;
        double distance = 7.0;
        SpatialModel<Double> spatialModel = buildSpatialModel(size);
        Formula formula = formula();
        Map<String, Function<Tuple, Box<Boolean>>> atoms = getOnlineAtoms();
        Map<String, Function<SpatialModel<Double>, DistanceStructure<Double, ?>>>
                distFunctions = setDistanceFunctions(distance, spatialModel);
        moonlightServiceBuilder = new MoonlightServiceBuilder(spatialModel,
                formula, atoms, distFunctions);
    }

    private void setupDataBus(){
        DataBus instance = DataBus.getInstance();
        instance.notify();
        instance.notify();
    }

    private static SpatialModel<Double> buildSpatialModel(int size){
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
        return Utils.createSpatialModel(size, cityMap);
    }

    private static Formula formula() {
        Formula controlPeople = new AtomicFormula("manyPeople");
        /*Formula isSchool = new AtomicFormula("isSchool");
        Formula noiseLevel = new AtomicFormula("noiseLevel");
        Formula noiseNearby = new EscapeFormula("distance", noiseLevel);*/

        //return new OrFormula(new NegationFormula(isSchool), noiseNearby);
        return controlPeople;
    }

    private static Map<String, Function<Tuple, Box<Boolean>>> getOnlineAtoms() {
        int maxDecibels = 70;
        Map<String, Function<Tuple, Box<Boolean>>> atoms = new HashMap<>();
        atoms.put("noiseLevel", a -> booleanInterval((Integer) a.getIthValue(1)< maxDecibels));
        atoms.put("isSchool", a -> booleanInterval("School".equals(a.getIthValue(0))));
        atoms.put("manyPeople", a -> booleanInterval((Integer) a.getIthValue(2) < maxDecibels));
        return atoms;
    }

    private static Box<Boolean> booleanInterval(boolean cond) {
        return cond ? new Box<>(true, true) :
                new Box<>(false, false);
    }

    private static HashMap<String, Function<SpatialModel<Double>, DistanceStructure<Double, ?>>> setDistanceFunctions
            (double distance, SpatialModel<Double> city) {
        HashMap<String, Function<SpatialModel<Double>, DistanceStructure<Double, ?>>> distanceFunctions = new HashMap<>();
        distanceFunctions.put("distance",
                m -> new DefaultDistanceStructure<Double, Double>(x -> x,
                        new DoubleDomain(), distance, Double.MAX_VALUE, city));
        return distanceFunctions;
    }
}
