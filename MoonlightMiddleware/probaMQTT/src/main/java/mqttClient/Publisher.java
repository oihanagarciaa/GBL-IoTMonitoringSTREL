package mqttClient;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;


public class Publisher {
    static String mqtt_server = "broker.mqttdashboard.com";
    static String client_ID = "UnClienteID";

    public static void main(String[] args) {

        //Topic name
        String topic        = "ANerwTestTopic123456789";
        //data to be send
        String content      = "Temp:20,Humi:70";
        int qos             = 0;

        String broker       = "tcp://broker.mqttdashboard.com:1883";
        String clientId     = "MyClientID";
        MemoryPersistence persistence = new MemoryPersistence();

        try {
            MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            System.out.println("Connecting to broker: "+broker);
            sampleClient.connect(connOpts);
            System.out.println("Connected to broker");
            System.out.println("Publishing message:"+content);
            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(qos);
            sampleClient.publish(topic, message);
            System.out.println("Message published");
            sampleClient.disconnect();
            sampleClient.close();
            System.exit(0);
        } catch(MqttException me) {
            System.out.println("reason "+me.getReasonCode());
            System.out.println("msg "+me.getMessage());
            System.out.println("loc "+me.getLocalizedMessage());
            System.out.println("cause "+me.getCause());
            System.out.println("excep "+me);
            me.printStackTrace();
        }
    }
}
