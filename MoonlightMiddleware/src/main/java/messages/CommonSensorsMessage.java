package messages;

public interface CommonSensorsMessage<T> {

    int getId();

    double getTime();

    T getValue();
}
