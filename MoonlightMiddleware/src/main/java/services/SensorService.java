package services;

import com.google.gson.Gson;
import main.DataBus;
import messages.CommonSensorsMessage;
import messages.Message;
import subscriber.MessageListener;
import subscriber.Subscriber;

import java.lang.reflect.Type;

public class SensorService implements Service, MessageListener {
    private Subscriber subscriber;
    Class messageClass;

    public SensorService(Subscriber subscriber, Class messageClass){
        this.subscriber = subscriber;
        this.messageClass = messageClass;
    }

    @Override
    public boolean isRunning() {
        return subscriber!=null;
    }

    @Override
    public void receive(Message message) {
        //Do nothing
    }

    @Override
    public void init() {
        this.subscriber.addListener(this);
    }

    @Override
    public void stop() {

    }

    long startingTime = 0;
    long endingTime;
    @Override
    public void messageArrived(String topic, String jsonMessage) {
        Message message = new Gson().fromJson(jsonMessage, (Type) messageClass);
        CommonSensorsMessage m1 = (CommonSensorsMessage) message;
        if(startingTime == 0) startingTime = System.currentTimeMillis();
        else if(m1.getTime() == 10000) {
            endingTime = System.currentTimeMillis();
            float duration = (float) ((endingTime - startingTime) / 1000.0);
            System.out.println("DURATION: "+duration);
        }
        DataBus dataBus = DataBus.getInstance();
        dataBus.offer(message);
    }
}
