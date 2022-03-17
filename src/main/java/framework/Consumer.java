package framework;

import com.google.protobuf.InvalidProtocolBufferException;
import network.Connection;
import proto.MsgInfo;
import utils.Config;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Consumer implements Runnable{
    private String brokerName;
    private String consumerName;
    private Connection connection;
    private String topic;
    private int startingPosition;
    private BlockingQueue<MsgInfo.Msg> subscribedMsgQ;

    public Consumer(String brokerName, String consumerName, String topic, int startingPosition) {
        this.brokerName = brokerName;
        this.consumerName = consumerName;
        this.topic = topic;
        this.startingPosition = startingPosition;

        String brokerAddress = Config.hostList.get(this.brokerName).getHostAddress();
        int brokerPort = Config.hostList.get(brokerName).getPort();
        try {
            Socket socket = new Socket(brokerAddress, brokerPort);
            this.connection = new Connection(socket);
            this.subscribedMsgQ = new LinkedBlockingQueue<>();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendRequest(){
        MsgInfo.Msg requestMsg = MsgInfo.Msg.newBuilder().setType("subscribe").setTopic(this.topic).setSenderName(this.brokerName).setStartingPosition(this.startingPosition).build();
        this.connection.send(requestMsg.toByteArray());
    }

    public void updateBlockingQ(){
        boolean isReceiving = true;
        while(isReceiving){
            byte[] receivedBytes = this.connection.receive();
            try {
                MsgInfo.Msg receivedMsg = MsgInfo.Msg.parseFrom(receivedBytes);
                if(receivedMsg.getType().contains("stop")){
                    isReceiving = false;
                }else {
                    this.subscribedMsgQ.add(receivedMsg);
                }
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
    }

    public MsgInfo.Msg poll(int timeOut){
        MsgInfo.Msg polledMsg = null;
        try {
            polledMsg = this.subscribedMsgQ.poll(timeOut, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return polledMsg;

    }

    @Override
    public void run() {
        this.sendRequest();
        this.updateBlockingQ();
    }

    public void close(){
        this.connection.close();
    }
}
