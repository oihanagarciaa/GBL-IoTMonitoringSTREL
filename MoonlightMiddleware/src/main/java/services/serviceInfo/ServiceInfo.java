package services.serviceInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceInfo {
    private String serviceType;
    private String formula;
    private ConnectionInfo connection;
    private List<DeviceInfo> devices;

    public String getServiceType() {
        return serviceType;
    }

    public String getFormula() {
        return formula;
    }

    public ConnectionInfo getConnection() {
        return connection;
    }

    public Map<String, String> getDevices() {
        Map<String, String> devicesAccessToken = new HashMap<>();
        for(DeviceInfo device: devices){
            devicesAccessToken.put(device.getIdentifier(), device.getAccessKey());
        }
        return devicesAccessToken;
    }
}

