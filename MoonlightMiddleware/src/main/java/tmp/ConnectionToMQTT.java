package tmp;


import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

public class ConnectionToMQTT {

    public static void main(String[] args){
        String topic        = "v1/devices/me/telemetry";
        int qos             = 0;
        String broker       = "tcp://thingsboard.cloud:1883";
        String clientId     = "JavaSample1234";
        String username     = "T6Tn0xfSJKolnUfxmZFr";
        String password     = "";
        MemoryPersistence persistence = new MemoryPersistence();

        try {
            MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setUserName(username);
            connOpts.setPassword(password.toCharArray());
            System.out.println("Connecting to broker: "+broker);
            sampleClient.connect(connOpts);
            System.out.println("Connected");
            //System.out.println("Publishing message: "+content);
            int i = 1;
            final SecureRandom rand = new SecureRandom();
            double temp = 0;
            int co2 = 0;
            while(i == 1) {
                temp = 20+ rand.nextDouble(15);
                co2 = 300 + rand.nextInt(300);
                String content = "{'id':1,'time':123456.0,'temp': "+temp+",'hum':15.3, 'co2':"+co2+", 'tvoc': 45}";
                MqttMessage message = new MqttMessage(content.getBytes());
                message.setQos(qos);
                sampleClient.publish(topic, message);
                TimeUnit.SECONDS.sleep(3);
            }
            System.out.println("Message published");
            sampleClient.disconnect();
            System.out.println("Disconnected");
            System.exit(0);
        } catch(MqttException | InterruptedException me) {
            //System.out.println("reason "+me.getReasonCode());
            System.out.println("msg "+me.getMessage());
            System.out.println("loc "+me.getLocalizedMessage());
            System.out.println("cause "+me.getCause());
            System.out.println("excep "+me);
            me.printStackTrace();
        }
    }
}
