import dataStorages.Buffer;
import dataStorages.ConstantSizeBuffer;
import messages.Message;
import org.junit.jupiter.api.Test;
import services.Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class ConstantSizeBufferTest {

    @Test
    void constantSizeBufferTest(){
        int size = 3;
        Service<Integer, ?> service = mock(Service.class);
        Buffer<Integer> buffer = new ConstantSizeBuffer<>(size, size, service);
        for (int i = 0; i < size; i ++){
            Message<Integer> message = getMessage();
            message.transformReceivedData(Integer.toString(i), "1.0/1");
            if(i < size-1){
                assertEquals(false, buffer.add(message));
            }else {
                assertEquals(true, buffer.add(message));
            }
        }
    }

    @Test
    void constantSizeBufferTest2(){
        int size = 3;
        Service<Integer, ?> service = mock(Service.class);
        Buffer<Integer> buffer = new ConstantSizeBuffer<>(size+1, size, service);
        for (int i = 0; i < size+1; i ++){
            Message<Integer> message = getMessage();
            message.transformReceivedData(Integer.toString(i), "1.0/1");
            if(i < size){
                assertEquals(false, buffer.add(message));
            }else {
                assertEquals(true, buffer.add(message));
            }
        }
    }

    private Message<Integer> getMessage() {
        Message<Integer> message = new Message<Integer>() {
            @Override
            public void transformReceivedData(String topic, String message) {
                int id = Integer.parseInt(topic);
                String[] msg = message.split("/");
                double time = Double.parseDouble(msg[0]);
                int value = Integer.parseInt(msg[1]);
                this.setMessageData(id, time, value);
            }

            @Override
            public Integer getDefaultValue() {
                return Integer.MAX_VALUE;
            }
        };
        return message;
    }
}
