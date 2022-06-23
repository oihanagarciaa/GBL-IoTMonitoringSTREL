package thingsboard;

import connection.ConnType;
import connection.MQTTSubscriber;
import connection.Publisher;
import messages.Message;
import connection.MQTTPublisher;
import simulation.ConnectionSimulations.PublisherSimulator;
import simulation.ConnectionSimulations.SensorSubscriberSimulator;

import java.util.Map;

public abstract class ThingsboardConnector {
    private final static int QOS = 0;

    public void publishToThingsboard(ConnType connectionType, String broker, String topic, String username, String json){
        Publisher publisher;
        if(connectionType == ConnType.MQTT) {
            publisher = new MQTTPublisher(broker, topic, QOS);
        }else if (connectionType == ConnType.REST){
            throw new UnsupportedOperationException("Not supported connection type");
        } else if (connectionType == ConnType.SIMULATION){
            publisher = new PublisherSimulator();
        }
        else {
            throw new UnsupportedOperationException("Not supported connection type");
        }
        publisher.publish(username, "", json);
    }

    public String getUsername(Map<String, String> deviceAccessToken, String key) {
        return deviceAccessToken.get(key);
    }

    public abstract void sendMessage(Message message);
    public abstract String getJson();
}
