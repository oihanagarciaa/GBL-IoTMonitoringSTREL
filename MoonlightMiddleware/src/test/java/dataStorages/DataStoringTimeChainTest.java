package dataStorages;

import eu.quanticol.moonlight.online.signal.TimeChain;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DataStoringTimeChainTest {

    @Test
    void getDataToMonitorTest(){
        DataStoringTimeChain<Integer> dataStoringTimeChain = initDataStoringTimeChain();
        TimeChain<Double, List<Integer>> timeChain = dataStoringTimeChain.getDataToMonitor(5.0);
        assertEquals(5.0, timeChain.getEnd());
        assertEquals(ArrayList.class, timeChain.getFirst().getValue().getClass());
    }

    @Test
    void getDataTwice(){
        DataStoringTimeChain<Integer> dataStoringTimeChain = initDataStoringTimeChain();
        dataStoringTimeChain.getDataToMonitor(6.0);
        dataStoringTimeChain.saveNewValue(0, 7.0, 0);
        dataStoringTimeChain.saveNewValue(0, 9.0, 0);
        TimeChain<Double, List<Integer>> timeChain = dataStoringTimeChain.getDataToMonitor(9.0);
        assertEquals(9.0, timeChain.getEnd());
        List<Integer> list = new ArrayList<>();
        list.add(0);
        list.add(9);
        list.add(10);
        assertEquals(list, timeChain.getLast().getValue());
    }

    @Test
    void getDataWithNoValues(){
        int size = 3;
        DataStoringTimeChain<Integer> dataStoringTimeChain = new DataStoringTimeChain<>(size);
        TimeChain<Double, List<Integer>> timeChain = dataStoringTimeChain.getDataToMonitor(0.0);
        dataStoringTimeChain.saveNewValue(1, 1.0, 6);
        dataStoringTimeChain.saveNewValue(1, 4.0, 8);
        TimeChain<Double, List<Integer>> timeChain2 = dataStoringTimeChain.getDataToMonitor(5.0);
        assertEquals(1.0, timeChain2.get(0).getStart());
    }

    private DataStoringTimeChain<Integer> initDataStoringTimeChain(){
        int size = 3;
        DataStoringTimeChain<Integer> dataStoringTimeChain = new DataStoringTimeChain<>(size);

        dataStoringTimeChain.saveNewValue(1, 1.0, 6);
        dataStoringTimeChain.saveNewValue(2, 0.0, 7);
        dataStoringTimeChain.saveNewValue(1, 2.0, 9);
        dataStoringTimeChain.saveNewValue(0, 4.0, 15);
        dataStoringTimeChain.saveNewValue(2, 5.0, 3);
        dataStoringTimeChain.saveNewValue(2, 6.0, 10);

        return dataStoringTimeChain;
    }
}
