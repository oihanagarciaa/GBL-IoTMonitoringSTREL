package thingsBoard;

import com.google.gson.Gson;
import eu.quanticol.moonlight.core.base.Tuple;
import messages.CommonSensorsMessage;
import messages.Message;

import java.util.Map;

public class ThingsboardSensorsCommunication extends ThingsboardCommunication{
    private final Map<String, String> deviceAccessToken;

    public ThingsboardSensorsCommunication(Map<String, String> deviceAccessToken){
        this.deviceAccessToken = deviceAccessToken;
    }

    @Override
    public String getJson(Message message) {
        CommonSensorsMessage commonSensorsMessage = (CommonSensorsMessage) message;
        Tuple tuple = (Tuple) commonSensorsMessage.getValue();
        ThingsboardMessage myMessage = new ThingsboardMessage();
        myMessage.setTemperature(tuple.getIthValue(0));
        myMessage.setHumidity(tuple.getIthValue(1));
        myMessage.setCo2(tuple.getIthValue(2));
        myMessage.setTvoc(tuple.getIthValue(3));
        String jsonMessage = new Gson().toJson(myMessage);
        return jsonMessage;
    }

    @Override
    public String getUsername(Message message) {
        CommonSensorsMessage commonSensorsMessage = (CommonSensorsMessage) message;
        int num = commonSensorsMessage.getId()+1;
        String username = deviceAccessToken.get("Thingy"+num);
        return username;
    }

    public class ThingsboardMessage {
        Double temperature;
        Double humidity;
        Integer co2;
        Integer tvoc;

        public Double getTemperature() {
            return temperature;
        }

        public void setTemperature(Double temperature) {
            this.temperature = temperature;
        }

        public Integer getCo2() {
            return co2;
        }

        public void setCo2(Integer co2) {
            this.co2 = co2;
        }

        public Double getHumidity() {
            return humidity;
        }

        public void setHumidity(Double humidity) {
            this.humidity = humidity;
        }

        public Integer getTvoc() {
            return tvoc;
        }

        public void setTvoc(Integer tvoc) {
            this.tvoc = tvoc;
        }
    }
}
