package services.service_info;

public class ConnectionSettings {
    private String broker;
    private String topic;
    private String username;
    private String password;

    public String getBroker() {
        return broker;
    }

    public String getTopic() {
        return topic;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
