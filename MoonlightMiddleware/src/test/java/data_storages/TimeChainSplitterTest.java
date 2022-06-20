package data_storages;

import data_converters.TimeChainSplitter;
import eu.quanticol.moonlight.online.signal.TimeChain;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TimeChainSplitterTest {

    @Test
    void firstSplitTest(){
        TimeChainSplitter<Integer> timeChainSplitter = new TimeChainSplitter<>();
        List<TimeChain<Double, Integer>> timeChainList = getATimeChainList();

        List<TimeChain<Double, Integer>>[] splitedTimeChains =
                timeChainSplitter.splitTimeChain(timeChainList, 5.0);

        List<TimeChain<Double, Integer>> firstTimeChain = splitedTimeChains[0];
        assertEquals(7, firstTimeChain.get(2).getLast().getValue());
        assertEquals(0.0, firstTimeChain.get(2).getLast().getStart());
        //assertEquals(5.0, firstTimeChain.get(0).getEnd());
    }

    @Test
    void secondSplitTest(){
        TimeChainSplitter<Integer> timeChainSplitter = new TimeChainSplitter<>();
        List<TimeChain<Double, Integer>> timeChainList = getATimeChainList();

        List<TimeChain<Double, Integer>>[] splitedTimeChains =
                timeChainSplitter.splitTimeChain(timeChainList, 5.0);

        List<TimeChain<Double, Integer>> secondTimeChain = splitedTimeChains[1];
        assertEquals(10, secondTimeChain.get(2).getLast().getValue());
        assertEquals(7, secondTimeChain.get(2).getFirst().getValue());
        assertEquals(Double.MAX_VALUE, secondTimeChain.get(0).getEnd());
    }

    private List<TimeChain<Double, Integer>> getATimeChainList(){
        int size = 3;
        DataStoringTimeChain<Integer> dataStoringTimeChain = new DataStoringTimeChain<>(size);

        dataStoringTimeChain.saveNewValue(1, 1.0, 6);
        dataStoringTimeChain.saveNewValue(2, 0.0, 7);
        dataStoringTimeChain.saveNewValue(1, 2.0, 9);
        dataStoringTimeChain.saveNewValue(0, 4.0, 15);
        dataStoringTimeChain.saveNewValue(2, 5.0, 3);
        dataStoringTimeChain.saveNewValue(2, 6.0, 10);

        return dataStoringTimeChain.timeChainList;
    }
}
