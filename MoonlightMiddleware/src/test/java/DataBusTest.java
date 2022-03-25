import main.DataBus;
import messages.Message;
import org.junit.jupiter.api.Test;
import services.Service;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DataBusTest {

    @Test
    void testOnlyOneDataBusExists() {
     DataBus instance1 = DataBus.getInstance();
     DataBus instance2 = DataBus.getInstance();
     assertEquals(instance1, instance2);
    }

    @Test
    void testNotifyTwoServices() {
        DataBus bus = DataBus.getInstance();
        StubService s1 = new StubService();
        MessageTest message = mock(MessageTest.class);
        when(message.getValue()).thenReturn("test");

        bus.notify(s1);
        bus.offer((Message) message);

        assertEquals(1, s1.messages.size());
        MessageTest messageResult = (MessageTest) s1.messages.get(0);
        assertEquals("test", messageResult.getValue());
    }

    private static class StubService implements Service {
        private final List<Message> messages = new ArrayList<>();

        public List<Message> getMessages() {
            return messages;
        }

        @Override
        public boolean isRunning() {
            return false;
        }

        @Override
        public void receive(Message message) {
            messages.add(message);
        }

        @Override
        public void init() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void stop() {
            throw new UnsupportedOperationException();
        }
    }

    public class MessageTest implements Message{
        String value;

        public String getValue() {
            return value;
        }
    }

}