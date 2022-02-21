package dataconverter;

/**
 * Interface to convert the received message into the desired data
 * @param <U> Monitoring data, update
 * @param <M> The received message type
 */
public interface DataConverter<U, M> {

    /**
     *
     * @return
     */
    U getDataToMonitor();

    /**
     *
     * @param message The message arrived from the broker
     * @return (Update or just one moonlight record??)
     */
    U fromMessageToMonitorData(M message);
}
