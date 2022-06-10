package main;

import messages.OfficeSensorMessage;

public class Settings {
    private static final String SETTINGS_BROKER = "tcp://stefanschupp.de:1883";
    private static final String SETTINGS_TOPIC = "v1/board/runner";
    private static final String SETTINGS_USERNAME = "oihana";
    private static final String SETTINGS_PASSWORD = "22oihana22";

    private static final Class receivingMessage = OfficeSensorMessage.class;

    private static final Integer bufferSize = 6;

    private Settings() {}   // Hidden from the external
    

    public static String getSettingsBroker() {
        return SETTINGS_BROKER;
    }

    public static String getSettingsTopic() {
        return SETTINGS_TOPIC;
    }

    public static String getSettingsUsername() {
        return SETTINGS_USERNAME;
    }

    public static String getSettingsPassword() {
        return SETTINGS_PASSWORD;
    }

    public static Class getReceivingMessage() {
        return receivingMessage;
    }

    public static Integer getBufferSize(){
        return bufferSize;
    }
}
