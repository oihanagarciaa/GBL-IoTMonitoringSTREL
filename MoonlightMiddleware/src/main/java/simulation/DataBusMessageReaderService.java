package simulation;

import messages.CommonSensorsMessage;
import messages.Message;
import messages.ResultsMessage;
import services.Service;
import simulation.ConnectionSimulations.DataBusPublisherSimulator;

public class DataBusMessageReaderService implements Service {
    @Override
    public boolean isRunning() {
        return true;
    }

    @Override
    public void receive(Message message) {
        if(message instanceof ResultsMessage m1) System.out.println(
                "Moonlight results: "+m1.toString());
        else if (message instanceof CommonSensorsMessage<?> m2) System.out.println(
                "Sensor messages: ID: "+m2.getId()+ "  t:"+m2.getTime()+ "  V:"+m2.getValue());
        else if (message instanceof DataBusPublisherSimulator.MyMessage m3) System.out.println(
                "Thingboard: "+m3.getMessage());
    }

    @Override
    public void init() {

    }

    @Override
    public void stop() {

    }
}
