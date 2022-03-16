import eu.quanticol.moonlight.core.base.Box;
import eu.quanticol.moonlight.core.base.DataHandler;
import eu.quanticol.moonlight.core.base.MoonLightRecord;
import eu.quanticol.moonlight.core.base.Pair;
import eu.quanticol.moonlight.core.formula.Formula;
import eu.quanticol.moonlight.core.signal.SpaceTimeSignal;
import eu.quanticol.moonlight.core.space.DistanceStructure;
import eu.quanticol.moonlight.core.space.SpatialModel;
import eu.quanticol.moonlight.formula.AtomicFormula;
import eu.quanticol.moonlight.offline.signal.RecordHandler;
import eu.quanticol.moonlight.online.signal.Update;
import eu.quanticol.moonlight.util.Utils;
import org.junit.jupiter.api.Test;
import services.OnlineMoonlightService;

import java.util.*;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class OnlineMoonlightServiceTest {

    OnlineMoonlightService onlineMoonlightService;


    private void startOnlineMoonlightService(){
        Formula formula = mock(Formula.class);
        SpatialModel model = mock(SpatialModel.class);
        Map atoms = mock(Map.class);
        HashMap distanceFunctions = mock(HashMap.class);
        onlineMoonlightService = new OnlineMoonlightService(formula, model, atoms, distanceFunctions);
        onlineMoonlightService.init();
    }

    @Test
    void serviceInitialization(){
        startOnlineMoonlightService();
        assertTrue(onlineMoonlightService.isRunning());
    }

    @Test
    void monitorWithoutUpdate(){
        startOnlineMoonlightService();
        assertThrows(IllegalArgumentException.class, () -> {onlineMoonlightService.run(new Update<>(0.0, 0.0, null));});
    }

    @Test
    void getResponseFromServiceTest(){
        OnlineMoonlightService onlineMoonlightService;
        Formula formula = new AtomicFormula("manyPeople");
        SpatialModel<Double> model;
        HashMap<Pair<Integer, Integer>, Double> cityMap = new HashMap<>();
        model = Utils.createSpatialModel(6, cityMap);

        Map<String, Function<MoonLightRecord, Box<Boolean>>> atoms = new HashMap<>();
        atoms.put("manyPeople", a -> booleanInterval(a.get(0, Integer.class) < 30));

        HashMap<String, Function<SpatialModel<Double>, DistanceStructure<Double, ?>>> distanceFunctions = new HashMap<>();

        onlineMoonlightService = new OnlineMoonlightService(formula, model, atoms, distanceFunctions);
        onlineMoonlightService.init();

        RecordHandler factory;
        factory = new RecordHandler(DataHandler.INTEGER);
        List<MoonLightRecord> signalSP = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            signalSP.add(factory.fromObjectArray(20));
        }
        Update<Double, List<MoonLightRecord>> update = new Update<>(0.0, 1.0, signalSP);
        onlineMoonlightService.run(update);
        SpaceTimeSignal<Double, Box<Boolean>> signal = onlineMoonlightService.getResponseFromService();
        System.out.println(signal);
        assertNotNull(signal);
    }
    private static Box<Boolean> booleanInterval(boolean cond) {
        return cond ? new Box<>(true, true) :
                new Box<>(false, false);
    }

}
