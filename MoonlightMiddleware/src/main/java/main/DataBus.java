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

    public static DataBus getInstance() {
        if(instance == null) {
            instance = new DataBus();
        }

        return instance;
    }

    public void offer(Message m) {
        instance.services.forEach(s -> s.receive(m));
    }

    public void notify(Service s) {
        instance.services.add(s);
    }

}
