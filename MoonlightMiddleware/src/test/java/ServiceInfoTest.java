import com.google.gson.Gson;
import messages.ConfigMessage;
import org.junit.jupiter.api.Test;
import services.serviceInfo.ServiceInfo;

import java.lang.reflect.Type;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ServiceInfoTest {
    String configJson = "{\n" +
        "  \"services\": [\n" +
        "    {\n" +
        "      \"serviceType\": \"sensors\",\n" +
        "      \"connection\": {\n" +
        "        \"type\": \"mqtt\",\n" +
        "        \"settings\": {\n" +
        "          \"broker\": \"sensorBroker\",\n" +
        "          \"topic\": \"sensorTopic\",\n" +
        "          \"username\": \"username\",\n" +
        "          \"password\": \"password\"\n" +
        "        }\n" +
        "      }\n" +
        "    },\n" +
        "    {\n" +
        "      \"serviceType\": \"moonlight\",\n" +
        "      \"formula\": \"String ... \"\n" +
        "    },\n" +
        "    {\n" +
        "      \"serviceType\": \"thingsboard\",\n" +
        "      \"connection\": {\n" +
        "        \"type\": \"mqtt\",\n" +
        "        \"settings\": {\n" +
        "          \"topic\": \"thingsboardTopic\"\n" +
        "        }\n" +
        "      },\n" +
        "      \"devices\": [\n" +
        "        {\n" +
        "          \"identifier\": \"Thingy1\",\n" +
        "          \"accessKey\": \"XXX\"\n" +
        "        }\n" +
        "      ]\n" +
        "    }\n" +
        "  ]\n" +
        "}";

    @Test
    void simpleService0Test(){
        ConfigMessage message = new Gson().fromJson(configJson, (Type) ConfigMessage.class);
        ServiceInfo serviceInfo = message.getServiceInfo().get(0);
        assertEquals("sensors", serviceInfo.getServiceType());
        assertEquals("sensorBroker", serviceInfo.getConnection().getSettings().getBroker());
    }

    @Test
    void simpleService1Test(){
        ConfigMessage message = new Gson().fromJson(configJson, (Type) ConfigMessage.class);
        ServiceInfo serviceInfo = message.getServiceInfo().get(1);
        assertEquals("moonlight", serviceInfo.getServiceType());
        assertEquals("String ... ", serviceInfo.getFormula());
    }

    @Test
    void simpleService2Test(){
        ConfigMessage message = new Gson().fromJson(configJson, (Type) ConfigMessage.class);
        ServiceInfo serviceInfo = message.getServiceInfo().get(2);
        assertEquals("thingsboard", serviceInfo.getServiceType());
        assertEquals("mqtt", serviceInfo.getConnection().getType());
        assertEquals("thingsboardTopic", serviceInfo.getConnection()
                .getSettings().getTopic());
        assertEquals("XXX", serviceInfo.getDevices().get("Thingy1"));
    }
}
