import controller.MainController;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import subscriber.MQTTSubscriber;
import subscriber.Subscriber;
import static org.junit.jupiter.api.Assertions.*;

//TODO: Do MQTT tests
public class MQTTTest {

    @Disabled()
    @Test
    void receiveTest(){
        MainController controller = MainControllerTest.controllerInitRealValues();
        controller.run();
        Subscriber subscriber = new MQTTSubscriber("tcp:localhost:1883", controller);
        for( int i=0; i < 6; i++){
            subscriber.receive("asas/"+i, "{ \"place\": 2, \"noise\": 300, \"people\": 10}");
        }
    }

    @Disabled()
    @Test
    void receiveTestWrongTopic(){
        MainController controller = MainControllerTest.controllerInitRealValues();
        controller.run();
        Subscriber subscriber = new MQTTSubscriber("tcp:localhost:1883", controller);
        assertThrows(NumberFormatException.class, ()-> subscriber.receive("asas/asdf", "{ \"place\": 2, \"noise\": 300, \"people\": 10}"));
    }


}
