package services;

import eu.quanticol.moonlight.core.formula.Formula;
import eu.quanticol.moonlight.core.space.SpatialModel;
import org.junit.jupiter.api.Test;

import java.util.Map;

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
    void sendMessages(){
        OnlineMoonlightService onlineMoonlightService = getOnlineMoonlightService();
        //TODO
    }

    public OnlineMoonlightService getOnlineMoonlightService(){
        Formula formula = mock(Formula.class);
        SpatialModel spatialModel = mock(SpatialModel.class);
        Map atoms = mock(Map.class);
        Map distances = mock(Map.class);
        return new OnlineMoonlightService(formula, spatialModel, atoms, distances);
    }
}