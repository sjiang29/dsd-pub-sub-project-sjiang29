package drivers;

import framework.Consumer;
import framework.Producer;
import proto.MsgInfo;
import utils.Config;

import java.io.IOException;
import java.io.PrintWriter;

public class ConsumerHost {

    public static void main(String args[]){
        if (args.length != 4){
            System.out.println("Usage of the application is: java drivers ConsumerHost <hostName> ");
            System.exit(1);
        }

        String consumerHostName = args[3];
        String topic1File = "/Users/sj/Desktop/Distributed Software Dev/Projects/p2/consumer1.txt";
        String topic2File = "/Users/sj/Desktop/Distributed Software Dev/Projects/p2/consumer3.txt";

        String topic1 = Config.topic1;
        String topic2 = Config.topic2;

        Consumer consumer1 = new Consumer("broker", "consumer1", topic1, 20);
        Thread t1 = new Thread(consumer1);
        t1.start();
        Thread t2 = new Thread(() -> saveToFile(consumer1, topic1File));
        t2.start();

        Consumer consumer2 = new Consumer("broker", "consumer2", topic2, 20);
        Thread t3 = new Thread(consumer2);
        t3.start();
        Thread t4 = new Thread(() -> saveToFile(consumer2, topic2File));
        t4.start();

    }

    public static void saveToFile(Consumer consumer, String file){
        try {
            PrintWriter pw = new PrintWriter(file);
            while(true){
                MsgInfo.Msg msg = consumer.poll(100);
                String line = String.valueOf(msg.getContent());
                pw.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
