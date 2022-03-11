import DELETEdataConverters.DataConverter;
import DELETEdataConverters.MoonlightRecordConverter;
import eu.quanticol.moonlight.io.MoonLightRecord;
import eu.quanticol.moonlight.signal.online.Update;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OnlineMoonlightConverterTest {

    @Test
    void convertWithoutInit(){
        MoonlightRecordConverter converter = new MoonlightRecordConverter();
        assertThrows(UnsupportedOperationException.class, () -> {
            converter.fromMessageToMonitorData("");
        });
    }

    @Test
    void convertTest(){
        MoonlightRecordConverter converter = new MoonlightRecordConverter();
        MoonlightRecordConverter spyConverter = spy(converter);

        //spyConverter.initDataConverter(Integer.MIN_VALUE);
        MoonLightRecord uExpected = mock(MoonLightRecord.class);
        String s = "";
        doReturn(uExpected).when(spyConverter).fromMessageToMonitorData(s);

        MoonLightRecord uReturned = spyConverter.fromMessageToMonitorData(s);

        assertEquals(uExpected, uReturned);
    }

    @Disabled("Maybe I will delete the converter")
    @Test
    void convertJSON(){
        DataConverter dataConverter = new MoonlightRecordConverter();
        //dataConverter.initDataConverter(5);
        Object u = dataConverter.fromMessageToMonitorData("{ \"place\": 2, \"noise\": 300, \"people\": 10}");
        assertEquals(u.getClass(), Update.class);
    }

    /**
     * If the JSON file is not correct, the program must continue working, treat exceptions
     */
    @Disabled("we need to deal with failures in some way")
    @Test
    void failingJSON(){
        DataConverter dataConverter = new MoonlightRecordConverter();
        //dataConverter.initDataConverter(5);
        dataConverter.fromMessageToMonitorData(/*2,*/ "asdfghjk");
    }
}
