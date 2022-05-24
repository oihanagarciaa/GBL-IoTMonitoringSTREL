package main;

import serviceBuilders.MoonlightServiceBuilder;
import serviceBuilders.ResultsThingsboardServiceBuilder;
import serviceBuilders.SensorsServiceBuilder;
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
import subscriber.ConnType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Main {
    private SensorsServiceBuilder sensorsServiceBuilder;
    private MoonlightServiceBuilder moonlightServiceBuilder;
    private ResultsThingsboardServiceBuilder thingsboardServiceBuilder;

    public Main(){
        //TODO: the client must pass all the information
        setSensorsServiceBuilderServiceBuilders();
        setMoonlightServiceBuilder();
        setThingsboardServiceBuilder();
        runServices();
        notifyServices();
    }

    private void runServices() {
        sensorsServiceBuilder.run();
        moonlightServiceBuilder.run();
        thingsboardServiceBuilder.run();
    }

    private void notifyServices(){
        DataBus dataBus = DataBus.getInstance();
        dataBus.notify(sensorsServiceBuilder.getService());
        dataBus.notify(moonlightServiceBuilder.getService());
        dataBus.notify(thingsboardServiceBuilder.getService());
    }

    private void setSensorsServiceBuilderServiceBuilders(){
        String broker = "tcp://stefanschupp.de:1883";
        String topic = "institute/thingy/#";
        String username = "oihana";
        String password = "22oihana22";
        sensorsServiceBuilder = new SensorsServiceBuilder
                (ConnType.MQTT, broker, topic, username, password, OfficeSensorMessage.class);
    }

    private void setThingsboardServiceBuilder() {
        Map<String, String> deviceAccessTokens = new HashMap<>();
        deviceAccessTokens.put("1", "v8iK9AKNXuRZNhIrzROu");
        deviceAccessTokens.put("2", "q1qbXmY3KR51xhD24iHP");
        deviceAccessTokens.put("3", "eqV89m6l1buoWhjY9hOV");
        deviceAccessTokens.put("4", "MwzTm5OkYmsNfcIQqLmB");
        deviceAccessTokens.put("5", "0OOXKKKFQF6BkHAQ1DqR");
        deviceAccessTokens.put("6", "CTDHfcr3KncVvsVZyYb3");
        deviceAccessTokens.put("Monitor", "EN2RFpa41RFQgVZrDNdy");
        thingsboardServiceBuilder =
                new ResultsThingsboardServiceBuilder(deviceAccessTokens);
    }

    private void setMoonlightServiceBuilder(){
        int size = 4;
        int bufferSize = 12;
        double distance = 7.0;
        SpatialModel<Double> spatialModel = buildSpatialModel(size);
        Formula formula = formula();
        Map<String, Function<Tuple, Box<Boolean>>> atoms = getOnlineAtoms();
        Map<String, Function<SpatialModel<Double>, DistanceStructure<Double, ?>>>
                distFunctions = setDistanceFunctions(distance, spatialModel);
        moonlightServiceBuilder = new MoonlightServiceBuilder(spatialModel,
                formula, atoms, distFunctions, bufferSize);
    }

    private static SpatialModel<Double> buildSpatialModel(int size){
        HashMap<Pair<Integer, Integer>, Double> cityMap = new HashMap<>();
        cityMap.put(new Pair<>(0, 2), 4.0);
        cityMap.put(new Pair<>(2, 0), 4.0);
        cityMap.put(new Pair<>(2, 1), 8.0);
        return Utils.createSpatialModel(size, cityMap);
    }

    private static Formula formula() {
        Formula controlPeople = new AtomicFormula("highTemperature");
        return controlPeople;
    }

    //TODO: how do I change here from bool to double??
    private static Map<String, Function<Tuple, Box<Boolean>>> getOnlineAtoms() {
        double maxTemperature = 30;
        Map<String, Function<Tuple, Box<Boolean>>> atoms = new HashMap<>();
        atoms.put("highTemperature", a -> booleanInterval((Double) a.getIthValue(0)< maxTemperature));
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
                m -> new DefaultDistanceStructure<>(x -> x,
                        new DoubleDomain(), distance, Double.MAX_VALUE, city));
        return distanceFunctions;
    }

    public static void main(String[] args) {
        new Main();
    }
}
