import eu.quanticol.moonlight.core.base.Tuple;
import messages.Message;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OfficeMessageTest {

    @Test
    void transformData(){
//        Message message = new OfficeMessage();
//        message.transformReceivedData("asdfv/2", basicValidJSON());
//        assertEquals(2, message.getId());
//        Tuple tuple = (Tuple) message.getValueElement();
//        assertEquals(40, (Integer) tuple.getIthValue(1));
//        assertEquals(30, (Integer) tuple.getIthValue(2));
        //TODO: test time
    }

    @Test
    void transformWrongData(){
//        Message message = new OfficeMessage();
//        assertThrows(UnsupportedOperationException.class,
//                ()->message.transformReceivedData(
//                        "asdfv/2", "{ \"asda\":4}"));
    }

    @Test
    void transformWrongTopic(){
//        Message message = new OfficeMessage();
//        assertThrows(UnsupportedOperationException.class,
//                ()->message.transformReceivedData(
//                        "asdfv", basicValidJSON()));
    }

    @Test
    void getDefaultValueTest(){
//        Message message = new OfficeMessage();
//        Tuple tuple = (Tuple) message.getDefaultValue();
//        assertEquals(0, (Integer) tuple.getIthValue(2));
    }

    private static String basicValidJSON() {
        return "{\n" +
                "            \"place\": 3\n" +
                "            \"noise\": 40\n" +
                "            \"people\": 30\n" +
                "            \"time\": 1.0\n" +
                "}";
    }
}
