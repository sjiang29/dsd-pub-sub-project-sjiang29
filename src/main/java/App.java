import framework.Broker;
import framework.Consumer;
import framework.Producer;
import proto.MsgInfo;
import utils.Config;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class App {
    public static void main(String[] args){
        if (args.length != 3){
            System.out.println("Usage of the application is: java App <hostName> ");
            System.exit(1);
        }

        String hostName = args[2];
        run(hostName);

    }

    public static void run(String hostName){
        if(hostName.equals("broker")){
            dealBroker(hostName);
        } else if(hostName.contains("producer")){
            dealProducer(hostName);
        } else if(hostName.contains("consumer")){
            dealConsumer(hostName);
        }
    }

    public static void dealBroker(String brokerName){
        Broker broker = new Broker(brokerName);
        broker.startBroker();
    }

    public static void dealProducer(String producerName){
        String file = Config.producerAndFile.get(producerName);
        Producer producer = new Producer("broker", producerName);
        runProducer(producer, file);
    }

    public static void runProducer(Producer producer, String file){
        try (FileInputStream fileInputStream = new FileInputStream(file);
             BufferedReader br = new BufferedReader(new InputStreamReader(fileInputStream, StandardCharsets.ISO_8859_1))) {
            String line;

            while ((line = br.readLine()) != null) {
                byte[] data = line.getBytes(StandardCharsets.UTF_8);
                String topic = Config.topics.get(file);
                producer.send(topic, data);

            }
            producer.close();

        }catch (FileNotFoundException e) {
            System.out.println("File does not exist!");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void dealConsumer(String consumerName){
        String topic1File = "/Users/sj/Desktop/Distributed Software Dev/Projects/p2/p.log";
        String topic2File = "/Users/sj/Desktop/Distributed Software Dev/Projects/p2/z.log";

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
