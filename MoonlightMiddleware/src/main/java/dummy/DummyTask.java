package dummy;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.security.SecureRandom;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class DummyTask implements Runnable{
    private int id;
    private String clientId = UUID.randomUUID().toString();
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
                double hum;
                int co2;
                int tvoc;
                int cont;
                final SecureRandom rand = new SecureRandom();
                do {
                    temp = 25+ rand.nextDouble(8);
                    hum = 10+ rand.nextDouble(40);
                    co2 = 600+ rand.nextInt(100);
                    tvoc = rand.nextInt(400);
                    String s = "{ 'id':"+id+",\n" +
                            "   'time':"+dummyCommonValues.addAndGetTime()+",\n" +
                            "   'temp':"+temp+",\n" +
                            "   'hum': "+hum+",\n" +
                            "   'co2': "+co2+",\n" +
                            "   'tvoc':"+tvoc+"\n" +
                            "   }";
                    sampleClient.publish(topic+id,new MqttMessage(s.getBytes()));
                    cont = dummyCommonValues.addAndGetCont();
                    try {
                        TimeUnit.SECONDS.sleep(5);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }while (cont <= 1000);

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
