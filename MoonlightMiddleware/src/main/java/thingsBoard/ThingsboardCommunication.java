package thingsBoard;

import messages.Message;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.UUID;

public abstract class ThingsboardCommunication {
    private MemoryPersistence persistence;
    final String topic        = "v1/devices/me/telemetry";
    final int qos             = 0;
    final String broker       = "tcp://thingsboard.cloud:1883";
    final String clientId     = UUID.randomUUID().toString();
    final String password     = "";

    ThingsboardCommunication() {}

    public void sendMessageToThingsboard(Message message){
        String username = getUsername(message);
        String json = getJson(message);
        persistence = new MemoryPersistence();
        try {
            MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setUserName(username);
            connOpts.setPassword(password.toCharArray());
            sampleClient.connect(connOpts);

            MqttMessage sendingMessage = new MqttMessage(json.getBytes());
            sendingMessage.setQos(qos);
            sampleClient.publish(topic, sendingMessage);

            sampleClient.disconnect();
        } catch(MqttException me) {
            System.out.println("reason "+me.getReasonCode());
            System.out.println("msg "+me.getMessage());
            System.out.println("loc "+me.getLocalizedMessage());
            System.out.println("cause "+me.getCause());
            System.out.println("excep "+me);
            me.printStackTrace();
        }
    }

    abstract public String getJson(Message message);
    abstract public String getUsername(Message message);
}
