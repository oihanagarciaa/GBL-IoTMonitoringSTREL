package messages;

import eu.quanticol.moonlight.core.base.Tuple;
import eu.quanticol.moonlight.core.base.TupleType;

public class OfficeSensorMessage implements Message, CommonSensorsMessage<Tuple>{
    private int id;
    private double time;
    private double temp;
    private double hum;
    private int co2;
    private int tvoc;


    @Override
    public int getId() {
        return id;
    }

    @Override
    public double getTime() {
        return time;
    }

    @Override
    public Tuple getValue() {
        TupleType tupleType = TupleType.of
                (Double.class, Double.class, Integer.class, Integer.class);

        return Tuple.of(tupleType, temp, hum, co2, tvoc);
    }
}