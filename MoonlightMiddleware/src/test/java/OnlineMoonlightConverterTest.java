import dataconverter.OnlineMoonlightBuffer;
import eu.quanticol.moonlight.signal.online.Update;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OnlineMoonlightConverterTest {

    @Test
    void convertWithoutInit(){
        OnlineMoonlightBuffer converter = new OnlineMoonlightBuffer();

        assertThrows(NullPointerException.class, () -> converter.fromMessageToMonitorData(new String("")));
    }

    @Test
    void convertTest(){
        OnlineMoonlightBuffer converter = new OnlineMoonlightBuffer();
        OnlineMoonlightBuffer spyConverter = spy(converter);

        spyConverter.initDataConverter(Integer.MIN_VALUE);
        Update uExpected = mock(Update.class);
        String s = "";
        doReturn(uExpected).when(spyConverter).fromMessageToMonitorData(s);

        Update uReturned = spyConverter.fromMessageToMonitorData(s);

        assertEquals(uExpected, uReturned);
    }
}
