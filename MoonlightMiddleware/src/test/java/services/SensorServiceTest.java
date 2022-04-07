package services;

import messages.JsonOfficeSensorMessage;
import org.junit.jupiter.api.Test;
import subscriber.MQTTSubscriber;
import subscriber.Subscriber;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class SensorServiceTest {

    @Test
    void simpleSensorServiceTest(){
        Subscriber<String> subscriber = mock(MQTTSubscriber.class);
        SensorService sensorService = new SensorService(subscriber, JsonOfficeSensorMessage.class);
        sensorService.init();
        assertTrue(sensorService.isRunning());
    }

    @Test
    void sensorMessageTest(){
        Subscriber<String> subscriber = mock(MQTTSubscriber.class);
        SensorService sensorService = new SensorService(subscriber, JsonOfficeSensorMessage.class);
        assertAll(() -> sensorService.messageArrived("topic", getBasicJSON()));
    }

    public String getBasicJSON(){
        return "{" +
                    "'id': 1,"+
                    "'place': 'School',"+
                    "'noise': 30,"+
                    "'people': 11,"+
                    "'time': 2"+
                "}";
    }
}
