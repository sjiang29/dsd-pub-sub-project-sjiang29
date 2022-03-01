package framework;

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
    private ConcurrentHashMap<String, ArrayList<Consumer>> subscriberList;

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

        }
    }
}
