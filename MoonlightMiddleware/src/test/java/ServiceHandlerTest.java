import eu.quanticol.moonlight.signal.online.TimeChain;
import eu.quanticol.moonlight.signal.online.Update;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import services.Service;
import services.DELETEServiceHandler;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServiceHandlerTest {

    @Disabled("The service handler is not used anymore")
    @Test
    void ableToStartAService() {
        Service service = new FakeWorkingService();
        DELETEServiceHandler handler = new DELETEServiceHandler(service);

        handler.startService();

        assertTrue(service.isRunning());
    }

    @Test
    void ableToDealWithFailingServices() {
        Service service = new FakeNotWorkingService();

        DELETEServiceHandler handler = new DELETEServiceHandler(service);

        handler.startService();
        assertFalse(service.isRunning());
    }


    static class FakeWorkingService implements Service<Double, Double> {
        private boolean running = false;
        @Override
        public boolean isRunning() {
            return running;
        }

        @Override
        public void run(Update<Double, List<Double>> update) {
            running = true;
        }

        @Override
        public void run(TimeChain<Double, List<Double>> timeChain) {
            running = true;
        }

        @Override
        public void init() {
            // do nothing
        }

        @Override
        public void stop() {
            running = false;
        }

        @Override
        public Double getResponseFromService() {
            return null;
        }
    }

    static class FakeNotWorkingService implements Service<Double, Double> {

        @Override
        public boolean isRunning() {
            return false;
        }

        @Override
        public void run(Update<Double, List<Double>> update) {

        }

        @Override
        public void run(TimeChain<Double, List<Double>> timeChain) {

        }

        @Override
        public void init() {

        }

        @Override
        public void stop() {

        }

        @Override
        public Double getResponseFromService() {
            return null;
        }
    }

}