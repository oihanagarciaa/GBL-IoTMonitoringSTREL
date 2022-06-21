package service_builders;

import connection.MQTTSubscriber;
import connection.Subscriber;
import main.Settings;
import services.RunnerService;
import services.Service;

import java.util.Map;

public class RunnerServiceBuilder implements ServiceBuilder {
    private final String broker;
    private final String topic;
    private final String username;
    private final String password;
    private Service service;
    private final Map<String, ServiceBuilder> serviceBuilders;

    public RunnerServiceBuilder(String broker, String topic, String username,
                                String password, Map<String, ServiceBuilder> services) {
        this.broker = broker;
        this.topic = topic;
        this.username = username;
        this.password = password;
        this.serviceBuilders = services;
    }

    @Override
    public void initializeService() {
        Subscriber<String> subscriber = new MQTTSubscriber(broker,
                topic, username, password);
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
