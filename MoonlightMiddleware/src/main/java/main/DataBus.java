package main;

import messages.Message;
import services.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Singleton data bus class
 */
public class DataBus {
    private static DataBus instance;

    private final List<Service> services;

    private DataBus() {
        services = new ArrayList<>();
    }

    synchronized public static DataBus getInstance() {
        if(instance == null) {
            instance = new DataBus();
        }

        return instance;
    }

    synchronized public void offer(Message m) {
        instance.services.forEach(s -> s.receive(m));
    }

    synchronized public void subscribe(Service s) {
        instance.services.add(s);
    }

    synchronized public void unsubscribe(Service s) {
        instance.services.remove(s);
    }

}
