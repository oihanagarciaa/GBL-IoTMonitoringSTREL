package controller;

import dataconverter.DataConverter;
import dataconverter.OnlineMoonlightDataConverter;
import eu.quanticol.moonlight.io.MoonLightRecord;
import eu.quanticol.moonlight.signal.online.Update;
import messages.Message;
import services.OnlineMoonlightService;
import services.Service;
import services.ServiceHandler;
import services.ServiceType;
import subscriber.MQTTSubscriber;
import subscriber.Subscriber;

import java.util.List;

/**
 * Main
 */
public class ServiceController implements Controller{
    ServiceHandler<Update<Double, List<MoonLightRecord>>, Message> serviceHandler;
    Subscriber<Message> subscriber;
    DataConverter<Update<Double, List<MoonLightRecord>>, Message> dataConverter;
    String broker = "tcp://broker.mqttdashboard.com:1883";
    Service service;

    public ServiceController(){
        initializeService(new OnlineMoonlightService());
        establishConnection(ConnType.MQTT, broker);
        subscriber.subscribe("iot/sensors");
        dataConverter = new OnlineMoonlightDataConverter();
    }

    @Override
    public void initializeService(Service service) {
        //serviceHandler = new ServiceHandler(service);
        this.service = service;
        this.service.init();
    }

    @Override
    public void establishConnection(ConnType connType, String broker) {
        if(connType == ConnType.MQTT){
            //TODO: create a subscriber. One subscriber for each protocol?
            subscriber = new MQTTSubscriber(broker, this);
        }else if (connType == ConnType.REST){
            throw new UnsupportedOperationException("Rest not implemented");
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
        throw new UnsupportedOperationException("This is not implemented yet");
    }

    @Override
    public void updateData(Message message) {
        service.askService(dataConverter.fromMessageToMonitorData(message));
        service.run();
        service.getResponseFromService();
    }
}
