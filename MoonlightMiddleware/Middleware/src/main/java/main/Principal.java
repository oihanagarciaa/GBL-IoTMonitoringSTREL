package main;

import controller.Controller;
import controller.MQTT_handler;
import view.Screen;

public class Principal {
    static final int size = 6;

    public static void main(String[] args) {
        Screen view = new Screen();
        MQTT_handler mqtt_handler = new MQTT_handler(view, size);

        Controller controller = new Controller(view, mqtt_handler);
    }
}
