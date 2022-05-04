package services;

import messages.CommonSensorsMessage;
import messages.Message;
import messages.ResultsMessage;
import thingsBoard.ThingsboardConnector;
import thingsBoard.ThingsboardMoonlightConnector;
import thingsBoard.ThingsboardSensorsConnector;

import java.util.Map;

public class ResultThingsboardService implements Service{
    private final Map<String, String> deviceAccessTokens;

    public ResultThingsboardService(Map<String, String> deviceAccessToken, int qos){
        this.deviceAccessTokens = deviceAccessToken;
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
        }else if(message instanceof ResultsMessage){
            thingsboardCommunication =
                    new ThingsboardMoonlightConnector(deviceAccessTokens);
        }
        else {
            throw new UnsupportedOperationException("Message type not supported");
        }
        thingsboardCommunication.sendMessage(message);
    }

    @Override
    public void init() {

    }

    @Override
    public void stop() {

    }
}
