package services.service_info;

import java.util.Locale;

public class ConnectionInfo {
    private String type;
    private ConnectionSettings settings;

    public String getType() {
        return type.toUpperCase(Locale.ROOT);
    }

    public ConnectionSettings getSettings() {
        return settings;
    }
}
