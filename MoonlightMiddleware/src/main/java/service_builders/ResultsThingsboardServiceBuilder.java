package service_builders;

import connection.ConnType;
import services.ResultThingsboardService;
import services.Service;

import java.util.Map;

public class ResultsThingsboardServiceBuilder implements ServiceBuilder{
    private final ConnType connType;
    private Service service;
    private final String broker;
    private final String topic;
    private final Map<String, String> deviceAccessToken;

    public ResultsThingsboardServiceBuilder(ConnType connType, String broker, String topic, Map<String, String> deviceAccessToken){
        this.connType = connType;
        this.broker = broker;
        this.topic = topic;
        this.deviceAccessToken = deviceAccessToken;
    }

    @Override
    public void initializeService() {
        service = new ResultThingsboardService(connType, broker, topic, deviceAccessToken);
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
