package thingsboard;

import connection.ConnType;
import eu.quanticol.moonlight.core.base.Box;
import eu.quanticol.moonlight.core.signal.Sample;
import messages.Message;
import messages.ResultsMessage;

import java.util.List;
import java.util.Map;

public class ThingsboardMoonlightConnector extends ThingsboardConnector {
    private final ConnType connType;
    private final String broker;
    private final String topic;
    private final Map<String, String> deviceAccessToken;
    private Boolean resultBoolean;
    double lastTime;

    public ThingsboardMoonlightConnector(ConnType connType, String broker, String topic,
                       Map<String, String> deviceAccessToken, double lastTime){
        this.connType = connType;
        this.broker = broker;
        this.topic = topic;
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
                String username = getUsername(deviceAccessToken,
                        String.valueOf(j+1));
                Box box = (Box) valueList.get(j);
                resultBoolean = (Boolean) box.getStart();
                String json = getJson();
                publishToThingsboard(connType, broker, topic, username, json);
            }
        }
        lastTime = (double) resultsList.get(resultsList.size()-1).getStart();
    }

    //If I create the json file like bellow, thingsboard takes true/false as a string
    @Override
    public String getJson() {
        return "{ 'result' = "+(resultBoolean? 1:0 )+"}";
    }
}
