import com.google.protobuf.ByteString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import proto.MsgInfo;

import java.nio.charset.StandardCharsets;

public class AutoTests {

    /**
     * Test protobuff handles msg correctly
     *
     */
    @Test
    public void testProtoBuff1(){
        String msg = "hello";
        byte[] msgBytes = msg.getBytes(StandardCharsets.UTF_8);
        MsgInfo.Msg sentMsg = MsgInfo.Msg.newBuilder().setContent(ByteString.copyFrom(msgBytes)).build();
        byte[] receivedBytes = sentMsg.getContent().toByteArray();
        Assertions.assertEquals(new String(msgBytes), new String(receivedBytes));
    }

    /**
     * Test protobuff handles msg correctly
     *
     */
    @Test
    public void testProtoBuff2(){
        String msg = "hello";
        byte[] msgBytes = msg.getBytes(StandardCharsets.UTF_8);
        MsgInfo.Msg sentMsg = MsgInfo.Msg.newBuilder().setContent(ByteString.copyFrom(msgBytes)).build();
        // String.valueOf doesn't work properly here
        String sentContent = String.valueOf(sentMsg.getContent());
        Assertions.assertNotEquals(msg, sentContent);
    }
}
