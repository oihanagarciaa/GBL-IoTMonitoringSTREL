package services;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import connection.ConnType;
import connection.Subscriber;
import eu.quanticol.moonlight.core.formula.Formula;
import messages.Message;
import messages.ConfigMessage;
import messages.OfficeSensorMessage;
import serviceBuilders.SensorsServiceBuilder;
import serviceBuilders.ServiceBuilder;
import services.serviceInfo.ServiceInfo;
import connection.MessageListener;

import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.lang.reflect.Type;
import java.util.List;

public class RunnerService implements Service, MessageListener {
    private final Subscriber<?> subscriber;
    private final List<ServiceBuilder> serviceBuilders;

    public RunnerService(Subscriber<?> subscriber,
                         List<ServiceBuilder> serviceBuilders){
        this.subscriber = subscriber;
        this.serviceBuilders = serviceBuilders;
    }

    @Override
    public boolean isRunning() {
        return subscriber!=null;
    }
    
    private void processMessage(Message message) {
        switch(message) {
            case ConfigMessage config -> setupNewCommand(config);
            default -> throw new UnsupportedOperationException("Unsupported " +
                    "message");
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
    }

    private void generateService(ServiceInfo serviceInfo) {
        switch (serviceInfo.getServiceType()){
            case "sensors":
                generateSensorsService(serviceInfo);
                break;
            case "moonlight":
                generateMoonlightService();
                Formula formula = evaluateFormula(serviceInfo.getFormula());
                break;
            case "thingsboard":
                break;
            default:
                throw new UnsupportedOperationException("Service not recognized");
        }
    }

    private void generateMoonlightService() {
        
    }

    @Override
    public void receive(Message message) {
        processMessage(message);
    }
    
    private SensorsServiceBuilder generateSensorsService(ServiceInfo info) {
        String broker = "tcp://stefanschupp.de:1883";
        String topic = "institute/thingy/#";
        String username = "oihana";
        String password = "22oihana22";
        Class receivingMessage = OfficeSensorMessage.class;
//        sensorsServiceBuilder = new SensorsServiceBuilder
//                (ConnType.MQTT, info.broker, topic, username, password,
//                        receivingMessage);
//        services.add(sensorsServiceBuilder);
//        sensorsServiceBuilder.run();
        return null;
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
        //TODO: should I send this to the DataBus??
        // or is it better to create another method
    }

}
