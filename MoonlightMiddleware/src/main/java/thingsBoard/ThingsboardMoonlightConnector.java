package thingsBoard;

import eu.quanticol.moonlight.core.base.Box;
import eu.quanticol.moonlight.core.signal.Sample;
import messages.Message;
import messages.ResultsMessage;

import java.util.List;
import java.util.Map;

public class ThingsboardMoonlightConnector extends ThingsboardConnector {
    private final Map<String, String> deviceAccessToken;
    private int device;
    private Boolean resultBoolean;
    double lastTime;

    public ThingsboardMoonlightConnector(Map<String, String> deviceAccessToken, double lastTime){
        this.deviceAccessToken = deviceAccessToken;
        this.lastTime = lastTime;
    }

    @Override
    public void sendMessage( Message message) {
        ResultsMessage resultsMessage = (ResultsMessage) message;
        List<Sample> resultsList = resultsMessage.getSpaceTimeSignal().getSegments().toList();
        int counter = 0;
        double time;
        do{
            time = (double) resultsList.get(counter).getStart();
            counter ++;
        }while (time < lastTime);
        for(int i = counter-1; i < resultsList.size()-1; i++){
            List valueList = (List) resultsList.get(i).getValue();
            for(int j = 0; j < valueList.size(); j++){
                device = j+1;
                String username = getUsername();
                String password = "";
                Box box = (Box) valueList.get(j);
                resultBoolean = (Boolean) box.getStart();
                String json = getJson();
                publishToThingsboard(username, password, json);
            }
        }
        lastTime = (double) resultsList.get(resultsList.size()-1).getStart();
    }

    //If I create the json file like bellow, thingsboard takes true/false as a string
    @Override
    public String getJson() {
        String jsonMessage = "{ 'result' = "+(resultBoolean? 1:0 )+"}";
        return jsonMessage;
    }

    @Override
    public String getUsername() {
        String username = deviceAccessToken.get("Thingy"+(device));
        return username;
    }
}
