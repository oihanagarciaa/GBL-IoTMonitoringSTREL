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
import main.DataBus;
import messages.Message;
import messages.OfficeSensorMessage;
import messages.ResultsMessage;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

public class OnlineMoonlightServiceTest {

    @Test
    void simpleOnlineMoonlightInit(){
        OnlineMoonlightService onlineMoonlightService = getOnlineMoonlightService();
        onlineMoonlightService.init();
        assertTrue(onlineMoonlightService.isRunning());
    }

    @Test
    void basicSendMessages(){
        ServiceTest serviceTest = new ServiceTest();
        DataBus dataBus = DataBus.getInstance();
        dataBus.notify(serviceTest);
        OnlineMoonlightService onlineMoonlightService = getOnlineMoonlightServiceWithRealValues();
        onlineMoonlightService.init();
        for(int i = 0; i < 6; i++){
            Message message = getMessage(i, i, 11);
            onlineMoonlightService.receive(message);
        }
        for(int i = 0; i < 6; i++){
            Message message = getMessage(i, i, 11);
            onlineMoonlightService.receive(message);
        }
        Message message = serviceTest.getResultsMessage();
        assertTrue(message instanceof ResultsMessage);
    }

    @Test
    void sendingMessages(){
        ServiceTest serviceTest = new ServiceTest();
        DataBus dataBus = DataBus.getInstance();
        dataBus.notify(serviceTest);
        OnlineMoonlightService onlineMoonlightService = getOnlineMoonlightServiceWithRealValues();
        onlineMoonlightService.init();
        for(int i = 0; i < 6; i++){
            Message message = getMessage(i, i, 11);
            onlineMoonlightService.receive(message);
        }
        for(int i = 0; i < 6; i++){
            Message message = getMessage(i, i, 7);
            onlineMoonlightService.receive(message);
        }
        for(int i = 0; i < 6; i++){
            Message message = getMessage(i, 6+i, 11);
            onlineMoonlightService.receive(message);
        }
        Message message = serviceTest.getResultsMessage();
        assertTrue(message instanceof ResultsMessage);
    }

    @Test
    void sendingMessages2(){
        ServiceTest serviceTest = new ServiceTest();
        DataBus dataBus = DataBus.getInstance();
        dataBus.notify(serviceTest);
        OnlineMoonlightService onlineMoonlightService = getOnlineMoonlightServiceWithRealValues();
        onlineMoonlightService.init();

        List<Message> messages = new ArrayList<>();
        messages.add(getMessage(1, 1, 11));
        messages.add(getMessage(0, 1, 11));
        messages.add(getMessage(1, 2, 11));
        messages.add(getMessage(2, 1, 11));
        messages.add(getMessage(3, 1, 11));
        messages.add(getMessage(4, 1, 11));
        messages.add(getMessage(3, 2, 11));
        messages.add(getMessage(4, 3, 11));
        for(int i = 0; i < messages.size(); i++){
            onlineMoonlightService.receive(messages.get(i));
        }
        messages = new ArrayList<>();

        messages.add(getMessage(5, 2, 11));

        messages.add(getMessage(1, 2, 1));
        messages.add(getMessage(1, 3, 11));
        messages.add(getMessage(0, 1, 11));
        messages.add(getMessage(2, 3, 11));
        messages.add(getMessage(3, 3, 11));
        messages.add(getMessage(4, 3, 11));
        messages.add(getMessage(5, 3, 11));
        messages.add(getMessage(0, 3, 11));
        for(int i = 0; i < messages.size(); i++){
            onlineMoonlightService.receive(messages.get(i));
        }
        messages = new ArrayList<>();
        messages.add(getMessage(2, 3, 7));
        messages.add(getMessage(4, 4, 11));
        messages.add(getMessage(5, 4, 11));
        messages.add(getMessage(0, 4, 11));
        for(int i = 0; i < messages.size(); i++){
            onlineMoonlightService.receive(messages.get(i));
        }

        Message message = serviceTest.getResultsMessage();
        assertTrue(message instanceof ResultsMessage);
    }

    private OnlineMoonlightService getOnlineMoonlightService(){
        Formula formula = mock(Formula.class);
        SpatialModel spatialModel = mock(SpatialModel.class);
        Map atoms = mock(Map.class);
        Map distances = mock(Map.class);
        return new OnlineMoonlightService(formula, spatialModel, atoms, distances, 6);
    }

    private OnlineMoonlightService getOnlineMoonlightServiceWithRealValues(){
        Formula formula = formula();
        SpatialModel spatialModel = buildSpatialModel(6);
        Map atoms = getOnlineAtoms();
        Map distances = setDistanceFunctions(spatialModel);
        return new OnlineMoonlightService(formula, spatialModel, atoms, distances, 6);
    }

    private Message getMessage(int id, double time, int people){
        String jsonMessage = "{" +
                "'id': "+id+","+
                "'place': 'School',"+
                "'noise': 30,"+
                "'people': "+people+","+
                "'time': "+time+
                "}";
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

    private static HashMap<String, Function<SpatialModel<Double>, DistanceStructure<Double, ?>>>
    setDistanceFunctions(SpatialModel<Double> city) {
        HashMap<String, Function<SpatialModel<Double>, DistanceStructure<Double, ?>>> distanceFunctions = new HashMap<>();
        distanceFunctions.put("distance",
                m -> new DefaultDistanceStructure<Double, Double>(x -> x,
                        new DoubleDomain(), 60.0, Double.MAX_VALUE, city));
        return distanceFunctions;
    }

    public class ServiceTest implements Service{
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