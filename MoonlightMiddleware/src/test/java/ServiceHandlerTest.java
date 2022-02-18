

import org.junit.jupiter.api.Test;
import services.Service;
import services.ServiceHandler;
import services.ServiceType;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServiceHandlerTest {

    @Test
    void ableToStartAService() {
        Service service = new FakeWorkingService();
        ServiceHandler handler = new ServiceHandler(ServiceType.MOONLIGHT, service);

        handler.startService();

        assertTrue(service.isRunning());
    }

    @Test
    void ableToDealWithFailingServices() {
        Service service = new FakeNotWorkingService();

        //assertFalse(service.run());
    }


    static class FakeWorkingService implements Service {
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
    }

    static class FakeNotWorkingService implements Service {
        /**
         * @return true if it is running
         */
        @Override
        public boolean isRunning() {
            return false;
        }

        @Override
        public void run() {
            return;
        }

        /**
         * Setup the basics for the service
         */
        @Override
        public void init() {

        }

        /**
         * The service will stop functioning afterwords.
         */
        @Override
        public void stop() {

        }
    }

}