import eu.quanticol.moonlight.online.signal.TimeChain;
import eu.quanticol.moonlight.online.signal.Update;
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
        Message<String> message = mock(Message.class);
        when(message.getValueElement()).thenReturn("test");

        bus.notify(s1);
        bus.offer(message);

        assertEquals(1, s1.messages.size());
        assertEquals("test", s1.messages.get(0).getValueElement());
    }

    private static class StubService implements Service<Object, Object> {
        private final List<Message<?>> messages = new ArrayList<>();

        public List<Message<?>> getMessages() {
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

        @Override
        public Object getResponseFromService() {
            return null;
        }

        @Override
        public void run(TimeChain<Double, List<Object>> timeChain) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void run(Update<Double, List<Object>> update) {
            throw new UnsupportedOperationException();
        }
    }

}