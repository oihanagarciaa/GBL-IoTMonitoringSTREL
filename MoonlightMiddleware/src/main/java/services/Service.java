package services;

import messages.Message;

/**
 * Interface to implement services
 */
public interface Service {

    /**
     * @return true if it is running
     */
    boolean isRunning();

    /**
     * The DataBus calls this method to notify when a new message has arrived
     * @param message
     */
    void receive(Message message);

    /**
     * Setup the basics for the service
     */
    void init();

    /**
     * The service will stop functioning afterwords.
     */
    void stop();
}
