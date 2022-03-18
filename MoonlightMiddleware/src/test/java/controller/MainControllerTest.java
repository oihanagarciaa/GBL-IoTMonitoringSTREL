package controller;

import eu.quanticol.moonlight.core.base.Pair;
import eu.quanticol.moonlight.core.base.Tuple;
import eu.quanticol.moonlight.core.formula.Formula;
import eu.quanticol.moonlight.core.space.DefaultDistanceStructure;
import eu.quanticol.moonlight.core.space.DistanceStructure;
import eu.quanticol.moonlight.core.space.SpatialModel;
import eu.quanticol.moonlight.core.base.Box;
import eu.quanticol.moonlight.domain.DoubleDomain;
import eu.quanticol.moonlight.formula.AtomicFormula;
import eu.quanticol.moonlight.util.Utils;

import messages.Message;
import messages.OfficeMessage;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import services.MonitorType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
class MainControllerTest {

    @Test
    void basicControllerInit() {
        Controller controller = controllerInit();

        assertTrue(controller.run());
    }

    @Test
    void updateDataMainControllerTest(){
        MainController controller = controllerInitRealValues();
        controller.initializeService();
        controller.run();
        Message message = new OfficeMessage();
        message.transformReceivedData("asdasd/4", basicValidJSON());
        for(int i = 0; i < 3; i++){
            controller.updateData(message);
        }
        assertEquals(3, controller.buffer.get().size());
    }

    private static MainController controllerInit() {
        MainController controller = new MainController();
        String sourceId = "tcp://localhost:1883";
        ConnType connType = ConnType.MQTT;
        MonitorType monitorType = MonitorType.ONLINE_MOONLIGHT;
        Formula f = mock(Formula.class);
        SpatialModel<Double> model = mock(SpatialModel.class);
        Map<String, Function<Tuple, Box<Boolean>>> atoms = mock(Map.class);
        HashMap<String, Function<SpatialModel<Double>, DistanceStructure<Double, ?>>> distance = mock(HashMap.class);

        controller.setDataSource(sourceId);
        controller.setConnectionType(connType);
        controller.setMonitorType(monitorType);
        controller.setFormula(f);
        controller.setSpatialModel(model);
        controller.setAtomicFormulas(atoms);
        controller.setDistanceFunctions(distance);
        return controller;
    }

    private static MainController controllerInitRealValues(){
        MainController c = new MainController();
        c.setMonitorType(MonitorType.ONLINE_MOONLIGHT);
        c.setConnectionType(ConnType.MQTT);
        c.setDataSource("tcp://localhost:1883");
        SpatialModel<Double> spatialModel = buildSpatialModel(6);
        c.setSpatialModel(spatialModel);
        c.setFormula(formula());
        c.setAtomicFormulas(getOnlineAtoms());
        c.setDistanceFunctions(setDistanceFunctions(spatialModel));
        return c;
    }

    private static SpatialModel<Double> buildSpatialModel(int size){
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

    private static String basicValidJSON() {
        return "{\n" +
                "            \"place\": 3\n" +
                "            \"noise\": 40\n" +
                "            \"people\": 30\n" +
                "            \"time\": 1.0\n" +
                "}";
    }
}