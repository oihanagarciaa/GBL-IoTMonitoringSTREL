package controller;

import services.Service;
import services.ServiceHandler;
import services.ServiceType;


public class ServiceController implements Controller{
    ServiceHandler serviceHandler;

    public ServiceController(){

    }

    @Override
    public void initializeService(Service service) {
        //TODO: how do I know the service type?
        serviceHandler = new ServiceHandler(ServiceType.MOONLIGHT, service);
    }

    @Override
    public void establishConnection(ConnType connType, String broker) {
        if(connType == ConnType.MQTT){
            //TODO: create a subscriber. One subscriber for each protocol?
            /*
            MQTTSubscriber subscriber = new MQTTSubscriber();
             */
        }else if (connType == ConnType.REST){

        }else
            throw new UnsupportedOperationException("Unknown connection type");
    }

    @Override
    public void runForever() {
        //TODO: run forever
        /*
        What does this do?
        ...
        1. getmessage?
        2. convert the data
        3. send update to the service
        4. serviceHandler.startService();
        5. serviceHandler.getResults(); (?)
         */
    }
}
