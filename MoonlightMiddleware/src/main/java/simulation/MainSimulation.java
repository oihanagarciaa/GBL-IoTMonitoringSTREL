package simulation;

import connection.ConnType;
import main.DataBus;
import service_builders.RunnerServiceBuilder;
import service_builders.ServiceBuilder;

import java.util.HashMap;
import java.util.Map;

public class MainSimulation {
    private final Map<String, ServiceBuilder> services;
    private RunnerServiceBuilder runnerServiceBuilder;

    public MainSimulation(){
        services = new HashMap<>();
        setMessageReaderService();
        setRunnerServiceBuilder();
    }

    private void setMessageReaderService() {
        DataBusMessageReaderService messageReaderService = new DataBusMessageReaderService();
        DataBus.getInstance().subscribe(messageReaderService);
    }

    private void setRunnerServiceBuilder() {
        runnerServiceBuilder = new RunnerServiceBuilder(ConnType.SIMULATION,
                "","", "", "", services);
        runnerServiceBuilder.run();
    }

    public static void main(String[] args) {
        new MainSimulation();
    }
}
