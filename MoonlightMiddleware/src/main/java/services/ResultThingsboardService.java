package services;

import eu.quanticol.moonlight.core.signal.Sample;
import main.DataBus;
import messages.CommonSensorsMessage;
import messages.Message;
import messages.ResultsMessage;
import thingsboard.ThingsboardConnector;
import thingsboard.ThingsboardMoonlightConnector;
import thingsboard.ThingsboardSensorsConnector;

import java.util.List;
import java.util.Map;

public class ResultThingsboardService implements Service{
    private Map<String, String> deviceAccessTokens;
    private final String broker;
    private final String topic;
    private double lastTime;

    public ResultThingsboardService(String broker, String topic, Map<String, String> deviceAccessToken){
        this.broker = broker;
        this.topic = topic;
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
        ThingsboardConnector thingsboardCommunication = null;
        if(message instanceof CommonSensorsMessage<?> commonSensorsMessage){
            thingsboardCommunication =
                    new ThingsboardSensorsConnector(broker, topic, deviceAccessTokens);
            thingsboardCommunication.sendMessage(commonSensorsMessage);
        }else if(message instanceof ResultsMessage<?> resultsMessage){
            thingsboardCommunication =
                    new ThingsboardMoonlightConnector(broker, topic, deviceAccessTokens, lastTime);
            lastTime = getLastResultsMessageTimeValue(resultsMessage);
            thingsboardCommunication.sendMessage(resultsMessage);
        }
    }

    @Override
    public void init() {

    }

    @Override
    public void stop() {
        deviceAccessTokens = null;
        DataBus.getInstance().unsubscribe(this);
    }

    public double getLastResultsMessageTimeValue(ResultsMessage resultsMessage){
        List<Sample> segments = resultsMessage.getSpaceTimeSignal().getSegments().toList();
        Double time = (Double) segments.get(segments.size()-1).getStart();
        return time;
    }
}
