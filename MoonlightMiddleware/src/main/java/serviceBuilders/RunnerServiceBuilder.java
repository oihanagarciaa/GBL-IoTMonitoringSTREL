package serviceBuilders;

import connection.MQTTSubscriber;
import connection.Subscriber;
import main.Settings;
import services.RunnerService;
import services.Service;

import java.util.List;

public class RunnerServiceBuilder implements ServiceBuilder {
    private Service service;
    private final List<ServiceBuilder> serviceBuilders;

    public RunnerServiceBuilder(List<ServiceBuilder> services) {
        this.serviceBuilders = services;
    }

    @Override
    public void initializeService() {
        Subscriber subscriber = new MQTTSubscriber(Settings.getThingsboardBroker(),
                Settings.getThingsboardTopic(), "", "");
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
