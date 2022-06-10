package main;

import eu.quanticol.moonlight.core.base.Pair;
import eu.quanticol.moonlight.core.space.SpatialModel;
import eu.quanticol.moonlight.util.Utils;
import serviceBuilders.*;
import java.util.HashMap;
import java.util.Map;

public class Main {
    private Map<String, ServiceBuilder> services;
    private RunnerServiceBuilder runnerServiceBuilder;

    public Main(){
        services = new HashMap<>();
        setRunnerServiceBuilder();
    }


    /**
     * TODO: load in the runner
     * Settings file:
     * Thingsboard interaction info:
     * - topic
     * - broker
     */

    /**
     * // TODO: dynamic stuff, we need to interpret it
     * <pre>
     * {
     *      services: [
     *          {
     *            serviceType: "sensors",
     *            connection: {
     *                type: mqtt,
     *                settings: {
     *                    broker: "..",
     *                    topic: "",
     *                }
     *            devices: [
     *                  {
     *                    "identifier": "Thingy1",
     *                    "accessKey" : "XXX"
     *                  }
     *                ]
     *            }
     *          }
     *      ]
     *      ,  
     *     
     * }
     * </pre>
     * - Broker for the sensors
     * - Sensor's topic
     * - Sensors username & password
     * - A list of pairs: (readable Name, access token)
     * 
     * - Formula (later atoms)
     * - later -> Set the distance and spatial model
     */

    private void setRunnerServiceBuilder() {
        runnerServiceBuilder = new RunnerServiceBuilder(services);
        runnerServiceBuilder.run();
    }

    public static SpatialModel<Double> buildSpatialModel(int size){
        HashMap<Pair<Integer, Integer>, Double> cityMap = new HashMap<>();
        cityMap.put(new Pair<>(0, 2), 4.0);
        cityMap.put(new Pair<>(2, 0), 4.0);
        cityMap.put(new Pair<>(2, 1), 8.0);
        return Utils.createSpatialModel(size, cityMap);
    }

   /* private static Formula formula() {
        Formula highTemperature = new AtomicFormula("highTemperature");
        Formula co2Formula = new EventuallyFormula( new AtomicFormula("highCO2"), new Interval(0, 1500)); //15 sec
        Formula finalFormula = new AndFormula(highTemperature, co2Formula);
        return finalFormula;
    }

    //TODO: how do I change here from bool to double??
    private static Map<String, Function<Tuple, Box<Boolean>>> getOnlineAtoms() {
        double maxTemperature = 30;
        int maxCO2 = 600;
        Map<String, Function<Tuple, Box<Boolean>>> atoms = new HashMap<>();
        atoms.put("highTemperature", a -> booleanInterval((Double) a.getIthValue(0)< maxTemperature));
        atoms.put("highCO2", a -> booleanInterval((Integer) a.getIthValue(3) < maxCO2));
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
    }*/

    public static void main(String[] args) {
        new Main();
    }
}
