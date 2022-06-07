package main;

public class Settings {
    private static final String THINGSBOARD_BROKER = "tcp://stefanschupp.de:1884";
    private static final String THINGSBOARD_TOPIC = "v1/board/runner";
    
    private Settings() {}   // Hidden from the external
    

    public static String getThingsboardBroker() {
        return THINGSBOARD_BROKER;
    }

    public static String getThingsboardTopic() {
        return THINGSBOARD_TOPIC;
    }
}
