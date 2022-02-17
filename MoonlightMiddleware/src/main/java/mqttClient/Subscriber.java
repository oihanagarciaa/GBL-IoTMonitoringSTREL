package mqttClient;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.Scanner;

public class Subscriber implements MqttCallback {
    /** The broker url. */
    private static final String brokerUrl ="tcp://broker.mqttdashboard.com:1883";

    /** The client id. */
    private static final String clientId = "MyClientID2";

    /** The topic. */
    private static final String topic = "ANerwTestTopic123456789";

    public static void main(String[] args) {

        System.out.println("Subscriber running");

        Scanner teclado = new Scanner(System.in);
        Subscriber suscriptor = new Subscriber();

        Thread hilo = new Thread(new Runnable() {

            @Override
            public void run() {
                teclado.nextLine();
                synchronized(suscriptor) {
                    suscriptor.notify();
                }
            }


        });
        hilo.start();
        suscriptor.subscribe(topic);
    }

    public void subscribe(String topic) {
        //	logger file name and pattern to log
        MemoryPersistence persistence = new MemoryPersistence();

        try
        {

            MqttClient sampleClient = new MqttClient(brokerUrl, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);

            System.out.println("checking");
            System.out.println("Mqtt Connecting to broker: " + brokerUrl);

            sampleClient.connect(connOpts);
            System.out.println("Mqtt Connected");

            sampleClient.setCallback(this);
            sampleClient.subscribe(topic);

            System.out.println("Subscribed");
            System.out.println("Listening");

        } catch (MqttException me) {
            System.out.println(me);
        }
    }

    //Called when the client lost the connection to the broker
    public void connectionLost(Throwable arg0) {

    }

    //Called when a outgoing publish is complete
    public void deliveryComplete(IMqttDeliveryToken arg0) {

    }

    public void messageArrived(String topic, MqttMessage message) throws Exception {

        System.out.println("| Topic:" + topic);
        System.out.println("| Message: " +message.toString());
        System.out.println("-------------------------------------------------");

    }
}