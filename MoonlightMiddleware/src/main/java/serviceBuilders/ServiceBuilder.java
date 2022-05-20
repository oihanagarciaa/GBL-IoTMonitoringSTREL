package serviceBuilders;

import services.Service;

public interface ServiceBuilder {

    void initializeService();
    boolean run();
    Service getService();
}
