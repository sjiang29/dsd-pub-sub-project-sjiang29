package framework;

import com.google.protobuf.InvalidProtocolBufferException;
import network.Connection;
import network.FaultInjector;
import network.LossyInjector;
import proto.MsgInfo;
import utils.Config;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class Broker {
    private String brokerName;
    private ServerSocket server;
    // key is topic, value is msg list of corresponding topic
    private ConcurrentHashMap<String, ArrayList<MsgInfo.Msg>> msgLists;
    // key is topic, value is list of consumers who subscribe this topic
    private ConcurrentHashMap<String, ArrayList<String>> subscriberList;
    // key is consumer's name, value is its corresponding connection
    private ConcurrentHashMap<String, Connection> connections;

    public Broker(String brokerName) {
        this.brokerName = brokerName;
        this.msgLists = new ConcurrentHashMap<>();
        this.subscriberList = new ConcurrentHashMap<>();
        this.connections = new ConcurrentHashMap<>();
        int brokerPort = Config.hostList.get(brokerName).getPort();
        try {
            //starting broker server
            this.server = new ServerSocket(brokerPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startReceiver(){
        boolean isListening = true;
        while(isListening){
            Connection connection = this.buildNewConnection();
            this.updateConnections(connection);

            Thread receiver = new Thread(new Receiver(connection));
            receiver.start();
        }

    }

    public void updateConnections(Connection connection){
        byte[] receivedBytes = connection.receive();
        MsgInfo.Msg receivedMsg = null;
        try {
            receivedMsg = MsgInfo.Msg.parseFrom(receivedBytes);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        String sender = receivedMsg.getSenderName();
        if(!this.connections.contains(sender)){
            connections.put(sender, connection);
        }
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
        System.out.println("someone is calling");
        FaultInjector fi = new LossyInjector(Config.lossRate);
        Connection connection = new Connection(socket, fi);
        return connection;
    }

    class Receiver implements Runnable{
        private Connection connection;

        public Receiver(Connection connection) {
            this.connection = connection;
        }
        @Override
        public void run() {
            boolean isReceiving = true;
            while(isReceiving){
                byte[] receivedBytes = this.connection.receive();
                try {
                    MsgInfo.Msg receivedMsg = MsgInfo.Msg.parseFrom(receivedBytes);
                    String sender = receivedMsg.getSenderName();
                    String type = receivedMsg.getType();
                    if(type.equals("subscribe") && sender.contains("consumer")){
                        String subscribedTopic = receivedMsg.getTopic();
                        ArrayList<String> subscribers = subscriberList.get(subscribedTopic);
                        if(subscribers == null){
                            subscribers = new ArrayList<>();
                        }
                        subscribers.add(sender);
                        subscriberList.put(subscribedTopic, subscribers);
                    } else if (sender.contains("producer")) {
                        String publishedTopic = receivedMsg.getTopic();
                        ArrayList<MsgInfo.Msg> messages = msgLists.get(publishedTopic);
                        if(messages == null){
                            messages = new ArrayList<>();
                        }
                        messages.add(receivedMsg);
                        msgLists.put(publishedTopic, messages);
                    }

                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
