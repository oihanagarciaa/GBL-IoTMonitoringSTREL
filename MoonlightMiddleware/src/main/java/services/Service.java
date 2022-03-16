package services;

import eu.quanticol.moonlight.signal.online.TimeChain;
import eu.quanticol.moonlight.signal.online.Update;

import java.util.List;

/**
 * Interface to implement for Moonlight Middleware services
 */
public interface Service<E, T> {

    /**
     * @return true if it is running
     */
    boolean isRunning();

    /**
     * Runs the service
     * @param update New values as an Update class
     */
    void run(Update<Double, List<E>> update);

    /**
     * Runs the service
     * @param timeChain New values as a TimeChain class
     */
    void run(TimeChain<Double, List<E>> timeChain);

    /**
     * Setup the basics for the service
     */
    void init();

    /**
     * The service will stop functioning afterwords.
     */
    void stop();

    /**
     * Get the results
     * @return Gets the result of the monitor
     */
    T getResponseFromService();
}
