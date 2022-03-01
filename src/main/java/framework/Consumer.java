package framework;

import network.Connection;

public class Consumer {
    private String brokerName;
    private String consumerName;
    private Connection connection;
    private int startingPosition;

    public Consumer(String brokerName, String consumerName, Connection connection, int startingPosition) {
        this.brokerName = brokerName;
        this.consumerName = consumerName;
        this.connection = connection;
        this.startingPosition = startingPosition;
    }


}
