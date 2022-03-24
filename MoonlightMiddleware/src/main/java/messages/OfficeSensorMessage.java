package messages;

import eu.quanticol.moonlight.core.base.Tuple;
import eu.quanticol.moonlight.core.base.TupleType;

public class OfficeSensorMessage implements Message, CommonSensorsMessage<Tuple>{
    private int id;
    private String place;
    private int noise;
    private int people;
    private double time;

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
                (String.class, Integer.class, Integer.class);

        return Tuple.of(tupleType, place, noise, people);
    }
}