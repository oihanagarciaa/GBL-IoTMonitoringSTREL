package service_builders;

import connection.MQTTSubscriber;
import connection.Subscriber;
import main.Settings;
import services.RunnerService;
import services.Service;

import java.util.Map;

public class RunnerServiceBuilder implements ServiceBuilder {
    private Service service;
    private final Map<String, ServiceBuilder> serviceBuilders;

    public RunnerServiceBuilder(Map<String, ServiceBuilder> services) {
        this.serviceBuilders = services;
    }

    @Override
    public void initializeService() {
        Subscriber<String> subscriber = new MQTTSubscriber(Settings.getSettingsBroker(),
                Settings.getSettingsTopic(), Settings.getSettingsUsername(),
                Settings.getSettingsPassword());
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
