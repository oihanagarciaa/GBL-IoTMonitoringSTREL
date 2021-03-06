package thingsboard;

import com.google.gson.Gson;
import connection.ConnType;
import messages.CommonSensorsMessage;
import messages.Message;

import java.util.Map;

public class ThingsboardSensorsConnector extends ThingsboardConnector {
    private final ConnType connType;
    private final Map<String, String> deviceAccessToken;
    private final String broker;
    private final String topic;
    protected CommonSensorsMessage commonSensorsMessage;

    public ThingsboardSensorsConnector(ConnType connType, String broker,
                                       String topic, Map<String, String> deviceAccessToken){
        this.connType = connType;
        this.broker = broker;
        this.topic = topic;
        this.deviceAccessToken = deviceAccessToken;
    }

    @Override
    public void sendMessage(Message message) {
        commonSensorsMessage = (CommonSensorsMessage) message;
        String username = getUsername(
                deviceAccessToken, String.valueOf(commonSensorsMessage.getId()+1));
        String json = getJson();
        publishToThingsboard(connType, broker, topic, username, json);
    }

    @Override
    public String getJson() {
        Gson gson = new Gson();
        String jsonInString = gson.toJson(commonSensorsMessage);
        return jsonInString;
    }
}
