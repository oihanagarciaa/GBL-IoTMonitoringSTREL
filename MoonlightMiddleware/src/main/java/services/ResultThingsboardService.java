package services;

import messages.CommonSensorsMessage;
import messages.Message;
import messages.ResultsMessage;

import java.util.Map;
import java.util.UUID;

public class ResultThingsboardService implements Service{
    private final Map<String, String> deviceAccessToken;

    public ResultThingsboardService(Map<String, String> deviceAccessToken){
        this.deviceAccessToken = deviceAccessToken;
    }

    @Override
    public boolean isRunning() {
        return this != null;
    }

    //TODO: to do
    @Override
    public void receive(Message message) {
        if(message instanceof CommonSensorsMessage){

        }else if(message instanceof ResultsMessage){

        }
    }

    @Override
    public void init() {

    }

    @Override
    public void stop() {

    }
}
