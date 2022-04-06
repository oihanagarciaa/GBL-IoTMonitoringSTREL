package messages;

import eu.quanticol.moonlight.core.base.Tuple;

public class SensorsMessage implements Message, CommonSensorsMessage<Tuple>{
    int id;
    double time;
    Tuple value;
    public SensorsMessage(String message){
        /*String[] values = message.split(";");
        String[] brokers = values[0].split("/");
        this.id = Integer.parseInt(brokers[brokers.length - 1]);
        this.time = */
    }

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public double getTime() {
        return 0;
    }

    @Override
    public Tuple getValue() {
        return null;
    }
}
