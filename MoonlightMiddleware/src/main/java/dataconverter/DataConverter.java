package dataconverter;

/**
 * Interface to convert the received message into the desired data
 * @param <U> Monitoring data, update
 * @param <M> The received message type
 */
public interface DataConverter<U, M> {

    /**
     *
     * @param message The message arrived from the broker
     * @return (Update or just one moonlight record??)
     */
    U fromMessageToMonitorData(int id, M message);

    void initDataConverter(int size);
}
