package services;

import java.util.List;

/**
 * Interface to implement for Moonlight Middleware services
 */
public interface Service<U, T> {

    /**
     * @return true if it is running
     */
    boolean isRunning();

    /**
     * Runs the service
     */
    void run();

    /**
     * Setup the basics for the service
     */
    void init();

    /**
     * The service will stop functioning afterwords.
     */
    void stop();

    /**
     * Pass the update
     * @param u Update, new collected data
     */
    void updateService(U u);

    /**
     * Pass a set of updates
     * @param updates new collected data
     */
    void updateService(List<U> updates);

    /**
     * Get the results
     * @return Gets the result of the monitor
     */
    T getResponseFromService();
}
