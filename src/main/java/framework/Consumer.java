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
import static framework.Broker.logger;

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
        int brokerPort = Config.hostList.get(this.brokerName).getPort();
        try {
            Socket socket = new Socket(brokerAddress, brokerPort);
            this.connection = new Connection(socket);
            this.subscribedMsgQ = new LinkedBlockingQueue<>();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendRequest(int startingPoint){
        int requiredMsgCount = 20;
        MsgInfo.Msg requestMsg = MsgInfo.Msg.newBuilder().setType("subscribe").setTopic(this.topic).setSenderName(this.consumerName)
                .setStartingPosition(startingPoint).setRequiredMsgCount(requiredMsgCount).build();
        this.connection.send(requestMsg.toByteArray());
    }

    public int updateBlockingQ(int startingPoint){
        int receivedMsgCount = 0;
        boolean isReceiving = true;
        while(isReceiving){
            byte[] receivedBytes = this.connection.receive();
            try {
                MsgInfo.Msg receivedMsg = MsgInfo.Msg.parseFrom(receivedBytes);
                if(receivedMsg.getType().equals("unavailable")){
                    this.sendRequest(startingPoint);
                }else if(receivedMsg.getType().contains("stop")){
                    isReceiving = false;
                }else if(receivedMsg.getType().equals("result")) {
                    logger.info("consumer line 57: received msg " + receivedMsg.getContent());
                    receivedMsgCount = receivedMsgCount + 1;
                    this.subscribedMsgQ.put(receivedMsg);
                }
            } catch (InvalidProtocolBufferException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        return receivedMsgCount;
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
        int startingPoint = this.startingPosition;
        while(this.connection.isOpen() && startingPoint >= 0){
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.sendRequest(startingPoint);
            int receivedMsgCount = this.updateBlockingQ(startingPoint);
            startingPoint = startingPoint + receivedMsgCount;
        }

    }

    public void close(){
        this.connection.close();
    }
}
