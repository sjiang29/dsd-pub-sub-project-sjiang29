package framework;

import com.google.protobuf.InvalidProtocolBufferException;
import network.Connection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import proto.MsgInfo;
import utils.Config;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;


public class Broker {
    public static  Logger logger = LogManager.getLogger();
    private String brokerName;
    private ServerSocket server;
    private int brokerPort;
    private boolean isRunning;
    // key is topic, value is msg list of corresponding topic
    private ConcurrentHashMap<String, ArrayList<MsgInfo.Msg>> msgLists;
    // key is topic, value is list of consumers who subscribe this topic
    // private ConcurrentHashMap<String, ArrayList<String>> subscriberList;
    // key is consumer's name, value is its corresponding connection
    //private ConcurrentHashMap<String, Connection> connections;

    public Broker(String brokerName) {
        this.brokerName = brokerName;
        this.msgLists = new ConcurrentHashMap<>();
        this.isRunning = true;
        this.brokerPort = Config.hostList.get(brokerName).getPort();
        try {
            //starting broker server
            this.server = new ServerSocket(this.brokerPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getBrokerPort() {
        return brokerPort;
    }

    public ConcurrentHashMap<String, ArrayList<MsgInfo.Msg>> getMsgLists() {
        return msgLists;
    }

    public void startBroker(){
        this.isRunning = true;
        while(this.isRunning){
            Connection connection = this.buildNewConnection();
            Thread connectionHandler = new Thread(new ConnectionHandler(connection));
            connectionHandler.start();
        }
    }

    public void shutDownBroker(){
        this.isRunning = false;
    }

    /**
     * Listens to new socket connection, return corresponding connection according to value of delay and lossRate

     * @return see method description
     */
    public Connection buildNewConnection() {
        Socket socket = null;
        try {
            socket = this.server.accept();
        } catch (IOException e) {
            e.printStackTrace();
        }

        logger.info("broker's line 76: someone is calling");

        Connection connection = new Connection(socket);
        return connection;
    }

    class ConnectionHandler implements Runnable{
        private Connection connection;

        public ConnectionHandler(Connection connection) {
            this.connection = connection;
        }

        @Override
        public void run() {
            //boolean isRunning = true;
            while(isRunning){
                byte[] receivedBytes = this.connection.receive();
                try {
                    MsgInfo.Msg receivedMsg = MsgInfo.Msg.parseFrom(receivedBytes);
                    String senderName = receivedMsg.getSenderName();
                    logger.info("broker line 84: senderName + " + senderName);
                    String type = receivedMsg.getType();

                    if(type.equals("subscribe") && senderName.contains("consumer")){
                        dealConsumerReq(receivedMsg);
                    } else if(type.equals("publish") && senderName.contains("producer")) {
                        dealProducerReq(receivedMsg);
                    }

                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        }

        private void dealConsumerReq(MsgInfo.Msg receivedMsg) {
            String subscribedTopic = receivedMsg.getTopic();
            int startingPosition = receivedMsg.getStartingPosition();
            int requiredMsgCount = receivedMsg.getRequiredMsgCount();
            logger.info("broker line 103: subscribedTopic + " + subscribedTopic);

            ArrayList<MsgInfo.Msg> requiredMsgList = msgLists.get(subscribedTopic);
            if(requiredMsgList == null){
                MsgInfo.Msg responseMsg = MsgInfo.Msg.newBuilder().setType("unavailable").setSenderName(brokerName).build();
                this.connection.send(responseMsg.toByteArray());
            } else {
                // send Msg one by one
                MsgInfo.Msg requiredMsg;
                for(int i = startingPosition; i < startingPosition + requiredMsgCount; i++){
                    requiredMsg = MsgInfo.Msg.newBuilder().setType("result").setContent(requiredMsgList.get(i).getContent()).build();
                    logger.info("broker 115, response msg : " + requiredMsg.getContent());
                    this.connection.send(requiredMsg.toByteArray());
                }
                MsgInfo.Msg stopMsg = MsgInfo.Msg.newBuilder().setType("stop").build();
                this.connection.send(stopMsg.toByteArray());
            }
        }

        private void dealProducerReq(MsgInfo.Msg receivedMsg){
            String publishedTopic = receivedMsg.getTopic();
            logger.info("broker line 124: publishedTopic + " + publishedTopic);
            ArrayList<MsgInfo.Msg> messages = msgLists.get(publishedTopic);
            if(messages == null){
                messages = new ArrayList<>();
            }
            messages.add(receivedMsg);
            msgLists.put(publishedTopic, messages);
        }
    }





}
