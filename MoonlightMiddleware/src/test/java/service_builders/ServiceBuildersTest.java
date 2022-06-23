package service_builders;

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
import messages.OfficeSensorMessage;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import connection.ConnType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class ServiceBuildersTest {

    @Test
    void basicMoonlightBuilderInit() {
        MoonlightServiceBuilder serviceBuilder = moonlightBuilderInit();
        assertTrue(serviceBuilder.run());
    }

    @Test
    void moonlightBuilderInitNull() {
        MoonlightServiceBuilder serviceBuilder = new MoonlightServiceBuilder
                (null, null, null, null, 0);
        assertThrows(NullPointerException.class,
                () -> serviceBuilder.run());
    }

    public MoonlightServiceBuilder moonlightBuilderInit(){
        SpatialModel spatialModel = buildSpatialModel(6);
        Formula formula = formula();
        Map<String, Function<Tuple, Box<Boolean>>> atoms = getOnlineAtoms();
        HashMap distanceFunctions = setDistanceFunctions(spatialModel);
        return new MoonlightServiceBuilder(spatialModel, formula, atoms, distanceFunctions, 6);
    }

    @Test
    @Disabled("In my computer works but not in github actions")
    void basicSensorBuilderInit() {
        SensorsServiceBuilder serviceBuilder = new SensorsServiceBuilder
                (ConnType.MQTT, "tcp://localhost:1883", "topic", "", "", OfficeSensorMessage.class);
        assertTrue(serviceBuilder.run());
    }

    @Test
    void sensorsBuilderInitNull1() {
        SensorsServiceBuilder serviceBuilder = new SensorsServiceBuilder
                (ConnType.MQTT, "", "", "", "", OfficeSensorMessage.class);
        assertThrows(IllegalArgumentException.class,
                () -> serviceBuilder.run());
    }

    @Test
    void sensorsBuilderInitNull2() {
        SensorsServiceBuilder serviceBuilder = new SensorsServiceBuilder
                (null, "", "", "", "", OfficeSensorMessage.class);
        assertFalse(serviceBuilder.run());
    }

    @Test
    void thingsboardBuilderInit(){
        Map<String, String> tokens = new HashMap<>();
        ResultsThingsboardServiceBuilder resultsThingsboardServiceBuilder =
                new ResultsThingsboardServiceBuilder(ConnType.MQTT, "", "", tokens);
        assertTrue(resultsThingsboardServiceBuilder.run());
    }

    @Test
    void runnerBuilderInit(){
        Map<String, ServiceBuilder> map = new HashMap();
        RunnerServiceBuilder runnerServiceBuilder = new RunnerServiceBuilder( ConnType.MQTT,
                "tcp://localhost:1883", "topic", "", "", map);
        assertTrue(runnerServiceBuilder.run());
    }

    private static SpatialModel<Double> buildSpatialModel(int size){
        Map<Pair<Integer, Integer>, Double> cityMap = new HashMap<>();
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
        return controlPeople;
    }

    private static Map<String, Function<Tuple, Box<Boolean>>> getOnlineAtoms() {
        Map<String, Function<Tuple, Box<Boolean>>> atoms = new HashMap<>();
        atoms.put("manyPeople", a -> booleanInterval((Integer) a.getIthValue(2) < 10));
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
                        new DoubleDomain(), 60.0, Double.MAX_VALUE, city));
        return distanceFunctions;
    }
}