package main;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

public class Dummy {

    public static void main(String[] args) {

        String clientId = "Client1";
        int qos             = 0;
        String broker       = "tcp://localhost:1883";
        String topic     = "institute/thingy/";
        MemoryPersistence persistence = new MemoryPersistence();

        try {
            MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            System.out.println("Connecting to broker: "+broker);
            sampleClient.connect(connOpts);
            System.out.println("Connected");
            //System.out.println("Publishing message: "+content);
            //MqttMessage message = new MqttMessage(content.getBytes());
            //message.setQos(qos);

            final SecureRandom rand = new SecureRandom();
            try{

                int i, n, p;
                do {
                    i = rand.nextInt(6);
                    n = 50+rand.nextInt(30);
                    p = 20+rand.nextInt(10);
                    String s = "{\n" +
                            "            \"id\": "+i+"\n" +
                            "            \"place\":"+ i+"\n" +
                            "            \"noise\":"+ n+"\n" +
                            "            \"people\":"+ p+"\n" +
                            "         }";

                    sampleClient.publish(topic+i,new MqttMessage(s.getBytes()));
                    Thread.sleep(1000);
                }while (true);
            }catch(InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            System.out.println("Message published");
            sampleClient.disconnect();
            System.out.println("Disconnected");
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
