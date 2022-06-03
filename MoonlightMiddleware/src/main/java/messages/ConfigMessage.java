package messages;

import services.serviceInfo.ServiceInfo;

import java.util.ArrayList;
import java.util.List;

public class ConfigMessage implements Message{
    private List<ServiceInfo> services = new ArrayList<>();

    public List<ServiceInfo> getServiceInfo() {
        return services;
    }
}
