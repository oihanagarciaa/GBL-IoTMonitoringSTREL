package services;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import connection.Subscriber;
import eu.quanticol.moonlight.core.formula.Formula;
import messages.Message;
import messages.ConfigMessage;
import serviceBuilders.ServiceBuilder;
import services.serviceInfo.ServiceInfo;
import connection.MessageListener;

import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.lang.reflect.Type;
import java.util.List;

public class RunnerService implements Service, MessageListener {
    private final Subscriber subscriber;
    private final List<ServiceBuilder> serviceBuilders;

    public RunnerService(Subscriber subscriber, List<ServiceBuilder> serviceBuilders){
        this.subscriber = subscriber;
        this.serviceBuilders = serviceBuilders;
    }

    @Override
    public boolean isRunning() {
        return subscriber!=null;
    }

    @Override
    public void receive(Message message) {
        //TODO: Should I put this code in another method??
        // here I receive from the DataBus
        if(message instanceof ConfigMessage configMessage){
            for(ServiceInfo serviceInfo: configMessage.getServiceInfo()) {
                switch (serviceInfo.getServiceType()){
                    case "sensors":

                        break;
                    case "moonlight":
                        Formula formula = evaluateFormula(serviceInfo.getFormula());
                        break;
                    case "thingsboard":
                        break;
                    default:
                        throw new UnsupportedOperationException("Service not recognized");
                }
            }
        }
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
        } catch (JsonSyntaxException e){
            throw new UnsupportedOperationException("Unknown message type");
        }
        //TODO: should I send this to the DataBus??
        // or is it better to create another method
    }

}
