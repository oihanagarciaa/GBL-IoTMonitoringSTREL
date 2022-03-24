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

//    /**
//     * Runs the service
//     * @param update New values as an Update class
//     * @deprecated use receive instead
//     */
//    @Deprecated
//    void run(Update<Double, List<E>> update);

    /**
     * The DataBus calls this method to notify when a new message has arrived
     * @param message
     */
    void receive(Message message);

//    /**
//     * Runs the service
//     * @param timeChain New values as a TimeChain class
//     * @deprecated use receive instead
//     */
//    @Deprecated
//    void run(TimeChain<Double, List<E>> timeChain);

    /**
     * Setup the basics for the service
     */
    void init();

    /**
     * The service will stop functioning afterwords.
     */
    void stop();

//    /**
//     * Get the results
//     * @return Gets the result of the monitor
//     * @deprecated use DataBus instead
//     */
//    @Deprecated
//    T getResponseFromService();
}
