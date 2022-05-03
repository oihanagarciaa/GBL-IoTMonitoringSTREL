package subscriber;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MQTTConnection {
    private static MqttClient sampleClient;

    public MQTTConnection(String broker,
                          String username, String password) {
        try(MemoryPersistence persistence = new MemoryPersistence()) {
            sampleClient = new MqttClient(broker, MqttClient.generateClientId(), persistence);
            MqttConnectOptions connOpts = setUpConnectionOptions(username, password);
            sampleClient.connect(connOpts);
        } catch (MqttPersistenceException e) {
            e.printStackTrace();
        } catch (MqttSecurityException e) {
            e.printStackTrace();
        } catch (MqttException exception) {
            exception.printStackTrace();
        }
    }

    private static MqttConnectOptions setUpConnectionOptions(
            String username, String password) {
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        connOpts.setUserName(username);
        connOpts.setPassword(password.toCharArray());
        return connOpts;
    }

    public MqttClient getSampleClient() {
        return sampleClient;
    }

    public void disconnect(){
        try {
            sampleClient.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
