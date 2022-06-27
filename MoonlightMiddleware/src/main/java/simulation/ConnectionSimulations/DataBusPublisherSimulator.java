package simulation.ConnectionSimulations;

import connection.Publisher;
import main.DataBus;
import messages.Message;

public class DataBusPublisherSimulator implements Publisher {

    @Override
    public void publish(String username, String password, String message) {
        MyMessage message1 = new MyMessage();
        message1.setMessage(message);
        DataBus.getInstance().offer(message1);
    }

    public class MyMessage implements Message{
        String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
