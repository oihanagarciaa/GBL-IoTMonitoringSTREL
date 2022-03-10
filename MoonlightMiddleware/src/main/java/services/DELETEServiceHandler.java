package services;

//TODO: this doesn't do nothing for the moment. Maybe delete
/**
 * Handler for a generic service,
 * this should be an Abstract Factory for services,
 * calling specific setup algorithms depending on the kind of service.
 *
 * TODO: Moonlight stuff should be somewhere else, not general
 *
 * @see Service
 */
public class DELETEServiceHandler<U, T> {
    private final Service<U, T> service;

    public DELETEServiceHandler(Service<U, T> service) {
        //service = null; //todo: should be MoonlightService
        this.service = service;
        service.init();
    }

    //Theads

    public void startService() {
        //service.run();
    }

    public void endService() {
        service.stop();
    }

    //TODO: should I add getResults and setUpdate?

}
