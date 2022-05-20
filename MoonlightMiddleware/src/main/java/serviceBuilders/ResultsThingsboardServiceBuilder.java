package serviceBuilders;

import services.ResultThingsboardService;
import services.Service;

import java.util.Map;

public class ResultsThingsboardServiceBuilder implements ServiceBuilder{
    private Service service;
    private final Map<String, String> deviceAccessToken;

    public ResultsThingsboardServiceBuilder(Map<String, String> deviceAccessToken){
        this.deviceAccessToken = deviceAccessToken;
    }

    @Override
    public void initializeService() {
        service = new ResultThingsboardService(deviceAccessToken);
        service.init();
    }

    @Override
    public boolean run() {
        try {
            initializeService();
            return true;
        } catch (UnsupportedOperationException e) {
            return false;
        }
    }

    @Override
    public Service getService() {
        return service;
    }
}
