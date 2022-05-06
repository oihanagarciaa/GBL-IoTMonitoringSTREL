package thingsBoard;

import com.google.gson.Gson;
import messages.CommonSensorsMessage;
import messages.Message;

import java.util.Map;

public class ThingsboardSensorsConnector extends ThingsboardConnector {
    private final Map<String, String> deviceAccessToken;
    protected CommonSensorsMessage commonSensorsMessage;

    public ThingsboardSensorsConnector(Map<String, String> deviceAccessToken){
        this.deviceAccessToken = deviceAccessToken;
    }

    @Override
    public void sendMessage(Message message) {
        commonSensorsMessage = (CommonSensorsMessage) message;
        String username = getUsername();
        String password = "";
        String json = getJson();
        publishToThingsboard(username, password, json);
    }

    @Override
    public String getJson() {
        Gson gson = new Gson();
        String jsonInString = gson.toJson(commonSensorsMessage);
        System.out.println("JSON: "+jsonInString);
        return jsonInString;
    }

    @Override
    public String getUsername() {
        int num = commonSensorsMessage.getId()+1;
        String username = deviceAccessToken.get("Thingy"+num);
        return username;
    }
}
