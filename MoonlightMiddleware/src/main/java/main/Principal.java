package main;

import controller.ConnType;
import controller.MainController;
import eu.quanticol.moonlight.domain.AbstractInterval;
import eu.quanticol.moonlight.domain.DoubleDistance;
import eu.quanticol.moonlight.formula.*;
import eu.quanticol.moonlight.io.MoonLightRecord;
import eu.quanticol.moonlight.space.DistanceStructure;
import eu.quanticol.moonlight.space.SpatialModel;
import eu.quanticol.moonlight.util.Pair;
import eu.quanticol.moonlight.util.Utils;
import services.MonitorType;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.function.Function;

public class Principal {

    static final double SECURITY_DISTANCE = 7;
    static final int MAX_PERSONS = 24;
    static final int MAX_DECIBELS = 70;

    private static MainController c;

    //TODO: the client has to set up the controller
    public static void main(String[] args) {

        c = new MainController();
        initMainController();
        c.run();
        Thread thread = new Thread(){
            final SecureRandom rand = new SecureRandom();
            @Override
            public void run() {
                try{

                    int i, n, p;
                    do {
                            i = rand.nextInt(6);
                            n = 50+rand.nextInt(30);
                            p = 20+rand.nextInt(10);
                            String s = "{\n" +
                                    "            \"id\": "+i+"\n" +
                                    "            \"place\":"+ i+"\n" +
                                    "            \"noise\":"+ n+"\n" +
                                    "            \"people\":"+ p+"\n" +
                                    "         }";
                            c.updateData(s);
                            System.out.println("RESULTS:\n"+c.getResults()+"\n- - - - - - - - - - - - - - - - - - - -");
                            Thread.sleep(500);
                    }while (true);
                }catch(InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        };
        Scanner teclado = new Scanner(System.in);
        thread.start();
        teclado.nextLine();
        thread.interrupt();
        try {
            thread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

    }

    private static void initMainController() {
        c.setMonitorType(MonitorType.ONLINE_MOONLIGHT);
        c.setConnectionType(ConnType.MQTT);
        c.setDataSource("tcp...");
        SpatialModel<Double> spatialModel = buildSpatialModel(6);
        c.setSpatialModel(spatialModel);
        c.setFormula(formula());
        c.setAtomicFormulas(getOnlineAtoms());
        c.setDistanceFunctions(setDistanceFunctions(spatialModel));
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

    //TODO: Escape formula gets stuck
    public static Formula formula() {
        Formula controlPeople = new AtomicFormula("manyPeople");
        /*Formula isSchool = new AtomicFormula("isSchool");
        Formula noiseLevel = new AtomicFormula("noiseLevel");
        Formula noiseNearby = new EscapeFormula("distance", noiseLevel);*/

        //return new OrFormula(new NegationFormula(isSchool), noiseNearby);
        return controlPeople;
    }

    public static Map<String, Function<MoonLightRecord, AbstractInterval<Boolean>>> getOnlineAtoms() {
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

    private static HashMap<String, Function<SpatialModel<Double>, DistanceStructure<Double, ?>>> setDistanceFunctions(SpatialModel<Double> city) {
        HashMap<String, Function<SpatialModel<Double>, DistanceStructure<Double, ?>>> distanceFunctions = new HashMap<>();
        distanceFunctions.put("distance",
                m -> new DistanceStructure<Double, Double>(x -> x,
                        new DoubleDistance(), SECURITY_DISTANCE, Double.MAX_VALUE, city));
        return distanceFunctions;
    }
}
