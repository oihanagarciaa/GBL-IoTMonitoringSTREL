package services;

import eu.quanticol.moonlight.core.signal.Sample;
import messages.CommonSensorsMessage;
import messages.Message;
import messages.ResultsMessage;
import thingsBoard.ThingsboardConnector;
import thingsBoard.ThingsboardMoonlightConnector;
import thingsBoard.ThingsboardSensorsConnector;

import java.util.List;
import java.util.Map;

public class ResultThingsboardService implements Service{
    private final Map<String, String> deviceAccessTokens;
    private double lastTime;

    public ResultThingsboardService(Map<String, String> deviceAccessToken, int qos){
        this.deviceAccessTokens = deviceAccessToken;
        lastTime = 0;
    }

    //TODO: ¿?¿?¿?¿?¿?¿? isRunning, init, stop ?¿?¿?¿?¿?
    @Override
    public boolean isRunning() {
        return deviceAccessTokens != null;
    }

    @Override
    public void receive(Message message) {
        //TODO: Make thingsboard Json creator abstract and add the send message
        // to thingsboard inside this class as an abstract function
        ThingsboardConnector thingsboardCommunication = null;
        if(message instanceof CommonSensorsMessage){
            thingsboardCommunication =
                    new ThingsboardSensorsConnector(deviceAccessTokens);
            thingsboardCommunication.sendMessage(message);
        }else if(message instanceof ResultsMessage){
            thingsboardCommunication =
                    new ThingsboardMoonlightConnector(deviceAccessTokens, lastTime);
            lastTime = getLastResultsMessageTimeValue((ResultsMessage) message);
            thingsboardCommunication.sendMessage(message);/**/
        }
    }

    @Override
    public void init() {

    }

    @Override
    public void stop() {

    }

    public double getLastResultsMessageTimeValue(ResultsMessage resultsMessage){
        List<Sample> segments = resultsMessage.getSpaceTimeSignal().getSegments().toList();
        Double time = (Double) segments.get(segments.size()-1).getStart();
        return time;
    }
}
