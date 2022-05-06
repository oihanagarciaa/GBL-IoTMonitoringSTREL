package serviceBuilders;

import services.ResultThingsboardService;
import services.Service;

import java.util.Map;

public class ResultsThingsboardServiceBuilder {
    private Service service;
    private final Map<String, String> deviceAccessToken;

    public ResultsThingsboardServiceBuilder(Map<String, String> deviceAccessToken){
        this.deviceAccessToken = deviceAccessToken;
    }

    private void initializeService() {
        service = new ResultThingsboardService(deviceAccessToken);
        service.init();
    }

    public boolean run() {
        try {
            initializeService();
            return true;
        } catch (UnsupportedOperationException e) {
            return false;
        }
    }

    public Service getService() {
        return service;
    }
}
