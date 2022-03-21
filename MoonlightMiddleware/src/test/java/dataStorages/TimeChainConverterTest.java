package dataStorages;

import dataConverters.TimeChainConverter;
import eu.quanticol.moonlight.online.signal.TimeChain;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TimeChainConverterTest {
    @Test
    void timeChainConverter() {
        int size = 3;
        DataStoringTimeChain<Integer> dataStoringTimeChain = new DataStoringTimeChain<>(size);

        dataStoringTimeChain.saveNewValue(1, 1.0, 6);
        dataStoringTimeChain.saveNewValue(2, 0.0, 7);
        dataStoringTimeChain.saveNewValue(1, 2.0, 9);
        dataStoringTimeChain.saveNewValue(0, 4.0, 15);

        List<TimeChain<Double, Integer>> timeChainList = dataStoringTimeChain.timeChainList;

        TimeChainConverter<Integer> timeChainConverter = new TimeChainConverter<>(size);
        TimeChain<Double, List<Integer>> result = timeChainConverter.fromListToTimeChain(timeChainList, 5.0, Integer.MAX_VALUE);
        assertEquals(4.0,
                result.getLast().getStart());
        List<Integer> finalList = new ArrayList<>();
        finalList.add(15);
        finalList.add(9);
        finalList.add(7);
        assertEquals(finalList,
                result.getLast().getValue());
    }
}
