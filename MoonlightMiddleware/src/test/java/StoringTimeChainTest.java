import dataStorages.DataStoringTimeChain;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class StoringTimeChainTest {
    @Test
    void basicControllerInit() {
        int size = 3;
        DataStoringTimeChain<Integer> dataStoringTimeChain = new DataStoringTimeChain<>(size, Integer.MAX_VALUE);

        dataStoringTimeChain.saveNewValue(1, 1.0, 6);
        dataStoringTimeChain.saveNewValue(2, 0.0, 7);
        dataStoringTimeChain.saveNewValue(1, 2.0, 9);
        dataStoringTimeChain.saveNewValue(0, 4.0, 15);

        assertEquals(4.0,
                dataStoringTimeChain.getDataToMonitor().getLast().getStart());
        List<Integer> finalList = new ArrayList<>();
        finalList.add(15);
        finalList.add(9);
        finalList.add(7);
        assertEquals(finalList,
                dataStoringTimeChain.getDataToMonitor().getLast().getValue());
    }
}
