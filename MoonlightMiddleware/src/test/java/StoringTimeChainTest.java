import controller.Controller;
import dataStorages.DataStoringTimeChain;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class StoringTimeChainTest {
    int size = 6;

    @Test
    void basicControllerInit() {

        DataStoringTimeChain<Integer> dataStoringTimeChain = new DataStoringTimeChain<>(size);

        dataStoringTimeChain.saveNewValue(2, 1.0, 6);
        dataStoringTimeChain.saveNewValue(1, 1.0, 7);
        dataStoringTimeChain.saveNewValue(1, 2.0, 9);
        dataStoringTimeChain.getDataToMonitor();
    }
}
