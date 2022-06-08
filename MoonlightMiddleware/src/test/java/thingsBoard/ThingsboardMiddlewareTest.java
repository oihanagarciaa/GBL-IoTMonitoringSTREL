package thingsBoard;

import com.google.gson.Gson;
import eu.quanticol.moonlight.core.base.Box;
import eu.quanticol.moonlight.core.base.Pair;
import eu.quanticol.moonlight.core.base.Tuple;
import eu.quanticol.moonlight.core.formula.Formula;
import eu.quanticol.moonlight.core.signal.SpaceTimeSignal;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import services.OnlineMoonlightService;
import services.Service;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ThingsboardMiddlewareTest {
    Map<String, String> sensorAccessToken;

    @BeforeEach
    void initMap(){
        sensorAccessToken = new HashMap<>();
        sensorAccessToken.put("1", "T6Tn0xfSJKolnUfxmZFr");
        sensorAccessToken.put("2", "U7THZHwXrc0cqCT3S0Yz");
        sensorAccessToken.put("3", "LRXoHowQzSHI4MIzlr5s");
        sensorAccessToken.put("Monitor", "EN2RFpa41RFQgVZrDNdy");
    }

    @Test
    void thingsboardConvertion(){
        ThingsboardMoonlightConnector thingsboardMiddleware = Mockito.spy(
                new ThingsboardMoonlightConnector(sensorAccessToken, 0));
        //doNothing().when(thingsboardMiddleware).
        //        publishToThingsboard(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());

        SpaceTimeSignal spaceTimeSignal = setOnlineMoonlight();
        ResultsMessage<Boolean> resultsMessage = new ResultsMessage<>(spaceTimeSignal);
        thingsboardMiddleware.sendMessage(resultsMessage);
        assertEquals(9, thingsboardMiddleware.lastTime);
    }

    //functions to get the SpaceTimeSignal
    public SpaceTimeSignal setOnlineMoonlight(){
        ServiceTest serviceTest = new ServiceTest();
        DataBus dataBus = DataBus.getInstance();
        dataBus.subscribe(serviceTest);
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
        for(int i = 0; i < 6; i++){
            Message message = getMessage(i, 7, 17);
            onlineMoonlightService.receive(message);
        }
        for(int i = 0; i < 6; i++){
            Message message = getMessage(i, 8, 17);
            onlineMoonlightService.receive(message);
        }
        for(int i = 0; i < 6; i++){
            Message message = getMessage(i, 9, 11);
            onlineMoonlightService.receive(message);
        }
        SpaceTimeSignal spaceTimeSignal = serviceTest.getSpaceTimeSignal();
        return spaceTimeSignal;
    }

    private OnlineMoonlightService getOnlineMoonlightServiceWithRealValues(){
        Formula formula = formula();
        SpatialModel spatialModel = buildSpatialModel(6);
        Map atoms = getOnlineAtoms();
        Map distances = setDistanceFunctions(spatialModel);
        return new OnlineMoonlightService(formula, spatialModel, atoms, distances, 12);
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

    public class ServiceTest implements Service {
        ResultsMessage resultsMessage;

        public SpaceTimeSignal getSpaceTimeSignal() {
            return resultsMessage.getSpaceTimeSignal();
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
