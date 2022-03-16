package drivers;

import framework.Broker;

public class BrokerHost {
    private static String brokerName = "broker";

    public static void main(String[] args){
        Broker broker = new Broker(brokerName);
    }



}
