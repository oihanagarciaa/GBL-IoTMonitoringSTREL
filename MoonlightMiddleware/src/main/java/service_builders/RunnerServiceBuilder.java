package service_builders;

import connection.ConnType;
import connection.MQTTSubscriber;
import connection.Subscriber;
import services.RunnerService;
import services.Service;
import simulation.ConnectionSimulations.ClientInterfaceSimulator;

import java.util.Map;

public class RunnerServiceBuilder implements ServiceBuilder {
    private final String broker;
    private final String topic;
    private final String username;
    private final String password;
    private Service service;
    private final Map<String, ServiceBuilder> serviceBuilders;
    private final ConnType connectionType;

    public RunnerServiceBuilder(ConnType connection,
                                String broker, String topic, String username,
                                String password, Map<String, ServiceBuilder> services) {
        this.connectionType = connection;
        this.broker = broker;
        this.topic = topic;
        this.username = username;
        this.password = password;
        this.serviceBuilders = services;
    }

    @Override
    public void initializeService() {
        Subscriber subscriber;
        if(connectionType == ConnType.MQTT) {
            subscriber = new MQTTSubscriber(broker,
                    topic, username, password);
        }else if (connectionType == ConnType.REST){
            throw new UnsupportedOperationException("Not supported connection type");
        }else if (connectionType == ConnType.SIMULATION){
            subscriber = new ClientInterfaceSimulator();
        }
        else {
            throw new UnsupportedOperationException("Not supported connection type");
        }
        service = new RunnerService(subscriber, serviceBuilders);
        service.init();
    }

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
