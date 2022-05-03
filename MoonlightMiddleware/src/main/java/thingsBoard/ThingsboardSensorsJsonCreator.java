package thingsBoard;

import com.google.gson.Gson;
import messages.CommonSensorsMessage;
import messages.Message;

import java.util.Map;

public class ThingsboardSensorsJsonCreator implements ThingsboardJsonCreator {
    private final Map<String, String> deviceAccessToken;
    private final Message message;
    public ThingsboardSensorsJsonCreator(Map<String, String> deviceAccessToken, Message message){
        this.deviceAccessToken = deviceAccessToken;
        this.message = message;
    }

    @Override
    public String getJson() {
        CommonSensorsMessage commonSensorsMessage = (CommonSensorsMessage) message;
        Gson gson = new Gson();
        String jsonInString = gson.toJson(commonSensorsMessage);
        System.out.println("JSON: "+jsonInString);
        return jsonInString;
    }

    @Override
    public String getUsername() {
        CommonSensorsMessage commonSensorsMessage = (CommonSensorsMessage) message;
        int num = commonSensorsMessage.getId()+1;
        String username = deviceAccessToken.get("Thingy"+num);
        return username;
    }
}
