package services;

import org.junit.jupiter.api.Test;
import service_builders.ServiceBuilder;

import java.util.HashMap;
import java.util.Map;

public class RunnerServiceTest {
    String jsonMessage = """
            {
                          "services": [
                            {
                              "command": "start",      "serviceType": "moonlight",
                              "formula": "import dsl.* \\nval temp = \\"temperature\\" greaterThan 10 \\n val humidity = \\"humidity\\" lessThan 10 \\n val f1 = temp and (eventually(humidity) within interval(0, 1)) or somewhere(humidity) \\n      Specification.formula = f1"   \s
                            }  ]
                        }""";
    @Test
    void getFormula(){
        Map<String, ServiceBuilder> serviceBuilders = new HashMap<>();
        RunnerService runnerService = new RunnerService(null, serviceBuilders);
        runnerService.messageArrived("", jsonMessage);
    }
}
