package services;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import connection.ConnType;
import connection.Subscriber;
import eu.quanticol.moonlight.core.base.Box;
import eu.quanticol.moonlight.core.base.Tuple;
import eu.quanticol.moonlight.core.formula.Formula;
import eu.quanticol.moonlight.core.space.DistanceStructure;
import eu.quanticol.moonlight.core.space.SpatialModel;
import main.DataBus;
import main.Settings;
import messages.Message;
import messages.ConfigMessage;
import serviceBuilders.MoonlightServiceBuilder;
import serviceBuilders.ResultsThingsboardServiceBuilder;
import serviceBuilders.SensorsServiceBuilder;
import serviceBuilders.ServiceBuilder;
import services.serviceInfo.ConnectionInfo;
import services.serviceInfo.ConnectionSettings;
import services.serviceInfo.ServiceInfo;
import connection.MessageListener;

import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.function.Function;

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
        Map<String ,String> deviceAccessTokens = info.getDevices();
        thingsboardServiceBuilder = new ResultsThingsboardServiceBuilder(deviceAccessTokens);
        return thingsboardServiceBuilder;
    }

    private MoonlightServiceBuilder generateMoonlightService(ServiceInfo info) {
        MoonlightServiceBuilder moonlightServiceBuilder;
        //TODO: add Spatial Model, atoms and distance
        SpatialModel<Double> spatialModel = null;
        Map<String, Function<Tuple, Box<Boolean>>> atoms = null;
        Map<String, Function<SpatialModel<Double>, DistanceStructure<Double, ?>>>
                distFunctions = null;
        Formula formula = evaluateFormula(info.getFormula());
        moonlightServiceBuilder = new MoonlightServiceBuilder(spatialModel, formula,
                atoms, distFunctions, Settings.getBufferSize());
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
    
    private Formula evaluateFormula(String formulaToEvaluate) {
        var engine = new ScriptEngineManager().getEngineByExtension("kts");
        try {
            Formula result = (Formula) engine.eval(formulaToEvaluate);
            System.out.println("Parsed formula: " + result);
            return result;
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
    }

    @Override
    public void messageArrived(String topic, String jsonMessage) {
        System.out.println("MESSAGE: "+jsonMessage);
        try{
            Message message = new Gson().fromJson(jsonMessage,
                    (Type) ConfigMessage.class);
            receive(message);
        } catch (JsonSyntaxException e){
            throw new UnsupportedOperationException("Unknown message type");
        }
    }
}
