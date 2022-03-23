package builders;

import services.SensorService;
import services.Service;
import subscriber.ConnType;

public class SensorsServiceBuilder {
    private Service service;
    private String broker;
    private String topic;
    private ConnType connectionType;

    public SensorsServiceBuilder(ConnType connection,
                                 String broker,
                                 String topic) {
        this.connectionType = connection;
        this.broker = broker;
        this.topic = topic;
    }

    public void initializeService() {
        if(connectionType == ConnType.MQTT) {
            service = new SensorService();
        } else {
            throw new UnsupportedOperationException("Not supported monitor type");
        }
        service.init();
    }

    public boolean run() {
        try {
            new SensorService();
            return true;
        } catch (UnsupportedOperationException e) {
            return false;
        }
    }
}
