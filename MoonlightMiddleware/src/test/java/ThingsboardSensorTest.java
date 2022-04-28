import com.google.gson.Gson;
import messages.OfficeSensorMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import thingsBoard.ThingsboardSensorsCommunication;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ThingsboardSensorTest {
    Map<String, String> sensorAccessToken;

    @BeforeEach
    void initMap(){
        sensorAccessToken = new HashMap<>();
        sensorAccessToken.put("Thingy1", "askjdzsdjhfb");
        sensorAccessToken.put("Thingy2", "6s5df4as6dfs");
        sensorAccessToken.put("Thingy3", "dfsdfagawefs");
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
        ThingsboardSensorsCommunication thingsSensors = new ThingsboardSensorsCommunication(sensorAccessToken);
        assertEquals("{\"temperature\":20.2,\"humidity\":15.3,\"co2\":123,\"tvoc\":45}", thingsSensors.getJson(getMessage()));
        assertEquals("6s5df4as6dfs", thingsSensors.getUsername(getMessage()));
    }
}
