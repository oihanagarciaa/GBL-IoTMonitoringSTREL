package services;

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
     * @param u
     */
    void updateService(U u);

    /**
     * Get the results
     * @return
     */
    T getResponseFromService();
}
