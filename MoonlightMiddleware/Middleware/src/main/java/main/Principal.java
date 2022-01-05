package main;

import controller.Controller;
import controller.MQTT_handler;
import model.SignalDefinition;
import view.Screen;

public class Principal {
    static final int size = 6;

    public static void main(String[] args) {
        Screen view = new Screen();
        SignalDefinition signalDefinition = new SignalDefinition(size);
        MQTT_handler mqtt_handler = new MQTT_handler(view, signalDefinition, size);

        Controller controller = new Controller(view, mqtt_handler);
    }
}
