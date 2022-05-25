package services;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
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

    @Override
    public void messageArrived(String topic, String jsonMessage) {
        System.out.println("MESSAGE: "+jsonMessage);
        try{
            Message message = new Gson().fromJson(jsonMessage, (Type) messageClass);
            DataBus dataBus = DataBus.getInstance();
            dataBus.offer(message);
        }catch (JsonSyntaxException e){
            //Ignore
        }
    }
}
