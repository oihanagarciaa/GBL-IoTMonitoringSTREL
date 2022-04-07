package messages;

public interface CommonSensorsMessage<T> extends Message{

    int getId();

    double getTime();

    T getValue();
}
