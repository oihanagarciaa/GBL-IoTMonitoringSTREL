package services;

/**
 * Handler for a generic service,
 * this should be an Abstract Factory for services,
 * calling specific setup algorithms depending on the kind of service.
 *
 * TODO: Moonlight stuff should be somewhere else, not general
 *
 * @see Service
 */
public class ServiceHandler {
    private final Service service;

    public ServiceHandler(ServiceType type, Service service) {
        if(type == ServiceType.MOONLIGHT) {
            //service = null; //todo: should be MoonlightService
            this.service = service;
            service.init();
        } else
            throw new UnsupportedOperationException("Unknown service passed");

    }

    public void startService() {
        service.run();
    }

    public void endService() {
        service.stop();
    }

}
