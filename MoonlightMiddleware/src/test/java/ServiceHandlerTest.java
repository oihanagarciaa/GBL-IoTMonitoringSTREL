

import org.junit.jupiter.api.Test;
import services.Service;
import services.ServiceHandler;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServiceHandlerTest {

    @Test
    void ableToStartAService() {
        Service service = new FakeWorkingService();
        ServiceHandler handler = new ServiceHandler(service);

        handler.startService();

        assertTrue(service.isRunning());
    }

    @Test
    void ableToDealWithFailingServices() {
        Service service = new FakeNotWorkingService();

        ServiceHandler handler = new ServiceHandler(service);

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
        public void run() {
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
        public void updateService(Double aDouble) {

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
        public void run() {
            return;
        }

        @Override
        public void init() {

        }

        @Override
        public void stop() {

        }

        @Override
        public void updateService(Double aDouble) {

        }

        @Override
        public Double getResponseFromService() {
            return null;
        }
    }

}