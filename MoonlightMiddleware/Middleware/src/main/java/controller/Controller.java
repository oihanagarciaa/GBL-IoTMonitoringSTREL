package controller;

import view.Screen;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Controller implements ActionListener {
    Screen view;
    MQTT_handler mqtt_handler;

    public Controller(Screen view, MQTT_handler mqttHandler){
        this.view = view;
        this.view.addJButtonListener(this);
        this.mqtt_handler = mqttHandler;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "Subscribe":
                mqtt_handler.subscribe();
                view.setRecievedMessage("");
                break;
            case "Send message":
                mqtt_handler.publishMessage(view.getSendingMessage());
                break;
            default:
                break;
        }
    }
}
