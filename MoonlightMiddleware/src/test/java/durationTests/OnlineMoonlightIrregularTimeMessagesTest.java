package durationTests;

import com.google.gson.Gson;
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
import main.DataBus;
import messages.Message;
import messages.OfficeSensorMessage;
import messages.ResultsMessage;
import org.junit.jupiter.api.Test;
import services.OnlineMoonlightService;
import services.Service;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OnlineMoonlightIrregularTimeMessagesTest {
    @Test
    void irregularTimeSendMessages(){
        OnlineMoonlightIrregularTimeMessagesTest.ServiceTest serviceTest = new OnlineMoonlightIrregularTimeMessagesTest.ServiceTest();
        DataBus dataBus = DataBus.getInstance();
        dataBus.subscribe(serviceTest);
        OnlineMoonlightService onlineMoonlightService = getOnlineMoonlightServiceWithRealValues();
        onlineMoonlightService.init();
        long startingMillis = System.currentTimeMillis();
        Random rand = new Random();
        double t = 0;
        for(int i = 0; i < 100; i++){
            t += 5;
            for(int j = 0; j < 6; j++){
                double randT = rand.nextInt(8);
                Message message = getMessage(j, t+randT, rand.nextInt(20)+20);
                onlineMoonlightService.receive(message);
            }
        }
        long finishingMilis = System.currentTimeMillis();
        double duration = ((finishingMilis-startingMillis)/1000.0);
        assertNotNull(serviceTest.getResultsMessage());
    }

    private OnlineMoonlightService getOnlineMoonlightServiceWithRealValues(){
        Formula formula = formula();
        SpatialModel spatialModel = buildSpatialModel(6);
        Map atoms = getOnlineAtoms();
        Map distances = setDistanceFunctions(spatialModel);
        return new OnlineMoonlightService(formula, spatialModel, atoms, distances, 12);
    }

    private Message getMessage(int id, double time, int temp){
        String jsonMessage = "{ 'id':"+id+",\n" +
                "   'time':"+time+",\n" +
                "   'temp':"+temp+",\n" +
                "   'hum': "+temp+",\n" +
                "   'co2': "+3+",\n" +
                "   'tvoc':"+3+"\n" +
                "    }";
        Message message = new Gson().fromJson(jsonMessage, (Type) OfficeSensorMessage.class);
        return message;
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
        Formula controlPeople = new AtomicFormula("highTemperature");
        return controlPeople;
    }

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

    private static HashMap<String, Function<SpatialModel<Double>, DistanceStructure<Double, ?>>>
    setDistanceFunctions(SpatialModel<Double> city) {
        HashMap<String, Function<SpatialModel<Double>, DistanceStructure<Double, ?>>> distanceFunctions = new HashMap<>();
        distanceFunctions.put("distance",
                m -> new DefaultDistanceStructure<Double, Double>(x -> x,
                        new DoubleDomain(), 60.0, Double.MAX_VALUE, city));
        return distanceFunctions;
    }


    public class ServiceTest implements Service {
        ResultsMessage resultsMessage;

        public ResultsMessage getResultsMessage() {
            return resultsMessage;
        }

        @Override
        public boolean isRunning() {
            return false;
        }

        @Override
        public void receive(Message message) {
            if (message instanceof ResultsMessage){
                resultsMessage = (ResultsMessage) message;
            }
        }

        @Override
        public void init() {

        }

        @Override
        public void stop() {

        }
    }
}
