package controller;

import eu.quanticol.moonlight.io.MoonLightRecord;
import eu.quanticol.moonlight.signal.online.*;
import org.apache.commons.lang3.SerializationUtils;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.Serializable;
import java.util.List;

public class MQTT_handler {
    private static final String clientId = "MyIoTClientID2";
    private static final String brokerUrl ="tcp://broker.mqttdashboard.com:1883";
    private static final String topicSubscriber = "iot/sensors";
    private static final String topicPublisher = "iot/moonlight";
    int qos = 0;
    MqttClient sampleClient;
    Subscriber sub;

    public MQTT_handler(int size, Controller controller){
        sub = new Subscriber(this, controller);
        MemoryPersistence persistence = new MemoryPersistence();
        try {
            sampleClient = new MqttClient(brokerUrl, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);

            sampleClient.connect(connOpts);
        } catch (MqttException e) {
            e.printStackTrace();
        }
        subscribe();
    }

    public void subscribe(){
        try {
            sampleClient.setCallback(sub);
            sampleClient.subscribe(topicSubscriber);
            System.out.println("Subscribed");
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /*
    /!\ Update is not serializable, so I can't send it this way
    /!\ Moonlight Record is not serializable, so I can't send it this way
     */
    public void publishUpdate(Update<Double, List<MoonLightRecord>> update){
        try {
            byte[] data = SerializationUtils.serialize((Serializable) update);
            MqttMessage mqttmessage = new MqttMessage(data);
            mqttmessage.setQos(qos);
            sampleClient.publish(topicPublisher, mqttmessage);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /*public void publishSignal(List<Integer> numbers){
        SignalDomain<>
        MultiOnlineSignal multiOnlineSignal = new MultiOnlineSignal();
        MultiOnlineSpaceTimeSignal multiOnlineSpaceTimeSignal = new MultiOnlineSpaceTimeSignal(6, );

        OnlineSignal<Double> onlineSignal
                = new OnlineSignal<Double>(new DoubleDomain());


        TimeChain<Double, List<Integer>> timeChain2 = new TimeChain<Double, List<Integer>>(5.0);

        timeChain2.add(new Segment<>(0, numbers));
        AbstractInterval<Double> abstractInterval = new AbstractInterval<Double>(15.0, 19.1);


        TimeChain<Double, AbstractInterval<Double>> timeChain = new TimeChain<Double, AbstractInterval<Double>>(Double.MAX_VALUE);
        timeChain.add(new Segment<>(2.0, new AbstractInterval<Double>(4.5, 6.4)));
        onlineSignal.refine(timeChain);
    }*/
}
