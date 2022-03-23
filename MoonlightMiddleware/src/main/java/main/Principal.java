package main;

import builders.MoonlightServiceBuilder;
import eu.quanticol.moonlight.core.base.Box;
import eu.quanticol.moonlight.core.base.Pair;
import eu.quanticol.moonlight.core.base.Tuple;
import eu.quanticol.moonlight.core.formula.Formula;
import eu.quanticol.moonlight.core.space.DefaultDistanceStructure;
import eu.quanticol.moonlight.domain.DoubleDomain;
import eu.quanticol.moonlight.formula.*;
import eu.quanticol.moonlight.core.space.DistanceStructure;
import eu.quanticol.moonlight.core.space.SpatialModel;
import eu.quanticol.moonlight.util.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Principal {

    static final double SECURITY_DISTANCE = 7;
    static final int MAX_PERSONS = 24;
    static final int MAX_DECIBELS = 70;

    private static MoonlightServiceBuilder c;

    //TODO: the client has to set up the controller
    public static void main(String[] args) {

        SpatialModel<Double> spatialModel = buildSpatialModel(6);
        c = new MoonlightServiceBuilder(spatialModel, formula(), getOnlineAtoms(), setDistanceFunctions(spatialModel));
        c.run();
    }

    public static SpatialModel<Double> buildSpatialModel(int size){
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

    public static Formula formula() {
        Formula controlPeople = new AtomicFormula("manyPeople");
        /*Formula isSchool = new AtomicFormula("isSchool");
        Formula noiseLevel = new AtomicFormula("noiseLevel");
        Formula noiseNearby = new EscapeFormula("distance", noiseLevel);*/

        //return new OrFormula(new NegationFormula(isSchool), noiseNearby);
        return controlPeople;
    }

    public static Map<String, Function<Tuple, Box<Boolean>>> getOnlineAtoms() {
        Map<String, Function<Tuple, Box<Boolean>>> atoms = new HashMap<>();
        atoms.put("noiseLevel", a -> booleanInterval((Integer) a.getIthValue(1)< MAX_DECIBELS));
        atoms.put("isSchool", a -> booleanInterval("School".equals(a.getIthValue(0))));
        atoms.put("manyPeople", a -> booleanInterval((Integer) a.getIthValue(2) < MAX_PERSONS));
        return atoms;
    }

    private static Box<Boolean> booleanInterval(boolean cond) {
        return cond ? new Box<>(true, true) :
                new Box<>(false, false);
    }

    private static HashMap<String, Function<SpatialModel<Double>, DistanceStructure<Double, ?>>> setDistanceFunctions(SpatialModel<Double> city) {
        HashMap<String, Function<SpatialModel<Double>, DistanceStructure<Double, ?>>> distanceFunctions = new HashMap<>();
        distanceFunctions.put("distance",
                m -> new DefaultDistanceStructure<Double, Double>(x -> x,
                        new DoubleDomain(), SECURITY_DISTANCE, Double.MAX_VALUE, city));
        return distanceFunctions;
    }
}
