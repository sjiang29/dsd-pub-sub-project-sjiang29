package framework;

import com.google.protobuf.ByteString;
import network.Connection;
import proto.MsgInfo;
import utils.Config;

import java.io.IOException;
import java.net.Socket;

public class Producer {
    private String brokerName;
    private String producerName;
    private Connection connection;
    private int msgId;

    public Producer(String brokerName, String producerName) {
        this.msgId = 1;
        this.brokerName = brokerName;
        this.producerName = producerName;
        String brokerAddress = Config.hostList.get(this.brokerName).getHostAddress();
        int brokerPort = Config.hostList.get(brokerName).getPort();
        try {
            Socket socket = new Socket(brokerAddress, brokerPort);
            this.connection = new Connection(socket);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(String topic, byte[] data){
        MsgInfo.Msg sentMsg = MsgInfo.Msg.newBuilder().setTopic(topic).setType("publish").setContent(ByteString.copyFrom(data)).setId(this.msgId++).setSenderName(this.producerName).build();
        this.connection.send(sentMsg.toByteArray());

    }

    public void close(){
        this.connection.close();
    }
}
