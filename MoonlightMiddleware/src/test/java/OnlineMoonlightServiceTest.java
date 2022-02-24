import eu.quanticol.moonlight.domain.AbstractInterval;
import eu.quanticol.moonlight.formula.AtomicFormula;
import eu.quanticol.moonlight.formula.Formula;
import eu.quanticol.moonlight.io.MoonLightRecord;
import eu.quanticol.moonlight.signal.DataHandler;
import eu.quanticol.moonlight.signal.RecordHandler;
import eu.quanticol.moonlight.signal.online.SpaceTimeSignal;
import eu.quanticol.moonlight.signal.online.Update;
import eu.quanticol.moonlight.space.DistanceStructure;
import eu.quanticol.moonlight.space.SpatialModel;
import eu.quanticol.moonlight.util.Pair;
import eu.quanticol.moonlight.util.Utils;
import org.junit.jupiter.api.Test;
import services.OnlineMoonlightService;

import java.util.*;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class OnlineMoonlightServiceTest {

    OnlineMoonlightService onlineMoonlightService;


    public void startOnlineMoonlightService(){
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
        //TODO: change the exception class
        startOnlineMoonlightService();
        assertThrows(Exception.class, onlineMoonlightService::run);
    }

    //////No va
    @Test
    void getResponseFromServiceTest(){
        OnlineMoonlightService onlineMoonlightService;
        Formula formula = new AtomicFormula("manyPeople");
        SpatialModel<Double> model;
        HashMap<Pair<Integer, Integer>, Double> cityMap = new HashMap<>();
        model = Utils.createSpatialModel(6, cityMap);

        Map<String, Function<MoonLightRecord, AbstractInterval<Boolean>>> atoms = new HashMap<>();
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
        onlineMoonlightService.updateService(update);
        onlineMoonlightService.run();
        SpaceTimeSignal<Double, AbstractInterval<Boolean>> signal = onlineMoonlightService.getResponseFromService();
        System.out.println(signal);
        assertNotNull(signal);
    }
    private static AbstractInterval<Boolean> booleanInterval(boolean cond) {
        return cond ? new AbstractInterval<>(true, true) :
                new AbstractInterval<>(false, false);
    }

}
