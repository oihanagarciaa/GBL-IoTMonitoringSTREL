package thingsBoard;

import messages.Message;

import java.util.Map;

public class ThingsboardMoonlightCommunication extends ThingsboardCommunication{
    private final Map<String, String> deviceAccessToken;

    public ThingsboardMoonlightCommunication(Map<String, String> deviceAccessToken){
        this.deviceAccessToken = deviceAccessToken;
    }

    @Override
    public String getJson(Message message) {
        //TODO
        String jsonMessage = "{}";
        return jsonMessage;
    }

    @Override
    public String getUsername(Message message) {
        String username = deviceAccessToken.get("Monitor");
        return username;
    }
}
