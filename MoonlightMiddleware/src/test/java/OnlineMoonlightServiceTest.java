import org.junit.jupiter.api.Test;
import services.OnlineMoonlightService;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OnlineMoonlightServiceTest {

    @Test
    public void serviceInitialization(){
        OnlineMoonlightService onlineMoonlightService = new OnlineMoonlightService();
        onlineMoonlightService.init();
        assertTrue(onlineMoonlightService.isRunning());
    }

    @Test
    public void monitorWithoutUpdate(){
        OnlineMoonlightService onlineMoonlightService = new OnlineMoonlightService();
        onlineMoonlightService.init();
        assertThrows(Exception.class, onlineMoonlightService::run);
    }

}
