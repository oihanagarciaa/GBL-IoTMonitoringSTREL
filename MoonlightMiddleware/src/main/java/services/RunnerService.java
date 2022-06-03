package services;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import eu.quanticol.moonlight.core.formula.Formula;
import messages.Message;
import messages.ConfigMessage;
import services.serviceInfo.ServiceInfo;
import connection.MessageListener;

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
        //TODO: Should I put this code in another method??
        // here I receive from the DataBus
        if(message instanceof ConfigMessage configMessage){
            for(ServiceInfo serviceInfo: configMessage.getServiceInfo()) {
                if(serviceInfo.getServiceType().equals("moonlight")) {
                    Formula formula = evaluateFormula(serviceInfo.getFormula());
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
    }

}
