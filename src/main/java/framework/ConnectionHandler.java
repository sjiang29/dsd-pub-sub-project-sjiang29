package framework;

import network.Connection;

public class ConnectionHandler implements Runnable{

    private Connection connection;

    public ConnectionHandler(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void run() {

    }

    private void receive(){

    }

    private void send(){

    }
}
