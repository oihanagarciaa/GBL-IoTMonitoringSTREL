package service_builders;

import connection.Subscriber;
import messages.CommonSensorsMessage;
import services.SensorService;
import services.Service;
import connection.ConnType;
import connection.MQTTSubscriber;
import simulation.ConnectionSimulations.SensorSubscriberSimulator;

public class SensorsServiceBuilder implements ServiceBuilder{
    private Service service;
    private String broker;
    private String topic;
    private String username;
    private String password;
    private ConnType connectionType;
    private Class<? extends CommonSensorsMessage<?>> messageClass;

    public SensorsServiceBuilder(ConnType connection,
                                 String broker,
                                 String topic,
                                 String username,
                                 String password,
                                 Class<? extends CommonSensorsMessage<?>> messageClass) {
        this.connectionType = connection;
        this.broker = broker;
        this.topic = topic;
        this.username = username;
        this.password = password;
        this.messageClass = messageClass;
    }

    @Override
    public void initializeService() {
        Subscriber subscriber;
        if(connectionType == ConnType.MQTT) {
            subscriber = new MQTTSubscriber(broker, topic, username, password);
        }else if (connectionType == ConnType.REST){
            throw new UnsupportedOperationException("Not supported connection type");
        } else if (connectionType == ConnType.SIMULATION){
            subscriber = new SensorSubscriberSimulator();
        }
        else {
            throw new UnsupportedOperationException("Not supported connection type");
        }
        service = new SensorService(subscriber, messageClass);
        service.init();
    }

    /* TODO:
        it takes the catch as a duplication:
        see Template method or strategy
     */
    @Override
    public boolean run() {
        try {
            initializeService();
            return true;
        } catch (UnsupportedOperationException e) {
            return false;
        }
    }

    @Override
    public Service getService() {
        return service;
    }
}
