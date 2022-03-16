import eu.quanticol.moonlight.core.base.MoonLightRecord;
import messages.Message;
import messages.OfficeMessage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OfficeMessageTest {

    @Test
    void transformData(){
        Message message = new OfficeMessage();
        message.transformReceivedData("asdfv/2", basicValidJSON());
        assertEquals(2, message.getId());
        //TODO: test time
        assertEquals(MoonLightRecord.class, message.getValueElement().getClass());
    }

    @Test
    void transformWrongData(){
        Message message = new OfficeMessage();
        assertThrows(UnsupportedOperationException.class,
                ()->message.transformReceivedData(
                        "asdfv/2", "{ \"asda\":4}"));
    }

    @Test
    void transformWrongTopic(){
        Message message = new OfficeMessage();
        assertThrows(UnsupportedOperationException.class,
                ()->message.transformReceivedData(
                        "asdfv", basicValidJSON()));
    }

    @Test
    void getDefaultValueTest(){
        Message message = new OfficeMessage();
        MoonLightRecord moonLightRecord = (MoonLightRecord) message.getDefaulValue();
        assertEquals(MoonLightRecord.class, moonLightRecord.getClass());
    }

    private static String basicValidJSON() {
        return "{\n" +
                "            \"place\": 3\n" +
                "            \"noise\": 40\n" +
                "            \"people\": 30\n" +
                "}";
    }
}
