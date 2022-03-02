import controller.ConnType;
import controller.Controller;
import controller.MainController;
import dataconverter.ConverterStringMoonlightRecord;
import dataconverter.DataConverter;
import dataconverter.OnlineMoonlightBuffer;
import eu.quanticol.moonlight.domain.AbstractInterval;
import eu.quanticol.moonlight.domain.DoubleDistance;
import eu.quanticol.moonlight.formula.AtomicFormula;
import eu.quanticol.moonlight.formula.Formula;
import eu.quanticol.moonlight.io.MoonLightRecord;
import eu.quanticol.moonlight.signal.online.Update;
import eu.quanticol.moonlight.space.DistanceStructure;
import eu.quanticol.moonlight.space.SpatialModel;
import eu.quanticol.moonlight.util.Pair;
import eu.quanticol.moonlight.util.Utils;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import services.MonitorType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    void testTypicalDataReceived() {
        MainController controller = controllerInit();
        MainController spy = spy(controller);

        spy.run();

        String expectedResult = "test";
        List<String> l = new ArrayList<>();
        l.add("test");
        doReturn(l).when(spy).getResults();

        assertEquals(expectedResult, spy.getResults().get(0));
    }

    @Test
    void updateDataMainControllerTest(){
        MainController controller = controllerInitRealValues();
        controller.initializeService();
        controller.run();
        for(int i = 0; i < 3; i++){
            controller.updateData(4, "");
        }
        List<String> results = controller.getResults();
        assertEquals(3, results.size());
    }

    public static MainController controllerInit() {
        MainController controller = new MainController();
        String sourceId = "tcp://localhost:1883";
        ConnType connType = ConnType.MQTT;
        MonitorType monitorType = MonitorType.ONLINE_MOONLIGHT;
        Formula f = mock(Formula.class);
        SpatialModel<Double> model = mock(SpatialModel.class);
        Map<String, Function<MoonLightRecord, AbstractInterval<Boolean>>> atoms = mock(Map.class);
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

    public static MainController controllerInitRealValues(){
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

    public static Formula formula() {
        Formula controlPeople = new AtomicFormula("manyPeople");
        return controlPeople;
    }

    public static Map<String, Function<MoonLightRecord, AbstractInterval<Boolean>>> getOnlineAtoms() {
        Map<String, Function<MoonLightRecord, AbstractInterval<Boolean>>> atoms = new HashMap<>();
        atoms.put("manyPeople", a -> booleanInterval(a.get(2, Integer.class) < 10));
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
                        new DoubleDistance(), 60.0, Double.MAX_VALUE, city));
        return distanceFunctions;
    }
}