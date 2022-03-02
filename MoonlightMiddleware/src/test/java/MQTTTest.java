import controller.MainController;
import org.junit.jupiter.api.Test;
import subscriber.MQTTSubscriber;
import subscriber.Subscriber;
import static org.junit.jupiter.api.Assertions.*;

public class MQTTTest {

    @Test
    void receiveTest(){
        MainController controller = MainControllerTest.controllerInitRealValues();
        controller.run();
        Subscriber subscriber = new MQTTSubscriber("tcp:localhost:1883", controller);
        subscriber.receive("asas/2", "{ \"place\": 2, \"noise\": 300, \"people\": 10}");
    }

    @Test
    void receiveTestWrongTopic(){
        MainController controller = MainControllerTest.controllerInitRealValues();
        controller.run();
        Subscriber subscriber = new MQTTSubscriber("tcp:localhost:1883", controller);
        assertThrows(NumberFormatException.class, ()-> subscriber.receive("asas/asdf", "{ \"place\": 2, \"noise\": 300, \"people\": 10}"));
    }


}
