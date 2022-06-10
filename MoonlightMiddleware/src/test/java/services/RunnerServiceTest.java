package services;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import serviceBuilders.ServiceBuilder;

import java.util.HashMap;
import java.util.Map;

public class RunnerServiceTest {
    String jsonMessage = "{\n" +
            "  \"services\": [\n" +
            "    {\n" +
            "      \"command\": \"start\","+
            "      \"serviceType\": \"moonlight\",\n" +
            "      \"formula\": \"temp and (eventually(humidity) within interval(0, 1))\"" +
            "    }"+
            "  ]\n" +
            "}";
    @Test
    @Disabled()
    void getFormula(){
        Map<String, ServiceBuilder> serviceBuilders = new HashMap<>();
        RunnerService runnerService = new RunnerService(null, serviceBuilders);
        runnerService.messageArrived("", jsonMessage);
    }
}
