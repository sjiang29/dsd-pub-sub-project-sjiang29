package framework;

import com.google.protobuf.InvalidProtocolBufferException;
import network.Connection;
import network.FaultInjector;
import network.LossyInjector;
import proto.MsgInfo;
import utils.Config;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Consumer{
    private String brokerName;
    private String consumerName;
    private Connection connection;
    private int startingPosition;
    private BlockingQueue<MsgInfo.Msg> subscribedMsgQ;

    public Consumer(String brokerName, String consumerName, Connection connection, int startingPosition) {
        this.brokerName = brokerName;
        this.consumerName = consumerName;
        this.startingPosition = startingPosition;

        String brokerAddress = Config.hostList.get(this.brokerName).getHostAddress();
        int brokerPort = Config.hostList.get(brokerName).getPort();
        try {
            Socket socket = new Socket(brokerAddress, brokerPort);
            FaultInjector fi = new LossyInjector(Config.lossRate);
            this.connection = new Connection(socket, fi);
            this.subscribedMsgQ = new LinkedBlockingQueue<>();
            this.updateBlockingQ();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BlockingQueue<MsgInfo.Msg> getSubscribedMsgQ() {
        return subscribedMsgQ;
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



}
