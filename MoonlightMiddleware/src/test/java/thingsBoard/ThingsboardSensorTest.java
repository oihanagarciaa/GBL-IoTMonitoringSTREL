package thingsBoard;

import com.google.gson.Gson;
import messages.OfficeSensorMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ThingsboardSensorTest {
    Map<String, String> sensorAccessToken;

    @BeforeEach
    void initMap(){
        sensorAccessToken = new HashMap<>();
        sensorAccessToken.put("1", "askjdzsdjhfb");
        sensorAccessToken.put("2", "6s5df4as6dfs");
        sensorAccessToken.put("3", "dfsdfagawefs");
    }

    OfficeSensorMessage getMessage(){
        String json = "{'id': 1, 'time':123456," +
                     "   'temp':20.2," +
                     "   'hum': 15.3," +
                     "   'co2': 123," +
                     "   'tvoc':45}";
        OfficeSensorMessage officeSensorMessage =
                new Gson().fromJson(json, OfficeSensorMessage.class);
        return officeSensorMessage;
    }

    @Test
    void thingsboardConversion(){
        ThingsboardSensorsConnector thingsSensors = new ThingsboardSensorsConnector(sensorAccessToken);
        thingsSensors.commonSensorsMessage = getMessage();
        assertEquals("{\"id\":1,\"time\":123456.0,\"temp\":20.2,\"hum\":15.3,\"co2\":123,\"tvoc\":45}", thingsSensors.getJson());
        assertEquals("6s5df4as6dfs", thingsSensors.getUsername());
    }
}
