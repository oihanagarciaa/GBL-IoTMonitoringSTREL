package serviceBuilders;

import services.Service;

import java.util.List;

public class RunnerServiceBuilder implements ServiceBuilder {
    public RunnerServiceBuilder(List<ServiceBuilder> services) {
        
    }

    @Override
    public void initializeService() {
        
    }

    @Override
    public boolean run() {
        return false;
    }

    @Override
    public Service getService() {
        return null;
    }
}
