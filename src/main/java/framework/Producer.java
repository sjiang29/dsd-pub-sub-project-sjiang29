package framework;

import network.Connection;
import network.FaultInjector;
import network.LossyInjector;
import utils.Config;

import java.io.IOException;
import java.net.Socket;

public class Producer {
    private String brokerName;
    private Connection connection;

    public Producer(String brokerName) {
        this.brokerName = brokerName;
        String brokerAddress = Config.hostList.get(brokerName).getHostAddress();
        int brokerPort = Config.hostList.get(brokerName).getPort();
        try {
            Socket socket = new Socket(brokerAddress, brokerPort);
            FaultInjector fi = new LossyInjector(Config.lossRate);
            this.connection = new Connection(socket, fi);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(String topic, byte[] data){

    }
}
