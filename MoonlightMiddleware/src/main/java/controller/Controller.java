package controller;

import services.Service;

/**
 *
 */
public interface Controller {

    /**
     *
     * @param service Interface to implement the monitors
     * @see Service
     */
    void initializeService(Service service);

    void establishConnection(ConnType connType, String broker);

    void runForever();
}
