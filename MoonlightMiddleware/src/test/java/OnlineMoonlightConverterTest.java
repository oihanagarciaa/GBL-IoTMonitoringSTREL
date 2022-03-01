import dataconverter.DataConverter;
import dataconverter.OnlineMoonlightBuffer;
import eu.quanticol.moonlight.signal.online.Update;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OnlineMoonlightConverterTest {

    @Test
    void convertWithoutInit(){
        OnlineMoonlightBuffer converter = new OnlineMoonlightBuffer();
        converter.fromMessageToMonitorData(1, "");
    }

    @Test
    void convertTest(){
        OnlineMoonlightBuffer converter = new OnlineMoonlightBuffer();
        OnlineMoonlightBuffer spyConverter = spy(converter);

        spyConverter.initDataConverter(Integer.MIN_VALUE);
        Update uExpected = mock(Update.class);
        String s = "";
        int id = 1;
        doReturn(uExpected).when(spyConverter).fromMessageToMonitorData(id, s);

        Update uReturned = spyConverter.fromMessageToMonitorData(id, s);

        assertEquals(uExpected, uReturned);
    }

    @Test
    void convertJSON(){
        DataConverter dataConverter = new OnlineMoonlightBuffer();
        dataConverter.initDataConverter(5);
        Object u = dataConverter.fromMessageToMonitorData(2, "{ \"place\": 2, \"noise\": 300, \"people\": 10}");
        assertEquals(u.getClass(), Update.class);
    }

    /**
     * If the JSON file is not correct, the program must continue working, treat exceptions
     */
    @Test
    void failingJSON(){
        DataConverter dataConverter = new OnlineMoonlightBuffer();
        dataConverter.initDataConverter(5);
        Object u = dataConverter.fromMessageToMonitorData(2, "asdfghjk");
    }
}
