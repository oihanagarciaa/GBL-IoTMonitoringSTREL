package services;

import messages.Message;
import subscriber.ConnType;
import subscriber.MQTTSubscriber;
import subscriber.Subscriber;

public class SensorService implements Service{
    private Subscriber<String> subscriber;

    public SensorService(){

    }

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public void receive(Message message) {

    }

    @Override
    public void init() {

    }

    @Override
    public void stop() {

    }

    private void establishConnection() throws UnsupportedOperationException {
        if (connectionType == ConnType.MQTT) {
            //TODO: create service
            subscriber = new MQTTSubscriber(broker, this);
            subscriber.subscribe(topic);
        } else if (connectionType == ConnType.REST) {
            throw new UnsupportedOperationException("Rest not implemented");
        } else
            throw new UnsupportedOperationException("Unknown connection type");
    }
}
