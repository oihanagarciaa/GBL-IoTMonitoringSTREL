import com.google.gson.Gson;
import dataStorages.Buffer;
import dataStorages.ConstantSizeBuffer;
import messages.CommonSensorsMessage;
import messages.Message;
import messages.OfficeSensorMessage;
import org.junit.jupiter.api.Test;
import services.Service;

import java.lang.reflect.Type;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class ConstantSizeBufferTest {

    @Test
    void constantSizeBufferTest(){
        int size = 3;
        Service service = mock(Service.class);
        Buffer<Integer> buffer = new ConstantSizeBuffer<>(size, size);
        for (int i = 0; i < size; i ++){
            Message message = getMessage(i, 0, 7);
            if(i == size-1){
                assertEquals(false, buffer.add((CommonSensorsMessage) message));
            }
        }
    }

    @Test
    void constantSizeBufferTest2(){
        int size = 3;
        Service service = mock(Service.class);
        Buffer<Integer> buffer = new ConstantSizeBuffer<>(size, size);
        for (int i = 0; i < 2; i ++){
            for (int j = 0; j < 3; j++){
                Message message = getMessage(j, i, 7);
                if(i == 1 && j == size-1){
                    assertEquals(true, buffer.add((CommonSensorsMessage) message));
                }else {
                    assertEquals(false, buffer.add((CommonSensorsMessage) message));
                }
            }
        }
    }

    private Message getMessage(int id, double time, int people){
        String jsonMessage = "{" +
                "'id': "+id+","+
                "'place': 'School',"+
                "'noise': 30,"+
                "'people': "+people+","+
                "'time': "+time+
                "}";
        Message message = new Gson().fromJson(jsonMessage, (Type) OfficeSensorMessage.class);
        return message;
    }
}
