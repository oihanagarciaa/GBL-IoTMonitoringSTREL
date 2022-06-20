import connection.MQTTSubscriber;
import connection.MessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

public class MQTTTest {

    @Test
    void simpleMessageArriveTest(){
        MQTTSubscriber subscriber = new MQTTSubscriber("tcp://localhost:1883",
                "topic", "", "");
        TestListener testListener = new TestListener();
        subscriber.addListener(testListener);
        subscriber.messageArrived("topic", mock(MqttMessage.class));
        assertEquals(true, testListener.isMessageArrived());
    }

    class TestListener implements MessageListener{
        boolean messageArrived = false;
        public TestListener(){};
        @Override
        public void messageArrived(String topic, String jsonMessage) {
            messageArrived = true;
        }

        public boolean isMessageArrived() {
            return messageArrived;
        }
    }
}
