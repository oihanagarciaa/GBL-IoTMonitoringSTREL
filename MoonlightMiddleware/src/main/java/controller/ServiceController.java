package controller;

import services.Service;

//TODO:
public class ServiceController implements Controller{

    public ServiceController(){

    }

    @Override
    public void initializeService(Service service) {
        service.init();
        if(service.isRunning()){
            service.run();
        }
    }

    @Override
    public void establishConnection(ConnType connType, String broker) {
        if(connType == ConnType.MQTT){

        }else if (connType == ConnType.REST){

        }else
            throw new UnsupportedOperationException("Unknown connection type");
    }

    @Override
    public void runForever() {

    }
}
