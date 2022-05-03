package services;

import messages.CommonSensorsMessage;
import messages.Message;
import messages.ResultsMessage;
import subscriber.MQTTPublisher;
import thingsBoard.ThingsboardJsonCreator;
import thingsBoard.ThingsboardMoonlightJsonCreator;
import thingsBoard.ThingsboardSensorsJsonCreator;

import java.util.Map;

public class ResultThingsboardService implements Service{
    private final Map<String, String> deviceAccessTokens;
    private final static String broker = "tcp://thingsboard.cloud:1883";
    private final static String topic = "v1/devices/me/telemetry";
    private final int qos;
    private MQTTPublisher mqttPublisher;

    public ResultThingsboardService(Map<String, String> deviceAccessToken, int qos){
        this.deviceAccessTokens = deviceAccessToken;
        this.qos = qos;
    }

    @Override
    public boolean isRunning() {
        return mqttPublisher != null;
    }

    //TODO: to do
    @Override
    public void receive(Message message) {
        //TODO: Make thingsboard Json creator abstract and add the send message
        // to thingsboard inside this class as an abstract function
        ThingsboardJsonCreator thingsboardCommunication = null;
        if(message instanceof CommonSensorsMessage){
            thingsboardCommunication =
                    new ThingsboardSensorsJsonCreator(deviceAccessTokens, message);
        }else if(message instanceof ResultsMessage){
            thingsboardCommunication =
                    new ThingsboardMoonlightJsonCreator(deviceAccessTokens, message);
        }
        else {
            throw new UnsupportedOperationException("Message type not supported");
        }
        sendMessageToThingsboard(thingsboardCommunication);
    }

    private void sendMessageToThingsboard(ThingsboardJsonCreator thingsboardJsonCreator){
        String username = thingsboardJsonCreator.getUsername();
        String password = "";
        String json = thingsboardJsonCreator.getJson();
        mqttPublisher.publish(username, password, json);
    }

    @Override
    public void init() {
        mqttPublisher = new MQTTPublisher(broker, topic, qos);
    }

    @Override
    public void stop() {
        mqttPublisher = null;
    }
}
