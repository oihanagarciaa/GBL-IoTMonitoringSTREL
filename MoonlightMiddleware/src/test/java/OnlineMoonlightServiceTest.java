import eu.quanticol.moonlight.domain.AbstractInterval;
import eu.quanticol.moonlight.formula.Formula;
import eu.quanticol.moonlight.io.MoonLightRecord;
import eu.quanticol.moonlight.signal.DataHandler;
import eu.quanticol.moonlight.signal.EnumerationHandler;
import eu.quanticol.moonlight.signal.RecordHandler;
import eu.quanticol.moonlight.signal.online.SpaceTimeSignal;
import eu.quanticol.moonlight.signal.online.Update;
import eu.quanticol.moonlight.space.SpatialModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import services.OnlineMoonlightService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class OnlineMoonlightServiceTest {

    OnlineMoonlightService onlineMoonlightService;

    @BeforeEach
    public void start(){
        Formula formula = mock(Formula.class);
        SpatialModel<?> model = mock(SpatialModel.class);
        onlineMoonlightService = new OnlineMoonlightService(formula, model);
        onlineMoonlightService.init();
    }

    @Test
    void serviceInitialization(){
        assertTrue(onlineMoonlightService.isRunning());
    }

    @Test
    void monitorWithoutUpdate(){
        //TODO: change the exception class
        assertThrows(Exception.class, onlineMoonlightService::run);
    }

    @Test
    void getResponseFromServiceTest(){
        RecordHandler factory;
        List<String> places;
        places = Arrays.asList("Hospital", "School", "MetroStop", "School", "MainSquare", "BusStop");
        factory = new RecordHandler(new EnumerationHandler<>(String.class, places.toArray(new String[0])), DataHandler.INTEGER, DataHandler.INTEGER);
        List<MoonLightRecord> signalSP = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            signalSP.add(factory.fromObjectArray(places.get(i), 20, 30));
        }
        Update<Double, List<MoonLightRecord>> update = new Update<Double, List<MoonLightRecord>>(0.0, 1.0, signalSP);
        onlineMoonlightService.askService(update);
        onlineMoonlightService.run();
        SpaceTimeSignal<Double, AbstractInterval<Boolean>> signal = onlineMoonlightService.getResponseFromService();
        System.out.println(signal);
        assertNotNull(signal);
    }

}
