package services;

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
import messages.Message;
import messages.OfficeSensorMessage;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OnlineMoonlightMultipleMessagesTest {

    @Test
    void multipleMessagesTest(){
        int cont = 0;
        OnlineMoonlightService onlineMoonlightService = getOnlineMoonlightServiceWithRealValues();
        onlineMoonlightService.init();
        for(int j = 0; j < 150; j ++){
            for(int i = 0; i < 3; i++) {
                cont++;
                for (int k = 0; k < 3; k++) {
                    Message message = getMessage(k, cont, 11);
                    onlineMoonlightService.receive(message);
                }
            }
            for(int i = 0; i < 3; i++){
                cont++;
                for(int k = 0; k < 3; k++){
                    Message message = getMessage(k, cont, 50);
                    onlineMoonlightService.receive(message);
                }
            }
        }
        assertEquals(true, onlineMoonlightService.isRunning());
    }

    private OnlineMoonlightService getOnlineMoonlightServiceWithRealValues(){
        Formula formula = formula();
        SpatialModel spatialModel = buildSpatialModel(3);
        Map atoms = getOnlineAtoms();
        Map distances = setDistanceFunctions(spatialModel);
        return new OnlineMoonlightService(formula, spatialModel, atoms, distances);
    }


    private Message getMessage(int id, double time, int temp){
        String jsonMessage = "{ 'id': "+id+"," +
                "   'time': "+time+"," +
                "   'temp':" +temp+"," +
                "   'hum': 20," +
                "   'co2': 30," +
                "   'tvoc': 0" +
                "   }";
        Message message = new Gson().fromJson(jsonMessage, (Type) OfficeSensorMessage.class);
        return message;
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

}
