package main;

import connection.ConnType;
import eu.quanticol.moonlight.core.base.Pair;
import eu.quanticol.moonlight.core.space.SpatialModel;
import eu.quanticol.moonlight.util.Utils;
import messages.OfficeSensorMessage;

import java.util.HashMap;

public class Settings {
    private static final ConnType CONN_TYPE = ConnType.MQTT;
    private static final String SETTINGS_BROKER = "tcp://stefanschupp.de:1883";
    private static final String SETTINGS_TOPIC = "v1/board/runner";
    private static final String SETTINGS_USERNAME = "oihana";
    private static final String SETTINGS_PASSWORD = "22oihana22";

    private static final Class receivingMessage = OfficeSensorMessage.class;

    private static final Integer bufferSize = 6;

    private Settings() {}   // Hidden from the external
    
    public static ConnType getConnType(){
        return CONN_TYPE;
    }

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

    public static SpatialModel<Double> buildSpatialModel(int size){
        HashMap<Pair<Integer, Integer>, Double> cityMap = new HashMap<>();
        cityMap.put(new Pair<>(0, 1), 11.0);
        cityMap.put(new Pair<>(1, 0), 11.0);
        cityMap.put(new Pair<>(1, 2), 7.0);
        cityMap.put(new Pair<>(2, 1), 7.0);
        cityMap.put(new Pair<>(1, 3), 8.0);
        cityMap.put(new Pair<>(3, 1), 8.0);
        cityMap.put(new Pair<>(2, 3), 8.0);
        cityMap.put(new Pair<>(3, 2), 8.0);
        return Utils.createSpatialModel(size, cityMap);
    }
}
