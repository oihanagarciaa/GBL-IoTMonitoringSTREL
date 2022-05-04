package thingsBoard;

import messages.Message;

import java.util.Map;

public class ThingsboardMoonlightConnector extends ThingsboardConnector {
    private final Map<String, String> deviceAccessToken;

    public ThingsboardMoonlightConnector(Map<String, String> deviceAccessToken){
        this.deviceAccessToken = deviceAccessToken;
    }

    @Override
    public void sendMessage( Message message) {

    }



    //TODO: Thingsboard and Json
    // thingsboard just do NOT support objects and arrays
    // everything must be __ : __
    // think how to send the results
    // create multiple jsons and send them to different devices
    @Override
    public String getJson() {
        /*ResultsMessage<Boolean> resultsMessage = (ResultsMessage<Boolean>) message;
        SpaceTimeSignal<Double, Box<Boolean>> results = resultsMessage.getSpaceTimeSignal();
        System.out.println("- - - - -  - THINGSBOARD MOONLIGHT RESULTS - - - - - -");
        System.out.println(results.getSegments().toList());
        System.out.println("_");*/

        String jsonMessage = "{ 'monitorstate' = true}";
        return jsonMessage;
    }

    @Override
    public String getUsername() {
        String username = deviceAccessToken.get("Monitor");
        return username;
    }
}
