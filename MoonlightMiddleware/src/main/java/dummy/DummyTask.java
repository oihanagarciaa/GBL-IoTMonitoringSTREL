package dummy;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.security.SecureRandom;
import java.util.UUID;

public class DummyTask implements Runnable{
    private int id;
    private String clientId = UUID.randomUUID().toString();
    private int qos         = 0;
    private String username = "oihana";
    private String password = "22oihana22";
    private String broker   = "tcp://stefanschupp.de:1883";
    private String topic    = "institute/thingy/";
    private DummyCommonValues dummyCommonValues;

    public DummyTask(int id, DummyCommonValues dummyCommonValues){
        this.id = id;
        this.dummyCommonValues = dummyCommonValues;
    }

    @Override
    public void run() {
        try (MemoryPersistence persistence = new MemoryPersistence()){
            try(MqttClient sampleClient = new MqttClient(broker, clientId, persistence)) {
                MqttConnectOptions connOpts = new MqttConnectOptions();
                connOpts.setCleanSession(true);
                connOpts.setUserName(username);
                connOpts.setPassword(password.toCharArray());
                System.out.println("Connecting to broker: "+broker);
                sampleClient.connect(connOpts);
                System.out.println("Connected");
                double temp;
                int cont;
                final SecureRandom rand = new SecureRandom();
                do {
                    temp = 20+ rand.nextDouble(15);

                    String s = "{ 'id':"+id+",\n" +
                            "   'time':"+dummyCommonValues.addAndGetTime()+",\n" +
                            "   'temp':"+temp+",\n" +
                            "   'hum': "+temp+",\n" +
                            "   'co2': "+3+",\n" +
                            "   'tvoc':"+3+"\n" +
                            "   }";

                    sampleClient.publish(topic+id,new MqttMessage(s.getBytes()));
                    cont = dummyCommonValues.addAndGetCont();
                }while (cont <= 10000);

                sampleClient.disconnect();
                System.out.println("Disconnected");
            }

        } catch(MqttException me) {
            System.out.println("reason "+me.getReasonCode());
            System.out.println("msg "+me.getMessage());
            System.out.println("loc "+me.getLocalizedMessage());
            System.out.println("cause "+me.getCause());
            System.out.println("excep "+me);
        }
    }
}
