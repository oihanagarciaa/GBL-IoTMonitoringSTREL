import com.google.gson.Gson;
import dataStorages.Buffer;
import dataStorages.ConstantSizeBuffer;
import messages.CommonSensorsMessage;
import messages.Message;
import messages.OfficeSensorMessage;
import net.bytebuddy.agent.builder.AgentBuilder;
import org.junit.jupiter.api.Test;
import services.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

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

    @Test
    void savingValues(){
        int size = 3;
        Buffer<Integer> buffer = new ConstantSizeBuffer<>(size, 6);
        buffer.add(new MyNewMessage(1, 156, 0));
        buffer.add(new MyNewMessage(0, 1264, 1));
        buffer.add(new MyNewMessage(1, 947, 2));
        buffer.add(new MyNewMessage(2, 3465, 3));
        buffer.add(new MyNewMessage(0, 2003, 4));


        List<Integer> expectedListInt = new ArrayList<>();
        expectedListInt.add(1);
        expectedListInt.add(2);
        expectedListInt.add(3);
        List<Integer> actualListInt = buffer.get().get(0).getValue();
        assertEquals(expectedListInt, actualListInt);
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

    public class MyNewMessage implements CommonSensorsMessage<Integer> {
        int id;
        double time;
        Integer value;

        public MyNewMessage(int id, double time, int value){
            this.id = id;
            this.time = time;
            this.value = value;
        }

        @Override
        public int getId() {
            return id;
        }

        @Override
        public double getTime() {
            return time;
        }

        @Override
        public Integer getValue() {
            return value;
        }
    }
}
