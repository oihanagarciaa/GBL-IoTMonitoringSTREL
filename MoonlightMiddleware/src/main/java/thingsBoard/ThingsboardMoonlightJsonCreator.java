package thingsBoard;

import messages.Message;

import java.util.Map;

public class ThingsboardMoonlightJsonCreator implements ThingsboardJsonCreator {
    private final Map<String, String> deviceAccessToken;
    private final Message message;

    public ThingsboardMoonlightJsonCreator(Map<String, String> deviceAccessToken, Message message){
        this.deviceAccessToken = deviceAccessToken;
        this.message = message;
    }

    @Override
    public String getJson() {
        //TODO
        String jsonMessage = "{}";
        return jsonMessage;
    }

    @Override
    public String getUsername() {
        String username = deviceAccessToken.get("Monitor");
        return username;
    }
}
