package tmpmoonlight.model;

import eu.quanticol.moonlight.domain.AbstractInterval;
import eu.quanticol.moonlight.domain.DoubleDistance;
import eu.quanticol.moonlight.io.MoonLightRecord;
import eu.quanticol.moonlight.space.DistanceStructure;
import eu.quanticol.moonlight.space.SpatialModel;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class OnlineFormulas {
    private static HashMap<String, Function<SpatialModel<Double>, DistanceStructure<Double, ?>>> distanceFunctions;

    final double SECURITY_DISTANCE = 7;
    final int MAX_PERSONS = 24;
    final int MAX_DECIBELS = 70;

    public OnlineFormulas(SpatialModel<Double> city){
        setDistanceFunctions(city);
    }

    private void setDistanceFunctions(SpatialModel<Double> city) {
        distanceFunctions = new HashMap<>();
        distanceFunctions.put("distance",
                m -> new DistanceStructure<Double, Double>(x -> x,
                        new DoubleDistance(), SECURITY_DISTANCE, Double.MAX_VALUE, city));
    }

    public HashMap<String, Function<SpatialModel<Double>, DistanceStructure<Double, ?>>> getdistanceFUnctions(){
        return distanceFunctions;
    }

    public  Map<String, Function<MoonLightRecord, AbstractInterval<Boolean>>> getOnlineAtoms() {
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
}
