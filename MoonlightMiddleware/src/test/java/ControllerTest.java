import controller.Controller;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import services.OnlineMoonlightService;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ControllerTest {

    @Mock Controller controller;

    @Test
    void initializationTest(){
        OnlineMoonlightService onlineMoonlightService = new OnlineMoonlightService();
        controller = mock(Controller.class);
        controller.initializeService(onlineMoonlightService);

        verify(controller,times(1)).initializeService(onlineMoonlightService);
    }

}
