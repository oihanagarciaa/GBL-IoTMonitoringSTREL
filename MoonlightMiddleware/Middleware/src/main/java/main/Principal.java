package main;

import controller.Controller;
import controller.MQTT_handler;

public class Principal {
    static final int size = 6;

    public static void main(String[] args) {
        Controller controller = new Controller();
        MQTT_handler mqtt_handler = new MQTT_handler(size, controller);
    }
}
