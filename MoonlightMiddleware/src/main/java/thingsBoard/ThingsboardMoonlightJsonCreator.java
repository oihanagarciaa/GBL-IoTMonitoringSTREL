package thingsBoard;

import eu.quanticol.moonlight.core.base.Box;
import eu.quanticol.moonlight.core.signal.SpaceTimeSignal;
import messages.Message;
import messages.ResultsMessage;

import java.util.Map;

public class ThingsboardMoonlightJsonCreator implements ThingsboardJsonCreator {
    private final Map<String, String> deviceAccessToken;
    private final Message message;

    public ThingsboardMoonlightJsonCreator(Map<String, String> deviceAccessToken, Message message){
        this.deviceAccessToken = deviceAccessToken;
        this.message = message;
    }

    //TODO: Thingsboard and Json
    // thingsboard just do NOT support objects and arrays
    // everything must be __ : __
    // think how to send the results
    // create multiple jsons and send them to different devices
    @Override
    public String getJson() {
        ResultsMessage<Boolean> resultsMessage = (ResultsMessage<Boolean>) message;
        SpaceTimeSignal<Double, Box<Boolean>> results = resultsMessage.getSpaceTimeSignal();
        System.out.println("- - - - -  - THINGSBOARD MOONLIGHT RESULTS - - - - - -");
        System.out.println(results.getSegments().toList());
        System.out.println("_");

        String jsonMessage = "{ 'monitorstate' = true}";
        return jsonMessage;
    }

    @Override
    public String getUsername() {
        String username = deviceAccessToken.get("Monitor");
        return username;
    }
}
