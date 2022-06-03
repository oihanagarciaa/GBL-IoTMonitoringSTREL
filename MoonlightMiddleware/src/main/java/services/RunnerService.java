package services;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import eu.quanticol.moonlight.core.formula.Formula;
import main.DataBus;
import messages.Message;
import subscriber.MessageListener;

import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.lang.reflect.Type;

public class RunnerService implements Service, MessageListener {
    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public void receive(Message message) {
        for(ServiceInfo serviceInfo: message.getServiceInfo()) {
            if(serviceInfo.getServiceType() == "moonlight") {
                Formula formula = evaluateFormula(serviceInfo.getFormula());
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

    }

    @Override
    public void stop() {

    }

    @Override
    public void messageArrived(String topic, String jsonMessage) {
        System.out.println("MESSAGE: "+jsonMessage);
        try{
            Message message = new Gson().fromJson(jsonMessage, (Type) messageClass);
            
            
        } catch (JsonSyntaxException e){
            throw new UnsupportedOperationException("Unknown message type");
        }
    }
}
