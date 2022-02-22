package controller;

import eu.quanticol.moonlight.formula.AtomicFormula;
import eu.quanticol.moonlight.formula.Formula;
import eu.quanticol.moonlight.space.GraphModel;
import eu.quanticol.moonlight.space.SpatialModel;
import messages.Message;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import services.MonitorType;
import services.OnlineMoonlightService;
import services.Service;

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



    @Disabled("to be reworked depending on the logic described")
    @Test
    void testTypicalDataReceived() {
        MainController controller = controllerInit();
        MainController spy = spy(controller);
        // TODO:
        //  we spy the Controller
        //  we replace the implementation of the service
        //  the service returns as result list ListOf("test")

        //when(spy.initializeService()).thenReturn(true);
        Message message = mock(Message.class);
        String expectedResult = "test";

        controller.updateData(message);

        assertEquals(expectedResult, controller.getResults().get(0));
    }

    @Disabled("Changed Controller implementation")
    @Test
    void initializationTest(){
        Service service = mock(Service.class);
        Controller controller = new MainController();


//        controller.initializeService(onlineMoonlightService);
//
//        verify(controller,times(1)).initializeService(onlineMoonlightService);
    }

    private MainController controllerInit() {
        MainController controller = new MainController();
        String sourceId = "source";
        ConnType connType = ConnType.MQTT;
        MonitorType monitorType = MonitorType.ONLINE_MOONLIGHT;
        Formula f = mock(Formula.class);
        SpatialModel<?> model = mock(SpatialModel.class);

        controller.setDataSource(sourceId);
        controller.setConnectionType(connType);
        controller.setMonitorType(monitorType);
        controller.setFormula(f);
        controller.setSpatialModel(model);
        return controller;
    }
}