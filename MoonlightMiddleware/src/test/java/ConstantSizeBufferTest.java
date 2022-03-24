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
        Service service = mock(Service.class);
        Buffer<Integer> buffer = new ConstantSizeBuffer<>(size, size);
        for (int i = 0; i < size; i ++){
//            Message<Integer> message = getMessage();
//            if(i < size-1){
//                assertEquals(false, buffer.add(message));
//            }else {
//                assertEquals(true, buffer.add(message));
//            }
        }
    }

//    @Test
//    void constantSizeBufferTest2(){
//        int size = 3;
//        Service<Integer, ?> service = mock(Service.class);
//        Buffer<Integer> buffer = new ConstantSizeBuffer<>(size+1, size, service);
//        for (int i = 0; i < size+1; i ++){
//            Message<Integer> message = getMessage();
//            message.transformReceivedData(Integer.toString(i), "1.0/1");
//            if(i < size){
//                assertEquals(false, buffer.add(message));
//            }else {
//                assertEquals(true, buffer.add(message));
//            }
//        }
//    }

//    private Message<Integer> getMessage() {
//        Message<Integer> message = () -> 4;
//        return message;
//    }
}
