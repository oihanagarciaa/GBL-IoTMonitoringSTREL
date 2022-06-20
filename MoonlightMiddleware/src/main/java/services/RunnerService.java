package services;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import connection.ConnType;
import connection.Subscriber;
import dsl.Specification;
import main.DataBus;
import main.Settings;
import messages.Message;
import messages.ConfigMessage;
import service_builders.MoonlightServiceBuilder;
import service_builders.ResultsThingsboardServiceBuilder;
import service_builders.SensorsServiceBuilder;
import service_builders.ServiceBuilder;
import services.service_info.ConnectionInfo;
import services.service_info.ConnectionSettings;
import services.service_info.ServiceInfo;
import connection.MessageListener;

import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.lang.reflect.Type;
import java.util.Map;

public class RunnerService implements Service, MessageListener {
    private final Subscriber<?> subscriber;
    private final Map<String, ServiceBuilder> serviceBuilders;

    public RunnerService(Subscriber<?> subscriber,
                         Map<String, ServiceBuilder> serviceBuilders){
        this.subscriber = subscriber;
        this.serviceBuilders = serviceBuilders;
    }

    @Override
    public boolean isRunning() {
        return subscriber!=null;
    }
    
    private void processMessage(Message message) {
        if (message instanceof ConfigMessage config) {
            setupNewCommand(config);
        } else {
            throw new UnsupportedOperationException("Unsupported message");
        }
    }

    private void setupNewCommand(ConfigMessage config) {
        for(ServiceInfo serviceInfo: config.getServiceInfo()) {
            executeCommand(serviceInfo);
        }
    }

    private void executeCommand(ServiceInfo serviceInfo) {
        switch (serviceInfo.getCommand()) {
            case "start" -> generateService(serviceInfo);
            case "stop" -> stopService(serviceInfo);
            default -> throw new UnsupportedOperationException("Unknown " +
                    "command");
        }
    }

    private void stopService(ServiceInfo serviceInfo) {
        String serviceId = serviceInfo.getServiceId();
        ServiceBuilder serviceBuilder = serviceBuilders.get(serviceId);
        serviceBuilder.getService().stop();
        serviceBuilders.remove(serviceId);
    }

    private void generateService(ServiceInfo serviceInfo) {
        ServiceBuilder serviceBuilder = switch (serviceInfo.getServiceType()) {
            case "sensors" -> generateSensorsService(serviceInfo);
            case "moonlight" -> generateMoonlightService(serviceInfo);
            case "thingsboard" -> generateThingsboardService(serviceInfo);
            default -> throw new UnsupportedOperationException("Service not recognized");
        };
        startBuilder(serviceInfo.getServiceId(), serviceBuilder);
    }

    private void startBuilder(String serviceID, ServiceBuilder serviceBuilder){
        serviceBuilders.put(serviceID, serviceBuilder);
        serviceBuilder.run();
        DataBus dataBus = DataBus.getInstance();
        dataBus.subscribe(serviceBuilder.getService());
    }

    private ServiceBuilder generateThingsboardService(ServiceInfo info) {
        ResultsThingsboardServiceBuilder thingsboardServiceBuilder;
        String broker = info.getConnection().getSettings().getBroker();
        String topic = info.getConnection().getSettings().getTopic();
        Map<String ,String> deviceAccessTokens = info.getDevices();
        thingsboardServiceBuilder = new ResultsThingsboardServiceBuilder(
                broker, topic, deviceAccessTokens);
        return thingsboardServiceBuilder;
    }

    private MoonlightServiceBuilder generateMoonlightService(ServiceInfo info) {
        MoonlightServiceBuilder moonlightServiceBuilder;
        evaluateFormula(info.getFormula());
        moonlightServiceBuilder =
                new MoonlightServiceBuilder(Specification.spatialModel,
                        Specification.formula,
                Specification.atoms, Specification.distanceFunctions,
                        Settings.getBufferSize());
        return moonlightServiceBuilder;
    }
    
    private SensorsServiceBuilder generateSensorsService(ServiceInfo info) {
        SensorsServiceBuilder sensorsServiceBuilder;
        ConnectionInfo connInfo = info.getConnection();
        ConnectionSettings connSett = connInfo.getSettings();
        sensorsServiceBuilder = new SensorsServiceBuilder(
                ConnType.valueOf(connInfo.getType()), connSett.getBroker(),
                connSett.getTopic(), connSett.getUsername(), connSett.getPassword(),
                Settings.getReceivingMessage());
        return sensorsServiceBuilder;
    }

    private void evaluateFormula(String scriptToEvaluate) throws UnsupportedOperationException{
        var engine = new ScriptEngineManager().getEngineByExtension("kts");
        try {
            engine.eval(scriptToEvaluate);
        } catch (ScriptException e) {
            throw new UnsupportedOperationException("Unable to understand the" +
                    " formula");
        }
    }

    @Override
    public void receive(Message message) {
        processMessage(message);
    }

    @Override
    public void init() {
        this.subscriber.addListener(this);
    }

    @Override
    public void stop() {
        //Stop not handled
    }

    @Override
    public void messageArrived(String topic, String jsonMessage) {
        System.out.println("MESSAGE: "+jsonMessage);
        try{
            Message message = new Gson().fromJson(jsonMessage,
                    (Type) ConfigMessage.class);
            receive(message);
        } catch (JsonSyntaxException e){
            throw new UnsupportedOperationException("Json Syntax incorrect");
        } catch (Exception e){

            //TODO: It would be interesting to send the error message to the client
        }
    }
}
